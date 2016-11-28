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

import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.StringPool;
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
import java.util.Map;

/**
 * @author Lily Chi
 */
public class UserDataFactory extends BaseDataFactory {

	public UserDataFactory(InitContext initContext) throws Exception {
		super(initContext);

		_globalGroupId = initContext.getCounter().get();
		_guestGroupId = initContext.getCounter().get();

		_initAccountModel();
		_initCompanyModel();
		_initGroupModels();
		_initUserFirstNames();
		_initUserLastNames();
		_initUserModels();
		_initRoleModels();
		_initVirtualHostModel();
	}

	public AccountModel getAccountModel() {
		return _accountModel;
	}

	public RoleModel getAdministratorRoleModel() {
		return _administratorRoleModel;
	}

	public CompanyModel getCompanyModel() {
		return _companyModel;
	}

	public UserModel getDefaultUserModel() {
		return _defaultUserModel;
	}

	public List<String> getFirstNames() {
		return _firstNames;
	}

	public long getGlobalGroupId() {
		return _globalGroupId;
	}

	public GroupModel getGlobalGroupModel() {
		return _globalGroupModel;
	}

	public long getGroupClassNameId() {
		return getClassNameId(Group.class, initContext.getClassNameModels());
	}

	public List<GroupModel> getGroupModels() {
		return _groupModels;
	}

	public GroupModel getGuestGroupModel() {
		return _guestGroupModel;
	}

	public RoleModel getGuestRoleModel() {
		return _guestRoleModel;
	}

	public UserModel getGuestUserModel() {
		return _guestUserModel;
	}

	public List<String> getLastNames() {
		return _lastNames;
	}

