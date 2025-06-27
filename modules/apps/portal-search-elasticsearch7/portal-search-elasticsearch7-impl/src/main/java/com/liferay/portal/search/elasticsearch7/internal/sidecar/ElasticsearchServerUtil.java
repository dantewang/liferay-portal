/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.reflect.ReflectionUtil;

import java.io.Closeable;
import java.io.InputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.message.Message;

import org.elasticsearch.cli.ExitCodes;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.io.stream.StreamOutput;

/**
 * @author Tina Tian
 */
public class ElasticsearchServerUtil {

	public static void shutdown() {
		try {
			_shutdownMethod.invoke(null);
		}
		catch (Exception exception) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Unable to invoke stop method", exception);
			}

			System.exit(ExitCodes.CODE_ERROR);
		}

		_shutdownCountDownLatch.countDown();
	}

	public static String start(SidecarServerArgs sidecarServerArgs)
		throws ProcessException {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			StreamOutput streamOutput = new OutputStreamStreamOutput(
				unsyncByteArrayOutputStream)) {

			sidecarServerArgs.writeTo(streamOutput);

			InputStream originalSystemInInputStream = System.in;

			Object bootstrapObject = null;

			try (UnsyncByteArrayInputStream unsyncByteArrayInputStream =
					new UnsyncByteArrayInputStream(
						unsyncByteArrayOutputStream.toByteArray())) {

				System.setIn(unsyncByteArrayInputStream);

				bootstrapObject = _initPhase1Method.invoke(null);
			}
			finally {
				System.setIn(originalSystemInInputStream);
			}

			try (Closeable closeable = _enableAppender()) {
				_initPhase2Method.invoke(null, bootstrapObject);

				_initPhase3Method.invoke(null, bootstrapObject);
			}

			System.setSecurityManager(null);

			_addShutdownHook();

			return _getAddress();
		}
		catch (Exception exception) {
			throw new ProcessException(
				"Unable to start elasticsearch server", exception);
		}
	}

	public static void waitForShutdown() throws ProcessException {
		try {
			_shutdownCountDownLatch.await();
		}
		catch (InterruptedException interruptedException) {
			throw new ProcessException(
				"Sidecar main thread is interrupted", interruptedException);
		}
	}

	private static void _addShutdownHook() throws ReflectiveOperationException {
		synchronized (_hooksField.getDeclaringClass()) {
			Map<Thread, Thread> hooks = (Map<Thread, Thread>)_hooksField.get(
				null);

			Set<Thread> threads = new HashSet<>(hooks.keySet());

			hooks.clear();

			Thread shutdownHook = new Thread(
				() -> {
					try {
						_shutdownCountDownLatch.await();
					}
					catch (InterruptedException interruptedException) {
						if (_logger.isDebugEnabled()) {
							_logger.debug(interruptedException);
						}
					}

					for (Thread thread : threads) {
						thread.start();
					}

					for (Thread thread : threads) {
						while (true) {
							try {
								thread.join();

								break;
							}
							catch (InterruptedException interruptedException) {
								if (_logger.isDebugEnabled()) {
									_logger.debug(interruptedException);
								}
							}
						}
					}
				},
				"Elasticsearch Server Shutdown Hook");

			hooks.put(shutdownHook, shutdownHook);
		}
	}

	private static Closeable _enableAppender() throws Exception {
		ClassLoader classLoader =
			ElasticsearchServerUtil.class.getClassLoader();

		Class<?> clazz = classLoader.loadClass(_LOGGER_NAME);

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext(
			clazz.getClassLoader(), false);

		org.apache.logging.log4j.core.Logger logger = loggerContext.getLogger(
			_LOGGER_NAME);

		LoggerConfig loggerConfig = logger.get();

		boolean additive = logger.isAdditive();
		Level level = logger.getLevel();

		loggerConfig.setAdditive(false);
		loggerConfig.setLevel(Level.INFO);

		CaptureAddressAppender captureAddressAppender =
			new CaptureAddressAppender(_LOGGER_NAME + "_capture");

		captureAddressAppender.start();

		logger.addAppender(captureAddressAppender);

		loggerContext.updateLoggers();

		return () -> {
			captureAddressAppender.stop();

			logger.removeAppender(captureAddressAppender);
			loggerConfig.setAdditive(additive);
			loggerConfig.setLevel(level);

			loggerContext.updateLoggers();
		};
	}

	private static String _getAddress() {
		if (_address == null) {
			throw new IllegalStateException(
				"The bound address is not captured");
		}

		return _address;
	}

	private static final String _LOGGER_NAME =
		"org.elasticsearch.http.AbstractHttpServerTransport";

	private static final Logger _logger = LogManager.getLogger(
		ElasticsearchServerUtil.class);

	private static String _address;
	private static final Field _hooksField;
	private static final Method _initPhase1Method;
	private static final Method _initPhase2Method;
	private static final Method _initPhase3Method;
	private static final CountDownLatch _shutdownCountDownLatch =
		new CountDownLatch(1);
	private static final Method _shutdownMethod;

	static {
		try {
			ClassLoader classLoader =
				ElasticsearchServerUtil.class.getClassLoader();

			_hooksField = ReflectionUtil.getDeclaredField(
				classLoader.loadClass("java.lang.ApplicationShutdownHooks"),
				"hooks");

			Class<?> elasticsearchClass = classLoader.loadClass(
				"org.elasticsearch.bootstrap.Elasticsearch");

			Class<?> bootstrapClass = classLoader.loadClass(
				"org.elasticsearch.bootstrap.Bootstrap");

			_initPhase1Method = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "initPhase1");
			_initPhase2Method = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "initPhase2", bootstrapClass);
			_initPhase3Method = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "initPhase3", bootstrapClass);
			_shutdownMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "shutdown");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private static class CaptureAddressAppender extends AbstractAppender {

		@Override
		public void append(LogEvent logEvent) {
			Message message = logEvent.getMessage();

			String formattedMessage = message.getFormattedMessage();

			if ((formattedMessage == null) ||
				!formattedMessage.contains("publish_address")) {

				return;
			}

			Matcher matcher = _publishAddressPattern.matcher(formattedMessage);

			if (matcher.find()) {
				_address = matcher.group(1);
			}
		}

		private CaptureAddressAppender(String appenderName) {
			super(appenderName, null, null, true, null);
		}

		private static final Pattern _publishAddressPattern = Pattern.compile(
			"publish_address \\{([^}]+)}");

	}

}