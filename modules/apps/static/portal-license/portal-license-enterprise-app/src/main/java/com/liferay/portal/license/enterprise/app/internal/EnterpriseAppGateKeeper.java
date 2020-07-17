/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.license.enterprise.app.internal;

import com.liferay.osgi.util.BundleUtil;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.license.enterprise.app.internal.constants.EnterpriseAppDestinationNames;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tina Tian
 */
@Component(immediate = true, service = {})
public class EnterpriseAppGateKeeper {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_enterpriseAppBundleListener = new EnterpriseAppBundleListener();

		bundleContext.addBundleListener(_enterpriseAppBundleListener);

		_scanBundles(bundleContext);

		_enterpriseAppBundleListener.setInitialized(true);

		Dictionary<String, Object> dictionary = new HashMapDictionary<>();

		dictionary.put(
			"destination.name", EnterpriseAppDestinationNames.ENTERPRISE_APP);

		_messageListenerServiceRegistration = bundleContext.registerService(
			MessageListener.class, new EnterpriseAppMessageListener(),
			dictionary);

		_enterpriseAppServletContextHelperServiceTracker =
			ServiceTrackerFactory.open(
				bundleContext,
				StringBundler.concat(
					"(&(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
					"=*)(objectClass=org.osgi.service.http.context.",
					"ServletContextHelper))"),
				new EnterpriseAppWebContextServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_enterpriseAppServletContextHelperServiceTracker.close();

		_messageListenerServiceRegistration.unregister();

		_bundleContext.removeBundleListener(_enterpriseAppBundleListener);
	}

	private String _getProductId(String enterpriseAppHeader) {
		int index = enterpriseAppHeader.indexOf(_PRODUCT_ID);

		if (index == -1) {
			return null;
		}

		int endIndex = enterpriseAppHeader.indexOf(index, CharPool.SEMICOLON);

		if (endIndex == -1) {
			return enterpriseAppHeader.substring(index + _PRODUCT_ID.length());
		}

		return enterpriseAppHeader.substring(
			index + _PRODUCT_ID.length(), endIndex);
	}

	private boolean _processBundleInstalled(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String enterpriseAppHeader = headers.get("Liferay-Enterprise-App");

		if (enterpriseAppHeader == null) {
			return false;
		}

		String productId = _getProductId(enterpriseAppHeader);

		synchronized (this) {
			if (_verifyLicense(productId)) {
				_blockedBundleMap.remove(bundle.getLocation());

				String webContextPath = headers.get("Web-ContextPath");

				if (webContextPath != null) {
					_webContextPaths.put(webContextPath, productId);
				}

				return false;
			}

			BundleStartLevel bundleStartLevel = bundle.adapt(
				BundleStartLevel.class);

			int startLevel = bundleStartLevel.getStartLevel();

			try {
				bundle.uninstall();

				_blockedBundleMap.put(bundle.getLocation(), startLevel);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to uninstall bundle " + bundle.getSymbolicName(),
					exception);
			}
		}

