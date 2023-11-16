/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context.customizer;

import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;

import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.registration.Registration;

import org.osgi.dto.DTO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Dante Wang
 */
public abstract class BaseServiceTrackerCustomizer
	<S, T extends Registration<?, ? extends DTO>>
		implements ServiceTrackerCustomizer<S, T> {

	public BaseServiceTrackerCustomizer(
		BundleContext bundleContext,
		HttpServletEndpointController httpServletEndpointController,
		LiferayContextController liferayContextController) {

		this.bundleContext = bundleContext;
		this.httpServletEndpointController = httpServletEndpointController;
		this.liferayContextController = liferayContextController;
	}

	@Override
	public void modifiedService(
		ServiceReference<S> serviceReference, T registration) {
	}

	@Override
	public void removedService(
		ServiceReference<S> serviceReference, T registration) {

		registration.destroy();
	}

	protected BundleContext bundleContext;
	protected HttpServletEndpointController httpServletEndpointController;
	protected LiferayContextController liferayContextController;

}