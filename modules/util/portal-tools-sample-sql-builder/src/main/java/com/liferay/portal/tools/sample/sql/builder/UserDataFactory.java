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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.util.SimpleCounter;

import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class UserDataFactory extends BaseDataFactory {

	public UserDataFactory() throws Exception {
		_userScreenNameCounter = new SimpleCounter();

		_accountId = counter.get();

		_userPersonalSiteGroupId = counter.get();

		_initRoleModels();
		_initUserNames();
	}

	public long getAdministratorRoleId() {
		return ADMINISTRATOR_ROLE_ID;
	}

	public int getMaxGroupCount() {
		return BenchmarksPropsValues.MAX_GROUP_COUNT;
	}

	public List<Long> getNewUserGroupIds(
		long groupId, GroupModel guestGroupModel) {

		List<Long> groupIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT + 1);

		groupIds.add(guestGroupModel.getGroupId());

		if ((groupId + BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT) >
				BenchmarksPropsValues.MAX_GROUP_COUNT) {

			groupId =
				groupId - BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT + 1;
		}

		for (int i = 0; i < BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT;
			 i++) {

			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public long getPowerUserRoleId() {
		return POWER_USER_ROLE_ID;
	}

	public List<RoleModel> getRoleModels() {
		return _roleModels;
	}

	public long getUserRoleId() {
		return USER_ROLE_ID;
	}

	public AccountModel newAccountModel() {
		AccountModel accountModel = new AccountModelImpl();

		accountModel.setAccountId(_accountId);
		accountModel.setCompanyId(COMPANY_ID);
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());
		accountModel.setName("Liferay");
		accountModel.setLegalName("Liferay, Inc.");

		return accountModel;
	}

	public GroupModel newCommerceCatalogGroupModel() {
		return newGroupModel(
			COMMERCE_CATALOG_GROUP_ID, getClassNameId(CommerceCatalog.class),
			COMMERCE_CATALOG_ID, COMMERCE_CATALOG_NAME, false);
	}

	public GroupModel newCommerceChannelGroupModel() {
		return newGroupModel(
			COMMERCE_CHANNEL_GROUP_ID, getClassNameId(CommerceChannel.class),
			COMMERCE_CHANNEL_ID, COMMERCE_CHANNEL_NAME, false);
	}

	public CompanyModel newCompanyModel() {
		CompanyModel companyModel = new CompanyModelImpl();

		companyModel.setCompanyId(COMPANY_ID);
		companyModel.setAccountId(_accountId);
		companyModel.setWebId("liferay.com");
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);

		return companyModel;
	}

	public ContactModel newContactModel(UserModel userModel) {
		ContactModel contactModel = new ContactModelImpl();

		contactModel.setContactId(userModel.getContactId());
		contactModel.setCompanyId(userModel.getCompanyId());
		contactModel.setUserId(userModel.getUserId());

		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		contactModel.setUserName(
			fullNameGenerator.getFullName(
				userModel.getFirstName(), userModel.getMiddleName(),
				userModel.getLastName()));

		contactModel.setCreateDate(new Date());
		contactModel.setModifiedDate(new Date());
		contactModel.setClassNameId(getClassNameId(User.class));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setAccountId(_accountId);
		contactModel.setParentContactId(
			ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contactModel.setEmailAddress(userModel.getEmailAddress());
		contactModel.setFirstName(userModel.getFirstName());
		contactModel.setLastName(userModel.getLastName());
		contactModel.setMale(true);
		contactModel.setBirthday(new Date());

		return contactModel;
	}

	public UserModel newDefaultUserModel() {
		return newUserModel(
			DEFAULT_USER_ID, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, true);
	}

	public GroupModel newGlobalGroupModel() {
		return newGroupModel(
			GLOBAL_GROUP_ID, getClassNameId(Company.class), COMPANY_ID,
			GroupConstants.GLOBAL, false);
	}

	public GroupModel newGroupModel(UserModel userModel) {
		return newGroupModel(
			counter.get(), getClassNameId(User.class), userModel.getUserId(),
			userModel.getScreenName(), false);
	}

	public List<GroupModel> newGroupModels() {
		List<GroupModel> groupModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_GROUP_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			groupModels.add(
				newGroupModel(
					i, getClassNameId(Group.class), i, "Site " + i, true));
		}

		return groupModels;
	}

	public GroupModel newGuestGroupModel() {
		return newGroupModel(
			GUEST_GROUP_ID, getClassNameId(Group.class), GUEST_GROUP_ID,
			GroupConstants.GUEST, true);
	}

	public UserModel newGuestUserModel() {
		return newUserModel(counter.get(), "Test", "Test", "Test", false);
	}

	public UserModel newSampleUserModel() {
		return newUserModel(
			SAMPLE_USER_ID, SAMPLE_USER_NAME, SAMPLE_USER_NAME,
			SAMPLE_USER_NAME, false);
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_USER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_USER_COUNT; i++) {
			String[] userName = _nextUserName(i);

			userModels.add(
				newUserModel(
					counter.get(), userName[0], userName[1],
					"test" + _userScreenNameCounter.get(), false));
		}

		return userModels;
	}

	public GroupModel newUserPersonalSiteGroupModel() {
		return newGroupModel(
			_userPersonalSiteGroupId, getClassNameId(UserPersonalSite.class),
			DEFAULT_USER_ID, GroupConstants.USER_PERSONAL_SITE, false);
	}

	public VirtualHostModel newVirtualHostModel() {
		VirtualHostModel virtualHostModel = new VirtualHostModelImpl();

		virtualHostModel.setVirtualHostId(counter.get());
		virtualHostModel.setCompanyId(COMPANY_ID);
		virtualHostModel.setHostname(BenchmarksPropsValues.VIRTUAL_HOST_NAME);

		return virtualHostModel;
	}

	protected GroupModel newGroupModel(
		long groupId, long classNameId, long classPK, String name,
		boolean site) {

		GroupModel groupModel = new GroupModelImpl();

		groupModel.setUuid(SequentialUUID.generate());
		groupModel.setGroupId(groupId);
		groupModel.setCompanyId(COMPANY_ID);
		groupModel.setCreatorUserId(SAMPLE_USER_ID);
		groupModel.setClassNameId(classNameId);
		groupModel.setClassPK(classPK);
		groupModel.setTreePath(
			StringPool.SLASH + groupModel.getGroupId() + StringPool.SLASH);
		groupModel.setGroupKey(name);
		groupModel.setName(name);
		groupModel.setManualMembership(true);
		groupModel.setMembershipRestriction(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION);
		groupModel.setFriendlyURL(
			StringPool.FORWARD_SLASH +
				FriendlyURLNormalizerUtil.normalize(name));
		groupModel.setSite(site);
		groupModel.setActive(true);

		return groupModel;
	}

	protected RoleModel newRoleModel(String name, int type, long roleId) {
		RoleModel roleModel = new RoleModelImpl();

		roleModel.setUuid(SequentialUUID.generate());
		roleModel.setRoleId(roleId);
		roleModel.setCompanyId(COMPANY_ID);
		roleModel.setUserId(SAMPLE_USER_ID);
		roleModel.setUserName(SAMPLE_USER_NAME);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());
		roleModel.setClassNameId(getClassNameId(Role.class));
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}

	protected UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser) {

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(COMPANY_ID);
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(counter.get());
		userModel.setPassword("test");
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion("What is your screen name?");
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(screenName + "@liferay.com");
		userModel.setScreenName(screenName);
		userModel.setLanguageId("en_US");
		userModel.setGreeting("Welcome " + screenName + StringPool.EXCLAMATION);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		userModel.setLoginDate(new Date());
		userModel.setLastLoginDate(new Date());
		userModel.setLastFailedLoginDate(new Date());
		userModel.setLockoutDate(new Date());
		userModel.setAgreedToTermsOfUse(true);
		userModel.setEmailAddressVerified(true);

		return userModel;
	}

	private void _initRoleModels() {
		_roleModels = new ArrayList<>();

		// Administrator

		_administratorRoleModel = newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR,
			ADMINISTRATOR_ROLE_ID);

		_roleModels.add(_administratorRoleModel);

		// Guest

		_guestRoleModel = newRoleModel(
			RoleConstants.GUEST, RoleConstants.TYPE_REGULAR, GUEST_ROLE_ID);

		_roleModels.add(_guestRoleModel);

		// Organization Administrator

		_roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_ADMINISTRATOR,
				RoleConstants.TYPE_ORGANIZATION, counter.get()));

		// Organization Owner

		_roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_OWNER,
				RoleConstants.TYPE_ORGANIZATION, counter.get()));

		// Organization User

		_roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_USER,
				RoleConstants.TYPE_ORGANIZATION, counter.get()));

		// Owner

		_ownerRoleModel = newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR, OWNER_ROLE_ID);

		_roleModels.add(_ownerRoleModel);

		// Power User

		_powerUserRoleModel = newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR,
			POWER_USER_ROLE_ID);

		_roleModels.add(_powerUserRoleModel);

		// Site Administrator

		_roleModels.add(
			newRoleModel(
				RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE,
				counter.get()));

		// Site Member

		_siteMemberRoleModel = newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE,
			SITE_MEMBER_ROLE_ID);

		_roleModels.add(_siteMemberRoleModel);

		// Site Owner

		_roleModels.add(
			newRoleModel(
				RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE,
				counter.get()));

		// User

		_userRoleModel = newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR, USER_ROLE_ID);

		_roleModels.add(_userRoleModel);
	}

	private void _initUserNames() throws Exception {
		_firstNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream("first_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			_firstNames.add(line);
		}

		unsyncBufferedReader.close();

		_lastNames = new ArrayList<>();

		unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream("last_names.txt")));

		while ((line = unsyncBufferedReader.readLine()) != null) {
			_lastNames.add(line);
		}

		unsyncBufferedReader.close();
	}

	private String[] _nextUserName(long index) {
		String[] userName = new String[2];

		userName[0] = _firstNames.get(
			(int)(index / _lastNames.size()) % _firstNames.size());
		userName[1] = _lastNames.get((int)(index % _lastNames.size()));

		return userName;
	}

	private final long _accountId;
	private RoleModel _administratorRoleModel;
	private List<String> _firstNames;
	private RoleModel _guestRoleModel;
	private List<String> _lastNames;
	private RoleModel _ownerRoleModel;
	private RoleModel _powerUserRoleModel;
	private List<RoleModel> _roleModels;
	private RoleModel _siteMemberRoleModel;
	private final long _userPersonalSiteGroupId;
	private RoleModel _userRoleModel;
	private final SimpleCounter _userScreenNameCounter;

}