/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.registration.EndpointRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;
import org.eclipse.equinox.http.servlet.internal.servlet.HttpServletRequestWrapperImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.HttpServletResponseWrapperImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.ResponseStateHandler;
import org.eclipse.equinox.http.servlet.internal.util.Params;

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
		_servletName = servletName;
		_requestURI = requestURI;
		_servletPath = GetterUtil.getString(servletPath);
		_pathInfo = pathInfo;
		_queryString = queryString;

		_matchingFilterRegistrations = filterRegistrations;
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

	@Override
	public void addRequestParameters(HttpServletRequest httpServletRequest) {
		if (_queryString == null) {
			_parameterMap = httpServletRequest.getParameterMap();
			_queryString = httpServletRequest.getQueryString();

			return;
		}

		Map<String, String[]> parameterMap = _parseParameterMap(_queryString);

		Map<String, String[]> requestParameterMap =
			httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry :
				requestParameterMap.entrySet()) {

			String[] values = parameterMap.get(entry.getKey());

			values = Params.append(values, entry.getValue());

			parameterMap.put(entry.getKey(), values);
		}

		_parameterMap = Collections.unmodifiableMap(parameterMap);
	}

	@Override
	public boolean doDispatch(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path,
			DispatcherType dispatcherType)
		throws IOException, ServletException {

		setDispatcherType(dispatcherType);

		RequestAttributeSetter requestAttributeSetter =
			new RequestAttributeSetter(httpServletRequest);

		if (_dispatcherType == DispatcherType.INCLUDE) {
			requestAttributeSetter.setAttribute(
				RequestDispatcher.INCLUDE_CONTEXT_PATH,
				_liferayContextController.getFullContextPath());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.INCLUDE_PATH_INFO, getPathInfo());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.INCLUDE_QUERY_STRING, getQueryString());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.INCLUDE_REQUEST_URI, getRequestURI());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.INCLUDE_SERVLET_PATH, getServletPath());
		}
		else if (_dispatcherType == DispatcherType.FORWARD) {
			if (!httpServletRequest.isAsyncStarted() &&
				!httpServletResponse.isCommitted()) {

				httpServletResponse.resetBuffer();
			}

			requestAttributeSetter.setAttribute(
				RequestDispatcher.FORWARD_CONTEXT_PATH,
				httpServletRequest.getContextPath());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.FORWARD_PATH_INFO,
				httpServletRequest.getPathInfo());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.FORWARD_QUERY_STRING,
				httpServletRequest.getQueryString());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.FORWARD_REQUEST_URI,
				httpServletRequest.getRequestURI());
			requestAttributeSetter.setAttribute(
				RequestDispatcher.FORWARD_SERVLET_PATH,
				httpServletRequest.getServletPath());
		}

		HttpServletRequestWrapperImpl httpServletRequestWrapperImpl =
			HttpServletRequestWrapperImpl.findHttpRuntimeRequest(
				httpServletRequest);

		if (httpServletRequestWrapperImpl == null) {
			httpServletRequestWrapperImpl = new HttpServletRequestWrapperImpl(
				httpServletRequest);

			httpServletRequest = httpServletRequestWrapperImpl;

			httpServletResponse = new HttpServletResponseWrapperImpl(
				httpServletResponse);
		}

		try {
			httpServletRequestWrapperImpl.push(this);

			ResponseStateHandler responseStateHandler =
				new ResponseStateHandler(
					httpServletRequest, httpServletResponse, this);

			responseStateHandler.processRequest();

			if ((_dispatcherType == DispatcherType.FORWARD) &&
				!httpServletResponse.isCommitted() &&
				!httpServletRequest.isAsyncStarted()) {

				try {
					httpServletResponse.flushBuffer();

					Writer writer = httpServletResponse.getWriter();

					writer.close();
				}
				catch (IllegalStateException illegalStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(illegalStateException);
					}

					try {
						ServletOutputStream servletOutputStream =
							httpServletResponse.getOutputStream();

						servletOutputStream.close();
					}
					catch (IllegalStateException | IOException exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}
				}
			}

			return true;
		}
		finally {
			httpServletRequestWrapperImpl.pop();

			requestAttributeSetter.close();
		}
	}

	@Override
	public ContextController getContextController() {
		return _liferayContextController;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return _dispatcherType;
	}

	@Override
	public List<FilterRegistration> getMatchingFilterRegistrations() {
		return _matchingFilterRegistrations;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public String getPathInfo() {
		return _pathInfo;
	}

	@Override
	public String getQueryString() {
		return _queryString;
	}

	@Override
	public String getRequestURI() {
		if (_requestURI == null) {
			return null;
		}

		return _liferayContextController.getFullContextPath() + _requestURI;
	}

	@Override
	public String getServletName() {
		return _servletName;
	}

	@Override
	public String getServletPath() {
		return _servletPath;
	}

	@Override
	public EndpointRegistration<?> getServletRegistration() {
		return _endpointRegistration;
	}

	@Override
	public Map<String, Object> getSpecialOverides() {
		return _specialOverrides;
	}

	@Override
	public void setDispatcherType(DispatcherType dispatcherType) {
		_dispatcherType = dispatcherType;
	}

	private Map<String, String[]> _parseParameterMap(String queryString) {
		if (Validator.isBlank(queryString)) {
			return new HashMap<>();
		}

		Map<String, String[]> parameterMap = new LinkedHashMap<>();

		String[] parameters = StringUtil.split(
			queryString, StringPool.AMPERSAND);

		for (String parameter : parameters) {
			int index = parameter.indexOf('=');

			String name = parameter;

			if (index > 0) {
				name = URLCodec.decodeURL(parameter.substring(0, index));
			}

			String[] values = parameterMap.get(name);

			if (values == null) {
				values = new String[0];
			}

			String value = null;

			if ((index > 0) && (parameter.length() > (index + 1))) {
				value = URLCodec.decodeURL(parameter.substring(index + 1));
			}

			values = Params.append(values, value);

			parameterMap.put(name, values);
		}

		return parameterMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayDispatchTargets.class.getName());

	private DispatcherType _dispatcherType;
	private final EndpointRegistration<?> _endpointRegistration;
	private final LiferayContextController _liferayContextController;
	private final List<FilterRegistration> _matchingFilterRegistrations;
	private Map<String, String[]> _parameterMap;
	private final String _pathInfo;
	private String _queryString;
	private final String _requestURI;
	private final String _servletName;
	private final String _servletPath;
	private final Map<String, Object> _specialOverrides =
		new ConcurrentHashMap<>();

	private static class RequestAttributeSetter implements Closeable {

		public RequestAttributeSetter(ServletRequest servletRequest) {
			_servletRequest = servletRequest;
		}

		@Override
		public void close() {
			for (Map.Entry<String, Object> oldValue :
					_oldValuesMap.entrySet()) {

				if (oldValue.getValue() == null) {
					_servletRequest.removeAttribute(oldValue.getKey());
				}
				else {
					_servletRequest.setAttribute(
						oldValue.getKey(), oldValue.getValue());
				}
			}
		}

		public void setAttribute(String name, Object value) {
			_oldValuesMap.put(name, _servletRequest.getAttribute(name));

			_servletRequest.setAttribute(name, value);
		}

		private final Map<String, Object> _oldValuesMap = new HashMap<>();
		private final ServletRequest _servletRequest;

	}

}