		return true;
	}

	private void _processBundleUninstalled(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String webContextPath = headers.get("Web-ContextPath");

		if (webContextPath != null) {
			_webContextPaths.remove(webContextPath);
		}

		synchronized (this) {
			_blockedBundleMap.remove(bundle.getLocation());
		}
	}

	private void _scanBundles(BundleContext bundleContext) {
		List<Bundle> uninstalledBundles = new ArrayList<>();

		for (Bundle bundle : bundleContext.getBundles()) {
			if ((bundle.getState() != Bundle.UNINSTALLED) &&
				_processBundleInstalled(bundle)) {

				uninstalledBundles.add(bundle);
			}
		}

		if (!uninstalledBundles.isEmpty()) {
			BundleUtil.refreshBundles(bundleContext, uninstalledBundles);
		}
	}

	private boolean _verifyLicense(String productId) {
		String name = ReleaseInfo.getName();

		if (name.contains("Community")) {
			return true;
		}

		if (_licenseManager.getLicenseState(productId) !=
				LicenseManager.STATE_GOOD) {

			_log.error(
				"This product " + productId + " does not have a valid license");

			return false;
		}

		Map<String, String> portalLicenseProperties =
			_licenseManager.getLicenseProperties("Portal");

		String portalLicenseType = portalLicenseProperties.get("type");

		if (portalLicenseType == null) {
			_log.error(
				"This product " + productId +
					" requires a valid Liferay DXP license");

			return false;
		}

		Map<String, String> appLicenseProperties =
			_licenseManager.getLicenseProperties(productId);

		String appLicenseType = portalLicenseProperties.get("type");

		if (appLicenseType.equals(portalLicenseType)) {
			if (appLicenseType.equals("trial") &&
				!Objects.equals(
					appLicenseProperties.get("lifetime"),
					portalLicenseProperties.get("lifetime"))) {

				_log.error(
					StringBundler.concat(
						"This product ", productId,
						" does not have same lifetime with the Liferay DXP ",
						"trial license"));

				return false;
			}
		}
		else {
			if (appLicenseType.startsWith("developer")) {
				_log.error(
					StringBundler.concat(
						"This product ", productId,
						" must not use a developer license when the Liferay ",
						"DXP license is not a developer license"));

				return false;
			}
			else if (portalLicenseType.startsWith("developer")) {
				_log.error(
					StringBundler.concat(
						"This product ", productId,
						" requires a developer license when the Liferay DXP ",
						"license is a developer license"));

				return false;
			}
		}

		return true;
	}

	private static final String _PRODUCT_ID = "product.id=";

	private static final Log _log = LogFactoryUtil.getLog(
		EnterpriseAppGateKeeper.class);

	private final Map<String, Integer> _blockedBundleMap = new HashMap<>();
	private BundleContext _bundleContext;
	private EnterpriseAppBundleListener _enterpriseAppBundleListener;
	private ServiceTracker<Object, ServiceRegistration<Filter>>
		_enterpriseAppServletContextHelperServiceTracker;

	@Reference
	private LicenseManager _licenseManager;

	@Reference
	private LPKGDeployer _lpkgDeployer;

	private ServiceRegistration<MessageListener>
		_messageListenerServiceRegistration;
	private final Map<String, String> _webContextPaths =
		new ConcurrentHashMap<>();

	private class EnterpriseAppBundleListener
		implements SynchronousBundleListener {

		@Override
		public void bundleChanged(BundleEvent bundleEvent) {
			if (bundleEvent.getType() == BundleEvent.INSTALLED) {
				_processBundleInstalled(bundleEvent.getBundle());
			}
			else if (_initialized &&
					 (bundleEvent.getType() == BundleEvent.UNINSTALLED)) {

				_processBundleUninstalled(bundleEvent.getBundle());
			}
		}

		public void setInitialized(boolean initialized) {
			_initialized = initialized;
		}

		private boolean _initialized;

	}

	private class EnterpriseAppMessageListener implements MessageListener {

		@Override
		public void receive(Message message) {
			String productId = (String)message.getPayload();

			if (Validator.isNull(productId) || !_verifyLicense(productId)) {
				return;
			}

			synchronized (EnterpriseAppGateKeeper.this) {
				for (Map.Entry<String, Integer> entry :
						_blockedBundleMap.entrySet()) {

					try {
						BundleUtil.installBundle(
							_bundleContext, _lpkgDeployer, entry.getKey(),
							entry.getValue());
					}
					catch (Exception exception) {
						_log.error(
							"Unable to install bundle " + entry.getKey(),
							exception);
					}
				}
			}
		}

	}

	private class EnterpriseAppWebContextServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<Object, ServiceRegistration<Filter>> {

		@Override
		public ServiceRegistration<Filter> addingService(
			ServiceReference<Object> serviceReference) {

			String webContextPath = GetterUtil.getString(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH));

			String productId = _webContextPaths.get(webContextPath);

			if (productId == null) {
				return null;
			}

			return _bundleContext.registerService(
				Filter.class,
				new EnterpriseAppPortletServletFilter(
					productId, _licenseManager),
				_buildProperties(serviceReference));
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference,
			ServiceRegistration<Filter> filterServiceRegistration) {

			filterServiceRegistration.setProperties(
				_buildProperties(serviceReference));
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference,
			ServiceRegistration<Filter> filterServiceRegistration) {

			filterServiceRegistration.unregister();
		}

		private Dictionary<String, Object> _buildProperties(
			ServiceReference<Object> serviceReference) {

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			for (String key : serviceReference.getPropertyKeys()) {
				if (key.startsWith("osgi.http.whiteboard")) {
					properties.put(key, serviceReference.getProperty(key));
				}
			}

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				StringBundler.concat(
					"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
					"=",
					GetterUtil.getString(
						serviceReference.getProperty(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_CONTEXT_NAME)),
					")"));
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER,
				new String[] {
					HttpWhiteboardConstants.DISPATCHER_INCLUDE,
					HttpWhiteboardConstants.DISPATCHER_FORWARD,
					HttpWhiteboardConstants.DISPATCHER_REQUEST
				});
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET,
				PortletServlet.class.getName());

			return properties;
		}

	}

}