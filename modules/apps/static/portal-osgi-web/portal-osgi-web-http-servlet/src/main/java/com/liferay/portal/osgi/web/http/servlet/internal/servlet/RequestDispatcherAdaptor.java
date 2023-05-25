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

/*******************************************************************************
 * Copyright (c) 2005, 2015 Cognos Incorporated, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cognos Incorporated - initial API and implementation
 *     IBM Corporation - bug fixes and enhancements
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.osgi.web.http.servlet.internal.context.DispatchTargets;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dante Wang
 * @author Cognos Incorporated
 * @author IBM Corporation
 * @author Raymond Augé
 */
public class RequestDispatcherAdaptor implements RequestDispatcher {

	public RequestDispatcherAdaptor(
		DispatchTargets dispatchTargets, String path) {

		_dispatchTargets = dispatchTargets;
		_path = path;
	}

	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_dispatchTargets.doDispatch(
			(HttpServletRequest)servletRequest,
			(HttpServletResponse)servletResponse, _path,
			DispatcherType.FORWARD);
	}

	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_dispatchTargets.doDispatch(
			(HttpServletRequest)servletRequest,
			(HttpServletResponse)servletResponse, _path,
			DispatcherType.INCLUDE);
	}

	@Override
	public String toString() {
		String value = _string;

		if (value == null) {
			value = StringBundler.concat(
				RequestDispatcherAdaptor.class.getSimpleName(), '[', _path,
				", ", _dispatchTargets, ']');

			_string = value;
		}

		return value;
	}

	private final DispatchTargets _dispatchTargets;
	private final String _path;
	private String _string;

}