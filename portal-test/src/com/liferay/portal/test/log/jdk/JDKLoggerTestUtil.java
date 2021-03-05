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

package com.liferay.portal.test.log.jdk;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Jdk14LogImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogWrapper;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Shuyang Zhou
 */
public class JDKLoggerTestUtil {

	public static JDKLogCapture configureJDKLogger(String name, Level level) {
		LogWrapper logWrapper = (LogWrapper)LogFactoryUtil.getLog(name);

		Log log = logWrapper.getWrappedLog();

		if (!(log instanceof Jdk14LogImpl)) {
			throw new IllegalStateException(
				"Log " + name + " is not a JDK logger");
		}

		Jdk14LogImpl jdk14LogImpl = (Jdk14LogImpl)log;

		Logger logger = jdk14LogImpl.getWrappedLogger();

		JDKLogCapture jdkLogCapture = new JDKLogCapture(logger, level);

		logger.addHandler(jdkLogCapture);

		return jdkLogCapture;
	}

	static {

		// See LPS-32051 and LPS-32471

		LogFactoryUtil.getLog(JDKLoggerTestUtil.class);
	}

	private static class JDKLogCapture extends Handler implements LogCapture {

		@Override
		public void close() {
			_logEntries.clear();

			_logger.removeHandler(this);

			for (Handler handler : _handlers) {
				_logger.addHandler(handler);
			}

			_logger.setLevel(_level);
			_logger.setUseParentHandlers(_useParentHandlers);
		}

		@Override
		public void flush() {
			_logEntries.clear();
		}

		@Override
		public List<LogEntry> getLogEntries() {
			return _logEntries;
		}

		@Override
		public boolean isLoggable(LogRecord logRecord) {
			return false;
		}

		@Override
		public void publish(LogRecord logRecord) {
			_logEntries.add(new JDKLogEntry(logRecord));
		}

		@Override
		public List<LogEntry> resetPriority(String priority) {
			_logEntries.clear();

			_logger.setLevel(Level.parse(priority));

			return _logEntries;
		}

		private JDKLogCapture(Logger logger, Level level) {
			_logger = logger;

			_handlers = logger.getHandlers();
			_level = logger.getLevel();
			_useParentHandlers = logger.getUseParentHandlers();

			for (Handler handler : _handlers) {
				logger.removeHandler(handler);
			}

			logger.setLevel(level);
			logger.setUseParentHandlers(false);
		}

		private final Handler[] _handlers;
		private final Level _level;
		private final List<LogEntry> _logEntries = new CopyOnWriteArrayList<>();
		private final Logger _logger;
		private final boolean _useParentHandlers;

	}

	private static class JDKLogEntry extends LogRecord implements LogEntry {

		@Override
		public String getPriority() {
			return String.valueOf(getLevel());
		}

		@Override
		public Throwable getThrowable() {
			return getThrown();
		}

		@Override
		public Object getWrappedObject() {
			return this;
		}

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

		private JDKLogEntry(LogRecord logRecord) {
			super(logRecord.getLevel(), logRecord.getMessage());

			setLoggerName(logRecord.getLoggerName());
			setMillis(logRecord.getMillis());
			setParameters(logRecord.getParameters());
			setResourceBundle(logRecord.getResourceBundle());
			setResourceBundleName(logRecord.getResourceBundleName());
			setSequenceNumber(logRecord.getSequenceNumber());
			setSourceClassName(logRecord.getSourceClassName());
			setSourceMethodName(logRecord.getSourceMethodName());
			setThreadID(logRecord.getThreadID());
			setThrown(logRecord.getThrown());
		}

	}

}