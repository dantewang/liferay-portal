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

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.util.ServiceProxyFactory;

import java.util.concurrent.ExecutorService;

import javax.portlet.PortletAsyncContext;
import javax.portlet.PortletAsyncListener;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * @author Leon Chi
 */
public class PortletAsyncContextImpl implements PortletAsyncContext {

	public PortletAsyncContextImpl(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
	}

	@Override
	public void addListener(PortletAsyncListener portletAsyncListener)
		throws IllegalStateException {
	}

	@Override
	public void addListener(
			PortletAsyncListener portletAsyncListener,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {
	}

	@Override
	public void complete() throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _completed || _dispatched) {
			throw new IllegalStateException();
		}

		_completed = true;
	}

	@Override
	public <T extends PortletAsyncListener> T createPortletAsyncListener(
			Class<T> aClass)
		throws PortletException {

		T portletAsyncListener = null;

		try {
			portletAsyncListener = aClass.newInstance();
		}
		catch (Exception e) {
			throw new PortletException(e);
		}

		return (T)portletAsyncListener;
	}

	@Override
	public void dispatch() throws IllegalStateException {
		dispatch(null);
	}

	@Override
	public void dispatch(String s) throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _completed) {
			throw new IllegalStateException();
		}

		_dispatched = true;
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
		return 0;
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		return false;
	}

	public boolean isDispatched() {
		return _dispatched;
	}

	@Override
	public void setTimeout(long l) {
	}

	@Override
	public void start(Runnable runnable) throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _completed || _dispatched) {
			throw new IllegalStateException();
		}

		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor(
				PortletAsyncContextImpl.class.getName());

		executorService.execute(runnable);
	}

	private static volatile PortalExecutorManager _portalExecutorManager =
		ServiceProxyFactory.newServiceTrackedInstance(
			PortalExecutorManager.class, PortletAsyncContextImpl.class,
			"_portalExecutorManager", true);

	private boolean _completed;
	private boolean _dispatched;
	private final ResourceRequest _resourceRequest;
	private final ResourceResponse _resourceResponse;

}