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

package com.liferay.users.admin.service.user.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 * @author José Manuel Navarro
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class UserServiceWhenGettingUserByEmailAddressTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		PropsUtil.set(
			PropsKeys.COMPANY_SECURITY_STRANGERS_WITH_MX,
			Boolean.TRUE.toString());

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(0);

		_user = UserTestUtil.addUser(true);

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
	}

	@After
	public void tearDown() throws Exception {
		PrincipalThreadLocal.setName(_originalName);

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test(expected = NoSuchUserException.class)
	public void testShouldFailIfUserDeleted() throws Exception {
		UserServiceUtil.deleteUser(_user.getUserId());

		UserServiceUtil.getUserByEmailAddress(
			TestPropsValues.getCompanyId(), _user.getEmailAddress());
	}

	@Test
	public void testShouldReturnUserIfPresent() throws Exception {
		try {
			User retrievedUser = UserServiceUtil.getUserByEmailAddress(
				TestPropsValues.getCompanyId(), _user.getEmailAddress());

			Assert.assertEquals(_user, retrievedUser);
		}
		finally {
			UserLocalServiceUtil.deleteUser(_user);
		}
	}

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private User _user;

}