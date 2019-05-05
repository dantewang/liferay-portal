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
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.util.PropsImpl;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.portlet.MockPortletRequest;

/**
 * @author Leon Chi
 */
public class PortletToolbarTest {

	@BeforeClass
	public static void setUpClass() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		PropsUtil.setProps(new PropsImpl());

		RegistryUtil.setRegistry(new BasicRegistryImpl());

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			PortletToolbarContributorLocator.class,
			new TestPortletToolbarContributorLocator());
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetPortletTitleMenus() {
		PortletToolbar portletToolbar = new PortletToolbar();

		PortletRequest portletRequest = new MockPortletRequest();

		portletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST,
			new MockHttpServletRequest());

		List<Menu> menus = portletToolbar.getPortletTitleMenus(
			RandomTestUtil.randomString(), portletRequest,
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