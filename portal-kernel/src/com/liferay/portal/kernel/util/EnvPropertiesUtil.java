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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jiaxu Wei
 */
public class EnvPropertiesUtil {

	private static Map<String, Character> _getCharPoolChars() {
		try {
			Map<String, Character> charPoolChars = new HashMap<>();

			for (Field field : CharPool.class.getFields()) {
				if (Modifier.isStatic(field.getModifiers()) &&
					(field.getType() == char.class)) {

					charPoolChars.put(
						StringUtil.removeChar(
							field.getName(), CharPool.UNDERLINE),
						field.getChar(null));
				}
			}

			return charPoolChars;
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private static final Map<String, Character> _charPoolChars =
		_getCharPoolChars();

}