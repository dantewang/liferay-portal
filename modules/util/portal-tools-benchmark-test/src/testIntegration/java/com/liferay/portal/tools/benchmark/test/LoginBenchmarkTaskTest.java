/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmark.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.benchmark.LoginBenchmarkTask;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;

import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class LoginBenchmarkTaskTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_originalVirtualHostsIgnoredHosts =
			ReflectionTestUtil.getAndSetFieldValue(
				PortalInstances.class, "_virtualHostsIgnoreHosts",
				Collections.singleton("localhost"));
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			PortalInstances.class, "_virtualHostsIgnoreHosts",
			_originalVirtualHostsIgnoredHosts);
	}

	@Test
	public void testExecute() throws Exception {
		String webId = RandomTestUtil.randomString();

		Company company = _companyLocalService.addCompany(
			null, webId, "127.0.0.1",
			webId + "." + RandomTestUtil.randomString(3), 0, true, null, null,
			null, null, null, null);

		User user = _userLocalService.getUserByEmailAddress(
			company.getCompanyId(),
			PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + "@" +
				company.getMx());

		user.setPassword(PasswordEncryptorUtil.encrypt("test"));
		user.setPasswordEncrypted(true);
		user.setPasswordReset(false);
		user.setReminderQueryQuestion("test");
		user.setReminderQueryAnswer("test");
		user.setAgreedToTermsOfUse(true);

		user = _userLocalService.updateUser(user);

		LoginBenchmarkTask loginBenchmarkTask = new LoginBenchmarkTask(
			"127.0.0.1", 8080, user.getEmailAddress(), "test");

		loginBenchmarkTask.execute();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Set<String> _originalVirtualHostsIgnoredHosts;

	@Inject
	private UserLocalService _userLocalService;

}