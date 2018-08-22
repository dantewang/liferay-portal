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
		Map<MethodKey, Method> methods = ReflectionTestUtil.getFieldValue(
			MethodCache.class, "_methods");

		MethodKey publicMethodKey = new MethodKey(
			TestMethodCache.class, "publicMethod", String.class);

		Method method = MethodCache.get(publicMethodKey);

		Assert.assertTrue(method.isAccessible());

		Assert.assertEquals(methods.toString(), 1, methods.size());

		method.setAccessible(false);

		Method method1 = MethodCache.get(publicMethodKey);

		Assert.assertSame(method, method1);

		Assert.assertFalse(method.isAccessible());
		Assert.assertEquals(methods.toString(), 1, methods.size());

		MethodCache.reset();

		Assert.assertEquals(methods.toString(), 0, methods.size());
	}

	private class TestMethodCache {

		public void publicMethod(String string) {
		}

	}

}