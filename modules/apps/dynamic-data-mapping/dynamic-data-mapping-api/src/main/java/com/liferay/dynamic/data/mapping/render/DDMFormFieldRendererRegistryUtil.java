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

package com.liferay.dynamic.data.mapping.render;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Pablo Carvalho
 */
public class DDMFormFieldRendererRegistryUtil {

	public static DDMFormFieldRenderer getDDMFormFieldRenderer(
		String ddmFormFieldType) {

		return getDDMFormFieldRendererRegistry().getDDMFormFieldRenderer(
			ddmFormFieldType);
	}

	public static DDMFormFieldRendererRegistry
		getDDMFormFieldRendererRegistry() {

		return _serviceTracker.getService();
	}

	private static final ServiceTracker
		<DDMFormFieldRendererRegistry, DDMFormFieldRendererRegistry>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			DDMFormFieldRendererRegistry.class);

		ServiceTracker
			<DDMFormFieldRendererRegistry, DDMFormFieldRendererRegistry>
				serviceTracker = new ServiceTracker<>(
					bundle.getBundleContext(),
					DDMFormFieldRendererRegistry.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}