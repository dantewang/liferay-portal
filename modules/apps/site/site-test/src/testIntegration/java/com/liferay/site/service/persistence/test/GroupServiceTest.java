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

package com.liferay.site.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.GroupParentException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.test.LayoutTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 * @author Julio Camarero
 * @author Roberto Díaz
 * @author Sergio González
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class GroupServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddCompanyStagingGroup() throws Exception {
		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			TestPropsValues.getCompanyId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute("staging", Boolean.TRUE);

		Group companyStagingGroup = GroupServiceUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, companyGroup.getGroupId(),
			companyGroup.getNameMap(), companyGroup.getDescriptionMap(),
			companyGroup.getType(), companyGroup.isManualMembership(),
			companyGroup.getMembershipRestriction(),
			companyGroup.getFriendlyURL(), false, companyGroup.isActive(),
			serviceContext);

		try {
			Assert.assertTrue(companyStagingGroup.isCompanyStagingGroup());

			Assert.assertEquals(
				companyGroup.getGroupId(),
				companyStagingGroup.getLiveGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(companyStagingGroup);
		}
	}

	@Test
	public void testAddParentToStagedGroup() throws Exception {
		Group parentGroup = _group;

		Group childGroup = GroupTestUtil.addGroup();

		try {
			GroupTestUtil.enableLocalStaging(childGroup);

			childGroup = GroupServiceUtil.updateGroup(
				childGroup.getGroupId(), parentGroup.getGroupId(),
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			Group stagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				parentGroup.getGroupId(), stagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup);
		}
	}

	@Test
	public void testAddStagedParentToStagedGroup() throws Exception {
		Group parentGroup = _group;

		Group childGroup = GroupTestUtil.addGroup();

		try {
			GroupTestUtil.enableLocalStaging(childGroup);

			GroupTestUtil.enableLocalStaging(parentGroup);

			childGroup = GroupServiceUtil.updateGroup(
				childGroup.getGroupId(), parentGroup.getGroupId(),
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			Group childGroupStagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				parentGroup.getGroupId(),
				childGroupStagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup);
		}
	}

	@Test
	public void testDeleteGroupRemovesSharedPortletPreferences()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				group.getCompanyId(), group.getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED, RandomTestUtil.randomString(),
				null, null);

		GroupServiceUtil.deleteGroup(group.getGroupId());

		Assert.assertNull(
			"Deleting the group should also delete layout type portlet " +
				"preferences that do no belong to a single layout",
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test(expected = NoSuchResourcePermissionException.class)
	public void testDeleteGroupWithStagingGroupRemovesStagingResource()
		throws Exception {

		GroupTestUtil.enableLocalStaging(_group);

		Assert.assertTrue(_group.hasStagingGroup());

		Group stagingGroup = _group.getStagingGroup();

		Role role = RoleLocalServiceUtil.getRole(
			stagingGroup.getCompanyId(), RoleConstants.OWNER);

		ResourcePermissionLocalServiceUtil.getResourcePermission(
			stagingGroup.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(stagingGroup.getGroupId()), role.getRoleId());
	}

	@Test
	public void testDeleteGroupWithStagingGroupRemovesStagingUserGroupRoles()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(group);

		Assert.assertTrue(group.hasStagingGroup());

		Group stagingGroup = group.getStagingGroup();

		List<UserGroupRole> stagingUserGroupRoles =
			UserGroupRoleLocalServiceUtil.getUserGroupRolesByGroup(
				stagingGroup.getGroupId());

		int stagingUserGroupRolesCount = stagingUserGroupRoles.size();

		Assert.assertEquals(1, stagingUserGroupRolesCount);

		GroupServiceUtil.deleteGroup(group.getGroupId());

		stagingUserGroupRoles =
			UserGroupRoleLocalServiceUtil.getUserGroupRolesByGroup(
				stagingGroup.getGroupId());

		stagingUserGroupRolesCount = stagingUserGroupRoles.size();

		Assert.assertEquals(0, stagingUserGroupRolesCount);
	}

	@Test
	public void testDeleteOrganizationSiteOnlyRemovesSiteRoles()
		throws Exception {

		Organization organization =
			OrganizationLocalServiceUtil.addOrganization(
				TestPropsValues.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				RandomTestUtil.randomString(), true);

		Group organizationSite = GroupLocalServiceUtil.getOrganizationGroup(
			TestPropsValues.getCompanyId(), organization.getOrganizationId());

		organizationSite.setManualMembership(true);

		User user = UserTestUtil.addOrganizationOwnerUser(organization);

		UserLocalServiceUtil.addGroupUser(
			organizationSite.getGroupId(), user.getUserId());
		UserLocalServiceUtil.addOrganizationUsers(
			organization.getOrganizationId(), new long[] {user.getUserId()});

		Role siteRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), organizationSite.getGroupId(),
			new long[] {siteRole.getRoleId()});

		GroupServiceUtil.deleteGroup(organizationSite.getGroupId());

		Assert.assertEquals(
			1,
			UserGroupRoleLocalServiceUtil.getUserGroupRolesCount(
				user.getUserId(), organizationSite.getGroupId()));

		UserLocalServiceUtil.deleteUser(user);

		OrganizationLocalServiceUtil.deleteOrganization(organization);
	}

	@Test
	public void testDeleteSite() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		int initialTagsCount = AssetTagLocalServiceUtil.getGroupTagsCount(
			group.getGroupId());

		AssetTagLocalServiceUtil.addTag(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		Assert.assertEquals(
			initialTagsCount + 1,
			AssetTagLocalServiceUtil.getGroupTagsCount(group.getGroupId()));

		GroupServiceUtil.deleteGroup(group.getGroupId());

		Assert.assertEquals(
			initialTagsCount,
			AssetTagLocalServiceUtil.getGroupTagsCount(group.getGroupId()));
	}

	@Test
	public void testFindGroupByDescription() throws Exception {
		Assert.assertEquals(
			1,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), null,
				_group.getDescription(getLocale()),
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByDescriptionWithSpaces() throws Exception {
		_group.setDescription(
			RandomTestUtil.randomString() + StringPool.SPACE +
				RandomTestUtil.randomString());

		GroupLocalServiceUtil.updateGroup(_group);

		Assert.assertEquals(
			1,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), null,
				_group.getDescription(getLocale()),
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByName() throws Exception {
		Assert.assertEquals(
			1,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), _group.getName(getLocale()),
				null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByNameWithSpaces() throws Exception {
		_group.setName(
			RandomTestUtil.randomString() + StringPool.SPACE +
				RandomTestUtil.randomString());

		GroupLocalServiceUtil.updateGroup(_group);

		Assert.assertEquals(
			1,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), _group.getName(getLocale()),
				null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGroupByRole() throws Exception {
		long roleId = RoleTestUtil.addGroupRole(_group.getGroupId());

		String[] groupParams = {
			"groupsRoles:" + String.valueOf(roleId) + ":long",
			"site:true:boolean"
		};

		Assert.assertEquals(
			1,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), null, null, groupParams));

		List<Group> groups = GroupServiceUtil.search(
			TestPropsValues.getCompanyId(), null, null, groupParams,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(groups.toString(), 1, groups.size());
		Assert.assertEquals(_group, groups.get(0));

		Assert.assertEquals(
			1, GroupLocalServiceUtil.getRoleGroupsCount(roleId));

		groups = GroupLocalServiceUtil.getRoleGroups(roleId);

		Assert.assertEquals(groups.toString(), 1, groups.size());
		Assert.assertEquals(_group, groups.get(0));
	}

	@Test
	public void testFindGuestGroupByCompanyName() throws Exception {
		Assert.assertEquals(
			1,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), "liferay%", null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindGuestGroupByCompanyNameCapitalized() throws Exception {
		Assert.assertEquals(
			1,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), "Liferay%", null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFindNonexistentGroup() throws Exception {
		Assert.assertEquals(
			0,
			GroupServiceUtil.searchCount(
				TestPropsValues.getCompanyId(), "cabina14", null,
				new String[] {
					"manualMembership:true:boolean", "site:true:boolean"
				}));
	}

	@Test
	public void testFriendlyURLDefaults() throws Exception {
		long companyId = _group.getCompanyId();

		String defaultNewGroupFriendlyURL =
			StringPool.SLASH +
				FriendlyURLNormalizerUtil.normalize(
					_group.getName(LocaleUtil.getDefault()));

		Assert.assertNotNull(
			GroupLocalServiceUtil.fetchFriendlyURLGroup(
				companyId, defaultNewGroupFriendlyURL));

		GroupServiceUtil.updateFriendlyURL(_group.getGroupId(), null);

		Assert.assertNull(
			GroupLocalServiceUtil.fetchFriendlyURLGroup(
				companyId, defaultNewGroupFriendlyURL));

		String defaultFriendlyURL = "/group-" + _group.getGroupId();

		Assert.assertNotNull(
			GroupLocalServiceUtil.fetchFriendlyURLGroup(
				companyId, defaultFriendlyURL));

		GroupServiceUtil.updateFriendlyURL(
			_group.getGroupId(),
			StringPool.SLASH + RandomTestUtil.randomString());

		Group group = GroupTestUtil.addGroup();

		try {
			GroupServiceUtil.updateFriendlyURL(
				group.getGroupId(), defaultFriendlyURL);

			GroupServiceUtil.updateFriendlyURL(_group.getGroupId(), null);

			Assert.assertNotNull(
				GroupLocalServiceUtil.fetchFriendlyURLGroup(
					companyId, defaultFriendlyURL + "-1"));
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group);
		}
	}

	@Test(expected = GroupFriendlyURLException.class)
	public void testFriendlyURLSetToGroupId() throws Exception {
		String friendlyURL = "/" + _group.getGroupId();

		GroupServiceUtil.updateFriendlyURL(_group.getGroupId(), friendlyURL);
	}

	@Test(expected = GroupFriendlyURLException.class)
	public void testFriendlyURLSetToRandomLong() throws Exception {
		String friendlyURL = "/" + RandomTestUtil.nextLong();

		GroupServiceUtil.updateFriendlyURL(_group.getGroupId(), friendlyURL);
	}

	@Test
	public void testGetGlobalSiteDefaultLocale() throws Exception {
		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		Assert.assertEquals(
			company.getLocale(),
			PortalUtil.getSiteDefaultLocale(company.getGroupId()));
	}

	@Test
	public void testGetGlobalSiteDefaultLocaleWhenCompanyLocaleModified()
		throws Exception {

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		User defaultUser = company.getDefaultUser();

		String languageId = defaultUser.getLanguageId();

		try {
			defaultUser.setLanguageId(
				LanguageUtil.getLanguageId(LocaleUtil.BRAZIL));

			defaultUser = UserLocalServiceUtil.updateUser(defaultUser);

			Assert.assertEquals(
				LocaleUtil.BRAZIL,
				PortalUtil.getSiteDefaultLocale(company.getGroupId()));
		}
		finally {
			defaultUser.setLanguageId(languageId);

			UserLocalServiceUtil.updateUser(defaultUser);
		}
	}

	@Test
	public void testGetGroupsLikeName() throws Exception {
		List<Group> allChildGroups = new ArrayList<>();
		Group parentGroup = GroupTestUtil.addGroup();

		List<Group> allGroups = new ArrayList<>(
			GroupLocalServiceUtil.getGroups(
				TestPropsValues.getCompanyId(),
				GroupConstants.DEFAULT_PARENT_GROUP_ID, true));

		try {
			String name = RandomTestUtil.randomString(10);

			long parentGroupId = parentGroup.getGroupId();

			List<Group> likeNameChildGroups = new ArrayList<>();

			for (int i = 0; i < 10; i++) {
				Group group = GroupTestUtil.addGroup(parentGroupId);

				group.setName(name + i);

				group = GroupLocalServiceUtil.updateGroup(group);

				likeNameChildGroups.add(group);
			}

			allChildGroups.addAll(likeNameChildGroups);
			allChildGroups.add(GroupTestUtil.addGroup(parentGroupId));
			allChildGroups.add(GroupTestUtil.addGroup(parentGroupId));
			allChildGroups.add(GroupTestUtil.addGroup(parentGroupId));

			allGroups.addAll(allChildGroups);

			assertExpectedGroups(
				likeNameChildGroups, parentGroupId, name + "%");
			assertExpectedGroups(
				likeNameChildGroups, parentGroupId,
				StringUtil.toLowerCase(name) + "%");
			assertExpectedGroups(
				likeNameChildGroups, parentGroupId,
				StringUtil.toUpperCase(name) + "%");
			assertExpectedGroups(
				likeNameChildGroups, GroupConstants.ANY_PARENT_GROUP_ID,
				name + "%");
			assertExpectedGroups(allChildGroups, parentGroupId, null);
			assertExpectedGroups(allChildGroups, parentGroupId, "");
			assertExpectedGroups(
				allGroups, GroupConstants.ANY_PARENT_GROUP_ID, "");
		}
		finally {
			for (Group childGroup : allChildGroups) {
				GroupTestUtil.deleteGroup(childGroup);
			}

			GroupTestUtil.deleteGroup(parentGroup);
		}
	}

	@Test
	public void testGetGtGroups() throws Exception {
		for (int i = 0; i < 10; i++) {
			_groups.add(GroupTestUtil.addGroup());
		}

		long parentGroupId = 0;
		int size = 5;

		List<Group> groups = GroupServiceUtil.getGtGroups(
			0, TestPropsValues.getCompanyId(), parentGroupId, true, size);

		Assert.assertFalse(groups.isEmpty());
		Assert.assertEquals(groups.toString(), size, groups.size());

		Group lastGroup = groups.get(groups.size() - 1);

		groups = GroupServiceUtil.getGtGroups(
			lastGroup.getGroupId(), TestPropsValues.getCompanyId(),
			parentGroupId, true, size);

		Assert.assertFalse(groups.isEmpty());
		Assert.assertEquals(groups.toString(), size, groups.size());

		long previousGroupId = 0;

		for (Group group : groups) {
			long groupId = group.getGroupId();

			Assert.assertTrue(groupId > lastGroup.getGroupId());
			Assert.assertTrue(groupId > previousGroupId);

			previousGroupId = groupId;
		}
	}

	@Test
	public void testGetSiteDefaultInheritLocale() throws Exception {
		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		Assert.assertEquals(
			company.getLocale(),
			PortalUtil.getSiteDefaultLocale(_group.getGroupId()));
	}

	@Test
	public void testGetSiteDefaultInheritLocaleWhenCompanyLocaleModified()
		throws Exception {

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		User defaultUser = company.getDefaultUser();

		String languageId = defaultUser.getLanguageId();

		try {
			defaultUser.setLanguageId(
				LanguageUtil.getLanguageId(LocaleUtil.CHINA));

			defaultUser = UserLocalServiceUtil.updateUser(defaultUser);

			Assert.assertEquals(
				LocaleUtil.CHINA,
				PortalUtil.getSiteDefaultLocale(_group.getGroupId()));
		}
		finally {
			defaultUser.setLanguageId(languageId);

			UserLocalServiceUtil.updateUser(defaultUser);
		}
	}

	@Test
	public void testGroupHasCurrentPageScopeDescriptiveName() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group scopeGroup = addScopeGroup(_group);

		themeDisplay.setPlid(scopeGroup.getClassPK());

		themeDisplay.setScopeGroupId(_group.getGroupId());

		String scopeDescriptiveName = scopeGroup.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName,
			scopeDescriptiveName.contains("current-page"));

		GroupLocalServiceUtil.deleteGroup(scopeGroup);
	}

	@Test
	public void testGroupHasCurrentSiteScopeDescriptiveName() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		String scopeDescriptiveName = _group.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName,
			scopeDescriptiveName.contains("current-site"));
	}

	@Test
	public void testGroupHasDefaultScopeDescriptiveName() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group group = GroupTestUtil.addGroup();

		group.setClassName(LayoutPrototype.class.getName());

		themeDisplay.setScopeGroupId(_group.getGroupId());

		String scopeDescriptiveName = group.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName, scopeDescriptiveName.contains("default"));

		GroupLocalServiceUtil.deleteGroup(group);
	}

	@Test
	public void testGroupHasLocalizedName() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		String scopeDescriptiveName = _group.getScopeDescriptiveName(
			themeDisplay);

		Assert.assertTrue(
			scopeDescriptiveName.equals(
				_group.getName(themeDisplay.getLocale())));
	}

	@Test
	public void testGroupIsChildSiteScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		Group subgroup = GroupTestUtil.addGroup(_group.getGroupId());

		String scopeLabel = subgroup.getScopeLabel(themeDisplay);

		Assert.assertEquals("child-site", scopeLabel);

		GroupLocalServiceUtil.deleteGroup(subgroup);
	}

	@Test
	public void testGroupIsCurrentSiteScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		String scopeLabel = _group.getScopeLabel(themeDisplay);

		Assert.assertEquals("current-site", scopeLabel);
	}

	@Test
	public void testGroupIsGlobalScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		themeDisplay.setCompany(company);

		Group companyGroup = company.getGroup();

		String scopeLabel = companyGroup.getScopeLabel(themeDisplay);

		Assert.assertEquals("global", scopeLabel);
	}

	@Test
	public void testGroupIsPageScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group scopeGroup = addScopeGroup(_group);

		themeDisplay.setPlid(scopeGroup.getClassPK());

		themeDisplay.setScopeGroupId(_group.getGroupId());

		String scopeLabel = scopeGroup.getScopeLabel(themeDisplay);

		Assert.assertEquals("page", scopeLabel);

		GroupLocalServiceUtil.deleteGroup(scopeGroup);
	}

	@Test
	public void testGroupIsParentSiteScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group subgroup = GroupTestUtil.addGroup(_group.getGroupId());

		themeDisplay.setScopeGroupId(subgroup.getGroupId());

		String scopeLabel = _group.getScopeLabel(themeDisplay);

		Assert.assertEquals("parent-site", scopeLabel);

		GroupLocalServiceUtil.deleteGroup(subgroup);
	}

	@Test
	public void testGroupIsSiteScopeLabel() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Group group = GroupTestUtil.addGroup();

		themeDisplay.setScopeGroupId(_group.getGroupId());

		String scopeLabel = group.getScopeLabel(themeDisplay);

		Assert.assertEquals("site", scopeLabel);

		GroupLocalServiceUtil.deleteGroup(group);
	}

	@Test
	public void testIndividualResourcePermission() throws Exception {
		int resourcePermissionsCount =
			ResourcePermissionLocalServiceUtil.getResourcePermissionsCount(
				_group.getCompanyId(), Group.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_group.getGroupId()));

		Assert.assertEquals(1, resourcePermissionsCount);
	}

	@Test
	public void testInheritLocalesByDefault() throws Exception {
		Assert.assertTrue(LanguageUtil.isInheritLocales(_group.getGroupId()));
		Assert.assertEquals(
			LanguageUtil.getAvailableLocales(),
			LanguageUtil.getAvailableLocales(_group.getGroupId()));
	}

	@Test
	public void testInvalidChangeAvailableLanguageIds() throws Exception {
		testUpdateDisplaySettings(
			Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.US), null, true);
	}

	@Test
	public void testInvalidChangeDefaultLanguageId() throws Exception {
		testUpdateDisplaySettings(
			Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US), LocaleUtil.GERMANY,
			true);
	}

	@Test
	public void testRemoveParentFromStagedGroup() throws Exception {
		Group parentGroup = _group;

		Group childGroup = GroupTestUtil.addGroup();

		try {
			GroupTestUtil.enableLocalStaging(childGroup);

			GroupServiceUtil.updateGroup(
				childGroup.getGroupId(), parentGroup.getGroupId(),
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			childGroup = GroupServiceUtil.updateGroup(
				childGroup.getGroupId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			Group childGroupStagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				childGroupStagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup);
		}
	}

	@Test
	public void testRemoveStagedParentFromStagedGroup() throws Exception {
		Group parentGroup = _group;

		Group childGroup = GroupTestUtil.addGroup();

		try {
			GroupTestUtil.enableLocalStaging(childGroup);

			GroupTestUtil.enableLocalStaging(parentGroup);

			GroupServiceUtil.updateGroup(
				childGroup.getGroupId(), parentGroup.getGroupId(),
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			childGroup = GroupLocalServiceUtil.updateGroup(
				childGroup.getGroupId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			Group childGroupStagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				GroupConstants.DEFAULT_PARENT_GROUP_ID,
				childGroupStagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup);
		}
	}

	@Test
	public void testScopes() throws Exception {
		Layout layout = LayoutTestUtil.addLayout(_group);

		Assert.assertFalse(layout.hasScopeGroup());

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(
			LocaleUtil.getDefault(), layout.getName(LocaleUtil.getDefault()));

		Group scope = GroupLocalServiceUtil.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			Layout.class.getName(), layout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap,
			(Map<Locale, String>)null, 0, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, true,
			null);

		Assert.assertFalse(scope.isRoot());
		Assert.assertEquals(scope.getParentGroupId(), _group.getGroupId());

		GroupLocalServiceUtil.deleteGroup(scope);
	}

	@Test
	public void testSelectableParentSites() throws Exception {
		testSelectableParentSites(false);
	}

	@Test
	public void testSelectableParentSitesStaging() throws Exception {
		testSelectableParentSites(true);
	}

	@Test(expected = GroupParentException.MustNotHaveChildParent.class)
	public void testSelectFirstChildGroupAsParentSite() throws Exception {
		Group group1 = _group;

		Group group11 = GroupTestUtil.addGroup(group1.getGroupId());

		try {
			GroupServiceUtil.updateGroup(
				group1.getGroupId(), group11.getGroupId(), group1.getNameMap(),
				group1.getDescriptionMap(), group1.getType(),
				group1.isManualMembership(), group1.getMembershipRestriction(),
				group1.getFriendlyURL(), group1.isInheritContent(),
				group1.isActive(), ServiceContextTestUtil.getServiceContext());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group11);
		}
	}

	@Test(expected = GroupParentException.MustNotHaveChildParent.class)
	public void testSelectLastChildGroupAsParentSite() throws Exception {
		Group group1 = _group;

		Group group11 = GroupTestUtil.addGroup(group1.getGroupId());

		Group group111 = GroupTestUtil.addGroup(group11.getGroupId());

		Group group1111 = GroupTestUtil.addGroup(group111.getGroupId());

		try {
			GroupServiceUtil.updateGroup(
				group1.getGroupId(), group1111.getGroupId(),
				group1.getNameMap(), group1.getDescriptionMap(),
				group1.getType(), group1.isManualMembership(),
				group1.getMembershipRestriction(), group1.getFriendlyURL(),
				group1.isInheritContent(), group1.isActive(),
				ServiceContextTestUtil.getServiceContext());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(group1111);

			GroupLocalServiceUtil.deleteGroup(group111);

			GroupLocalServiceUtil.deleteGroup(group11);
		}
	}

	@Test(expected = GroupParentException.MustNotHaveStagingParent.class)
	public void testSelectLiveGroupAsParentSite() throws Exception {
		GroupTestUtil.enableLocalStaging(_group);

		Assert.assertTrue(_group.hasStagingGroup());

		Group stagingGroup = _group.getStagingGroup();

		GroupServiceUtil.updateGroup(
			stagingGroup.getGroupId(), _group.getGroupId(),
			stagingGroup.getNameMap(), stagingGroup.getDescriptionMap(),
			stagingGroup.getType(), stagingGroup.isManualMembership(),
			stagingGroup.getMembershipRestriction(),
			stagingGroup.getFriendlyURL(), stagingGroup.isInheritContent(),
			stagingGroup.isActive(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test(expected = GroupParentException.MustNotBeOwnParent.class)
	public void testSelectOwnGroupAsParentSite() throws Exception {
		GroupServiceUtil.updateGroup(
			_group.getGroupId(), _group.getGroupId(), _group.getNameMap(),
			_group.getDescriptionMap(), _group.getType(),
			_group.isManualMembership(), _group.getMembershipRestriction(),
			_group.getFriendlyURL(), _group.isInheritContent(),
			_group.isActive(), ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testSubsites() throws Exception {
		Group group1 = _group;

		Group group11 = GroupTestUtil.addGroup(group1.getGroupId());

		Group group111 = GroupTestUtil.addGroup(group11.getGroupId());

		Assert.assertTrue(group1.isRoot());
		Assert.assertFalse(group11.isRoot());
		Assert.assertFalse(group111.isRoot());
		Assert.assertEquals(group1.getGroupId(), group11.getParentGroupId());
		Assert.assertEquals(group11.getGroupId(), group111.getParentGroupId());

		GroupLocalServiceUtil.deleteGroup(group111);

		GroupLocalServiceUtil.deleteGroup(group11);
	}

	@Test
	public void testUpdateAvailableLocales() throws Exception {
		List<Locale> availableLocales = Arrays.asList(
			LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US);

		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), availableLocales, null);

		Assert.assertEquals(
			new HashSet<>(availableLocales),
			LanguageUtil.getAvailableLocales(_group.getGroupId()));
	}

	@Test
	public void testUpdateDefaultLocale() throws Exception {
		_group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, LocaleUtil.SPAIN);

		Assert.assertEquals(
			LocaleUtil.SPAIN,
			PortalUtil.getSiteDefaultLocale(_group.getGroupId()));
	}

	@Test
	public void testUpdateGroupParentFromStagedParentToStagedParentInStagedGroup()
		throws Exception {

		Group childGroup = GroupTestUtil.addGroup();
		Group parentGroup1 = GroupTestUtil.addGroup();
		Group parentGroup2 = _group;

		try {
			GroupTestUtil.enableLocalStaging(childGroup);
			GroupTestUtil.enableLocalStaging(parentGroup1);
			GroupTestUtil.enableLocalStaging(parentGroup2);

			GroupServiceUtil.updateGroup(
				childGroup.getGroupId(), parentGroup1.getGroupId(),
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			childGroup = GroupServiceUtil.updateGroup(
				childGroup.getGroupId(), parentGroup2.getGroupId(),
				childGroup.getNameMap(), childGroup.getDescriptionMap(),
				childGroup.getType(), childGroup.isManualMembership(),
				childGroup.getMembershipRestriction(),
				childGroup.getFriendlyURL(), childGroup.isInheritContent(),
				childGroup.isActive(), null);

			Group childGroupStagingGroup = childGroup.getStagingGroup();

			Assert.assertEquals(
				parentGroup2.getGroupId(),
				childGroupStagingGroup.getParentGroupId());
		}
		finally {
			GroupLocalServiceUtil.deleteGroup(childGroup);
			GroupLocalServiceUtil.deleteGroup(parentGroup1);
		}
	}

	@Test
	public void testValidChangeAvailableLanguageIds() throws Exception {
		testUpdateDisplaySettings(
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.SPAIN, LocaleUtil.US), null, false);
	}

	@Test
	public void testValidChangeDefaultLanguageId() throws Exception {
		testUpdateDisplaySettings(
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			Arrays.asList(LocaleUtil.GERMANY, LocaleUtil.SPAIN, LocaleUtil.US),
			LocaleUtil.GERMANY, false);
	}

	protected Group addScopeGroup(Group group) throws Exception {
		Layout scopeLayout = LayoutTestUtil.addLayout(group);

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.getDefault(), RandomTestUtil.randomString());

		return GroupLocalServiceUtil.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			Layout.class.getName(), scopeLayout.getPlid(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap,
			(Map<Locale, String>)null, 0, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, true,
			null);
	}

	protected void assertExpectedGroups(
			List<Group> expectedGroups, long parentGroupId, String nameSearch)
		throws Exception {

		List<Group> actualGroups = GroupServiceUtil.getGroups(
			TestPropsValues.getCompanyId(), parentGroupId, nameSearch, true,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			actualGroups.toString(), expectedGroups.size(),
			actualGroups.size());
		Assert.assertTrue(
			actualGroups.toString(), actualGroups.containsAll(expectedGroups));

		Assert.assertEquals(
			expectedGroups.size(),
			GroupServiceUtil.getGroupsCount(
				TestPropsValues.getCompanyId(), parentGroupId, nameSearch,
				true));
	}

	protected Locale getLocale() {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		return themeDisplay.getLocale();
	}

	protected void testSelectableParentSites(boolean staging) throws Exception {
		Assert.assertTrue(_group.isRoot());

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		params.put("site", Boolean.TRUE);

		List<Long> excludedGroupIds = new ArrayList<>();

		excludedGroupIds.add(_group.getGroupId());

		if (staging) {
			GroupTestUtil.enableLocalStaging(_group);

			Assert.assertTrue(_group.hasStagingGroup());

			Group stagingGroup = _group.getStagingGroup();

			excludedGroupIds.add(stagingGroup.getGroupId());
		}

		params.put("excludedGroupIds", excludedGroupIds);

		List<Group> selectableGroups = GroupServiceUtil.search(
			_group.getCompanyId(), null, StringPool.BLANK, params,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (Group selectableGroup : selectableGroups) {
			long selectableGroupId = selectableGroup.getGroupId();

			Assert.assertNotEquals(
				"A group cannot be its own parent", _group.getGroupId(),
				selectableGroupId);

			if (staging) {
				Assert.assertNotEquals(
					"A group cannot have its live group as parent",
					_group.getLiveGroupId(), selectableGroupId);
			}
		}
	}

	protected void testUpdateDisplaySettings(
			Collection<Locale> portalAvailableLocales,
			Collection<Locale> groupAvailableLocales, Locale groupDefaultLocale,
			boolean expectFailure)
		throws Exception {

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), portalAvailableLocales,
			LocaleUtil.getDefault());

		try {
			GroupTestUtil.updateDisplaySettings(
				_group.getGroupId(), groupAvailableLocales, groupDefaultLocale);

			Assert.assertFalse(expectFailure);
		}
		catch (LocaleException le) {
			Assert.assertTrue(expectFailure);
		}
		finally {
			CompanyTestUtil.resetCompanyLocales(
				TestPropsValues.getCompanyId(), availableLocales,
				LocaleUtil.getDefault());
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}