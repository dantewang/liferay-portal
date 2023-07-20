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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.DispatchTargets;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ProxyContext;
import com.liferay.portal.osgi.web.http.servlet.internal.error.HttpWhiteboardFailureException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.IllegalContextNameException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.IllegalContextPathException;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Const;
import com.liferay.portal.osgi.web.http.servlet.internal.util.Path;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServiceProperties;
import com.liferay.portal.osgi.web.http.servlet.internal.util.StringPlus;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.dto.DTO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FailedServletContextDTO;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.runtime.dto.RequestInfoDTO;
import org.osgi.service.http.runtime.dto.ResourceDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Dante Wang
 */
public class HttpServiceRuntimeController {

	public HttpServiceRuntimeController(
		BundleContext trackingBundleContext,
		BundleContext consumingBundleContext,
		ServletContext parentServletContext,
		Map<String, Object> attributesMap) {

		_trackingBundleContext = trackingBundleContext;
		_consumingBundleContext = consumingBundleContext;
		_parentServletContext = parentServletContext;
		_attributesMap = attributesMap;

		_contextServiceTracker = new ServiceTracker<>(
			trackingBundleContext, ServletContextHelper.class,
			new ServletContextHelperServiceTrackerCustomizer());

		_contextServiceTracker.open();
	}

	public void destroy() {
		_contextServiceTracker.close();

		_controllersMap.clear();
		_dtosMap.clear();
		_registeredObjects.clear();

		_attributesMap = null;
		_trackingBundleContext = null;
		_consumingBundleContext = null;
		_parentServletContext = null;
		_registeredObjects = null;
		_contextServiceTracker = null;
	}

	public boolean doDispatch(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path)
		throws IOException, ServletException {

		DispatchTargets dispatchTargets = getDispatchTargets(path, null);

		if (dispatchTargets == null) {
			return false;
		}

		return dispatchTargets.doDispatch(
			httpServletRequest, httpServletResponse, path,
			httpServletRequest.getDispatcherType());
	}

	public ContextController getContextController(
		ServiceReference<ServletContextHelper> serviceReference) {

		return _controllersMap.get(serviceReference);
	}

	public Collection<ContextController> getContextControllers() {
		return Collections.unmodifiableCollection(_controllersMap.values());
	}

	public DispatchTargets getDispatchTargets(
		String pathString, RequestInfoDTO requestInfoDTO) {

		Path path = new Path(pathString);

		String queryString = path.getQueryString();
		String requestURI = path.getRequestURI();

		// perfect match

		DispatchTargets dispatchTargets = _getDispatchTargets(
			requestURI, null, queryString, Match.EXACT, requestInfoDTO);

		if (dispatchTargets == null) {

			// extension match

			dispatchTargets = _getDispatchTargets(
				requestURI, path.getExtension(), queryString, Match.EXTENSION,
				requestInfoDTO);
		}

		if (dispatchTargets == null) {

			// regex match

			dispatchTargets = _getDispatchTargets(
				requestURI, null, queryString, Match.REGEX, requestInfoDTO);
		}

		if (dispatchTargets == null) {

			// handle '/' aliases

			dispatchTargets = _getDispatchTargets(
				requestURI, null, queryString, Match.DEFAULT_SERVLET,
				requestInfoDTO);
		}

		return dispatchTargets;
	}

	public <T> T[] getDTOs(Class<T> clazz, Function<DTO, T> function) {
		Map<ServiceReference<?>, DTO> map = _dtosMap.get(clazz);

		return TransformUtil.transformToArray(
			map.values(),
			dto -> {
				if (function == null) {
					return clazz.cast(dto);
				}

				return function.apply(dto);
			},
			clazz);
	}

	public List<String> getHttpServiceEndpoints() {
		return Arrays.asList(
			StringPlus.from(
				_attributesMap.get(
					HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT)));
	}

	public ServletContext getParentServletContext() {
		return _parentServletContext;
	}

	public Set<Object> getRegisteredObjects() {
		return _registeredObjects;
	}

	public boolean matches(ServiceReference<?> serviceReference) {
		String target = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET);

		if (target == null) {
			return true;
		}

		Filter targetFilter;

		try {
			targetFilter = FrameworkUtil.createFilter(target);
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new IllegalArgumentException(invalidSyntaxException);
		}

		if (targetFilter.matches(_attributesMap)) {
			return true;
		}

