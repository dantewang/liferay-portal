/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.tracker;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextControllerListener;
import com.liferay.portal.osgi.web.http.servlet.internal.error.HttpWhiteboardFailureException;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ServletRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.util.ServiceProperties;
import com.liferay.portal.osgi.web.http.servlet.internal.util.StringPlus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Dante Wang
 */
public class ServletTracker {

	public ServletTracker(
		BundleContext bundleContext,
		HttpServiceRuntimeController httpServiceRuntimeController) {

		_httpServiceRuntimeController = httpServiceRuntimeController;

		_httpServiceRuntimeController.addContextControllerListener(
			_contextControllerListener);

		_serviceTracker = new ServiceTracker<>(
			bundleContext, Servlet.class,
			new ServletServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	public void close() {
		_httpServiceRuntimeController.removeContextControllerListener(
			_contextControllerListener);

		_serviceTracker.close();
	}

	private void _recordFailedServletDTO(
		ServiceReference<Servlet> serviceReference, int failureReason,
		ContextController contextController) {

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

		_httpServiceRuntimeController.recordDTO(
			serviceReference, failedServletDTO);
	}

	private void _register(
		ContextController contextController,
		ServiceReference<Servlet> serviceReference) {

		try {
			_servletRegistrationsMap.put(
				serviceReference,
				contextController.addServletRegistration(serviceReference));
		}
		catch (HttpWhiteboardFailureException httpWhiteboardFailureException) {
			_log.error(httpWhiteboardFailureException);

			_recordFailedServletDTO(
				serviceReference,
				httpWhiteboardFailureException.getFailureReason(),
				contextController);
		}
		catch (Exception exception) {
			_log.error(exception);

			_recordFailedServletDTO(
				serviceReference, DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT,
				contextController);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServletTracker.class.getName());

	private final ContextControllerListener _contextControllerListener =
		new ServletTrackerContextControllerListener();
	private final Map<ServiceReference<Servlet>, String> _contextSelectorsMap =
		new ConcurrentHashMap<>();
	private final HttpServiceRuntimeController _httpServiceRuntimeController;
	private final ServiceTracker<Servlet, String> _serviceTracker;
	private final Map<ServiceReference<Servlet>, ServletRegistration>
		_servletRegistrationsMap = new ConcurrentHashMap<>();

	private class ServletServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Servlet, String> {

		@Override
		public String addingService(
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

			if (!_httpServiceRuntimeController.matches(serviceReference)) {
				return null;
			}

			String contextSelector = GetterUtil.getString(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT),
				StringBundler.concat(
					"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
					"=",
					HttpWhiteboardConstants.
						HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME,
					")"));

			for (ContextController contextController :
					_httpServiceRuntimeController.getContextControllers()) {

				if (!contextController.matches(contextSelector)) {
					continue;
				}

				_register(contextController, serviceReference);
			}

			_contextSelectorsMap.put(serviceReference, contextSelector);

			return contextSelector;
		}

		@Override
		public void modifiedService(
			ServiceReference<Servlet> serviceReference,
			String contextSelector) {

			removedService(serviceReference, contextSelector);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<Servlet> serviceReference,
			String contextSelector) {

			_contextSelectorsMap.remove(serviceReference);

			ServletRegistration servletRegistration =
				_servletRegistrationsMap.remove(serviceReference);

			if (servletRegistration != null) {
				servletRegistration.destroy();
			}

			_httpServiceRuntimeController.removeDTO(
				FailedServletDTO.class, serviceReference);
		}

	}

	private class ServletTrackerContextControllerListener
		implements ContextControllerListener {

		@Override
		public void contextControllerAdded(
			ContextController contextController) {

			_contextSelectorsMap.forEach(
				(serviceReference, contextSelector) -> {
					if (contextController.matches(contextSelector)) {
						_register(contextController, serviceReference);
					}
				});
		}

	}

}