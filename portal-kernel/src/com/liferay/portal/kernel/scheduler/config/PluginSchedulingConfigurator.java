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

package com.liferay.portal.kernel.scheduler.config;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListener;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListenerAdapter;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuyang Zhou
 * @author Tina Tian
 */
public class PluginSchedulingConfigurator {

	public void afterPropertiesSet() {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		boolean forceSync = ProxyModeThreadLocal.isForceSync();

		ProxyModeThreadLocal.setForceSync(true);

		try {
			ClassLoader portalClassLoader =
				PortalClassLoaderUtil.getClassLoader();

			currentThread.setContextClassLoader(portalClassLoader);

			for (SchedulerEntry schedulerEntry : _schedulerEntries) {
				try {
					MessageListener messageListener =
						(MessageListener)InstanceFactory.newInstance(
							PortletClassLoaderUtil.getClassLoader(),
							schedulerEntry.getEventListenerClass());

					SchedulerEventMessageListenerAdapter
						schedulerEventMessageListenerAdapter =
							new SchedulerEventMessageListenerAdapter();

					schedulerEventMessageListenerAdapter.setMessageListener(
						messageListener);
					schedulerEventMessageListenerAdapter.setSchedulerEntry(
						schedulerEntry);

					Map<String, Object> properties = new HashMap<>();

					properties.put(
						"destination.name",
						DestinationNames.SCHEDULER_DISPATCH);

					Registry registry = RegistryUtil.getRegistry();

					ServiceRegistration<SchedulerEventMessageListener>
						serviceRegistration = registry.registerService(
							SchedulerEventMessageListener.class,
							schedulerEventMessageListenerAdapter, properties);

					_serviceRegistrations.put(
						schedulerEntry.getEventListenerClass(),
						serviceRegistration);
				}
				catch (Exception e) {
					_log.error("Unable to schedule " + schedulerEntry, e);
				}
			}
		}
		finally {
			ProxyModeThreadLocal.setForceSync(forceSync);

			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	public void destroy() {
		for (ServiceRegistration<SchedulerEventMessageListener>
				serviceRegistration : _serviceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
		_schedulerEntries.clear();
	}

	public void setSchedulerEntries(List<SchedulerEntry> schedulerEntries) {
		_schedulerEntries = schedulerEntries;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PluginSchedulingConfigurator.class);

	private List<SchedulerEntry> _schedulerEntries = Collections.emptyList();
	private final Map
		<String, ServiceRegistration<SchedulerEventMessageListener>>
			_serviceRegistrations = new HashMap<>();

}