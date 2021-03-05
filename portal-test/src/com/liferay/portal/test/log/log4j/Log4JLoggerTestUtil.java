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

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;

import java.lang.reflect.Field;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * @author Shuyang Zhou
 */
public class Log4JLoggerTestUtil {

	public static final String ALL = String.valueOf(Level.ALL);

	public static final String DEBUG = String.valueOf(Level.DEBUG);

	public static final String ERROR = String.valueOf(Level.ERROR);

	public static final String FATAL = String.valueOf(Level.FATAL);

	public static final String INFO = String.valueOf(Level.INFO);

	public static final String OFF = String.valueOf(Level.OFF);

	public static final String TRACE = String.valueOf(Level.TRACE);

	public static final String WARN = String.valueOf(Level.WARN);

	public static LogCapture configureLog4JLogger(
		String name, String priority) {

		LogWrapper logWrapper = (LogWrapper)LogFactoryUtil.getLog(name);

		Log log = logWrapper.getWrappedLog();

		Logger logger = null;

		try {
			logger = ReflectionTestUtil.getFieldValue(log, "_logger");
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Log " + name + " is not a Log4j logger");
		}

		Log4JLogCapture log4JLogCapture = new Log4JLogCapture(logger);

		logger.addAppender(log4JLogCapture);

		logger.setLevel(Level.toLevel(priority));

		return log4JLogCapture;
	}

	private static class Log4JLogCapture
		extends AppenderSkeleton implements LogCapture {

		@Override
		public void close() {
			closed = true;

			_logger.removeAppender(this);

			_logger.setLevel(_level);

			try {
				_parentField.set(_logger, _parentCategory);
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		public List<LogEntry> getLogEntries() {
			return _logEntries;
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		@Override
		public List<LogEntry> resetPriority(String priority) {
			_logEntries.clear();

			_logger.setLevel(Level.toLevel(priority));

			return _logEntries;
		}

		@Override
		protected void append(LoggingEvent loggingEvent) {
			_logEntries.add(new Log4JLogEntry(loggingEvent));
		}

		private Log4JLogCapture(Logger logger) {
			_logger = logger;

			_level = _logger.getLevel();

			_parentCategory = logger.getParent();

			try {
				_parentField.set(_logger, null);
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private static final Field _parentField;

		static {
			try {
				_parentField = ReflectionUtil.getDeclaredField(
					Category.class, "parent");
			}
			catch (Exception exception) {
				throw new ExceptionInInitializerError(exception);
			}
		}

		private final Level _level;
		private final List<LogEntry> _logEntries = new CopyOnWriteArrayList<>();
		private final Logger _logger;
		private final Category _parentCategory;

	}

	private static class Log4JLogEntry implements LogEntry {

		@Override
		public String getMessage() {
			return _loggingEvent.getRenderedMessage();
		}

		@Override
		public String getPriority() {
			return String.valueOf(_loggingEvent.getLevel());
		}

		@Override
		public Throwable getThrowable() {
			ThrowableInformation throwableInformation =
				_loggingEvent.getThrowableInformation();

			if (throwableInformation != null) {
				return throwableInformation.getThrowable();
			}

			return null;
		}

		@Override
		public Object getWrappedObject() {
			return _loggingEvent;
		}

		@Override
		public String toString() {
			StringBundler sb = new StringBundler(5);

			sb.append("{level=");
			sb.append(getPriority());
			sb.append(", message=");
			sb.append(getMessage());
			sb.append("}");

			return sb.toString();
		}

		private Log4JLogEntry(LoggingEvent loggingEvent) {
			_loggingEvent = loggingEvent;
		}

		private final LoggingEvent _loggingEvent;

	}

}