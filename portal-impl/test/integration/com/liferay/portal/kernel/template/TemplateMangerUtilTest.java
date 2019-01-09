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
import java.io.Writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Philip Jones
 */
public class TemplateMangerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestTemplateManager testTemplateManager = new TestTemplateManager();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("language.type", "English");
		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			TemplateManager.class, testTemplateManager, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetTemplate1() throws TemplateException {
		TestTemplateResource testTemplateResource = new TestTemplateResource();

		Template template = TemplateManagerUtil.getTemplate(
			TestTemplateManager.TEST_TEMPLATE_MANAGER_NAME,
			testTemplateResource, false);

		Class<?> clazz = template.getClass();

		Assert.assertEquals(TestTemplate.class.getName(), clazz.getName());
	}

	@Test
	public void testGetTemplate2() throws TemplateException {
		TestTemplateResource testTemplateResource = new TestTemplateResource();

		Template template = TemplateManagerUtil.getTemplate(
			TestTemplateManager.TEST_TEMPLATE_MANAGER_NAME,
			testTemplateResource, null, false);

		Class<?> clazz = template.getClass();

		Assert.assertEquals(TestTemplate.class.getName(), clazz.getName());
	}

	@Test
	public void testGetTemplateManager() {
		TemplateManager templateManager =
			TemplateManagerUtil.getTemplateManager(
				TestTemplateManager.TEST_TEMPLATE_MANAGER_NAME);

		Class<?> clazz = templateManager.getClass();

		Assert.assertEquals(
			TestTemplateManager.class.getName(), clazz.getName());
	}

	@Test
	public void testGetTemplateManagerName() {
		Set<String> templateManagerNames =
			TemplateManagerUtil.getTemplateManagerNames();

		Assert.assertTrue(
			templateManagerNames.toString(),
			templateManagerNames.contains(
				TestTemplateManager.TEST_TEMPLATE_MANAGER_NAME));
	}

	@Test
	public void testGetTemplateManagers() {
		Map<String, TemplateManager> templateManagers =
			TemplateManagerUtil.getTemplateManagers();

		TemplateManager templateManager = templateManagers.get(
			TestTemplateManager.TEST_TEMPLATE_MANAGER_NAME);

		Class<?> clazz = templateManager.getClass();

		Assert.assertEquals(
			TestTemplateManager.class.getName(), clazz.getName());
	}

	@Test
	public void testHasTemplateManager() {
		Assert.assertTrue(
			TemplateManagerUtil.hasTemplateManager(
				TestTemplateManager.TEST_TEMPLATE_MANAGER_NAME));
	}

	private static ServiceRegistration<TemplateManager> _serviceRegistration;

	private static class TestTemplate implements Template {

		@Override
		public void clear() {
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public void doProcessTemplate(Writer writer) throws Exception {
		}

		@Override
		public Set<Map.Entry<String, Object>> entrySet() {
			return null;
		}

		@Override
		public Object get(Object key) {
			return null;
		}

		@Override
		public Object get(String key) {
			return null;
		}

		@Override
		public String[] getKeys() {
			return null;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public Set<String> keySet() {
			return null;
		}

		@Override
		public void prepare(HttpServletRequest request) {
		}

		@Override
		public void processTemplate(Writer writer) {
		}

		@Override
		public Object put(String key, Object value) {
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> map) {
		}

		@Override
		public Object remove(Object key) {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Collection<Object> values() {
			return null;
		}

	}

	private static class TestTemplateManager implements TemplateManager {

		public static final String TEST_TEMPLATE_MANAGER_NAME =
			"TEST_TEMPLATE_MANAGER_NAME";

		@Override
		public void addContextObjects(
			Map<String, Object> contextObjects,
			Map<String, Object> newContextObjects) {
		}

		@Override
		public void addStaticClassSupport(
			Map<String, Object> contextObjects, String variableName,
			Class<?> variableClass) {
		}

		@Override
		public void addTaglibApplication(
			Map<String, Object> contextObjects, String applicationName,
			ServletContext servletContext) {
		}

		@Override
		public void addTaglibFactory(
			Map<String, Object> contextObjects, String taglibLiferayHash,
			ServletContext servletContext) {
		}

		@Override
		public void addTaglibRequest(
			Map<String, Object> contextObjects, String applicationName,
			HttpServletRequest request, HttpServletResponse response) {
		}

		@Override
		public void addTaglibSupport(
			Map<String, Object> contextObjects, HttpServletRequest request,
			HttpServletResponse response) {
		}

		@Override
		public void addTaglibTheme(
			Map<String, Object> contextObjects, String string,
			HttpServletRequest request, HttpServletResponse response) {
		}

		@Override
		public void destroy() {
		}

		@Override
		public void destroy(ClassLoader classLoader) {
		}

		@Override
		public String getName() {
			return TEST_TEMPLATE_MANAGER_NAME;
		}

		@Override
		public String[] getRestrictedVariables() {
			return null;
		}

		@Override
		public Template getTemplate(
			List<TemplateResource> templateResources, boolean restricted) {

			return getTemplate(templateResources.get(0), restricted);
		}

		@Override
		public Template getTemplate(
			List<TemplateResource> templateResources,
			TemplateResource errorTemplateResource, boolean restricted) {

			return getTemplate(templateResources, restricted);
		}

		@Override
		public Template getTemplate(
			TemplateResource templateResource, boolean restricted) {

			String templateId = templateResource.getTemplateId();

			if (templateId.equals(
					TestTemplateResource.TEST_TEMPLATE_RESOURCE_TEMPLATE_ID)) {

				return new TestTemplate();
			}

			return null;
		}

		@Override
		public Template getTemplate(
			TemplateResource templateResource,
			TemplateResource errorTemplateResource, boolean restricted) {

			return getTemplate(templateResource, restricted);
		}

		@Override
		public void init() {
		}

	}

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

}