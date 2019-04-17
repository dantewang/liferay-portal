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

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import org.junit.After;
import org.junit.Before;

/**
 * @author Brian Wing Shun Chan
 * @author José Manuel Navarro
 * @author Drew Brokke
 */
public abstract class BaseUserServiceTestCase {

	@Before
	public void setUp() throws Exception {
		if (site) {
			organization = OrganizationTestUtil.addOrganization(true);
		}
		else {
			organization = OrganizationTestUtil.addOrganization();
		}

		if (regularSite) {
			group = GroupTestUtil.addGroup();

			groupOwnerUser = UserTestUtil.addGroupOwnerUser(group);

			groupAdminUser = UserTestUtil.addGroupAdminUser(group);
		}
		else {
			group = organization.getGroup();

			organizationAdminUser = UserTestUtil.addOrganizationAdminUser(
				organization);

			organizationOwnerUser = UserTestUtil.addOrganizationOwnerUser(
				organization);
		}
	}

	@After
	public void tearDown() throws Exception {
		if (group.isRegularSite()) {
			_groupLocalService.deleteGroup(group);
		}
	}

	protected void unsetGroupUsers(
			long groupId, User subjectUser, User objectUser)
		throws Exception {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			subjectUser);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		ServiceContext serviceContext = new ServiceContext();

		_userService.unsetGroupUsers(
			groupId, new long[] {objectUser.getUserId()}, serviceContext);
	}

	protected void unsetOrganizationUsers(
			long organizationId, User subjectUser, User objectUser)
		throws Exception {

		PermissionChecker permissionChecker = _permissionCheckerFactory.create(
			subjectUser);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		_userService.unsetOrganizationUsers(
			organizationId, new long[] {objectUser.getUserId()});
	}

	protected Group group;

	@DeleteAfterTestRun
	protected User groupAdminUser;

	@DeleteAfterTestRun
	protected User groupOwnerUser;

	@DeleteAfterTestRun
	protected Organization organization;

	@DeleteAfterTestRun
	protected User organizationAdminUser;

	@DeleteAfterTestRun
	protected User organizationOwnerUser;

	protected boolean regularSite;
	protected boolean site;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private UserService _userService;

}