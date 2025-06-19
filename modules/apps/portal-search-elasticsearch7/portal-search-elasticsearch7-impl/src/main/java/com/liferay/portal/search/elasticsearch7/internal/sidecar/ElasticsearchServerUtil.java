/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.reflect.ReflectionUtil;

import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.cli.ExitCodes;
import org.elasticsearch.common.hash.MessageDigests;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.KeyStoreWrapper;

/**
 * @author Tina Tian
 */
public class ElasticsearchServerUtil {

	public static void shutdown() {
		try {
			_stopMethod.invoke(null);
		}
		catch (Exception exception) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Unable to invoke stop method", exception);
			}

			System.exit(ExitCodes.CODE_ERROR);
		}

		_shutdownCountDownLatch.countDown();
	}

	public static Object start(SidecarServerArgs sidecarServerArgs)
		throws ProcessException {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			StreamOutput streamOutput = new OutputStreamStreamOutput(
				unsyncByteArrayOutputStream)) {

			_writeSidecarServerArgs(sidecarServerArgs, streamOutput);

			InputStream originalSystemInInputStream = System.in;

			try (UnsyncByteArrayInputStream unsyncByteArrayInputStream =
					new UnsyncByteArrayInputStream(
						unsyncByteArrayOutputStream.toByteArray())) {

				System.setIn(unsyncByteArrayInputStream);

				_mainMethod.invoke(null, (Object)null);
			}
			finally {
				System.setIn(originalSystemInInputStream);
			}

			System.setSecurityManager(null);

			_addShutdownHook();

			return _nodeField.get(_instanceField.get(null));
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

	public static class Reflection {

		public static Object invoke(Object instance, String methodName)
			throws Exception {

			return invoke(instance, methodName, _EMPTY_PARAMETER_TYPES);
		}

		public static Object invoke(
				Object instance, String methodName, Class<?>[] parameterTypes,
				Object... parameters)
			throws Exception {

			Method method = ReflectionUtil.getDeclaredMethod(
				instance.getClass(), methodName, parameterTypes);

			return method.invoke(instance, parameters);
		}

		private static final Class<?>[] _EMPTY_PARAMETER_TYPES =
			new Class<?>[0];

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

	private static void _writeSidecarServerArgs(
			SidecarServerArgs sidecarServerArgs, StreamOutput streamOutput)
		throws Exception {

		streamOutput.writeBoolean(sidecarServerArgs.isDaemonize());
		streamOutput.writeBoolean(sidecarServerArgs.isQuiet());
		streamOutput.writeOptionalString(sidecarServerArgs.getPidFile());
		streamOutput.writeString(KeyStoreWrapper.class.getName());

		try (KeyStoreWrapper keyStoreWrapper = KeyStoreWrapper.create()) {
			streamOutput.writeInt(keyStoreWrapper.getFormatVersion());
			streamOutput.writeBoolean(keyStoreWrapper.hasPassword());
			streamOutput.writeBoolean(false);
			streamOutput.writeVInt(1);
			streamOutput.writeString(KeyStoreWrapper.SEED_SETTING.getKey());

			ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(
				ElasticsearchServerUtil.class.getSimpleName());

			byte[] bytes = byteBuffer.array();

			MessageDigest messageDigest = MessageDigests.sha256();

			streamOutput.writeByteArray(bytes);
			streamOutput.writeByteArray(messageDigest.digest(bytes));
			streamOutput.writeBoolean(false);
		}

		streamOutput.writeVInt(
			sidecarServerArgs.getSettings(
			).size());

		Map<String, Serializable> settings = sidecarServerArgs.getSettings();

		for (Map.Entry<String, Serializable> entry : settings.entrySet()) {
			streamOutput.writeString(entry.getKey());
			streamOutput.writeGenericValue(entry.getValue());
		}

		streamOutput.writeString(sidecarServerArgs.getConfigDir());
		streamOutput.writeString(sidecarServerArgs.getLogsDir());

		streamOutput.flush();
	}

	private static final Logger _logger = LogManager.getLogger(
		ElasticsearchServerUtil.class);

	private static final ClassLoader _classLoader;
	private static final Field _hooksField;
	private static final Field _instanceField;
	private static final Method _mainMethod;
	private static final Field _nodeField;
	private static final CountDownLatch _shutdownCountDownLatch =
		new CountDownLatch(1);
	private static final Method _stopMethod;

	static {
		try {
			_classLoader = ElasticsearchServerUtil.class.getClassLoader();

			_hooksField = ReflectionUtil.getDeclaredField(
				_classLoader.loadClass("java.lang.ApplicationShutdownHooks"),
				"hooks");

			Class<?> elasticsearchClass = _classLoader.loadClass(
				"org.elasticsearch.bootstrap.Elasticsearch");

			_instanceField = ReflectionUtil.getDeclaredField(
				elasticsearchClass, "INSTANCE");
			_mainMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "main", String[].class);
			_nodeField = ReflectionUtil.getDeclaredField(
				elasticsearchClass, "node");
			_stopMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "shutdown");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

}