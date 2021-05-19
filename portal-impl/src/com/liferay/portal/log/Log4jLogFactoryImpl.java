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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * @author Brian Wing Shun Chan
 */
public class Log4jLogFactoryImpl implements LogFactory {

	public Log4jLogFactoryImpl() {
		Logger rootLogger = _loggerContext.getRootLogger();

		LoggerConfig loggerConfig = rootLogger.get();

		loggerConfig.setLevel(Level.ERROR);

		_loggerContext.updateLoggers();
	}

	@Override
	public Log getLog(Class<?> c) {
		return getLog(c.getName());
	}

	@Override
	public Log getLog(String name) {
		return new Log4jLogImpl(_loggerContext.getLogger(name));
	}

	private final LoggerContext _loggerContext = LoggerContext.getContext();

}