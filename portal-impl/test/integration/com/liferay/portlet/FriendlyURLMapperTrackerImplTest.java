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

package com.liferay.portlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapperTracker;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.Router;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.internal.FriendlyURLMapperTrackerImpl;
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
 * @author Philip Jones
 */
public class FriendlyURLMapperTrackerImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestFriendlyURLMapper testFriendlyURLMapper =
			new TestFriendlyURLMapper();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put(
			"javax.portlet.name", "FriendlyURLMapperTrackerImplTest");
		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			FriendlyURLMapper.class, testFriendlyURLMapper, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetFriendlyURLMapper() throws Exception {
		Portlet portlet = new PortletImpl();

		portlet.setPortletClass(MVCPortlet.class.getName());
		portlet.setPortletId("FriendlyURLMapperTrackerImplTest");

		FriendlyURLMapperTracker friendlyURLMapperTracker =
			new FriendlyURLMapperTrackerImpl(portlet);

		FriendlyURLMapper friendlyURLMapper =
			friendlyURLMapperTracker.getFriendlyURLMapper();

		Class<?> clazz = friendlyURLMapper.getClass();

		Assert.assertEquals(
			TestFriendlyURLMapper.class.getName(), clazz.getName());
	}

	private static ServiceRegistration<FriendlyURLMapper> _serviceRegistration;

	private static class TestFriendlyURLMapper implements FriendlyURLMapper {

		@Override
		public String buildPath(LiferayPortletURL liferayPortletURL) {
			return null;
		}

		@Override
		public String getMapping() {
			return null;
		}

		@Override
		public String getPortletId() {
			return null;
		}

		@Override
		public Router getRouter() {
			return null;
		}

		@Override
		public boolean isCheckMappingWithPrefix() {
			return false;
		}

		@Override
		public boolean isPortletInstanceable() {
			return false;
		}

		@Override
		public void populateParams(
			String friendlyURLPath, Map<String, String[]> parameterMap,
			Map<String, Object> requestContext) {
		}

		@Override
		public void setMapping(String mapping) {
		}

		@Override
		public void setPortletId(String portletId) {
		}

		@Override
		public void setPortletInstanceable(boolean portletInstanceable) {
		}

		@Override
		public void setRouter(Router router) {
		}

	}

}