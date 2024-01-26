/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.servlet.ResponseStateHandler;

/**
 * @author Dante Wang
 */
public class LiferayResponseStateHandler extends ResponseStateHandler {

	public LiferayResponseStateHandler(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DispatchTargets dispatchTargets) {

		super(httpServletRequest, httpServletResponse, dispatchTargets);
	}

}