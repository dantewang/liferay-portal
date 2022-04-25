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

package com.liferay.portal.configuration.metatype.bnd.util;

import com.liferay.petra.reflect.ReflectionUtil;

import java.lang.reflect.Method;

import java.util.concurrent.CountDownLatch;

/**
 * @author Janis Zhang
 */
public class DefineClassMethodUtil {

	public static Object defineClass(
			ClassLoader classLoader, String snapshotClassName,
			byte[] snapshotClassData, int off, int len)
		throws Exception {

		if (_block) {
			_waitingCountDownLatch.countDown();

			_blockingCountDownLatch.await();
		}

		return _defineClassMethod.invoke(
			classLoader, snapshotClassName, snapshotClassData, off, len);
	}

	public static void setBlock(Boolean block) {
		_block = block;
	}

	public static void unblock() {
		_blockingCountDownLatch.countDown();
	}

	public static void waitUntilBlock() throws InterruptedException {
		_waitingCountDownLatch.await();
	}

	private static Boolean _block = false;
	private static final CountDownLatch _blockingCountDownLatch =
		new CountDownLatch(1);
	private static final Method _defineClassMethod;
	private static final CountDownLatch _waitingCountDownLatch =
		new CountDownLatch(2);

	static {
		try {
			_defineClassMethod = ReflectionUtil.getDeclaredMethod(
				ClassLoader.class, "defineClass", String.class, byte[].class,
				int.class, int.class);
		}
		catch (Throwable throwable) {
			throw new ExceptionInInitializerError(throwable);
		}
	}

}