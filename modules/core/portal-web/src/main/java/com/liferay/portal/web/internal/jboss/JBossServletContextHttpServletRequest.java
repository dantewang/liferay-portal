/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.web.internal.jboss;

import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Dante Wang
 */
public class JBossServletContextHttpServletRequest
	extends PersistentHttpServletRequestWrapper {

	public JBossServletContextHttpServletRequest(
		HttpServletRequest httpServletRequest, ServletContext servletContext) {

		super(httpServletRequest);

		_servletContext = servletContext;
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	private final ServletContext _servletContext;

}