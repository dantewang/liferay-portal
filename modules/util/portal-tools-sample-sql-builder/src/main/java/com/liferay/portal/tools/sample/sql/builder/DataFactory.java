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

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.social.BlogsActivityKeys;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceCatalogModel;
import com.liferay.commerce.product.model.CommerceChannelModel;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBCategoryModel;
import com.liferay.message.boards.model.MBDiscussionModel;
import com.liferay.message.boards.model.MBMailingListModel;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.model.MBStatsUserModel;
import com.liferay.message.boards.model.MBThreadFlagModel;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.message.boards.model.impl.MBCategoryModelImpl;
import com.liferay.message.boards.model.impl.MBDiscussionModelImpl;
import com.liferay.message.boards.model.impl.MBMailingListModelImpl;
import com.liferay.message.boards.model.impl.MBMessageModelImpl;
import com.liferay.message.boards.model.impl.MBStatsUserModelImpl;
import com.liferay.message.boards.model.impl.MBThreadFlagModelImpl;
import com.liferay.message.boards.model.impl.MBThreadModelImpl;
import com.liferay.message.boards.social.MBActivityKeys;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.model.ReleaseModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.ReleaseModelImpl;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.subscription.constants.SubscriptionConstants;
import com.liferay.subscription.model.SubscriptionModel;
import com.liferay.subscription.model.impl.SubscriptionModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.WikiPageResourceModel;
import com.liferay.wiki.model.impl.WikiNodeModelImpl;
import com.liferay.wiki.model.impl.WikiPageModelImpl;
import com.liferay.wiki.model.impl.WikiPageResourceModelImpl;
import com.liferay.wiki.social.WikiActivityKeys;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.sql.Types;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory extends BaseDDMDataFactory {

	public DataFactory() throws Exception {
		_simpleDateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", TimeZone.getDefault());

		_timeCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();

		_accountId = counter.get();
		_userPersonalSiteGroupId = counter.get();

		initUserNames();
	}

	public long getAdministratorRoleId() {
		return ADMINISTRATOR_ROLE_ID;
	}

	public BaseDataFactory getDataFactoryInstance(String name) {
		if (name.equals("assetDataFactory")) {
			return AssetDataFactory.getInstance();
		}
		else if (name.equals("blogDataFactory")) {
			return BlogDataFactory.getInstance();
		}
		else if (name.equals("commerceDataFactory")) {
			return CommerceDataFactory.getInstance();
		}
		else if (name.equals("counterDataFactory")) {
			return CounterDataFactory.getInstance();
		}
		else if (name.equals("ddlDDMDataFactory")) {
			return DDLDDMDataFactory.getInstance();
		}
		else if (name.equals("dlDataFactory")) {
			return DLDataFactory.getInstance();
		}
		else if (name.equals("fragmentDataFactory")) {
			return FragmentDataFactory.getInstance();
		}
		else if (name.equals("journalDataFactory")) {
			return JournalDataFactory.getInstance();
		}
		else if (name.equals("layoutDataFactory")) {
			return LayoutDataFactory.getInstance();
		}
		else if (name.equals("portletPreferenceDataFactory")) {
			return PortletPreferenceDataFactory.getInstance();
		}

		return ClassNameDataFactory.getInstance();
	}

	public int getMaxGroupCount() {
		return BenchmarksPropsValues.MAX_GROUP_COUNT;
	}

	public int getMaxWikiPageCommentCount() {
		return BenchmarksPropsValues.MAX_WIKI_PAGE_COMMENT_COUNT;
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

	public void initUserNames() throws IOException {
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

		return newGroupModel(
			counter.get(), classNameId,
			commerceCatalogModel.getCommerceCatalogId(),
			commerceCatalogModel.getName(), false);
	}

	public ResourcePermissionModel newCommerceCatalogResourcePermissionModel(
		CommerceCatalogModel commerceCatalogModel) {

		return newResourcePermissionModel(
			CommerceCatalog.class.getName(),
			String.valueOf(commerceCatalogModel.getCommerceCatalogId()),
			GUEST_ROLE_ID, SAMPLE_USER_ID);
	}

	public GroupModel newCommerceChannelGroupModel(
		CommerceChannelModel commerceChannelModel, long classNameId) {

		return newGroupModel(
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
		return newUserModel(
			DEFAULT_USER_ID, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, true);
	}

	public GroupModel newGlobalGroupModel(long classNameId) {
		return newGroupModel(
			GLOBAL_GROUP_ID, classNameId, COMPANY_ID, GroupConstants.GLOBAL,
			false);
	}

	public GroupModel newGroupModel(UserModel userModel, long classNameId) {
		return newGroupModel(
			counter.get(), classNameId, userModel.getUserId(),
			userModel.getScreenName(), false);
	}

	public List<GroupModel> newGroupModels(long classNameId) {
		List<GroupModel> groupModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_GROUP_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			groupModels.add(
				newGroupModel(i, classNameId, i, "Site " + i, true));
		}

		return groupModels;
	}

	public GroupModel newGuestGroupModel(long classNameId) {
		return newGroupModel(
			GUEST_GROUP_ID, classNameId, GUEST_GROUP_ID, GroupConstants.GUEST,
			true);
	}

	public UserModel newGuestUserModel() {
		return newUserModel(counter.get(), "Test", "Test", "Test", false);
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT; i++) {
			mbCategoryModels.add(newMBCategoryModel(groupId, i));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		// UUID

		mbDiscussionModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbDiscussionModel.setDiscussionId(counter.get());

		// Group instance

		mbDiscussionModel.setGroupId(groupId);

		// Audit fields

		mbDiscussionModel.setCompanyId(COMPANY_ID);
		mbDiscussionModel.setUserId(SAMPLE_USER_ID);
		mbDiscussionModel.setUserName(SAMPLE_USER_NAME);
		mbDiscussionModel.setCreateDate(new Date());
		mbDiscussionModel.setModifiedDate(new Date());

		// Other fields

		mbDiscussionModel.setClassNameId(classNameId);
		mbDiscussionModel.setClassPK(classPK);
		mbDiscussionModel.setThreadId(threadId);
		mbDiscussionModel.setLastPublishDate(new Date());

		return mbDiscussionModel;
	}

	public MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel, UserModel sampleUserModel) {

		MBMailingListModel mbMailingListModel = new MBMailingListModelImpl();

		// UUID

		mbMailingListModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbMailingListModel.setMailingListId(counter.get());

		// Group instance

		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());

		// Audit fields

		mbMailingListModel.setCompanyId(COMPANY_ID);
		mbMailingListModel.setUserId(SAMPLE_USER_ID);
		mbMailingListModel.setUserName(SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());

		// Other fields

		mbMailingListModel.setCategoryId(mbCategoryModel.getCategoryId());
		mbMailingListModel.setInProtocol("pop3");
		mbMailingListModel.setInServerPort(110);
		mbMailingListModel.setInUserName(sampleUserModel.getEmailAddress());
		mbMailingListModel.setInPassword(sampleUserModel.getPassword());
		mbMailingListModel.setInReadInterval(5);
		mbMailingListModel.setOutServerPort(25);

		return mbMailingListModel;
	}

	public MBMessageModel newMBMessageModel(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int index) {

		long messageId = 0;
		long parentMessageId = 0;
		String subject = null;
		String body = null;
		String urlSubject = null;

		if (index == 0) {
			messageId = mbThreadModel.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			subject = String.valueOf(classPK);
			body = String.valueOf(classPK);
			urlSubject = String.valueOf(mbThreadModel.getRootMessageId());
		}
		else {
			messageId = counter.get();
			parentMessageId = mbThreadModel.getRootMessageId();
			subject = "N/A";
			body = "This is test comment " + index + ".";
			urlSubject = "test-comment-" + index;
		}

		return newMBMessageModel(
			mbThreadModel.getGroupId(), classNameId, classPK,
			MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			mbThreadModel.getThreadId(), messageId,
			mbThreadModel.getRootMessageId(), parentMessageId, subject,
			urlSubject, body);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT);

		mbMessageModels.add(
			newMBMessageModel(
				mbThreadModel.getGroupId(), 0, 0, mbThreadModel.getCategoryId(),
				mbThreadModel.getThreadId(), mbThreadModel.getRootMessageId(),
				mbThreadModel.getRootMessageId(),
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, "Test Message 1",
				"test-message-1", "This is test message 1."));

		for (int i = 2; i <= BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT; i++) {
			mbMessageModels.add(
				newMBMessageModel(
					mbThreadModel.getGroupId(), 0, 0,
					mbThreadModel.getCategoryId(), mbThreadModel.getThreadId(),
					counter.get(), mbThreadModel.getRootMessageId(),
					mbThreadModel.getRootMessageId(), "Test Message " + i,
					"test-message-" + i, "This is test message " + i + "."));
		}

		return mbMessageModels;
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int maxMessageCount) {

		List<MBMessageModel> mbMessageModels = new ArrayList<>(maxMessageCount);

		for (int i = 1; i <= maxMessageCount; i++) {
			mbMessageModels.add(
				newMBMessageModel(mbThreadModel, classNameId, classPK, i));
		}

		return mbMessageModels;
	}

	public MBStatsUserModel newMBStatsUserModel(long groupId) {
		MBStatsUserModel mbStatsUserModel = new MBStatsUserModelImpl();

		// PK fields

		mbStatsUserModel.setStatsUserId(counter.get());

		// Group instance

		mbStatsUserModel.setGroupId(groupId);

		// Audit fields

		mbStatsUserModel.setUserId(SAMPLE_USER_ID);

		// Other fields

		mbStatsUserModel.setMessageCount(
			BenchmarksPropsValues.MAX_MB_CATEGORY_COUNT *
				BenchmarksPropsValues.MAX_MB_THREAD_COUNT *
					BenchmarksPropsValues.MAX_MB_MESSAGE_COUNT);
		mbStatsUserModel.setLastPostDate(new Date());

		return mbStatsUserModel;
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		// UUID

		mbThreadFlagModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbThreadFlagModel.setThreadFlagId(counter.get());

		// Group instance

		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());

		// Audit fields

		mbThreadFlagModel.setCompanyId(COMPANY_ID);
		mbThreadFlagModel.setUserId(SAMPLE_USER_ID);
		mbThreadFlagModel.setUserName(SAMPLE_USER_NAME);
		mbThreadFlagModel.setCreateDate(new Date());
		mbThreadFlagModel.setModifiedDate(new Date());

		// Other fields

		mbThreadFlagModel.setThreadId(mbThreadModel.getThreadId());
		mbThreadFlagModel.setLastPublishDate(new Date());

		return mbThreadFlagModel;
	}

	public MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId) {

		return newMBThreadModel(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		List<MBThreadModel> mbThreadModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_MB_THREAD_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_MB_THREAD_COUNT; i++) {
			mbThreadModels.add(
				newMBThreadModel(
					counter.get(), mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(), counter.get()));
		}

		return mbThreadModels;
	}

	public List<ReleaseModel> newReleaseModels() throws IOException {
		List<ReleaseModel> releases = new ArrayList<>();

		Version latestSchemaVersion =
			PortalUpgradeProcess.getLatestSchemaVersion();

		releases.add(
			newReleaseModel(
				ReleaseConstants.DEFAULT_ID,
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
				latestSchemaVersion.toString(), ReleaseInfo.getBuildNumber(),
				false, ReleaseConstants.TEST_STRING));

		try (InputStream is = DataFactory.class.getResourceAsStream(
				"dependencies/releases.txt");
			Reader reader = new InputStreamReader(is);
			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String[] parts = StringUtil.split(line, CharPool.COLON);

				if (parts.length > 0) {
					String servletContextName = parts[0];
					String schemaVersion = parts[1];

					releases.add(
						newReleaseModel(
							counter.get(), servletContextName, schemaVersion, 0,
							true, null));
				}
			}
		}

		return releases;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		return newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() == DEFAULT_USER_ID) {
			return Collections.singletonList(
				newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					OWNER_ROLE_ID, DEFAULT_USER_ID));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				OWNER_ROLE_ID, DEFAULT_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel, String className) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMStructure.class.getName(), className);
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, OWNER_ROLE_ID, ddmStructureModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, USER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel, String className) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMTemplate.class.getName(), className);
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, OWNER_ROLE_ID, ddmTemplateModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, USER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				OWNER_ROLE_ID, SAMPLE_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		return newResourcePermissionModels(
			Layout.class.getName(), String.valueOf(layoutModel.getPlid()), 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		return newResourcePermissionModels(
			MBCategory.class.getName(),
			String.valueOf(mbCategoryModel.getCategoryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return newResourcePermissionModels(
			MBMessage.class.getName(),
			String.valueOf(mbMessageModel.getMessageId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		PortletPreferencesModel portletPreferencesModel) {

		String portletId = portletPreferencesModel.getPortletId();

		String name = portletId;

		int index = portletId.indexOf(StringPool.UNDERLINE);

		if (index > 0) {
			name = portletId.substring(0, index);
		}

		String primKey = PortletPermissionUtil.getPrimaryKey(
			portletPreferencesModel.getPlid(), portletId);

		return newResourcePermissionModels(name, primKey, 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				OWNER_ROLE_ID, SAMPLE_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				OWNER_ROLE_ID, userModel.getUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		return newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		return newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()), SAMPLE_USER_ID);
	}

	public List<RoleModel> newRoleModels(long classNameId) {
		List<RoleModel> roleModels = new ArrayList<>();

		// Administrator

		roleModels.add(
			newRoleModel(
				RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR,
				ADMINISTRATOR_ROLE_ID, classNameId));

		// Guest

		roleModels.add(
			newRoleModel(
				RoleConstants.GUEST, RoleConstants.TYPE_REGULAR, GUEST_ROLE_ID,
				classNameId));

		// Organization Administrator

		roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_ADMINISTRATOR,
				RoleConstants.TYPE_ORGANIZATION, counter.get(), classNameId));

		// Organization Owner

		roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_OWNER,
				RoleConstants.TYPE_ORGANIZATION, counter.get(), classNameId));

		// Organization User

		roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_USER,
				RoleConstants.TYPE_ORGANIZATION, counter.get(), classNameId));

		// Owner

		roleModels.add(
			newRoleModel(
				RoleConstants.OWNER, RoleConstants.TYPE_REGULAR, OWNER_ROLE_ID,
				classNameId));

		// Power User

		roleModels.add(
			newRoleModel(
				RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR,
				POWER_USER_ROLE_ID, classNameId));

		// Site Administrator

		roleModels.add(
			newRoleModel(
				RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE,
				counter.get(), classNameId));

		// Site Member

		roleModels.add(
			newRoleModel(
				RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE,
				SITE_MEMBER_ROLE_ID, classNameId));

		// Site Owner

		roleModels.add(
			newRoleModel(
				RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE,
				counter.get(), classNameId));

		// User

		roleModels.add(
			newRoleModel(
				RoleConstants.USER, RoleConstants.TYPE_REGULAR, USER_ROLE_ID,
				classNameId));

		return roleModels;
	}

	public UserModel newSampleUserModel() {
		return newUserModel(
			SAMPLE_USER_ID, SAMPLE_USER_NAME, SAMPLE_USER_NAME,
			SAMPLE_USER_NAME, false);
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel, long classNameId) {

		return newSocialActivityModel(
			blogsEntryModel.getGroupId(), classNameId,
			blogsEntryModel.getEntryId(), BlogsActivityKeys.ADD_ENTRY,
			"{\"title\":\"" + blogsEntryModel.getTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel, long classNameId) {

		return newSocialActivityModel(
			dlFileEntryModel.getGroupId(), classNameId,
			dlFileEntryModel.getFileEntryId(), DLActivityKeys.ADD_FILE_ENTRY,
			StringPool.BLANK);
	}

	public SocialActivityModel newSocialActivityModel(
		JournalArticleModel journalArticleModel, long classNameId) {

		int type = JournalActivityKeys.UPDATE_ARTICLE;

		if (journalArticleModel.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT) {

			type = JournalActivityKeys.ADD_ARTICLE;
		}

		return newSocialActivityModel(
			journalArticleModel.getGroupId(), classNameId,
			journalArticleModel.getResourcePrimKey(), type,
			"{\"title\":\"" + journalArticleModel.getUrlTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		MBMessageModel mbMessageModel, long wikiPageClassNameId,
		long mbMessageClassNameId) {

		long classNameId = mbMessageModel.getClassNameId();
		long classPK = mbMessageModel.getClassPK();

		int type = 0;
		String extraData = null;

		if (classNameId == wikiPageClassNameId) {
			extraData = "{\"version\":1}";

			type = WikiActivityKeys.ADD_PAGE;
		}
		else if (classNameId == 0) {
			extraData = "{\"title\":\"" + mbMessageModel.getSubject() + "\"}";

			type = MBActivityKeys.ADD_MESSAGE;

			classNameId = mbMessageClassNameId;
			classPK = mbMessageModel.getMessageId();
		}
		else {
			StringBundler sb = new StringBundler(5);

			sb.append("{\"messageId\": \"");
			sb.append(mbMessageModel.getMessageId());
			sb.append("\", \"title\": ");
			sb.append(mbMessageModel.getSubject());
			sb.append("}");

			extraData = sb.toString();

			type = SocialActivityConstants.TYPE_ADD_COMMENT;
		}

		return newSocialActivityModel(
			mbMessageModel.getGroupId(), classNameId, classPK, type, extraData);
	}

	public SubscriptionModel newSubscriptionModel(
		BlogsEntryModel blogsEntryModel, long classNameId) {

		return newSubscriptionModel(classNameId, blogsEntryModel.getEntryId());
	}

	public SubscriptionModel newSubscriptionModel(
		MBThreadModel mBThreadModel, long classNameId) {

		return newSubscriptionModel(classNameId, mBThreadModel.getThreadId());
	}

	public SubscriptionModel newSubscriptionModel(
		WikiPageModel wikiPageModel, long classNameId) {

		return newSubscriptionModel(
			classNameId, wikiPageModel.getResourcePrimKey());
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_USER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_USER_COUNT; i++) {
			String[] userName = nextUserName(i);

			userModels.add(
				newUserModel(
					counter.get(), userName[0], userName[1],
					"test" + _userScreenNameCounter.get(), false));
		}

		return userModels;
	}

	public GroupModel newUserPersonalSiteGroupModel(long classNameId) {
		return newGroupModel(
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

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		List<WikiNodeModel> wikiNodeModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_WIKI_NODE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_WIKI_NODE_COUNT; i++) {
			wikiNodeModels.add(newWikiNodeModel(groupId, i));
		}

		return wikiNodeModels;
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel) {
		List<WikiPageModel> wikiPageModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_WIKI_PAGE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_WIKI_PAGE_COUNT; i++) {
			wikiPageModels.add(newWikiPageModel(wikiNodeModel, i));
		}

		return wikiPageModels;
	}

	public WikiPageResourceModel newWikiPageResourceModel(
		WikiPageModel wikiPageModel) {

		WikiPageResourceModel wikiPageResourceModel =
			new WikiPageResourceModelImpl();

		// UUID

		wikiPageResourceModel.setUuid(SequentialUUID.generate());

		// PK fields

		wikiPageResourceModel.setResourcePrimKey(
			wikiPageModel.getResourcePrimKey());

		// Other fields

		wikiPageResourceModel.setNodeId(wikiPageModel.getNodeId());
		wikiPageResourceModel.setTitle(wikiPageModel.getTitle());

		return wikiPageResourceModel;
	}

	public String[] nextUserName(long index) {
		String[] userName = new String[2];

		userName[0] = _firstNames.get(
			(int)(index / _lastNames.size()) % _firstNames.size());
		userName[1] = _lastNames.get((int)(index % _lastNames.size()));

		return userName;
	}

	public String toInsertSQL(BaseModel<?> baseModel) {
		try {
			StringBundler sb = new StringBundler();

			toInsertSQL(sb, baseModel);

			Class<?> clazz = baseModel.getClass();

			for (Class<?> modelClass : clazz.getInterfaces()) {
				try {
					Method method = DataFactory.class.getMethod(
						"newResourcePermissionModels", modelClass);

					for (ResourcePermissionModel resourcePermissionModel :
							(List<ResourcePermissionModel>)method.invoke(
								this, baseModel)) {

						sb.append("\n");

						toInsertSQL(sb, resourcePermissionModel);
					}
				}
				catch (NoSuchMethodException noSuchMethodException) {
				}
			}

			return sb.toString();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			return ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	public String toInsertSQL(BaseModel<?> baseModel, String className) {
		try {
			StringBundler sb = new StringBundler();

			toInsertSQL(sb, baseModel);

			Class<?> clazz = baseModel.getClass();

			for (Class<?> modelClass : clazz.getInterfaces()) {
				try {
					Class<?>[] classArg = new Class<?>[2];

					classArg[0] = modelClass;
					classArg[1] = String.class;

					Method method = DataFactory.class.getMethod(
						"newResourcePermissionModels", classArg);

					for (ResourcePermissionModel resourcePermissionModel :
							(List<ResourcePermissionModel>)method.invoke(
								this, baseModel, className)) {

						sb.append("\n");

						toInsertSQL(sb, resourcePermissionModel);
					}
				}
				catch (NoSuchMethodException noSuchMethodException) {
				}
			}

			return sb.toString();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			return ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	public String toInsertSQL(
		String mappingTableName, long companyId, long leftPrimaryKey,
		long rightPrimaryKey) {

		StringBundler sb = new StringBundler(9);

		sb.append("insert into ");
		sb.append(mappingTableName);
		sb.append(" values (");
		sb.append(companyId);
		sb.append(", ");
		sb.append(leftPrimaryKey);
		sb.append(", ");
		sb.append(rightPrimaryKey);
		sb.append(", 0, null);");

		return sb.toString();
	}

	protected GroupModel newGroupModel(
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

	protected MBCategoryModel newMBCategoryModel(long groupId, int index) {
		MBCategoryModel mbCategoryModel = new MBCategoryModelImpl();

		// UUID

		mbCategoryModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbCategoryModel.setCategoryId(counter.get());

		// Group instance

		mbCategoryModel.setGroupId(groupId);

		// Audit fields

		mbCategoryModel.setCompanyId(COMPANY_ID);
		mbCategoryModel.setUserId(SAMPLE_USER_ID);
		mbCategoryModel.setUserName(SAMPLE_USER_NAME);
		mbCategoryModel.setCreateDate(new Date());
		mbCategoryModel.setModifiedDate(new Date());

		// Other fields

		mbCategoryModel.setParentCategoryId(
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		mbCategoryModel.setName("Test Category " + index);
		mbCategoryModel.setDisplayStyle(
			MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		mbCategoryModel.setLastPublishDate(new Date());
		mbCategoryModel.setStatusDate(new Date());

		return mbCategoryModel;
	}

	protected MBMessageModel newMBMessageModel(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String urlSubject, String body) {

		MBMessageModel mBMessageModel = new MBMessageModelImpl();

		// UUID

		mBMessageModel.setUuid(SequentialUUID.generate());

		// PK fields

		mBMessageModel.setMessageId(messageId);

		// Group instance

		mBMessageModel.setGroupId(groupId);

		// Audit fields

		mBMessageModel.setCompanyId(COMPANY_ID);
		mBMessageModel.setUserId(SAMPLE_USER_ID);
		mBMessageModel.setUserName(SAMPLE_USER_NAME);
		mBMessageModel.setCreateDate(new Date());
		mBMessageModel.setModifiedDate(new Date());

		// Other fields

		mBMessageModel.setClassNameId(classNameId);
		mBMessageModel.setClassPK(classPK);
		mBMessageModel.setCategoryId(categoryId);
		mBMessageModel.setThreadId(threadId);
		mBMessageModel.setRootMessageId(rootMessageId);
		mBMessageModel.setParentMessageId(parentMessageId);
		mBMessageModel.setSubject(subject);
		mBMessageModel.setUrlSubject(urlSubject + "-" + messageId);
		mBMessageModel.setBody(body);
		mBMessageModel.setFormat(MBMessageConstants.DEFAULT_FORMAT);
		mBMessageModel.setLastPublishDate(new Date());
		mBMessageModel.setStatusDate(new Date());

		return mBMessageModel;
	}

	protected MBThreadModel newMBThreadModel(
		long threadId, long groupId, long categoryId, long rootMessageId) {

		MBThreadModel mbThreadModel = new MBThreadModelImpl();

		// UUID

		mbThreadModel.setUuid(SequentialUUID.generate());

		// PK fields

		mbThreadModel.setThreadId(threadId);

		// Group instance

		mbThreadModel.setGroupId(groupId);

		// Audit fields

		mbThreadModel.setCompanyId(COMPANY_ID);
		mbThreadModel.setUserId(SAMPLE_USER_ID);
		mbThreadModel.setUserName(SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());

		// Other fields

		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(SAMPLE_USER_ID);
		mbThreadModel.setLastPostByUserId(SAMPLE_USER_ID);
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		return mbThreadModel;
	}

	protected ReleaseModelImpl newReleaseModel(
			long releaseId, String servletContextName, String schemaVersion,
			int buildNumber, boolean verified, String testString)
		throws IOException {

		ReleaseModelImpl releaseModelImpl = new ReleaseModelImpl();

		// PK fields

		releaseModelImpl.setReleaseId(releaseId);

		// Audit fields

		releaseModelImpl.setCreateDate(new Date());
		releaseModelImpl.setModifiedDate(new Date());

		// Other fields

		releaseModelImpl.setServletContextName(servletContextName);
		releaseModelImpl.setSchemaVersion(schemaVersion);
		releaseModelImpl.setBuildNumber(buildNumber);
		releaseModelImpl.setBuildDate(new Date());
		releaseModelImpl.setVerified(verified);
		releaseModelImpl.setTestString(testString);

		return releaseModelImpl;
	}

	protected ResourcePermissionModel newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		// PK fields

		resourcePermissionModel.setResourcePermissionId(
			resourcePermissionCounter.get());

		// Audit fields

		resourcePermissionModel.setCompanyId(COMPANY_ID);

		// Other fields

		resourcePermissionModel.setName(name);
		resourcePermissionModel.setScope(ResourceConstants.SCOPE_INDIVIDUAL);
		resourcePermissionModel.setPrimKey(primKey);
		resourcePermissionModel.setPrimKeyId(GetterUtil.getLong(primKey));
		resourcePermissionModel.setRoleId(roleId);
		resourcePermissionModel.setOwnerId(ownerId);
		resourcePermissionModel.setActionIds(1);
		resourcePermissionModel.setViewActionId(true);

		return resourcePermissionModel;
	}

	protected List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, OWNER_ROLE_ID, ownerId));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, SITE_MEMBER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	protected RoleModel newRoleModel(
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

	protected SocialActivityModel newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		// PK fields

		socialActivityModel.setActivityId(socialActivityCounter.get());

		// Group instance

		socialActivityModel.setGroupId(groupId);

		// Audit fields

		socialActivityModel.setCompanyId(COMPANY_ID);
		socialActivityModel.setUserId(SAMPLE_USER_ID);
		socialActivityModel.setCreateDate(_CURRENT_TIME + _timeCounter.get());

		// Other fields

		socialActivityModel.setClassNameId(classNameId);
		socialActivityModel.setClassPK(classPK);
		socialActivityModel.setType(type);
		socialActivityModel.setExtraData(extraData);

		return socialActivityModel;
	}

	protected SubscriptionModel newSubscriptionModel(
		long classNameId, long classPK) {

		SubscriptionModel subscriptionModel = new SubscriptionModelImpl();

		// PK fields

		subscriptionModel.setSubscriptionId(counter.get());

		// Audit fields

		subscriptionModel.setCompanyId(COMPANY_ID);
		subscriptionModel.setUserId(SAMPLE_USER_ID);
		subscriptionModel.setUserName(SAMPLE_USER_NAME);
		subscriptionModel.setCreateDate(new Date());
		subscriptionModel.setModifiedDate(new Date());

		// Other fields

		subscriptionModel.setClassNameId(classNameId);
		subscriptionModel.setClassPK(classPK);
		subscriptionModel.setFrequency(SubscriptionConstants.FREQUENCY_INSTANT);

		return subscriptionModel;
	}

	protected UserModel newUserModel(
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

	protected WikiNodeModel newWikiNodeModel(long groupId, int index) {
		WikiNodeModel wikiNodeModel = new WikiNodeModelImpl();

		// UUID

		wikiNodeModel.setUuid(SequentialUUID.generate());

		// PK fields

		wikiNodeModel.setNodeId(counter.get());

		// Group instance

		wikiNodeModel.setGroupId(groupId);

		// Audit fields

		wikiNodeModel.setCompanyId(COMPANY_ID);
		wikiNodeModel.setUserId(SAMPLE_USER_ID);
		wikiNodeModel.setUserName(SAMPLE_USER_NAME);
		wikiNodeModel.setCreateDate(new Date());
		wikiNodeModel.setModifiedDate(new Date());

		// Other fields

		wikiNodeModel.setName("Test Node " + index);
		wikiNodeModel.setLastPostDate(new Date());
		wikiNodeModel.setLastPublishDate(new Date());
		wikiNodeModel.setStatusDate(new Date());

		return wikiNodeModel;
	}

	protected WikiPageModel newWikiPageModel(
		WikiNodeModel wikiNodeModel, int index) {

		WikiPageModel wikiPageModel = new WikiPageModelImpl();

		// UUID

		wikiPageModel.setUuid(SequentialUUID.generate());

		// PK fields

		wikiPageModel.setPageId(counter.get());

		// Resource

		wikiPageModel.setResourcePrimKey(counter.get());

		// Group instance

		wikiPageModel.setGroupId(wikiNodeModel.getGroupId());

		// Audit fields

		wikiPageModel.setCompanyId(COMPANY_ID);
		wikiPageModel.setUserId(SAMPLE_USER_ID);
		wikiPageModel.setUserName(SAMPLE_USER_NAME);
		wikiPageModel.setCreateDate(new Date());
		wikiPageModel.setModifiedDate(new Date());

		// Other fields

		wikiPageModel.setNodeId(wikiNodeModel.getNodeId());
		wikiPageModel.setTitle("Test Page " + index);
		wikiPageModel.setVersion(WikiPageConstants.VERSION_DEFAULT);
		wikiPageModel.setContent(
			StringBundler.concat(
				"This is Test Page ", index, " of ", wikiNodeModel.getName(),
				"."));
		wikiPageModel.setFormat("creole");
		wikiPageModel.setHead(true);
		wikiPageModel.setLastPublishDate(new Date());

		return wikiPageModel;
	}

	protected void toInsertSQL(StringBundler sb, BaseModel<?> baseModel) {
		try {
			sb.append("insert into ");

			Class<?> clazz = baseModel.getClass();

			Field tableNameField = clazz.getField("TABLE_NAME");

			sb.append(tableNameField.get(null));

			sb.append(" values (");

			Field tableColumnsField = clazz.getField("TABLE_COLUMNS");

			for (Object[] tableColumn :
					(Object[][])tableColumnsField.get(null)) {

				String name = TextFormatter.format(
					(String)tableColumn[0], TextFormatter.G);

				if (name.endsWith(StringPool.UNDERLINE)) {
					name = name.substring(0, name.length() - 1);
				}
				else if (name.equals("DeliverySubTypeSettings")) {
					name = "DeliverySubscriptionTypeSettings";
				}
				else if (name.equals("LPageTemplateStructureRelId")) {
					name = "LayoutPageTemplateStructureRelId";
				}

				int type = (int)tableColumn[1];

				if (type == Types.TIMESTAMP) {
					Method method = clazz.getMethod("get".concat(name));

					Date date = (Date)method.invoke(baseModel);

					if (date == null) {
						sb.append("null");
					}
					else {
						sb.append("'");
						sb.append(_simpleDateFormat.format(date));
						sb.append("'");
					}
				}
				else if ((type == Types.VARCHAR) || (type == Types.CLOB)) {
					Method method = clazz.getMethod("get".concat(name));

					sb.append("'");
					sb.append(method.invoke(baseModel));
					sb.append("'");
				}
				else if (type == Types.BOOLEAN) {
					Method method = clazz.getMethod("is".concat(name));

					sb.append(method.invoke(baseModel));
				}
				else {
					Method method = clazz.getMethod("get".concat(name));

					sb.append(method.invoke(baseModel));
				}

				sb.append(", ");
			}

			sb.setIndex(sb.index() - 1);

			sb.append(");");
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	private String _getResourcePermissionModelName(String... classNames) {
		if (ArrayUtil.isEmpty(classNames)) {
			return StringPool.BLANK;
		}

		Arrays.sort(classNames);

		StringBundler sb = new StringBundler(classNames.length * 2);

		for (String className : classNames) {
			sb.append(className);
			sb.append(StringPool.DASH);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static final long _CURRENT_TIME = System.currentTimeMillis();

	private final long _accountId;
	private List<String> _firstNames;
	private List<String> _lastNames;
	private final Format _simpleDateFormat;
	private final SimpleCounter _timeCounter;
	private final long _userPersonalSiteGroupId;
	private final SimpleCounter _userScreenNameCounter;

}