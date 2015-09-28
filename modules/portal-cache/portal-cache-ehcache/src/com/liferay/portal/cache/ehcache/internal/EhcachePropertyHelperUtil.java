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

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Tina Tian
 */
public class EhcachePropertyHelperUtil {

	public static String getPropertiesString(
		Properties properties, String propertySeparator) {

		if (propertySeparator == null) {
			propertySeparator = StringPool.COMMA;
		}

		StringBundler sb = new StringBundler(properties.size() * 4);

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			sb.append(entry.getKey());
			sb.append(StringPool.EQUAL);
			sb.append(entry.getValue());
			sb.append(propertySeparator);
		}

		if (!properties.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	public static String parseFactoryClassName(
		String factoryClassName, Props props) {

		if (factoryClassName.indexOf(CharPool.EQUAL) == -1) {
			return factoryClassName;
		}

		String[] factoryClassNameParts = StringUtil.split(
			factoryClassName, CharPool.EQUAL);

		if (factoryClassNameParts[0].equals(_PORTAL_PROPERTY_KEY)) {
			return props.get(factoryClassNameParts[1]);
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Unable to parse factory class name " + factoryClassName);
		}

		return factoryClassName;
	}

	public static Properties parseProperties(
		String propertiesString, String propertySeparator, Props props) {

		Properties properties = new Properties();

		if (propertiesString == null) {
			return properties;
		}

		if (propertySeparator == null) {
			propertySeparator = StringPool.COMMA;
		}

		String propertyLines = propertiesString.trim();

		propertyLines = StringUtil.replace(
			propertyLines, propertySeparator, StringPool.NEW_LINE);

		try {
			properties.load(new UnsyncStringReader(propertyLines));
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		String portalPropertyKey = (String)properties.remove(
			_PORTAL_PROPERTY_KEY);

		if (Validator.isNull(portalPropertyKey)) {
			return properties;
		}

		String[] values = props.getArray(portalPropertyKey);

		if (_log.isInfoEnabled()) {
			_log.info(
				"portalPropertyKey " + portalPropertyKey + " has value " +
					Arrays.toString(values));
		}

		for (String value : values) {
			String[] valueParts = StringUtil.split(value, CharPool.EQUAL);

			if (valueParts.length != 2) {
				if (_log.isWarnEnabled()) {
					_log.warn("Ignore malformed value " + value);
				}

				continue;
			}

			properties.put(valueParts[0], _unescape(valueParts[1]));
		}

		return properties;
	}

	private static String _unescape(String text) {
		return StringUtil.replace(text, "&", ";", _unescapeMap);
	}

	private static final String _PORTAL_PROPERTY_KEY = "portalPropertyKey";

	private static final Log _log = LogFactoryUtil.getLog(
		EhcachePropertyHelperUtil.class);

	private static final Map<String, String> _unescapeMap = new HashMap<>();

	static {
		_unescapeMap.put("amp", "&");
		_unescapeMap.put("gt", ">");
		_unescapeMap.put("lt", "<");
		_unescapeMap.put("rsquo", "\u2019");
		_unescapeMap.put("#034", "\"");
		_unescapeMap.put("#039", "'");
		_unescapeMap.put("#040", "(");
		_unescapeMap.put("#041", ")");
		_unescapeMap.put("#044", ",");
		_unescapeMap.put("#035", "#");
		_unescapeMap.put("#037", "%");
		_unescapeMap.put("#059", ";");
		_unescapeMap.put("#061", "=");
		_unescapeMap.put("#043", "+");
		_unescapeMap.put("#045", "-");
	}

}