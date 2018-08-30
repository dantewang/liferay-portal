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

package com.liferay.portal.test.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author Xiangyue Cai
 */
@Aspect
public class PropertiesUtilAdvice {

	public static void setReadThrowable(Throwable readThrowable) {
		_readThrowable = readThrowable;
	}

	@Around(
		"execution(public int com.liferay.portal.kernel.io.unsync." +
			"UnsyncStringReader.read(char[]))"
	)
	public Object read(ProceedingJoinPoint proceedingJoinPoint)
		throws Throwable {

		if (_readThrowable != null) {
			throw _readThrowable;
		}

		return proceedingJoinPoint.proceed();
	}

	private static Throwable _readThrowable;

}