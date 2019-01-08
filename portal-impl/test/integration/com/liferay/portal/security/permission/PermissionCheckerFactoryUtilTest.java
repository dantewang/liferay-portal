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

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class PermissionCheckerFactoryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestPermissionCheckerFactory testPermissionCheckerFactory =
			new TestPermissionCheckerFactory();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			PermissionCheckerFactory.class, testPermissionCheckerFactory,
			properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetPermissionCheckerFactory() {
		PermissionCheckerFactory permissionCheckerFactory =
			PermissionCheckerFactoryUtil.getPermissionCheckerFactory();

		Assert.assertNotNull(permissionCheckerFactory);

		Class<?> clazz = permissionCheckerFactory.getClass();

		Assert.assertEquals(
			TestPermissionCheckerFactory.class.getName(), clazz.getName());
	}

	private static ServiceRegistration<PermissionCheckerFactory>
		_serviceRegistration;

	private static class TestPermissionCheckerFactory
		implements PermissionCheckerFactory {

		@Override
		public PermissionChecker create(User user) throws Exception {
			return null;
		}

	}

}