	public List<Long> getNewUserGroupIds(long groupId) {
		int maxUserToGroupCount = initContext.getMaxUserToGroupCount();
		int maxGroupsCount = initContext.getMaxGroupsCount();

		List<Long> groupIds = new ArrayList<>(maxUserToGroupCount + 1);

		groupIds.add(_guestGroupModel.getGroupId());

		if ((groupId + maxUserToGroupCount) > maxGroupsCount) {
			groupId = groupId - maxUserToGroupCount + 1;
		}

		for (int i = 0; i < maxUserToGroupCount; i++) {
			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public RoleModel getOwnerRoleModel() {
		return _ownerRoleModel;
	}

	public RoleModel getPowerUserRoleModel() {
		return _powerUserRoleModel;
	}

	public List<RoleModel> getRoleModels() {
		return _roleModels;
	}

	public UserModel getSampleUserModel() {
		return _sampleUserModel;
	}

	public RoleModel getSiteMemberRoleModel() {
		return _siteMemberRoleModel;
	}

	public RoleModel getUserRoleModel() {
		return _userRoleModel;
	}

	public VirtualHostModel getVirtualHostModel() {
		return _virtualHostModel;
	}

	public ContactModel newContactModel(UserModel userModel) {
		ContactModel contactModel = new ContactModelImpl();

		contactModel.setContactId(userModel.getContactId());
		contactModel.setCompanyId(userModel.getCompanyId());
		contactModel.setUserId(userModel.getUserId());

		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		String fullName = fullNameGenerator.getFullName(
			userModel.getFirstName(), userModel.getMiddleName(),
			userModel.getLastName());

		contactModel.setUserName(fullName);

		contactModel.setCreateDate(new Date());
		contactModel.setModifiedDate(new Date());
		contactModel.setClassNameId(
			getClassNameId(User.class, initContext.getClassNameModels()));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setAccountId(initContext.getAccountId());
		contactModel.setParentContactId(
			ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contactModel.setEmailAddress(userModel.getEmailAddress());
		contactModel.setFirstName(userModel.getFirstName());
		contactModel.setLastName(userModel.getLastName());
		contactModel.setMale(true);
		contactModel.setBirthday(new Date());

		return contactModel;
	}

	public GroupModel newGroupModel(UserModel userModel) throws Exception {
		SimpleCounter counter = initContext.getCounter();

		return _newGroupModel(
			counter.get(), getClassNameId(
				User.class, initContext.getClassNameModels()),
			userModel.getUserId(), userModel.getScreenName(), false);
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			initContext.getMaxUserCount());

		SimpleCounter counter = initContext.getCounter();

		for (int i = 0; i < initContext.getMaxUserCount(); i++) {
			String[] userName = nextUserName(i);
			String lastName = "test" + _userScreenNameCounter.get();

			userModels.add(
				newUserModel(
					counter.get(), userName[0], userName[1], lastName, false));
		}

		return userModels;
	}

	protected UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser) {

		String greeting =
			DataFactoryConstants.GREETING_PREFIX + screenName +
				StringPool.EXCLAMATION;

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(initContext.getCompanyId());
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(initContext.getCounter().get());
		userModel.setPassword(DataFactoryConstants.USER_PASSWORD);
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion(
			DataFactoryConstants.REMINDER_QUERY_QUESTION);
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(
			screenName + DataFactoryConstants.EMAIL_POSTFIX);
		userModel.setScreenName(screenName);
		userModel.setLanguageId(DataFactoryConstants.LANGUAGE_ID);
		userModel.setGreeting(greeting);
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

	protected String[] nextUserName(long index) {
		String[] userName = new String[2];

		int firstNameSize = _firstNames.size();
		int lastNameSize = _lastNames.size();

		int firstNameIndex = (int)(index / lastNameSize) % firstNameSize;
		int lastNameIndex = (int)(index % lastNameSize);

		userName[0] = _firstNames.get(firstNameIndex);
		userName[1] = _lastNames.get(lastNameIndex);

		return userName;
	}

	private void _initAccountModel() {
		_accountModel = new AccountModelImpl();

		_accountModel.setAccountId(initContext.getAccountId());
		_accountModel.setCompanyId(initContext.getCompanyId());
		_accountModel.setCreateDate(new Date());
		_accountModel.setModifiedDate(new Date());
		_accountModel.setName(DataFactoryConstants.ACCOUNT_NAME);
		_accountModel.setLegalName(DataFactoryConstants.ACCOUNT_LEGAL_NAME);
	}

	private void _initCompanyModel() {
		_companyModel = new CompanyModelImpl();

		_companyModel.setCompanyId(initContext.getCompanyId());
		_companyModel.setAccountId(initContext.getAccountId());
		_companyModel.setWebId(DataFactoryConstants.COMPANY_WEBID);
		_companyModel.setMx(DataFactoryConstants.COMPANY_WEBID);
		_companyModel.setActive(true);
	}

	private GroupModel _initGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site)
		throws Exception {

		GroupModel globalGroupModel = _newGroupModel(
			groupId, classNameId, classPK, name, site);

		return globalGroupModel;
	}

	private void _initGroupModels() throws Exception {
		int maxGroupsCount = initContext.getMaxGroupsCount();

		Map<String, ClassNameModel> classNameModels =
			initContext.getClassNameModels();

		_globalGroupModel = _initGroupModel(
			_globalGroupId, getClassNameId(Company.class, classNameModels),
			initContext.getCompanyId(), GroupConstants.GLOBAL, false);

		_guestGroupModel = _initGroupModel(
			_guestGroupId, getGroupClassNameId(), _guestGroupId,
			GroupConstants.GUEST, true);

		_groupModels = new ArrayList<>(maxGroupsCount);

		for (int i = 1; i <= maxGroupsCount; i++) {
			GroupModel groupModel = _initGroupModel(
				i, getGroupClassNameId(), i,
				DataFactoryConstants.GROUP_NAME_PREFIX + i, true);

			_groupModels.add(groupModel);
		}
	}

	private void _initRoleModels() {
		_roleModels = new ArrayList<>();

		// Administrator

		_administratorRoleModel = _newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR);

		_roleModels.add(_administratorRoleModel);

		// Guest

		_guestRoleModel = _newRoleModel(
			RoleConstants.GUEST, RoleConstants.TYPE_REGULAR);

		_roleModels.add(_guestRoleModel);

		// Organization Administrator

		RoleModel organizationAdministratorRoleModel = _newRoleModel(
			RoleConstants.ORGANIZATION_ADMINISTRATOR,
			RoleConstants.TYPE_ORGANIZATION);

		_roleModels.add(organizationAdministratorRoleModel);

		// Organization Owner

		RoleModel organizationOwnerRoleModel = _newRoleModel(
			RoleConstants.ORGANIZATION_OWNER, RoleConstants.TYPE_ORGANIZATION);

		_roleModels.add(organizationOwnerRoleModel);

		// Organization User

		RoleModel organizationUserRoleModel = _newRoleModel(
			RoleConstants.ORGANIZATION_USER, RoleConstants.TYPE_ORGANIZATION);

		_roleModels.add(organizationUserRoleModel);

		// Owner

		_ownerRoleModel = _newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR);

