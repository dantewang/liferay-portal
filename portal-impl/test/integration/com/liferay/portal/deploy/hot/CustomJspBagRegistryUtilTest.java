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

package com.liferay.portal.deploy.hot;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.url.URLContainer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceRegistration;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class CustomJspBagRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Registry registry = RegistryUtil.getRegistry();

		TestCustomJspBag testCustomJspBag = new TestCustomJspBag();

		Map<String, Object> properties1 = new HashMap<>();

		properties1.put("context.id", "TestCustomJspBag");
		properties1.put("context.name", "Test Custom JSP Bag");
		properties1.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration1 = registry.registerService(
			CustomJspBag.class, testCustomJspBag, properties1);

		TestGlobalCustomJspBag testGlobalCustomJspBag =
			new TestGlobalCustomJspBag();

		Map<String, Object> properties2 = new HashMap<>();

		properties2.put("context.id", "TestGlobalCustomJspBag");
		properties2.put("context.name", "Test Global Custom JSP Bag");
		properties2.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration2 = registry.registerService(
			CustomJspBag.class, testGlobalCustomJspBag, properties2);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration1.unregister();
		_serviceRegistration2.unregister();
	}

	@Test
	public void testGetCustomJspBags() {
		Assert.assertTrue(
			"TestCustomJspBag not found",
			_findCustomJspBag("TestCustomJspBag"));
	}

	@Test
	public void testGetGlobalCustomJspBags() {
		Assert.assertTrue(
			"TestGlobalCustomJspBag not found",
			_findCustomJspBag("TestGlobalCustomJspBag"));
	}

	private boolean _findCustomJspBag(String targetContextId) {
		Map<ServiceReference<CustomJspBag>, CustomJspBag> customJspBags =
			CustomJspBagRegistryUtil.getCustomJspBags();

		for (ServiceReference<CustomJspBag> serviceReference :
				customJspBags.keySet()) {

			String contextId = GetterUtil.getString(
				serviceReference.getProperty("context.id"));

			if (contextId.equals(targetContextId)) {
				return true;
			}
		}

		return false;
	}

	private static ServiceRegistration<CustomJspBag> _serviceRegistration1;
	private static ServiceRegistration<CustomJspBag> _serviceRegistration2;

	private static class TestCustomJspBag implements CustomJspBag {

		@Override
		public String getCustomJspDir() {
			return StringPool.SLASH;
		}

		@Override
		public List<String> getCustomJsps() {
			return _customJsps;
		}

		@Override
		public URLContainer getURLContainer() {
			return _urlContainer;
		}

		@Override
		public boolean isCustomJspGlobal() {
			return false;
		}

		private final List<String> _customJsps = new ArrayList<>();

		private final URLContainer _urlContainer = new URLContainer() {

			@Override
			public URL getResource(String name) {
				Class<?> clazz = getClass();

				return clazz.getResource("dependencies/bottom-ext.jsp");
			}

			@Override
			public Set<String> getResources(String path) {
				return Collections.singleton(
					"/html/common/themes/bottom-ext.jsp");
			}

		};

	}

	private static class TestGlobalCustomJspBag implements CustomJspBag {

		@Override
		public String getCustomJspDir() {
			return StringPool.SLASH;
		}

		@Override
		public List<String> getCustomJsps() {
			return _customJsps;
		}

		@Override
		public URLContainer getURLContainer() {
			return _urlContainer;
		}

		@Override
		public boolean isCustomJspGlobal() {
			return true;
		}

		private final List<String> _customJsps = new ArrayList<>();

		private final URLContainer _urlContainer = new URLContainer() {

			@Override
			public URL getResource(String name) {
				Class<?> clazz = getClass();

				return clazz.getResource("dependencies/bottom-ext.jsp");
			}

			@Override
			public Set<String> getResources(String path) {
				return Collections.singleton(
					"/html/common/themes/bottom-ext.jsp");
			}

		};

	}

}