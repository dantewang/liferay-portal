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

import com.liferay.petra.string.StringBundler;

import java.io.Closeable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

/**
 * @author Shuyang Zhou
 */
public class CaptureAppender extends AbstractAppender implements Closeable {

	public CaptureAppender(Logger logger) {
		super(logger.getName(), null, null, true, null);

		_logger = logger;

		_level = _logger.getLevel();

		_loggerConfig = _logger.get();

		_loggerConfig.setAdditive(false);
	}

	@Override
	public void append(LogEvent logEvent) {
		_logEvents.add(new PrintableLogEvent((Log4jLogEvent)logEvent));
	}

	@Override
	public void close() {
		_logger.removeAppender(this);

		_logger.setLevel(_level);

		_loggerConfig.setAdditive(true);
	}

	public List<LogEvent> getLogEvents() {
		return _logEvents;
	}

	private final Level _level;
	private final List<LogEvent> _logEvents = new CopyOnWriteArrayList<>();
	private final Logger _logger;
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

		private PrintableLogEvent(LogEvent logEvent) {
			super(
				logEvent.getLoggerName(), logEvent.getMarker(),
				logEvent.getLoggerFqcn(), logEvent.getSource(),
				logEvent.getLevel(), logEvent.getMessage(),
				(List<Property>)null, logEvent.getThrown());
		}

	}

}