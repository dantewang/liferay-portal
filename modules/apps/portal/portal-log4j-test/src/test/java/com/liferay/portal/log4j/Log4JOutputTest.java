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

		String[] outputLines = StringUtil.splitLines(actualOutput);

		Assert.assertTrue("Log should be output", outputLines.length > 0);

		String logLine = outputLines[0];

		Matcher dateMatcher = _datePattern.matcher(
			logLine.substring(0, _DATE_FORMAT.length()));

		Assert.assertTrue(
			"Output date format should be yyyy-MM-dd HH:mm:ss.SSS",
			dateMatcher.matches());

		Thread currentThread = Thread.currentThread();

		Pattern contentPattern = Pattern.compile(
			StringBundler.concat(
				" ", expectedLevel, " {1,2}\\[", currentThread.getName(), "\\]",
				"\\[", Log4JOutputTest.class.getSimpleName(), ":\\d+\\] ",
				expectedMessage));

		Matcher contentMatcher = contentPattern.matcher(
			logLine.substring(_DATE_FORMAT.length()));

		String expectedOutput = StringBundler.concat(
			"", expectedLevel, " [", currentThread.getName(), "][",
			Log4JOutputTest.class.getSimpleName(), ":lineNumber] ",
			expectedMessage);

		Assert.assertTrue(
			"Expected output content should be " + expectedOutput,
			contentMatcher.matches());

		if (outputLines.length > 1) {
			Assert.assertEquals(
				"Expected output exception should be " +
					TestException.class.getName(),
				TestException.class.getName(), outputLines[1]);
		}
	}

	private void _assertXmlLog(
			String expectedLevel, String expectedMethodName,
			String expectedMessage, boolean thrown, String actualOutput)
		throws Exception {

		String consoleOutput = _unsyncStringWriter.toString();

		String date = consoleOutput.substring(0, _DATE_FORMAT.length());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(_DATE_FORMAT);

		Date logDate = simpleDateFormat.parse(date);

		String content = consoleOutput.substring(_DATE_FORMAT.length());

		int index = content.indexOf(":");

		content = content.substring(index);

		index = content.indexOf("]");

		String lineNumber = content.substring(1, index);

		Thread currentThread = Thread.currentThread();

		if (thrown) {
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

		StringBundler sb = new StringBundler(25);

		sb.append("<log4j:event logger=\"");
		sb.append(Log4JOutputTest.class.getName());
		sb.append("\" timestamp=\"");
		sb.append(logDate.getTime());
		sb.append("\" level=\"");
		sb.append(expectedLevel);
		sb.append("\" thread=\"");
		sb.append(currentThread.getName());
		sb.append("\">\r\n");
		sb.append("<log4j:message><![CDATA[");

		if (expectedMessage != null) {
			sb.append(expectedMessage);
		}

		sb.append("]]></log4j:message>\r\n");

		if (thrown) {
			sb.append("<log4j:throwable><![CDATA[");
			sb.append(TestException.class.getName());
			sb.append("]]></log4j:throwable>\r\n");
		}

		sb.append("<log4j:locationInfo class=\"");
		sb.append(Log4JOutputTest.class.getName());
		sb.append("\" method=\"");
		sb.append(expectedMethodName);
		sb.append("\" file=\"");
		sb.append(Log4JOutputTest.class.getSimpleName());
		sb.append(".java\" line=\"");
		sb.append(lineNumber);
		sb.append("\"/>\r\n");
		sb.append("</log4j:event>\r\n\r\n");

		Assert.assertEquals(
			"LogMessage should be " + sb.toString(), sb.toString(),
			actualOutput);
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
						level, "_outputLog", message, throwable != null,
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