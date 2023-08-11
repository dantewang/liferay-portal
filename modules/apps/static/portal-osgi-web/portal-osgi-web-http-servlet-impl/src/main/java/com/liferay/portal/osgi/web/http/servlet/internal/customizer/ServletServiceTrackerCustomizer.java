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

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeController;
import com.liferay.portal.osgi.web.http.servlet.internal.constants.HttpServiceConstants;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.error.HttpWhiteboardFailureException;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServletRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ServletConfigImpl;
import com.liferay.portal.osgi.web.http.servlet.internal.util.PatternUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServiceProperties;
import com.liferay.portal.osgi.web.http.servlet.internal.util.StringPlus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.ErrorPageDTO;
import org.osgi.service.http.runtime.dto.FailedServletDTO;
import org.osgi.service.http.runtime.dto.ServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class ServletServiceTrackerCustomizer
	extends BaseServiceTrackerCustomizer
		<Servlet, AtomicReference<ServletRegistration>> {

	public ServletServiceTrackerCustomizer(
		BundleContext bundleContext, ContextController contextController,
		HttpServiceRuntimeController httpServiceRuntimeController) {

		super(bundleContext, contextController, httpServiceRuntimeController);
	}

	@Override
	public AtomicReference<ServletRegistration> addingService(
		ServiceReference<Servlet> serviceReference) {

		Object servletErrorPage = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE);

		Object servletName = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);

		Object servletPattern = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN);

		if ((servletErrorPage == null) && (servletName == null) &&
			(servletPattern == null)) {

			return null;
		}

		if (!contextController.matches(serviceReference) ||
			!httpServiceRuntimeController.matches(serviceReference)) {

			return null;
		}

		AtomicReference<ServletRegistration> result = new AtomicReference<>();

		try {
			result.set(_addServletRegistration(serviceReference));
		}
		catch (HttpWhiteboardFailureException httpWhiteboardFailureException) {
			_log.error(httpWhiteboardFailureException);

			_recordFailedServletDTO(
				serviceReference,
				httpWhiteboardFailureException.getFailureReason());
		}
		catch (Exception exception) {
			_log.error(exception);

			_recordFailedServletDTO(
				serviceReference,
				DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT);
		}

		return result;
	}

	@Override
	public void modifiedService(
		ServiceReference<Servlet> serviceReference,
		AtomicReference<ServletRegistration> servletReference) {

		removedService(serviceReference, servletReference);

		AtomicReference<ServletRegistration> added = addingService(
			serviceReference);

		servletReference.set(added.get());
	}

	@Override
	public void removedService(
		ServiceReference<Servlet> serviceReference,
		AtomicReference<ServletRegistration> servletReference) {

		ServletRegistration registration = servletReference.get();

		if (registration != null) {
			registration.destroy();
		}

		httpServiceRuntimeController.removeDTO(
			FailedServletDTO.class, serviceReference);
	}

	private ServletRegistration _addServletRegistration(
			ContextController.ServiceHolder<Servlet> serviceHolder,
			ServiceReference<Servlet> serviceReference)
		throws ServletException {

		String[] errorPages = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE));

		String[] patterns = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN));

		String servletNameFromProperties = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);

		if ((patterns.length == 0) && (errorPages.length == 0) &&
			(servletNameFromProperties == null)) {

			StringBundler sb = new StringBundler(7);

			sb.append("One of the service properties ");
			sb.append(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE);
			sb.append(", ");
			sb.append(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);
			sb.append(", ");
			sb.append(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN);
			sb.append(" must contain a value.");

			throw new IllegalArgumentException(sb.toString());
		}

		for (String pattern : patterns) {
			PatternUtil.checkPattern(pattern);
		}

		boolean asyncSupported = GetterUtil.getBoolean(
			serviceReference.getProperty(
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED));

		Long serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);

		ClassLoader legacyTCCL = (ClassLoader)serviceReference.getProperty(
			HttpServiceConstants.CONTEXT_CLASSLOADER);

		if (legacyTCCL != null) {
			serviceId = -serviceId;
		}

		Map<String, String> servletInitParamsMap =
			ServiceProperties.parseInitParams(
				serviceReference,
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);

		Servlet servlet = serviceHolder.get();

		String generatedServletName = ServiceProperties.parseName(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME),
			servlet);

		ServletDTO servletDTO = new ServletDTO();

		servletDTO.asyncSupported = asyncSupported;
		servletDTO.initParams = servletInitParamsMap;
		servletDTO.name = generatedServletName;
		servletDTO.patterns = sort(patterns);
		servletDTO.serviceId = serviceId;
		servletDTO.servletContextId = contextController.getServiceId();
		servletDTO.servletInfo = servlet.getServletInfo();

		ErrorPageDTO errorPageDTO = null;

		if (errorPages.length > 0) {
			List<String> exceptions = new ArrayList<>();

			Set<Long> errorCodeSet = new LinkedHashSet<>();

			for (String errorPage : errorPages) {
				try {
					if (Objects.equals(errorPage, "4xx")) {
						for (long code = 400; code < 500; code++) {
							errorCodeSet.add(code);
						}
					}
					else if (Objects.equals(errorPage, "5xx")) {
						for (long code = 500; code < 600; code++) {
							errorCodeSet.add(code);
						}
					}
					else {
						long code = Long.parseLong(errorPage);

						errorCodeSet.add(code);
					}
				}
				catch (NumberFormatException numberFormatException) {
					if (_log.isDebugEnabled()) {
						_log.debug(numberFormatException);
					}

					exceptions.add(errorPage);
				}
			}

			long[] errorCodes = new long[errorCodeSet.size()];
			int i = 0;

			for (Long code : errorCodeSet) {
				errorCodes[i] = code;
				i++;
			}

			errorPageDTO = new ErrorPageDTO();

			errorPageDTO.asyncSupported = asyncSupported;
			errorPageDTO.errorCodes = errorCodes;
			errorPageDTO.exceptions = exceptions.toArray(new String[0]);
			errorPageDTO.initParams = servletInitParamsMap;
			errorPageDTO.name = generatedServletName;
			errorPageDTO.serviceId = serviceId;
			errorPageDTO.servletContextId = contextController.getServiceId();
			errorPageDTO.servletInfo = servlet.getServletInfo();
		}

		ServletContextHelper servletContextHelper =
			contextController.getServletContextHelper(
				serviceHolder.getBundle());

		ServletRegistration servletRegistration = new ServletRegistration(
			serviceHolder, servletDTO, errorPageDTO, servletContextHelper,
			contextController, legacyTCCL);

		servletRegistration.init(
			new ServletConfigImpl(
				generatedServletName, servletInitParamsMap,
				contextController.createServletContextAdaptor(
					serviceHolder.getBundle(), servletContextHelper)));

		Set<EndpointRegistration<?>> endpointRegistrations =
			contextController.getEndpointRegistrations();

		endpointRegistrations.add(servletRegistration);

		return servletRegistration;
	}

	private ServletRegistration _addServletRegistration(
			ServiceReference<Servlet> serviceReference)
		throws ServletException {

		contextController.checkShutdown();

		ContextController.ServiceHolder<Servlet> serviceHolder =
			new ContextController.ServiceHolder<>(
				bundleContext.getServiceObjects(serviceReference));

		Servlet servlet = serviceHolder.get();

		ServletRegistration registration = null;
		boolean addedRegisteredObject = false;

		Set<Object> registeredObjects =
			httpServiceRuntimeController.getRegisteredObjects();

		try {
			if (servlet == null) {
				throw new IllegalArgumentException("Servlet cannot be null");
			}

			addedRegisteredObject = registeredObjects.add(servlet);

			if (addedRegisteredObject) {
				registration = _addServletRegistration(
					serviceHolder, serviceReference);
			}
		}
		finally {
			if (registration == null) {
				serviceHolder.release();

				if (addedRegisteredObject) {
					registeredObjects.remove(servlet);
				}
			}
		}

		return registration;
	}

	private void _recordFailedServletDTO(
		ServiceReference<Servlet> serviceReference, int failureReason) {

		FailedServletDTO failedServletDTO = new FailedServletDTO();

		failedServletDTO.asyncSupported = GetterUtil.getBoolean(
			serviceReference.getProperty(
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED));
		failedServletDTO.failureReason = failureReason;
		failedServletDTO.initParams = ServiceProperties.parseInitParams(
			serviceReference,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);
		failedServletDTO.name = (String)serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);
		failedServletDTO.patterns = StringPlus.from(
			serviceReference.getProperty(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN));
		failedServletDTO.serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		failedServletDTO.servletContextId = contextController.getServiceId();
		failedServletDTO.servletInfo = StringPool.BLANK;

		httpServiceRuntimeController.recordDTO(
			serviceReference, failedServletDTO);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServletServiceTrackerCustomizer.class.getName());

}