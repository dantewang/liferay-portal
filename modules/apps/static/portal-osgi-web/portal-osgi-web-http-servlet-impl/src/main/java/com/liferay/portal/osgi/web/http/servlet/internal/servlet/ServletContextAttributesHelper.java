/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.portal.kernel.util.ProxyFactory;

import javax.servlet.ServletContext;

import org.eclipse.equinox.http.servlet.internal.context.ProxyContext;

/**
 * @author Dante Wang
 */
public class ServletContextAttributesHelper extends ProxyContext {

	public ServletContextAttributesHelper(
		String contextName, ServletContext servletContext) {

		super(contextName, _dummyServletContext);
	}

	private static final ServletContext _dummyServletContext =
		ProxyFactory.newDummyInstance(ServletContext.class);

}