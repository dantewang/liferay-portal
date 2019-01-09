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

package com.liferay.portal.kernel.portlet.toolbar;

import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;
import com.liferay.registry.dependency.ServiceDependencyManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Philip Jones
 */
public class PortletToolbarTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestPortletToolbarContributorLocator
			testPortletToolbarContributorLocator =
				new TestPortletToolbarContributorLocator();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			PortletToolbarContributorLocator.class,
			testPortletToolbarContributorLocator, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetPortletTitleMenus() {
		PortletToolbar portletToolbar = new PortletToolbar();

		ServiceDependencyManager serviceDependencyManager =
			new ServiceDependencyManager();

		serviceDependencyManager.registerDependencies(
			PortletToolbarContributorLocator.class);

		serviceDependencyManager.waitForDependencies(1000);

		List<Menu> menus = portletToolbar.getPortletTitleMenus(
			RandomTestUtil.randomString(),
			ProxyFactory.newDummyInstance(PortletRequest.class),
			ProxyFactory.newDummyInstance(PortletResponse.class));

		Assert.assertTrue(
			"Unable to retrieve menu with label " +
				TestPortletToolbarContributor.LABEL,
			menus.removeIf(
				menu -> TestPortletToolbarContributor.LABEL.equals(
					menu.getLabel())));
	}

	private static ServiceRegistration<PortletToolbarContributorLocator>
		_serviceRegistration;

	private static class TestPortletToolbarContributor
		implements PortletToolbarContributor {

		public static final String LABEL = "LABEL";

		@Override
		public List<Menu> getPortletTitleMenus(
			PortletRequest portletRequest, PortletResponse portletResponse) {

			List<Menu> portletTitleMenus = new ArrayList<>();

			Menu menu = new Menu();

			menu.setLabel(LABEL);

			portletTitleMenus.add(menu);

			return portletTitleMenus;
		}

	}

	private static class TestPortletToolbarContributorLocator
		implements PortletToolbarContributorLocator {

		@Override
		public List<PortletToolbarContributor> getPortletToolbarContributors(
			String portletId, PortletRequest portletRequest) {

			List<PortletToolbarContributor> portletToolbarContributors =
				new ArrayList<>();

			TestPortletToolbarContributor testPortletToolbarContributor =
				new TestPortletToolbarContributor();

			portletToolbarContributors.add(testPortletToolbarContributor);

			return portletToolbarContributors;
		}

	}

}