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

package com.liferay.portal.security.membershippolicy;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class OrganizationMembershipPolicyFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestOrganizationMembershipPolicy testOrganizationMembershipPolicy =
			new TestOrganizationMembershipPolicy();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			OrganizationMembershipPolicy.class,
			testOrganizationMembershipPolicy, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetOrganizationMembershipPolicy() {
		OrganizationMembershipPolicy organizationMembershipPolicy =
			OrganizationMembershipPolicyFactoryUtil.
				getOrganizationMembershipPolicy();

		Class<?> clazz = organizationMembershipPolicy.getClass();

		Assert.assertEquals(
			TestOrganizationMembershipPolicy.class.getName(), clazz.getName());
	}

	private static ServiceRegistration<OrganizationMembershipPolicy>
		_serviceRegistration;

	private static class TestOrganizationMembershipPolicy
		implements OrganizationMembershipPolicy {

		@Override
		public void checkMembership(
			long[] userIds, long[] addOrganizationIds,
			long[] removeOrganizationIds) {
		}

		@Override
		public void checkRoles(
			List<UserGroupRole> addUserGroupRoles,
			List<UserGroupRole> removeUserGroupRoles) {
		}

		@Override
		public boolean isMembershipAllowed(long userId, long organizationId) {
			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isMembershipProtected(
			PermissionChecker permissionChecker, long userId,
			long organizationId) {

			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isMembershipRequired(long userId, long organizationId) {
			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isRoleAllowed(
			long userId, long organizationId, long roleId) {

			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isRoleProtected(
			PermissionChecker permissionChecker, long userId,
			long organizationId, long roleId) {

			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isRoleRequired(
			long userId, long organizationId, long roleId) {

			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public void propagateMembership(
			long[] userIds, long[] addOrganizationIds,
			long[] removeOrganizationIds) {
		}

		@Override
		public void propagateRoles(
			List<UserGroupRole> addUserGroupRoles,
			List<UserGroupRole> removeUserGroupRoles) {
		}

		@Override
		public void verifyPolicy() {
		}

		@Override
		public void verifyPolicy(Organization organization) {
		}

		@Override
		public void verifyPolicy(
			Organization organization, Organization oldOrganization,
			List<AssetCategory> oldAssetCategories, List<AssetTag> oldAssetTags,
			Map<String, Serializable> oldExpandoAttributes) {
		}

		@Override
		public void verifyPolicy(Role role) {
		}

		@Override
		public void verifyPolicy(
			Role role, Role oldRole,
			Map<String, Serializable> oldExpandoAttributes) {
		}

	}

}