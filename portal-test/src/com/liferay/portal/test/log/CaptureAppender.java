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

package com.liferay.portal.test.log;

import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.petra.string.StringBundler;

import java.io.Closeable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.bridge.LogEventAdapter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

/**
 * @author Shuyang Zhou
 */
public class CaptureAppender extends AbstractAppender implements Closeable {

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no replacement
	 */
	@Deprecated
	public CaptureAppender(Logger logger) {
		super(logger.getName(), null, null, true, null);

		_logger = null;

		_level = null;

		_loggerConfig = null;

		_additive = false;
	}

	public CaptureAppender(org.apache.logging.log4j.core.Logger logger) {
		super(logger.getName(), null, null, true, null);

		_logger = logger;

		_level = _logger.getLevel();

		_loggerConfig = _logger.get();

		_additive = _loggerConfig.isAdditive();

		_loggerConfig.setAdditive(false);
	}

	@Override
	public void append(org.apache.logging.log4j.core.LogEvent logEvent) {
		_logEvents.add(new LogEvent(new PrintableLogEvent(logEvent)));
	}

	@Override
	public void close() {
		_logger.removeAppender(this);

		String loggerName = _logger.getName();
		String loggerConfigName = _loggerConfig.getName();

		if (!loggerName.equals(loggerConfigName)) {
			LoggerConfig loggerConfig = _logger.get();

			loggerConfig.setAdditive(_additive);
		}

		_loggerConfig.setAdditive(_additive);

		Log4JUtil.setLevel(loggerName, _level.toString(), false);
	}

	public List<LogEvent> getLogEvents() {
		return _logEvents;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #getLogEvents()}
	 */
	@Deprecated
	public List<LoggingEvent> getLoggingEvents() {
		List<LoggingEvent> loggingEvents = new ArrayList<>();

		for (LogEvent logEvent : _logEvents) {
			loggingEvents.add(
				new LogEventAdapter(
					(org.apache.logging.log4j.core.LogEvent)
						logEvent.getWrappedObject()));
		}

		return loggingEvents;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no replacement
	 */
	@Deprecated
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no replacement
	 */
	@Deprecated
	protected void append(LoggingEvent loggingEvent) {
	}

	private final boolean _additive;
	private final Level _level;
	private final List<LogEvent> _logEvents = new CopyOnWriteArrayList<>();
	private final org.apache.logging.log4j.core.Logger _logger;
	private final LoggerConfig _loggerConfig;

	private static class PrintableLogEvent extends Log4jLogEvent {

		@Override
		public String toString() {
			StringBundler sb = new StringBundler(5);

			sb.append("{level=");
			sb.append(getLevel());
			sb.append(", message=");
			sb.append(getMessage());
			sb.append("}");

			return sb.toString();
		}

		private PrintableLogEvent(
			org.apache.logging.log4j.core.LogEvent logEvent) {

			super(
				logEvent.getLoggerName(), logEvent.getMarker(),
				logEvent.getLoggerFqcn(), logEvent.getSource(),
				logEvent.getLevel(), logEvent.getMessage(),
				(List<Property>)null, logEvent.getThrown());
		}

	}

}