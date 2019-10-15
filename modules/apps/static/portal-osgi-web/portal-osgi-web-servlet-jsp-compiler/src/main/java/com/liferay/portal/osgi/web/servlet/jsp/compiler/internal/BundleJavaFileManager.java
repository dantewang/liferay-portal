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

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.apache.jasper.Constants;

/**
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class BundleJavaFileManager
	extends ForwardingJavaFileManager<JavaFileManager> {

	public static final String OPT_VERBOSE = "-verbose";

	public BundleJavaFileManager(
		ClassLoader classLoader, JavaFileManager javaFileManager,
		List<JavaFileObjectResolver> javaFileObjectResolvers,
		List<BytecodeJavaFileObject> bytecodeJavaFileObjects) {

		super(javaFileManager);

		_classLoader = classLoader;
		_javaFileObjectResolvers = javaFileObjectResolvers;
		_bytecodeJavaFileObjects = bytecodeJavaFileObjects;
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		if (location != StandardLocation.CLASS_PATH) {
			return fileManager.getClassLoader(location);
		}

		return _classLoader;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(
		Location location, String className, JavaFileObject.Kind kind,
		FileObject sibling) {

		Map<String, Map<String, JavaFileObject>> packageMap = _packageMap;

		String packageName = className.substring(
			0, className.lastIndexOf(CharPool.PERIOD));

		Map<String, JavaFileObject> packageJavaFileObjects = packageMap.get(
			packageName);

		BytecodeJavaFileObject bytecodeJavaFileObject =
			new BytecodeJavaFileObject(className);

		if (packageJavaFileObjects == null) {
			packageJavaFileObjects = new ConcurrentHashMap<>();

			packageMap.put(packageName, packageJavaFileObjects);
		}

		packageJavaFileObjects.put(className, bytecodeJavaFileObject);

		_bytecodeJavaFileObjects.add(bytecodeJavaFileObject);

		return bytecodeJavaFileObject;
	}

	@Override
	public String inferBinaryName(
		Location location, JavaFileObject javaFileObject) {

		if (javaFileObject instanceof BytecodeJavaFileObject) {
			BytecodeJavaFileObject bytecodeJavaFileObject =
				(BytecodeJavaFileObject)javaFileObject;

			return bytecodeJavaFileObject.getClassName();
		}

		if ((location == StandardLocation.CLASS_PATH) &&
			(javaFileObject instanceof BaseJavaFileObject)) {

			BaseJavaFileObject baseJavaFileObject =
				(BaseJavaFileObject)javaFileObject;

			if (_log.isInfoEnabled()) {
				_log.info("Inferring binary name from " + baseJavaFileObject);
			}

			return baseJavaFileObject.getClassName();
		}

		return fileManager.inferBinaryName(location, javaFileObject);
	}

	@Override
	public Iterable<JavaFileObject> list(
			Location location, String packageName,
			Set<JavaFileObject.Kind> kinds, boolean recurse)
		throws IOException {

		if ((location == StandardLocation.CLASS_PATH) &&
			packageName.startsWith(Constants.JSP_PACKAGE_NAME)) {

			Map<String, Map<String, JavaFileObject>> packageMap = _packageMap;

			Map<String, JavaFileObject> packageFiles = packageMap.get(
				packageName);

			if (packageFiles != null) {
				return packageFiles.values();
			}
		}

		if (!kinds.contains(JavaFileObject.Kind.CLASS)) {
			return Collections.emptyList();
		}

		if ((location == StandardLocation.CLASS_PATH) && _log.isInfoEnabled()) {
			StringBundler sb = new StringBundler(9);

			sb.append("List for {kinds=");
			sb.append(_kinds);
			sb.append(", location=");
			sb.append(location);
			sb.append(", packageName=");
			sb.append(packageName);
			sb.append(", recurse=");
			sb.append(recurse);
			sb.append(StringPool.CLOSE_CURLY_BRACE);

			_log.info(sb.toString());
		}

		String packagePath = StringUtil.replace(
			packageName, CharPool.PERIOD, CharPool.SLASH);

		if (!packageName.startsWith("java.") &&
			(location == StandardLocation.CLASS_PATH)) {

			for (JavaFileObjectResolver javaFileObjectResolver :
					_javaFileObjectResolvers) {

				Collection<JavaFileObject> javaFileObjects =
					javaFileObjectResolver.resolveClasses(recurse, packagePath);

				if (!javaFileObjects.isEmpty()) {
					return javaFileObjects;
				}
			}
		}

		return fileManager.list(location, packagePath, _kinds, recurse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BundleJavaFileManager.class);

	private static final Set<JavaFileObject.Kind> _kinds = EnumSet.of(
		JavaFileObject.Kind.CLASS);

	private final List<BytecodeJavaFileObject> _bytecodeJavaFileObjects;
	private final ClassLoader _classLoader;
	private final List<JavaFileObjectResolver> _javaFileObjectResolvers;
	private final Map<String, Map<String, JavaFileObject>> _packageMap =
		new ConcurrentHashMap<>();

}