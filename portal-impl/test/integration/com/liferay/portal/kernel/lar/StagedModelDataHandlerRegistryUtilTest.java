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

package com.liferay.portal.kernel.lar;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.List;
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
public class StagedModelDataHandlerRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestStagedModelDataHandler testStagedModelDataHandler =
			new TestStagedModelDataHandler();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			StagedModelDataHandler.class, testStagedModelDataHandler,
			properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetStagedModelDataHandler() {
		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				TestStagedModelDataHandler.CLASS_NAMES[0]);

		Class<?> clazz = stagedModelDataHandler.getClass();

		Assert.assertEquals(
			TestStagedModelDataHandler.class.getName(), clazz.getName());
	}

	@Test
	public void testGetStagedModelDataHandlers() {
		List<StagedModelDataHandler<?>> stagedModelDataHandlers =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandlers();

		String testClassName = TestStagedModelDataHandler.class.getName();

		Assert.assertTrue(
			testClassName + " not found in " + stagedModelDataHandlers,
			stagedModelDataHandlers.removeIf(
				stagedModelDataHandler -> {
					Class<?> clazz = stagedModelDataHandler.getClass();

					return testClassName.equals(clazz.getName());
				}));
	}

	private static ServiceRegistration<StagedModelDataHandler>
		_serviceRegistration;

	private static class TestStagedModelDataHandler
		implements StagedModelDataHandler<User> {

		public static final String[] CLASS_NAMES = {
			TestStagedModelDataHandler.class.getName()
		};

		@Override
		public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData) {
		}

		@Override
		public void deleteStagedModel(User user) {
		}

		@Override
		public void exportStagedModel(
			PortletDataContext portletDataContext, User user) {
		}

		@Override
		public User fetchMissingReference(String uuid, long groupId) {
			return null;
		}

		@Override
		public User fetchStagedModelByUuidAndGroupId(
			String uuid, long groupId) {

			return null;
		}

		@Override
		public List<User> fetchStagedModelsByUuidAndCompanyId(
			String uuid, long companyId) {

			return null;
		}

		@Override
		public String[] getClassNames() {
			return CLASS_NAMES;
		}

		@Override
		public String getDisplayName(User user) {
			return null;
		}

		@Override
		public int[] getExportableStatuses() {
			return null;
		}

		@Override
		public Map<String, String> getReferenceAttributes(
			PortletDataContext portletDataContext, User user) {

			return null;
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		@Override
		public void importCompanyStagedModel(
			PortletDataContext portletDataContext, Element element) {
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		@Override
		public void importCompanyStagedModel(
			PortletDataContext portletDataContext, String uuid, long classPK) {
		}

		@Override
		public void importMissingReference(
			PortletDataContext portletDataContext, Element referenceElement) {
		}

		@Override
		public void importMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long classPK) {
		}

		@Override
		public void importStagedModel(
			PortletDataContext portletDataContext, User user) {
		}

		@Override
		public void restoreStagedModel(
			PortletDataContext portletDataContext, User user) {
		}

		@Override
		public boolean validateReference(
			PortletDataContext portletDataContext, Element referenceElement) {

			return false;
		}

	}

}