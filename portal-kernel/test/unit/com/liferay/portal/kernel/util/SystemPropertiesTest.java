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

package com.liferay.portal.kernel.util;

import java.io.IOException;

import java.net.URL;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class SystemPropertiesTest {

	@Test
	public void testConstructor() {
		new SystemProperties();
	}

	@Test
	public void testGet() {
		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		SystemProperties.set(_TEST_BLANK_REFERENCE_KEY, "blankValue.${}");

		SystemProperties.set(
			_TEST_INEXISTENT_REFERENCE_KEY, "${test.inexistent.key}");

		SystemProperties.set(_TEST_KEY, _TEST_VALUE);

		SystemProperties.set(
			_TEST_LEFT_REFERENCE_KEY, "leftValue.${test.reference.key}");

		SystemProperties.set(_TEST_REFERENCE_KEY, "${test.key}");

		SystemProperties.set(
			_TEST_RIGHT_REFERENCE_KEY, "${test.reference.key}.rightValue");

		SystemProperties.set(_TEST_RIGHT_PART_REFERENCE_KEY, "test.key}");

		SystemProperties.set(_TEST_LEFT_PART_REFERENCE_KEY, "${test.key");

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_KEY));

		Assert.assertEquals(
			_TEST_VALUE, SystemProperties.get(_TEST_REFERENCE_KEY));

		Assert.assertEquals(
			"${test.inexistent.key}",
			SystemProperties.get(_TEST_INEXISTENT_REFERENCE_KEY));

		Assert.assertEquals(
			"leftValue." + _TEST_VALUE,
			SystemProperties.get(_TEST_LEFT_REFERENCE_KEY));

		Assert.assertEquals(
			_TEST_VALUE + ".rightValue",
			SystemProperties.get(_TEST_RIGHT_REFERENCE_KEY));

		Assert.assertEquals(
			"blankValue.${}", SystemProperties.get(_TEST_BLANK_REFERENCE_KEY));

		SystemProperties.clear(_TEST_KEY);

		Assert.assertEquals(
			"${test.key}", SystemProperties.get(_TEST_REFERENCE_KEY));

		Assert.assertNull(SystemProperties.get(_TEST_KEY));

		Assert.assertEquals(
			"defaultValue", SystemProperties.get(_TEST_KEY, "defaultValue"));

		Assert.assertEquals(
			"test.key}", SystemProperties.get(_TEST_RIGHT_PART_REFERENCE_KEY));

		Assert.assertEquals(
			"${test.key", SystemProperties.get(_TEST_LEFT_PART_REFERENCE_KEY));
	}

	@Test
	public void testGetArray() {
		Assert.assertTrue(
			ArrayUtil.isEmpty(SystemProperties.getArray(_TEST_ARRAY_KEY)));

		SystemProperties.set(_TEST_ARRAY_KEY, _TEST_ARRAY_VALUE);

		String[] expectedArray = {"test.array.value", "test.array.value"};

		Assert.assertArrayEquals(
			expectedArray, SystemProperties.getArray(_TEST_ARRAY_KEY));
	}

	@Test
	public void testGetProperties() {
		String prefix = "test.gp.";

		Map<String, String> propertiesWithoutPrefix =
			SystemProperties.getProperties(prefix, true);

		Assert.assertTrue(propertiesWithoutPrefix.isEmpty());

		Map<String, String> propertiesWithPrefix =
			SystemProperties.getProperties(prefix, false);

		Assert.assertTrue(propertiesWithPrefix.isEmpty());

		SystemProperties.set(_TEST_GP_KEY, _TEST_VALUE);

		propertiesWithoutPrefix = SystemProperties.getProperties(prefix, true);

		for (Map.Entry<String, String> property :
				propertiesWithoutPrefix.entrySet()) {

			Assert.assertEquals(_TEST_VALUE, property.getValue());
		}

		propertiesWithPrefix = SystemProperties.getProperties(prefix, false);

		for (Map.Entry<String, String> property :
				propertiesWithPrefix.entrySet()) {

			Assert.assertEquals(_TEST_VALUE, property.getValue());
		}
	}

	@Test
	public void testGetPropertyNames() {
		SystemProperties.set(_TEST_GPN_KEY, _TEST_VALUE);

		Set<String> propertyNames = SystemProperties.getPropertyNames();

		Assert.assertTrue(propertyNames.contains(_TEST_GPN_KEY));
	}

	@Test
	public void testLoad() {
		Set<String> propertyNames = SystemProperties.getPropertyNames();

		Assert.assertFalse(propertyNames.isEmpty());

		boolean quiet = GetterUtil.getBoolean(
			System.getProperty(SystemProperties.SYSTEM_PROPERTIES_QUIET));

		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		System.setProperty(
			SystemProperties.SYSTEM_PROPERTIES_QUIET, String.valueOf(!quiet));

		SystemProperties.load(classLoader);

		Assert.assertFalse(propertyNames.isEmpty());

		System.setProperty(
			SystemProperties.SYSTEM_PROPERTIES_SET_OVERRIDE,
			Boolean.FALSE.toString());

		System.clearProperty(_TEST_CASE_KEY);

		SystemProperties.load(classLoader);

		Assert.assertFalse(propertyNames.isEmpty());

		System.setProperty(
			SystemProperties.SYSTEM_PROPERTIES_SET, Boolean.FALSE.toString());

		SystemProperties.load(classLoader);

		Assert.assertFalse(propertyNames.isEmpty());

		TestClassLoader testClassLoader = new TestClassLoader(
			"system.properties");

		try {
			SystemProperties.load(testClassLoader);
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertTrue(eiie.getCause() instanceof IOException);
		}

		testClassLoader = new TestClassLoader("system-ext.properties");

		try {
			SystemProperties.load(testClassLoader);
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertTrue(eiie.getCause() instanceof IOException);
		}
	}

	@Test
	public void testSetAndClear() {
		Assert.assertNull(SystemProperties.get(_TEST_SC_KEY));

		SystemProperties.set(_TEST_SC_KEY, _TEST_VALUE);

		Assert.assertEquals(_TEST_VALUE, SystemProperties.get(_TEST_SC_KEY));

		SystemProperties.clear(_TEST_SC_KEY);

		Assert.assertNull(SystemProperties.get(_TEST_SC_KEY));
	}

	private static final String _TEST_ARRAY_KEY = "test.array.key";

	private static final String _TEST_ARRAY_VALUE =
		"test.array.value,test.array.value";

	private static final String _TEST_BLANK_REFERENCE_KEY =
		"test.blank.reference.key";

	private static final String _TEST_CASE_KEY = "test.case.key";

	private static final String _TEST_GP_KEY = "test.gp.key";

	private static final String _TEST_GPN_KEY = "test.gpn.key";

	private static final String _TEST_INEXISTENT_REFERENCE_KEY =
		"test.inexistent.reference.key";

	private static final String _TEST_KEY = "test.key";

	private static final String _TEST_LEFT_PART_REFERENCE_KEY =
		"test.left.part.reference.key";

	private static final String _TEST_LEFT_REFERENCE_KEY =
		"test.left.reference.key";

	private static final String _TEST_REFERENCE_KEY = "test.reference.key";

	private static final String _TEST_RIGHT_PART_REFERENCE_KEY =
		"test.right.part.reference.key";

	private static final String _TEST_RIGHT_REFERENCE_KEY =
		"test.right.reference.key";

	private static final String _TEST_SC_KEY = "test.sc.key";

	private static final String _TEST_VALUE = "test.value";

	private static class TestClassLoader extends ClassLoader {

		public TestClassLoader(String fileName) {
			_fileName = fileName;
		}

		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			if (name.equals(_fileName)) {
				throw new IOException();
			}

			return super.getResources(name);
		}

		private final String _fileName;

	}

}