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

package com.liferay.portal.template;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.ClassLoaderTemplateResource;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.FileUtil;

import java.net.URL;

/**
 * @author Tina Tian
 * @deprecated As of Mueller (7.2.x), with no direct replacement
 */
@Deprecated
public class ClassLoaderResourceParser extends URLResourceParser {

	public ClassLoaderResourceParser() {
		Class<?> clazz = getClass();

		_classLoader = clazz.getClassLoader();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public ClassLoaderResourceParser(ClassLoader classLoader) {
		throw new UnsupportedOperationException();
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

		URL url = _classLoader.getResource(templateId);

		if (url != null) {
			return url;
		}

		TemplateResource templateResource = _getTemplateResource(templateId);

		if ((templateResource != null) &&
			(templateResource instanceof ClassLoaderTemplateResource)) {

			ClassLoaderTemplateResource classLoaderTemplateResource =
				(ClassLoaderTemplateResource)templateResource;

			ClassLoader classLoader =
				classLoaderTemplateResource.getClassLoader();

			return classLoader.getResource(templateId);
		}

		return null;
	}

	private TemplateResource _getTemplateResource(String templateId) {
		TemplateResource templateResource =
			TemplateResourceThreadLocal.getTemplateResource(
				FileUtil.getExtension(templateId));

		if (templateResource instanceof CacheTemplateResource) {
			CacheTemplateResource cacheTemplateResource =
				(CacheTemplateResource)templateResource;

			return cacheTemplateResource.getInnerTemplateResource();
		}

		return templateResource;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClassLoaderResourceParser.class);

	private final ClassLoader _classLoader;

}