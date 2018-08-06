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

package com.liferay.petra.lang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dante Wang
 */
public class ServletClassLoaderPool {

	public static ClassLoader getClassLoader(String servletContextName) {
		ClassLoader classLoader = null;

		if ((servletContextName != null) &&
			!servletContextName.equals("null")) {

			classLoader = _classLoaders.get(servletContextName);
		}

		if (classLoader == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();
		}

		return classLoader;
	}

	public static String getServletContextName(ClassLoader classLoader) {
		if (classLoader == null) {
			return "null";
		}

		String contextName = _servletContextNames.get(classLoader);

		if (contextName == null) {
			contextName = "null";
		}

		return contextName;
	}

	public static void register(
		String servletContextName, ClassLoader classLoader) {

		_classLoaders.put(servletContextName, classLoader);
		_servletContextNames.put(classLoader, servletContextName);
	}

	public static void unregister(ClassLoader classLoader) {
		String servletContextName = _servletContextNames.remove(classLoader);

		if (servletContextName != null) {
			_classLoaders.remove(servletContextName);
		}
	}

	public static void unregister(String servletContextName) {
		ClassLoader classLoader = _classLoaders.remove(servletContextName);

		if (classLoader != null) {
			_servletContextNames.remove(classLoader);
		}
	}

	private static final Map<String, ClassLoader> _classLoaders =
		new ConcurrentHashMap<>();
	private static final Map<ClassLoader, String> _servletContextNames =
		new ConcurrentHashMap<>();

	static {
		register(
			"GlobalClassLoader", ServletClassLoaderPool.class.getClassLoader());
	}

}