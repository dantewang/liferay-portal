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

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.security.auth.FullNameValidator;
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
 * @author Peter Fellwock
 */
public class FullNameValidatorFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestFullNameValidator testFullNameValidator =
			new TestFullNameValidator();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			FullNameValidator.class, testFullNameValidator, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testValidate() {
		FullNameValidator fullNameValidator =
			FullNameValidatorFactory.getInstance();

		Assert.assertTrue(
			fullNameValidator.validate(1, "Brian", "middleName", "lastName"));
		Assert.assertFalse(
			fullNameValidator.validate(2, "Brian", "middleName", "lastName"));
		Assert.assertFalse(
			fullNameValidator.validate(1, "Peter", "middleName", "lastName"));
	}

	private static ServiceRegistration<FullNameValidator> _serviceRegistration;

	private static class TestFullNameValidator implements FullNameValidator {

		@Override
		public boolean validate(
			long companyId, String firstName, String middleName,
			String lastName) {

			if (companyId == 1) {
				if (firstName.equals("Brian")) {
					return true;
				}
			}

			return false;
		}

	}

}