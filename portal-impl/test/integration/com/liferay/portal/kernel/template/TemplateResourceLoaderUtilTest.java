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

package com.liferay.portal.kernel.template;

import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Philip Jones
 */
public class TemplateResourceLoaderUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestTemplateResourceLoader testTemplateResourceLoader =
			new TestTemplateResourceLoader();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			TemplateResourceLoader.class, testTemplateResourceLoader,
			properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetTemplateResource() throws TemplateException {
		TemplateResource templateResource =
			TemplateResourceLoaderUtil.getTemplateResource(
				TestTemplateResourceLoader.TEST_TEMPLATE_RESOURCE_LOADER_NAME,
				TestTemplateResource.TEST_TEMPLATE_RESOURCE_TEMPLATE_ID);

		Class<?> clazz = templateResource.getClass();

		Assert.assertEquals(
			TestTemplateResource.class.getName(), clazz.getName());
	}

	@Test
	public void testGetTemplateResourceLoader() throws TemplateException {
		TemplateResourceLoader templateResourceLoader =
			TemplateResourceLoaderUtil.getTemplateResourceLoader(
				TestTemplateResourceLoader.TEST_TEMPLATE_RESOURCE_LOADER_NAME);

		Class<?> clazz = templateResourceLoader.getClass();

		Assert.assertEquals(
			TestTemplateResourceLoader.class.getName(), clazz.getName());
	}

	@Test
	public void testGetTemplateResourceLoaderNames() {
		Set<String> templateResourceLoaderNames =
			TemplateResourceLoaderUtil.getTemplateResourceLoaderNames();

		Assert.assertTrue(
			templateResourceLoaderNames.toString(),
			templateResourceLoaderNames.contains(
				TestTemplateResourceLoader.TEST_TEMPLATE_RESOURCE_LOADER_NAME));
	}

	@Test
	public void testHasTemplateResource() throws TemplateException {
		Assert.assertTrue(
			TemplateResourceLoaderUtil.hasTemplateResource(
				TestTemplateResourceLoader.TEST_TEMPLATE_RESOURCE_LOADER_NAME,
				TestTemplateResource.TEST_TEMPLATE_RESOURCE_TEMPLATE_ID));
	}

	@Test
	public void testHasTemplateResourceLoader() {
		Assert.assertTrue(
			TemplateResourceLoaderUtil.hasTemplateResourceLoader(
				TestTemplateResourceLoader.TEST_TEMPLATE_RESOURCE_LOADER_NAME));
	}

	private static ServiceRegistration<TemplateResourceLoader>
		_serviceRegistration;

	private static class TestTemplateResource implements TemplateResource {

		public static final String TEST_TEMPLATE_RESOURCE_TEMPLATE_ID =
			"TEST_TEMPLATE_RESOURCE_TEMPLATE_ID";

		@Override
		public long getLastModified() {
			return 0;
		}

		@Override
		public Reader getReader() {
			return null;
		}

		@Override
		public String getTemplateId() {
			return TEST_TEMPLATE_RESOURCE_TEMPLATE_ID;
		}

		@Override
		public void readExternal(ObjectInput objectInput) {
		}

		@Override
		public void writeExternal(ObjectOutput objectOutput) {
		}

	}

	private static class TestTemplateResourceLoader
		implements TemplateResourceLoader {

		public static final String TEST_TEMPLATE_RESOURCE_LOADER_NAME =
			"TEST_TEMPLATE_RESOURCE_LOADER_NAME";

		@Override
		public void clearCache() {
		}

		@Override
		public void clearCache(String templateId) {
		}

		@Override
		public void destroy() {
		}

		@Override
		public String getName() {
			return TEST_TEMPLATE_RESOURCE_LOADER_NAME;
		}

		@Override
		public TemplateResource getTemplateResource(String templateId) {
			if (templateId.equals(
					TestTemplateResource.TEST_TEMPLATE_RESOURCE_TEMPLATE_ID)) {

				return new TestTemplateResource();
			}

			return null;
		}

		@Override
		public boolean hasTemplateResource(String templateId) {
			if (templateId.equals(
					TestTemplateResource.TEST_TEMPLATE_RESOURCE_TEMPLATE_ID)) {

				return true;
			}

			return false;
		}

	}

}