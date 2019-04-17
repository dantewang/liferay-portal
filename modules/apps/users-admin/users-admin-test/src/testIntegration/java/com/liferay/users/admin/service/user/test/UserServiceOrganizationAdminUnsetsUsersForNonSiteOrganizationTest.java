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
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
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
public class UserServiceOrganizationAdminUnsetsUsersForNonSiteOrganizationTest
	extends BaseUserServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testShouldUnsetOrganizationAdmin() throws Exception {
		User otherOrganizationAdminUser = UserTestUtil.addOrganizationAdminUser(
			organization);

		try {
			unsetOrganizationUsers(
				organization.getOrganizationId(), organizationAdminUser,
				otherOrganizationAdminUser);

			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organization.getOrganizationId(),
					otherOrganizationAdminUser.getUserId()));
		}
		finally {
			_userLocalService.deleteUser(otherOrganizationAdminUser);
		}
	}

	@Test
	public void testShouldUnsetOrganizationOwner() throws Exception {
		unsetOrganizationUsers(
			organization.getOrganizationId(), organizationAdminUser,
			organizationOwnerUser);

		Assert.assertTrue(
			_userLocalService.hasOrganizationUser(
				organization.getOrganizationId(),
				organizationOwnerUser.getUserId()));
	}

	@Inject
	private UserLocalService _userLocalService;

}