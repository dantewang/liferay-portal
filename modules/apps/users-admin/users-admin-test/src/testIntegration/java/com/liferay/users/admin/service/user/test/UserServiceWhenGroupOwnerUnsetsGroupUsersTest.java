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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class UserServiceWhenGroupOwnerUnsetsGroupUsersTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_organization = OrganizationTestUtil.addOrganization(true);

		_group = GroupTestUtil.addGroup();

		_groupOwnerUser = UserTestUtil.addGroupOwnerUser(_group);

		_organizationGroupUser = UserTestUtil.addGroupOwnerUser(
			_organization.getGroup());
	}

	@Test
	public void testShouldUnsetGroupAdmin() throws Exception {
		User groupAdminUser = UserTestUtil.addGroupAdminUser(_group);

		_unsetUsers(groupAdminUser, false);
	}

	@Test
	public void testShouldUnsetGroupOwner() throws Exception {
		User groupOwnerUser = UserTestUtil.addGroupOwnerUser(_group);

		_unsetUsers(groupOwnerUser, false);
	}

	@Test
	public void testShouldUnsetOrganizationAdmin() throws Exception {
		User organizationAdminUser = UserTestUtil.addOrganizationAdminUser(
			_organization);

		_unsetUsers(organizationAdminUser, true);
	}

	@Test
	public void testShouldUnsetOrganizationOwner() throws Exception {
		User organizationOwnerUser = UserTestUtil.addOrganizationOwnerUser(
			_organization);

		_unsetUsers(organizationOwnerUser, true);
	}

	private void _unsetUsers(User objectUser, boolean organizationSite)
		throws Exception {

		try {
			if (organizationSite) {
				UserServiceTestUtil.unsetOrganizationUsers(
					_organization.getOrganizationId(), _organizationGroupUser,
					objectUser);

				Assert.assertTrue(
					_userLocalService.hasOrganizationUser(
						_organization.getOrganizationId(),
						objectUser.getUserId()));
			}
			else {
				UserServiceTestUtil.unsetGroupUsers(
					_group.getGroupId(), _groupOwnerUser, objectUser);

				Assert.assertFalse(
					_userLocalService.hasGroupUser(
						_group.getGroupId(), objectUser.getUserId()));
			}
		}
		finally {
			_userLocalService.deleteUser(objectUser);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _groupOwnerUser;

	@DeleteAfterTestRun
	private Organization _organization;

	@DeleteAfterTestRun
	private User _organizationGroupUser;

	@Inject
	private UserLocalService _userLocalService;

}