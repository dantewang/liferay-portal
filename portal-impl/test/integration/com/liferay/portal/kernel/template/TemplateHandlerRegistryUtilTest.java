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

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
public class TemplateHandlerRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestTemplateHandler testTemplateHandler = new TestTemplateHandler();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			TemplateHandler.class, testTemplateHandler, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetClassNameIds() {
		long classNameId = PortalUtil.getClassNameId(
			TestTemplateHandler.class.getName());

		Assert.assertTrue(
			ArrayUtil.contains(
				TemplateHandlerRegistryUtil.getClassNameIds(), classNameId));
	}

	@Test
	public void testGetTemplateHandlerByClassName() {
		TemplateHandler templateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(
				TestTemplateHandler.class.getName());

		Assert.assertEquals(
			TestTemplateHandler.class.getName(),
			templateHandler.getClassName());
	}

	@Test
	public void testGetTemplateHandlerByClassNameId() {
		long classNameId = PortalUtil.getClassNameId(
			TestTemplateHandler.class.getName());

		TemplateHandler templateHandler =
			TemplateHandlerRegistryUtil.getTemplateHandler(classNameId);

		Assert.assertEquals(
			TestTemplateHandler.class.getName(),
			templateHandler.getClassName());
	}

	@Test
	public void testGetTemplateHandlers() {
		boolean exists = false;

		List<TemplateHandler> templateHandlers =
			TemplateHandlerRegistryUtil.getTemplateHandlers();

		for (TemplateHandler templateHandler : templateHandlers) {
			String className = templateHandler.getClassName();

			if (className.equals(TestTemplateHandler.class.getName())) {
				exists = true;

				break;
			}
		}

		Assert.assertTrue(exists);
	}

	public static class TestTemplateHandler implements TemplateHandler {

		@Override
		public String getClassName() {
			return TestTemplateHandler.class.getName();
		}

		@Override
		public Map<String, Object> getCustomContextObjects() {
			return Collections.emptyMap();
		}

		@Override
		public List<Element> getDefaultTemplateElements() {
			return Collections.emptyList();
		}

		@Override
		public String getDefaultTemplateKey() {
			return null;
		}

		@Override
		public String getName(Locale locale) {
			return null;
		}

		@Override
		public String getResourceName() {
			return null;
		}

		@Override
		public String[] getRestrictedVariables(String language) {
			return null;
		}

		@Override
		public String getTemplatesHelpContent(String language) {
			return null;
		}

		@Override
		public String getTemplatesHelpPath(String language) {
			return null;
		}

		@Override
		public String getTemplatesHelpPropertyKey() {
			return null;
		}

		@Override
		public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale) {

			return null;
		}

		@Override
		public boolean isDisplayTemplateHandler() {
			return false;
		}

	}

	private static ServiceRegistration<TemplateHandler> _serviceRegistration;

}