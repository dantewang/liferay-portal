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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.cli.ExitCodes;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.KeyStoreWrapper;
import org.elasticsearch.node.Node;

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

	public static Node start(String[] arguments) throws ProcessException {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			StreamOutput streamOutput = new OutputStreamStreamOutput(
				unsyncByteArrayOutputStream)) {

			Object serverArgs = _createServerArgs(arguments);

			_writeToMethod.invoke(serverArgs, streamOutput);

			InputStream originalSystemInInputStream = System.in;

			try (UnsyncByteArrayInputStream unsyncByteArrayInputStream =
					new UnsyncByteArrayInputStream(
						unsyncByteArrayOutputStream.toByteArray())) {

				System.setIn(unsyncByteArrayInputStream);

				_mainMethod.invoke(null);
			}
			finally {
				System.setIn(originalSystemInInputStream);
			}

			System.setSecurityManager(null);

			_addShutdownHook();

			return (Node)_nodeField.get(_instanceField.get(null));
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

	private static Object _createServerArgs(String[] arguments)
		throws ReflectiveOperationException {

		Object processInfo = _fromSystemMethod.invoke(null);

		Object serverCli = _serverCliConstructor.newInstance();

		Object optionSet = _parseMethod.invoke(
			_parserField.get(serverCli), (Object)arguments);

		Object environment = _createEnvMethod.invoke(
			serverCli, optionSet, processInfo);

		return _createArgsMethod.invoke(
			serverCli, optionSet, environment, KeyStoreWrapper.create(),
			processInfo);
	}

	private static final Logger _logger = LogManager.getLogger(
		ElasticsearchServerUtil.class);

	private static final Method _createArgsMethod;
	private static final Method _createEnvMethod;
	private static final Method _fromSystemMethod;
	private static final Field _hooksField;
	private static final Field _instanceField;
	private static final Method _mainMethod;
	private static final Field _nodeField;
	private static final Field _parserField;
	private static final Method _parseMethod;
	private static final Constructor<?> _serverCliConstructor;
	private static final CountDownLatch _shutdownCountDownLatch =
		new CountDownLatch(1);
	private static final Method _stopMethod;
	private static final Method _writeToMethod;

	static {
		try {
			ClassLoader classLoader =
				ElasticsearchServerUtil.class.getClassLoader();

			_hooksField = ReflectionUtil.getDeclaredField(
				classLoader.loadClass("java.lang.ApplicationShutdownHooks"),
				"hooks");

			Class<?> elasticsearchClass = classLoader.loadClass(
				"org.elasticsearch.bootstrap.Elasticsearch");

			_mainMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "main", String[].class);

			_instanceField = ReflectionUtil.getDeclaredField(
				elasticsearchClass, "INSTANCE");

			_nodeField = ReflectionUtil.getDeclaredField(
				elasticsearchClass, "node");

			_stopMethod = ReflectionUtil.getDeclaredMethod(
				elasticsearchClass, "shutdown");

			_writeToMethod = ReflectionUtil.getDeclaredMethod(
				classLoader.loadClass("org.elasticsearch.bootstrap.ServerArgs"),
				"writeTo", StreamOutput.class);

			Class<?> processInfoClass = classLoader.loadClass(
				"org.elasticsearch.cli.ProcessInfo");

			_fromSystemMethod = ReflectionUtil.getDeclaredMethod(
				processInfoClass, "fromSystem");

			Class<?> optionParserClass = classLoader.loadClass(
				"joptsimple.OptionParser");

			_parseMethod = ReflectionUtil.getDeclaredMethod(
				optionParserClass, "parse", String[].class);

			Class<?> optionSetClass = classLoader.loadClass(
				"joptsimple.OptionSet");

			Class<?> serverCliClass = classLoader.loadClass(
				"org.elasticsearch.server.cli.ServerCli");

			_serverCliConstructor = serverCliClass.getDeclaredConstructor();

			_serverCliConstructor.setAccessible(true);

			_parserField = ReflectionUtil.getDeclaredField(
				serverCliClass.getSuperclass().getSuperclass(), "parser");

			_createEnvMethod = ReflectionUtil.getDeclaredMethod(
				serverCliClass.getSuperclass(), "createEnv", optionSetClass,
				processInfoClass);

			_createArgsMethod = ReflectionUtil.getDeclaredMethod(
				serverCliClass, "createArgs", optionSetClass,
				classLoader.loadClass("org.elasticsearch.env.Environment"),
				classLoader.loadClass(
					"org.elasticsearch.common.settings.SecureSettings"),
				processInfoClass);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

}