/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import java.io.IOException;

import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.equinox.http.servlet.internal.registration.EndpointRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;

/**
 * @author Dante Wang
 */
public class FilterChainImpl
	extends org.eclipse.equinox.http.servlet.internal.servlet.FilterChainImpl {

	public FilterChainImpl(
		List<FilterRegistration> matchingFilterRegistrations,
		EndpointRegistration<?> endpointRegistration,
		DispatcherType dispatcherType) {

		super(
			matchingFilterRegistrations, endpointRegistration, dispatcherType);

		_matchingFilterRegistrations = matchingFilterRegistrations;
		_endpointRegistration = endpointRegistration;
		_dispatcherType = dispatcherType;

		_filterCount = matchingFilterRegistrations.size();
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

	@Override
	public DispatcherType getDispatcherType() {
		return _dispatcherType;
	}

	private final DispatcherType _dispatcherType;
	private final EndpointRegistration<?> _endpointRegistration;
	private final int _filterCount;
	private int _filterIndex;
	private final List<FilterRegistration> _matchingFilterRegistrations;

}