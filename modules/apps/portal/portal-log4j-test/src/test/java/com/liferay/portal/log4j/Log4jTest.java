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

package com.liferay.portal.log4j;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.log.Log4jLogFactoryImpl;
import com.liferay.portal.util.PropsImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Hai Yu
 */
public class Log4jTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		PropsUtil.setProps(new PropsImpl());

		_originalOutputStream = System.out;

		_printStream = new TeePrintStream(
			_originalOutputStream, _unsyncStringWriter);

		System.setOut(_printStream);

		ClassLoader classLoader = Log4jTest.class.getClassLoader();

		URL url = classLoader.getResource(
			"com/liferay/portal/log4j/dependencies/log4j.xml");

		String urlContent = "";

		try (InputStream inputStream = url.openStream()) {
			urlContent = StreamUtil.toString(inputStream);
		}

		Path tempLogDir = Files.createTempDirectory(Log4jTest.class.getName());

		_tempLogDir = tempLogDir.toFile();

		_tempLogDir.deleteOnExit();

		urlContent = StringUtil.replace(
			urlContent, "@temp_dir@",
			StringUtil.replace(tempLogDir.toString(), '\\', '/'));

		Files.write(Paths.get(url.toURI()), urlContent.getBytes());

		Log4JUtil.configureLog4J(url);

		LogFactoryUtil.setLogFactory(new Log4jLogFactoryImpl());

		_unsyncStringWriter.reset();
	}

	@AfterClass
	public static void tearDownClass() {
		System.setOut(_originalOutputStream);

		LogManager.shutdown();
	}

	@Test
	public void testConsoleAppenderWithOutputMessage() {
		_testConsoleAppender("TRACE", true, false);

		_testConsoleAppender("DEBUG", true, false);

		_testConsoleAppender("INFO", true, false);

		_testConsoleAppender("WARN", true, false);

		_testConsoleAppender("ERROR", true, false);

		_testConsoleAppender("FATAL", true, false);
	}

	@Test
	public void testConsoleAppenderWithOutputMessageAndThrowException() {
		_testConsoleAppender("TRACE", true, true);

		_testConsoleAppender("DEBUG", true, true);

		_testConsoleAppender("INFO", true, true);

		_testConsoleAppender("WARN", true, true);

		_testConsoleAppender("ERROR", true, true);

		_testConsoleAppender("FATAL", true, true);
	}

	@Test
	public void testConsoleAppenderWithThrowException() {
		_testConsoleAppender("TRACE", false, true);

		_testConsoleAppender("DEBUG", false, true);

		_testConsoleAppender("INFO", false, true);

		_testConsoleAppender("WARN", false, true);

		_testConsoleAppender("ERROR", false, true);

		_testConsoleAppender("FATAL", false, true);
	}

	@Test
	public void testFileAppenderWithOutputMessage() throws Exception {
		_testFileAppender("TRACE", true, false);

		_testFileAppender("DEBUG", true, false);

		_testFileAppender("INFO", true, false);

		_testFileAppender("WARN", true, false);

		_testFileAppender("ERROR", true, false);

		_testFileAppender("FATAL", true, false);
	}

	@Test
	public void testFileAppenderWithOutputMessageAndThrowException()
		throws Exception {

		_testFileAppender("TRACE", true, true);

		_testFileAppender("DEBUG", true, true);

		_testFileAppender("INFO", true, true);

		_testFileAppender("WARN", true, true);

		_testFileAppender("ERROR", true, true);

		_testFileAppender("FATAL", true, true);
	}

	@Test
	public void testFileAppenderWithThrowException() throws Exception {
		_testFileAppender("TRACE", false, true);

		_testFileAppender("DEBUG", false, true);

		_testFileAppender("INFO", false, true);

		_testFileAppender("WARN", false, true);

		_testFileAppender("ERROR", false, true);

		_testFileAppender("FATAL", false, true);
	}

	@Test
	public void testGetOriginalLevel() {
		String level = Log4JUtil.getOriginalLevel(
			LoggerName.LOGGER_INFO.toString());

		Assert.assertEquals("The original level should be INFO", "INFO", level);

		level = Log4JUtil.getOriginalLevel(LoggerName.LOGGER_DEBUG.toString());

		Assert.assertEquals(
			"The original level should be DEBUG", "DEBUG", level);
	}

	@Test
	public void testLoggerEnabled() {
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

	@Test
	public void testSetLevel() {
		Log log = LogFactoryUtil.getLog(LoggerName.LOGGER_WARN.toString());

		Assert.assertTrue("Warn level should be enabled", log.isWarnEnabled());

		_setLevel(LoggerName.LOGGER_WARN.toString(), "DEBUG");

		Assert.assertTrue(
			"DEBUG level should be enabled", log.isDebugEnabled());

		_setLevel(LoggerName.LOGGER_WARN.toString(), "WARN");

		Log childLog = LogFactoryUtil.getLog("com.test.parent.child");

		Assert.assertTrue(
			"INFO level should be enabled", childLog.isInfoEnabled());
		Assert.assertFalse(
			"DEBUG level should be not enabled", childLog.isDebugEnabled());

		_setLevel("com.test.parent", "DEBUG");

		Assert.assertTrue(
			"DEBUG level should be enabled", childLog.isDebugEnabled());
	}

	private static void _setLevel(String loggerName, String level) {
		Log4JUtil.setLevel(loggerName, level, false);
	}

	private void _assertLog(
		String actualOutput, String level, String renderMessage) {

		String[] logMessages = StringUtil.splitLines(actualOutput);

		Assert.assertTrue("Log should be outputed", logMessages.length > 0);

		String actualOutputMessage = logMessages[0];

		String datePattern = "yyyy-MM-dd HH:mm:ss.SSS";

		Matcher matcher = _datePattern.matcher(
			actualOutputMessage.substring(0, datePattern.length()));

		Assert.assertTrue(
			"Output date format should be yyyy-MM-dd HH:mm:ss.SSS",
			matcher.matches());

		Thread currentThread = Thread.currentThread();

		Pattern contentPattern = Pattern.compile(
			StringBundler.concat(
				" ", level, " {1,2}\\[", currentThread.getName(), "\\]",
				"\\[Log4jTest:\\d+\\] ", renderMessage));

		String content = actualOutputMessage.substring(datePattern.length());

		matcher = contentPattern.matcher(content);

		String expectedOutput = StringBundler.concat(
			"", level, " [", currentThread.getName(),
			"][Log4jTest:lineNumber] ", renderMessage);

		Assert.assertTrue(
			"Expected output content should be " + expectedOutput,
			matcher.matches());

		if (logMessages.length > 1) {
			Assert.assertEquals(
				"Expected output exception should be " +
					TestException.class.getName(),
				TestException.class.getName(), logMessages[1]);
		}
	}

	private void _assertXmlLog(
			String actualOutput, String level, String outputLogMethodName,
			String renderMessage, boolean throwable)
		throws Exception {

		String consoleOutput = _unsyncStringWriter.toString();

		String datePattern = "yyyy-MM-dd HH:mm:ss.SSS";

		String date = consoleOutput.substring(0, datePattern.length());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

		Date logDate = simpleDateFormat.parse(date);

		String content = consoleOutput.substring(datePattern.length());

		int index = content.indexOf(":");

		content = content.substring(index);

		index = content.indexOf("]");

		String lineNumber = content.substring(1, index);

		Thread currentThread = Thread.currentThread();

		if (throwable) {
			String startThrowableTag = "<log4j:throwable><![CDATA[";
			String endThrowableTag = "]]></log4j:throwable>";

			int start = actualOutput.indexOf(startThrowableTag);
			int end = actualOutput.indexOf(endThrowableTag);

			String stackTraceContent = actualOutput.substring(
				start + startThrowableTag.length(), end);

			String[] stackTrace = StringUtil.splitLines(stackTraceContent);

			Assert.assertEquals(
				"Expected output exception should include " +
					TestException.class.getName(),
				TestException.class.getName(), stackTrace[0]);

			actualOutput = StringUtil.replace(
				actualOutput, stackTraceContent, TestException.class.getName());
		}

		StringBundler sb = new StringBundler(23);

		sb.append("<log4j:event logger=\"");
		sb.append(Log4jTest.class.getName());
		sb.append("\" timestamp=\"");
		sb.append(logDate.getTime());
		sb.append("\" level=\"");
		sb.append(level);
		sb.append("\" thread=\"");
		sb.append(currentThread.getName());
		sb.append("\">\r\n");
		sb.append("<log4j:message><![CDATA[");

		if (renderMessage != null) {
			sb.append(renderMessage);
		}

		sb.append("]]></log4j:message>\r\n");

		if (throwable) {
			sb.append("<log4j:throwable><![CDATA[");
			sb.append(TestException.class.getName());
			sb.append("]]></log4j:throwable>\r\n");
		}

		sb.append("<log4j:locationInfo class=\"");
		sb.append(Log4jTest.class.getName());
		sb.append("\" method=\"");
		sb.append(outputLogMethodName);
		sb.append("\" file=\"Log4jTest.java\" line=\"");
		sb.append(lineNumber);
		sb.append("\"/>\r\n");
		sb.append("</log4j:event>\r\n\r\n");

		Assert.assertEquals(
			"LogMessage should be " + sb.toString(), sb.toString(),
			actualOutput);
	}

	private void _outputLog(
		String level, String renderMessage, boolean message,
		boolean throwable) {

		Log log = LogFactoryUtil.getLog("level." + level.toLowerCase());

		if (message && throwable) {
			ReflectionTestUtil.invoke(
				log, level.toLowerCase(),
				new Class[]{Object.class, Throwable.class}, renderMessage,
				new TestException());
		}
		else if (message && !throwable) {
			ReflectionTestUtil.invoke(
				log, level.toLowerCase(), new Class[]{Object.class},
				renderMessage);
		}
		else {
			ReflectionTestUtil.invoke(
				log, level.toLowerCase(), new Class[]{Throwable.class},
				new TestException());
		}
	}

	private void _testConsoleAppender(
		String level, boolean message, boolean throwable) {

		String renderMessage = null;

		if (message) {
			renderMessage = level + " Message";
		}

		_outputLog(level, renderMessage, message, throwable);

		try {
			_assertLog(_unsyncStringWriter.toString(), level, renderMessage);
		}
		finally {
			_unsyncStringWriter.reset();
		}
	}

	private void _testFileAppender(
			String level, boolean message, boolean throwable)
		throws Exception {

		String renderMessage = null;

		if (message) {
			renderMessage = level + "message";
		}

		for (File logFile : _tempLogDir.listFiles()) {
			try (FileWriter fileWriter = new FileWriter(logFile, false)) {
				fileWriter.write("");
			}
		}

		String outputLogMethodName = "_outputLog";

		_outputLog(level, renderMessage, message, throwable);

		String content = "";

		Matcher matcher = null;

		try {
			for (File file : _tempLogDir.listFiles()) {
				String fileName = file.getName();

				if (fileName.endsWith(".log")) {
					matcher = _textFileNamePattern.matcher(fileName);

					Assert.assertTrue(
						"test file name should be " + fileName,
						matcher.matches());

					_assertLog(
						StreamUtil.toString(new FileInputStream(file)), level,
						renderMessage);
				}
				else {
					matcher = _xmlFileNamePattern.matcher(fileName);

					Assert.assertTrue(
						"xml file name should be " + fileName,
						matcher.matches());

					content = StreamUtil.toString(new FileInputStream(file));

					_assertXmlLog(
						content, level, outputLogMethodName, renderMessage,
						throwable);
				}
			}
		}
		finally {
			_unsyncStringWriter.reset();
		}
	}

	private static final Pattern _datePattern = Pattern.compile(
		"\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d.\\d\\d\\d");
	private static PrintStream _originalOutputStream;
	private static PrintStream _printStream;
	private static File _tempLogDir;
	private static final Pattern _textFileNamePattern = Pattern.compile(
		"liferay.\\d\\d\\d\\d-\\d\\d-\\d\\d.log");
	private static final UnsyncStringWriter _unsyncStringWriter =
		new UnsyncStringWriter();
	private static final Pattern _xmlFileNamePattern = Pattern.compile(
		"liferay.\\d\\d\\d\\d-\\d\\d-\\d\\d.xml");

	private static class TeePrintStream extends PrintStream {

		public TeePrintStream(
			OutputStream outputStream, UnsyncStringWriter unsyncStringWriter) {

			super(outputStream);

			_unsyncStringWriter = unsyncStringWriter;
		}

		@Override
		public void close() {
		}

		@Override
		public void flush() {
		}

		@Override
		public void write(byte[] bytes) throws IOException {
			super.write(bytes);

			String content = new String(bytes);

			_unsyncStringWriter.write(content.toCharArray());
		}

		@Override
		public void write(byte[] bytes, int offset, int length) {
			super.write(bytes, offset, length);

			String content = new String(bytes);

			_unsyncStringWriter.write(content.toCharArray(), offset, length);
		}

		@Override
		public void write(int integer) {
			super.write(integer);

			_unsyncStringWriter.write(integer);
		}

		private final UnsyncStringWriter _unsyncStringWriter;

	}

	private static class TestException extends Exception {
	}

	private enum LoggerName {

		LOGGER_ALL("level.all"), LOGGER_DEBUG("level.debug"),
		LOGGER_ERROR("level.error"), LOGGER_FATAL("level.fatal"),
		LOGGER_INFO("level.info"), LOGGER_OFF("level.off"),
		LOGGER_TRACE("level.trace"), LOGGER_WARN("level.warn");

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