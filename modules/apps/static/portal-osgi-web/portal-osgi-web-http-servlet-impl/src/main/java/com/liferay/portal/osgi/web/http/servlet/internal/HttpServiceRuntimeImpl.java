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

package com.liferay.portal.osgi.web.http.servlet.internal;

import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.RuntimeDTO;

/**
 * @author Dante Wang
 */
public class HttpServiceRuntimeImpl implements HttpServiceRuntime {

	public HttpServiceRuntimeImpl(
		HttpServiceRuntimeController httpServiceRuntimeController) {

		_httpServiceRuntimeController = httpServiceRuntimeController;
	}

	@Override
	public RequestInfoDTO calculateRequestInfoDTO(String path) {
		RequestInfoDTO requestInfoDTO = new RequestInfoDTO();

		requestInfoDTO.path = path;

		try {
			_httpServiceRuntimeController.getDispatchTargets(
				path, requestInfoDTO);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return requestInfoDTO;
	}

	@Override
	public RuntimeDTO getRuntimeDTO() {
		RuntimeDTO runtimeDTO = new RuntimeDTO();

		runtimeDTO.serviceDTO = _httpServiceRuntimeController.getServiceDTO();

		runtimeDTO.failedErrorPageDTOs = null;
		runtimeDTO.failedFilterDTOs =
			_httpServiceRuntimeController.getFailedFilterDTOs();
		runtimeDTO.failedListenerDTOs =
			_httpServiceRuntimeController.getFailedListenerDTOs();
		runtimeDTO.failedResourceDTOs =
			_httpServiceRuntimeController.getFailedResourceDTOs();
		runtimeDTO.failedServletContextDTOs =
			_httpServiceRuntimeController.getFailedServletContextDTO();
		runtimeDTO.failedServletDTOs =
			_httpServiceRuntimeController.getFailedServletDTOs();
		runtimeDTO.servletContextDTOs =
			_httpServiceRuntimeController.getServletContextDTOs();

		return runtimeDTO;
	}

	private final HttpServiceRuntimeController _httpServiceRuntimeController;

}