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

package com.liferay.petra.log4j;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.log.Log4jLogFactoryImpl;
import com.liferay.portal.util.PropsImpl;

import java.io.File;

import java.net.URL;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Hai Yu
 */
public class Log4JUtilTest {

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(new PropsImpl());

		LogFactoryUtil.setLogFactory(new Log4jLogFactoryImpl());
	}

	@After
	public void tearDown() {
		Log4JUtil.setLevel(
			"com.liferay.portal.internal.servlet.MainServlet", "INFO", false);

		Log4JUtil.shutdownLog4J();
	}

	@Test
	public void testConfigureLog4JWithClassLoader() {
		URL url = _classLoader.getResource("META-INF/portal-log4j-ext.xml");

		String path = url.getPath();

		File file = new File(path);

		File tempFile = new File(
			StringUtil.replace(
				path, "portal-log4j-ext.xml", "portal-log4j-ext-temp.xml"));

		file.renameTo(tempFile);

		Log4JUtil.configureLog4J(_classLoader);

		Log log = LogFactoryUtil.getLog(
			"com.liferay.portal.internal.servlet.MainServlet");

		_assertLogLevel("INFO", log);

		tempFile.renameTo(file);

		Log4JUtil.configureLog4J(_classLoader);

		_assertLogLevel("WARN", log);

		_assertLogEnable();
	}

	@Test
	public void testConfigureLog4JWithURL() {
		URL url = _classLoader.getResource("META-INF/portal-log4j-ext.xml");

		Log4JUtil.configureLog4J(url);

		_assertLogEnable();
	}

	@Test
	public void testGetCustomLogSettings() {
		Log4JUtil.configureLog4J(_classLoader);

		Log4JUtil.setLevel("com.custom.log1", "INFO", true);
		Log4JUtil.setLevel("com.custom.log2", "WARN", true);

		Map<String, String> customLogMap = Log4JUtil.getCustomLogSettings();

		Assert.assertEquals(
			"The logger com.custom.log1 level should be INFO", "INFO",
			customLogMap.get("com.custom.log1"));
		Assert.assertEquals(
			"The logger com.custom.log2 level should be WARN", "WARN",
			customLogMap.get("com.custom.log2"));
	}

	@Test
	public void testGetOriginalLevel() {
		Log4JUtil.configureLog4J(_classLoader);

		Assert.assertEquals(
			"The original level should be WARN", "WARN",
			Log4JUtil.getOriginalLevel(
				"com.liferay.portal.internal.servlet.MainServlet"));

		Assert.assertEquals(
			"The original level should be WARN", "WARN",
			Log4JUtil.getOriginalLevel(LoggerName.LOGGER_WARN.toString()));
	}

	@Test
	public void testInitLog4J() {
		Log4JUtil.initLog4J(
			ServerDetector.getServerId(), PropsUtil.get(PropsKeys.LIFERAY_HOME),
			_classLoader, new Log4jLogFactoryImpl(),
			HashMapBuilder.putAll(
				new HashMap<String, String>()
			).put(
				com.liferay.portal.util.PropsUtil.class.getName(), "WARN"
			).build());

		_assertLogLevel(
			"WARN",
			LogFactoryUtil.getLog(
				com.liferay.portal.util.PropsUtil.class.getName()));

		_assertLogLevel(
			"INFO", LogFactoryUtil.getLog(LoggerName.LOGGER_INFO.toString()));

		_assertLogLevel(
			"WARN",
			LogFactoryUtil.getLog(
				"com.liferay.portal.internal.servlet.MainServlet"));
	}

	@Test
	public void testSetLevel() {
		Log4JUtil.configureLog4J(_classLoader);

		Log log = LogFactoryUtil.getLog(LoggerName.LOGGER_WARN.toString());

		_assertLogLevel("WARN", log);

		Log4JUtil.setLevel(LoggerName.LOGGER_WARN.toString(), "DEBUG", false);

		_assertLogLevel("DEBUG", log);

		Log childLog = LogFactoryUtil.getLog("com.test.parent.child");

		_assertLogLevel("INFO", childLog);

		Log4JUtil.setLevel("com.test.parent", "DEBUG", false);

		_assertLogLevel("DEBUG", childLog);
	}

	@Test
	public void testShutdownLog4J() {
		Log4JUtil.configureLog4J(_classLoader);

		Logger logger = Logger.getRootLogger();

		Enumeration<Appender> appendersEnumeration = logger.getAllAppenders();

		Assert.assertTrue(
			"The root logger should include appenders",
			appendersEnumeration.hasMoreElements());

		Log4JUtil.shutdownLog4J();

		Assert.assertFalse(
			"The root logger should not own appenders after shutting down",
			appendersEnumeration.hasMoreElements());
	}

	private void _assertLogEnable() {
		_assertLogLevel(
			"ALL", LogFactoryUtil.getLog(LoggerName.LOGGER_ALL.toString()));
		_assertLogLevel(
			"OFF", LogFactoryUtil.getLog(LoggerName.LOGGER_OFF.toString()));
		_assertLogLevel(
			"FATAL", LogFactoryUtil.getLog(LoggerName.LOGGER_FATAL.toString()));
		_assertLogLevel(
			"ERROR", LogFactoryUtil.getLog(LoggerName.LOGGER_ERROR.toString()));
		_assertLogLevel(
			"WARN", LogFactoryUtil.getLog(LoggerName.LOGGER_WARN.toString()));
		_assertLogLevel(
			"INFO", LogFactoryUtil.getLog(LoggerName.LOGGER_INFO.toString()));
		_assertLogLevel(
			"DEBUG", LogFactoryUtil.getLog(LoggerName.LOGGER_DEBUG.toString()));
		_assertLogLevel(
			"TRACE", LogFactoryUtil.getLog(LoggerName.LOGGER_TRACE.toString()));
	}

	private void _assertLogLevel(String expectedLevel, Log log) {
		if (expectedLevel.equals("ALL")) {
			Assert.assertTrue(
				"TRACE should be enabled if logging level is ALL",
				log.isTraceEnabled());

			return;
		}

		String actualLevel = null;

		if (log.isTraceEnabled()) {
			actualLevel = "TRACE";
		}
		else if (log.isDebugEnabled()) {
			actualLevel = "DEBUG";
		}
		else if (log.isInfoEnabled()) {
			actualLevel = "INFO";
		}
		else if (log.isWarnEnabled()) {
			actualLevel = "WARN";
		}
		else if (log.isErrorEnabled()) {
			actualLevel = "ERROR";
		}
		else if (log.isFatalEnabled()) {
			actualLevel = "FATAL";
		}
		else {
			actualLevel = "OFF";
		}

		Assert.assertEquals(
			"Logging level is wrong", expectedLevel, actualLevel);
	}

	private static final ClassLoader _classLoader =
		Log4JUtilTest.class.getClassLoader();

	private enum LoggerName {

		LOGGER_ALL("logger.all"), LOGGER_DEBUG("logger.debug"),
		LOGGER_ERROR("logger.error"), LOGGER_FATAL("logger.fatal"),
		LOGGER_INFO("logger.info"), LOGGER_OFF("logger.off"),
		LOGGER_TRACE("logger.trace"), LOGGER_WARN("logger.warn");

		@Override
		public String toString() {
			return _name;
		}

		private LoggerName(String name) {
			_name = name;
		}

		private final String _name;

	}

}