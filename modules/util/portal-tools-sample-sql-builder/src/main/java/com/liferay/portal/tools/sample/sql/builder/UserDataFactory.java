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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class UserDataFactory {
	
	public static AccountModel getAccountModel() {
		return InitContextUtil.getAccountModel();
	}

	public static RoleModel getAdministratorRoleModel() {
		return InitContextUtil.getAdministratorRoleModel();
	}

	public static CompanyModel getCompanyModel() {
		return InitContextUtil.getCompanyModel();
	}
	
	public static UserModel getDefaultUserModel() {
		return InitContextUtil.getDefaultUserModel();
	}
	
	public static GroupModel getGlobalGroupModel() {
		return InitContextUtil.getGlobalGroupModel();
	}

	public static List<GroupModel> getGroupModels() {
		return InitContextUtil.getGroupModels();
	}

	public static GroupModel getGuestGroupModel() {
		return InitContextUtil.getGuestGroupModel();
	}

	public static UserModel getGuestUserModel() {
		return InitContextUtil.getGuestUserModel();
	}

	public static int getMaxGroupCount() {
		return InitContextUtil.getMaxGroupsCount();
	}

	public static RoleModel getPowerUserRoleModel() {
		return InitContextUtil.getPowerUserRoleModel();
	}

	public static List<RoleModel> getRoleModels() {
		return InitContextUtil.getRoleModels();
	}

	public static UserModel getSampleUserModel() {
		return InitContextUtil.getSampleUserModel();
	}

	public static RoleModel getUserRoleModel() {
		return InitContextUtil.getUserRoleModel();
	}

	public static VirtualHostModel getVirtualHostModel() {
		return InitContextUtil.getVirtualHostModel();
	}

	public static List<Long> getNewUserGroupIds(long groupId) {
		int maxUserToGroupCount = InitContextUtil.getMaxUserToGroupCount();
		int maxGroupsCount = InitContextUtil.getMaxGroupsCount();

		List<Long> groupIds = new ArrayList<>(maxUserToGroupCount + 1);

		groupIds.add(InitContextUtil.getGuestGroupModel().getGroupId());

		if ((groupId + maxUserToGroupCount) > maxGroupsCount) {
			groupId = groupId - maxUserToGroupCount + 1;
		}

		for (int i = 0; i < maxUserToGroupCount; i++) {
			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public static ContactModel newContactModel(UserModel userModel) {
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
			InitDataFactoryUtil.getClassNameId(
				User.class, InitContextUtil.getClassNameModels()));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setAccountId(InitContextUtil.getAccountId());
		contactModel.setParentContactId(
			ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contactModel.setEmailAddress(userModel.getEmailAddress());
		contactModel.setFirstName(userModel.getFirstName());
		contactModel.setLastName(userModel.getLastName());
		contactModel.setMale(true);
		contactModel.setBirthday(new Date());

		return contactModel;
	}

	public static GroupModel newGroupModel(UserModel userModel)
		throws Exception {

		return InitDataFactoryUtil.newGroupModel(
			InitContextUtil.getCounter().get(),
			InitDataFactoryUtil.getClassNameId(
				User.class, InitContextUtil.getClassNameModels()),
			userModel.getUserId(), userModel.getScreenName(), false,
			InitContextUtil.getCompanyId(), InitContextUtil.getSampleUserId());
	}

	public static List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			InitContextUtil.getMaxUserCount());

		for (int i = 0; i < InitContextUtil.getMaxUserCount(); i++) {
			String[] userName = InitDataFactoryUtil.nextUserName(i);
			String lastName =
				"test" + InitContextUtil.getUserScreenNameCounter().get();
			userModels.add(
				InitDataFactoryUtil.newUserModel(
					InitContextUtil.getCounter().get(), userName[0],
					userName[1], lastName, false,
					InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId()));
		}

		return userModels;
	}

}