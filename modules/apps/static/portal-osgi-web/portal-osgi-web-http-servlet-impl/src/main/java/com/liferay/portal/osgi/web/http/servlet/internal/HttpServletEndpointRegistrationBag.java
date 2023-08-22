/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal;

import org.osgi.framework.ServiceRegistration;

/**
 * @author Dante Wang
 */
public class HttpServletEndpointRegistrationBag {

	public HttpServletEndpointRegistrationBag(
		ServiceRegistration<?>... serviceRegistrations) {

		_serviceRegistrations = serviceRegistrations;
	}

	public void close() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private final ServiceRegistration<?>[] _serviceRegistrations;

}