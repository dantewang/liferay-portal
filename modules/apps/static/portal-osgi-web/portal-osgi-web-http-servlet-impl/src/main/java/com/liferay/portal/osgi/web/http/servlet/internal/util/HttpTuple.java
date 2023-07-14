/*******************************************************************************
 * Copyright (c) 2014, 2015 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal.util;

import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ProxyServlet;

import org.osgi.framework.ServiceRegistration;

/**
 * @author Raymond Augé
 */
public class HttpTuple {

	public HttpTuple(
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