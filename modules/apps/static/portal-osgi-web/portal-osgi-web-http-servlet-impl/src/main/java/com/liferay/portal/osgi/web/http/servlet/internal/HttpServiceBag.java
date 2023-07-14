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

package com.liferay.portal.osgi.web.http.servlet.internal;

import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ProxyServlet;

import org.osgi.framework.ServiceRegistration;

/**
 * @author Dante Wang
 */
public class HttpServiceBag {

	public HttpServiceBag(
		ProxyServlet proxyServlet,
		HttpServiceRuntimeImpl httpServiceRuntimeImpl,
		ServiceRegistration<?>... serviceRegistrations) {

		_proxyServlet = proxyServlet;
		_httpServiceRuntimeImpl = httpServiceRuntimeImpl;
		_serviceRegistrations = serviceRegistrations;
	}

	public void destroy() {
		for (int i = _serviceRegistrations.length - 1; i > 0; i--) {
			_serviceRegistrations[i].unregister();
		}

		_proxyServlet.setHttpServiceRuntimeImpl(null);
		_proxyServlet.destroy();

		_httpServiceRuntimeImpl.destroy();
	}

	private final HttpServiceRuntimeImpl _httpServiceRuntimeImpl;
	private final ProxyServlet _proxyServlet;
	private final ServiceRegistration<?>[] _serviceRegistrations;

}