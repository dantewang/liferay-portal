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

package com.liferay.portal.test.rule;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Janis Zhang
 */
public class CountingInvocationHandler implements InvocationHandler {

	public static int invocationCount = 0;

	public CountingInvocationHandler(Object target) {
		_target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		invocationCount++;

		return method.invoke(_target, args);
	}

	private final Object _target;

}