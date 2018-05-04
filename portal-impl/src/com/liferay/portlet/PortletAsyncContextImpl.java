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

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.executor.CopyThreadLocalCallable;
import com.liferay.portal.kernel.portlet.LiferayPortletAsyncContext;
import com.liferay.portal.kernel.util.DefaultThreadLocalBinder;
import com.liferay.portal.kernel.util.JavaConstants;

import javax.portlet.PortletAsyncListener;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ResourceRequestWrapper;
import javax.portlet.filter.ResourceResponseWrapper;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leon Chi
 * @author Dante Wang
 */
public class PortletAsyncContextImpl implements LiferayPortletAsyncContext {

	public static void updateDispatchInfo(
		AsyncPortletServletRequest asyncPortletServletRequest,
		ServletContext servletContext, String path) {

		Map<String, ServletRegistration> servletRegistrationMap =
			(Map<String, ServletRegistration>)
				servletContext.getServletRegistrations();

		Collection<ServletRegistration> servletRegistrations =
			servletRegistrationMap.values();

		Stream<ServletRegistration> servletRegistrationStream =
			servletRegistrations.stream();

		Set<String> servletURLPatterns = servletRegistrationStream.flatMap(
			servletRegistration -> servletRegistration.getMappings().stream()
		).collect(Collectors.toSet());

		String contextPath = servletContext.getContextPath();
		String pathInfo = null;
		String queryString = null;
		String requestURI = null;
		String servletPath = null;

		// TODO: what if Liferay is deployed into e.g. /liferay?

		if ((contextPath.length() > 0) && path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		if (path != null) {
			String pathNoQueryString = path;

			int pos = path.indexOf(CharPool.QUESTION);

			if (pos != -1) {
				pathNoQueryString = path.substring(0, pos);
				queryString = path.substring(pos + 1);
			}

			for (String urlPattern : servletURLPatterns) {
				if (urlPattern.endsWith("/*")) {
					int length = urlPattern.length() - 2;

					if ((pathNoQueryString.length() > length) &&
						pathNoQueryString.regionMatches(
							0, urlPattern, 0, length) &&
						(pathNoQueryString.charAt(length) == CharPool.SLASH)) {

						pathInfo = pathNoQueryString.substring(length);
						servletPath = urlPattern.substring(0, length);

						break;
					}
				}
			}

			if (servletPath == null) {
				servletPath = pathNoQueryString;
			}

			if (contextPath.equals(StringPool.SLASH)) {
				requestURI = pathNoQueryString;
			}
			else {
				requestURI = contextPath + pathNoQueryString;
			}
		}

		asyncPortletServletRequest.setContextPath(contextPath);
		asyncPortletServletRequest.setPathInfo(pathInfo);
		asyncPortletServletRequest.setQueryString(queryString);
		asyncPortletServletRequest.setRequestURI(requestURI);
		asyncPortletServletRequest.setServletPath(servletPath);
	}

	public PortletAsyncContextImpl(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		AsyncContext asyncContext) {

		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_asyncContext = asyncContext;

		_portletAsyncListenerAdapter =
			new PortletAsyncListenerAdapter(this);

		_asyncContext.addListener(_portletAsyncListenerAdapter);

		_asyncPortletServletRequest =
			(AsyncPortletServletRequest)_asyncContext.getRequest();
	}

	@Override
	public void addListener(PortletAsyncListener portletAsyncListener)
		throws IllegalStateException {

		addListener(portletAsyncListener, null, null);
	}

	@Override
	public void addListener(
			PortletAsyncListener portletAsyncListener,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {

		if (!_resourceRequest.isAsyncStarted() || _calledComplete || _calledDispatch) {
			throw new IllegalStateException();
		}

		_portletAsyncListenerAdapter.addListener(
			portletAsyncListener, resourceRequest, resourceResponse);
	}

	@Override
	public void complete() throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _calledComplete || _calledDispatch) {
			throw new IllegalStateException();
		}

		_calledComplete = true;

		_asyncContext.complete();
	}

