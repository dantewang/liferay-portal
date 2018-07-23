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

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.portal.kernel.cache.PortalCache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dante Wang
 */
public class ConcurrentMapInvocationHandler implements InvocationHandler {

	public ConcurrentMapInvocationHandler(
		ConcurrentMap<String, PortalCache> portalCaches) {

		_portalCaches = portalCaches;
	}

	public void block() {
		_count.set(0);
		_semaphore = new Semaphore(0);
	}

	public int getPutIfAbsentExecutionCount() {
		return _count.get();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		String methodName = method.getName();

		if (methodName.equals("putIfAbsent")) {
			return _putIfAbsent((String)args[0], (PortalCache)args[1]);
		}

		return method.invoke(_portalCaches, args);
	}

	public void unblock(int permits) {
		Semaphore semaphore = _semaphore;

		_semaphore = null;

		semaphore.release(permits);
	}

	public void waitUntilBlock(int threadCount) {
		Semaphore semaphore = _semaphore;

		if (semaphore != null) {
			while (semaphore.getQueueLength() < threadCount);
		}
	}

	private Object _putIfAbsent(String key, PortalCache value)
		throws Throwable {

		Semaphore semaphore = _semaphore;

		if (semaphore != null) {
			semaphore.acquire();
		}

		_count.incrementAndGet();

		return _portalCaches.putIfAbsent(key, value);
	}

	private final AtomicInteger _count = new AtomicInteger();
	private final ConcurrentMap<String, PortalCache> _portalCaches;
	private volatile Semaphore _semaphore;

}