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

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.util.DTOUtil;

import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.dto.FailedFilterDTO;
import org.osgi.service.http.runtime.dto.FailedListenerDTO;
import org.osgi.service.http.runtime.dto.FailedResourceDTO;
import org.osgi.service.http.runtime.dto.FailedServletContextDTO;
import org.osgi.service.http.runtime.dto.FailedServletDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.RuntimeDTO;
import org.osgi.service.http.runtime.dto.ServletContextDTO;

/**
 * @author Dante Wang
 */
public class HttpServiceRuntimeImpl implements HttpServiceRuntime {

	public HttpServiceRuntimeImpl(
		BundleContext bundleContext,
		HttpServiceRuntimeController httpServiceRuntimeController) {

		_bundleContext = bundleContext;
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

		runtimeDTO.serviceDTO = _getServiceDTO();

		runtimeDTO.failedErrorPageDTOs = null;
		runtimeDTO.failedFilterDTOs = _httpServiceRuntimeController.getDTOs(
			FailedFilterDTO.class, dto -> DTOUtil.clone((FailedFilterDTO)dto));
		runtimeDTO.failedListenerDTOs = _httpServiceRuntimeController.getDTOs(
			FailedListenerDTO.class,
			dto -> DTOUtil.clone((FailedListenerDTO)dto));
		runtimeDTO.failedResourceDTOs = _httpServiceRuntimeController.getDTOs(
			FailedResourceDTO.class,
			dto -> DTOUtil.clone((FailedResourceDTO)dto));
		runtimeDTO.failedServletContextDTOs =
			_httpServiceRuntimeController.getDTOs(
				FailedServletContextDTO.class,
				dto -> DTOUtil.clone((FailedServletContextDTO)dto));
		runtimeDTO.failedServletDTOs = _httpServiceRuntimeController.getDTOs(
			FailedServletDTO.class,
			dto -> DTOUtil.clone((FailedServletDTO)dto));
		runtimeDTO.servletContextDTOs = TransformUtil.transformToArray(
			_httpServiceRuntimeController.getContextControllers(),
			ContextController::getServletContextDTO, ServletContextDTO.class);

		return runtimeDTO;
	}

	private ServiceReferenceDTO _getServiceDTO() {
		Bundle bundle = _bundleContext.getBundle();

		for (ServiceReferenceDTO serviceReferenceDTO :
				bundle.adapt(ServiceReferenceDTO[].class)) {

			for (String type :
					(String[])serviceReferenceDTO.properties.get(
						Constants.OBJECTCLASS)) {

				if (Objects.equals(HttpServiceRuntime.class.getName(), type)) {
					return serviceReferenceDTO;
				}
			}
		}

		return null;
	}

	private final BundleContext _bundleContext;
	private final HttpServiceRuntimeController _httpServiceRuntimeController;

}