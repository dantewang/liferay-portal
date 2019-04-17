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

package com.liferay.portal.kernel.atom;

import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class AtomCollectionAdapterRegistryUtilTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());
	}

	@Before
	public void setUp() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			AtomCollectionAdapter.class,
			(AtomCollectionAdapter)ProxyUtil.newProxyInstance(
				AtomCollectionAdapter.class.getClassLoader(),
				new Class<?>[] {AtomCollectionAdapter.class},
				(proxy, method, args) -> {
					if ("getCollectionName".equals(method.getName())) {
						return _TEST_ATOM_COLLECTION_NAME;
					}

					return null;
				}));
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetAtomCollectionAdapter() {
		Registry registry = RegistryUtil.getRegistry();

		Assert.assertSame(
			registry.getService(_serviceRegistration.getServiceReference()),
			AtomCollectionAdapterRegistryUtil.getAtomCollectionAdapter(
				_TEST_ATOM_COLLECTION_NAME));
	}

	@Test
	public void testGetAtomCollectionAdapters() {
		List<AtomCollectionAdapter<?>> atomCollectionAdapters =
			AtomCollectionAdapterRegistryUtil.getAtomCollectionAdapters();

		Assert.assertTrue(
			_TEST_ATOM_COLLECTION_NAME + " not found in " +
				atomCollectionAdapters,
			atomCollectionAdapters.removeIf(
				atomCollectionAdapter -> _TEST_ATOM_COLLECTION_NAME.equals(
					atomCollectionAdapter.getCollectionName())));
	}

	private static final String _TEST_ATOM_COLLECTION_NAME =
		"TEST_ATOM_COLLECTION_NAME";

	private ServiceRegistration<AtomCollectionAdapter> _serviceRegistration;

}