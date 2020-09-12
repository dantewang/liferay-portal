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

import com.liferay.commerce.product.model.CommerceCatalogModel;
import com.liferay.commerce.product.model.CommerceChannelModel;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.UserModel;
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

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class UserDataFactory extends BaseDataFactory {

	public static UserDataFactory getInstance() {
		return _userDataFactory;
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

	public long getUserRoleId() {
		return USER_ROLE_ID;
	}

	public AccountModel newAccountModel() {
		AccountModel accountModel = new AccountModelImpl();

		// PK fields

		accountModel.setAccountId(_accountId);

		// Audit fields

		accountModel.setCompanyId(COMPANY_ID);
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());

		// Other fields

		accountModel.setName("Liferay");
		accountModel.setLegalName("Liferay, Inc.");

		return accountModel;
	}

	public GroupModel newCommerceCatalogGroupModel(
		CommerceCatalogModel commerceCatalogModel, long classNameId) {

		return _newGroupModel(
			counter.get(), classNameId,
			commerceCatalogModel.getCommerceCatalogId(),
			commerceCatalogModel.getName(), false);
	}

	public GroupModel newCommerceChannelGroupModel(
		CommerceChannelModel commerceChannelModel, long classNameId) {

		return _newGroupModel(
			counter.get(), classNameId,
			commerceChannelModel.getCommerceChannelId(),
			commerceChannelModel.getName(), false);
	}

	public CompanyModel newCompanyModel() {
		CompanyModel companyModel = new CompanyModelImpl();

		// PK fields

		companyModel.setCompanyId(COMPANY_ID);

		// Other fields

		companyModel.setAccountId(_accountId);
		companyModel.setWebId("liferay.com");
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);

		return companyModel;
	}

	public ContactModel newContactModel(UserModel userModel, long classNameId) {
		ContactModel contactModel = new ContactModelImpl();

		// PK fields

		contactModel.setContactId(userModel.getContactId());

		// Audit fields

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

		// Other fields

		contactModel.setClassNameId(classNameId);
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
		return _newUserModel(
			DEFAULT_USER_ID, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, true);
	}

	public GroupModel newGlobalGroupModel(long classNameId) {
		return _newGroupModel(
			GLOBAL_GROUP_ID, classNameId, COMPANY_ID, GroupConstants.GLOBAL,
			false);
	}

	public GroupModel newGroupModel(UserModel userModel, long classNameId) {
		return _newGroupModel(
			counter.get(), classNameId, userModel.getUserId(),
			userModel.getScreenName(), false);
	}

	public List<GroupModel> newGroupModels(long classNameId) {
		List<GroupModel> groupModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_GROUP_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			groupModels.add(
				_newGroupModel(i, classNameId, i, "Site " + i, true));
		}

		return groupModels;
	}

	public GroupModel newGuestGroupModel(long classNameId) {
		return _newGroupModel(
			GUEST_GROUP_ID, classNameId, GUEST_GROUP_ID, GroupConstants.GUEST,
			true);
	}

	public UserModel newGuestUserModel() {
		return _newUserModel(counter.get(), "Test", "Test", "Test", false);
	}

	public List<RoleModel> newRoleModels(long classNameId) {
		List<RoleModel> roleModels = new ArrayList<>();

		// Administrator

		roleModels.add(
			_newRoleModel(
				RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR,
				ADMINISTRATOR_ROLE_ID, classNameId));

		// Guest

		roleModels.add(
			_newRoleModel(
				RoleConstants.GUEST, RoleConstants.TYPE_REGULAR, GUEST_ROLE_ID,
				classNameId));

		// Organization Administrator

		roleModels.add(
			_newRoleModel(
				RoleConstants.ORGANIZATION_ADMINISTRATOR,
				RoleConstants.TYPE_ORGANIZATION, counter.get(), classNameId));

		// Organization Owner

		roleModels.add(
			_newRoleModel(
				RoleConstants.ORGANIZATION_OWNER,
				RoleConstants.TYPE_ORGANIZATION, counter.get(), classNameId));

		// Organization User

		roleModels.add(
			_newRoleModel(
				RoleConstants.ORGANIZATION_USER,
				RoleConstants.TYPE_ORGANIZATION, counter.get(), classNameId));

		// Owner

		roleModels.add(
			_newRoleModel(
				RoleConstants.OWNER, RoleConstants.TYPE_REGULAR, OWNER_ROLE_ID,
				classNameId));

		// Power User

		roleModels.add(
			_newRoleModel(
				RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR,
				POWER_USER_ROLE_ID, classNameId));

		// Site Administrator

		roleModels.add(
			_newRoleModel(
				RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE,
				counter.get(), classNameId));

		// Site Member

		roleModels.add(
			_newRoleModel(
				RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE,
				SITE_MEMBER_ROLE_ID, classNameId));

		// Site Owner

		roleModels.add(
			_newRoleModel(
				RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE,
				counter.get(), classNameId));

		// User

		roleModels.add(
			_newRoleModel(
				RoleConstants.USER, RoleConstants.TYPE_REGULAR, USER_ROLE_ID,
				classNameId));

		return roleModels;
	}

	public UserModel newSampleUserModel() {
		return _newUserModel(
			SAMPLE_USER_ID, SAMPLE_USER_NAME, SAMPLE_USER_NAME,
			SAMPLE_USER_NAME, false);
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_USER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_USER_COUNT; i++) {
			String[] userName = _nextUserName(i);

			userModels.add(
				_newUserModel(
					counter.get(), userName[0], userName[1],
					"test" + _userScreenNameCounter.get(), false));
		}

		return userModels;
	}

	public GroupModel newUserPersonalSiteGroupModel(long classNameId) {
		return _newGroupModel(
			_userPersonalSiteGroupId, classNameId, DEFAULT_USER_ID,
			GroupConstants.USER_PERSONAL_SITE, false);
	}

	public VirtualHostModel newVirtualHostModel() {
		VirtualHostModel virtualHostModel = new VirtualHostModelImpl();

		//  PK fields

		virtualHostModel.setVirtualHostId(counter.get());

		// Audit fields

		virtualHostModel.setCompanyId(COMPANY_ID);

		// Other fields

		virtualHostModel.setHostname(BenchmarksPropsValues.VIRTUAL_HOST_NAME);

		return virtualHostModel;
	}

	private UserDataFactory() {
		_userScreenNameCounter = new SimpleCounter();

		_accountId = counter.get();

		_userPersonalSiteGroupId = counter.get();

		try {
			_initUserNames();
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void _initUserNames() throws IOException {
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

	private GroupModel _newGroupModel(
		long groupId, long classNameId, long classPK, String name,
		boolean site) {

		GroupModel groupModel = new GroupModelImpl();

		// UUID

		groupModel.setUuid(SequentialUUID.generate());

		// PK fields

		groupModel.setGroupId(groupId);

		// Audit fields

		groupModel.setCompanyId(COMPANY_ID);
		groupModel.setCreatorUserId(SAMPLE_USER_ID);

		// Other fields

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

	private RoleModel _newRoleModel(
		String name, int type, long roleId, long classNameId) {

		RoleModel roleModel = new RoleModelImpl();

		// UUID

		roleModel.setUuid(SequentialUUID.generate());

		// PK fields

		roleModel.setRoleId(roleId);

		// Audit fields

		roleModel.setCompanyId(COMPANY_ID);
		roleModel.setUserId(SAMPLE_USER_ID);
		roleModel.setUserName(SAMPLE_USER_NAME);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());

		// Other fields

		roleModel.setClassNameId(classNameId);
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}

	private UserModel _newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser) {

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		// UUID

		userModel.setUuid(SequentialUUID.generate());

		// PK fields

		userModel.setUserId(userId);

		// Audit fields

		userModel.setCompanyId(COMPANY_ID);
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());

		// Other fields

		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(counter.get());
		userModel.setPassword("test");
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion("What is your screen name?");
		userModel.setReminderQueryAnswer(screenName);
		userModel.setScreenName(screenName);
		userModel.setEmailAddress(screenName + "@liferay.com");
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

	private String[] _nextUserName(long index) {
		String[] userName = new String[2];

		userName[0] = _firstNames.get(
			(int)(index / _lastNames.size()) % _firstNames.size());
		userName[1] = _lastNames.get((int)(index % _lastNames.size()));

		return userName;
	}

	private static UserDataFactory _userDataFactory = new UserDataFactory();

	private final long _accountId;
	private List<String> _firstNames;
	private List<String> _lastNames;
	private final long _userPersonalSiteGroupId;
	private final SimpleCounter _userScreenNameCounter;

}