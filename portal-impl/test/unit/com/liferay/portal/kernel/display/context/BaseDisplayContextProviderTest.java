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

package com.liferay.portal.kernel.display.context;

import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class BaseDisplayContextProviderTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		_baseDisplayContextProvider = new BaseDisplayContextProvider<>(
			TestDisplayContextFactory.class);

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			TestDisplayContextFactory.class,
			new TestBaseDisplayContextFactoryImpl());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_baseDisplayContextProvider.destroy();
		_serviceRegistration.unregister();
	}

	@Test
	public void testDisplayContextHasBeenRegistered() throws Exception {
		TestDisplayContextFactory testDisplayContextFactoryExtension = null;

		Iterable<TestDisplayContextFactory> displayContextFactories =
			_baseDisplayContextProvider.getDisplayContextFactories();

		Iterator<TestDisplayContextFactory> iterator =
			displayContextFactories.iterator();

		while (iterator.hasNext()) {
			TestDisplayContextFactory testDisplayContextFactory =
				iterator.next();

			Class<?> clazz = testDisplayContextFactory.getClass();

			String className = clazz.getName();

			if (className.equals(
					TestBaseDisplayContextFactoryImpl.class.getName())) {

				testDisplayContextFactoryExtension = testDisplayContextFactory;
			}
		}

		Assert.assertNotNull(testDisplayContextFactoryExtension);
	}

	private static BaseDisplayContextProvider<TestDisplayContextFactory>
		_baseDisplayContextProvider;
	private static ServiceRegistration<TestDisplayContextFactory>
		_serviceRegistration;

	private static class TestBaseDisplayContextFactoryImpl
		implements TestDisplayContextFactory {
	}

}