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

import com.liferay.portal.kernel.security.auth.EmailAddressGenerator;
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
 * @author Raymond Augé
 */
public class EmailAddressGeneratorFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestEmailAddressGenerator testEmailAddressGenerator =
			new TestEmailAddressGenerator();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			EmailAddressGenerator.class, testEmailAddressGenerator, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGenerate() {
		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		Assert.assertEquals(
			"2@generated.com", emailAddressGenerator.generate(1, 2));
	}

	@Test
	public void testIsFake() {
		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		Assert.assertTrue(emailAddressGenerator.isFake("2@fake.com"));
		Assert.assertFalse(emailAddressGenerator.isFake("2@generated.com"));
	}

	@Test
	public void testIsGenerated() {
		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		Assert.assertTrue(emailAddressGenerator.isGenerated("2@generated.com"));
	}

	private static ServiceRegistration<EmailAddressGenerator>
		_serviceRegistration;

	private static class TestEmailAddressGenerator
		implements EmailAddressGenerator {

		@Override
		public String generate(long companyId, long userId) {
			return userId + "@generated.com";
		}

		@Override
		public boolean isFake(String emailAddress) {
			if (emailAddress.endsWith("@generated.com")) {
				return false;
			}

			return true;
		}

		@Override
		public boolean isGenerated(String emailAddress) {
			return emailAddress.endsWith("@generated.com");
		}

	}

}