	@Override
	public <T extends PortletAsyncListener> T createPortletAsyncListener(
			Class<T> aClass)
		throws PortletException {

		T portletAsyncListener = null;

		try {
			portletAsyncListener = aClass.newInstance();
		}
		catch (Throwable e) {
			throw new PortletException(e);
		}

		return (T)portletAsyncListener;
	}

	@Override
	public void dispatch() throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _calledComplete || _calledDispatch) {
			throw new IllegalStateException();
		}

		_calledDispatch = true;

		ServletRequest originalRequest = _asyncPortletServletRequest;

		while (originalRequest instanceof ServletRequestWrapper) {
			originalRequest =
				((ServletRequestWrapper)originalRequest).getRequest();
		}

		String path = ((HttpServletRequest)originalRequest).getRequestURI();

		path = path.concat("?").concat(
			((HttpServletRequest)originalRequest).getQueryString());

		ServletContext servletContext = originalRequest.getServletContext();

		updateDispatchInfo(_asyncPortletServletRequest, servletContext, path);

		_asyncContext.dispatch(servletContext, path);
	}

	@Override
	public void dispatch(String path) throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _calledComplete || _calledDispatch) {
			throw new IllegalStateException();
		}

		_calledDispatch = true;

		ServletRequest originalRequest = _asyncPortletServletRequest;

		while (originalRequest instanceof ServletRequestWrapper) {
			originalRequest =
				((ServletRequestWrapper)originalRequest).getRequest();
		}

		ServletContext servletContext = originalRequest.getServletContext();

		String fullPath = _getFullPath(path);

		updateDispatchInfo(
			_asyncPortletServletRequest, servletContext, fullPath);

		_asyncContext.dispatch(servletContext, fullPath);
	}

	@Override
	public ResourceRequest getResourceRequest() throws IllegalStateException {
		return _resourceRequest;
	}

	@Override
	public ResourceResponse getResourceResponse() throws IllegalStateException {
		return _resourceResponse;
	}

	@Override
	public long getTimeout() {
		return _asyncContext.getTimeout();
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		if (_resourceRequest instanceof ResourceRequestWrapper ||
			_resourceResponse instanceof ResourceResponseWrapper) {

			return false;
		}

		return true;
	}

	public void reSet(){
		_calledDispatch = false;
		_calledComplete = false;
		_pendingRunnable = null;
	}

	public boolean isCalledComplete() {
		return _calledComplete;
	}

	public boolean isCalledDispatch() {
		return _calledDispatch;
	}

	@Override
	public void setTimeout(long timeout) {
		_asyncContext.setTimeout(timeout);
	}

	@Override
	public void start(Runnable runnable) throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _calledComplete || _calledDispatch) {
			throw new IllegalStateException();
		}

		_pendingRunnable = new PortletAsyncRunnableWrapper(runnable);
	}

	@Override
	public void doStart() {
		if (_pendingRunnable == null) {
			return;
		}

		_asyncContext.start(_pendingRunnable);

		_pendingRunnable = null;
	}

	private String _getFullPath(String path) {
		return _resourceRequest.getContextPath().concat(path);
	}

	private AsyncPortletServletRequest _asyncPortletServletRequest;
	private final PortletAsyncListenerAdapter _portletAsyncListenerAdapter;
	private AsyncContext _asyncContext;
	private boolean _calledComplete;
	private boolean _calledDispatch;
	private Runnable _pendingRunnable;
	private final ResourceRequest _resourceRequest;
	private final ResourceResponse _resourceResponse;

	private class PortletAsyncRunnableWrapper
		extends CopyThreadLocalCallable<Object> implements Runnable {

		public PortletAsyncRunnableWrapper(Runnable runnable) {
			super(new DefaultThreadLocalBinder(), false, true);

			_runnable = runnable;
		}

		@Override
		public Object doCall() throws Exception {
			_runnable.run();

			return null;
		}

		@Override
		public void run() {
			try {
				call();
			}
			catch (Throwable t) {

				// Tomcat doesn't invoke onError

				try {
					_portletAsyncListenerAdapter.onError(
						new AsyncEvent(_asyncContext, t));
				}
				catch (IOException e) {
				}
			}
		}

		private final Runnable _runnable;

	}

}