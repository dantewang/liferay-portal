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

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Manuel de la Peña
 */
public class AuthTokenUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestAuthToken testAuthToken = new TestAuthToken();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			AuthToken.class, testAuthToken, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testAddCSRFToken() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			request, PortletKeys.PORTAL, 0, PortletRequest.ACTION_PHASE);

		AuthTokenUtil.addCSRFToken(request, liferayPortletURL);

		Assert.assertEquals(
			"TEST_TOKEN", liferayPortletURL.getParameter("p_auth"));
	}

	@Test
	public void testAddPortletInvocationToken() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			request, PortletKeys.PORTAL, 0, PortletRequest.ACTION_PHASE);

		AuthTokenUtil.addPortletInvocationToken(request, liferayPortletURL);

		Assert.assertEquals(
			"TEST_TOKEN_BY_PLID_AND_PORTLET_ID",
			liferayPortletURL.getParameter("p_p_auth"));
	}

	@Test
	public void testGetToken() {
		Assert.assertEquals(
			"TEST_TOKEN", AuthTokenUtil.getToken(new MockHttpServletRequest()));
	}

	@Test
	public void testGetTokenByPlidAndPortletId() {
		Assert.assertEquals(
			"TEST_TOKEN_BY_PLID_AND_PORTLET_ID",
			AuthTokenUtil.getToken(
				new MockHttpServletRequest(), 0L,
				RandomTestUtil.randomString()));
	}

	@Test
	public void testIsValidPortletInvocationToken() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"p_p_auth", "VALID_PORTLET_INVOCATION_TOKEN");

		Assert.assertTrue(
			AuthTokenUtil.isValidPortletInvocationToken(
				mockHttpServletRequest, null, null));

		mockHttpServletRequest.setParameter(
			"p_p_auth", "INVALID_PORTLET_INVOCATION_TOKEN");

		Assert.assertFalse(
			AuthTokenUtil.isValidPortletInvocationToken(
				mockHttpServletRequest, null, null));
	}

	private static ServiceRegistration<AuthToken> _serviceRegistration;

	private static class TestAuthToken implements AuthToken {

		@Override
		public void addCSRFToken(
			HttpServletRequest request, LiferayPortletURL liferayPortletURL) {

			liferayPortletURL.setParameter("p_auth", "TEST_TOKEN");
		}

		@Override
		public void addPortletInvocationToken(
			HttpServletRequest request, LiferayPortletURL liferayPortletURL) {

			liferayPortletURL.setParameter(
				"p_p_auth", "TEST_TOKEN_BY_PLID_AND_PORTLET_ID");
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		@Override
		public void check(HttpServletRequest request) {
		}

		@Override
		public void checkCSRFToken(HttpServletRequest request, String origin) {
		}

		@Override
		public String getToken(HttpServletRequest request) {
			return "TEST_TOKEN";
		}

		@Override
		public String getToken(
			HttpServletRequest request, long plid, String portletId) {

			return "TEST_TOKEN_BY_PLID_AND_PORTLET_ID";
		}

		@Override
		public boolean isValidPortletInvocationToken(
			HttpServletRequest request, Layout layout, Portlet portlet) {

			String tokenValue = request.getParameter("p_p_auth");

			return "VALID_PORTLET_INVOCATION_TOKEN".equals(tokenValue);
		}

		/**
		 * @deprecated As of Wilberforce (7.0.x)
		 */
		@Deprecated
		@Override
		public boolean isValidPortletInvocationToken(
			HttpServletRequest request, long plid, String portletId,
			String strutsAction, String tokenValue) {

			return "VALID_PORTLET_INVOCATION_TOKEN".equals(tokenValue);
		}

	}

}