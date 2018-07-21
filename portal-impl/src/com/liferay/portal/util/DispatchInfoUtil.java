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

package com.liferay.portal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portlet.AsyncPortletServletRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * @author Dante Wang
 * @author Leon Chi
 */
public class DispatchInfoUtil {

	public static void updateDispatchInfo(
		AsyncPortletServletRequest asyncPortletServletRequest,
		ServletContext servletContext, String path) {

		Map<String, ServletRegistration> servletRegistrationMap =
			(Map<String, ServletRegistration>)
				servletContext.getServletRegistrations();

		Collection<ServletRegistration> servletRegistrations =
			servletRegistrationMap.values();

		Stream<ServletRegistration> servletRegistrationStream =
			servletRegistrations.stream();

		Set<String> servletURLPatterns = servletRegistrationStream.flatMap(
			servletRegistration -> {
				Collection<String> mappings = servletRegistration.getMappings();

				return mappings.stream();
			}
		).collect(
			Collectors.toSet()
		);

		String contextPath = servletContext.getContextPath();
		String pathInfo = null;
		String queryString = null;
		String requestURI = null;
		String servletPath = null;

		// TODO: what if Liferay is deployed into e.g. /liferay?

		if ((contextPath.length() > 0) && path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		if (path != null) {
			String pathNoQueryString = path;

			int pos = path.indexOf(CharPool.QUESTION);

			if (pos != -1) {
				pathNoQueryString = path.substring(0, pos);
				queryString = path.substring(pos + 1);
			}

			for (String urlPattern : servletURLPatterns) {
				if (urlPattern.endsWith("/*")) {
					int length = urlPattern.length() - 2;

					if ((pathNoQueryString.length() > length) &&
						pathNoQueryString.regionMatches(
							0, urlPattern, 0, length) &&
						(pathNoQueryString.charAt(length) == CharPool.SLASH)) {

						pathInfo = pathNoQueryString.substring(length);
						servletPath = urlPattern.substring(0, length);

						break;
					}
				}
			}

			if (servletPath == null) {
				servletPath = pathNoQueryString;
			}

			if (contextPath.equals(StringPool.SLASH)) {
				requestURI = pathNoQueryString;
			}
			else {
				requestURI = contextPath + pathNoQueryString;
			}
		}

		asyncPortletServletRequest.setContextPath(contextPath);
		asyncPortletServletRequest.setPathInfo(pathInfo);
		asyncPortletServletRequest.setQueryString(queryString);
		asyncPortletServletRequest.setRequestURI(requestURI);
		asyncPortletServletRequest.setServletPath(servletPath);
	}

}