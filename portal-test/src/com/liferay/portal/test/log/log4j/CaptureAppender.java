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

package com.liferay.portal.test.log.log4j;

import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Closeable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * @author Shuyang Zhou
 */
public class CaptureAppender implements Closeable {

	public CaptureAppender(Logger logger) {
		_logger = logger;

		_level = _logger.getLevel();

		_loggerConfig = _logger.get();

		_additive = _loggerConfig.isAdditive();

		_loggerConfig.setAdditive(false);

		_logAppender.start();

		_logger.addAppender(_logAppender);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no replacement
	 */
	@Deprecated
	public CaptureAppender(org.apache.log4j.Logger logger) {
		_logger = null;

		_level = null;

		_loggerConfig = null;

		_additive = false;
	}

	@Override
	public void close() {
		_logger.removeAppender(_logAppender);

		_loggerConfig.setAdditive(_additive);

		Log4JUtil.setLevel(_logger.getName(), _level.toString(), false);
	}

	public List<LogEvent> getLogEvents() {
		return _logEvents;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #getLogEvents()}
	 */
	@Deprecated
	public List<LogEvent> getLoggingEvents() {
		return getLogEvents();
	}

	private final boolean _additive;
	private final Level _level;
	private final LogAppender _logAppender = new LogAppender(
		StringUtil.randomString());
	private final List<LogEvent> _logEvents = new CopyOnWriteArrayList<>();
	private final Logger _logger;
	private final LoggerConfig _loggerConfig;

	private class LogAppender extends AbstractAppender {

		public LogAppender(String appenderName) {
			super(appenderName, null, null, true, null);
		}

		@Override
		public void append(org.apache.logging.log4j.core.LogEvent logEvent) {
			_logEvents.add(new LogEvent(logEvent));
		}

	}

}