		return false;
	}

	public void recordDTO(ServiceReference<?> serviceReference, DTO dto) {
		Map<ServiceReference<?>, DTO> dtosMap = _dtosMap.computeIfAbsent(
			dto.getClass(), clazz -> new ConcurrentHashMap<>());

		dtosMap.putIfAbsent(serviceReference, dto);
	}

	public void removeDTO(
		Class<?> clazz, ServiceReference<?> serviceReference) {

		Map<ServiceReference<?>, DTO> map = _dtosMap.get(clazz);

		if (map != null) {
			map.remove(serviceReference);
		}
	}

	private Collection<ContextController> _getContextControllers(
		String requestURI) {

		int pos = requestURI.lastIndexOf('/');

		while (true) {
			List<ContextController> contextControllers = new ArrayList<>();

			for (ContextController contextController :
					_controllersMap.values()) {

				if (Objects.equals(
						contextController.getContextPath(), requestURI)) {

					contextControllers.add(contextController);
				}
			}

			if (!contextControllers.isEmpty()) {
				return contextControllers;
			}

			if (pos > -1) {
				requestURI = requestURI.substring(0, pos);

				pos = requestURI.lastIndexOf('/');

				continue;
			}

			break;
		}

		return null;
	}

	private DispatchTargets _getDispatchTargets(
		String requestURI, String extension, String queryString, Match match,
		RequestInfoDTO requestInfoDTO) {

		Collection<ContextController> contextControllers =
			_getContextControllers(requestURI);

		if ((contextControllers == null) || contextControllers.isEmpty()) {
			return null;
		}

		Iterator<ContextController> iterator = contextControllers.iterator();

		ContextController firstContextController = iterator.next();

		String contextPath = firstContextController.getContextPath();

		requestURI = requestURI.substring(contextPath.length());

		int pos = requestURI.lastIndexOf('/');

		String servletPath = requestURI;

		String pathInfo = null;

		if (match == Match.DEFAULT_SERVLET) {
			pathInfo = servletPath;
			servletPath = Const.SLASH;
		}

		while (true) {
			for (ContextController contextController : contextControllers) {
				DispatchTargets dispatchTargets =
					contextController.getDispatchTargets(
						null, requestURI, servletPath, pathInfo, extension,
						queryString, match, requestInfoDTO);

				if (dispatchTargets != null) {
					return dispatchTargets;
				}
			}

			if (match == Match.EXACT) {
				break;
			}

			if (pos > -1) {
				servletPath = requestURI.substring(0, pos);

				pathInfo = requestURI.substring(pos);

				pos = servletPath.lastIndexOf('/');

				continue;
			}

			break;
		}

		return null;
	}

	private void _recordFailedServletContextDTO(
		ServiceReference<ServletContextHelper> serviceReference,
		String contextName, String contextPath, int failureReason) {

		FailedServletContextDTO failedServletContextDTO =
			new FailedServletContextDTO();

		failedServletContextDTO.attributes = Collections.emptyMap();
		failedServletContextDTO.contextPath = contextPath;
		failedServletContextDTO.errorPageDTOs = new ErrorPageDTO[0];
		failedServletContextDTO.failureReason = failureReason;
		failedServletContextDTO.filterDTOs = new FilterDTO[0];
		failedServletContextDTO.initParams = ServiceProperties.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX);
		failedServletContextDTO.listenerDTOs = new ListenerDTO[0];
		failedServletContextDTO.name = contextName;
		failedServletContextDTO.resourceDTOs = new ResourceDTO[0];
		failedServletContextDTO.serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		failedServletContextDTO.servletDTOs = new ServletDTO[0];

		recordDTO(serviceReference, failedServletContextDTO);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HttpServiceRuntimeController.class.getName());

	private Map<String, Object> _attributesMap;
	private BundleContext _consumingBundleContext;
	private ServiceTracker
		<ServletContextHelper, AtomicReference<ContextController>>
			_contextServiceTracker;
	private final ConcurrentMap
		<ServiceReference<ServletContextHelper>, ContextController>
			_controllersMap = new ConcurrentHashMap<>();
	private final Map<Class<?>, Map<ServiceReference<?>, DTO>> _dtosMap =
		new ConcurrentHashMap<>();
	private ServletContext _parentServletContext;
	private Set<Object> _registeredObjects = Collections.newSetFromMap(
		new ConcurrentHashMap<>());
	private BundleContext _trackingBundleContext;

	private class ServletContextHelperServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ServletContextHelper, AtomicReference<ContextController>> {

		@Override
		public synchronized AtomicReference<ContextController> addingService(
			ServiceReference<ServletContextHelper> serviceReference) {

			AtomicReference<ContextController> result = new AtomicReference<>();

			if (!matches(serviceReference)) {
				return result;
			}

			String contextName = (String)serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME);
			String contextPath = (String)serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH);

			try {
				if (contextName == null) {
					throw new IllegalContextNameException(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +
							" is null. Ignoring!",
						DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
				}

				if (contextPath == null) {
					throw new IllegalContextPathException(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH +
							" is null. Ignoring!",
						DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
				}

				ContextController contextController = new ContextController(
					_trackingBundleContext, _consumingBundleContext,
					serviceReference,
					new ProxyContext(contextName, _parentServletContext),
					HttpServiceRuntimeController.this, contextName,
					contextPath);

				_controllersMap.put(serviceReference, contextController);

				result.set(contextController);
			}
			catch (HttpWhiteboardFailureException
						httpWhiteboardFailureException) {

				_log.error(httpWhiteboardFailureException);

				_recordFailedServletContextDTO(
					serviceReference, contextName, contextPath,
					httpWhiteboardFailureException.getFailureReason());
			}
			catch (Exception exception) {
				_log.error(exception);

				_recordFailedServletContextDTO(
					serviceReference, contextName, contextPath,
					DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT);
			}

			return result;
		}

		@Override
		public synchronized void modifiedService(
			ServiceReference<ServletContextHelper> serviceReference,
			AtomicReference<ContextController> contextController) {

			removedService(serviceReference, contextController);

			AtomicReference<ContextController> added = addingService(
				serviceReference);

			contextController.set(added.get());
		}

		@Override
		public synchronized void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			AtomicReference<ContextController> contextControllerRef) {

			ContextController contextController = contextControllerRef.get();

			if (contextController != null) {
				contextController.destroy();
			}

			_controllersMap.remove(serviceReference);
			removeDTO(FailedServletContextDTO.class, serviceReference);
			_trackingBundleContext.ungetService(serviceReference);
		}

	}

}