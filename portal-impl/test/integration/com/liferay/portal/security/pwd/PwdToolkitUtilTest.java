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

package com.liferay.portal.security.pwd;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.security.pwd.Toolkit;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class PwdToolkitUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestToolkit testToolkit = new TestToolkit();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			Toolkit.class, testToolkit, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() {
		_called = false;
	}

	@Test
	public void testGenerated() {
		Assert.assertEquals(
			TestToolkit.PASSWORD, PwdToolkitUtil.generate(null));
	}

	@Test
	public void testInstance() {
		Toolkit toolkit = PwdToolkitUtil.getToolkit();

		Class<?> clazz = toolkit.getClass();

		Assert.assertEquals(TestToolkit.class.getName(), clazz.getName());
	}

	@Test
	public void testValidate() throws PortalException {
		PwdToolkitUtil.validate(1, 1, "passwd", "passwd", null);

		Assert.assertTrue(_called);
	}

	private static boolean _called;
	private static ServiceRegistration<Toolkit> _serviceRegistration;

	private static class TestToolkit implements Toolkit {

		public static final String PASSWORD = "shibboleth";

		@Override
		public String generate(PasswordPolicy passwordPolicy) {
			return PASSWORD;
		}

		@Override
		public void validate(
			long userId, String password1, String password2,
			PasswordPolicy passwordPolicy) {

			_called = true;
		}

		@Override
		public void validate(
			String password1, String password2, PasswordPolicy passwordPolicy) {

			_called = true;
		}

	}

}