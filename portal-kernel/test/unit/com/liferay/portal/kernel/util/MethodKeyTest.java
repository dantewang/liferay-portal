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

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class MethodKeyTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		_methodKey = new MethodKey(_clazz, "testMethod", String.class);
	}

	@Test
	public void testConstructors() throws NoSuchMethodException {
		Assert.assertEquals(_clazz, _methodKey.getDeclaringClass());
		Assert.assertEquals("testMethod", _methodKey.getMethodName());
		Assert.assertEquals(String.class, _methodKey.getParameterTypes()[0]);

		MethodKey methodKey = new MethodKey();

		Assert.assertNull(methodKey.getDeclaringClass());
		Assert.assertNull(methodKey.getMethodName());
		Assert.assertNull(methodKey.getParameterTypes());

		Method method = _clazz.getMethod("testMethod", String.class);

		methodKey = new MethodKey(method);

		Assert.assertEquals(_clazz, methodKey.getDeclaringClass());
		Assert.assertEquals("testMethod", methodKey.getMethodName());
		Assert.assertEquals(String.class, methodKey.getParameterTypes()[0]);

		methodKey = new MethodKey(
			"com.liferay.portal.kernel.util.MethodKeyTest$TestMethodKey",
			"testMethod", String.class);

		Assert.assertEquals(_clazz, methodKey.getDeclaringClass());
		Assert.assertEquals("testMethod", methodKey.getMethodName());
		Assert.assertEquals(String.class, methodKey.getParameterTypes()[0]);

		try {
			new MethodKey("ClassNotFound", "testMethod", String.class);
			Assert.fail("No RuntimeException throw!");
		}
		catch (RuntimeException re) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(_methodKey.equals(_methodKey));

		TestMethodKey testMethodKey = new TestMethodKey();

		Assert.assertFalse(_methodKey.equals(testMethodKey));

		MethodKey methodKey = new MethodKey(_clazz, "testMethod", String.class);

		Assert.assertTrue(_methodKey.equals(methodKey));

		methodKey = new MethodKey(_clazz, "testMethod", int.class);

		Assert.assertFalse(_methodKey.equals(methodKey));

		methodKey = new MethodKey(_clazz, "testMethod1", String.class);

		Assert.assertFalse(_methodKey.equals(methodKey));

		methodKey = new MethodKey(
			TestMethodKey1.class, "testMethod", String.class);

		Assert.assertFalse(_methodKey.equals(methodKey));
	}

	@Test
	public void testGetMethod()
		throws IllegalAccessException, InvocationTargetException,
			   NoSuchMethodException {

		Method method = _methodKey.getMethod();
		TestMethodKey testMethodKey = new TestMethodKey();

		String result = (String)method.invoke(testMethodKey, "test");

		Assert.assertEquals("test", result);
	}

	@Test
	public void testHashCode() throws NoSuchMethodException {
		Method method = _clazz.getMethod("testMethod", String.class);

		Assert.assertEquals(method.hashCode(), _methodKey.hashCode());
	}

	@Test
	public void testToString() {
		Assert.assertEquals(
			"com.liferay.portal.kernel.util.MethodKeyTest$TestMethodKey." +
				"testMethod(java.lang.String)",
			_methodKey.toString());

		MethodKey methodKey = new MethodKey(_clazz, "testMethod", String.class);

		ReflectionTestUtil.setFieldValue(methodKey, "_toString", "testString");

		Assert.assertEquals("testString", methodKey.toString());
	}

	@Test
	public void testTransform() throws ClassNotFoundException {
		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		MethodKey methodKey = _methodKey.transform(classLoader);

		Assert.assertTrue(_methodKey.equals(methodKey));
	}

	@Test
	public void testWriteAndReadExternal()
		throws ClassNotFoundException, IOException {

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
			byteArrayOutputStream);

		_methodKey.writeExternal(objectOutputStream);

		objectOutputStream.close();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			byteArrayOutputStream.toByteArray());

		ObjectInputStream objectInputStream = new ObjectInputStream(
			byteArrayInputStream);

		MethodKey methodKey = new MethodKey();

		methodKey.readExternal(objectInputStream);

		objectInputStream.close();

		Assert.assertTrue(_methodKey.equals(methodKey));
	}

	private final Class<?> _clazz = TestMethodKey.class;
	private MethodKey _methodKey;

	private class TestMethodKey {

		public void testMethod(int string) {
		}

		public String testMethod(String string) {
			return string;
		}

		public void testMethod1(String string) {
		}

	}

	private class TestMethodKey1 {

		public void testMethod(String string) {
		}

	}

}