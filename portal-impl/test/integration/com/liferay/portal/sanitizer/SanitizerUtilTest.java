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

package com.liferay.portal.sanitizer;

import com.liferay.portal.kernel.sanitizer.BaseSanitizer;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
public class SanitizerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestSanitizer testSanitizer = new TestSanitizer();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			Sanitizer.class, testSanitizer, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() {
		_called = false;
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSanitize1() throws SanitizerException {
		SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			"bytes".getBytes());

		Assert.assertTrue(_called);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSanitize2() throws SanitizerException {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			byteArrayOutputStream.toByteArray());

		SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			byteArrayInputStream, byteArrayOutputStream);

		Assert.assertTrue(_called);
	}

	@Test
	public void testSanitize3() throws SanitizerException {
		String value = SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType", "s");

		Assert.assertEquals("1:1", value);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSanitize4() throws SanitizerException {
		SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			Sanitizer.MODE_ALL, "bytes".getBytes(),
			new HashMap<String, Object>());

		Assert.assertTrue(_called);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSanitize5() throws SanitizerException {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			byteArrayOutputStream.toByteArray());

		SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			Sanitizer.MODE_ALL, byteArrayInputStream, byteArrayOutputStream,
			new HashMap<String, Object>());

		Assert.assertTrue(_called);
	}

	@Test
	public void testSanitize6() throws SanitizerException {
		String value = SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			Sanitizer.MODE_ALL, "s", new HashMap<String, Object>());

		Assert.assertEquals("1:1", value);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSanitize7() throws SanitizerException {
		SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			Sanitizer.MODE_ALL, "bytes".getBytes(),
			new HashMap<String, Object>());

		Assert.assertTrue(_called);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSanitize8() throws SanitizerException {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			byteArrayOutputStream.toByteArray());

		SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			new String[] {
				Sanitizer.MODE_ALL, Sanitizer.MODE_BAD_WORDS, Sanitizer.MODE_XSS
			},
			byteArrayInputStream, byteArrayOutputStream,
			new HashMap<String, Object>());

		Assert.assertTrue(_called);
	}

	@Test
	public void testSanitize9() throws SanitizerException {
		String value = SanitizerUtil.sanitize(
			1, 1, 1, TestSanitizer.class.getName(), 1, "contentType",
			new String[] {
				Sanitizer.MODE_ALL, Sanitizer.MODE_BAD_WORDS, Sanitizer.MODE_XSS
			},
			"s", new HashMap<String, Object>());

		Assert.assertEquals("1:1", value);
	}

	private static boolean _called;
	private static ServiceRegistration<Sanitizer> _serviceRegistration;

	private static class TestSanitizer extends BaseSanitizer {

		@Override
		public String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String[] modes, String content,
			Map<String, Object> options) {

			_called = true;

			return companyId + ":" + groupId;
		}

	}

}