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

import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
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
public class EmailAddressValidatorFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestEmailAddressValidator testEmailAddressValidator =
			new TestEmailAddressValidator();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			EmailAddressValidator.class, testEmailAddressValidator, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testValidate() {
		EmailAddressValidator emailAddressValidator =
			EmailAddressValidatorFactory.getInstance();

		Assert.assertTrue(
			emailAddressValidator.validate(
				1, "TestEmailAddressValidator@liferay-test.com"));
		Assert.assertFalse(
			emailAddressValidator.validate(
				2, "TestEmailAddressValidator@liferay-test.com"));
		Assert.assertFalse(
			emailAddressValidator.validate(1, "not@liferay-test.com"));
	}

	private static ServiceRegistration<EmailAddressValidator>
		_serviceRegistration;

	private static class TestEmailAddressValidator
		implements EmailAddressValidator {

		@Override
		public boolean validate(long companyId, String emailAddress) {
			if (companyId == 1) {
				if (emailAddress.equals(
						"TestEmailAddressValidator@liferay-test.com")) {

					return true;
				}
			}

			return false;
		}

	}

}