/*******************************************************************************
 * Copyright (c) 2014 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package com.liferay.portal.osgi.web.http.servlet.internal.customizer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeController;
import com.liferay.portal.osgi.web.http.servlet.internal.constants.HttpServiceConstants;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.error.HttpWhiteboardFailureException;
import com.liferay.portal.osgi.web.http.servlet.internal.error.RegisteredFilterException;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.FilterConfigImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.util.PatternUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServiceProperties;
import com.liferay.portal.osgi.web.http.servlet.internal.util.StringPlus;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedFilterDTO;
import org.osgi.service.http.runtime.dto.FilterDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class FilterServiceTrackerCustomizer
	extends BaseServiceTrackerCustomizer
		<Filter, AtomicReference<FilterRegistration>> {

	public FilterServiceTrackerCustomizer(
		BundleContext bundleContext, ContextController contextController,
		HttpServiceRuntimeController httpServiceRuntimeController) {

		super(bundleContext, contextController, httpServiceRuntimeController);
	}

	@Override
	public AtomicReference<FilterRegistration> addingService(
		ServiceReference<Filter> serviceReference) {

		Object filterPattern = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN);

		Object filterRegex = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX);

		Object filterServlet = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET);

		if ((filterPattern == null) && (filterRegex == null) &&
			(filterServlet == null)) {

			return null;
		}

		if (!contextController.matches(serviceReference) ||
			!httpServiceRuntimeController.matches(serviceReference)) {

			return null;
		}

		AtomicReference<FilterRegistration> result = new AtomicReference<>();

		try {
			result.set(_addFilterRegistration(serviceReference));
		}
		catch (HttpWhiteboardFailureException httpWhiteboardFailureException) {
			_log.error(httpWhiteboardFailureException);

			_recordFailedFilterDTO(
				serviceReference,
				httpWhiteboardFailureException.getFailureReason());
		}
		catch (Exception exception) {
			_log.error(exception);

			_recordFailedFilterDTO(
				serviceReference,
				DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT);
		}

		return result;
	}

	@Override
	public void modifiedService(
		ServiceReference<Filter> serviceReference,
		AtomicReference<FilterRegistration> filterReference) {

		removedService(serviceReference, filterReference);

		AtomicReference<FilterRegistration> added = addingService(
			serviceReference);

		filterReference.set(added.get());
	}

	@Override
	public void removedService(
		ServiceReference<Filter> serviceReference,
		AtomicReference<FilterRegistration> filterReference) {

		FilterRegistration registration = filterReference.get();

		if (registration != null) {
			registration.destroy();
		}

		httpServiceRuntimeController.removeDTO(
			FailedFilterDTO.class, serviceReference);
	}

	private FilterRegistration _addFilterRegistration(
			ContextController.ServiceHolder<Filter> serviceHolder,
			ServiceReference<Filter> serviceReference)
		throws ServletException {

		String[] patterns = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN));

		String[] regexes = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX));

		String[] servletNames = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET));

		if ((patterns.length == 0) && (regexes.length == 0) &&
			(servletNames.length == 0)) {

			throw new IllegalArgumentException(
				"Patterns, regex or servletNames must contain a value");
		}

		for (String pattern : patterns) {
			PatternUtil.checkPattern(pattern);
		}

		Filter filter = serviceHolder.get();

		if (filter == null) {
			throw new IllegalArgumentException("Filter cannot be null");
		}

		String name = ServiceProperties.parseName(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME),
			serviceHolder.get());

		if (name == null) {
			Class<?> clazz = filter.getClass();

			name = clazz.getName();
		}

		Set<FilterRegistration> filterRegistrations =
			contextController.getFilterRegistrations();

		for (FilterRegistration filterRegistration : filterRegistrations) {
			if (Objects.equals(filter, filterRegistration.getT())) {
				throw new RegisteredFilterException(filter);
			}
		}

		Long serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);

		ClassLoader legacyTCCL = (ClassLoader)serviceReference.getProperty(
			HttpServiceConstants.CONTEXT_CLASSLOADER);

		if (legacyTCCL != null) {
			serviceId = -serviceId;
		}

		Map<String, String> filterInitParamsMap =
			ServiceProperties.parseInitParams(
				serviceReference,
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX);

		FilterDTO filterDTO = new FilterDTO();

		filterDTO.asyncSupported = GetterUtil.getBoolean(
			serviceReference.getProperty(
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED));
		filterDTO.dispatcher = sort(
			_checkDispatcher(
				StringPlus.from(
					serviceReference.getProperty(
						HttpWhiteboardConstants.
							HTTP_WHITEBOARD_FILTER_DISPATCHER))));
		filterDTO.initParams = filterInitParamsMap;
		filterDTO.name = name;
		filterDTO.patterns = sort(patterns);
		filterDTO.regexs = regexes;
		filterDTO.serviceId = serviceId;
		filterDTO.servletContextId = contextController.getServiceId();
		filterDTO.servletNames = sort(servletNames);

		Integer filterPriority = (Integer)serviceReference.getProperty(
			Constants.SERVICE_RANKING);

		if (filterPriority == null) {
			filterPriority = 0;
		}

		FilterRegistration newFilterRegistration = new FilterRegistration(
			serviceHolder, filterDTO, filterPriority, contextController,
			legacyTCCL);

		newFilterRegistration.init(
			new FilterConfigImpl(
				name, filterInitParamsMap,
				contextController.createServletContextAdaptor(
					serviceHolder.getBundle(),
					contextController.getServletContextHelper(
						serviceHolder.getBundle()))));

		filterRegistrations.add(newFilterRegistration);

		return newFilterRegistration;
	}

	private FilterRegistration _addFilterRegistration(
			ServiceReference<Filter> serviceReference)
		throws ServletException {

		contextController.checkShutdown();

		ContextController.ServiceHolder<Filter> filterHolder =
			new ContextController.ServiceHolder<>(
				bundleContext.getServiceObjects(serviceReference));

		Filter filter = filterHolder.get();

		FilterRegistration registration = null;
		boolean addedRegisteredObject = false;

		Set<Object> registeredObjects =
			httpServiceRuntimeController.getRegisteredObjects();

		try {
			if (filter == null) {
				throw new IllegalArgumentException("Filter cannot be null");
			}

			addedRegisteredObject = registeredObjects.add(filter);

			if (addedRegisteredObject) {
				registration = _addFilterRegistration(
					filterHolder, serviceReference);
			}
		}
		finally {
			if (registration == null) {
				filterHolder.release();

				if (addedRegisteredObject) {
					registeredObjects.remove(filter);
				}
			}
		}

		return registration;
	}

	private String[] _checkDispatcher(String[] dispatchers) {
		if ((dispatchers == null) || (dispatchers.length == 0)) {
			return _DISPATCHER;
		}

		for (String dispatcher : dispatchers) {
			try {
				DispatcherType.valueOf(dispatcher);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				throw new IllegalArgumentException(
					"Invalid dispatcher '" + dispatcher + "'",
					illegalArgumentException);
			}
		}

		Arrays.sort(dispatchers);

		return dispatchers;
	}

	private void _recordFailedFilterDTO(
		ServiceReference<Filter> serviceReference, int failureReason) {

		FailedFilterDTO failedFilterDTO = new FailedFilterDTO();

		failedFilterDTO.asyncSupported = GetterUtil.getBoolean(
			serviceReference.getProperty(
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED));
		failedFilterDTO.dispatcher = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER));
		failedFilterDTO.failureReason = failureReason;
		failedFilterDTO.initParams = ServiceProperties.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX);
		failedFilterDTO.name = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME);

		failedFilterDTO.patterns = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN));

		failedFilterDTO.regexs = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX));

		failedFilterDTO.serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);

		failedFilterDTO.servletContextId = contextController.getServiceId();

		failedFilterDTO.servletNames = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET));

		httpServiceRuntimeController.recordDTO(
			serviceReference, failedFilterDTO);
	}

	private static final String[] _DISPATCHER = {
		DispatcherType.REQUEST.toString()
	};

	private static final Log _log = LogFactoryUtil.getLog(
		FilterServiceTrackerCustomizer.class.getName());

}