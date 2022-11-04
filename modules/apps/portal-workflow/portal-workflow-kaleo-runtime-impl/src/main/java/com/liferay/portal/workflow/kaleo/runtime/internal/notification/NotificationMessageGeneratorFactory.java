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
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.runtime.internal.util.WorkflowServiceTrackerCustomizer;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationMessageGenerator;

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
@Component(service = NotificationMessageGeneratorFactory.class)
public class NotificationMessageGeneratorFactory {

	public NotificationMessageGenerator getNotificationMessageGenerator(
			String templateLanguage)
		throws WorkflowException {

		NotificationMessageGenerator notificationMessageGenerator =
			_notificationMessageGenerators.get(templateLanguage);

		if (notificationMessageGenerator == null) {
			throw new WorkflowException(
				"Invalid template language " + templateLanguage);
		}

		return notificationMessageGenerator;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(template.language=*)(objectClass=" +
				NotificationMessageGenerator.class.getName() + "))",
			new WorkflowServiceTrackerCustomizer<NotificationMessageGenerator>(
				bundleContext, null, false, "template.language",
				_notificationMessageGenerators, true));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private final Map<String, NotificationMessageGenerator>
		_notificationMessageGenerators = new ConcurrentHashMap<>();
	private ServiceTracker
		<NotificationMessageGenerator, NotificationMessageGenerator>
			_serviceTracker;

}