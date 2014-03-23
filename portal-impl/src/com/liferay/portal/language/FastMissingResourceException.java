/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.language;

import com.liferay.portal.kernel.util.StringPool;

import java.util.MissingResourceException;

/**
 * @author Carlos Sierra Andrés
 */
public class FastMissingResourceException extends MissingResourceException {

	/**
	 * Constructs a MissingResourceException with the specified information.
	 * A detail message is a String that describes this particular exception.
	 *
	 * @param s         the detail message
	 * @param className the name of the resource class
	 * @param key       the key for the missing resource.
	 */
	public FastMissingResourceException(
		String s, String className, String key) {
		super(s, className, key);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return null;

	}

	public static final MissingResourceException
		BLANK_MISSING_RESOURCE_EXCEPTION = new FastMissingResourceException(
		StringPool.BLANK, StringPool.BLANK, StringPool.BLANK);

}
