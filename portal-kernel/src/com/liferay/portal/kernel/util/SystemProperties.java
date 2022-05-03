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

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Mirco Tamburini
 * @author Brett Randall
 * @author Shuyang Zhou
 */
public class SystemProperties {

	public static final String SYSTEM_PROPERTIES_QUIET =
		"system.properties.quiet";

	public static final String SYSTEM_PROPERTIES_SET = "system.properties.set";

	public static final String SYSTEM_PROPERTIES_SET_OVERRIDE =
		"system.properties.set.override";

	public static final String TMP_DIR = "java.io.tmpdir";

	public static void clear(String key) {
		System.clearProperty(key);

		_properties.remove(key);
	}

	public static String get(String key) {
		String value = _properties.get(key);

		if (value == null) {
			value = System.getProperty(key);
		}

		return value;
	}

	public static String[] getArray(String key) {
		String[] value = _propertiesArrayCache.get(key);

		if (value == null) {
			String propertiesValue = get(key);

			value = StringUtil.split(propertiesValue);

			_propertiesArrayCache.put(key, value);
		}

		return value;
	}

	public static Properties getProperties() {
		return PropertiesUtil.fromMap(_properties);
	}

	public static Properties getProperties(
		String prefix, boolean removePrefix) {

		return PropertiesUtil.getProperties(
			getProperties(), prefix, removePrefix);
	}

	public static void load(ClassLoader classLoader) {
		Properties properties = new Properties();

		List<URL> urls = null;

		if (!GetterUtil.getBoolean(
				System.getProperty(SYSTEM_PROPERTIES_QUIET))) {

			urls = new ArrayList<>();
		}

		// system.properties

		try {
			Enumeration<URL> enumeration = classLoader.getResources(
				"system.properties");

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				try (InputStream inputStream = url.openStream()) {
					properties.load(inputStream);
				}

				if (urls != null) {
					urls.add(url);
				}
			}
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}

		// system-ext.properties

		try {
			Enumeration<URL> enumeration = classLoader.getResources(
				"system-ext.properties");

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				try (InputStream inputStream = url.openStream()) {
					properties.load(inputStream);
				}

				if (urls != null) {
					urls.add(url);
				}
			}
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}

		// Set environment properties

		SystemEnv.setProperties(properties);

		// Default liferay home directory

		set(SystemPropsKeys.DEFAULT_LIFERAY_HOME, _getDefaultLiferayHome());

		// Set system properties

		if (GetterUtil.getBoolean(
				System.getProperty(SYSTEM_PROPERTIES_SET), true)) {

			boolean systemPropertiesSetOverride = GetterUtil.getBoolean(
				System.getProperty(SYSTEM_PROPERTIES_SET_OVERRIDE), true);

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String key = String.valueOf(entry.getKey());

				if (systemPropertiesSetOverride ||
					Validator.isNull(System.getProperty(key))) {

					System.setProperty(key, String.valueOf(entry.getValue()));
				}
			}

			if (!systemPropertiesSetOverride) {
				Properties systemProperties = System.getProperties();

				for (Map.Entry<Object, Object> entry :
						systemProperties.entrySet()) {

					String key = String.valueOf(entry.getKey());

					if (Validator.isNotNull(properties.get(key))) {
						properties.put(key, entry.getValue());
					}
				}
			}
		}

		// Use a fast concurrent hash map implementation instead of the slower
		// java.util.Properties

		PropertiesUtil.fromProperties(properties, _properties);
		_parseProperties(_properties);

		if (urls != null) {
			for (URL url : urls) {
				System.out.println("Loading " + url);
			}
		}
	}

	public static void set(String key, String value) {
		value = _replacePlaceholders(value, null);

		System.setProperty(key, value);

		_properties.put(key, value);
	}

	private static String _getDefaultLiferayHome() {
		String defaultLiferayHome = null;

		if (ServerDetector.isJBoss()) {
			defaultLiferayHome = get("jboss.home.dir") + "/..";
		}
		else if (ServerDetector.isWebLogic()) {
			defaultLiferayHome = get("env.DOMAIN_HOME") + "/..";
		}
		else if (ServerDetector.isTomcat()) {
			defaultLiferayHome = get("catalina.base") + "/..";
		}
		else {
			defaultLiferayHome = get("user.dir") + "/liferay";
		}

		defaultLiferayHome = StringUtil.replace(
			defaultLiferayHome, CharPool.BACK_SLASH, CharPool.SLASH);

		defaultLiferayHome = StringUtil.replace(
			defaultLiferayHome, StringPool.DOUBLE_SLASH, StringPool.SLASH);

		if (defaultLiferayHome.endsWith("/..")) {
			int pos = defaultLiferayHome.lastIndexOf(
				CharPool.SLASH, defaultLiferayHome.length() - 4);

			if (pos != -1) {
				defaultLiferayHome = defaultLiferayHome.substring(0, pos);
			}
		}

		return defaultLiferayHome;
	}

	private static void _parseProperties(Map<String, String> properties) {
		Map<String, String> placeholderPropsCache = new ConcurrentHashMap<>();

		for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
			String entryValue = propertyEntry.getValue();

			String value = _replacePlaceholders(
				entryValue, placeholderPropsCache);

			if (!entryValue.equals(value)) {
				placeholderPropsCache.put(propertyEntry.getKey(), value);

				propertyEntry.setValue(value);

				System.setProperty(propertyEntry.getKey(), value);
			}
		}
	}

	private static String _replacePlaceholders(
		String propertiesValue, Map<String, String> placeholderPropsCache) {

		int startIndex = propertiesValue.indexOf(
			StringPool.DOLLAR_AND_OPEN_CURLY_BRACE);

		while (startIndex != -1) {
			int endIndex = propertiesValue.indexOf(
				StringPool.CLOSE_CURLY_BRACE, startIndex);

			if (endIndex != -1) {
				String placeholderKey = propertiesValue.substring(
					startIndex +
						StringPool.DOLLAR_AND_OPEN_CURLY_BRACE.length(),
					endIndex);

				String placeholderValue = null;

				if (Objects.nonNull(placeholderPropsCache)) {
					placeholderValue = placeholderPropsCache.get(
						placeholderKey);
				}

				if (Objects.isNull(placeholderValue)) {
					placeholderValue = get(placeholderKey);

					if (Objects.isNull(placeholderValue)) {
						placeholderValue = "";
					}

					placeholderValue = _replacePlaceholders(
						placeholderValue, placeholderPropsCache);

					if (Objects.nonNull(placeholderPropsCache)) {
						placeholderPropsCache.put(
							placeholderKey, placeholderValue);
					}
				}

				propertiesValue = StringUtil.replace(
					propertiesValue,
					StringPool.DOLLAR_AND_OPEN_CURLY_BRACE + placeholderKey +
						StringPool.CLOSE_CURLY_BRACE,
					placeholderValue, startIndex);

				startIndex = propertiesValue.indexOf(
					StringPool.DOLLAR_AND_OPEN_CURLY_BRACE);
			}
			else {
				break;
			}
		}

		return propertiesValue;
	}

	private static final Map<String, String> _properties =
		new ConcurrentHashMap<>();
	private static final Map<String, String[]> _propertiesArrayCache =
		new ConcurrentHashMap<>();

	static {
		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		load(classLoader);
	}

}