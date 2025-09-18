/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.web.internal.jboss;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

/**
 * @author Dante Wang
 */
public class JBossServletContextFilter implements Filter {

	public JBossServletContextFilter(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		if (servletRequest instanceof HttpServletRequest) {
			servletRequest = _getWrappedHttpServletRequest(
				(HttpServletRequest)servletRequest);
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private HttpServletRequest _getWrappedHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		HttpServletRequest wrappedHttpServletRequest = httpServletRequest;

		while (wrappedHttpServletRequest instanceof
					HttpServletRequestWrapper httpServletRequestWrapper) {

			if (httpServletRequestWrapper instanceof
					JBossServletContextHttpServletRequest) {

				return httpServletRequest;
			}

			wrappedHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		return new JBossServletContextHttpServletRequest(
			httpServletRequest, _servletContext);
	}

	private final ServletContext _servletContext;

}