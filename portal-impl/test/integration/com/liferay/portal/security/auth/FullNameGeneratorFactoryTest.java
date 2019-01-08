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

import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
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
public class FullNameGeneratorFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestFullNameGenerator testFullNameGenerator =
			new TestFullNameGenerator();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			FullNameGenerator.class, testFullNameGenerator, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetFullName() {
		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		Assert.assertEquals(
			"John Stephen Piper",
			fullNameGenerator.getFullName("John", "Stephen", "Piper"));
	}

	@Test
	public void testGetLocalizedFullName() {
		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		Assert.assertEquals(
			"Jacques",
			fullNameGenerator.getLocalizedFullName(
				"James", "middle", "lastname", Locale.FRENCH, 1, 1));

		Assert.assertNotEquals(
			"Jacques",
			fullNameGenerator.getLocalizedFullName(
				"Tom", "middle", "lastname", Locale.CHINESE, 1, 1));
	}

	@Test
	public void testSplitFullName() {
		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		String[] splitFullName = fullNameGenerator.splitFullName(
			"John Stephen Piper");

		Assert.assertEquals(
			Arrays.toString(splitFullName), 3, splitFullName.length);
	}

	private static ServiceRegistration<FullNameGenerator> _serviceRegistration;

	private static class TestFullNameGenerator implements FullNameGenerator {

		@Override
		public String getFullName(
			String firstName, String middleName, String lastName) {

			return StringBundler.concat(
				firstName, " ", middleName, " ", lastName);
		}

		@Override
		public String getLocalizedFullName(
			String firstName, String middleName, String lastName, Locale locale,
			long prefixId, long suffixId) {

			if (firstName.equals("James")) {
				if (locale.equals(Locale.FRENCH)) {
					return "Jacques";
				}
			}

			return "not Jacques";
		}

		@Override
		public String[] splitFullName(String fullName) {
			return new String[] {"firstName", "middleName", "lastName"};
		}

	}

}