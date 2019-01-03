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

package com.liferay.announcements.web.internal.messaging;

import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalService;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.SchedulerEntryImpl;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListener;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.util.PropsValues;

import java.util.Date;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Tina Tian
 */
@Component(
	immediate = true,
	property = "destination.name=" + DestinationNames.SCHEDULER_DISPATCH,
	service = {
		CheckEntryMessageListener.class,
		ClusterMasterTokenTransitionListener.class,
		SchedulerEventMessageListener.class
	}
)
public class CheckEntryMessageListener
	extends BaseMessageListener
	implements ClusterMasterTokenTransitionListener,
			   SchedulerEventMessageListener {

	@Override
	public SchedulerEntry getSchedulerEntry() {
		return _schedulerEntry;
	}

	@Override
	public void masterTokenAcquired() {
	}

	@Override
	public void masterTokenReleased() {
		_previousEndDate = null;
	}

	@Activate
	protected void activate() {
		Class<?> clazz = getClass();

		String className = clazz.getName();

		Trigger trigger = _triggerFactory.createTrigger(
			className, className, null, null,
			PropsValues.ANNOUNCEMENTS_ENTRY_CHECK_INTERVAL, TimeUnit.MINUTE);

		_schedulerEntry = new SchedulerEntryImpl(className, trigger);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		Date startDate = _previousEndDate;
		Date endDate = new Date();

		if (startDate == null) {
			startDate = new Date(
				endDate.getTime() - _ANNOUNCEMENTS_ENTRY_CHECK_INTERVAL);
		}

		_previousEndDate = endDate;

		_announcementsEntryLocalService.checkEntries(startDate, endDate);
	}

	@Reference(unbind = "-")
	protected void setAnnouncementsEntryLocalService(
		AnnouncementsEntryLocalService announcementsEntryLocalService) {

		_announcementsEntryLocalService = announcementsEntryLocalService;
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	private static final long _ANNOUNCEMENTS_ENTRY_CHECK_INTERVAL =
		PropsValues.ANNOUNCEMENTS_ENTRY_CHECK_INTERVAL * Time.MINUTE;

	private AnnouncementsEntryLocalService _announcementsEntryLocalService;
	private Date _previousEndDate;
	private SchedulerEntry _schedulerEntry;

	@Reference
	private TriggerFactory _triggerFactory;

}