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

package com.liferay.dynamic.data.mapping.storage;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Bruno Basto
 */
public class FieldRendererFactory {

	public static FieldRenderer getFieldRenderer(String dataType) {
		FieldRenderer fieldRenderer = _fieldRenderers.getService(dataType);

		if (fieldRenderer == null) {
			fieldRenderer = _fieldRenderers.getService(FieldConstants.STRING);
		}

		return fieldRenderer;
	}

	private static final ServiceTrackerMap<String, FieldRenderer>
		_fieldRenderers;

	static {
		Bundle bundle = FrameworkUtil.getBundle(BaseFieldRenderer.class);

		_fieldRenderers = ServiceTrackerMapFactory.openSingleValueMap(
			bundle.getBundleContext(), FieldRenderer.class, "key");
	}

}