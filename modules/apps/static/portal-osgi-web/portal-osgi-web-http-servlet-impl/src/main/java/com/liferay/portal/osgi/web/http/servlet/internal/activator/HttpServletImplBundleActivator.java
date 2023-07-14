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

package com.liferay.portal.osgi.web.http.servlet.internal.activator;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.servlet.PortletSessionListenerManager;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.osgi.web.http.servlet.ExtendedHttpService;
import com.liferay.portal.osgi.web.http.servlet.HttpServletService;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceBag;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceFactory;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.HttpSessionTracker;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ProxyServlet;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Dante Wang
 */
public class HttpServletImplBundleActivator implements BundleActivator {

	public static final String UNIQUE_SERVICE_ID = "unique.service.id";

	@Override
	public void start(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, HttpServletService.class,
			new HttpServletServiceServiceTrackerCustomizer(bundleContext));

		_serviceTracker.open();
	}

	@Override
	public void stop(BundleContext bundleContext) {
		_serviceTracker.close();
	}

	private String[] _getHttpServiceEndpoints(
		ServletContext servletContext, String servletName) {

		int majorVersion = servletContext.getMajorVersion();

		if (majorVersion < 3) {
			_log.error(
				StringBundler.concat(
					"The http container does not support servlet 3.0+. ",
					"Therefore, the value of ",
					HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT,
					" cannot be calculated."));

			return new String[0];
		}

		ServletRegistration servletRegistration = null;

		try {
			servletRegistration = servletContext.getServletRegistration(
				servletName);
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			_log.error(
				"Could not find the servlet registration for the servlet: " +
					servletName,
				unsupportedOperationException);
		}

		if (servletRegistration == null) {
			return new String[0];
		}

		List<String> httpServiceEndpoints = new ArrayList<>();

		for (String mapping : servletRegistration.getMappings()) {
			if (mapping.indexOf('/') == 0) {
				if (mapping.charAt(mapping.length() - 1) == '*') {
					mapping = mapping.substring(0, mapping.length() - 2);

					if ((mapping.length() > 1) &&
						(mapping.charAt(mapping.length() - 1) != '/')) {

						mapping += '/';
					}
				}

				httpServiceEndpoints.add(
					servletContext.getContextPath() + mapping);
			}
		}

		return httpServiceEndpoints.toArray(new String[0]);
	}

	private static final String[] _HTTP_SERVICES_CLASSES = {
		HttpService.class.getName(), ExtendedHttpService.class.getName()
	};

	private static final HttpSessionListener _HTTP_SESSION_LISTENER =
		new HttpSessionListener() {

			@Override
			public void sessionCreated(HttpSessionEvent httpSessionEvent) {
			}

			@Override
			public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
				HttpSession httpSession = httpSessionEvent.getSession();

				HttpSessionTracker.clearHttpSessionAdaptors(
					httpSession.getId());
			}

		};

	private static final Log _log = LogFactoryUtil.getLog(
		HttpServletImplBundleActivator.class.getName());

	private ServiceTracker<HttpServletService, HttpServiceBag> _serviceTracker;

	private class HttpServletServiceServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<HttpServletService, HttpServiceBag> {

		public HttpServletServiceServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public HttpServiceBag addingService(
			ServiceReference<HttpServletService> serviceReference) {

			HttpServletService httpServletService = _bundleContext.getService(
				serviceReference);

			ServletConfig servletConfig = httpServletService.getServletConfig();

			ProxyServlet proxyServlet = new ProxyServlet();

			try {
				proxyServlet.init(servletConfig);
			}
			catch (ServletException servletException) {
				_log.error(servletException);
			}

			ServletContext servletContext = servletConfig.getServletContext();

			long uniqueServiceId = SecureRandomUtil.nextLong();

			String targetFilter = StringBundler.concat(
				"(", UNIQUE_SERVICE_ID, "=", uniqueServiceId, ")");

			Map<String, Object> attributesMap =
				HashMapBuilder.<String, Object>put(
					UNIQUE_SERVICE_ID, uniqueServiceId
				).put(
					ListUtil.fromEnumeration(
						servletConfig.getInitParameterNames()),
					servletConfig::getInitParameter
				).put(
					HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT,
					() -> {
						Object httpServiceEndpoint =
							servletConfig.getInitParameter(
								HttpServiceRuntimeConstants.
									HTTP_SERVICE_ENDPOINT);

						if (httpServiceEndpoint != null) {
							return null;
						}

						return _getHttpServiceEndpoints(
							servletContext, servletConfig.getServletName());
					}
				).build();

			HttpServiceRuntimeImpl httpServiceRuntimeImpl =
				new HttpServiceRuntimeImpl(
					_bundleContext, _bundleContext, servletContext,
					targetFilter, Collections.unmodifiableMap(attributesMap));

			proxyServlet.setHttpServiceRuntimeImpl(httpServiceRuntimeImpl);

			PortletSessionListenerManager.addHttpSessionListener(
				_HTTP_SESSION_LISTENER);

			return new HttpServiceBag(
				proxyServlet, httpServiceRuntimeImpl,
				_bundleContext.registerService(
					HttpServlet.class, proxyServlet,
					HashMapDictionaryBuilder.put(
						Arrays.asList(serviceReference.getPropertyKeys()),
						serviceReference::getProperty
					).build()),
				_bundleContext.registerService(
					_HTTP_SERVICES_CLASSES,
					new HttpServiceFactory(httpServiceRuntimeImpl),
					HashMapDictionaryBuilder.putAll(
						attributesMap
					).build()),
				_bundleContext.registerService(
					HttpServiceRuntime.class, httpServiceRuntimeImpl,
					HashMapDictionaryBuilder.putAll(
						attributesMap
					).put(
						HttpServiceRuntimeConstants.HTTP_SERVICE_ID,
						() -> {
							Collection<ServiceReference<HttpService>>
								serviceReferences =
									_bundleContext.getServiceReferences(
										HttpService.class, targetFilter);

							Iterator<ServiceReference<HttpService>> iterator =
								serviceReferences.iterator();

							ServiceReference<?>
								httpServiceFactoryServiceReference =
									iterator.next();

							return Collections.singletonList(
								httpServiceFactoryServiceReference.getProperty(
									Constants.SERVICE_ID));
						}
					).build()),
				_bundleContext.registerService(
					ServletContextHelper.class,
					new ServiceFactory<ServletContextHelper>() {

						@Override
						public ServletContextHelper getService(
							Bundle bundle,
							ServiceRegistration<ServletContextHelper>
								serviceRegistration) {

							return new ServletContextHelper(bundle) {
							};
						}

						@Override
						public void ungetService(
							Bundle bundle,
							ServiceRegistration<ServletContextHelper>
								serviceRegistration,
							ServletContextHelper servletContextHelper) {
						}

					},
					HashMapDictionaryBuilder.<String, Object>put(
						Constants.SERVICE_RANKING, Integer.MIN_VALUE
					).put(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
						HttpWhiteboardConstants.
							HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME
					).put(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
						Const.SLASH
					).put(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET,
						targetFilter
					).build()));
		}

		@Override
		public void modifiedService(
			ServiceReference<HttpServletService> serviceReference,
			HttpServiceBag httpServiceBag) {

			removedService(serviceReference, httpServiceBag);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<HttpServletService> serviceReference,
			HttpServiceBag httpServiceBag) {

			PortletSessionListenerManager.removeHttpSessionListener(
				_HTTP_SESSION_LISTENER);

			_bundleContext.ungetService(serviceReference);

			httpServiceBag.destroy();
		}

		private final BundleContext _bundleContext;

	}

}