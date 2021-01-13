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
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.log.Log4jLogFactoryImpl;
import com.liferay.portal.util.PropsImpl;

import java.io.IOException;

import java.net.URL;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Hai Yu
 */
@NewEnv(type = NewEnv.Type.JVM)
public class Log4JUtilTest {

	@ClassRule
	@Rule
	public static final NewEnvTestRule newEnvTestRule = NewEnvTestRule.INSTANCE;

	@Before
	public void setUp() {
		PropsUtil.setProps(new PropsImpl());

		LogFactoryUtil.setLogFactory(new Log4jLogFactoryImpl());
	}

	@Test
	public void testConfigureLog4JWithClassLoader() {
		Log4JUtil.configureLog4J(new TestClassLoader());

		Log log = LogFactoryUtil.getLog(
			"com.liferay.portal.internal.servlet.MainServlet");

		_assertLog4JLevel("INFO", log);

		Log4JUtil.configureLog4J(_classLoader);

		_assertLog4JLevel("WARN", log);

		_assertLog4JLevels();

		_assertJDKLogLevels();
	}

	@Test
	public void testConfigureLog4JWithURL() {
		URL url = _classLoader.getResource("META-INF/portal-log4j-ext.xml");

		Log4JUtil.configureLog4J(url);

		_assertLog4JLevels();

		_assertJDKLogLevels();
	}

	@Test
	public void testGetCustomLogSettings() {
		Log4JUtil.configureLog4J(_classLoader);

		Log4JUtil.setLevel("com.custom.log1", "INFO", true);
		Log4JUtil.setLevel("com.custom.log2", "WARN", true);

		Map<String, String> customLogMap = Log4JUtil.getCustomLogSettings();

		Assert.assertEquals(customLogMap.toString(), 2, customLogMap.size());
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
			"The original level should be WARN by configuration", "WARN",
			Log4JUtil.getOriginalLevel(
				"com.liferay.portal.internal.servlet.MainServlet"));

		Assert.assertEquals(
			"The original level should be WARN by configuration", "WARN",
			Log4JUtil.getOriginalLevel(LoggerName.LOGGER_WARN.toString()));

		Assert.assertEquals(
			"The original level should be ALL for Logger not configured or " +
				"created",
			"ALL", Log4JUtil.getOriginalLevel(StringUtil.randomString()));
	}

	@Test
	public void testInitLog4J() {
		Log4JUtil.initLog4J(
			ServerDetector.getServerId(), PropsUtil.get(PropsKeys.LIFERAY_HOME),
			_classLoader, new Log4jLogFactoryImpl(),
			HashMapBuilder.putAll(
				new HashMap<String, String>()
			).put(
				"com.liferay.portal.util.PropsUtil", "WARN"
			).build());

		_assertLog4JLevel(
			"WARN", LogFactoryUtil.getLog("com.liferay.portal.util.PropsUtil"));

		_assertLog4JLevel(
			"WARN",
			LogFactoryUtil.getLog(
				"com.liferay.portal.internal.servlet.MainServlet"));

		_assertLog4JLevels();

		_assertJDKLogLevels();
	}

	@Test
	public void testSetLevel() {
		Log4JUtil.configureLog4J(_classLoader);

		Log log = LogFactoryUtil.getLog(LoggerName.LOGGER_WARN.toString());

		java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger(
			LoggerName.LOGGER_WARN.toString());

		_assertLog4JLevel("WARN", log);
		_assertJDKLogLevel(Level.WARNING, jdkLogger);

		Log4JUtil.setLevel(LoggerName.LOGGER_WARN.toString(), "DEBUG", false);

		_assertLog4JLevel("DEBUG", log);
		_assertJDKLogLevel(Level.FINE, jdkLogger);

		Log childLog = LogFactoryUtil.getLog("com.test.parent.child");

		java.util.logging.Logger childJDKLogger =
			java.util.logging.Logger.getLogger("com.test.parent.child");

		_assertLog4JLevel("INFO", childLog);

		Assert.assertTrue(
			"The child logger should be at level INFO",
			childJDKLogger.isLoggable(Level.INFO) &&
			!childJDKLogger.isLoggable(Level.CONFIG));

		Log4JUtil.setLevel("com.test.parent", "DEBUG", false);

		_assertLog4JLevel("DEBUG", childLog);
		Assert.assertTrue(
			"The child logger should be at level FINE",
			childJDKLogger.isLoggable(Level.FINE) &&
			!childJDKLogger.isLoggable(Level.FINER));
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

	private void _assertJDKLogLevel(
		Level expectedLevel, java.util.logging.Logger jdkLog) {

		Level actualLevel = jdkLog.getLevel();

		Assert.assertEquals(
			"Logging level is wrong", expectedLevel, actualLevel);
	}

	private void _assertJDKLogLevels() {
		_assertJDKLogLevel(
			Level.INFO,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_ALL.toString()));
		_assertJDKLogLevel(
			Level.INFO,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_OFF.toString()));
		_assertJDKLogLevel(
			Level.INFO,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_FATAL.toString()));
		_assertJDKLogLevel(
			Level.SEVERE,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_ERROR.toString()));
		_assertJDKLogLevel(
			Level.WARNING,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_WARN.toString()));
		_assertJDKLogLevel(
			Level.INFO,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_INFO.toString()));
		_assertJDKLogLevel(
			Level.FINE,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_DEBUG.toString()));
		_assertJDKLogLevel(
			Level.INFO,
			java.util.logging.Logger.getLogger(
				LoggerName.LOGGER_TRACE.toString()));
	}

	private void _assertLog4JLevel(String expectedLevel, Log log) {
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

	private void _assertLog4JLevels() {
		_assertLog4JLevel(
			"ALL", LogFactoryUtil.getLog(LoggerName.LOGGER_ALL.toString()));
		_assertLog4JLevel(
			"OFF", LogFactoryUtil.getLog(LoggerName.LOGGER_OFF.toString()));
		_assertLog4JLevel(
			"FATAL", LogFactoryUtil.getLog(LoggerName.LOGGER_FATAL.toString()));
		_assertLog4JLevel(
			"ERROR", LogFactoryUtil.getLog(LoggerName.LOGGER_ERROR.toString()));
		_assertLog4JLevel(
			"WARN", LogFactoryUtil.getLog(LoggerName.LOGGER_WARN.toString()));
		_assertLog4JLevel(
			"INFO", LogFactoryUtil.getLog(LoggerName.LOGGER_INFO.toString()));
		_assertLog4JLevel(
			"DEBUG", LogFactoryUtil.getLog(LoggerName.LOGGER_DEBUG.toString()));
		_assertLog4JLevel(
			"TRACE", LogFactoryUtil.getLog(LoggerName.LOGGER_TRACE.toString()));
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

	private class TestClassLoader extends ClassLoader {

		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			if (name.equals("META-INF/portal-log4j-ext.xml")) {
				return Collections.enumeration(Collections.<URL>emptyList());
			}

			return super.getResources(name);
		}

		private TestClassLoader() {
			super(_classLoader);
		}

	}

}