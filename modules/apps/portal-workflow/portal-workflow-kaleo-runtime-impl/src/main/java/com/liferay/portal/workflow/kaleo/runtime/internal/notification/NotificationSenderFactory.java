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

package com.liferay.portal.workflow.kaleo.runtime.internal.notification;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.runtime.internal.util.WorkflowServiceTrackerCustomizer;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Michael C. Han
 */
@Component(service = NotificationSenderFactory.class)
public class NotificationSenderFactory {

	public NotificationSender getNotificationSender(String notificationType)
		throws WorkflowException {

		NotificationSender notificationSender = _notificationSenders.get(
			notificationType);

		if (notificationSender == null) {
			throw new WorkflowException(
				"Invalid notification type " + notificationType);
		}

		return notificationSender;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(notification.type=*)(objectClass=" +
				NotificationSender.class.getName() + "))",
			new WorkflowServiceTrackerCustomizer<NotificationSender>(
				bundleContext, null, false, "notification.type",
				_notificationSenders, true));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private String[] _getNotificationTypes(
		NotificationSender notificationSender, Map<String, Object> properties) {

		Object value = properties.get("notification.type");

		String[] notificationTypes = GetterUtil.getStringValues(
			value, new String[] {String.valueOf(value)});

		if (ArrayUtil.isEmpty(notificationTypes)) {
			throw new IllegalArgumentException(
				"The property \"notification.type\" is invalid for " +
					ClassUtil.getClassName(notificationSender));
		}

		return notificationTypes;
	}

	private final Map<String, NotificationSender> _notificationSenders =
		new ConcurrentHashMap<>();
	private ServiceTracker<NotificationSender, NotificationSender>
		_serviceTracker;

}