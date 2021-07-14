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

package com.liferay.company.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Hai Yu
 */
@DataGuard(scope = DataGuard.Scope.CLASS)
@RunWith(Arquillian.class)
public class CompanyTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_executorService = Executors.newWorkStealingPool();
	}

	@After
	public void tearDown() {
		_executorService.shutdownNow();
	}

	@Test
	public void testAddCompanyAndUserData() throws Exception {
		Assert.assertTrue(_companyLocalService.getCompaniesCount() == 1);

		List<Future<Void>> futures = new ArrayList<>();

		for (int i = 0; i < _COMPANYCOUNT; i++) {
			String webId = "liferay" + i + ".com";

			futures.add(
				_executorService.submit(
					() -> {
						_addCompany(webId);

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}

		Assert.assertTrue(
			"Company count should be " + (_COMPANYCOUNT + 1),
			_companyLocalService.getCompaniesCount() == (_COMPANYCOUNT + 1));
	}

	private void _addCompany(String webId) throws Exception {

		// Add company

		Company company = _companyLocalService.addCompany(
			null, webId, webId, webId, false, 0, true);

		PortalInstances.initCompany(
			ServletContextPool.get(StringPool.BLANK), webId);

		// Add user

		_addUser(company.getCompanyId(), company.getGroupId(), webId);

		int usersCount = _userLocalService.getCompanyUsersCount(
			company.getCompanyId());

		Assert.assertTrue(
			"users count should be " + (_USERCOUNT + 1),
			usersCount == (_USERCOUNT + 1));
	}

	private void _addUser(long companyId, long groupId, String webId)
		throws Exception {

		String middleName = StringPool.BLANK;
		long prefixId = 0;
		long suffixId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] organizationIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		for (int i = 0; i < _USERCOUNT; i++) {
			String screenName = "test" + i;

			String firstName = screenName;
			String lastName = screenName;

			String emailAddress = screenName + StringPool.AT + webId;

			_userLocalService.addUser(
				0, companyId, false, "test", "test", false, screenName,
				emailAddress, LocaleUtil.US, firstName, middleName, lastName,
				prefixId, suffixId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, new long[] {groupId}, organizationIds,
				new long[] {role.getRoleId()}, userGroupIds, sendEmail,
				_getServiceContext(companyId));
		}
	}

	private ServiceContext _getServiceContext(long companyId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(companyId);

		return serviceContext;
	}

	private static final int _COMPANYCOUNT = GetterUtil.get(
		PropsUtil.get("company.test.count"), 2);

	private static final int _USERCOUNT = GetterUtil.get(
		PropsUtil.get("each.company.include.users.count"), 2);

	@Inject
	private CompanyLocalService _companyLocalService;

	private ExecutorService _executorService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}