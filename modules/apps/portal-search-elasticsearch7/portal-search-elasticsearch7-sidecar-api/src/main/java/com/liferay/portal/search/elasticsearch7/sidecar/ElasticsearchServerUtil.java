/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.sidecar;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.reflect.ReflectionUtil;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.nio.file.Path;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

			System.exit(_EXIT_CODE_ERROR);
		}

		_shutdownCountDownLatch.countDown();
	}

	public static Object start(SidecarServerArgs sidecarServerArgs)
		throws ProcessException {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			Closeable closeable = (Closeable)Reflection.newInstance(
				"org.elasticsearch.common.io.stream.OutputStreamStreamOutput",
				new Class<?>[] {OutputStream.class},
				unsyncByteArrayOutputStream)) {

			_writeSidecarServerArgs(sidecarServerArgs, closeable);

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

		public static Object invoke(
				String className, Object instance, String methodName)
			throws Exception {

			return invoke(
				className, instance, methodName, _EMPTY_PARAMETER_TYPES);
		}

		public static Object invoke(
				String className, Object instance, String methodName,
				Class<?>[] parameterTypes, Object... parameters)
			throws Exception {

			Method method = ReflectionUtil.getDeclaredMethod(
				_classLoader.loadClass(className), methodName, parameterTypes);

			return method.invoke(instance, parameters);
		}

		public static Object invokeStatic(String className, String methodName)
			throws Exception {

			Method method = ReflectionUtil.getDeclaredMethod(
				_classLoader.loadClass(className), methodName);

			return method.invoke(null);
		}

		public static Object newInstance(
				String className, Class<?>[] parameterTypes,
				Object... parameters)
			throws Exception {

			Class<?> clazz = _classLoader.loadClass(className);

			Constructor<?> constructor = clazz.getConstructor(parameterTypes);

			return constructor.newInstance(parameters);
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
			SidecarServerArgs sidecarServerArgs, Object streamOutputObject)
		throws Exception {

		Reflection.invokeStatic(
			"org.elasticsearch.common.logging.LogConfigurator",
			"configureESLogging");

		Object keyStoreWrapperObject = Reflection.invokeStatic(
			"org.elasticsearch.common.settings.KeyStoreWrapper", "create");

		Object settingsBuilderObject = Reflection.invokeStatic(
			"org.elasticsearch.common.settings.Settings", "builder");

		settingsBuilderObject = Reflection.invoke(
			settingsBuilderObject, "loadFromMap", new Class<?>[] {Map.class},
			sidecarServerArgs.getSettings());

		Object settingsObject = Reflection.invoke(
			settingsBuilderObject, "build");

		Object serverArgsObject = Reflection.newInstance(
			"org.elasticsearch.bootstrap.ServerArgs",
			new Class<?>[] {
				boolean.class, boolean.class, Path.class,
				_classLoader.loadClass(
					"org.elasticsearch.common.settings.SecureSettings"),
				settingsObject.getClass(), Path.class, Path.class
			},
			sidecarServerArgs.isDaemonize(), sidecarServerArgs.isQuiet(),
			sidecarServerArgs.getPidFile(), keyStoreWrapperObject,
			settingsObject, Path.of(sidecarServerArgs.getConfigDir()),
			Path.of(sidecarServerArgs.getLogsDir()));

		Reflection.invoke(
			serverArgsObject, "writeTo",
			new Class<?>[] {
				_classLoader.loadClass(
					"org.elasticsearch.common.io.stream.StreamOutput")
			},
			streamOutputObject);

		Reflection.invoke(streamOutputObject, "flush");
	}

	// See org.elasticsearch.cli.ExitCodes.CODE_ERROR

	private static final int _EXIT_CODE_ERROR = 70;

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