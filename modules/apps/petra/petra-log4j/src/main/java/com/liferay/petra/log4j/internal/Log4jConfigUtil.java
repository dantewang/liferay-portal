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

import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Tina Tian
 */
public class Log4jConfigUtil {

	public static Map<String, String> configureLog4J(
		String xmlContent, String... removedAppenderNames) {

		Map<String, String> priorities = new HashMap<>();

		Document document = null;

		try {
			SAXReader saxReader = new SAXReader();

			document = saxReader.read(new UnsyncStringReader(xmlContent));

			Element rootElement = document.getRootElement();

			for (Element element : rootElement.elements()) {
				for (Element childElement : element.elements()) {
					for (String appenderName : removedAppenderNames) {
						_removeAppender(element, childElement, appenderName);
					}

					if (Objects.equals("Logger", childElement.getName())) {
						priorities.put(
							childElement.attributeValue("name"),
							childElement.attributeValue("level"));
					}
				}
			}

			_centralizedConfiguration.addConfiguration(
				ConfigurationFactory.getConfiguration(document.asXML()));

			return priorities;
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return Collections.emptyMap();
		}
	}

	public static java.util.logging.Level getJDKLevel(String levelString) {
		if (StringUtil.equalsIgnoreCase(levelString, Level.DEBUG.toString())) {
			return java.util.logging.Level.FINE;
		}
		else if (StringUtil.equalsIgnoreCase(
					levelString, Level.ERROR.toString())) {

			return java.util.logging.Level.SEVERE;
		}
		else if (StringUtil.equalsIgnoreCase(
					levelString, Level.WARN.toString())) {

			return java.util.logging.Level.WARNING;
		}

		return java.util.logging.Level.INFO;
	}

	public static Map<String, String> getPriorities() {
		Map<String, String> priorities = new HashMap<>();

		Map<String, LoggerConfig> loggerConfigs =
			_centralizedConfiguration.getLoggers();

		for (Map.Entry<String, LoggerConfig> loggerConfigEntry :
				loggerConfigs.entrySet()) {

			LoggerConfig loggerConfig = loggerConfigEntry.getValue();

			if (loggerConfig.getParent() != null) {
				priorities.put(
					loggerConfigEntry.getKey(),
					String.valueOf(loggerConfig.getLevel()));
			}
		}

		return priorities;
	}

	public static void setLevel(String name, String priority) {
		Level level = Level.toLevel(priority);

		LoggerConfig loggerConfig = _centralizedConfiguration.getLogger(name);

		if (loggerConfig != null) {
			loggerConfig.setLevel(level);
		}
		else {
			loggerConfig = new LoggerConfig(name, level, true);

			_centralizedConfiguration.addLogger(name, loggerConfig);
		}

		LoggerContext loggerContext =
			_centralizedConfiguration.getLoggerContext();

		loggerContext.updateLoggers();
	}

	public static void shutdownLog4J() {
		LogManager.shutdown();
	}

	private static void _removeAppender(
		Element parentElement, Element element, String appenderName) {

		if (Objects.equals("Appenders", parentElement.getName()) &&
			Objects.equals(appenderName, element.attributeValue("name"))) {

			parentElement.remove(element);
		}

		for (Element childElement : element.elements()) {
			if (Objects.equals("AppenderRef", childElement.getName()) &&
				Objects.equals(
					appenderName, childElement.attributeValue("ref"))) {

				element.remove(childElement);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Log4jConfigUtil.class);

	private static final CentralizedConfiguration _centralizedConfiguration =
		new CentralizedConfiguration();

}