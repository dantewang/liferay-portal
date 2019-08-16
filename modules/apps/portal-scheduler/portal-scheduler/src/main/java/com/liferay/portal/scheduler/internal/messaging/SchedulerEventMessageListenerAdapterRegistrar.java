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

package com.liferay.portal.scheduler.internal.messaging;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListenerAdapter;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Hai Yu
 */
@Component(immediate = true, service = {})
public class SchedulerEventMessageListenerAdapterRegistrar {

	@Activate
	public void activate(ComponentContext componentContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			componentContext.getBundleContext(),
			SchedulerEventMessageListenerAdapter.class,
			new SchedulerEventMessageListenerServiceTrackerCustomizer());
	}

	@Deactivate
	public void deactivate() {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	private ServiceTracker
		<SchedulerEventMessageListenerAdapter,
		 SchedulerEventMessageListenerAdapter> _serviceTracker;

	private class SchedulerEventMessageListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<SchedulerEventMessageListenerAdapter,
			 SchedulerEventMessageListenerAdapter> {

		@Override
		public SchedulerEventMessageListenerAdapter addingService(
			ServiceReference<SchedulerEventMessageListenerAdapter>
				serviceReference) {

			Bundle bundle = serviceReference.getBundle();

			BundleContext bundleContext = bundle.getBundleContext();

			SchedulerEventMessageListenerAdapter
				schedulerEventMessageListenerAdapter = bundleContext.getService(
					serviceReference);

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(
				"destination.name",
				(String)serviceReference.getProperty("destination.name"));

			SchedulerEventMessageListenerWrapper
				schedulerEventMessageListenerWrapper =
					new SchedulerEventMessageListenerWrapper();

			schedulerEventMessageListenerWrapper.
				setSchedulerEventMessageListener(
					schedulerEventMessageListenerAdapter);

			ServiceRegistration<MessageListener> serviceRegistration =
				bundleContext.registerService(
					MessageListener.class, schedulerEventMessageListenerWrapper,
					properties);

			_serviceRegistrations.put(
				(String)serviceReference.getProperty(
					"event.listener.classname"),
				serviceRegistration);

			return schedulerEventMessageListenerAdapter;
		}

		@Override
		public void modifiedService(
			ServiceReference<SchedulerEventMessageListenerAdapter>
				serviceReference,
			SchedulerEventMessageListenerAdapter
				schedulerEventMessageListenerAdapter) {
		}

		@Override
		public void removedService(
			ServiceReference<SchedulerEventMessageListenerAdapter>
				serviceReference,
			SchedulerEventMessageListenerAdapter
				schedulerEventMessageListenerAdapter) {

			Bundle bundle = serviceReference.getBundle();

			BundleContext bundleContext = bundle.getBundleContext();

			bundleContext.ungetService(serviceReference);

			ServiceRegistration<MessageListener> serviceRegistration =
				_serviceRegistrations.remove(
					(String)serviceReference.getProperty(
						"event.listener.classname"));

			serviceRegistration.unregister();
		}

		private final Map<String, ServiceRegistration<MessageListener>>
			_serviceRegistrations = new ConcurrentHashMap<>();

	}

}