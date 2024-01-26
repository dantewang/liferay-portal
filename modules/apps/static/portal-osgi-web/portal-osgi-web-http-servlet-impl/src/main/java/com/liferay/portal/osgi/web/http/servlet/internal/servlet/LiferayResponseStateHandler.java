/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import java.io.IOException;

import java.util.Collections;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.registration.EndpointRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;
import org.eclipse.equinox.http.servlet.internal.servlet.FilterChainImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.HttpServletRequestWrapperImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.HttpServletResponseWrapperImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.Match;
import org.eclipse.equinox.http.servlet.internal.servlet.ResponseStateHandler;
import org.eclipse.equinox.http.servlet.internal.util.EventListeners;

import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Dante Wang
 */
public class LiferayResponseStateHandler extends ResponseStateHandler {

	public LiferayResponseStateHandler(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DispatchTargets dispatchTargets) {

		super(httpServletRequest, httpServletResponse, dispatchTargets);

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_dispatchTargets = dispatchTargets;
	}

	@Override
	public void processRequest() throws IOException, ServletException {
		ContextController contextController =
			_dispatchTargets.getContextController();

		EventListeners eventListeners = contextController.getEventListeners();

		List<ServletRequestListener> servletRequestListeners =
			eventListeners.get(ServletRequestListener.class);

		EndpointRegistration<?> endpointRegistration =
			_dispatchTargets.getServletRegistration();

		endpointRegistration.addReference();

		List<FilterRegistration> matchingFilterRegistrations =
			_dispatchTargets.getMatchingFilterRegistrations();

		for (FilterRegistration matchingFilterRegistration :
				matchingFilterRegistrations) {

			matchingFilterRegistration.addReference();
		}

		ServletRequestEvent servletRequestEvent = null;

		try {
			if ((_dispatchTargets.getDispatcherType() ==
					DispatcherType.REQUEST) &&
				!servletRequestListeners.isEmpty()) {

				servletRequestEvent = new ServletRequestEvent(
					endpointRegistration.getServletContext(),
					_httpServletRequest);

				for (ServletRequestListener servletRequestListener :
						servletRequestListeners) {

					servletRequestListener.requestInitialized(
						servletRequestEvent);
				}
			}

			ServletContextHelper servletContextHelper =
				endpointRegistration.getServletContextHelper();

			if (servletContextHelper.handleSecurity(
					_httpServletRequest, _httpServletResponse)) {

				if (matchingFilterRegistrations.isEmpty()) {
					endpointRegistration.service(
						_httpServletRequest, _httpServletResponse);
				}
				else {
					Collections.sort(matchingFilterRegistrations);

					FilterChain filterChain = new FilterChainImpl(
						matchingFilterRegistrations, endpointRegistration,
						_dispatchTargets.getDispatcherType());

					filterChain.doFilter(
						_httpServletRequest, _httpServletResponse);
				}
			}
		}
		catch (Exception exception) {
			setException(exception);

			if (_dispatchTargets.getDispatcherType() !=
					DispatcherType.REQUEST) {

				_throwException(exception);
			}
		}
		finally {
			endpointRegistration.removeReference();

			for (FilterRegistration filterRegistration :
					matchingFilterRegistrations) {

				filterRegistration.removeReference();
			}

			if (_dispatchTargets.getDispatcherType() ==
					DispatcherType.REQUEST) {

				if (_exception != null) {
					_handleException();
				}
				else {
					_handleResponseCode();
				}

				for (ServletRequestListener servletRequestListener :
						servletRequestListeners) {

					servletRequestListener.requestDestroyed(
						servletRequestEvent);
				}
			}
		}
	}

	@Override
	public void setException(Exception exception) {
		_exception = exception;
	}

	private void _handleException() throws IOException, ServletException {
		if (!(_httpServletResponse instanceof HttpServletResponseWrapper)) {
			throw new IllegalStateException("Response is not a wrapper");
		}

		HttpServletResponseWrapper httpServletResponseWrapper =
			(HttpServletResponseWrapper)_httpServletResponse;

		HttpServletResponseWrapperImpl httpServletResponseWrapperImpl = null;

		while (true) {
			if (httpServletResponseWrapper instanceof
					HttpServletResponseWrapperImpl) {

				httpServletResponseWrapperImpl =
					(HttpServletResponseWrapperImpl)httpServletResponseWrapper;
			}
			else if (httpServletResponseWrapper.getResponse() instanceof
						HttpServletResponseWrapper) {

				httpServletResponseWrapper =
					(HttpServletResponseWrapper)
						httpServletResponseWrapper.getResponse();

				continue;
			}

			break;
		}

		if (httpServletResponseWrapperImpl == null) {
			throw new IllegalStateException("Can not locate response");
		}

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)httpServletResponseWrapperImpl.getResponse();

		if (httpServletResponse.isCommitted()) {
			_throwException(_exception);
		}

		ContextController contextController =
			_dispatchTargets.getContextController();

		Class<? extends Exception> clazz = _exception.getClass();

		DispatchTargets errorDispatchTargets =
			contextController.getDispatchTargets(
				clazz.getName(), null, null, null, null, null, Match.EXACT);

		if (errorDispatchTargets == null) {
			_throwException(_exception);
		}

		HttpServletRequestWrapperImpl httpServletRequestWrapperImpl =
			HttpServletRequestWrapperImpl.findHttpRuntimeRequest(
				_httpServletRequest);

