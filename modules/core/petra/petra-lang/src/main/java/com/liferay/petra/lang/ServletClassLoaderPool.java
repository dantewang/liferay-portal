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

import com.liferay.petra.lang.internal.ClassLoaderPoolImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 */
public class ServletClassLoaderPool {

	public static ClassLoader getClassLoader(String contextName) {
		return _classLoaderPoolImpl.getClassLoader(contextName);
	}

	public static String getContextName(ClassLoader classLoader) {
		return _classLoaderPoolImpl.getContextName(classLoader);
	}

	public static void register(String contextName, ClassLoader classLoader) {
		_classLoaderPoolImpl.register(contextName, classLoader);
	}

	public static void unregister(ClassLoader classLoader) {
		_classLoaderPoolImpl.unregister(classLoader);
	}

	public static void unregister(String contextName) {
		_classLoaderPoolImpl.unregister(contextName);
	}

	private static final ClassLoaderPoolImpl _classLoaderPoolImpl;
	private static final Map<String, ClassLoader> _classLoaders =
		new ConcurrentHashMap<>();
	private static final Map<ClassLoader, String> _contextNames =
		new ConcurrentHashMap<>();

	static {
		_classLoaderPoolImpl = new ClassLoaderPoolImpl(
			_classLoaders, _contextNames);

		register("GlobalClassLoader", ClassLoaderPool.class.getClassLoader());
	}

}