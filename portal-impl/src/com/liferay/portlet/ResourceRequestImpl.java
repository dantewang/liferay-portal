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
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.internal.ResourceParametersImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletAsyncContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderParameters;
import javax.portlet.ResourceParameters;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletRequestWrapper;

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
		return _dispatcherType;
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
			throw new IllegalStateException();
		}

		// TODO: portlet3

		return _portletAsyncContext;
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {

		// TODO: portlet3 - This method used to return null but Dante and Tina
		// implemented it for https://issues.liferay.com/browse/LPS-76916. You
		// refactored it to use RenderParameters but it needs to be tested.

		Map<String, String[]> privateRenderParameters = new HashMap<>();
		RenderParameters renderParameters = getRenderParameters();

		Set<String> renderParameterNames = renderParameters.getNames();

		for (String renderParameterName : renderParameterNames) {
			if (!renderParameters.isPublic(renderParameterName)) {
				privateRenderParameters.put(
					renderParameterName,
					renderParameters.getValues(renderParameterName));
			}
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
		return _resourceParameters;
	}

	@Override
	public boolean isAsyncStarted() {

		// TODO: portlet3

		return _asyncStarted;
	}

	@Override
	public boolean isAsyncSupported() {

		// TODO: portlet3

		Portlet portlet = getPortlet();

		return portlet.isAsyncSupported();
	}

	@Override
	public PortletAsyncContext startPortletAsync()
		throws IllegalStateException {

		// TODO: portlet3

		ResourceResponse resourceResponse = (ResourceResponse)getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		return startPortletAsync(this, resourceResponse);
	}

	@Override
	public PortletAsyncContext startPortletAsync(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {

		// TODO: portlet3

		if (!isAsyncSupported() || resourceResponse.isCommitted()) {
			throw new IllegalStateException();
		}

		if (_portletAsyncContext != null) {
			PortletAsyncContextImpl portletAsyncContextImpl =
				(PortletAsyncContextImpl)_portletAsyncContext;

			boolean dispatched = portletAsyncContextImpl.isDispatched();

			if (!dispatched) {
				throw new IllegalStateException();
			}
		}

		_asyncStarted = true;

		_dispatcherType = DispatcherType.ASYNC;

		HttpServletRequest httpServletRequest = getHttpServletRequest();

		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(
				(PortletResponse)getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE));

		HttpServletResponse httpServletResponse =
			liferayPortletResponse.getHttpServletResponse();

		while (httpServletRequest instanceof HttpServletRequestWrapper) {
			httpServletRequest =
				(HttpServletRequest)
					((HttpServletRequestWrapper)
						httpServletRequest).getRequest();
		}

		while (httpServletResponse instanceof HttpServletResponseWrapper) {
			httpServletResponse =
				(HttpServletResponse)
					((HttpServletResponseWrapper)
						httpServletResponse).getResponse();
		}

		_portletAsyncContext = new PortletAsyncContextImpl(
			resourceRequest, resourceResponse,
			httpServletRequest.startAsync(
				httpServletRequest, httpServletResponse));

		PortletAsyncContextImpl portletAsyncContextImpl =
			(PortletAsyncContextImpl)_portletAsyncContext;

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

		Map<String, String[]> resourceParameterMap = new LinkedHashMap<>();
		RenderParameters renderParameters = getRenderParameters();

		Set<String> renderParameterNames = renderParameters.getNames();

		Map<String, String[]> parameterMap = getParameterMap();
		String portletNamespace = PortalUtil.getPortletNamespace(
			getPortletName());
		Map<String, String[]> servletRequestParameterMap =
			request.getParameterMap();

		for (Map.Entry<String, String[]> mapEntry : parameterMap.entrySet()) {
			String name = mapEntry.getKey();

			// If the parameter name is not a public/private render parameter,
			// then regard it as a resource parameter. Otherwise, if the
			// parameter name is prefixed with the portlet namespace in the
			// original request, then regard it as a resource parameter (even if
			// it has the/ same name as a public render parameter). See: TCK
			// V3PortletParametersTests_SPEC11_4_getNames

			if (!renderParameterNames.contains(name)) {
				resourceParameterMap.put(name, mapEntry.getValue());
			}
			else {
				String namespacedParameter = portletNamespace + name;

				if (renderParameterNames.contains(name) &&
					servletRequestParameterMap.containsKey(
						namespacedParameter)) {

					resourceParameterMap.put(
						name,
						servletRequestParameterMap.get(namespacedParameter));
				}
			}
		}

		_resourceParameters = new ResourceParametersImpl(
			resourceParameterMap, portletNamespace);
	}

	private boolean _asyncStarted;
	private String _cacheablity;
	private DispatcherType _dispatcherType = DispatcherType.REQUEST;
	private PortletAsyncContext _portletAsyncContext;
	private String _resourceID;
	private ResourceParameters _resourceParameters;

}