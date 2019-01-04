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

package com.liferay.portal.security.sso;

import com.liferay.portal.kernel.security.sso.SSO;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;

import org.junit.After;
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
public class SSOUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestSSOImpl testSSOImpl = new TestSSOImpl();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			SSO.class, testSSOImpl, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() throws Exception {
		_setLoginDialogDisable(1, true);
		_setLoginDialogDisable(2, false);
	}

	@After
	public void tearDown() throws Exception {
		for (Map.Entry<Long, String> entry :
				_oldLoginDialogDisableds.entrySet()) {

			PortletPreferences portletPreferences =
				PrefsPropsUtil.getPreferences(entry.getKey());

			String disabled = entry.getValue();

			if (disabled == null) {
				portletPreferences.reset(PropsKeys.LOGIN_DIALOG_DISABLED);
			}
			else {
				portletPreferences.setValue(
					PropsKeys.LOGIN_DIALOG_DISABLED, disabled);
			}

			portletPreferences.store();
		}
	}

	@Test
	public void testGetSessionExpirationRedirectURL() {
		Assert.assertEquals(
			"getSessionExpirationRedirectUrl:1",
			SSOUtil.getSessionExpirationRedirectURL(
				1, "sessionExpirationRedirectURL"));
	}

	@Test
	public void testGetSignInURL() {
		Assert.assertEquals(
			"signInURL:1", SSOUtil.getSignInURL(1, "signInURL"));
	}

	@Test
	public void testIsLoginRedirectRequired() {
		Assert.assertTrue(SSOUtil.isLoginRedirectRequired(1));
		Assert.assertFalse(SSOUtil.isLoginRedirectRequired(2));
	}

	@Test
	public void testIsRedirectRequired() {
		Assert.assertTrue(SSOUtil.isRedirectRequired(1));
		Assert.assertFalse(SSOUtil.isRedirectRequired(2));
	}

	@Test
	public void testIsSessionRedirectOnExpire() {
		Assert.assertTrue(SSOUtil.isSessionRedirectOnExpire(1));
		Assert.assertFalse(SSOUtil.isSessionRedirectOnExpire(2));
	}

	private void _setLoginDialogDisable(long companyId, boolean disabled)
		throws Exception {

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			companyId);

		_oldLoginDialogDisableds.put(
			companyId,
			portletPreferences.getValue(PropsKeys.LOGIN_DIALOG_DISABLED, null));

		portletPreferences.setValue(
			PropsKeys.LOGIN_DIALOG_DISABLED, String.valueOf(disabled));

		portletPreferences.store();
	}

	private static ServiceRegistration<SSO> _serviceRegistration;

	private final Map<Long, String> _oldLoginDialogDisableds = new HashMap<>();

	private static class TestSSOImpl implements SSO {

		@Override
		public String getSessionExpirationRedirectUrl(long companyId) {
			return "getSessionExpirationRedirectUrl:" + companyId;
		}

		@Override
		public String getSignInURL(long companyId, String defaultSignInURL) {
			return defaultSignInURL + ":" + companyId;
		}

		@Override
		public boolean isLoginRedirectRequired(long companyId) {
			if (companyId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isRedirectRequired(long companyId) {
			if (companyId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isSessionRedirectOnExpire(long companyId) {
			if (companyId == 1) {
				return true;
			}

			return false;
		}

	}

}