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

package com.liferay.sharing.internal.messaging;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.SchedulerEntryImpl;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListener;
import com.liferay.sharing.internal.configuration.SharingConfiguration;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.sharing.internal.configuration.SharingConfiguration",
	immediate = true,
	property = "destination.name=" + DestinationNames.SCHEDULER_DISPATCH,
	service = {
		DeleteExpiredSharingEntriesMessageListener.class,
		SchedulerEventMessageListener.class
	}
)
public class DeleteExpiredSharingEntriesMessageListener
	extends BaseMessageListener implements SchedulerEventMessageListener {

	@Override
	public SchedulerEntry getSchedulerEntry() {
		return _schedulerEntry;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_sharingConfiguration = ConfigurableUtil.createConfigurable(
			SharingConfiguration.class, properties);

		Class<?> clazz = getClass();

		String className = clazz.getName();

		Trigger trigger = _triggerFactory.createTrigger(
			className, className, null, null,
			_sharingConfiguration.expiredSharingEntriesCheckInterval(),
			TimeUnit.MINUTE);

		_schedulerEntry = new SchedulerEntryImpl(className, trigger);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		_sharingEntryLocalService.deleteExpiredEntries();
	}

	private SchedulerEntry _schedulerEntry;
	private volatile SharingConfiguration _sharingConfiguration;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	@Reference
	private TriggerFactory _triggerFactory;

}