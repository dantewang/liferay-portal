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

import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class XmlRpcMethodUtilTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			Method.class,
			(Method)ProxyUtil.newProxyInstance(
				Method.class.getClassLoader(), new Class<?>[] {Method.class},
				(proxy, method, args) -> {
					if ("getToken".equals(method.getName())) {
						return _TOKEN;
					}

					if ("getMethodName".equals(method.getName())) {
						return _METHOD_NAME;
					}

					if ("equals".equals(method.getName())) {
						return proxy == args[0];
					}

					return null;
				}));
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testNoReturn() {
		Registry registry = RegistryUtil.getRegistry();

		Assert.assertSame(
			registry.getService(_serviceRegistration.getServiceReference()),
			XmlRpcMethodUtil.getMethod(_TOKEN, _METHOD_NAME));
	}

	private static final String _METHOD_NAME = "METHOD_NAME";

	private static final String _TOKEN = "TOKEN";

	private static ServiceRegistration<Method> _serviceRegistration;

}