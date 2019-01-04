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

package com.liferay.portal.xmlrpc;

import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.portal.kernel.xmlrpc.Response;
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
public class XmlRpcMethodUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestMethod testMethod = new TestMethod();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			Method.class, testMethod, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testNoReturn() {
		Method method = XmlRpcMethodUtil.getMethod(
			TestMethod.TOKEN, TestMethod.METHOD_NAME);

		Class<?> clazz = method.getClass();

		Assert.assertEquals(TestMethod.class.getName(), clazz.getName());
	}

	private static ServiceRegistration<Method> _serviceRegistration;

	private static class TestMethod implements Method {

		public static final String METHOD_NAME = "METHOD_NAME";

		public static final String TOKEN = "TOKEN";

		@Override
		public Response execute(long companyId) {
			return null;
		}

		@Override
		public String getMethodName() {
			return METHOD_NAME;
		}

		@Override
		public String getToken() {
			return TOKEN;
		}

		@Override
		public boolean setArguments(Object[] arguments) {
			return false;
		}

	}

}