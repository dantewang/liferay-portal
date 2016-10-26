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
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.model.BlogsStatsUserModel;
import com.liferay.blogs.model.impl.BlogsEntryModelImpl;
import com.liferay.blogs.model.impl.BlogsStatsUserModelImpl;
import com.liferay.blogs.social.BlogsActivityKeys;
import com.liferay.blogs.web.constants.BlogsPortletKeys;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFileVersionModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.document.library.web.constants.DLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordModel;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.lists.model.DDLRecordVersionModel;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetModelImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.friendly.url.model.FriendlyURLModel;
import com.liferay.friendly.url.model.impl.FriendlyURLModelImpl;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.model.impl.JournalArticleLocalizationModelImpl;
import com.liferay.journal.model.impl.JournalArticleModelImpl;
import com.liferay.journal.model.impl.JournalArticleResourceModelImpl;
import com.liferay.journal.model.impl.JournalContentSearchModelImpl;
import com.liferay.journal.social.JournalActivityKeys;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.message.boards.kernel.model.MBCategory;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
import com.liferay.message.boards.kernel.model.MBDiscussion;
import com.liferay.message.boards.kernel.model.MBDiscussionModel;
import com.liferay.message.boards.kernel.model.MBMailingListModel;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBMessageConstants;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBStatsUserModel;
import com.liferay.message.boards.kernel.model.MBThread;
import com.liferay.message.boards.kernel.model.MBThreadFlagModel;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.message.boards.web.constants.MBPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.SubscriptionConstants;
import com.liferay.portal.kernel.model.SubscriptionModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.LayoutSetModelImpl;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.SubscriptionModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagStatsModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.portlet.messageboards.model.impl.MBCategoryModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBDiscussionModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBMailingListModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBMessageModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBStatsUserModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBThreadFlagModelImpl;
import com.liferay.portlet.messageboards.model.impl.MBThreadModelImpl;
import com.liferay.portlet.messageboards.social.MBActivityKeys;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageConstants;
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.WikiPageResourceModel;
import com.liferay.wiki.model.impl.WikiNodeModelImpl;
import com.liferay.wiki.model.impl.WikiPageModelImpl;
import com.liferay.wiki.model.impl.WikiPageResourceModelImpl;
import com.liferay.wiki.social.WikiActivityKeys;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.portlet.PortletPreferences;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory {

	public DataFactory(Properties properties) throws Exception {
		InitContextUtil.initContext(properties);
		InitContextUtil.initParameter();
		InitContextUtil.initResource(_clazz);
		InitContextUtil.initCompanyModels();
		InitContextUtil.initUserNames(_clazz);
		InitContextUtil.initGroupModels();
		InitContextUtil.initUserModels(DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initAssetCategoryModels(
			DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initAssetTagModels(
			DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initDLFileEntryTypeModel(
			DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initRoleModels(DataFactoryConstants.SAMPLE_USER_NAME);
	}

	public AccountModel getAccountModel() {
		return InitContextUtil.getAccountModel();
	}

	public RoleModel getAdministratorRoleModel() {
		return InitContextUtil.getAdministratorRoleModel();
	}

	public List<Long> getAssetCategoryIds(long groupId) {
		return AssetDataFactory.getAssetCategoryIds(groupId);
	}

	public List<AssetCategoryModel> getAssetCategoryModels() {
		return AssetDataFactory.getAssetCategoryModels();
	}

	public List<Long> getAssetTagIds(long groupId) {
		return AssetDataFactory.getAssetTagIds(groupId);
	}

	public List<AssetTagModel> getAssetTagModels() {
		return AssetDataFactory.getAssetTagModels();
	}

	public List<AssetTagStatsModel> getAssetTagStatsModels() {
		return AssetDataFactory.getAssetTagStatsModels();
	}

	public List<AssetVocabularyModel> getAssetVocabularyModels() {
		return AssetDataFactory.getAssetVocabularyModels();
	}

	public long getBlogsEntryClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			BlogsEntry.class, InitContextUtil.getClassNameModels());
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return InitDataFactoryUtil.getClassNameModels();
	}

	public CompanyModel getCompanyModel() {
		return InitContextUtil.getCompanyModel();
	}

	public SimpleCounter getCounter() {
		return InitContextUtil.getCounter();
	}

	public long getCounterNext() {
		return InitContextUtil.getCounter().get();
	}

	public String getDateLong(Date date) {
		return InitDataFactoryUtil.getDateLong(date);
	}

	public String getDateString(Date date) {

		return InitDataFactoryUtil.getDateString(date);
	}

	public long getDDLRecordSetClassNameId() {

		return DDLDataFactory.getDDLRecordSetClassNameId();
	}

	public long getDefaultDLDDMStructureId() {
		return DDLDataFactory.getDefaultDLDDMStructureId();
	}

	public DDMStructureLayoutModel getDefaultDLDDMStructureLayoutModel() {
		return DDLDataFactory.getDefaultDLDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return DDLDataFactory.getDefaultDLDDMStructureModel();
	}

	public DDMStructureVersionModel getDefaultDLDDMStructureVersionModel() {
		return DDLDataFactory.getDefaultDLDDMStructureVersionModel();
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return DLDataFactory.getDefaultDLFileEntryTypeModel();
	}

	public DDMStructureLayoutModel getDefaultJournalDDMStructureLayoutModel() {
		return JournalDataFactory.getDefaultJournalDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultJournalDDMStructureModel() {
		return JournalDataFactory.getDefaultJournalDDMStructureModel();
	}

	public DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return JournalDataFactory.getDefaultJournalDDMStructureVersionModel();
	}

	public DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return JournalDataFactory.getDefaultJournalDDMTemplateModel();
	}

	public UserModel getDefaultUserModel() {
		return InitContextUtil.getDefaultUserModel();
	}

	public long getDLFileEntryClassNameId() {
		return DLDataFactory.getDLFileEntryClassNameId();
	}

	public GroupModel getGlobalGroupModel() {
		return InitContextUtil.getGlobalGroupModel();
	}

	public List<GroupModel> getGroupModels() {
		return InitContextUtil.getGroupModels();
	}

	public GroupModel getGuestGroupModel() {
		return InitContextUtil.getGuestGroupModel();
	}

	public UserModel getGuestUserModel() {
		return InitContextUtil.getGuestUserModel();
	}

	public long getJournalArticleClassNameId() {
		return JournalDataFactory.getJournalArticleClassNameId();
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		return JournalDataFactory.getJournalArticleLayoutColumn(portletPrefix);
	}

	public long getLayoutClassNameId() {
		return LayoutDataFactory.getLayoutClassNameId();
	}

	public int getMaxAssetPublisherPageCount() {
		return AssetDataFactory.getMaxAssetPublisherPageCount();
	}

	public int getMaxBlogsEntryCommentCount() {
		return InitContextUtil.getMaxBlogsEntryCommentCount();
	}

	public int getMaxDDLRecordCount() {
		return DDLDataFactory.getMaxDDLRecordCount();
	}

	public int getMaxDDLRecordSetCount() {
		return DDLDataFactory.getMaxDDLRecordSetCount();
	}

	public int getMaxDLFolderDepth() {
		return DLDataFactory.getMaxDLFolderDepth();
	}

	public int getMaxGroupCount() {
		return InitContextUtil.getMaxGroupsCount();
	}

	public int getMaxJournalArticleCount() {
		return JournalDataFactory.getMaxJournalArticleCount();
	}

	public int getMaxJournalArticlePageCount() {
		return JournalDataFactory.getMaxJournalArticlePageCount();
	}

	public int getMaxJournalArticleVersionCount() {
		return JournalDataFactory.getMaxJournalArticleVersionCount();
	}

	public int getMaxWikiPageCommentCount() {
		return InitContextUtil.getMaxWikiPageCommentCount();
	}

	public List<Long> getNewUserGroupIds(long groupId) {
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

	public RoleModel getPowerUserRoleModel() {
		return InitContextUtil.getPowerUserRoleModel();
	}

	public List<RoleModel> getRoleModels() {
		return InitContextUtil.getRoleModels();
	}

	public UserModel getSampleUserModel() {
		return InitContextUtil.getSampleUserModel();
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public RoleModel getUserRoleModel() {
		return InitContextUtil.getUserRoleModel();
	}

	public VirtualHostModel getVirtualHostModel() {
		return InitContextUtil.getVirtualHostModel();
	}

	public long getWikiPageClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			WikiPage.class, InitContextUtil.getClassNameModels());
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return AssetDataFactory.newAssetEntryModel(blogsEntryModel);
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return AssetDataFactory.newAssetEntryModel(dLFileEntryModel);
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return AssetDataFactory.newAssetEntryModel(dLFolderModel);
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		return AssetDataFactory.newAssetEntryModel(mbMessageModel);
	}

	public AssetEntryModel newAssetEntryModel(MBThreadModel mbThreadModel) {
		return AssetDataFactory.newAssetEntryModel(mbThreadModel);
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		Map<Long, String> journalArticleResourceUUIDs =
			InitContextUtil.getJournalArticleResourceUUIDs();

		return AssetDataFactory.newAssetEntryModel(
			objectValuePair, journalArticleResourceUUIDs);
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return AssetDataFactory.newAssetEntryModel(wikiPageModel);
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, BlogsPortletKeys.BLOGS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, WikiPortletKeys.WIKI,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		int maxBlogsEntryCount = InitContextUtil.getMaxBlogsEntryCount();

		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			maxBlogsEntryCount);

		for (int i = 1; i <= maxBlogsEntryCount; i++) {
			blogEntryModels.add(newBlogsEntryModel(groupId, i));
		}

		return blogEntryModels;
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		BlogsStatsUserModel blogsStatsUserModel = new BlogsStatsUserModelImpl();

		blogsStatsUserModel.setStatsUserId(InitContextUtil.getCounter().get());
		blogsStatsUserModel.setGroupId(groupId);
		blogsStatsUserModel.setCompanyId(InitContextUtil.getCompanyId());
		blogsStatsUserModel.setUserId(InitContextUtil.getSampleUserId());
		blogsStatsUserModel.setEntryCount(
			InitContextUtil.getMaxBlogsEntryCount());
		blogsStatsUserModel.setLastPostDate(new Date());

		return blogsStatsUserModel;
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

	public List<CounterModel> newCounterModels() {
		List<CounterModel> counterModels = new ArrayList<>();

		// Counter

		CounterModel counterModel = new CounterModelImpl();

		counterModel.setName(Counter.class.getName());
		counterModel.setCurrentId(InitContextUtil.getCounter().get());

		counterModels.add(counterModel);

		// ResourcePermission

		counterModel = new CounterModelImpl();

		counterModel.setName(ResourcePermission.class.getName());
		counterModel.setCurrentId(InitContextUtil.getResourcePermissionCounter().get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(InitContextUtil.getSocialActivityCounter().get());

		counterModels.add(counterModel);

		return counterModels;
	}

	public DDMStructureLayoutModel newDDLDDMStructureLayoutModel(
		long groupId, DDMStructureVersionModel ddmStructureVersionModel) {

		return DDLDataFactory.newDDLDDMStructureLayoutModel(
			groupId, ddmStructureVersionModel);
	}

	public DDMStructureModel newDDLDDMStructureModel(long groupId) {
		return DDLDataFactory.newDDLDDMStructureModel(groupId);
	}

	public List<PortletPreferencesModel>
		newDDLPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public DDLRecordModel newDDLRecordModel(
		DDLRecordSetModel dDLRecordSetModel) {

		return DDLDataFactory.newDDLRecordModel(dDLRecordSetModel);
	}

	public DDLRecordSetModel newDDLRecordSetModel(
		DDMStructureModel ddmStructureModel, int currentIndex) {

		return DDLDataFactory.newDDLRecordSetModel(
			ddmStructureModel, currentIndex);
	}

	public DDLRecordVersionModel newDDLRecordVersionModel(
		DDLRecordModel dDLRecordModel) {

		return DDLDataFactory.newDDLRecordVersionModel(dDLRecordModel);
	}

	public DDMContentModel newDDMContentModel(
		DDLRecordModel ddlRecordModel, int currentIndex) {

		return DDLDataFactory.newDDMContentModel(ddlRecordModel, currentIndex);
	}

	public DDMContentModel newDDMContentModel(
		DLFileEntryModel dlFileEntryModel) {

		return DLDataFactory.newDDMContentModel(dlFileEntryModel);
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		return JournalDataFactory.newDDMStorageLinkModel(
			journalArticleModel, structureId);
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		long ddmStorageLinkId, DDMContentModel ddmContentModel,
		long structureId) {

		return DDLDataFactory.newDDMStorageLinkModel(
			ddmStorageLinkId, ddmContentModel, structureId);
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DDLRecordSetModel ddlRecordSetModel) {

		return DDLDataFactory.newDDMStructureLinkModel(ddlRecordSetModel);
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DLFileEntryMetadataModel dLFileEntryMetadataModel) {

		return DLDataFactory.newDDMStructureLinkModel(dLFileEntryMetadataModel);
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		return InitDataFactoryUtil.newDDMStructureVersionModel(
			ddmStructureModel, DataFactoryConstants.SAMPLE_USER_NAME);
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		return JournalDataFactory.newDDMTemplateLinkModel(
			journalArticleModel, templateId);
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		return DLDataFactory.newDLFileEntryMetadataModel(
			ddmStorageLinkId, ddmStructureId, dlFileVersionModel);
	}

	public List<DLFileEntryModel> newDlFileEntryModels(
		DLFolderModel dlFolerModel) {

		return DLDataFactory.newDlFileEntryModels(dlFolerModel);
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		return DLDataFactory.newDLFileVersionModel(dlFileEntryModel);
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		return DLDataFactory.newDLFolderModels(groupId, parentFolderId);
	}

	public FriendlyURLModel newFriendlyURLModel(
		BlogsEntryModel blogsEntryModel) {

		FriendlyURLModel friendlyURLModel = new FriendlyURLModelImpl();

		friendlyURLModel.setUuid(SequentialUUID.generate());
		friendlyURLModel.setFriendlyURLId(InitContextUtil.getCounter().get());
		friendlyURLModel.setGroupId(blogsEntryModel.getGroupId());
		friendlyURLModel.setCompanyId(InitContextUtil.getCompanyId());
		friendlyURLModel.setCreateDate(new Date());
		friendlyURLModel.setModifiedDate(new Date());
		friendlyURLModel.setClassNameId(InitDataFactoryUtil.getClassNameId(
				BlogsEntry.class, InitContextUtil.getClassNameModels()));
		friendlyURLModel.setClassPK(blogsEntryModel.getEntryId());
		friendlyURLModel.setUrlTitle(blogsEntryModel.getUrlTitle());
		friendlyURLModel.setMain(true);

		return friendlyURLModel;
	}

	public GroupModel newGroupModel(UserModel userModel) throws Exception {
		return InitDataFactoryUtil.newGroupModel(
			InitContextUtil.getCounter().get(),
			InitDataFactoryUtil.getClassNameId(
				User.class, InitContextUtil.getClassNameModels()),
			userModel.getUserId(), userModel.getScreenName(), false,
			InitContextUtil.getCompanyId(), InitContextUtil.getSampleUserId());
	}

	public IntegerWrapper newInteger() {
		return new IntegerWrapper();
	}

	public JournalArticleLocalizationModel newJournalArticleLocalizationModel(
		JournalArticleModel journalArticleModel, int articleIndex,
		int versionIndex) {

		return JournalDataFactory.newJournalArticleLocalizationModel(
			journalArticleModel, articleIndex, versionIndex);
	}

	public JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex)
		throws PortalException {

		return JournalDataFactory.newJournalArticleModel(
			journalArticleResourceModel, articleIndex, versionIndex);
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId) {

		Map<Long, String> journalArticleResourceUUIDs =
			InitContextUtil.getJournalArticleResourceUUIDs();

		return JournalDataFactory.newJournalArticleResourceModel(
			groupId, journalArticleResourceUUIDs);
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		return JournalDataFactory.newJournalContentSearchModel(
			journalArticleModel, layoutId);
	}

	public List<PortletPreferencesModel>
		newJournalPortletPreferencesModels(long plid) {

		return Collections.singletonList(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		return LayoutDataFactory.newLayoutFriendlyURLModel(layoutModel);
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		return LayoutDataFactory.newLayoutModel(
			groupId, name, column1, column2);
	}

	public List<LayoutSetModel> newLayoutSetModels(
		long groupId, int publicLayoutSetPageCount) {

		return LayoutDataFactory.newLayoutSetModels(
			groupId, publicLayoutSetPageCount);
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		return MessageBoardDataFactory.newMBCategoryModels(groupId);
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		return MessageBoardDataFactory.newMBDiscussionModel(
			groupId, classNameId, classPK, threadId);
	}

	public MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel) {

		return MessageBoardDataFactory.newMBMailingListModel(mbCategoryModel);
	}

	public MBMessageModel newMBMessageModel(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int index) {

		return MessageBoardDataFactory.newMBMessageModel(
			mbThreadModel, classNameId, classPK, index);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		return MessageBoardDataFactory.newMBMessageModels(mbThreadModel);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int maxMessageCount) {

		return MessageBoardDataFactory.newMBMessageModels(
			mbThreadModel, classNameId, classPK, maxMessageCount);
	}

	public MBStatsUserModel newMBStatsUserModel(long groupId) {

		return MessageBoardDataFactory.newMBStatsUserModel(groupId);
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {

		return MessageBoardDataFactory.newMBThreadFlagModel(mbThreadModel);
	}

	public MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId, int messageCount) {

		return MessageBoardDataFactory.newMBThreadModel(
			threadId, groupId, rootMessageId, messageCount);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		return MessageBoardDataFactory.newMBThreadModels(mbCategoryModel);
	}

	public <K, V> ObjectValuePair<K, V> newObjectValuePair(K key, V value) {
		return new ObjectValuePair<>(key, value);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {
		int size = (int)groupId - 1;

		if (currentIndex == 1) {
			return newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		Map<Long, SimpleCounter> assetPublisherQueryCounter =
			InitContextUtil.getAssetPublisherQueryCounter();

		SimpleCounter counter = assetPublisherQueryCounter.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			assetPublisherQueryCounter.put(groupId, counter);
		}

		String[] assetPublisherQueryValues = null;

		if (InitContextUtil.getAssetPublisherQueryName().equals(
				"assetCategories")) {

			List<AssetCategoryModel> assetCategoryModels =
				InitContextUtil.getAssetCategoryModelsArray()[size];

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			int maxAssetEntryToAssetCategoryCount =
				InitContextUtil.getMaxAssetEntryToAssetCategoryCount();
			assetPublisherQueryValues =
				getAssetPublisherAssetCategoriesQueryValues(
					assetCategoryModels, (int)counter.get());
		}
		else {
			List<AssetTagModel> assetTagModels =
				InitContextUtil.getAssetTagModelsArray()[size];

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			assetPublisherQueryValues = getAssetPublisherAssetTagsQueryValues(
				assetTagModels, (int)counter.get());
		}

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)InitContextUtil.
				getDefaultAssetPublisherPortletPreference().clone();
		PortletPreferencesFactory portletPreferencesFactory =
			InitContextUtil.getPortletPreferencesFactory();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue(
			"queryName0", InitContextUtil.getAssetPublisherQueryName());
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue(
			"queryName1", InitContextUtil.getAssetPublisherQueryName());
		jxPortletPreferences.setValue(
			"queryValues1", assetPublisherQueryValues[3]);

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();
		PortletPreferencesFactory portletPreferencesFactory =
			InitContextUtil.getPortletPreferencesFactory();

		jxPortletPreferences.setValue("editable", "true");
		jxPortletPreferences.setValue(
			"recordSetId", String.valueOf(ddlRecordSetModel.getRecordSetId()));
		jxPortletPreferences.setValue("spreadsheet", "false");

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		PortletPreferencesFactory portletPreferencesFactory =
			InitContextUtil.getPortletPreferencesFactory();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public List<LayoutModel> newPublicLayoutModels(long groupId) {

		return LayoutDataFactory.newPublicLayoutModels(groupId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		return newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()), InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetTagModel assetTagModel) {

		return newResourcePermissionModels(
			AssetTag.class.getName(), String.valueOf(assetTagModel.getTagId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() == InitContextUtil.getDefaultUserId()) {
			return Collections.singletonList(
				newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					InitContextUtil.getOwnerRoleModel().getRoleId(), InitContextUtil.getDefaultUserId()));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()), InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(), InitContextUtil.getDefaultUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMStructure.class.getName(),
			getClassName(ddmStructureModel.getClassNameId()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getGuestRoleModel().getRoleId(), 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getOwnerRoleModel().getRoleId(),
				ddmStructureModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getUserRoleModel().getRoleId(), 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMTemplate.class.getName(),
			getClassName(ddmTemplateModel.getResourceClassNameId()));
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getGuestRoleModel().getRoleId(), 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getOwnerRoleModel().getRoleId(),
				ddmTemplateModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getUserRoleModel().getRoleId(), 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()), InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()), InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(), InitContextUtil.getSampleUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			InitContextUtil.getSampleUserId());
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
			String.valueOf(mbCategoryModel.getCategoryId()), InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				MBMessage.class.getName(),
				String.valueOf(mbMessageModel.getMessageId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(), InitContextUtil.getSampleUserId()));
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
				InitContextUtil.getOwnerRoleModel().getRoleId(), InitContextUtil.getSampleUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey), InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(), userModel.getUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		return newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		return newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()), InitContextUtil.getSampleUserId());
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel) {

		return newSocialActivityModel(
			blogsEntryModel.getGroupId(), InitDataFactoryUtil.getClassNameId(BlogsEntry.class, InitContextUtil.getClassNameModels()),
			blogsEntryModel.getEntryId(), BlogsActivityKeys.ADD_ENTRY,
			"{\"title\":\"" + blogsEntryModel.getTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel) {

		return newSocialActivityModel(
			dlFileEntryModel.getGroupId(), InitDataFactoryUtil.getClassNameId(DLFileEntry.class, InitContextUtil.getClassNameModels()),
			dlFileEntryModel.getFileEntryId(), DLActivityKeys.ADD_FILE_ENTRY,
			StringPool.BLANK);
	}

	public SocialActivityModel newSocialActivityModel(
		JournalArticleModel journalArticleModel) {

		int type = JournalActivityKeys.UPDATE_ARTICLE;

		if (journalArticleModel.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT) {

			type = JournalActivityKeys.ADD_ARTICLE;
		}

		return newSocialActivityModel(
			journalArticleModel.getGroupId(),
			InitDataFactoryUtil.getClassNameId(JournalArticle.class, InitContextUtil.getClassNameModels()),
			journalArticleModel.getResourcePrimKey(), type,
			"{\"title\":\"" + journalArticleModel.getUrlTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		MBMessageModel mbMessageModel) {

		long classNameId = mbMessageModel.getClassNameId();
		long classPK = mbMessageModel.getClassPK();

		int type = 0;
		String extraData = null;

		if (classNameId == InitDataFactoryUtil.getClassNameId(WikiPage.class, InitContextUtil.getClassNameModels())) {
			extraData = "{\"version\":1}";

			type = WikiActivityKeys.ADD_PAGE;
		}
		else if (classNameId == 0) {
			extraData = "{\"title\":\"" + mbMessageModel.getSubject() + "\"}";

			type = MBActivityKeys.ADD_MESSAGE;

			classNameId = InitDataFactoryUtil.getClassNameId(MBMessage.class, InitContextUtil.getClassNameModels());
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
		BlogsEntryModel blogsEntryModel) {

		return newSubscriptionModel(
			InitDataFactoryUtil.getClassNameId(
				BlogsEntry.class, InitContextUtil.getClassNameModels()),
			blogsEntryModel.getEntryId());
	}

	public SubscriptionModel newSubscriptionModel(MBThreadModel mBThreadModel) {
		return newSubscriptionModel(
			InitDataFactoryUtil.getClassNameId(
				MBThread.class, InitContextUtil.getClassNameModels()),
			mBThreadModel.getThreadId());
	}

	public SubscriptionModel newSubscriptionModel(WikiPageModel wikiPageModel) {
		return newSubscriptionModel(
			InitDataFactoryUtil.getClassNameId(
				WikiPage.class, InitContextUtil.getClassNameModels()),
			wikiPageModel.getResourcePrimKey());
	}

	public List<UserModel> newUserModels() {
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

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		int maxWikiNodeCount = InitContextUtil.getMaxWikiNodeCount();

		List<WikiNodeModel> wikiNodeModels = new ArrayList<>(maxWikiNodeCount);

		for (int i = 1; i <= maxWikiNodeCount; i++) {
			wikiNodeModels.add(
				InitDataFactoryUtil.newWikiNodeModel(
					groupId, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME));
		}

		return wikiNodeModels;
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel) {
		int maxWikiPageCount = InitContextUtil.getMaxWikiPageCount();

		List<WikiPageModel> wikiPageModels = new ArrayList<>(maxWikiPageCount);

		for (int i = 1; i <= maxWikiPageCount; i++) {
			wikiPageModels.add(newWikiPageModel(wikiNodeModel, i));
		}

		return wikiPageModels;
	}

	public WikiPageResourceModel newWikiPageResourceModel(
		WikiPageModel wikiPageModel) {

		WikiPageResourceModel wikiPageResourceModel =
			new WikiPageResourceModelImpl();

		wikiPageResourceModel.setUuid(SequentialUUID.generate());
		wikiPageResourceModel.setResourcePrimKey(
			wikiPageModel.getResourcePrimKey());
		wikiPageResourceModel.setNodeId(wikiPageModel.getNodeId());
		wikiPageResourceModel.setTitle(wikiPageModel.getTitle());

		return wikiPageResourceModel;
	}

	protected String[] getAssetPublisherAssetCategoriesQueryValues(
		List<AssetCategoryModel> assetCategoryModels, int index) {

		AssetCategoryModel assetCategoryModel0 = assetCategoryModels.get(
			index % assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel1 = assetCategoryModels.get(
			(index + InitContextUtil.getMaxAssetEntryToAssetCategoryCount()) %
				assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel2 = assetCategoryModels.get(
			(index + InitContextUtil.getMaxAssetEntryToAssetCategoryCount() * 2) %
				assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel3 = assetCategoryModels.get(
			(index + InitContextUtil.getMaxAssetEntryToAssetCategoryCount() * 3) %
				assetCategoryModels.size());

		return new String[] {
			String.valueOf(assetCategoryModel0.getCategoryId()),
			String.valueOf(assetCategoryModel1.getCategoryId()),
			String.valueOf(assetCategoryModel2.getCategoryId()),
			String.valueOf(assetCategoryModel3.getCategoryId())
		};
	}

	protected String[] getAssetPublisherAssetTagsQueryValues(
		List<AssetTagModel> assetTagModels, int index) {

		AssetTagModel assetTagModel0 = assetTagModels.get(
			index % assetTagModels.size());
		AssetTagModel assetTagModel1 = assetTagModels.get(
			(index + InitContextUtil.getMaxAssetEntryToAssetTagCount()) % assetTagModels.size());
		AssetTagModel assetTagModel2 = assetTagModels.get(
			(index + InitContextUtil.getMaxAssetEntryToAssetTagCount() * 2) %
				assetTagModels.size());
		AssetTagModel assetTagModel3 = assetTagModels.get(
			(index + InitContextUtil.getMaxAssetEntryToAssetTagCount() * 3) %
				assetTagModels.size());

		return new String[] {
			assetTagModel0.getName(), assetTagModel1.getName(),
			assetTagModel2.getName(), assetTagModel3.getName()
		};
	}

	protected String getClassName(long classNameId) {
		for (ClassNameModel classNameModel : InitContextUtil.getClassNameModels().values()) {
			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	protected BlogsEntryModel newBlogsEntryModel(long groupId, int index) {
		BlogsEntryModel blogsEntryModel = new BlogsEntryModelImpl();

		blogsEntryModel.setUuid(SequentialUUID.generate());
		blogsEntryModel.setEntryId(InitContextUtil.getCounter().get());
		blogsEntryModel.setGroupId(groupId);
		blogsEntryModel.setCompanyId(InitContextUtil.getCompanyId());
		blogsEntryModel.setUserId(InitContextUtil.getSampleUserId());
		blogsEntryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		blogsEntryModel.setCreateDate(new Date());
		blogsEntryModel.setModifiedDate(new Date());
		blogsEntryModel.setTitle("Test Blog " + index);
		blogsEntryModel.setSubtitle("Subtitle of Test Blog " + index);
		blogsEntryModel.setUrlTitle("testblog" + index);
		blogsEntryModel.setContent("This is test blog " + index + ".");
		blogsEntryModel.setDisplayDate(new Date());
		blogsEntryModel.setLastPublishDate(new Date());
		blogsEntryModel.setStatusByUserId(InitContextUtil.getSampleUserId());
		blogsEntryModel.setStatusDate(new Date());

		return blogsEntryModel;
	}

	protected PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId, String preferences) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		portletPreferencesModel.setPortletPreferencesId(
			InitContextUtil.getCounter().get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	protected ResourcePermissionModel newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		resourcePermissionModel.setResourcePermissionId(
			InitContextUtil.getResourcePermissionCounter().get());
		resourcePermissionModel.setCompanyId(InitContextUtil.getCompanyId());
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
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getGuestRoleModel().getRoleId(), 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getOwnerRoleModel().getRoleId(), ownerId));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getSiteMemberRoleModel().getRoleId(), 0));

		return resourcePermissionModels;
	}

	protected SocialActivityModel newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		socialActivityModel.setActivityId(InitContextUtil.getSocialActivityCounter().get());
		socialActivityModel.setGroupId(groupId);
		socialActivityModel.setCompanyId(InitContextUtil.getCompanyId());
		socialActivityModel.setUserId(InitContextUtil.getSampleUserId());
		socialActivityModel.setCreateDate(_CURRENT_TIME + InitContextUtil.getTimeCounter().get());
		socialActivityModel.setClassNameId(classNameId);
		socialActivityModel.setClassPK(classPK);
		socialActivityModel.setType(type);
		socialActivityModel.setExtraData(extraData);

		return socialActivityModel;
	}

	protected SubscriptionModel newSubscriptionModel(
		long classNameId, long classPK) {

		SubscriptionModel subscriptionModel = new SubscriptionModelImpl();

		subscriptionModel.setSubscriptionId(InitContextUtil.getCounter().get());
		subscriptionModel.setCompanyId(InitContextUtil.getCompanyId());
		subscriptionModel.setUserId(InitContextUtil.getSampleUserId());
		subscriptionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		subscriptionModel.setCreateDate(new Date());
		subscriptionModel.setModifiedDate(new Date());
		subscriptionModel.setClassNameId(classNameId);
		subscriptionModel.setClassPK(classPK);
		subscriptionModel.setFrequency(SubscriptionConstants.FREQUENCY_INSTANT);

		return subscriptionModel;
	}

	protected WikiPageModel newWikiPageModel(
		WikiNodeModel wikiNodeModel, int index) {

		WikiPageModel wikiPageModel = new WikiPageModelImpl();

		wikiPageModel.setUuid(SequentialUUID.generate());
		wikiPageModel.setPageId(InitContextUtil.getCounter().get());
		wikiPageModel.setResourcePrimKey(InitContextUtil.getCounter().get());
		wikiPageModel.setGroupId(wikiNodeModel.getGroupId());
		wikiPageModel.setCompanyId(InitContextUtil.getCompanyId());
		wikiPageModel.setUserId(InitContextUtil.getSampleUserId());
		wikiPageModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		wikiPageModel.setCreateDate(new Date());
		wikiPageModel.setModifiedDate(new Date());
		wikiPageModel.setNodeId(wikiNodeModel.getNodeId());
		wikiPageModel.setTitle("Test Page " + index);
		wikiPageModel.setVersion(WikiPageConstants.VERSION_DEFAULT);
		wikiPageModel.setContent("This is test page " + index + ".");
		wikiPageModel.setFormat("creole");
		wikiPageModel.setHead(true);
		wikiPageModel.setLastPublishDate(new Date());

		return wikiPageModel;
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
	private final Class<?> _clazz = getClass();
}