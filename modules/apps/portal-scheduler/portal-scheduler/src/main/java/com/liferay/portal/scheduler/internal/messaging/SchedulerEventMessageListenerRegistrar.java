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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListener;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Validator;

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
 * @author Dante Wang
 */
@Component(immediate = true, service = {})
public class SchedulerEventMessageListenerRegistrar {

	@Activate
	public void activate(ComponentContext componentContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			componentContext.getBundleContext(),
			SchedulerEventMessageListener.class,
			new SchedulerEventMessageListenerServiceTrackerCustomizer());
	}

	@Deactivate
	public void deactivate() {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SchedulerEventMessageListenerRegistrar.class);

	private ServiceTracker
		<SchedulerEventMessageListener, SchedulerEventMessageListener>
			_serviceTracker;

	private class SchedulerEventMessageListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<SchedulerEventMessageListener, SchedulerEventMessageListener> {

		@Override
		public SchedulerEventMessageListener addingService(
			ServiceReference<SchedulerEventMessageListener> serviceReference) {

			Bundle bundle = serviceReference.getBundle();

			BundleContext bundleContext = bundle.getBundleContext();

			SchedulerEventMessageListener schedulerEventMessageListener =
				bundleContext.getService(serviceReference);

			SchedulerEntry schedulerEntry =
				schedulerEventMessageListener.getSchedulerEntry();

			String destinationName = (String)serviceReference.getProperty(
				"destination.name");

			if (Validator.isNull(destinationName)) {
				destinationName = DestinationNames.SCHEDULER_DISPATCH;
			}

			String eventListenerClassName = _getEventListenerClassName(
				schedulerEventMessageListener);

			if (((schedulerEntry == null) ||
				 (schedulerEntry.getTrigger() == null)) &&
				destinationName.equals(DestinationNames.SCHEDULER_DISPATCH)) {

				if (_log.isWarnEnabled()) {
					StringBundler sb = new StringBundler(4);

					sb.append("Unable to register the ");
					sb.append(eventListenerClassName);
					sb.append(" to message bus when destinationName is ");
					sb.append(DestinationNames.SCHEDULER_DISPATCH);

					_log.warn(sb.toString());
				}

				return null;
			}

			ServiceRegistration<MessageListener> serviceRegistration =
				_serviceRegistrations.get(eventListenerClassName);

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put("destination.name", destinationName);

			if (serviceRegistration != null) {
				serviceRegistration.setProperties(properties);

				return null;
			}

			SchedulerEventMessageListenerWrapper
				schedulerEventMessageListenerWrapper =
					new SchedulerEventMessageListenerWrapper();

			schedulerEventMessageListenerWrapper.
				setSchedulerEventMessageListener(schedulerEventMessageListener);

			serviceRegistration = bundleContext.registerService(
				MessageListener.class, schedulerEventMessageListenerWrapper,
				properties);

			_serviceRegistrations.put(
				eventListenerClassName, serviceRegistration);

			return schedulerEventMessageListener;
		}

		@Override
		public void modifiedService(
			ServiceReference<SchedulerEventMessageListener> serviceReference,
			SchedulerEventMessageListener schedulerEventMessageListener) {
		}

		@Override
		public void removedService(
			ServiceReference<SchedulerEventMessageListener> serviceReference,
			SchedulerEventMessageListener schedulerEntryMessageListener) {

			Bundle bundle = serviceReference.getBundle();

			BundleContext bundleContext = bundle.getBundleContext();

			bundleContext.ungetService(serviceReference);

			ServiceRegistration<MessageListener> serviceRegistration =
				_serviceRegistrations.remove(
					_getEventListenerClassName(schedulerEntryMessageListener));

			serviceRegistration.unregister();
		}

		private String _getEventListenerClassName(
			SchedulerEventMessageListener schedulerEventMessageListener) {

			SchedulerEntry schedulerEntry =
				schedulerEventMessageListener.getSchedulerEntry();

			if (schedulerEntry != null) {
				return schedulerEntry.getEventListenerClass();
			}

			Class<?> clazz = schedulerEventMessageListener.getClass();

			return clazz.getName();
		}

		private final Map<String, ServiceRegistration<MessageListener>>
			_serviceRegistrations = new ConcurrentHashMap<>();

	}

}