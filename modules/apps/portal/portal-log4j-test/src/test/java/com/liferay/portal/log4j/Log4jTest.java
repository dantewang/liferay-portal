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

import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.log.Log4jLogFactoryImpl;
import com.liferay.portal.util.PropsImpl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URI;
import java.net.URL;

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

		_printStream = new TeePrintStream(_baos, _originalOutputStream);

		System.setOut(_printStream);

		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		URL url = classLoader.getResource(
			"com/liferay/portal/log4j/dependencies/log4j.xml");

		String urlContent = "";

		try (InputStream inputStream = url.openStream()) {
			byte[] bytes = _getBytes(inputStream);

			urlContent = new String(bytes, StringPool.UTF8);
		}

		String dir = StringUtil.replace(
			System.getProperty("user.dir"), '\\', '/');

		urlContent = StringUtil.replace(urlContent, "@user_dir@", dir);

		_tempFile = new File(System.getProperty("user.dir"), "/log4j.xml");

		try {
			_tempFile.createNewFile();

			DataOutputStream outstream = new DataOutputStream(
				new FileOutputStream(_tempFile));

			outstream.write(urlContent.getBytes());

			outstream.flush();

			outstream.close();

			URI uri = _tempFile.toURI();

			Log4JUtil.configureLog4J(uri.toURL());

			LogFactoryUtil.setLogFactory(new Log4jLogFactoryImpl());
		}
		catch (IOException ioException) {
			throw ioException;
		}
	}

	@AfterClass
	public static void tearDownClass() {
		_printStream.flush();

		_printStream.close();

		System.setOut(_originalOutputStream);

		LogManager.shutdown();

		if (_tempFile != null) {
			_tempFile.delete();
		}

		File logDir = new File(
			StringUtil.replace(System.getProperty("user.dir"), '\\', '/'),
			"logs");

		for (File file : logDir.listFiles()) {
			file.delete();
		}

		logDir.delete();
	}

	@Test
	public void testConsoleAppender() {
		Log log = LogFactoryUtil.getLog(Log4jTest.class);

		log.info("Test Message");

		String[] logMessages = StringUtil.splitLines(_baos.toString());

		String expectedOutput = logMessages[logMessages.length - 1];

		_assertLog(expectedOutput, 143);
	}

	@Test
	public void testGetOriginalLevel() {
		String level = Log4JUtil.getOriginalLevel(_NAMES[5]);

		Assert.assertEquals("The original level should be INFO", level, "INFO");

		level = Log4JUtil.getOriginalLevel(_NAMES[6]);

		Assert.assertEquals(
			"The original level should be DEBUG", level, "DEBUG");
	}

	@Test
	public void testLoggerEnabled() {
		Log log = LogFactoryUtil.getLog(_NAMES[0]);

		Assert.assertTrue("Logger should be all enabled", log.isTraceEnabled());

		log = LogFactoryUtil.getLog(_NAMES[1]);

		Assert.assertFalse(
			"Setting logger level OFF does not take effect",
			log.isTraceEnabled());

		log = LogFactoryUtil.getLog(_NAMES[2]);

		Assert.assertTrue(
			"Setting logger level FATAL does not take effect",
			log.isFatalEnabled() && !log.isErrorEnabled());

		log = LogFactoryUtil.getLog(_NAMES[3]);

		Assert.assertTrue(
			"Setting logger level ERROR does not take effect",
			log.isErrorEnabled() && !log.isWarnEnabled());

		log = LogFactoryUtil.getLog(_NAMES[4]);

		Assert.assertTrue(
			"Setting logger level WARN does not take effect",
			log.isWarnEnabled() && !log.isInfoEnabled());

		log = LogFactoryUtil.getLog(_NAMES[5]);

		Assert.assertTrue(
			"Setting logger level INFO does not take effect",
			log.isInfoEnabled() && !log.isDebugEnabled());

		log = LogFactoryUtil.getLog(_NAMES[6]);

		Assert.assertTrue(
			"Setting logger level DEBUG does not take effect",
			log.isDebugEnabled() && !log.isTraceEnabled());

		log = LogFactoryUtil.getLog(_NAMES[7]);

		Assert.assertTrue(
			"Setting logger level TRACE does not take effect",
			log.isTraceEnabled());
	}

	@Test
	public void testRollingFileAppender() throws Exception {
		Log log = LogFactoryUtil.getLog(Log4jTest.class);

		log.info("Test Message");

		File logDir = new File(
			StringUtil.replace(System.getProperty("user.dir"), '\\', '/'),
			"logs");

		String content = "";

		Matcher matcher = null;

		for (File file : logDir.listFiles()) {
			String fileName = file.getName();

			URI uri = file.toURI();

			URL url = uri.toURL();

			if (fileName.endsWith(".log")) {
				matcher = _textFileNamePattern.matcher(fileName);

				Assert.assertTrue(
					"test file name should be " + fileName, matcher.matches());

				try (InputStream inputStream = url.openStream()) {
					byte[] bytes = _getBytes(inputStream);

					content = new String(bytes, StringPool.UTF8);

					String[] logMessages = StringUtil.splitLines(content);

					_assertLog(logMessages[logMessages.length - 1], 217);
				}
			}
			else {
				matcher = _xmlFileNamePattern.matcher(fileName);

				Assert.assertTrue(
					"xml file name should be " + fileName, matcher.matches());

				try (InputStream inputStream = url.openStream()) {
					byte[] bytes = _getBytes(inputStream);

					content = new String(bytes, StringPool.UTF8);

					int index = content.lastIndexOf("<log4j:event");

					Assert.assertTrue(
						"There is no log meesage output", index > 0);

					_assertXmlLog(content.substring(index), 217);
				}
			}
		}
	}

	@Test
	public void testSetLevel() {
		Log log = LogFactoryUtil.getLog(_NAMES[4]);

		Assert.assertTrue("Warn level should be enabled", log.isWarnEnabled());

		Log4JUtil.setLevel(_NAMES[4], "DEBUG", false);

		Assert.assertTrue(
			"DEBUG level should be enabled", log.isDebugEnabled());

		Log4JUtil.setLevel(_NAMES[4], "WARN", false);

		Log childLog = LogFactoryUtil.getLog("com.test.parent.child");

		Assert.assertTrue(
			"INFO level should be enabled", childLog.isInfoEnabled());
		Assert.assertFalse(
			"DEBUG level should be not enabled", childLog.isDebugEnabled());

		Log4JUtil.setLevel("com.test.parent", "DEBUG", false);

		Assert.assertTrue(
			"DEBUG level should be enabled", childLog.isDebugEnabled());
	}

	private static byte[] _getBytes(InputStream inputStream)
		throws IOException {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		StreamUtil.transfer(inputStream, unsyncByteArrayOutputStream, -1, true);

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private void _assertLog(String expectedOutput, int invokeLineNumber) {
		Thread currentThread = Thread.currentThread();

		Matcher matcher = _datePattern.matcher(expectedOutput.substring(0, 23));

		Assert.assertTrue(
			"Output date format should be yyyy-MM-dd HH:mm:ss.SSS",
			matcher.matches());

		String content = expectedOutput.substring(23);

		Assert.assertEquals(
			"Output content should be " + content, content,
			StringBundler.concat(
				" INFO  [", currentThread.getName(), "][Log4jTest:",
				invokeLineNumber, "] Test Message"));
	}

	private void _assertXmlLog(String expectedOutput, int invokeLineNumber)
		throws Exception {

		Thread currentThread = Thread.currentThread();

		String[] logMessages = StringUtil.splitLines(_baos.toString());

		String consoleOutput = logMessages[logMessages.length - 1];

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

		String date = consoleOutput.substring(0, 23);

		Date logDate = simpleDateFormat.parse(date);

		StringBundler sb = new StringBundler(16);

		sb.append("<log4j:event logger=\"");
		sb.append(Log4jTest.class.getName());
		sb.append("\" timestamp=\"");
		sb.append(logDate.getTime());
		sb.append("\" level=\"INFO\" thread=\"");
		sb.append(currentThread.getName());
		sb.append("\">\r\n");
		sb.append("<log4j:message><![CDATA[");
		sb.append("Test Message]]></log4j:message>\r\n");
		sb.append("<log4j:locationInfo class=\"");
		sb.append(Log4jTest.class.getName());
		sb.append("\" method=\"testRollingFileAppender\" file=\"");
		sb.append("Log4jTest.java\" line=\"");
		sb.append(invokeLineNumber);
		sb.append("\"/>\r\n");
		sb.append("</log4j:event>\r\n\r\n");

		Assert.assertEquals(
			"LogMessage should be " + expectedOutput, expectedOutput,
			sb.toString());
	}

	private static final String[] _NAMES = {
		"level", "level.off", "level.fatal", "level.error", "level.warn",
		"level.info", "level.debug", "level.trace"
	};

	private static final ByteArrayOutputStream _baos =
		new ByteArrayOutputStream();
	private static final Pattern _datePattern = Pattern.compile(
		"\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d.\\d\\d\\d");
	private static PrintStream _originalOutputStream;
	private static PrintStream _printStream;
	private static File _tempFile;
	private static final Pattern _textFileNamePattern = Pattern.compile(
		"liferay.\\d\\d\\d\\d-\\d\\d-\\d\\d.log");
	private static final Pattern _xmlFileNamePattern = Pattern.compile(
		"liferay.\\d\\d\\d\\d-\\d\\d-\\d\\d.xml");

	private static class TeePrintStream extends PrintStream {

		public TeePrintStream(
			OutputStream outputStream, PrintStream printStream) {

			super(outputStream);

			_printStream = printStream;
		}

		@Override
		public void close() {
			super.close();

			_printStream.flush();
		}

		@Override
		public void flush() {
			super.flush();

			_printStream.flush();
		}

		@Override
		public void write(byte[] bytes) throws IOException {
			super.write(bytes);

			_printStream.write(bytes);
		}

		@Override
		public void write(byte[] bytes, int offset, int length) {
			super.write(bytes, offset, length);

			_printStream.write(bytes, offset, length);
		}

		@Override
		public void write(int integer) {
			super.write(integer);

			_printStream.write(integer);
		}

		private final PrintStream _printStream;

	}

}