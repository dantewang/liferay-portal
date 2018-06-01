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

import aQute.bnd.annotation.ProviderType;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletAsyncContext;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.internal.PortletAsyncContextImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletAsyncContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceParameters;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
@ProviderType
public class ResourceRequestImpl
	extends ClientDataRequestImpl implements ResourceRequest {

	@Override
	public String getCacheability() {
		return _cacheablity;
	}

	@Override
	public DispatcherType getDispatcherType() {
		if ((_portletAsyncContext != null) &&
			_portletAsyncContext.isCalledDispatch()) {

			return DispatcherType.ASYNC;
		}

		return DispatcherType.REQUEST;
	}

	@Override
	public String getETag() {
		return null;
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.RESOURCE_PHASE;
	}

	@Override
	public PortletAsyncContext getPortletAsyncContext() {
		if (!isAsyncSupported() || !isAsyncStarted()) {
			if (_portletAsyncContext == null) {
				throw new IllegalStateException();
			}
		}

		return _portletAsyncContext;
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {
		Map<String, String[]> renderParameters = RenderParametersPool.get(
			getOriginalHttpServletRequest(), getPlid(), getPortletName());

		if ((renderParameters == null) || renderParameters.isEmpty()) {
			return Collections.emptyMap();
		}

		Portlet portlet = getPortlet();

		Set<PublicRenderParameter> publicRenderParameters =
			portlet.getPublicRenderParameters();

		if (publicRenderParameters.isEmpty()) {
			return Collections.unmodifiableMap(renderParameters);
		}

		Map<String, String[]> privateRenderParameters = new HashMap<>();

		for (Map.Entry<String, String[]> entry : renderParameters.entrySet()) {
			if (portlet.getPublicRenderParameter(entry.getKey()) != null) {
				continue;
			}

			privateRenderParameters.put(entry.getKey(), entry.getValue());
		}

		if (privateRenderParameters.isEmpty()) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(privateRenderParameters);
	}

	@Override
	public String getResourceID() {
		return _resourceID;
	}

	@Override
	public ResourceParameters getResourceParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAsyncStarted() {
		return _asyncStarted;
	}

	@Override
	public boolean isAsyncSupported() {
		Portlet portlet = getPortlet();

		return portlet.isAsyncSupported();
	}

	public void setAsyncStarted(boolean asyncStarted) {
		_asyncStarted = asyncStarted;
	}

	@Override
	public PortletAsyncContext startPortletAsync()
		throws IllegalStateException {

		ResourceResponse resourceResponse = (ResourceResponse)getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		return startPortletAsync(this, resourceResponse);
	}

	@Override
	public PortletAsyncContext startPortletAsync(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {

		if (!isAsyncSupported()) {
			throw new IllegalStateException();
		}

		_asyncStarted = true;

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)getAttribute(
				PortletServlet.PORTLET_SERVLET_REQUEST);

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)getAttribute(
				PortletServlet.PORTLET_SERVLET_RESPONSE);

		if (_portletAsyncContext == null) {
			httpServletRequest = new AsyncPortletServletRequest(
				httpServletRequest);

			AsyncContext asyncContext = httpServletRequest.startAsync(
				httpServletRequest, httpServletResponse);

			_portletAsyncContext = new PortletAsyncContextImpl(
				resourceRequest, resourceResponse, asyncContext);
		}
		else {
			AsyncContext asyncContext = httpServletRequest.startAsync(
				httpServletRequest, httpServletResponse);

			_portletAsyncContext.reset(asyncContext);
		}

		return _portletAsyncContext;
	}

	@Override
	protected void init(
		HttpServletRequest request, Portlet portlet,
		InvokerPortlet invokerPortlet, PortletContext portletContext,
		WindowState windowState, PortletMode portletMode,
		PortletPreferences preferences, long plid) {

		if (Validator.isNull(windowState.toString())) {
			windowState = WindowState.NORMAL;
		}

		if (Validator.isNull(portletMode.toString())) {
			portletMode = PortletMode.VIEW;
		}

		super.init(
			request, portlet, invokerPortlet, portletContext, windowState,
			portletMode, preferences, plid);

		_cacheablity = ParamUtil.getString(
			request, "p_p_cacheability", ResourceURL.PAGE);

		_resourceID = request.getParameter("p_p_resource_id");

		if (!PortalUtil.isValidResourceId(_resourceID)) {
			_resourceID = StringPool.BLANK;
		}
	}

	private boolean _asyncStarted;
	private String _cacheablity;
	private LiferayPortletAsyncContext _portletAsyncContext;
	private String _resourceID;

}