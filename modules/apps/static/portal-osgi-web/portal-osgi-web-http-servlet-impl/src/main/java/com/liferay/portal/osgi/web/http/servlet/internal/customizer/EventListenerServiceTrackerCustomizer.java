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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServiceRuntimeController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.error.HttpWhiteboardFailureException;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.ListenerRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.util.EventListeners;
import com.liferay.portal.osgi.web.http.servlet.internal.util.StringPlus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedListenerDTO;
import org.osgi.service.http.runtime.dto.ListenerDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class EventListenerServiceTrackerCustomizer
	extends BaseServiceTrackerCustomizer
		<EventListener, AtomicReference<ListenerRegistration>> {

	public EventListenerServiceTrackerCustomizer(
		BundleContext bundleContext, ContextController contextController,
		HttpServiceRuntimeController httpServiceRuntimeController) {

		super(bundleContext, contextController, httpServiceRuntimeController);
	}

	@Override
	public AtomicReference<ListenerRegistration> addingService(
		ServiceReference<EventListener> serviceReference) {

		Object listenerObject = serviceReference.getProperty(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER);

		if ((listenerObject == null) ||
			!contextController.matches(serviceReference) ||
			!httpServiceRuntimeController.matches(serviceReference)) {

			return null;
		}

		AtomicReference<ListenerRegistration> result = new AtomicReference<>();

		try {
			if (!(listenerObject instanceof Boolean) &&
				!(listenerObject instanceof String)) {

				throw new HttpWhiteboardFailureException(
					StringBundler.concat(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "=",
						listenerObject, " is not a valid option. Ignoring!"),
					DTOConstants.FAILURE_REASON_VALIDATION_FAILED);
			}

			if (!Boolean.parseBoolean(String.valueOf(listenerObject))) {
				return result;
			}

			result.set(_addListenerRegistration(serviceReference));
		}
		catch (HttpWhiteboardFailureException httpWhiteboardFailureException) {
			_log.error(httpWhiteboardFailureException);

			_recordFailedListenerDTO(
				serviceReference,
				httpWhiteboardFailureException.getFailureReason());
		}
		catch (Exception exception) {
			_log.error(exception);

			_recordFailedListenerDTO(
				serviceReference,
				DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT);
		}

		return result;
	}

	@Override
	public void modifiedService(
		ServiceReference<EventListener> serviceReference,
		AtomicReference<ListenerRegistration> listenerRegistration) {

		removedService(serviceReference, listenerRegistration);

		addingService(serviceReference);
	}

	@Override
	public void removedService(
		ServiceReference<EventListener> serviceReference,
		AtomicReference<ListenerRegistration> listenerReference) {

		ListenerRegistration listenerRegistration = listenerReference.get();

		if (listenerRegistration != null) {
			listenerRegistration.destroy();
		}

		httpServiceRuntimeController.removeDTO(
			FailedListenerDTO.class, serviceReference);
	}

	private ListenerRegistration _addListenerRegistration(
		ContextController.ServiceHolder<EventListener> serviceHolder,
		ServiceReference<EventListener> serviceReference) {

		List<Class<? extends EventListener>> classes = _getListenerClasses(
			serviceReference);

		if (classes.isEmpty()) {
			throw new IllegalArgumentException(
				"EventListener does not implement a supported type");
		}

		EventListener eventListener = serviceHolder.get();

		Set<ListenerRegistration> listenerRegistrations =
			contextController.getListenerRegistrations();

		for (ListenerRegistration listenerRegistration :
				listenerRegistrations) {

			if (Objects.equals(eventListener, listenerRegistration.getT())) {
				return null;
			}
		}

		ListenerDTO listenerDTO = new ListenerDTO();

		listenerDTO.serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		listenerDTO.servletContextId = contextController.getServiceId();
		listenerDTO.types = _asStringArray(classes);

		ServletContext servletContext =
			contextController.createServletContextAdaptor(
				serviceHolder.getBundle(),
				contextController.getServletContextHelper(
					serviceHolder.getBundle()));

		ListenerRegistration listenerRegistration = new ListenerRegistration(
			serviceHolder, classes, listenerDTO, servletContext,
			contextController);

		if (classes.contains(ServletContextListener.class)) {
			ServletContextListener servletContextListener =
				(ServletContextListener)listenerRegistration.getT();

			servletContextListener.contextInitialized(
				new ServletContextEvent(servletContext));
		}

		listenerRegistrations.add(listenerRegistration);

		EventListeners eventListeners = contextController.getEventListeners();

		eventListeners.put(classes, listenerRegistration);

		return listenerRegistration;
	}

	private ListenerRegistration _addListenerRegistration(
		ServiceReference<EventListener> serviceReference) {

		contextController.checkShutdown();

		ContextController.ServiceHolder<EventListener> listenerHolder =
			new ContextController.ServiceHolder<>(
				bundleContext.getServiceObjects(serviceReference));

		EventListener listener = listenerHolder.get();

		ListenerRegistration registration = null;

		try {
			if (listener == null) {
				throw new IllegalArgumentException(
					"EventListener cannot be null");
			}

			registration = _addListenerRegistration(
				listenerHolder, serviceReference);
		}
		finally {
			if (registration == null) {
				listenerHolder.release();
			}
		}

		return registration;
	}

	private String[] _asStringArray(
		List<Class<? extends EventListener>> classes) {

		String[] classesArray = new String[classes.size()];

		for (int i = 0; i < classesArray.length; i++) {
			Class<?> clazz = classes.get(i);

			classesArray[i] = clazz.getName();
		}

		Arrays.sort(classesArray);

		return classesArray;
	}

	private List<Class<? extends EventListener>> _getListenerClasses(
		ServiceReference<EventListener> serviceReference) {

		List<String> objectClassList = Arrays.asList(
			StringPlus.from(
				serviceReference.getProperty(Constants.OBJECTCLASS)));

		List<Class<? extends EventListener>> classes = new ArrayList<>();

		if (objectClassList.contains(ServletContextListener.class.getName())) {
			classes.add(ServletContextListener.class);
		}

		if (objectClassList.contains(
				ServletContextAttributeListener.class.getName())) {

			classes.add(ServletContextAttributeListener.class);
		}

		if (objectClassList.contains(ServletRequestListener.class.getName())) {
			classes.add(ServletRequestListener.class);
		}

		if (objectClassList.contains(
				ServletRequestAttributeListener.class.getName())) {

			classes.add(ServletRequestAttributeListener.class);
		}

		if (objectClassList.contains(HttpSessionListener.class.getName())) {
			classes.add(HttpSessionListener.class);
		}

		if (objectClassList.contains(
				HttpSessionAttributeListener.class.getName())) {

			classes.add(HttpSessionAttributeListener.class);
		}

		if (objectClassList.contains(HttpSessionIdListener.class.getName())) {
			classes.add(HttpSessionIdListener.class);
		}

		return classes;
	}

	private void _recordFailedListenerDTO(
		ServiceReference<EventListener> serviceReference, int failureReason) {

		FailedListenerDTO failedListenerDTO = new FailedListenerDTO();

		failedListenerDTO.failureReason = failureReason;
		failedListenerDTO.serviceId = (Long)serviceReference.getProperty(
			Constants.SERVICE_ID);
		failedListenerDTO.servletContextId = contextController.getServiceId();
		failedListenerDTO.types = StringPlus.from(
			serviceReference.getProperty(Constants.OBJECTCLASS));

		httpServiceRuntimeController.recordDTO(
			serviceReference, failedListenerDTO);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EventListenerServiceTrackerCustomizer.class.getName());

}