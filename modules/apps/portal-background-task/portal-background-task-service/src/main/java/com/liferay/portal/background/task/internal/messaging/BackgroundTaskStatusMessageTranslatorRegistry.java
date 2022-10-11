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

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageTranslator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dante Wang
 */
public class BackgroundTaskStatusMessageTranslatorRegistry {

	public BackgroundTaskStatusMessageTranslator
		getBackgroundTaskStatusMessageTranslator(long backgroundTaskId) {

		return _backgroundTaskStatusMessageTranslators.get(backgroundTaskId);
	}

	public void registerBackgroundTaskStatusMessageTranslator(
		long backgroundTaskId,
		BackgroundTaskStatusMessageTranslator
			backgroundTaskStatusMessageTranslator) {

		_backgroundTaskStatusMessageTranslators.put(
			backgroundTaskId, backgroundTaskStatusMessageTranslator);
	}

	public void unregisterBackgroundTaskStatusMessageTranslator(
		long backgroundTaskId) {

		_backgroundTaskStatusMessageTranslators.remove(backgroundTaskId);
	}

	private final Map<Long, BackgroundTaskStatusMessageTranslator>
		_backgroundTaskStatusMessageTranslators = new ConcurrentHashMap<>();

}