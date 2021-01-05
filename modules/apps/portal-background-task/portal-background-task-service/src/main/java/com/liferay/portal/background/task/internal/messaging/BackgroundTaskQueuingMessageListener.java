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

package com.liferay.portal.background.task.internal.messaging;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.background.task.internal.lock.BackgroundTaskLockHelper;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Michael C. Han
 */
public class BackgroundTaskQueuingMessageListener extends BaseMessageListener {

	public BackgroundTaskQueuingMessageListener(
		BackgroundTaskManager backgroundTaskManager, LockManager lockManager) {

		_backgroundTaskManager = backgroundTaskManager;

		_backgroundTaskLockHelper = new BackgroundTaskLockHelper(lockManager);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		String taskExecutorClassName = (String)message.get(
			"taskExecutorClassName");

		if (Validator.isNull(taskExecutorClassName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Message " + message +
						" is missing the key \"taskExecutorClassName\"");
			}

			return;
		}

		String lockKey = (String)message.get("lockKey");

		if (lockKey == null) {
			return;
		}

		int status = (Integer)message.get("status");

		if ((status == BackgroundTaskConstants.STATUS_CANCELLED) ||
			(status == BackgroundTaskConstants.STATUS_FAILED) ||
			(status == BackgroundTaskConstants.STATUS_SUCCESSFUL)) {

			_executeQueuedBackgroundTasks(
				taskExecutorClassName, lockKey, message);
		}
		else if (status == BackgroundTaskConstants.STATUS_QUEUED) {
			long backgroundTaskId = (Long)message.get(
				BackgroundTaskConstants.BACKGROUND_TASK_ID);

			BackgroundTask backgroundTask =
				_backgroundTaskManager.fetchBackgroundTask(backgroundTaskId);

			if (!_backgroundTaskLockHelper.isLockedBackgroundTask(
					backgroundTask)) {

				_executeQueuedBackgroundTasks(
					taskExecutorClassName, lockKey, message);
			}
		}
	}

	private void _executeQueuedBackgroundTasks(
		String taskExecutorClassName, String lockKey, Message message) {

		int isolationLevel = (int)message.get("isolationLevel");

		if (_log.isDebugEnabled()) {
			if (isolationLevel !=
					BackgroundTaskConstants.ISOLATION_LEVEL_CUSTOM) {

				_log.debug(
					"Acquiring next queued background task for " + lockKey);
			}
			else {
				_log.debug(
					"Acquiring next queued background task for " +
						taskExecutorClassName);
			}
		}

		BackgroundTask backgroundTask = null;

		if (isolationLevel == BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY) {
			backgroundTask =
				_backgroundTaskManager.fetchFirstBackgroundTaskByCompanyId(
					GetterUtil.getLong(
						StringUtil.extractLast(lockKey, CharPool.POUND)),
					taskExecutorClassName,
					BackgroundTaskConstants.STATUS_QUEUED);
		}
		else if (isolationLevel ==
					BackgroundTaskConstants.ISOLATION_LEVEL_GROUP) {

			backgroundTask =
				_backgroundTaskManager.fetchFirstBackgroundTaskByGroupId(
					GetterUtil.getLong(
						StringUtil.extractLast(lockKey, CharPool.POUND)),
					taskExecutorClassName,
					BackgroundTaskConstants.STATUS_QUEUED);
		}
		else if (isolationLevel ==
					BackgroundTaskConstants.ISOLATION_LEVEL_TASK_NAME) {

			backgroundTask = _backgroundTaskManager.fetchFirstBackgroundTask(
				StringUtil.extractLast(lockKey, CharPool.POUND),
				taskExecutorClassName, BackgroundTaskConstants.STATUS_QUEUED);
		}
		else {
			backgroundTask = _backgroundTaskManager.fetchFirstBackgroundTask(
				taskExecutorClassName, BackgroundTaskConstants.STATUS_QUEUED);
		}

		if (backgroundTask == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No additional queued background tasks for " +
						taskExecutorClassName);
			}

			return;
		}

		_backgroundTaskManager.resumeBackgroundTask(
			backgroundTask.getBackgroundTaskId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BackgroundTaskQueuingMessageListener.class);

	private final BackgroundTaskLockHelper _backgroundTaskLockHelper;
	private final BackgroundTaskManager _backgroundTaskManager;

}