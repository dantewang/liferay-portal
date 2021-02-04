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

package com.liferay.petra.log4j.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

/**
 * @author Hai Yu
 */
public class Log4JConfigurator {

	public static void configureLog4JXml(String xml) {
		try {
			ConfigurationSource configurationSource = new ConfigurationSource(
				new UnsyncByteArrayInputStream(xml.getBytes(StringPool.UTF8)));

			AbstractConfiguration abstractConfiguration = null;

			if (xml.contains(
					"<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">")) {

				abstractConfiguration =
					new org.apache.log4j.xml.XmlConfiguration(
						_loggerContext, configurationSource, 0);
			}
			else {
				abstractConfiguration = new XmlConfiguration(
					_loggerContext, configurationSource);
			}

			_centralizedConfiguration.addConfiguration(abstractConfiguration);
		}
		catch (IOException ioException) {
			_log.error(ioException, ioException);
		}
	}

	public static String getOriginalLevel(String className) {
		Level level = Level.ALL;

		if (LogManager.exists(className)) {
			Logger logger = (Logger)LogManager.getLogger(className);

			level = logger.getLevel();
		}

		return level.toString();
	}

	public static void setLevel(String name, String priority) {
		Logger logger = (Logger)LogManager.getLogger(name);

		logger.setLevel(Level.toLevel(priority));
	}

	public static void shutdownLog4J() {
		LogManager.shutdown();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Log4JConfigurator.class);

	private static final CentralizedConfiguration _centralizedConfiguration;
	private static final LoggerContext _loggerContext;

	static {
		PluginManager.addPackage("com.liferay.petra.log4j");

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext();

		CentralizedConfiguration centralizedConfiguration =
			new CentralizedConfiguration(loggerContext);

		loggerContext.setConfiguration(centralizedConfiguration);

		_loggerContext = loggerContext;

		_centralizedConfiguration = centralizedConfiguration;
	}

}