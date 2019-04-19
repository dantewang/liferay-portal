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

package com.liferay.portal.kernel.lock;

import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Dante Wang
 * @author Peter Fellwock
 */
public class LockListenerRegistryUtilTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			LockListener.class,
			(LockListener)ProxyUtil.newProxyInstance(
				LockListener.class.getClassLoader(),
				new Class<?>[] {LockListener.class},
				(proxy, method, args) -> {
					if ("getClassName".equals(method.getName())) {
						return _CLASS_NAME;
					}

					if ("equals".equals(method.getName())) {
						return proxy == args[0];
					}

					if ("hashCode".equals(method.getName())) {
						return _CLASS_NAME.hashCode();
					}

					return null;
				}));
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetLockListener() {
		Registry registry = RegistryUtil.getRegistry();

		Assert.assertSame(
			registry.getService(_serviceRegistration.getServiceReference()),
			LockListenerRegistryUtil.getLockListener(_CLASS_NAME));
	}

	private static final String _CLASS_NAME = "TestLockListener";

	private static ServiceRegistration<LockListener> _serviceRegistration;

}