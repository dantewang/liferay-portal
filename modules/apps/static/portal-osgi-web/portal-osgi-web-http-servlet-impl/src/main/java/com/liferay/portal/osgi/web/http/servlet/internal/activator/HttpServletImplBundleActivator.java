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
import com.liferay.portal.kernel.servlet.PortletSessionListenerManager;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.osgi.web.http.servlet.ExtendedHttpService;
import com.liferay.portal.osgi.web.http.servlet.HttpServletService;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceFactory;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.HttpSessionTracker;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ProxyServlet;
import com.liferay.portal.osgi.web.http.servlet.internal.util.HttpTuple;
import com.liferay.portal.osgi.web.http.servlet.internal.util.UMDictionaryMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;
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
			new ServiceTrackerCustomizer<HttpServletService, HttpTuple>() {

				@Override
				public HttpTuple addingService(
					ServiceReference<HttpServletService> serviceReference) {

					HttpServletService httpServletService =
						bundleContext.getService(serviceReference);

					ServletConfig servletConfig =
						httpServletService.getServletConfig();

					ProxyServlet proxyServlet = new ProxyServlet();

					try {
						proxyServlet.init(servletConfig);
					}
					catch (ServletException servletException) {
						_log.error(servletException);
					}

					ServiceRegistration<HttpServlet>
						proxyServletServiceRegistration =
							bundleContext.registerService(
								HttpServlet.class, proxyServlet,
								HashMapDictionaryBuilder.put(
									Arrays.asList(
										serviceReference.getPropertyKeys()),
									serviceReference::getProperty
								).build());

					ServletContext servletContext =
						servletConfig.getServletContext();

					Dictionary<String, Object> dictionary =
						new HashMapDictionary<>();

					Enumeration<String> initParameterNamesEnumeration =
						servletConfig.getInitParameterNames();

					while (initParameterNamesEnumeration.hasMoreElements()) {
						String name =
							initParameterNamesEnumeration.nextElement();

						dictionary.put(
							name, servletConfig.getInitParameter(name));
					}

					Object httpServiceEndpointObject = dictionary.get(
						HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT);

					if (httpServiceEndpointObject == null) {
						dictionary.put(
							HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT,
							_getHttpServiceEndpoints(
								servletContext,
								servletConfig.getServletName()));
					}

					Random random = new Random();

					dictionary.put(UNIQUE_SERVICE_ID, random.nextLong());

					HttpServiceRuntimeImpl httpServiceRuntimeImpl =
						new HttpServiceRuntimeImpl(
							bundleContext, bundleContext, servletContext,
							new UMDictionaryMap<>(dictionary));

					proxyServlet.setHttpServiceRuntimeImpl(
						httpServiceRuntimeImpl);

					HttpServiceFactory httpServiceFactory =
						new HttpServiceFactory(httpServiceRuntimeImpl);

					ServiceRegistration<?>
						httpServiceFactoryServiceRegistration =
							bundleContext.registerService(
								_HTTP_SERVICES_CLASSES, httpServiceFactory,
								dictionary);

					ServiceReference<?> httpServiceFactoryServiceReference =
						httpServiceFactoryServiceRegistration.getReference();

					dictionary.put(
						HttpServiceRuntimeConstants.HTTP_SERVICE_ID,
						Collections.singletonList(
							httpServiceFactoryServiceReference.getProperty(
								Constants.SERVICE_ID)));

					ServiceRegistration<HttpServiceRuntime>
						httpServiceRuntimeServiceRegistration =
							bundleContext.registerService(
								HttpServiceRuntime.class,
								httpServiceRuntimeImpl, dictionary);

					PortletSessionListenerManager.addHttpSessionListener(
						_HTTP_SESSION_LISTENER);

					return new HttpTuple(
						proxyServlet, proxyServletServiceRegistration,
						httpServiceFactory,
						httpServiceFactoryServiceRegistration,
						httpServiceRuntimeImpl,
						httpServiceRuntimeServiceRegistration);
				}

				@Override
				public void modifiedService(
					ServiceReference<HttpServletService> serviceReference,
					HttpTuple httpTuple) {

					removedService(serviceReference, httpTuple);

					addingService(serviceReference);
				}

				@Override
				public void removedService(
					ServiceReference<HttpServletService> serviceReference,
					HttpTuple httpTuple) {

					PortletSessionListenerManager.removeHttpSessionListener(
						_HTTP_SESSION_LISTENER);

					bundleContext.ungetService(serviceReference);

					httpTuple.destroy();
				}

			});

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

	private ServiceTracker<HttpServletService, HttpTuple> _serviceTracker;

}