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

package com.liferay.portal.license.enterprise.app.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.license.LicenseInfo;
import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.IOException;
import java.io.Writer;

import java.util.Date;

import javax.portlet.PortletResponse;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dante Wang
 */
public class EnterpriseAppPortletServletFilter implements Filter {

	public EnterpriseAppPortletServletFilter(
		String productId, LicenseManager licenseManager) {

		_productId = productId;
		_licenseManager = licenseManager;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		LicenseInfo licenseInfo = _licenseManager.getLicenseInfo(_productId);

		Date expirationDate = licenseInfo.getExpirationDate();

		long expirationDays =
			(expirationDate.getTime() - System.currentTimeMillis()) / Time.DAY;

		if (expirationDays <= 0) {
			servletRequest.setAttribute(
				"ERROR_MESSAGE",
				StringBundler.concat(
					"Your license for product ", _productId, " expired ",
					expirationDays * -1, " days ago"));

			HttpServletResponse httpServletResponse =
				PortalUtil.getHttpServletResponse(
					(PortletResponse)servletRequest.getAttribute(
						JavaConstants.JAVAX_PORTLET_RESPONSE));

			httpServletResponse.sendRedirect(
				PortalUtil.getPathContext() + "/c/portal/license");

			return;
		}

		Date startDate = licenseInfo.getStartDate();

		long lifetimeDays =
			(expirationDate.getTime() - startDate.getTime()) / Time.DAY;

		if (((lifetimeDays == 30) && (expirationDays < 7)) ||
			((lifetimeDays > 30) && (expirationDays < 30))) {

			Writer writer = servletResponse.getWriter();

			writer.write(
				StringBundler.concat(
					"Update your <a class=\"alert-link\" href=\"",
					PortalUtil.getPathMain(),
					"/portal/license\">activation key</a>, \", it will be ",
					"expired in ", expirationDays, " day(s)"));
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	private final LicenseManager _licenseManager;
	private final String _productId;

}