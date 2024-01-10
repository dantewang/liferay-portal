/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.benchmark.test.util.Login;

import com.liferay.portal.benchmark.test.util.Statistics;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.util.PropsValues;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
@DataGuard(scope = DataGuard.Scope.METHOD)
public class LoginTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExecute() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		company.setVirtualHostname("127.0.0.1");

		company = _companyLocalService.updateCompany(company);

		User user = _userLocalService.getUserByEmailAddress(
			company.getCompanyId(),
			PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + "@" +
			company.getMx());

		user.setPasswordReset(false);
		user.setPasswordEncrypted(false);
		user.setAgreedToTermsOfUse(true);
		user.setReminderQueryQuestion("test");
		user.setReminderQueryAnswer("test");

		user = _userLocalService.updateUser(user);

		Login login = new Login(
			"127.0.0.1", 8080, user.getEmailAddress(), user.getPassword(),
			new Statistics(1));

		login.execute();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private UserLocalService _userLocalService;

}