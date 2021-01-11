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

		Assert.assertTrue(
			"The logger MainServlet should be INFO level", log.isInfoEnabled());

		tempFile.renameTo(file);

		Log4JUtil.configureLog4J(_classLoader);

		Assert.assertTrue(
			"The logger MainServlet should be WARN level",
			log.isWarnEnabled() && !log.isInfoEnabled());

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
			"The logger com.custom.log1 level shoule be INFO", "INFO",
			customLogMap.get("com.custom.log1"));
		Assert.assertEquals(
			"The logger com.custom.log2 level shoule be WARN", "WARN",
			customLogMap.get("com.custom.log2"));
	}

	@Test
	public void testGetOriginalLevel() {
		Log4JUtil.configureLog4J(_classLoader);

		String level = Log4JUtil.getOriginalLevel(
			"com.liferay.portal.internal.servlet.MainServlet");

		Assert.assertEquals("The original level should be WARN", "WARN", level);

		level = Log4JUtil.getOriginalLevel(LoggerName.LOGGER_WARN.toString());

		Assert.assertEquals("The original level should be WARN", "WARN", level);
	}

	@Test
	public void testInitLog4J() {
		String logName = com.liferay.portal.util.PropsUtil.class.getName();

		Log4JUtil.initLog4J(
			ServerDetector.getServerId(), PropsUtil.get(PropsKeys.LIFERAY_HOME),
			_classLoader, new Log4jLogFactoryImpl(),
			HashMapBuilder.putAll(
				new HashMap<String, String>()
			).put(
				logName, "WARN"
			).build());

		Log log = LogFactoryUtil.getLog(logName);

		Assert.assertTrue(
			logName + " logger level should be WARN", log.isWarnEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_INFO.toString());

		Assert.assertTrue(
			"The logger of ServerDetector should be INFO level",
			log.isInfoEnabled());

		log = LogFactoryUtil.getLog(
			"com.liferay.portal.internal.servlet.MainServlet");

		Assert.assertTrue(
			"The logger MainServlet should be WARN level",
			log.isWarnEnabled() && !log.isInfoEnabled());
	}

	@Test
	public void testSetLevel() {
		Log4JUtil.configureLog4J(_classLoader);

		Log log = LogFactoryUtil.getLog(LoggerName.LOGGER_WARN.toString());

		Assert.assertTrue("Warn level should be enabled", log.isWarnEnabled());

		Log4JUtil.setLevel(LoggerName.LOGGER_WARN.toString(), "DEBUG", false);

		Assert.assertTrue(
			"DEBUG level should be enabled", log.isDebugEnabled());

		Log childLog = LogFactoryUtil.getLog("com.test.parent.child");

		Assert.assertTrue(
			"INFO level should be enabled", childLog.isInfoEnabled());
		Assert.assertFalse(
			"DEBUG level should be not enabled", childLog.isDebugEnabled());

		Log4JUtil.setLevel("com.test.parent", "DEBUG", false);

		Assert.assertTrue(
			"DEBUG level should be enabled", childLog.isDebugEnabled());
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
		Log log = LogFactoryUtil.getLog(LoggerName.LOGGER_ALL.toString());

		Assert.assertTrue("Logger should be all enabled", log.isTraceEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_OFF.toString());

		Assert.assertFalse(
			"Setting logger level OFF does not take effect",
			log.isFatalEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_FATAL.toString());

		Assert.assertTrue(
			"Setting logger level FATAL does not take effect",
			log.isFatalEnabled() && !log.isErrorEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_ERROR.toString());

		Assert.assertTrue(
			"Setting logger level ERROR does not take effect",
			log.isErrorEnabled() && !log.isWarnEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_WARN.toString());

		Assert.assertTrue(
			"Setting logger level WARN does not take effect",
			log.isWarnEnabled() && !log.isInfoEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_INFO.toString());

		Assert.assertTrue(
			"Setting logger level INFO does not take effect",
			log.isInfoEnabled() && !log.isDebugEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_DEBUG.toString());

		Assert.assertTrue(
			"Setting logger level DEBUG does not take effect",
			log.isDebugEnabled() && !log.isTraceEnabled());

		log = LogFactoryUtil.getLog(LoggerName.LOGGER_TRACE.toString());

		Assert.assertTrue(
			"Setting logger level TRACE does not take effect",
			log.isTraceEnabled());
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