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

package com.liferay.opensocial.shindig.servlet;

import com.google.inject.Injector;

import com.liferay.opensocial.shindig.util.HttpServletRequestThreadLocal;
import com.liferay.opensocial.shindig.util.ShindigUtil;
import com.liferay.petra.string.StringPool;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shindig.common.servlet.GuiceServletContextListener;
import org.apache.shindig.common.servlet.InjectedFilter;

/**
 * @author Michael Young
 * @author Dennis Ju
 */
public class ShindigFilter extends InjectedFilter {

	public void destroy() {
	}

	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		if (injector == null) {
			HttpSession session = httpServletRequest.getSession();

			_init(session.getServletContext());
		}

		ShindigUtil.setScheme(servletRequest.getScheme());

		String serverName = servletRequest.getServerName();

		String host = serverName.concat(
			StringPool.COLON
		).concat(
			String.valueOf(servletRequest.getServerPort())
		);

		ShindigUtil.setHost(host);

		HttpServletRequestThreadLocal.setHttpServletRequest(httpServletRequest);

		try {
			filterChain.doFilter(servletRequest, servletResponse);
		}
		finally {
			HttpServletRequestThreadLocal.setHttpServletRequest(null);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// LPS-23577 and LPS-41715

		injector = null;
	}

	private void _init(ServletContext servletContext) throws ServletException {
		injector = (Injector)servletContext.getAttribute(
			GuiceServletContextListener.INJECTOR_ATTRIBUTE);

		if (injector == null) {
			injector = (Injector)servletContext.getAttribute(
				GuiceServletContextListener.INJECTOR_NAME);

			if (injector == null) {
				throw new UnavailableException(
					"Guice injector is not available. Please register " +
						GuiceServletContextListener.class.getName() + ".");
			}
		}

		injector.injectMembers(this);
	}

}