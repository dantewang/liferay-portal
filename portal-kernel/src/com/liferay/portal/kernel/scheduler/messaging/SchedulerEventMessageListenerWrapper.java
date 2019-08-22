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

package com.liferay.portal.kernel.scheduler.messaging;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.scheduler.JobState;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;

import java.util.Date;

/**
 * @author Shuyang Zhou
 * @deprecated As of Athanasius (7.3.x), replaced by {@link
 * com.liferay.portal.scheduler.internal.messaging.
 * SchedulerEventMessageListenerWrapper}
 */
@Deprecated
public class SchedulerEventMessageListenerWrapper
	implements SchedulerEventMessageListener {

	@Override
	public SchedulerEntry getSchedulerEntry() {
		return _schedulerEntry;
	}

	@Override
	public void receive(Message message) throws MessageListenerException {
		_messageListener.receive(message);
	}

	public void setMessageListener(MessageListener messageListener) {
		_messageListener = messageListener;
	}

	public void setSchedulerEntry(SchedulerEntry schedulerEntry) {
		_schedulerEntry = schedulerEntry;
	}

	protected void handleException(Message message, Exception exception) {
		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		if (jobState != null) {
			jobState.addException(exception, new Date());
		}
	}

	private MessageListener _messageListener;
	private volatile SchedulerEntry _schedulerEntry;

}