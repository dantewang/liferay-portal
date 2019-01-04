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

package com.liferay.portal.kernel.workflow;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class WorkflowHandlerRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestWorkflowHandler testWorkflowHandler = new TestWorkflowHandler();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			WorkflowHandler.class, testWorkflowHandler, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() {
		_called = false;
	}

	@Test
	public void testGetWorkflowHandler() {
		WorkflowHandler<Object> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				TestWorkflowHandler.class.getName());

		Assert.assertEquals(
			TestWorkflowHandler.class.getName(),
			workflowHandler.getClassName());
	}

	@Test
	public void testGetWorkflowHandlers() {
		List<WorkflowHandler<?>> workflowHandlers =
			WorkflowHandlerRegistryUtil.getWorkflowHandlers();

		String testClassName = TestWorkflowHandler.class.getName();

		Assert.assertTrue(
			testClassName + " not found in " + workflowHandlers,
			workflowHandlers.removeIf(
				workflowHandler -> {
					Class<?> clazz = workflowHandler.getClass();

					return testClassName.equals(clazz.getName());
				}));
	}

	@Test
	public void testStartWorkflowInstance1() throws PortalException {
		ServiceContext serviceContext = new ServiceContext();

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			1, 1, 1, TestWorkflowHandler.class.getName(), 1, null,
			serviceContext, new HashMap<String, Serializable>());

		Assert.assertTrue(_called);
	}

	@Test
	public void testStartWorkflowInstance2() throws PortalException {
		ServiceContext serviceContext = new ServiceContext();

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			1, 1, 1, TestWorkflowHandler.class.getName(), 1, null,
			serviceContext);

		Assert.assertTrue(_called);
	}

	private static boolean _called;
	private static ServiceRegistration<WorkflowHandler> _serviceRegistration;

	private static class TestWorkflowHandler
		implements WorkflowHandler<Object> {

		@Override
		public AssetRenderer<Object> getAssetRenderer(long classPK) {
			return null;
		}

		@Override
		public AssetRendererFactory<Object> getAssetRendererFactory() {
			return null;
		}

		@Override
		public String getClassName() {
			_called = true;

			return TestWorkflowHandler.class.getName();
		}

		@Override
		public String getIconCssClass() {
			return null;
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		@Override
		public String getIconPath(LiferayPortletRequest liferayPortletRequest) {
			return null;
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		@Override
		public String getSummary(long classPK, Locale locale) {
			return null;
		}

		@Override
		public String getSummary(
			long classPK, PortletRequest portletRequest,
			PortletResponse portletResponse) {

			return null;
		}

		@Override
		public String getTitle(long classPK, Locale locale) {
			return null;
		}

		@Override
		public String getType(Locale locale) {
			return null;
		}

		@Override
		public PortletURL getURLEdit(
			long classPK, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) {

			return null;
		}

		@Override
		public String getURLEditWorkflowTask(
			long workflowTaskId, ServiceContext serviceContext) {

			return null;
		}

		@Override
		public PortletURL getURLViewDiffs(
			long classPK, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) {

			return null;
		}

		@Override
		public String getURLViewInContext(
			long classPK, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect) {

			return null;
		}

		@Override
		public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK) {

			return null;
		}

		@Override
		public boolean include(
			long classPK, HttpServletRequest request,
			HttpServletResponse response, String template) {

			return false;
		}

		@Override
		public boolean isAssetTypeSearchable() {
			return false;
		}

		@Override
		public boolean isScopeable() {
			return false;
		}

		@Override
		public boolean isVisible() {
			return false;
		}

		@Override
		public void startWorkflowInstance(
			long companyId, long groupId, long userId, long classPK,
			Object model, Map<String, Serializable> workflowContext) {
		}

		@Override
		public Object updateStatus(
			int status, Map<String, Serializable> workflowContext) {

			_called = true;

			return null;
		}

	}

}