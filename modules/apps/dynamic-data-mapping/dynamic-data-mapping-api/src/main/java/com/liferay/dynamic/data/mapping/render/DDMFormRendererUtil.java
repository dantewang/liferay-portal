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

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Marcellus Tavares
 */
public class DDMFormRendererUtil {

	public static DDMFormRenderer getDDMFormRenderer() {
		return _serviceTracker.getService();
	}

	public static String render(
			DDMForm ddmForm,
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws PortalException {

		return getDDMFormRenderer().render(
			ddmForm, ddmFormFieldRenderingContext);
	}

	private static final ServiceTracker<DDMFormRenderer, DDMFormRenderer>
		_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(DDMFormRenderer.class);

		ServiceTracker<DDMFormRenderer, DDMFormRenderer> serviceTracker =
			new ServiceTracker<>(
				bundle.getBundleContext(), DDMFormRenderer.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}