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

package com.liferay.portal.kernel.format;

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
public class PhoneNumberFormatUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestPhoneNumberFormat testPhoneNumberFormat =
			new TestPhoneNumberFormat();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			PhoneNumberFormat.class, testPhoneNumberFormat, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testFormat() {
		Assert.assertEquals(
			TestPhoneNumberFormat.FORMATTED,
			PhoneNumberFormatUtil.format(TestPhoneNumberFormat.UNFORMATTED));
	}

	@Test
	public void testGetPhoneNumberFormat() {
		PhoneNumberFormat phoneNumberFormat =
			PhoneNumberFormatUtil.getPhoneNumberFormat();

		Class<?> clazz = phoneNumberFormat.getClass();

		Assert.assertEquals(
			TestPhoneNumberFormat.class.getName(), clazz.getName());
	}

	@Test
	public void testStrip() {
		Assert.assertEquals(
			TestPhoneNumberFormat.UNFORMATTED,
			PhoneNumberFormatUtil.strip(TestPhoneNumberFormat.FORMATTED));
	}

	@Test
	public void testValidate() {
		Assert.assertTrue(
			PhoneNumberFormatUtil.validate(TestPhoneNumberFormat.FORMATTED));
		Assert.assertFalse(
			PhoneNumberFormatUtil.validate(TestPhoneNumberFormat.UNFORMATTED));
	}

	private static ServiceRegistration<PhoneNumberFormat> _serviceRegistration;

	private static class TestPhoneNumberFormat implements PhoneNumberFormat {

		public static final String FORMATTED = "123-456-7890";

		public static final String UNFORMATTED = "1234567890";

		@Override
		public String format(String phoneNumber) {
			if (phoneNumber.equals(UNFORMATTED)) {
				return FORMATTED;
			}

			return "";
		}

		@Override
		public String strip(String phoneNumber) {
			if (phoneNumber.equals(FORMATTED)) {
				return UNFORMATTED;
			}

			return "";
		}

		@Override
		public boolean validate(String phoneNumber) {
			if (phoneNumber.equals(FORMATTED)) {
				return true;
			}

			return false;
		}

	}

}