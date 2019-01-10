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

package com.liferay.portal.template.internal;

import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.template.TemplateResourceParser;
import com.liferay.portal.template.URLResourceParser;

import java.net.URL;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(
	immediate = true,
	property = {
		"lang.type=" + TemplateConstants.LANG_TYPE_FTL,
		"lang.type=" + TemplateConstants.LANG_TYPE_SOY,
		"lang.type=" + TemplateConstants.LANG_TYPE_VM
	},
	service = TemplateResourceParser.class
)
public class ClassLoaderResourceParser extends URLResourceParser {

	public ClassLoaderResourceParser() {
		_classLoader = PortalClassLoaderUtil.getClassLoader();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 * 			#ClassLoaderResourceParser()}
	 */
	@Deprecated
	public ClassLoaderResourceParser(ClassLoader classLoader) {
		_classLoader = classLoader;
	}

	@Override
	@SuppressWarnings("deprecation")
	public URL getURL(String templateId) {
		if (templateId.contains(TemplateConstants.JOURNAL_SEPARATOR) ||
			templateId.contains(TemplateConstants.SERVLET_SEPARATOR) ||
			templateId.contains(TemplateConstants.TEMPLATE_SEPARATOR) ||
			templateId.contains(TemplateConstants.THEME_LOADER_SEPARATOR)) {

			return null;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Loading " + templateId);
		}

		ClassLoader classLoader = _classLoader;

		int pos = templateId.indexOf(TemplateConstants.CLASS_LOADER_SEPARATOR);

		if (pos >= 0) {
			classLoader = ClassLoaderPool.getClassLoader(
				templateId.substring(0, pos));

			templateId = templateId.substring(
				pos + TemplateConstants.CLASS_LOADER_SEPARATOR.length());
		}

		return classLoader.getResource(templateId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClassLoaderResourceParser.class);

	private final ClassLoader _classLoader;

}