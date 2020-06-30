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

package com.liferay.portal.profile;

import com.liferay.petra.string.StringPool;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

/**
 * @author Hai Yu
 */
public class BaseEnterpriseDSModulePortalProfile
	extends BaseDSModulePortalProfile {

	@Override
	public void activate() {
		Bundle bundle = _bundleContext.getBundle();

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (headers.get("Liferay-Enterprise-App") == null) {
			return;
		}

		Collection<ComponentDescriptionDTO> componentDescriptionDTOs =
			_serviceComponentRuntime.getComponentDescriptionDTOs(bundle);

		for (ComponentDescriptionDTO componentDescriptionDTO :
				componentDescriptionDTOs) {

			if (componentDescriptionDTO.defaultEnabled) {
				continue;
			}

			_serviceComponentRuntime.enableComponent(componentDescriptionDTO);
		}

		_bundleContext.ungetService(_serviceReference);
	}

	@Override
	protected void init(
		ComponentContext componentContext,
		Set<String> supportedPortalProfileNames, String... componentNames) {

		super.init(
			componentContext, supportedPortalProfileNames, componentNames);

		_bundleContext = componentContext.getBundleContext();

		_serviceReference = _bundleContext.getServiceReference(
			ServiceComponentRuntime.class);

		_serviceComponentRuntime = _bundleContext.getService(_serviceReference);
	}

	private BundleContext _bundleContext;
	private ServiceComponentRuntime _serviceComponentRuntime;
	private ServiceReference<ServiceComponentRuntime> _serviceReference;

}