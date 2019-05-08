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

package com.liferay.portlet.preferences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.test.LayoutTestUtil;

import javax.portlet.Portlet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
@RunWith(Arquillian.class)
public class PortletPreferencesFactoryImplGetPreferencesIdsTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			PortletPreferencesFactoryImplGetPreferencesIdsTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration1 = bundleContext.registerService(
			Portlet.class, new TestCompanyPortlet(),
			new HashMapDictionary<String, Object>() {
				{
					put("com.liferay.portlet.preferences-company-wide", "true");
					put("javax.portlet.name", TestCompanyPortlet.PORTLET_NAME);
				}
			});

		_serviceRegistration2 = bundleContext.registerService(
			Portlet.class, new TestGroupLayoutPortlet(),
			new HashMapDictionary<String, Object>() {
				{
					put(
						"com.liferay.portlet.preferences-company-wide",
						"false");
					put(
						"com.liferay.portlet.preferences-owned-by-group",
						"true");
					put(
						"com.liferay.portlet.preferences-unique-per-layout",
						"true");
					put(
						"javax.portlet.name",
						TestGroupLayoutPortlet.PORTLET_NAME);
				}
			});

		_serviceRegistration3 = bundleContext.registerService(
			Portlet.class, new TestGroupPortlet(),
			new HashMapDictionary<String, Object>() {
				{
					put(
						"com.liferay.portlet.preferences-company-wide",
						"false");
					put(
						"com.liferay.portlet.preferences-owned-by-group",
						"true");
					put(
						"com.liferay.portlet.preferences-unique-per-layout",
						"false");
					put("javax.portlet.name", TestGroupPortlet.PORTLET_NAME);
				}
			});

		_serviceRegistration4 = bundleContext.registerService(
			Portlet.class, new TestUserLayoutPortlet(),
			new HashMapDictionary<String, Object>() {
				{
					put(
						"com.liferay.portlet.preferences-company-wide",
						"false");
					put(
						"com.liferay.portlet.preferences-owned-by-group",
						"false");
					put(
						"com.liferay.portlet.preferences-unique-per-layout",
						"true");
					put(
						"javax.portlet.name",
						TestUserLayoutPortlet.PORTLET_NAME);
				}
			});

		_serviceRegistration5 = bundleContext.registerService(
			Portlet.class, new TestUserPortlet(),
			new HashMapDictionary<String, Object>() {
				{
					put(
						"com.liferay.portlet.preferences-company-wide",
						"false");
					put(
						"com.liferay.portlet.preferences-owned-by-group",
						"false");
					put(
						"com.liferay.portlet.preferences-unique-per-layout",
						"false");
					put("javax.portlet.name", TestUserPortlet.PORTLET_NAME);
				}
			});
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration1.unregister();
		_serviceRegistration2.unregister();
		_serviceRegistration3.unregister();
		_serviceRegistration4.unregister();
		_serviceRegistration5.unregister();
	}

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		ServiceTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addLayout(_group, true);
	}

	@Test
	public void testPreferencesOwnedByCompany() throws Exception {
		long siteGroupId = _layout.getGroupId();
		String portletId = TestCompanyPortlet.PORTLET_NAME;
		boolean modeEditGuest = false;

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout, portletId, "column-1", null);

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				siteGroupId, TestPropsValues.getUserId(), _layout, portletId,
				modeEditGuest);

		Assert.assertEquals(
			"The owner type should be of type company",
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the company",
			_layout.getCompanyId(), portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should not be a real value",
			PortletKeys.PREFS_PLID_SHARED, portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByGroup() throws Exception {
		long siteGroupId = _layout.getGroupId();
		String portletId = TestGroupPortlet.PORTLET_NAME;
		boolean modeEditGuest = false;

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				siteGroupId, TestPropsValues.getUserId(), _layout, portletId,
				modeEditGuest);

		Assert.assertEquals(
			"The owner type should be of type group",
			PortletKeys.PREFS_OWNER_TYPE_GROUP,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the group", siteGroupId,
			portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should not be a real value",
			PortletKeys.PREFS_PLID_SHARED, portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByGroupLayout() throws Exception {
		long siteGroupId = _layout.getGroupId();
		String portletId = TestGroupLayoutPortlet.PORTLET_NAME;
		boolean modeEditGuest = false;

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				siteGroupId, TestPropsValues.getUserId(), _layout, portletId,
				modeEditGuest);

		Assert.assertEquals(
			"The owner type should be of type layout",
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the default value",
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should be the PLID of the layout", _layout.getPlid(),
			portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByUser() throws Exception {
		long siteGroupId = _layout.getGroupId();
		String portletId = TestUserPortlet.PORTLET_NAME;
		boolean modeEditGuest = false;

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				siteGroupId, TestPropsValues.getUserId(), _layout, portletId,
				modeEditGuest);

		Assert.assertEquals(
			"The owner type should be of type user",
			PortletKeys.PREFS_OWNER_TYPE_USER,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the user who added it",
			TestPropsValues.getUserId(), portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should not be a real value",
			PortletKeys.PREFS_PLID_SHARED, portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesOwnedByUserLayout() throws Exception {
		long siteGroupId = _layout.getGroupId();
		String portletId = TestUserLayoutPortlet.PORTLET_NAME;
		boolean modeEditGuest = false;

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				siteGroupId, TestPropsValues.getUserId(), _layout, portletId,
				modeEditGuest);

		Assert.assertEquals(
			"The owner type should be of type user",
			PortletKeys.PREFS_OWNER_TYPE_USER,
			portletPreferencesIds.getOwnerType());
		Assert.assertEquals(
			"The owner ID should be the ID of the user who added it",
			TestPropsValues.getUserId(), portletPreferencesIds.getOwnerId());
		Assert.assertEquals(
			"The PLID should be the PLID of the layout", _layout.getPlid(),
			portletPreferencesIds.getPlid());
	}

	@Test
	public void testPreferencesWithModeEditGuestInPublicLayoutWithPermission()
		throws Exception {

		_layout = LayoutTestUtil.addLayout(_group, false);

		long siteGroupId = _layout.getGroupId();

		String portletId = TestGroupPortlet.PORTLET_NAME;
		boolean modeEditGuest = true;

		PortletPreferencesFactoryUtil.getPortletPreferencesIds(
			siteGroupId, TestPropsValues.getUserId(), _layout, portletId,
			modeEditGuest);
	}

	private static ServiceRegistration<Portlet> _serviceRegistration1;
	private static ServiceRegistration<Portlet> _serviceRegistration2;
	private static ServiceRegistration<Portlet> _serviceRegistration3;
	private static ServiceRegistration<Portlet> _serviceRegistration4;
	private static ServiceRegistration<Portlet> _serviceRegistration5;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	private static class TestCompanyPortlet extends MVCPortlet {

		public static final String PORTLET_NAME =
			"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
				"PreferencesIdsTest_TestCompanyPortlet";

	}

	private static class TestGroupLayoutPortlet extends MVCPortlet {

		public static final String PORTLET_NAME =
			"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
				"PreferencesIdsTest_TestGroupLayoutPortlet";

	}

	private static class TestGroupPortlet extends MVCPortlet {

		public static final String PORTLET_NAME =
			"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
				"PreferencesIdsTest_TestGroupPortlet";

	}

	private static class TestUserLayoutPortlet extends MVCPortlet {

		public static final String PORTLET_NAME =
			"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
				"PreferencesIdsTest_TestUserLayoutPortlet";

	}

	private static class TestUserPortlet extends MVCPortlet {

		public static final String PORTLET_NAME =
			"com_liferay_portlet_PortletPreferencesFactoryImplGet" +
				"PreferencesIdsTest_TestUserPortlet";

	}

}