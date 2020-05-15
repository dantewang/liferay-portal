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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Hai Yu
 */
public class Log4JOutputTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		PropsUtil.setProps(new PropsImpl());

		_originalOutputStream = System.out;

		_printStream = new TeePrintStream(
			_originalOutputStream, _unsyncStringWriter);

		System.setOut(_printStream);

		ClassLoader classLoader = Log4JOutputTest.class.getClassLoader();

		_prepareConfigurationFile(classLoader);

		Log4JUtil.configureLog4J(classLoader);

		LogFactoryUtil.setLogFactory(new Log4jLogFactoryImpl());

		Log4JUtil.setLevel(Log4JOutputTest.class.getName(), "TRACE", false);

		_log = LogFactoryUtil.getLog(Log4JOutputTest.class);

		_unsyncStringWriter.reset();
	}

	@AfterClass
	public static void tearDownClass() {
		System.setOut(_originalOutputStream);

		Log4JUtil.shutdownLog4J();
	}

	@Test
	public void testConsoleOutput() {
		_testConsoleOutput("TRACE", "TRACE message", null);
		_testConsoleOutput("DEBUG", "DEBUG message", null);
		_testConsoleOutput("INFO", "INFO message", null);
		_testConsoleOutput("WARN", "WARN message", null);
		_testConsoleOutput("ERROR", "ERROR message", null);
		_testConsoleOutput("FATAL", "FATAL message", null);

		_testConsoleOutput("TRACE", "TRACE message", new TestException());
		_testConsoleOutput("DEBUG", "DEBUG message", new TestException());
		_testConsoleOutput("INFO", "INFO message", new TestException());
		_testConsoleOutput("WARN", "WARN message", new TestException());
		_testConsoleOutput("ERROR", "ERROR message", new TestException());
		_testConsoleOutput("FATAL", "FATAL message", new TestException());

		_testConsoleOutput("TRACE", null, new TestException());
		_testConsoleOutput("DEBUG", null, new TestException());
		_testConsoleOutput("INFO", null, new TestException());
		_testConsoleOutput("WARN", null, new TestException());
		_testConsoleOutput("ERROR", null, new TestException());
		_testConsoleOutput("FATAL", null, new TestException());
	}

	@Test
	public void testFileOutput() throws Exception {
		_testFileOutput("TRACE", "TRACE message", null);
		_testFileOutput("DEBUG", "DEBUG message", null);
		_testFileOutput("INFO", "INFO message", null);
		_testFileOutput("WARN", "WARN message", null);
		_testFileOutput("ERROR", "ERROR message", null);
		_testFileOutput("FATAL", "FATAL message", null);

		_testFileOutput("TRACE", "TRACE message", new TestException());
		_testFileOutput("DEBUG", "DEBUG message", new TestException());
		_testFileOutput("INFO", "INFO message", new TestException());
		_testFileOutput("WARN", "WARN message", new TestException());
		_testFileOutput("ERROR", "ERROR message", new TestException());
		_testFileOutput("FATAL", "FATAL message", new TestException());

		_testFileOutput("TRACE", null, new TestException());
		_testFileOutput("DEBUG", null, new TestException());
		_testFileOutput("INFO", null, new TestException());
		_testFileOutput("WARN", null, new TestException());
		_testFileOutput("ERROR", null, new TestException());
		_testFileOutput("FATAL", null, new TestException());
	}

	private static void _prepareConfigurationFile(ClassLoader classLoader)
		throws Exception {

		URL url = classLoader.getResource("META-INF/portal-log4j-ext.xml");

		try (InputStream inputStream = url.openStream()) {
			String urlContent = StreamUtil.toString(inputStream);

			Path tempLogDir = Files.createTempDirectory(
				Log4JOutputTest.class.getName());

			_tempLogDir = tempLogDir.toFile();

			_tempLogDir.deleteOnExit();

			urlContent = StringUtil.replace(
				urlContent, "@temp_dir@",
				StringUtil.replace(tempLogDir.toString(), '\\', '/'));

			Files.write(Paths.get(url.toURI()), urlContent.getBytes());
		}
	}

	private void _assertTextLog(
		String expectedLevel, String expectedMessage, String actualOutput) {

		Matcher dateMatcher = _datePattern.matcher(
			actualOutput.substring(0, _DATE_FORMAT.length()));

		Assert.assertTrue(
			"Output date format should be yyyy-MM-dd HH:mm:ss.SSS",
			dateMatcher.matches());

		int x = actualOutput.indexOf(StringPool.OPEN_BRACKET);

		String actualLevel = actualOutput.substring(_DATE_FORMAT.length(), x);

		Assert.assertEquals(
			"Expected level is " + expectedLevel, expectedLevel,
			actualLevel.trim());

		Thread currentThread = Thread.currentThread();

		String expectedThreadName = currentThread.getName();

		int y = actualOutput.indexOf(StringPool.CLOSE_BRACKET, x);

		Assert.assertEquals(
			"Expected thread name is " + expectedThreadName, expectedThreadName,
			actualOutput.substring(x + 1, y));

		x = actualOutput.indexOf(StringPool.COLON, y);

		Assert.assertEquals(
			"Expected log output class simple name is " +
				Log4JOutputTest.class.getSimpleName(),
			Log4JOutputTest.class.getSimpleName(),
			actualOutput.substring(y + 2, x));

		y = actualOutput.indexOf(StringPool.CLOSE_BRACKET, x);

		String[] outputLines = StringUtil.splitLines(actualOutput);

		String actualMessage = actualOutput.substring(
			y + 2, outputLines[0].length());

		if (expectedMessage == null) {
			Assert.assertTrue(
				"Expected log message is " + expectedMessage,
				actualMessage.equals("null"));
		}
		else {
			Assert.assertEquals(
				"Expected log message is " + expectedMessage, expectedMessage,
				actualMessage);
		}

		if (outputLines.length > 1) {
			Assert.assertEquals(
				"Expected output exception should be " +
					TestException.class.getName(),
				TestException.class.getName(), outputLines[1]);

			String actualFirstPrefixStackTraceElement = outputLines[2].trim();

			Assert.assertTrue(
				actualFirstPrefixStackTraceElement.startsWith(
					"at " + Log4JOutputTest.class.getName()));
		}
	}

	private void _assertXmlLog(
			String expectedLevel, String expectedMessage, String actualOutput)
		throws Exception {

		String log4jEventLoggerProperty = "logger=\"";

		int x = actualOutput.indexOf(log4jEventLoggerProperty);

		int y = actualOutput.indexOf(
			StringPool.QUOTE, x + log4jEventLoggerProperty.length());

		Assert.assertEquals(
			"logger should be " + Log4JOutputTest.class.getName(),
			Log4JOutputTest.class.getName(),
			actualOutput.substring(x + log4jEventLoggerProperty.length(), y));

		String log4jEventTimestampProperty = "timestamp=\"";

		x = actualOutput.indexOf(log4jEventTimestampProperty);

		y = actualOutput.indexOf(
			StringPool.QUOTE, x + log4jEventTimestampProperty.length());

		String actualTimestamp = actualOutput.substring(
			x + log4jEventTimestampProperty.length(), y);

		Date actualTimestampDate = new Date(Long.valueOf(actualTimestamp));

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(_DATE_FORMAT);

		Matcher dateMatcher = _datePattern.matcher(
			simpleDateFormat.format(actualTimestampDate));

		Assert.assertTrue(
			"Output date format should be yyyy-MM-dd HH:mm:ss.SSS",
			dateMatcher.matches());

		String log4jEventLevelProperty = "level=\"";

		x = actualOutput.indexOf(log4jEventLevelProperty);

		y = actualOutput.indexOf(
			StringPool.QUOTE, x + log4jEventLevelProperty.length());

		Assert.assertEquals(
			"Expected level is " + expectedLevel, expectedLevel,
			actualOutput.substring(x + log4jEventLevelProperty.length(), y));

		String log4jEventThreadProperty = "thread=\"";

		x = actualOutput.indexOf(log4jEventThreadProperty);

		y = actualOutput.indexOf(
			StringPool.QUOTE, x + log4jEventThreadProperty.length());

		Thread currentThread = Thread.currentThread();

		Assert.assertEquals(
			"Expected thread name is " + currentThread.getName(),
			currentThread.getName(),
			actualOutput.substring(x + log4jEventThreadProperty.length(), y));

		String log4jMessageTag = "CDATA[";

		x = actualOutput.indexOf(log4jMessageTag);

		y = actualOutput.indexOf(
			StringPool.CLOSE_BRACKET, x + log4jMessageTag.length());

		String actualMessage = actualOutput.substring(
			x + log4jMessageTag.length(), y);

		if (expectedMessage == null) {
			Assert.assertTrue(
				"Expected log message is " + expectedMessage,
				Validator.isBlank(actualMessage));
		}
		else {
			Assert.assertTrue(
				"Expected log message is " + expectedMessage,
				StringUtil.equals(expectedMessage, actualMessage));
		}

		String log4jthrowableTag = "<log4j:throwable><![CDATA[";

		x = actualOutput.indexOf(log4jthrowableTag);

		if (x > -1) {
			y = actualOutput.indexOf(
				StringPool.CLOSE_BRACKET, x + log4jthrowableTag.length());

			String actualThrowable = actualOutput.substring(
				x + log4jthrowableTag.length(), y);

			String[] outputLines = StringUtil.splitLines(actualThrowable);

			Assert.assertEquals(
				"Expected output exception should be " +
					TestException.class.getName(),
				TestException.class.getName(), outputLines[0]);

			String actualFirstPrefixStackTraceElement = outputLines[1].trim();

			Assert.assertTrue(
				actualFirstPrefixStackTraceElement.startsWith(
					"at " + Log4JOutputTest.class.getName()));
		}

		String log4jlocationInfoClassProperty = "class=\"";

		x = actualOutput.indexOf(log4jlocationInfoClassProperty);

		y = actualOutput.indexOf(
			StringPool.QUOTE, x + log4jlocationInfoClassProperty.length());

		Assert.assertEquals(
			"Expected class is " + Log4JOutputTest.class.getName(),
			Log4JOutputTest.class.getName(),
			actualOutput.substring(
				x + log4jlocationInfoClassProperty.length(), y));

		String log4jlocationInfoFileProperty = "file=\"";

		x = actualOutput.indexOf(log4jlocationInfoFileProperty);

		y = actualOutput.indexOf(
			StringPool.QUOTE, x + log4jlocationInfoFileProperty.length());

		String expectedFileName =
			Log4JOutputTest.class.getSimpleName() + ".java";

		Assert.assertEquals(
			"Expected output log file is " + expectedFileName, expectedFileName,
			actualOutput.substring(
				x + log4jlocationInfoFileProperty.length(), y));
	}

	private void _outputLog(String level, String message, Throwable throwable) {
		if (level.equals("TRACE")) {
			if ((message == null) && (throwable != null)) {
				_log.trace(throwable);
			}
			else if ((message != null) && (throwable == null)) {
				_log.trace(message);
			}
			else {
				_log.trace(message, throwable);
			}
		}
		else if (level.equals("DEBUG")) {
			if ((message == null) && (throwable != null)) {
				_log.debug(throwable);
			}
			else if ((message != null) && (throwable == null)) {
				_log.debug(message);
			}
			else {
				_log.debug(message, throwable);
			}
		}
		else if (level.equals("INFO")) {
			if ((message == null) && (throwable != null)) {
				_log.info(throwable);
			}
			else if ((message != null) && (throwable == null)) {
				_log.info(message);
			}
			else {
				_log.info(message, throwable);
			}
		}
		else if (level.equals("WARN")) {
			if ((message == null) && (throwable != null)) {
				_log.warn(throwable);
			}
			else if ((message != null) && (throwable == null)) {
				_log.warn(message);
			}
			else {
				_log.warn(message, throwable);
			}
		}
		else if (level.equals("ERROR")) {
			if ((message == null) && (throwable != null)) {
				_log.error(throwable);
			}
			else if ((message != null) && (throwable == null)) {
				_log.error(message);
			}
			else {
				_log.error(message, throwable);
			}
		}
		else {
			if ((message == null) && (throwable != null)) {
				_log.fatal(throwable);
			}
			else if ((message != null) && (throwable == null)) {
				_log.fatal(message);
			}
			else {
				_log.fatal(message, throwable);
			}
		}
	}

	private void _testConsoleOutput(
		String level, String message, Throwable throwable) {

		_outputLog(level, message, throwable);

		try {
			_assertTextLog(level, message, _unsyncStringWriter.toString());
		}
		finally {
			_unsyncStringWriter.reset();
		}
	}

	private void _testFileOutput(
			String level, String message, Throwable throwable)
		throws Exception {

		for (File logFile : _tempLogDir.listFiles()) {
			try (FileWriter fileWriter = new FileWriter(logFile, false)) {
				fileWriter.write("");
			}
		}

		_outputLog(level, message, throwable);

		try {
			for (File file : _tempLogDir.listFiles()) {
				String fileName = file.getName();

				if (fileName.endsWith(".log")) {
					Matcher matcher = _textFileNamePattern.matcher(fileName);

					Assert.assertTrue(
						"Text log file name should match the pattern liferay." +
							"yyyy-MM-dd.log, but actual name is " + fileName,
						matcher.matches());

					_assertTextLog(
						level, message,
						StreamUtil.toString(new FileInputStream(file)));
				}
				else {
					Matcher matcher = _xmlFileNamePattern.matcher(fileName);

					Assert.assertTrue(
						"XML log file name should match the pattern liferay." +
							"yyyy-MM-dd.xml, but actual name is " + fileName,
						matcher.matches());

					_assertXmlLog(
						level, message,
						StreamUtil.toString(new FileInputStream(file)));
				}
			}
		}
		finally {
			_unsyncStringWriter.reset();
		}
	}

	private static final String _DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	private static Log _log;

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
			String content = new String(bytes);

			if (!content.contains(
					"[" + Log4JOutputTest.class.getSimpleName() + ":")) {

				super.write(bytes, offset, length);
			}

			_unsyncStringWriter.write(content.toCharArray(), offset, length);
		}

		@Override
		public void write(int integer) {
			super.write(integer);

			_unsyncStringWriter.write(integer);
		}

		private final UnsyncStringWriter _unsyncStringWriter;

	}

	private class TestException extends Exception {
	}

}