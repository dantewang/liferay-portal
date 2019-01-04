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

import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicyFactory;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicyFactoryUtil;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicyUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

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
public class UserGroupMembershipPolicyFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestUserGroupMembershipPolicy testUserGroupMembershipPolicy =
			new TestUserGroupMembershipPolicy();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			UserGroupMembershipPolicy.class, testUserGroupMembershipPolicy,
			properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() {
		_called = false;
	}

	@Test
	public void testCheckMembership() throws Exception {
		long[] array = {1, 2, 3};

		UserGroupMembershipPolicyUtil.checkMembership(array, array, array);

		Assert.assertTrue(_called);
	}

	@Test
	public void testGetUserGroupMembershipPolicy() {
		UserGroupMembershipPolicy userGroupMembershipPolicy =
			UserGroupMembershipPolicyFactoryUtil.getUserGroupMembershipPolicy();

		Class<?> clazz = userGroupMembershipPolicy.getClass();

		Assert.assertEquals(
			TestUserGroupMembershipPolicy.class.getName(), clazz.getName());
	}

	@Test
	public void testGetUserGroupMembershipPolicyFactory() {
		UserGroupMembershipPolicyFactory userGroupMembershipPolicyFactory =
			UserGroupMembershipPolicyFactoryUtil.
				getUserGroupMembershipPolicyFactory();

		UserGroupMembershipPolicy userGroupMembershipPolicy =
			userGroupMembershipPolicyFactory.getUserGroupMembershipPolicy();

		Class<?> clazz = userGroupMembershipPolicy.getClass();

		Assert.assertEquals(
			TestUserGroupMembershipPolicy.class.getName(), clazz.getName());
	}

	@Test
	public void testIsMembershipAllowed() throws Exception {
		Assert.assertTrue(
			UserGroupMembershipPolicyUtil.isMembershipAllowed(1, 1));
		Assert.assertFalse(
			UserGroupMembershipPolicyUtil.isMembershipAllowed(2, 2));
	}

	@Test
	public void testIsMembershipRequired() throws Exception {
		Assert.assertTrue(
			UserGroupMembershipPolicyUtil.isMembershipRequired(1, 1));
		Assert.assertFalse(
			UserGroupMembershipPolicyUtil.isMembershipRequired(2, 2));
	}

	@Test
	public void testPropagateMembership() throws Exception {
		long[] array = {1, 2, 3};

		UserGroupMembershipPolicyUtil.propagateMembership(array, array, array);

		Assert.assertTrue(_called);
	}

	@Test
	public void testVerifyPolicy1() throws Exception {
		UserGroupMembershipPolicyUtil.verifyPolicy();

		Assert.assertTrue(_called);
	}

	@Test
	public void testVerifyPolicy2() throws Exception {
		UserGroupMembershipPolicyUtil.verifyPolicy(null);

		Assert.assertTrue(_called);
	}

	@Test
	public void testVerifyPolicy3() throws Exception {
		UserGroupMembershipPolicyUtil.verifyPolicy(null, null, null);

		Assert.assertTrue(_called);
	}

	private static boolean _called;
	private static ServiceRegistration<UserGroupMembershipPolicy>
		_serviceRegistration;

	private static class TestUserGroupMembershipPolicy
		implements UserGroupMembershipPolicy {

		@Override
		public void checkMembership(
			long[] userIds, long[] addUserGroupIds, long[] removeUserGroupIds) {

			_called = true;
		}

		@Override
		public boolean isMembershipAllowed(long userId, long userGroupId) {
			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public boolean isMembershipRequired(long userId, long userGroupId) {
			if (userId == 1) {
				return true;
			}

			return false;
		}

		@Override
		public void propagateMembership(
			long[] userIds, long[] addUserGroupIds, long[] removeUserGroupIds) {

			_called = true;
		}

		@Override
		public void verifyPolicy() {
			_called = true;
		}

		@Override
		public void verifyPolicy(UserGroup userGroup) {
			_called = true;
		}

		@Override
		public void verifyPolicy(
			UserGroup userGroup, UserGroup oldUserGroup,
			Map<String, Serializable> oldExpandoAttributes) {

			_called = true;
		}

	}

}