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
		return InitContextUtil.getDefaultJournalDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultJournalDDMStructureModel() {
		return InitContextUtil.getDefaultJournalDDMStructureModel();
	}

	public DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return InitContextUtil.getDefaultJournalDDMStructureVersionModel();
	}

	public DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return InitContextUtil.getDefaultJournalDDMTemplateModel();
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
		return InitDataFactoryUtil.getClassNameId(
			JournalArticle.class, InitContextUtil.getClassNameModels());
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		int maxJournalArticleCount =
			InitContextUtil.getMaxJournalArticleCount();

		StringBundler sb = new StringBundler(3 * maxJournalArticleCount);

		for (int i = 1; i <= maxJournalArticleCount; i++) {
			sb.append(portletPrefix);
			sb.append(i);
			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	public long getLayoutClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			Layout.class, InitContextUtil.getClassNameModels());
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
		return InitContextUtil.getMaxJournalArticleCount();
	}

	public int getMaxJournalArticlePageCount() {
		return InitContextUtil.getMaxJournalArticlePageCount();
	}

	public int getMaxJournalArticleVersionCount() {
		return InitContextUtil.getMaxJournalArticleVersionCount();
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

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(
			InitContextUtil.getCounter().get());
		ddmStorageLinkModel.setClassNameId(
			InitDataFactoryUtil.getClassNameId(
				JournalArticle.class, InitContextUtil.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
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
		
		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		ddmTemplateLinkModel.setCompanyId(InitContextUtil.getCompanyId());
		ddmTemplateLinkModel.setTemplateLinkId(
			InitContextUtil.getCounter().get());
		ddmTemplateLinkModel.setClassNameId(
			InitDataFactoryUtil.getClassNameId(
				JournalArticle.class, InitContextUtil.getClassNameModels()));
		ddmTemplateLinkModel.setClassPK(journalArticleModel.getId());
		ddmTemplateLinkModel.setTemplateId(templateId);

		return ddmTemplateLinkModel;
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

		JournalArticleLocalizationModel journalArticleLocalizationModel =
			new JournalArticleLocalizationModelImpl();

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		journalArticleLocalizationModel.setArticleLocalizationId(
			InitContextUtil.getCounter().get());
		journalArticleLocalizationModel.setCompanyId(
			journalArticleModel.getCompanyId());
		journalArticleLocalizationModel.setArticlePK(
			journalArticleModel.getId());
		journalArticleLocalizationModel.setTitle(sb.toString());
		journalArticleLocalizationModel.setLanguageId(
			journalArticleModel.getDefaultLanguageId());

		return journalArticleLocalizationModel;
	}

	public JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex)
		throws PortalException {

		JournalArticleModel journalArticleModel = new JournalArticleModelImpl();

		journalArticleModel.setUuid(SequentialUUID.generate());
		journalArticleModel.setId(InitContextUtil.getCounter().get());
		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());
		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());
		journalArticleModel.setCompanyId(InitContextUtil.getCompanyId());
		journalArticleModel.setUserId(InitContextUtil.getSampleUserId());
		journalArticleModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		journalArticleModel.setCreateDate(new Date());
		journalArticleModel.setModifiedDate(new Date());
		journalArticleModel.setClassNameId(
			JournalArticleConstants.CLASSNAME_ID_DEFAULT);
		journalArticleModel.setArticleId(
			journalArticleResourceModel.getArticleId());
		journalArticleModel.setVersion(versionIndex);

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		String urlTitle = sb.toString();

		journalArticleModel.setUrlTitle(urlTitle);

		journalArticleModel.setContent(
			InitContextUtil.getJournalArticleContent());
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDDMStructureKey(
			InitContextUtil.getDefaultJournalDDMStructureModel().
				getStructureKey());
		journalArticleModel.setDDMTemplateKey(
			InitContextUtil.getDefaultJournalDDMTemplateModel().
				getTemplateKey());
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		journalArticleModel.setReviewDate(new Date());
		journalArticleModel.setIndexable(true);
		journalArticleModel.setLastPublishDate(new Date());
		journalArticleModel.setStatusDate(new Date());

		return journalArticleModel;
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId) {

		JournalArticleResourceModel journalArticleResourceModel =
			new JournalArticleResourceModelImpl();

		journalArticleResourceModel.setUuid(SequentialUUID.generate());
		journalArticleResourceModel.setResourcePrimKey(InitContextUtil.getCounter().get());
		journalArticleResourceModel.setGroupId(groupId);
		journalArticleResourceModel.setArticleId(
			String.valueOf(InitContextUtil.getCounter().get()));

		InitContextUtil.getJournalArticleResourceUUIDs().put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		JournalContentSearchModel journalContentSearchModel =
			new JournalContentSearchModelImpl();

		journalContentSearchModel.setContentSearchId(
			InitContextUtil.getCounter().get());
		journalContentSearchModel.setGroupId(journalArticleModel.getGroupId());
		journalContentSearchModel.setCompanyId(InitContextUtil.getCompanyId());
		journalContentSearchModel.setLayoutId(layoutId);
		journalContentSearchModel.setPortletId(
			"com_liferay_journal_content_web_portlet_JournalContentPortlet");
		journalContentSearchModel.setArticleId(
			journalArticleModel.getArticleId());

		return journalContentSearchModel;
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

		LayoutFriendlyURLModel layoutFriendlyURLModel =
			new LayoutFriendlyURLModelImpl();

		layoutFriendlyURLModel.setUuid(SequentialUUID.generate());
		layoutFriendlyURLModel.setLayoutFriendlyURLId(
			InitContextUtil.getCounter().get());
		layoutFriendlyURLModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLModel.setCompanyId(InitContextUtil.getCompanyId());
		layoutFriendlyURLModel.setUserId(InitContextUtil.getSampleUserId());
		layoutFriendlyURLModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		layoutFriendlyURLModel.setCreateDate(new Date());
		layoutFriendlyURLModel.setModifiedDate(new Date());
		layoutFriendlyURLModel.setPlid(layoutModel.getPlid());
		layoutFriendlyURLModel.setFriendlyURL(layoutModel.getFriendlyURL());
		layoutFriendlyURLModel.setLanguageId("en_US");
		layoutFriendlyURLModel.setLastPublishDate(new Date());

		return layoutFriendlyURLModel;
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		Map<Long, SimpleCounter> layoutCounters =
			InitContextUtil.getLayoutCounters();

		SimpleCounter simpleCounter = layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());
		layoutModel.setPlid(InitContextUtil.getCounter().get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(InitContextUtil.getCompanyId());
		layoutModel.setUserId(InitContextUtil.getSampleUserId());
		layoutModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());
		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_PORTLET);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsProperties = new UnicodeProperties(true);

		typeSettingsProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "2_columns_ii");
		typeSettingsProperties.setProperty("column-1", column1);
		typeSettingsProperties.setProperty("column-2", column2);

		String typeSettings = StringUtil.replace(
			typeSettingsProperties.toString(), '\n', "\\n");

		layoutModel.setTypeSettings(typeSettings);
		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	public List<LayoutSetModel> newLayoutSetModels(
		long groupId, int publicLayoutSetPageCount) {

		List<LayoutSetModel> layoutSetModels = new ArrayList<>(2);

		layoutSetModels.add(newLayoutSetModel(groupId, true, 0));
		layoutSetModels.add(
			newLayoutSetModel(groupId, false, publicLayoutSetPageCount));

		return layoutSetModels;
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		int maxMBCategoryCount = InitContextUtil.getMaxMBCategoryCount();
		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			maxMBCategoryCount);

		for (int i = 1; i <= maxMBCategoryCount; i++) {
			mbCategoryModels.add(newMBCategoryModel(groupId, i));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		mbDiscussionModel.setUuid(SequentialUUID.generate());
		mbDiscussionModel.setDiscussionId(InitContextUtil.getCounter().get());
		mbDiscussionModel.setGroupId(groupId);
		mbDiscussionModel.setCompanyId(InitContextUtil.getCompanyId());
		mbDiscussionModel.setUserId(InitContextUtil.getSampleUserId());
		mbDiscussionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbDiscussionModel.setCreateDate(new Date());
		mbDiscussionModel.setModifiedDate(new Date());
		mbDiscussionModel.setClassNameId(classNameId);
		mbDiscussionModel.setClassPK(classPK);
		mbDiscussionModel.setThreadId(threadId);
		mbDiscussionModel.setLastPublishDate(new Date());

		return mbDiscussionModel;
	}

	public MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel) {

		MBMailingListModel mbMailingListModel = new MBMailingListModelImpl();

		mbMailingListModel.setUuid(SequentialUUID.generate());
		mbMailingListModel.setMailingListId(InitContextUtil.getCounter().get());
		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());
		mbMailingListModel.setCompanyId(InitContextUtil.getCompanyId());
		mbMailingListModel.setUserId(InitContextUtil.getSampleUserId());
		mbMailingListModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());
		mbMailingListModel.setCategoryId(mbCategoryModel.getCategoryId());
		mbMailingListModel.setInProtocol("pop3");
		mbMailingListModel.setInServerPort(110);
		mbMailingListModel.setInUserName(
			InitContextUtil.getSampleUserModel().getEmailAddress());
		mbMailingListModel.setInPassword(
			InitContextUtil.getSampleUserModel().getPassword());
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

		if (index == 0) {
			messageId = mbThreadModel.getRootMessageId();
			parentMessageId = MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID;
			subject = String.valueOf(classPK);
			body = String.valueOf(classPK);
		}
		else {
			messageId = InitContextUtil.getCounter().get();
			parentMessageId = mbThreadModel.getRootMessageId();
			subject = "N/A";
			body = "This is test comment " + index + ".";
		}

		return newMBMessageModel(
			mbThreadModel.getGroupId(), classNameId, classPK,
			MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			mbThreadModel.getThreadId(), messageId,
			mbThreadModel.getRootMessageId(), parentMessageId, subject, body);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		int maxMBMessageCount = InitContextUtil.getMaxMBMessageCount();

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			maxMBMessageCount);

		mbMessageModels.add(
			newMBMessageModel(
				mbThreadModel.getGroupId(), 0, 0, mbThreadModel.getCategoryId(),
				mbThreadModel.getThreadId(), mbThreadModel.getRootMessageId(),
				mbThreadModel.getRootMessageId(),
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, "Test Message 1",
				"This is test message 1."));

		for (int i = 2; i <= maxMBMessageCount; i++) {
			mbMessageModels.add(
				newMBMessageModel(
					mbThreadModel.getGroupId(), 0, 0,
					mbThreadModel.getCategoryId(), mbThreadModel.getThreadId(),
					InitContextUtil.getCounter().get(),
					mbThreadModel.getRootMessageId(),
					mbThreadModel.getRootMessageId(), "Test Message " + i,
					"This is test message " + i + "."));
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
		int maxMBThreadCount = InitContextUtil.getMaxMBThreadCount();
		int maxMBCategoryCount = InitContextUtil.getMaxMBCategoryCount();
		int maxMBMessageCount = InitContextUtil.getMaxMBMessageCount();

		MBStatsUserModel mbStatsUserModel = new MBStatsUserModelImpl();

		mbStatsUserModel.setStatsUserId(InitContextUtil.getCounter().get());
		mbStatsUserModel.setGroupId(groupId);
		mbStatsUserModel.setUserId(InitContextUtil.getSampleUserId());
		mbStatsUserModel.setMessageCount(
			maxMBCategoryCount * maxMBThreadCount * maxMBMessageCount);
		mbStatsUserModel.setLastPostDate(new Date());

		return mbStatsUserModel;
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		mbThreadFlagModel.setUuid(SequentialUUID.generate());
		mbThreadFlagModel.setThreadFlagId(InitContextUtil.getCounter().get());
		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());
		mbThreadFlagModel.setCompanyId(InitContextUtil.getCompanyId());
		mbThreadFlagModel.setUserId(InitContextUtil.getSampleUserId());
		mbThreadFlagModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbThreadFlagModel.setCreateDate(new Date());
		mbThreadFlagModel.setModifiedDate(new Date());
		mbThreadFlagModel.setThreadId(mbThreadModel.getThreadId());
		mbThreadFlagModel.setLastPublishDate(new Date());

		return mbThreadFlagModel;
	}

	public MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId, int messageCount) {

		if (messageCount == 0) {
			messageCount = 1;
		}

		return newMBThreadModel(
			threadId, groupId, MBCategoryConstants.DISCUSSION_CATEGORY_ID,
			rootMessageId, messageCount);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		List<MBThreadModel> mbThreadModels = new ArrayList<>(
			InitContextUtil.getMaxMBThreadCount());

		for (int i = 0; i < InitContextUtil.getMaxMBThreadCount(); i++) {
			mbThreadModels.add(
				newMBThreadModel(
					InitContextUtil.getCounter().get(), 
					mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(), 
					InitContextUtil.getCounter().get(),
					InitContextUtil.getMaxMBMessageCount()));
		}

		return mbThreadModels;
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
		List<LayoutModel> layoutModels = new ArrayList<>();

		layoutModels.add(
			newLayoutModel(
				groupId, "welcome", LoginPortletKeys.LOGIN + ",",
				"com_liferay_hello_world_web_portlet_HelloWorldPortlet,"));
		layoutModels.add(
			newLayoutModel(groupId, "blogs", "", BlogsPortletKeys.BLOGS + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "document_library", "",
				DLPortletKeys.DOCUMENT_LIBRARY + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "forums", "", MBPortletKeys.MESSAGE_BOARDS + ","));
		layoutModels.add(
			newLayoutModel(groupId, "wiki", "", WikiPortletKeys.WIKI + ","));

		return layoutModels;
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

	protected LayoutSetModel newLayoutSetModel(
		long groupId, boolean privateLayout, int pageCount) {

		LayoutSetModel layoutSetModel = new LayoutSetModelImpl();

		layoutSetModel.setLayoutSetId(InitContextUtil.getCounter().get());
		layoutSetModel.setGroupId(groupId);
		layoutSetModel.setCompanyId(InitContextUtil.getCompanyId());
		layoutSetModel.setCreateDate(new Date());
		layoutSetModel.setModifiedDate(new Date());
		layoutSetModel.setPrivateLayout(privateLayout);
		layoutSetModel.setThemeId("classic_WAR_classictheme");
		layoutSetModel.setColorSchemeId("01");
		layoutSetModel.setPageCount(pageCount);

		return layoutSetModel;
	}

	protected MBCategoryModel newMBCategoryModel(long groupId, int index) {
		MBCategoryModel mbCategoryModel = new MBCategoryModelImpl();

		mbCategoryModel.setUuid(SequentialUUID.generate());
		mbCategoryModel.setCategoryId(InitContextUtil.getCounter().get());
		mbCategoryModel.setGroupId(groupId);
		mbCategoryModel.setCompanyId(InitContextUtil.getCompanyId());
		mbCategoryModel.setUserId(InitContextUtil.getSampleUserId());
		mbCategoryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbCategoryModel.setCreateDate(new Date());
		mbCategoryModel.setModifiedDate(new Date());
		mbCategoryModel.setParentCategoryId(
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		mbCategoryModel.setName("Test Category " + index);
		mbCategoryModel.setDisplayStyle(
			MBCategoryConstants.DEFAULT_DISPLAY_STYLE);
		mbCategoryModel.setThreadCount(InitContextUtil.getMaxMBThreadCount());
		mbCategoryModel.setMessageCount(
			InitContextUtil.getMaxMBThreadCount() * InitContextUtil.getMaxMBMessageCount());
		mbCategoryModel.setLastPostDate(new Date());
		mbCategoryModel.setLastPublishDate(new Date());
		mbCategoryModel.setStatusDate(new Date());

		return mbCategoryModel;
	}

	protected MBMessageModel newMBMessageModel(
		long groupId, long classNameId, long classPK, long categoryId,
		long threadId, long messageId, long rootMessageId, long parentMessageId,
		String subject, String body) {

		MBMessageModel mBMessageModel = new MBMessageModelImpl();

		mBMessageModel.setUuid(SequentialUUID.generate());
		mBMessageModel.setMessageId(messageId);
		mBMessageModel.setGroupId(groupId);
		mBMessageModel.setCompanyId(InitContextUtil.getCompanyId());
		mBMessageModel.setUserId(InitContextUtil.getSampleUserId());
		mBMessageModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mBMessageModel.setCreateDate(new Date());
		mBMessageModel.setModifiedDate(new Date());
		mBMessageModel.setClassNameId(classNameId);
		mBMessageModel.setClassPK(classPK);
		mBMessageModel.setCategoryId(categoryId);
		mBMessageModel.setThreadId(threadId);
		mBMessageModel.setRootMessageId(rootMessageId);
		mBMessageModel.setParentMessageId(parentMessageId);
		mBMessageModel.setSubject(subject);
		mBMessageModel.setBody(body);
		mBMessageModel.setFormat(MBMessageConstants.DEFAULT_FORMAT);
		mBMessageModel.setLastPublishDate(new Date());
		mBMessageModel.setStatusDate(new Date());

		return mBMessageModel;
	}

	protected MBThreadModel newMBThreadModel(
		long threadId, long groupId, long categoryId, long rootMessageId,
		int messageCount) {

		MBThreadModel mbThreadModel = new MBThreadModelImpl();

		mbThreadModel.setUuid(SequentialUUID.generate());
		mbThreadModel.setThreadId(threadId);
		mbThreadModel.setGroupId(groupId);
		mbThreadModel.setCompanyId(InitContextUtil.getCompanyId());
		mbThreadModel.setUserId(InitContextUtil.getSampleUserId());
		mbThreadModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());
		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(InitContextUtil.getSampleUserId());
		mbThreadModel.setMessageCount(messageCount);
		mbThreadModel.setLastPostByUserId(InitContextUtil.getSampleUserId());
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		return mbThreadModel;
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