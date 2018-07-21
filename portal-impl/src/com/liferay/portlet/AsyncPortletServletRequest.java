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

package com.liferay.portlet;

import com.liferay.portal.kernel.servlet.DynamicServletRequest;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Dante Wang
 */
public class AsyncPortletServletRequest extends HttpServletRequestWrapper {

	public static AsyncPortletServletRequest getAsyncPortletServletRequest(
		HttpServletRequest httpServletRequest) {

		while (httpServletRequest instanceof HttpServletRequestWrapper) {
			if (httpServletRequest instanceof AsyncPortletServletRequest) {
				return (AsyncPortletServletRequest)httpServletRequest;
			}

			httpServletRequest =
				(HttpServletRequest)
					((HttpServletRequestWrapper)
						httpServletRequest).getRequest();
		}

		return null;
	}

	public AsyncPortletServletRequest(HttpServletRequest request) {
		super(request);

		_contextPath = super.getContextPath();
		_queryString = super.getQueryString();
		_requestURI = super.getRequestURI();
		_servletPath = super.getServletPath();
	}

	@Override
	public String getContextPath() {
		return _contextPath;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return DispatcherType.ASYNC;
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
		return _requestURI;
	}

	@Override
	public String getServletPath() {
		return _servletPath;
	}

	public void setContextPath(String contextPath) {
		_contextPath = contextPath;
	}

	public void setPathInfo(String pathInfo) {
		_pathInfo = pathInfo;
	}

	public void setQueryString(String queryString) {
		_queryString = queryString;

		setRequest(
			DynamicServletRequest.addQueryString(
				(HttpServletRequest)getRequest(), queryString, true));
	}

	public void setRequestURI(String requestUri) {
		_requestURI = requestUri;
	}

	public void setServletPath(String servletPath) {
		_servletPath = servletPath;
	}

	private String _contextPath;
	private String _pathInfo;
	private String _queryString;
	private String _requestURI;
	private String _servletPath;

}