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

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.NullAppender;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.selector.BasicContextSelector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Hai Yu
 */
public class Log4jConfigUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		LogManager.setFactory(
			new Log4jContextFactory(new BasicContextSelector()));
	}

	@Test
	public void testConfigureLog4J() {
		String loggerName = StringUtil.randomString();

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		Map<String, String> priorities = Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _ALL));

		Assert.assertEquals(
			priorities, Collections.singletonMap(loggerName, _ALL));

		_assertPriority(logger, _ALL);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _DEBUG));

		_assertPriority(logger, _DEBUG);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _ERROR));

		_assertPriority(logger, _ERROR);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _FATAL));

		_assertPriority(logger, _FATAL);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _INFO));

		_assertPriority(logger, _INFO);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _OFF));

		_assertPriority(logger, _OFF);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _TRACE));

		_assertPriority(logger, _TRACE);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _WARN));

		_assertPriority(logger, _WARN);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, "FAKE_LEVEL"));

		_assertPriority(logger, _ERROR);
	}

	@Test
	public void testConfigureLog4JWithAppender() {
		String loggerName = StringUtil.randomString();

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _ERROR));

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		_assertAppenders(logger);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _ERROR, ConsoleAppender.class));

		_assertAppenders(logger, ConsoleAppender.class);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _ERROR, NullAppender.class));

		_assertAppenders(logger, ConsoleAppender.class, NullAppender.class);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _ERROR, ConsoleAppender.class));

		_assertAppenders(logger, ConsoleAppender.class, NullAppender.class);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(
				loggerName, _ERROR, ConsoleAppender.class, NullAppender.class),
			NullAppender.class.getName());

		_assertAppenders(logger, ConsoleAppender.class, NullAppender.class);
	}

	@Test
	public void testConfigureLog4JWithException() {
		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					Log4jConfigUtil.class.getName(), Level.SEVERE)) {

			Log4jConfigUtil.configureLog4J(null);

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"java.lang.NullPointerException", logRecord.getMessage());
		}
	}

	@Test
	public void testGetJDKLevel() {
		Assert.assertEquals(
			"FINE", String.valueOf(Log4jConfigUtil.getJDKLevel(_DEBUG)));
		Assert.assertEquals(
			"SEVERE", String.valueOf(Log4jConfigUtil.getJDKLevel(_ERROR)));
		Assert.assertEquals(
			"INFO", String.valueOf(Log4jConfigUtil.getJDKLevel(_INFO)));
		Assert.assertEquals(
			"WARNING", String.valueOf(Log4jConfigUtil.getJDKLevel(_WARN)));
	}

	@Test
	public void testGetPriorities() {
		String loggerName = StringUtil.randomString();

		Map<String, String> priorities = Log4jConfigUtil.getPriorities();

		Assert.assertFalse(priorities.containsKey(loggerName));

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _WARN));

		priorities = Log4jConfigUtil.getPriorities();

		Assert.assertEquals(
			"The priority should be WARN by configuration", _WARN,
			priorities.get(loggerName));

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, null));

		priorities = Log4jConfigUtil.getPriorities();

		Assert.assertEquals(
			"The level should use its parent level(root logger level is Error)",
			_ERROR, priorities.get(loggerName));
	}

	@Test
	public void testMisc() {
		new Log4jConfigUtil();
	}

	@Test
	public void testSetLevel() {
		String loggerName = StringUtil.randomString();

		Logger logger = (Logger)LogManager.getLogger(loggerName);

		_assertPriority(logger, _ERROR);

		String childLoggerName = loggerName + ".child";

		Logger childLogger = (Logger)LogManager.getLogger(childLoggerName);

		_assertPriority(childLogger, _ERROR);

		Log4jConfigUtil.configureLog4J(
			_generateXMLConfigurationContent(loggerName, _WARN));

		_assertPriority(logger, _WARN);
		_assertPriority(childLogger, _WARN);

		Log4jConfigUtil.setLevel(loggerName, _DEBUG);

		_assertPriority(logger, _DEBUG);
		_assertPriority(childLogger, _DEBUG);

		Log4jConfigUtil.setLevel(childLoggerName, _ERROR);

		_assertPriority(logger, _DEBUG);
		_assertPriority(childLogger, _ERROR);
	}

	@NewEnv(type = NewEnv.Type.JVM)
	@Test
	public void testShutdownLog4J() {
		Logger logger = (Logger)LogManager.getRootLogger();

		Map<String, Appender> appenders = logger.getAppenders();

		Assert.assertTrue(
			"The root logger should include appenders", !appenders.isEmpty());

		Log4jConfigUtil.shutdownLog4J();

		Assert.assertFalse(
			"The root logger should not own appenders after shutting down",
			appenders.isEmpty());
	}

	@Rule
	public final NewEnvTestRule newEnvTestRule = NewEnvTestRule.INSTANCE;

	private void _assertAppenders(Logger logger, Class<?>... appenderTypes) {
		Map<String, Appender> appenders = logger.getAppenders();

		List<String> targetAppenderNames = new ArrayList<>();

		for (String appenderName : appenders.keySet()) {
			targetAppenderNames.add(appenderName);
		}

		Assert.assertEquals(targetAppenderNames.size(), appenderTypes.length);

		for (Class<?> appenderType : appenderTypes) {
			Assert.assertTrue(
				"Missing appender " + appenderType.getName(),
				targetAppenderNames.contains(appenderType.getName()));
		}
	}

	private void _assertPriority(Logger logger, String priority) {
		if (priority.equals(_ALL)) {
			Assert.assertTrue(
				"TRACE should be enabled if logging priority is ALL",
				logger.isTraceEnabled());

			return;
		}

		if (logger.isTraceEnabled()) {
			Assert.assertEquals("Logging priority is wrong", priority, _TRACE);
		}
		else if (logger.isDebugEnabled()) {
			Assert.assertEquals("Logging priority is wrong", priority, _DEBUG);
		}
		else if (logger.isInfoEnabled()) {
			Assert.assertEquals("Logging priority is wrong", priority, _INFO);
		}
		else if (logger.isWarnEnabled()) {
			Assert.assertEquals("Logging priority is wrong", priority, _WARN);
		}
		else if (logger.isErrorEnabled()) {
			Assert.assertEquals("Logging priority is wrong", priority, _ERROR);
		}
		else if (logger.isFatalEnabled()) {
			Assert.assertEquals("Logging priority is wrong", priority, _FATAL);
		}
		else {
			Assert.assertEquals("Logging priority is wrong", priority, _OFF);
		}
	}

	private String _generateXMLConfigurationContent(
		String loggerName, String priority, Class<?>... appenderTypes) {

		StringBundler sb = new StringBundler(
			7 + ((6 * appenderTypes.length) + 2));

		sb.append("<?xml version=\"1.0\"?><Configuration>");

		if (appenderTypes.length > 0) {
			sb.append("<Appenders>");

			for (Class<?> appenderType : appenderTypes) {
				if (appenderType.equals(ConsoleAppender.class)) {
					sb.append("<Console name=\"");
					sb.append(appenderType.getName());
					sb.append("\"><PatternLayout /></Console>");
				}
				else {
					sb.append("<Null name=\"");
					sb.append(appenderType.getName());
					sb.append("\" />");
				}
			}

			sb.append("</Appenders>");
		}

		sb.append("<Loggers><Logger level= \"");
		sb.append(priority);
		sb.append("\" name=\"");
		sb.append(loggerName);
		sb.append("\">");

		for (Class<?> appenderType : appenderTypes) {
			sb.append("<AppenderRef ref=\"");
			sb.append(appenderType.getName());
			sb.append("\" />");
		}

		sb.append("</Logger></Loggers></Configuration>");

		return sb.toString();
	}

	private static final String _ALL = "ALL";

	private static final String _DEBUG = "DEBUG";

	private static final String _ERROR = "ERROR";

	private static final String _FATAL = "FATAL";

	private static final String _INFO = "INFO";

	private static final String _OFF = "OFF";

	private static final String _TRACE = "TRACE";

	private static final String _WARN = "WARN";

}