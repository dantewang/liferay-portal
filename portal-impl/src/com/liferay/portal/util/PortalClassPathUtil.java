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

package com.liferay.portal.util;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.io.File;
import java.io.FileFilter;

import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author Shuyang Zhou
 */
public class PortalClassPathUtil {

	public static ProcessConfig createProcessConfig(Class<?>... classes) {
		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		builder.setArguments(_processArgs);

		String classpath = _buildClassPath(classes);

		classpath = StringBundler.concat(
			classpath, File.pathSeparator,
			_portalProcessConfig.getBootstrapClassPath());

		builder.setBootstrapClassPath(classpath);

		builder.setProcessLogConsumer(
			processLog -> {
				if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.INFO == processLog.getLevel()) {
					if (_log.isInfoEnabled()) {
						_log.info(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.WARN == processLog.getLevel()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else {
					_log.error(
						processLog.getMessage(), processLog.getThrowable());
				}
			});
		builder.setReactClassLoader(PortalClassLoaderUtil.getClassLoader());
		builder.setRuntimeClassPath(classpath);

		return builder.build();
	}

	public static ProcessConfig getPortalProcessConfig() {
		return _portalProcessConfig;
	}

	public static void initializeClassPaths(ServletContext servletContext) {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		if (classLoader == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();
		}

		StringBundler sb = new StringBundler(8);

		sb.append(
			_buildClassPath(classLoader, ServletException.class.getName()));

		sb.append(File.pathSeparator);
		sb.append(
			_buildClassPath(
				classLoader, CentralizedThreadLocal.class.getName()));

		String bootstrapClassPath = sb.toString();

		sb.append(File.pathSeparator);
		sb.append(
			_buildClassPath(
				classLoader,
				"com.liferay.shielded.container.ShieldedContainerInitializer"));

		if (servletContext != null) {
			sb.append(File.pathSeparator);
			sb.append(servletContext.getRealPath(""));
			sb.append("/WEB-INF/classes");
		}

		String portalClassPath = sb.toString();

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		builder.setArguments(_processArgs);
		builder.setBootstrapClassPath(bootstrapClassPath);
		builder.setReactClassLoader(classLoader);
		builder.setRuntimeClassPath(portalClassPath);

		_portalProcessConfig = builder.build();
	}

	private static String _buildClassPath(Class<?>... classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(classes.length * 2);

		for (Class<?> clazz : classes) {
			sb.append(_buildClassPath(clazz.getClassLoader(), clazz.getName()));
			sb.append(File.pathSeparator);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static String _buildClassPath(
		ClassLoader classLoader, String... classNames) {

		Set<File> fileSet = new HashSet<>();

		for (String className : classNames) {
			File[] files = _listClassPathFiles(classLoader, className);

			if (files != null) {
				Collections.addAll(fileSet, files);
			}
		}

		File[] files = fileSet.toArray(new File[0]);

		Arrays.sort(files);

		StringBundler sb = new StringBundler(files.length * 2);

		for (File file : files) {
			sb.append(file.getAbsolutePath());
			sb.append(File.pathSeparator);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static File[] _listClassPathFiles(
		ClassLoader classLoader, String className) {

		UnsafeFunction<URL, URL, Exception> urlMapper = url -> {
			URLConnection urlConnection = url.openConnection();

			Class<?> clazz = urlConnection.getClass();

			Method getLocalURLMethod = clazz.getDeclaredMethod("getLocalURL");

			getLocalURLMethod.setAccessible(true);

			return (URL)getLocalURLMethod.invoke(urlConnection);
		};

		Map<String, UnsafeFunction<URL, URL, Exception>> urlMappers =
			HashMapBuilder.<String, UnsafeFunction<URL, URL, Exception>>put(
				"bundle", urlMapper
			).put(
				"bundleresource", urlMapper
			).build();

		File dir = new File(
			ClassUtil.getParentDir(classLoader, className, urlMappers));

		/*
		int pos = -1;

		if (!path.startsWith("file:") ||
			((pos = path.indexOf(CharPool.EXCLAMATION)) == -1)) {

			if (!path.endsWith(pathOfClass)) {
				_log.error(
					"Class " + className + " is not loaded from a JAR file");

				return null;
			}

			String classesDirName = path.substring(
				0, path.length() - pathOfClass.length());

			if (!classesDirName.endsWith("/WEB-INF/classes/")) {
				_log.error(
					StringBundler.concat(
						"Class ", className, " is not loaded from a standard ",
						"location (/WEB-INF/classes)"));

				return null;
			}

			String libDirName = classesDirName.substring(
				0, classesDirName.length() - "classes/".length());

			libDirName += "/lib";

			dir = new File(libDirName);
		}
		else {
			pos = path.lastIndexOf(CharPool.SLASH, pos);

			dir = new File(path.substring("file:".length(), pos));
		}
		*/

		if (!dir.isDirectory()) {
			_log.error(dir.toString() + " is not a directory");

			return null;
		}

		return dir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return false;
					}

					String name = file.getName();

					if (name.equals("bundleFile") || name.endsWith(".jar")) {
						return true;
					}

					return false;
				}

			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalClassPathUtil.class);

	private static ProcessConfig _portalProcessConfig;
	private static final List<String> _processArgs = Arrays.asList(
		"-Dconfiguration.impl.quiet=true", "-Djava.awt.headless=true",
		"-Dserver.detector.quiet=true", "-Dsystem.properties.quiet=true");

}