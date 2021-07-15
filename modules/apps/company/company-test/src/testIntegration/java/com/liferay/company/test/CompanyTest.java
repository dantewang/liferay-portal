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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

		for (int i = 1; i <= _COMPANYCOUNT; i++) {
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

		if (System.getenv("JENKINS_HOME") == null) {
			_exportCSV();
		}
	}

	private void _addCompany(String webId) throws Exception {

		// Add company

		Company company = _companyLocalService.addCompany(
			null, webId, webId, webId, false, 0, true);

		PortalInstances.initCompany(
			ServletContextPool.get(StringPool.BLANK), webId);

		_companyIds.add(company.getCompanyId());

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

		List<User> users = new ArrayList<>();

		int startNum = 0;
		int count = 0;

		if (_lock.tryLock(1, TimeUnit.MINUTES)) {
			try {
				startNum = _startNum * _USERCOUNT + 1;

				_startNum++;

				count = _startNum * _USERCOUNT;
			}
			finally {
				_lock.unlock();
			}
		}

		for (int i = startNum; i <= count; i++) {
			String screenName = "test" + i;

			String firstName = screenName;
			String lastName = screenName;

			String emailAddress = screenName + StringPool.AT + webId;

			User user = _userLocalService.addUser(
				0, companyId, false, "test", "test", false, screenName,
				emailAddress, LocaleUtil.US, firstName, middleName, lastName,
				prefixId, suffixId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, new long[] {groupId}, organizationIds,
				new long[] {role.getRoleId()}, userGroupIds, sendEmail,
				_getServiceContext(companyId));

			user.setLoginDate(new Date());
			user.setLastLoginDate(new Date());
			user.setLockoutDate(new Date());
			user.setAgreedToTermsOfUse(true);
			user.setEmailAddressVerified(true);
			user.setPasswordModified(true);
			user.setPasswordReset(false);
			user.setReminderQueryQuestion("What is your screen name?");
			user.setReminderQueryAnswer(screenName);

			_userLocalService.updateUser(user);

			users.add(user);
		}
	}

	private void _exportCSV() throws Exception {
		StringBundler sb = new StringBundler(_COMPANYCOUNT * _USERCOUNT);

		for (Long companyId : _companyIds) {
			List<User> users = _userLocalService.getCompanyUsers(
				companyId, -1, -1);

			String mx = null;

			for (User user : users) {
				String screenName = user.getScreenName();

				if (screenName.equals(PropsValues.DEFAULT_ADMIN_SCREEN_NAME)) {
					continue;
				}

				if (mx == null) {
					String emailAddress = user.getEmailAddress();

					mx = emailAddress.substring(
						emailAddress.indexOf(StringPool.AT) +
							StringPool.AT.length());
				}

				sb.append(_getUserCSV(user, mx));
			}
		}

		String csv = sb.toString();

		File csvFile = new File(
			PropsUtil.get(PropsKeys.LIFERAY_HOME) + "/companydata.csv");

		if (csvFile.exists()) {
			csvFile.delete();
		}

		FileUtil.write(csvFile, csv.getBytes());
	}

	private ServiceContext _getServiceContext(long companyId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(companyId);

		return serviceContext;
	}

	private String _getUserCSV(User user, String mx) {
		StringBundler sb = new StringBundler(4);

		sb.append(CSVUtil.encode(mx));
		sb.append(StringPool.COMMA);
		sb.append(CSVUtil.encode(user.getScreenName()));
		sb.append(StringPool.NEW_LINE);

		return sb.toString();
	}

	private static final int _COMPANYCOUNT = GetterUtil.get(
		PropsUtil.get("company.test.count"), 2);

	private static final int _USERCOUNT = GetterUtil.get(
		PropsUtil.get("each.company.include.users.count"), 2);

	private static final List<Long> _companyIds = Collections.synchronizedList(
		new ArrayList<Long>());
	private static final Lock _lock = new ReentrantLock();
	private static int _startNum;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ExecutorService _executorService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}