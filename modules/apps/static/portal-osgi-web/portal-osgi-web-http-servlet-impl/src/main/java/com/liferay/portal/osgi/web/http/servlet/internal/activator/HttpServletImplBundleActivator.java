/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.activator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PortletSessionListenerManager;
import com.liferay.portal.osgi.web.http.servlet.HttpServletEndpoint;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointRegistrationBag;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.eclipse.equinox.http.servlet.internal.Activator;
import org.eclipse.equinox.http.servlet.internal.servlet.HttpSessionTracker;
import org.eclipse.equinox.http.servlet.internal.servlet.ProxyServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Dante Wang
 */
public class HttpServletImplBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_activator = new Activator();

		_activator.start(bundleContext);

		PortletSessionListenerManager.addHttpSessionListener(
			_INVALIDATEHTTPSESSION_LISTENER);

		_serviceTracker = new ServiceTracker<>(
			bundleContext, HttpServletEndpoint.class,
			new HttpServletServiceServiceTrackerCustomizer(bundleContext));

		_serviceTracker.open();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		_serviceTracker.close();

		PortletSessionListenerManager.removeHttpSessionListener(
			_INVALIDATEHTTPSESSION_LISTENER);

		_activator.stop(bundleContext);
	}

	private static final HttpSessionListener _INVALIDATEHTTPSESSION_LISTENER =
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

	private Activator _activator;
	private ServiceTracker
		<HttpServletEndpoint, HttpServletEndpointRegistrationBag>
			_serviceTracker;

	private static class HttpServletServiceServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<HttpServletEndpoint, HttpServletEndpointRegistrationBag> {

		public HttpServletServiceServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public HttpServletEndpointRegistrationBag addingService(
			ServiceReference<HttpServletEndpoint> serviceReference) {

			HttpServletEndpoint httpServletEndpoint = _bundleContext.getService(
				serviceReference);

			ProxyServlet proxyServlet = new ProxyServlet() {

				@Override
				public ServletConfig getServletConfig() {
					return _servletConfig;
				}

				@Override
				public void init(ServletConfig servletConfig) {
					_servletConfig = servletConfig;
				}

				private ServletConfig _servletConfig;

			};

			try {
				proxyServlet.init(httpServletEndpoint.getServletConfig());
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return new HttpServletEndpointRegistrationBag(
				_bundleContext.registerService(
					HttpServlet.class, proxyServlet,
					httpServletEndpoint.getProperties()));
		}

		@Override
		public void modifiedService(
			ServiceReference<HttpServletEndpoint> serviceReference,
			HttpServletEndpointRegistrationBag
				httpServletEndpointRegistrationBag) {

			removedService(
				serviceReference, httpServletEndpointRegistrationBag);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<HttpServletEndpoint> serviceReference,
			HttpServletEndpointRegistrationBag
				httpServletEndpointRegistrationBag) {

			httpServletEndpointRegistrationBag.close();

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}