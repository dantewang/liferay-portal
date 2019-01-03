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

package com.liferay.trash.web.internal.messaging;

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
import com.liferay.portal.util.PropsValues;
import com.liferay.trash.service.TrashEntryLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides a scheduled task to empty the Recycly Bin when the maximum Recycle
 * Bin entry age has been exceeded. The maximum Recycle Bin entry age is defined
 * by the <code>trash.entries.max.age</code> property (in minutes). The
 * scheduled task uses the <code>trash.entry.check.interval</code> property to
 * define the execution interval (in minutes).
 *
 * @author Eudaldo Alonso
 */
@Component(
	immediate = true,
	property = "destination.name=" + DestinationNames.SCHEDULER_DISPATCH,
	service = {
		CheckEntryMessageListener.class, SchedulerEventMessageListener.class
	}
)
public class CheckEntryMessageListener
	extends BaseMessageListener implements SchedulerEventMessageListener {

	@Override
	public SchedulerEntry getSchedulerEntry() {
		return _schedulerEntry;
	}

	@Activate
	protected void activate() {
		Class<?> clazz = getClass();

		String className = clazz.getName();

		Trigger trigger = _triggerFactory.createTrigger(
			className, className, null, null,
			PropsValues.TRASH_ENTRY_CHECK_INTERVAL, TimeUnit.MINUTE);

		_schedulerEntry = new SchedulerEntryImpl(className, trigger);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		_trashEntryLocalService.checkEntries();
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	@Reference(unbind = "-")
	protected void setTrashEntryLocalService(
		TrashEntryLocalService trashEntryLocalService) {

		_trashEntryLocalService = trashEntryLocalService;
	}

	private SchedulerEntry _schedulerEntry;
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TriggerFactory _triggerFactory;

}