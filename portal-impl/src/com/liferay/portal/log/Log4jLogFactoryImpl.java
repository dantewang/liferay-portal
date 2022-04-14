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

package com.liferay.portal.log;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactory;
import com.liferay.portal.kernel.log.LogListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.BasePortalLifecycle;
import com.liferay.portal.kernel.util.PortalLifecycle;
import com.liferay.portal.kernel.util.PortalLifecycleUtil;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.message.Message;

/**
 * @author Brian Wing Shun Chan
 */
public class Log4jLogFactoryImpl implements LogFactory {

	@Override
	public Log getLog(Class<?> c) {
		return getLog(c.getName());
	}

	@Override
	public Log getLog(String name) {
		return new Log4jLogContextLogWrapper(
			new Log4jLogImpl(LogManager.getLogger(name)));
	}

	private static volatile LogListenerAppender _logListenerAppender;
	private static volatile ServiceTrackerList<LogListener> _serviceTrackerList;

	private static class LogListenerAppender extends AbstractAppender {

		public LogListenerAppender() {
			super(LogListenerAppender.class.getName(), null, null, false, null);
		}

		@Override
		public void append(LogEvent logEvent) {
			ServiceTrackerList<LogListener> serviceTrackerList =
				_serviceTrackerList;

			if (serviceTrackerList == null) {
				return;
			}

			for (LogListener logListener : _serviceTrackerList) {
				if (!logListener.isStarted()) {
					continue;
				}

				Level level = logEvent.getLevel();

				Message message = logEvent.getMessage();

				logListener.onLogged(
					level.toString(), logEvent.getLoggerName(),
					message.getFormattedMessage());
			}
		}

	}

	static {
		PortalLifecycleUtil.register(
			new BasePortalLifecycle() {

				@Override
				protected void doPortalDestroy() {
					ServiceTrackerList<LogListener> serviceTrackerList =
						_serviceTrackerList;

					_serviceTrackerList = null;

					if (serviceTrackerList != null) {
						serviceTrackerList.close();
					}

					LogListenerAppender logListenerAppender =
						_logListenerAppender;

					_logListenerAppender = null;

					if (logListenerAppender != null) {
						logListenerAppender.stop();
					}
				}

				@Override
				protected void doPortalInit() {
					_serviceTrackerList = ServiceTrackerListFactory.open(
						SystemBundleUtil.getBundleContext(), LogListener.class);

					Logger rootLogger = (Logger)LogManager.getRootLogger();

					_logListenerAppender = new LogListenerAppender();

					_logListenerAppender.start();

					rootLogger.addAppender(_logListenerAppender);
				}

			},
			PortalLifecycle.METHOD_ALL);
	}

}