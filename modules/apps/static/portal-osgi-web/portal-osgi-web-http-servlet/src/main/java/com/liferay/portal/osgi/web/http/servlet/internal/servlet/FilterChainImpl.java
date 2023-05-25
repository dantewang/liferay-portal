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
 * Copyright (c) 2011, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;

import java.io.IOException;

import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dante Wang
 * @author Raymond Augé
 */
public class FilterChainImpl implements FilterChain {

	public FilterChainImpl(
		List<FilterRegistration> matchingFilterRegistrations,
		EndpointRegistration<?> registration, DispatcherType dispatcherType) {

		_matchingFilterRegistrations = matchingFilterRegistrations;

		_filterCount = matchingFilterRegistrations.size();

		_dispatcherType = dispatcherType;

		_endpointRegistration = registration;
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		while (_filterIndex < _filterCount) {
			FilterRegistration filterRegistration =
				_matchingFilterRegistrations.get(_filterIndex++);

			if (filterRegistration.appliesTo(this)) {
				filterRegistration.doFilter(
					(HttpServletRequest)servletRequest,
					(HttpServletResponse)servletResponse, this);

				return;
			}
		}

		_endpointRegistration.service(
			(HttpServletRequest)servletRequest,
			(HttpServletResponse)servletResponse);
	}

	public DispatcherType getDispatcherType() {
		return _dispatcherType;
	}

	private final DispatcherType _dispatcherType;
	private final EndpointRegistration<?> _endpointRegistration;
	private final int _filterCount;
	private int _filterIndex;
	private final List<FilterRegistration> _matchingFilterRegistrations;

}