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

import java.lang.reflect.Method;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class MethodCacheTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstructor() {
		new MethodCache();
	}

	@Test
	public void testGetAndReset() throws NoSuchMethodException {
		MethodKey testMethodKey = new MethodKey(
			TestClass.class, "testMethod", String.class);

		Method expectedMethod = TestClass.class.getMethod(
			"testMethod", String.class);

		Method actualMethod = MethodCache.get(testMethodKey);

		Assert.assertEquals(expectedMethod, actualMethod);

		Map<MethodKey, Method> methods = ReflectionTestUtil.getFieldValue(
			MethodKey.class, "_methods");

		Assert.assertEquals(methods.toString(), 1, methods.size());

		MethodCache.reset();

		Assert.assertEquals(methods.toString(), 0, methods.size());
	}

	private class TestClass {

		public void testMethod(String parameter) {
		}

	}

}