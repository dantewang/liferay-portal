/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal;

import org.eclipse.equinox.http.servlet.internal.HttpServiceRuntimeImpl;

import org.osgi.framework.ServiceRegistration;

/**
 * @author Dante Wang
 */
public class HttpServletEndpointRegistrationBag {

	public HttpServletEndpointRegistrationBag(
		HttpServiceRuntimeImpl httpServiceRuntimeImpl,
		ServiceRegistration<?>... serviceRegistrations) {

		_httpServiceRuntimeImpl = httpServiceRuntimeImpl;
		_serviceRegistrations = serviceRegistrations;
	}

	public void close() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_httpServiceRuntimeImpl.destroy();
	}

	private final HttpServiceRuntimeImpl _httpServiceRuntimeImpl;
	private final ServiceRegistration<?>[] _serviceRegistrations;

}