		_roleModels.add(_ownerRoleModel);

		// Power User

		_powerUserRoleModel = _newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR);

		_roleModels.add(_powerUserRoleModel);

		// Site Administrator

		RoleModel siteAdministratorRoleModel = _newRoleModel(
			RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE);

		_roleModels.add(siteAdministratorRoleModel);

		// Site Member

		_siteMemberRoleModel = _newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE);

		_roleModels.add(_siteMemberRoleModel);

		// Site Owner

		RoleModel siteOwnerRoleModel = _newRoleModel(
			RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE);

		_roleModels.add(siteOwnerRoleModel);

		// User

		_userRoleModel = _newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR);

		_roleModels.add(_userRoleModel);
	}

	private void _initUserFirstNames() throws IOException {
		_firstNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				getResourceInputStream(DataFactoryConstants.FIRST_NAME_LIST)));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			_firstNames.add(line);
		}

		unsyncBufferedReader.close();
	}

	private void _initUserLastNames() throws IOException {
		_lastNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				getResourceInputStream(DataFactoryConstants.LAST_NAME_LIST)));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			_lastNames.add(line);
		}

		unsyncBufferedReader.close();
	}

	private void _initUserModels() {
		SimpleCounter counter = initContext.getCounter();

		long defaultUserId = initContext.getDefaultUserId();

		_defaultUserModel = newUserModel(
			defaultUserId, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			true);

		_guestUserModel = newUserModel(
			counter.get(), "Test", "Test", "Test", false);

		_sampleUserModel = newUserModel(
			initContext.getSampleUserId(),
			DataFactoryConstants.SAMPLE_USER_NAME,
			DataFactoryConstants.SAMPLE_USER_NAME,
			DataFactoryConstants.SAMPLE_USER_NAME, false);
	}

	private void _initVirtualHostModel() {
		_virtualHostModel = new VirtualHostModelImpl();

		SimpleCounter counter = initContext.getCounter();

		_virtualHostModel.setVirtualHostId(counter.get());

		_virtualHostModel.setCompanyId(initContext.getCompanyId());
		_virtualHostModel.setHostname(initContext.getVirtualHostname());
	}

	private GroupModel _newGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site)
		throws Exception {

		GroupModel groupModel = new GroupModelImpl();

		groupModel.setUuid(SequentialUUID.generate());
		groupModel.setGroupId(groupId);
		groupModel.setCompanyId(initContext.getCompanyId());
		groupModel.setCreatorUserId(initContext.getSampleUserId());
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

	private RoleModel _newRoleModel(String name, int type) {
		RoleModel roleModel = new RoleModelImpl();

		SimpleCounter counter = initContext.getCounter();

		long classNameId = getClassNameId(
			Role.class, initContext.getClassNameModels());

		roleModel.setUuid(SequentialUUID.generate());
		roleModel.setRoleId(counter.get());
		roleModel.setCompanyId(initContext.getCompanyId());
		roleModel.setUserId(initContext.getSampleUserId());
		roleModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());
		roleModel.setClassNameId(classNameId);
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}

	private AccountModel _accountModel;
	private RoleModel _administratorRoleModel;
	private CompanyModel _companyModel;
	private UserModel _defaultUserModel;
	private List<String> _firstNames;
	private final long _globalGroupId;
	private GroupModel _globalGroupModel;
	private List<GroupModel> _groupModels;
	private final long _guestGroupId;
	private GroupModel _guestGroupModel;
	private RoleModel _guestRoleModel;
	private UserModel _guestUserModel;
	private List<String> _lastNames;
	private RoleModel _ownerRoleModel;
	private RoleModel _powerUserRoleModel;
	private List<RoleModel> _roleModels;
	private UserModel _sampleUserModel;
	private RoleModel _siteMemberRoleModel;
	private RoleModel _userRoleModel;
	private final SimpleCounter _userScreenNameCounter = new SimpleCounter();
	private VirtualHostModel _virtualHostModel;

}