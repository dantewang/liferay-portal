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
import java.io.PrintStream;
import java.io.StringWriter;

import java.net.URI;
import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

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

		_printStream = new PrintStream(_baos);

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
		catch (IOException ioException) {
			throw ioException;
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
		Logger logger = Logger.getLogger(Log4jTest.class.getName());

		CustomAppender customAppender = new CustomAppender();

		logger.addAppender(customAppender);

		_log.info("Test Message");

		String[] logMessages = StringUtil.splitLines(_baos.toString());

		String expectedOutput = logMessages[logMessages.length - 1];

		try {
			_assert(expectedOutput, customAppender);
		}
		finally {
			logger.removeAppender(customAppender);
		}
	}

	@Test
	public void testGetOriginalLevel() {
		String level = Log4JUtil.getOriginalLevel(_NAMES[5]);

		Assert.assertTrue(
			"The original level should be INFO", level.equals("INFO"));

		level = Log4JUtil.getOriginalLevel(_NAMES[6]);

		Assert.assertTrue(
			"The original level should be DEBUG", level.equals("DEBUG"));
	}

	@Test
	public void testLoggerEnabled() {
		Log log = LogFactoryUtil.getLog(_NAMES[0]);

		if (log.isDebugEnabled() && log.isErrorEnabled() &&
			log.isFatalEnabled() && log.isInfoEnabled() &&
			log.isTraceEnabled() && log.isWarnEnabled()) {
		}
		else {
			Assert.fail("Logger should be all enabled");
		}

		log = LogFactoryUtil.getLog(_NAMES[1]);

		if (log.isDebugEnabled() || log.isErrorEnabled() ||
			log.isFatalEnabled() || log.isInfoEnabled() ||
			log.isTraceEnabled() || log.isWarnEnabled()) {

			Assert.fail("Setting logger level OFF does not take effect");
		}

		log = LogFactoryUtil.getLog(_NAMES[2]);

		if (log.isDebugEnabled() || log.isErrorEnabled() ||
			!log.isFatalEnabled() || log.isInfoEnabled() ||
			log.isTraceEnabled() || log.isWarnEnabled()) {

			Assert.fail("Setting logger level FATAL does not take effect");
		}

		log = LogFactoryUtil.getLog(_NAMES[3]);

		if (log.isDebugEnabled() || !log.isErrorEnabled() ||
			!log.isFatalEnabled() || log.isInfoEnabled() ||
			log.isTraceEnabled() || log.isWarnEnabled()) {

			Assert.fail("Setting logger level ERROR does not take effect");
		}

		log = LogFactoryUtil.getLog(_NAMES[4]);

		if (log.isDebugEnabled() || !log.isErrorEnabled() ||
			!log.isFatalEnabled() || log.isInfoEnabled() ||
			log.isTraceEnabled() || !log.isWarnEnabled()) {

			Assert.fail("Setting logger level WARN does not take effect");
		}

		log = LogFactoryUtil.getLog(_NAMES[5]);

		if (log.isDebugEnabled() || !log.isErrorEnabled() ||
			!log.isFatalEnabled() || !log.isInfoEnabled() ||
			log.isTraceEnabled() || !log.isWarnEnabled()) {

			Assert.fail("Setting logger level INFO does not take effect");
		}

		log = LogFactoryUtil.getLog(_NAMES[6]);

		if (!log.isDebugEnabled() || !log.isErrorEnabled() ||
			!log.isFatalEnabled() || !log.isInfoEnabled() ||
			log.isTraceEnabled() || !log.isWarnEnabled()) {

			Assert.fail("Setting logger level DEBUG does not take effect");
		}

		log = LogFactoryUtil.getLog(_NAMES[7]);

		if (!log.isDebugEnabled() || !log.isErrorEnabled() ||
			!log.isTraceEnabled() || !log.isInfoEnabled() ||
			!log.isTraceEnabled() || !log.isWarnEnabled()) {

			Assert.fail("Setting logger level TRACE does not take effect");
		}
	}

	@Test
	public void testRollingFileAppender() throws IOException {
		Logger logger = Logger.getLogger(Log4jTest.class.getName());

		CustomAppender customAppender = new CustomAppender();

		logger.addAppender(customAppender);

		_log.info("Test Message");

		File logDir = new File(
			StringUtil.replace(System.getProperty("user.dir"), '\\', '/'),
			"logs");

		try {
			for (File file : logDir.listFiles()) {
				String fileName = file.getName();

				URI uri = file.toURI();

				URL url = uri.toURL();

				String content = "";

				if (fileName.endsWith(".log")) {
					Matcher matcher = _textFileNamePattern.matcher(fileName);

					Assert.assertTrue(
						"test file name should be " + fileName,
						matcher.matches());

					try (InputStream inputStream = url.openStream()) {
						byte[] bytes = _getBytes(inputStream);

						content = new String(bytes, StringPool.UTF8);

						String[] logMessages = StringUtil.splitLines(content);

						_assert(
							logMessages[logMessages.length - 1],
							customAppender);
					}
				}
				else {
					Matcher matcher = _xmlFileNamePattern.matcher(fileName);

					Assert.assertTrue(
						"xml file name should be " + fileName,
						matcher.matches());

					try (InputStream inputStream = url.openStream()) {
						byte[] bytes = _getBytes(inputStream);

						content = new String(bytes, StringPool.UTF8);

						int index = content.lastIndexOf("<log4j:event");

						if (index < 0) {
							Assert.fail("There is no log meesage output");
						}

						_assertXmlLog(content.substring(index), customAppender);
					}
				}
			}
		}
		catch (IOException ioException) {
			throw ioException;
		}
		finally {
			logger.removeAppender(customAppender);
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

	@Test
	public void testWriteAppender() {
		StringWriter stringWrite = new StringWriter();

		WriterAppender writerAppender = new WriterAppender(
			new SimpleLayout(), stringWrite);

		Logger logger = Logger.getLogger(Log4jTest.class.getName());

		logger.addAppender(writerAppender);

		_log.info("Test message");

		String logMessage = stringWrite.toString();

		try {
			Assert.assertTrue(
				"Log message should be " + logMessage,
				logMessage.equals(
					Level.INFO + " - Test message" +
						System.getProperty("line.separator")));
		}
		finally {
			logger.removeAppender(writerAppender);
		}
	}

	private static byte[] _getBytes(InputStream inputStream)
		throws IOException {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		StreamUtil.transfer(inputStream, unsyncByteArrayOutputStream, -1, true);

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private void _assert(String expectedOutput, CustomAppender customAppender) {
		Matcher matcher = _datePattern.matcher(expectedOutput.substring(0, 23));

		Assert.assertTrue(
			"Output date format should be yyyy-MM-dd HH:mm:ss.SSS",
			matcher.matches());

		LoggingEvent loggingEvent = customAppender.getLoggingEvent();

		LocationInfo locationInfo = loggingEvent.getLocationInformation();

		String debugContent = expectedOutput.substring(23);

		Assert.assertTrue(
			"output content should be " + debugContent,
			debugContent.equals(
				StringBundler.concat(
					" ", loggingEvent.getLevel(), "  [",
					loggingEvent.getThreadName(), "][Log4jTest:",
					locationInfo.getLineNumber(), "] ",
					loggingEvent.getRenderedMessage())));
	}

	private void _assertXmlLog(
		String expectedOutput, CustomAppender customAppender) {

		LoggingEvent loggingEvent = customAppender.getLoggingEvent();

		LocationInfo locationInfo = loggingEvent.getLocationInformation();

		StringBundler sb = new StringBundler(22);

		sb.append("<log4j:event logger=\"");
		sb.append(loggingEvent.getLoggerName());
		sb.append("\" timestamp=\"");
		sb.append(loggingEvent.getTimeStamp());
		sb.append("\" level=\"");
		sb.append(loggingEvent.getLevel());
		sb.append("\" thread=\"");
		sb.append(loggingEvent.getThreadName());
		sb.append("\">\r\n");
		sb.append("<log4j:message><![CDATA[");
		sb.append(loggingEvent.getRenderedMessage());
		sb.append("]]></log4j:message>\r\n");
		sb.append("<log4j:locationInfo class=\"");
		sb.append(locationInfo.getClassName());
		sb.append("\" method=\"");
		sb.append(locationInfo.getMethodName());
		sb.append("\" file=\"");
		sb.append(locationInfo.getFileName());
		sb.append("\" line=\"");
		sb.append(locationInfo.getLineNumber());
		sb.append("\"/>\r\n");
		sb.append("</log4j:event>\r\n\r\n");

		Assert.assertTrue(
			"logMessage should be " + expectedOutput,
			expectedOutput.equals(sb.toString()));
	}

	private static final String[] _NAMES = {
		"level", "level.off", "level.fatal", "level.error", "level.warn",
		"level.info", "level.debug", "level.trace"
	};

	private static final Log _log = LogFactoryUtil.getLog(Log4jTest.class);

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

	private class CustomAppender extends AppenderSkeleton {

		@Override
		public void close() {
		}

		public LoggingEvent getLoggingEvent() {
			return _loggingEvent;
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		@Override
		protected void append(LoggingEvent event) {
			_loggingEvent = event;
		}

		private LoggingEvent _loggingEvent;

	}

}