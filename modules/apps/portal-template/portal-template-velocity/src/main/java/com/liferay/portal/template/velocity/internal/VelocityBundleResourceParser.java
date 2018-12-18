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

package com.liferay.portal.template.velocity.internal;

import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.template.TemplateResourceParser;
import com.liferay.portal.template.URLResourceParser;

import java.net.URL;

import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true, property = "lang.type=" + TemplateConstants.LANG_TYPE_VM,
	service = TemplateResourceParser.class
)
public class VelocityBundleResourceParser extends URLResourceParser {

	@Override
	@SuppressWarnings("deprecation")
	public URL getURL(String templateId) {
		if (templateId.contains(TemplateConstants.JOURNAL_SEPARATOR) ||
			templateId.contains(TemplateConstants.SERVLET_SEPARATOR) ||
			templateId.contains(TemplateConstants.TEMPLATE_SEPARATOR) ||
			templateId.contains(TemplateConstants.THEME_LOADER_SEPARATOR)) {

			return null;
		}

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResource(templateId);
	}

}