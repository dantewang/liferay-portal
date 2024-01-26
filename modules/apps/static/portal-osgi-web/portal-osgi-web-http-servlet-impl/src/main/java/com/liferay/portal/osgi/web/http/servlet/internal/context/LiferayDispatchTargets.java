/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context;

import java.util.Collections;
import java.util.List;

import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.registration.EndpointRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;
import org.eclipse.equinox.http.servlet.internal.util.Const;

/**
 * @author Dante Wang
 */
public class LiferayDispatchTargets extends DispatchTargets {

	public LiferayDispatchTargets(
		LiferayContextController liferayContextController,
		EndpointRegistration<?> endpointRegistration,
		List<FilterRegistration> filterRegistrations, String servletName,
		String requestURI, String servletPath, String pathInfo,
		String queryString) {

		super(
			liferayContextController, endpointRegistration, filterRegistrations,
			servletName, requestURI, servletPath, pathInfo, queryString);

		_liferayContextController = liferayContextController;
		_endpointRegistration = endpointRegistration;
		_filterRegistrations = filterRegistrations;
		_servletName = servletName;
		_requestURI = requestURI;
		_servletPath = (servletPath == null) ? Const.BLANK : servletPath;
		_pathInfo = pathInfo;
		_queryString = queryString;
	}

	public LiferayDispatchTargets(
		LiferayContextController liferayContextController,
		EndpointRegistration<?> endpointRegistration, String servletName,
		String requestURI, String servletPath, String pathInfo,
		String queryString) {

		this(
			liferayContextController, endpointRegistration,
			Collections.emptyList(), servletName, requestURI, servletPath,
			pathInfo, queryString);
	}

	private final EndpointRegistration<?> _endpointRegistration;
	private final List<FilterRegistration> _filterRegistrations;
	private final LiferayContextController _liferayContextController;
	private final String _pathInfo;
	private final String _queryString;
	private final String _requestURI;
	private final String _servletName;
	private final String _servletPath;

}