		try {
			errorDispatchTargets.setDispatcherType(DispatcherType.ERROR);

			httpServletRequestWrapperImpl.push(errorDispatchTargets);

			HttpServletRequest httpServletRequest =
				new HttpServletRequestWrapper(_httpServletRequest) {

					@Override
					public Object getAttribute(String attributeName) {
						if (getDispatcherType() == DispatcherType.ERROR) {
							if (attributeName.equals(
									RequestDispatcher.ERROR_EXCEPTION)) {

								return _exception;
							}
							else if (attributeName.equals(
										RequestDispatcher.
											ERROR_EXCEPTION_TYPE)) {

								return clazz.getName();
							}
							else if (attributeName.equals(
										RequestDispatcher.ERROR_MESSAGE)) {

								return _exception.getMessage();
							}
							else if (attributeName.equals(
										RequestDispatcher.ERROR_REQUEST_URI)) {

								return _httpServletRequest.getRequestURI();
							}
							else if (attributeName.equals(
										RequestDispatcher.ERROR_SERVLET_NAME)) {

								return _dispatchTargets.getServletRegistration(
								).getName();
							}
							else if (attributeName.equals(
										RequestDispatcher.ERROR_STATUS_CODE)) {

								return HttpServletResponse.
									SC_INTERNAL_SERVER_ERROR;
							}
						}

						return super.getAttribute(attributeName);
					}

					@Override
					public DispatcherType getDispatcherType() {
						return DispatcherType.ERROR;
					}

				};

			LiferayResponseStateHandler liferayResponseStateHandler =
				new LiferayResponseStateHandler(
					httpServletRequest,
					new HttpServletResponseWrapperImpl(httpServletResponse),
					errorDispatchTargets);

			liferayResponseStateHandler.processRequest();

			httpServletResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		finally {
			httpServletRequestWrapperImpl.pop();
		}
	}

	private void _handleResponseCode() throws IOException, ServletException {
		if (!(_httpServletResponse instanceof HttpServletResponseWrapper)) {
			throw new IllegalStateException("Response is not a wrapper");
		}

		HttpServletResponseWrapperImpl httpServletResponseWrapperImpl =
			HttpServletResponseWrapperImpl.findHttpRuntimeResponse(
				_httpServletResponse);

		if (httpServletResponseWrapperImpl == null) {
			throw new IllegalStateException("Can not locate response");
		}

		int status = httpServletResponseWrapperImpl.getInternalStatus();

		if ((status < HttpServletResponse.SC_BAD_REQUEST) || (status == -1)) {
			return;
		}

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)httpServletResponseWrapperImpl.getResponse();

		if (httpServletResponse.isCommitted()) {
			return;
		}

		ContextController contextController =
			_dispatchTargets.getContextController();

		DispatchTargets errorDispatchTargets =
			contextController.getDispatchTargets(
				String.valueOf(status), null, null, null, null, null,
				Match.EXACT);

		if (errorDispatchTargets == null) {
			httpServletResponse.sendError(
				status, httpServletResponseWrapperImpl.getMessage());

			return;
		}

		HttpServletRequestWrapperImpl httpServletRequestWrapperImpl =
			HttpServletRequestWrapperImpl.findHttpRuntimeRequest(
				_httpServletRequest);

		try {
			errorDispatchTargets.setDispatcherType(DispatcherType.ERROR);

			httpServletRequestWrapperImpl.push(errorDispatchTargets);

			HttpServletRequest httpServletRequest =
				new HttpServletRequestWrapper(_httpServletRequest) {

					@Override
					public Object getAttribute(String attributeName) {
						if (getDispatcherType() == DispatcherType.ERROR) {
							if (attributeName.equals(
									RequestDispatcher.ERROR_MESSAGE)) {

								return httpServletResponseWrapperImpl.
									getMessage();
							}
							else if (attributeName.equals(
										RequestDispatcher.ERROR_REQUEST_URI)) {

								return _httpServletRequest.getRequestURI();
							}
							else if (attributeName.equals(
										RequestDispatcher.ERROR_SERVLET_NAME)) {

								return _dispatchTargets.getServletRegistration(
								).getName();
							}
							else if (attributeName.equals(
										RequestDispatcher.ERROR_STATUS_CODE)) {

								return status;
							}
						}

						return super.getAttribute(attributeName);
					}

					@Override
					public DispatcherType getDispatcherType() {
						return DispatcherType.ERROR;
					}

				};

			LiferayResponseStateHandler liferayResponseStateHandler =
				new LiferayResponseStateHandler(
					httpServletRequest,
					new HttpServletResponseWrapperImpl(httpServletResponse),
					errorDispatchTargets);

			httpServletResponse.setStatus(status);

			liferayResponseStateHandler.processRequest();
		}
		finally {
			httpServletRequestWrapperImpl.pop();
		}
	}

	private void _throwException(Exception exception)
		throws IOException, ServletException {

		if (exception instanceof RuntimeException) {
			throw (RuntimeException)exception;
		}
		else if (exception instanceof IOException) {
			throw (IOException)exception;
		}
		else if (exception instanceof ServletException) {
			throw (ServletException)exception;
		}
	}

	private final DispatchTargets _dispatchTargets;
	private Exception _exception;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;

}