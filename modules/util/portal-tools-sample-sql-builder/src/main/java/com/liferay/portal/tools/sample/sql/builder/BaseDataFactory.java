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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.util.Time;
import com.liferay.util.SimpleCounter;

import java.io.InputStream;

import java.util.Date;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public long getClassNameId(Class<?> clazz) {
		ClassNameModel classNameModel = dataFactoryContext.getClassNameModel(
			clazz.getName());

		return classNameModel.getClassNameId();
	}

	public InputStream getResourceInputStream(String resourceName) {
		ClassLoader classLoader = _clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public Date nextFutureDate(SimpleCounter futureDateCounter) {
		return new Date(_FUTURE_TIME + (futureDateCounter.get() * Time.SECOND));
	}

	protected BaseDataFactory(DataFactoryContext dataFactoryContext) {
		this.dataFactoryContext = dataFactoryContext;
	}

	protected final DataFactoryContext dataFactoryContext;

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private final Class<?> _clazz = getClass();

}