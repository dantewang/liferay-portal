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

import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class UserDataFactory {

	public UserDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public AccountModel getAccountModel() {
		return _initContext.getAccountModel();
	}

	public RoleModel getAdministratorRoleModel() {
		return _initContext.getAdministratorRoleModel();
	}

	public CompanyModel getCompanyModel() {
		return _initContext.getCompanyModel();
	}

	public UserModel getDefaultUserModel() {
		return _initContext.getDefaultUserModel();
	}

	public GroupModel getGlobalGroupModel() {
		return _initContext.getGlobalGroupModel();
	}

	public List<GroupModel> getGroupModels() {
		return _initContext.getGroupModels();
	}

	public GroupModel getGuestGroupModel() {
		return _initContext.getGuestGroupModel();
	}

	public UserModel getGuestUserModel() {
		return _initContext.getGuestUserModel();
	}

	public int getMaxGroupCount() {
		return _initContext.getMaxGroupsCount();
	}

	public List<Long> getNewUserGroupIds(long groupId) {
		int maxUserToGroupCount = _initContext.getMaxUserToGroupCount();
		int maxGroupsCount = _initContext.getMaxGroupsCount();

		List<Long> groupIds = new ArrayList<>(maxUserToGroupCount + 1);

		groupIds.add(_initContext.getGuestGroupModel().getGroupId());

		if ((groupId + maxUserToGroupCount) > maxGroupsCount) {
			groupId = groupId - maxUserToGroupCount + 1;
		}

		for (int i = 0; i < maxUserToGroupCount; i++) {
			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public RoleModel getPowerUserRoleModel() {
		return _initContext.getPowerUserRoleModel();
	}

	public List<RoleModel> getRoleModels() {
		return _initContext.getRoleModels();
	}

	public UserModel getSampleUserModel() {
		return _initContext.getSampleUserModel();
	}

	public RoleModel getUserRoleModel() {
		return _initContext.getUserRoleModel();
	}

	public VirtualHostModel getVirtualHostModel() {
		return _initContext.getVirtualHostModel();
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
			_initContext.getClassNameId(
				User.class, _initContext.getClassNameModels()));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setAccountId(_initContext.getAccountId());
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

		SimpleCounter counter = _initContext.getCounter();

		return _initContext.newGroupModel(
			counter.get(), _initContext.getClassNameId(
				User.class, _initContext.getClassNameModels()),
			userModel.getUserId(), userModel.getScreenName(), false,
			_initContext.getCompanyId(), _initContext.getSampleUserId());
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			_initContext.getMaxUserCount());

		SimpleCounter counter = _initContext.getCounter();

		for (int i = 0; i < _initContext.getMaxUserCount(); i++) {
			String[] userName = nextUserName(i);
			String lastName =
				"test" + _initContext.getUserScreenNameCounter().get();

			userModels.add(
				_initContext.newUserModel(
					counter.get(), userName[0], userName[1],lastName, false,
					counter.get(),_initContext.getCompanyId()));
		}

		return userModels;
	}

	protected String[] nextUserName(long index) {
		String[] userName = new String[2];
		int firstNameSize = _initContext.getFirstNames().size();
		int lastNameSize = _initContext.getLastNames().size();

		userName[0] = _initContext.getFirstNames().get(
			(int)(index / lastNameSize) % firstNameSize);
		userName[1] = _initContext.getLastNames().get(
			(int)(index % lastNameSize));

		return userName;
	}

	private final InitContext _initContext;

}