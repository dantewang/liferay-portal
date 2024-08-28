/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.portal.search.rest.internal.servlet.APIEndpointCompatibilityServlet",
		"osgi.http.whiteboard.servlet.pattern=/portal-search-rest/*"
	},
	service = Servlet.class
)
public class APIEndpointCompatibilityServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		String requestURL = httpServletRequest.getRequestURL(
		).toString();

		httpServletResponse.sendRedirect(
			StringUtil.replace(requestURL, "/portal-search-rest", "/search") +
				StringPool.QUESTION + httpServletRequest.getQueryString());
	}

}