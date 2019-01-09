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

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthTokenWhitelistUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 * @author Tomas Polesovsky
 */
public class AuthTokenWhitelistUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Registry registry = RegistryUtil.getRegistry();

		TestAuthTokenIgnoreActions testAuthTokenIgnoreActions =
			new TestAuthTokenIgnoreActions();

		Map<String, Object> properties1 = new HashMap<>();

		properties1.put(
			PropsKeys.AUTH_TOKEN_IGNORE_ACTIONS,
			TestAuthTokenIgnoreActions.TEST_AUTH_TOKEN_IGNORE_ACTION_URL);
		properties1.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration1 = registry.registerService(
			Object.class, testAuthTokenIgnoreActions, properties1);

		TestAuthTokenIgnoreOrigins testAuthTokenIgnoreOrigins =
			new TestAuthTokenIgnoreOrigins();

		Map<String, Object> properties2 = new HashMap<>();

		properties2.put(
			PropsKeys.AUTH_TOKEN_IGNORE_ORIGINS,
			TestAuthTokenIgnoreOrigins.TEST_AUTH_TOKEN_IGNORE_ORIGINS_URL);
		properties2.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration2 = registry.registerService(
			Object.class, testAuthTokenIgnoreOrigins, properties2);

		TestAuthTokenIgnorePortlets testAuthTokenIgnorePortlets =
			new TestAuthTokenIgnorePortlets();

		Map<String, Object> properties3 = new HashMap<>();

		properties3.put(
			PropsKeys.AUTH_TOKEN_IGNORE_PORTLETS,
			TestAuthTokenIgnorePortlets.TEST_AUTH_TOKEN_IGNORE_PORTLETS_URL);
		properties3.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration3 = registry.registerService(
			Object.class, testAuthTokenIgnorePortlets, properties3);

		TestMVCActionCommand testMVCActionCommand = new TestMVCActionCommand();

		Map<String, Object> properties4 = new HashMap<>();

		properties4.put("auth.token.ignore.mvc.action", "1");
		properties4.put(
			"javax.portlet.name", TestMVCActionCommand.TEST_PORTLET_ID);
		properties4.put(
			"mvc.command.name", TestMVCActionCommand.TEST_MVC_COMMAND_NAME);
		properties4.put(
			"portlet.add.default.resource.check.whitelist.mvc.action", "1");
		properties4.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration4 = registry.registerService(
			MVCActionCommand.class, testMVCActionCommand, properties4);

		TestMVCRenderCommand testMVCRenderCommand = new TestMVCRenderCommand();

		Map<String, Object> properties5 = new HashMap<>();

		properties5.put(
			"javax.portlet.name", TestMVCRenderCommand.TEST_PORTLET_ID);
		properties5.put(
			"mvc.command.name", TestMVCRenderCommand.TEST_MVC_COMMAND_NAME);
		properties5.put(
			"portlet.add.default.resource.check.whitelist.mvc.action", "1");
		properties5.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration5 = registry.registerService(
			MVCRenderCommand.class, testMVCRenderCommand, properties5);

		TestMVCResourceCommand testMVCResourceCommand =
			new TestMVCResourceCommand();

		Map<String, Object> properties6 = new HashMap<>();

		properties6.put(
			"javax.portlet.name", TestMVCResourceCommand.TEST_PORTLET_ID);
		properties6.put(
			"mvc.command.name", TestMVCResourceCommand.TEST_MVC_COMMAND_NAME);
		properties6.put(
			"portlet.add.default.resource.check.whitelist.mvc.action", "1");
		properties6.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration6 = registry.registerService(
			MVCResourceCommand.class, testMVCResourceCommand, properties6);

		TestPortalAddDefaultResourceCheckWhitelist
			testPortalAddDefaultResourceCheckWhitelist =
				new TestPortalAddDefaultResourceCheckWhitelist();

		Map<String, Object> properties7 = new HashMap<>();

		properties7.put(
			PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST,
			TestPortalAddDefaultResourceCheckWhitelist.
				TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_URL);
		properties7.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration7 = registry.registerService(
			Object.class, testPortalAddDefaultResourceCheckWhitelist,
			properties7);

		TestPortalAddDefaultResourceCheckWhitelistActions
			testPortalAddDefaultResourceCheckWhitelistActions =
				new TestPortalAddDefaultResourceCheckWhitelistActions();

		Map<String, Object> properties8 = new HashMap<>();

		properties8.put(
			PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS,
			TestPortalAddDefaultResourceCheckWhitelistActions.
				TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS_URL);
		properties8.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration8 = registry.registerService(
			Object.class, testPortalAddDefaultResourceCheckWhitelistActions,
			properties8);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration1.unregister();
		_serviceRegistration2.unregister();
		_serviceRegistration3.unregister();
		_serviceRegistration4.unregister();
		_serviceRegistration5.unregister();
		_serviceRegistration6.unregister();
		_serviceRegistration7.unregister();
		_serviceRegistration8.unregister();
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletCSRFWhitelistActionsFromBundle() {
		Set<String> portletCSRFWhitelistActions =
			AuthTokenWhitelistUtil.getPortletCSRFWhitelistActions();

		Assert.assertTrue(
			portletCSRFWhitelistActions.toString(),
			portletCSRFWhitelistActions.contains(
				TestAuthTokenIgnoreActions.TEST_AUTH_TOKEN_IGNORE_ACTION_URL));
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletCSRFWhitelistActionsFromPortalProperties() {
		Set<String> portletCSRFWhitelistActions =
			AuthTokenWhitelistUtil.getPortletCSRFWhitelistActions();

		for (String authTokenIgnoreAction :
				PropsValues.AUTH_TOKEN_IGNORE_ACTIONS) {

			Assert.assertTrue(
				portletCSRFWhitelistActions.toString(),
				portletCSRFWhitelistActions.contains(authTokenIgnoreAction));
		}
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletCSRFWhitelistFromBundle() {
		Set<String> portletCSRFWhitelist =
			AuthTokenWhitelistUtil.getPortletCSRFWhitelist();

		Assert.assertTrue(
			portletCSRFWhitelist.toString(),
			portletCSRFWhitelist.contains(
				TestAuthTokenIgnorePortlets.
					TEST_AUTH_TOKEN_IGNORE_PORTLETS_URL));
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletCSRFWhitelistFromProperties() {
		Set<String> portletCSRFWhitelist =
			AuthTokenWhitelistUtil.getPortletCSRFWhitelist();

		for (String authTokenIgnoreAction :
				PropsValues.AUTH_TOKEN_IGNORE_PORTLETS) {

			Assert.assertTrue(
				portletCSRFWhitelist.toString(),
				portletCSRFWhitelist.contains(authTokenIgnoreAction));
		}
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletInvocationWhitelistActionsFromBundle() {
		Set<String> portletInvocationWhitelistActions =
			AuthTokenWhitelistUtil.getPortletInvocationWhitelistActions();

		String action =
			TestPortalAddDefaultResourceCheckWhitelistActions.
				TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS_URL;

		Assert.assertTrue(
			portletInvocationWhitelistActions.toString(),
			portletInvocationWhitelistActions.contains(action));
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletInvocationWhitelistActionsFromPortalProperties() {
		Set<String> portletInvocationWhitelistActions =
			AuthTokenWhitelistUtil.getPortletInvocationWhitelistActions();

		String[] actions =
			PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS;

		for (String action : actions) {
			Assert.assertTrue(
				portletInvocationWhitelistActions.toString(),
				portletInvocationWhitelistActions.contains(action));
		}
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletInvocationWhitelistFromBundle() {
		Set<String> portletInvocationWhitelist =
			AuthTokenWhitelistUtil.getPortletInvocationWhitelist();

		String action =
			TestPortalAddDefaultResourceCheckWhitelist.
				TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_URL;

		Assert.assertTrue(
			portletInvocationWhitelist.toString(),
			portletInvocationWhitelist.contains(action));
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	@Test
	public void testGetPortletInvocationWhitelistFromPortalProperties() {
		Set<String> portletInvocationWhitelist =
			AuthTokenWhitelistUtil.getPortletInvocationWhitelist();

		String[] actions =
			PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST;

		for (String action : actions) {
			Assert.assertTrue(
				portletInvocationWhitelist.toString(),
				portletInvocationWhitelist.contains(action));
		}
	}

	@Test
	public void testIsOriginCSRFWhitelistedFromBundle() {
		Assert.assertTrue(
			AuthTokenWhitelistUtil.isOriginCSRFWhitelisted(
				0,
				TestAuthTokenIgnoreOrigins.TEST_AUTH_TOKEN_IGNORE_ORIGINS_URL));
	}

	@Test
	public void testIsOriginCSRFWhitelistedFromPortalProperties() {
		String[] origins = PropsValues.AUTH_TOKEN_IGNORE_ORIGINS;

		for (String origin : origins) {
			Assert.assertTrue(
				AuthTokenWhitelistUtil.isOriginCSRFWhitelisted(0, origin));
		}
	}

	@Test
	public void testIsPortletCSRFWhitelistedForMVCActionCommand() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String namespace = PortalUtil.getPortletNamespace(
			TestMVCActionCommand.TEST_PORTLET_ID);

		mockHttpServletRequest.setParameter(
			namespace + ActionRequest.ACTION_NAME,
			TestMVCActionCommand.TEST_MVC_COMMAND_NAME);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestMVCActionCommand.TEST_PORTLET_ID);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletCSRFWhitelisted(
				mockHttpServletRequest, portlet));
	}

	@Test
	public void testIsPortletInvocationWhitelistedForMVCActionCommand() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String namespace = PortalUtil.getPortletNamespace(
			TestMVCActionCommand.TEST_PORTLET_ID);

		mockHttpServletRequest.setParameter(
			namespace + ActionRequest.ACTION_NAME,
			TestMVCActionCommand.TEST_MVC_COMMAND_NAME);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLifecycleAction(true);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestMVCActionCommand.TEST_PORTLET_ID);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletInvocationWhitelisted(
				mockHttpServletRequest, portlet));
	}

	@Test
	public void testIsPortletInvocationWhitelistedForMVCRenderCommand() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String namespace = PortalUtil.getPortletNamespace(
			TestMVCRenderCommand.TEST_PORTLET_ID);

		mockHttpServletRequest.setParameter(
			namespace + "mvcRenderCommandName",
			TestMVCRenderCommand.TEST_MVC_COMMAND_NAME);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLifecycleRender(true);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestMVCRenderCommand.TEST_PORTLET_ID);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletInvocationWhitelisted(
				mockHttpServletRequest, portlet));
	}

	@Test
	public void testIsPortletInvocationWhitelistedForMVCResourceCommand() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"p_p_id", TestMVCResourceCommand.TEST_PORTLET_ID);
		mockHttpServletRequest.setParameter(
			"p_p_resource_id", TestMVCResourceCommand.TEST_MVC_COMMAND_NAME);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLifecycleResource(true);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestMVCResourceCommand.TEST_PORTLET_ID);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletInvocationWhitelisted(
				mockHttpServletRequest, portlet));
	}

	@Test
	public void testIsPortletURLCSRFWhitelistedForMVCActionCommand() {
		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			new MockHttpServletRequest(), TestMVCActionCommand.TEST_PORTLET_ID,
			0, PortletRequest.ACTION_PHASE);

		liferayPortletURL.setParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand.TEST_MVC_COMMAND_NAME);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletURLCSRFWhitelisted(
				liferayPortletURL));
	}

	@Test
	public void testIsPortletURLInvocationWhitelistedForMVCActionCommand() {
		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			new MockHttpServletRequest(), TestMVCActionCommand.TEST_PORTLET_ID,
			0, PortletRequest.ACTION_PHASE);

		liferayPortletURL.setParameter(
			ActionRequest.ACTION_NAME,
			TestMVCActionCommand.TEST_MVC_COMMAND_NAME);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletURLPortletInvocationWhitelisted(
				liferayPortletURL));
	}

	@Test
	public void testIsPortletURLInvocationWhitelistedForMVCRenderCommand() {
		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			new MockHttpServletRequest(), TestMVCRenderCommand.TEST_PORTLET_ID,
			0, PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"mvcRenderCommandName", TestMVCRenderCommand.TEST_MVC_COMMAND_NAME);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletURLPortletInvocationWhitelisted(
				liferayPortletURL));
	}

	@Test
	public void testIsPortletURLInvocationWhitelistedForMVCResourceCommand() {
		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			new MockHttpServletRequest(),
			TestMVCResourceCommand.TEST_PORTLET_ID, 0,
			PortletRequest.RESOURCE_PHASE);

		liferayPortletURL.setResourceID(
			TestMVCResourceCommand.TEST_MVC_COMMAND_NAME);

		Assert.assertTrue(
			AuthTokenWhitelistUtil.isPortletURLPortletInvocationWhitelisted(
				liferayPortletURL));
	}

	private static ServiceRegistration<Object> _serviceRegistration1;
	private static ServiceRegistration<Object> _serviceRegistration2;
	private static ServiceRegistration<Object> _serviceRegistration3;
	private static ServiceRegistration<MVCActionCommand> _serviceRegistration4;
	private static ServiceRegistration<MVCRenderCommand> _serviceRegistration5;
	private static ServiceRegistration<MVCResourceCommand>
		_serviceRegistration6;
	private static ServiceRegistration<Object> _serviceRegistration7;
	private static ServiceRegistration<Object> _serviceRegistration8;

	private static class TestAuthTokenIgnoreActions {

		public static final String TEST_AUTH_TOKEN_IGNORE_ACTION_URL =
			"TEST_AUTH_TOKEN_IGNORE_ACTION_URL";

	}

	private static class TestAuthTokenIgnoreOrigins {

		public static final String TEST_AUTH_TOKEN_IGNORE_ORIGINS_URL =
			"TEST_AUTH_TOKEN_IGNORE_ORIGINS_URL";

	}

	private static class TestAuthTokenIgnorePortlets {

		public static final String TEST_AUTH_TOKEN_IGNORE_PORTLETS_URL =
			"TEST_AUTH_TOKEN_IGNORE_PORTLETS_URL";

	}

	private static class TestMVCActionCommand implements MVCActionCommand {

		public static final String TEST_MVC_COMMAND_NAME =
			"TEST_MVC_ACTION_COMMAND_NAME";

		public static final String TEST_PORTLET_ID = PortletKeys.PORTAL;

		@Override
		public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse) {

			return false;
		}

	}

	private static class TestMVCRenderCommand implements MVCRenderCommand {

		public static final String TEST_MVC_COMMAND_NAME =
			"TEST_MVC_RENDER_COMMAND_NAME";

		public static final String TEST_PORTLET_ID = PortletKeys.PORTAL;

		@Override
		public String render(
			RenderRequest renderRequest, RenderResponse renderResponse) {

			return null;
		}

	}

	private static class TestMVCResourceCommand implements MVCResourceCommand {

		public static final String TEST_MVC_COMMAND_NAME =
			"TEST_MVC_RESOURCE_COMMAND_NAME";

		public static final String TEST_PORTLET_ID = PortletKeys.PORTAL;

		@Override
		public boolean serveResource(
			ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) {

			return false;
		}

	}

	private static class TestPortalAddDefaultResourceCheckWhitelist {

		public static final String
			TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_URL =
				"TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_URL";

	}

	private static class TestPortalAddDefaultResourceCheckWhitelistActions {

		public static final String
			TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS_URL =
				"TEST_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS_URL";

	}

}