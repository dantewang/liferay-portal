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

import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class LockListenerRegistryUtilTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			LockListener.class, new TestLockListener(),
			new HashMap<String, Object>() {
				{
					put("service.ranking", Integer.MAX_VALUE);
				}
			});
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetLockListener() {
		LockListener lockListener = LockListenerRegistryUtil.getLockListener(
			TestLockListener.class.getName());

		Assert.assertEquals(
			lockListener.getClassName(), TestLockListener.class.getName());
	}

	private static ServiceRegistration<LockListener> _serviceRegistration;

	private static class TestLockListener implements LockListener {

		@Override
		public String getClassName() {
			return TestLockListener.class.getName();
		}

		@Override
		public void onAfterExpire(String key) {
		}

		@Override
		public void onAfterRefresh(String key) {
		}

		@Override
		public void onBeforeExpire(String key) {
		}

		@Override
		public void onBeforeRefresh(String key) {
		}

	}

}