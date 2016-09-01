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
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.model.BlogsEntryModel;
import com.liferay.blogs.kernel.model.BlogsStatsUserModel;
import com.liferay.blogs.web.constants.BlogsPortletKeys;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
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
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.social.JournalActivityKeys;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.message.boards.kernel.model.MBCategory;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
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
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.Role;
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
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.SubscriptionModelImpl;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.blogs.model.impl.BlogsStatsUserModelImpl;
import com.liferay.portlet.blogs.social.BlogsActivityKeys;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
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
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.WikiPageResourceModel;
import com.liferay.wiki.model.impl.WikiPageResourceModelImpl;
import com.liferay.wiki.social.WikiActivityKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.portlet.PortletPreferences;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory {

	public DataFactory(Properties properties) throws Exception {
		InitContextUtil.initContext(properties);
		InitContextUtil.initParameter();
		InitContextUtil.initResource(_clazz, _portletPreferencesFactory);
		InitContextUtil.initCompanyModels();
		InitContextUtil.initUserNames(_clazz);
		InitContextUtil.initGroupModels();
		InitContextUtil.initUserModels(DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initAssetCategoryModels(DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initAssetTagModels(DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initDLFileEntryTypeModel(DataFactoryConstants.SAMPLE_USER_NAME);
		InitContextUtil.initRoleModels(DataFactoryConstants.SAMPLE_USER_NAME);
	}

	public AccountModel getAccountModel() {
		return InitContextUtil.getAccountModel();
	}

	public RoleModel getAdministratorRoleModel() {
		return InitContextUtil.getAdministratorRoleModel();
	}

	public List<Long> getAssetCategoryIds(long groupId) {
		int maxAssetEntryToAssetCategoryCount =
			InitContextUtil.getMaxAssetEntryToAssetCategoryCount();

		SimpleCounter counter = _assetCategoryCounters.get(groupId);

		int size = (int)groupId - 1;

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetCategoryCounters.put(groupId, counter);
		}

		List<AssetCategoryModel> assetCategoryModels =
			InitContextUtil.getAssetCategoryModelsArray()[size];

		if ((assetCategoryModels == null) || assetCategoryModels.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> assetCategoryIds = new ArrayList<>(
			maxAssetEntryToAssetCategoryCount);

		for (int i = 0; i < maxAssetEntryToAssetCategoryCount; i++) {
			int index = (int)counter.get() % assetCategoryModels.size();

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index);

			assetCategoryIds.add(assetCategoryModel.getCategoryId());
		}

		return assetCategoryIds;
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
		return InitContextUtil.getClassNameModels().values();
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
		return String.valueOf(date.getTime());
	}

	public String getDateString(Date date) {
		if (date == null) {
			return null;
		}

		return InitContextUtil.getSimpleDateFormat().format(date);
	}

	public long getDDLRecordSetClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			DDLRecordSet.class, InitContextUtil.getClassNameModels());
	}

	public long getDefaultDLDDMStructureId() {
		return InitContextUtil.getDefaultDLDDMStructureModel().
			getStructureId();
	}

	public DDMStructureLayoutModel getDefaultDLDDMStructureLayoutModel() {
		return InitContextUtil.getDefaultDLDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return InitContextUtil.getDefaultDLDDMStructureModel();
	}

	public DDMStructureVersionModel getDefaultDLDDMStructureVersionModel()
	{
		return InitContextUtil.getDefaultDLDDMStructureVersionModel();
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return InitContextUtil.getDefaultDLFileEntryTypeModel();
	}

	public DDMStructureLayoutModel getDefaultJournalDDMStructureLayoutModel()
	{
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
		return InitDataFactoryUtil.getClassNameId(
			DLFileEntry.class, InitContextUtil.getClassNameModels());
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
		return InitContextUtil.getMaxDDLRecordCount();
	}

	public int getMaxDDLRecordSetCount() {
		return InitContextUtil.getMaxDDLRecordSetCount();
	}

	public int getMaxDLFolderDepth() {
		return InitContextUtil.getMaxDLFolderDepth();
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
		int maxUserToGroupCount =
			InitContextUtil.getMaxUserToGroupCount();
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

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel)
	{
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

		return AssetDataFactory.newAssetEntryModel(
			objectValuePair, _journalArticleResourceUUIDs);
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return AssetDataFactory.newAssetEntryModel(wikiPageModel);
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		return AssetDataFactory.newAssetPublisherPortletPreferencesModels(plid);
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		int maxBlogsEntryCount = InitContextUtil.getMaxBlogsEntryCount();

		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			maxBlogsEntryCount);

		for (int i = 1; i <= maxBlogsEntryCount; i++) {
			blogEntryModels.add(
				InitDataFactoryUtil.newBlogsEntryModel(
					groupId, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME));
		}

		return blogEntryModels;
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		BlogsStatsUserModel blogsStatsUserModel = new BlogsStatsUserModelImpl();

		blogsStatsUserModel.setStatsUserId(
			InitContextUtil.getCounter().get());
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
		counterModel.setCurrentId(
			InitContextUtil.getResourcePermissionCounter().get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(
			InitContextUtil.getSocialActivityCounter().get());

		counterModels.add(counterModel);

		return counterModels;
	}

	public DDMStructureLayoutModel newDDLDDMStructureLayoutModel(
		long groupId, DDMStructureVersionModel ddmStructureVersionModel) {

		int maxDDLCustomFieldCount =
			InitContextUtil.getMaxDDLCustomFieldCount();
		StringBundler sb = new StringBundler(4 + maxDDLCustomFieldCount * 4);

		sb.append("{\"defaultLanguageId\": \"en_US\", \"pages\": [{\"rows\": ");
		sb.append("[");

		for (int i = 0; i < maxDDLCustomFieldCount; i++) {
			sb.append("{\"columns\": [{\"fieldNames\": [\"");
			sb.append(InitDataFactoryUtil.nextDDLCustomFieldName(groupId, i));
			sb.append("\"], \"size\": 12}]}");
			sb.append(", ");
		}

		if (maxDDLCustomFieldCount > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("], \"title\": {\"en_US\": \"\"}}],\"paginationMode\": ");
		sb.append("\"single-page\"}");

		return InitDataFactoryUtil.newDDMStructureLayoutModel(
			InitContextUtil.getGlobalGroupId(),
			InitContextUtil.getDefaultUserId(),
			ddmStructureVersionModel.getStructureVersionId(), sb.toString(),
			InitContextUtil.getCounter().get(),
			InitContextUtil.getCompanyId(), DataFactoryConstants.SAMPLE_USER_NAME,
			InitContextUtil.getFutureDateCounter());
	}

	public DDMStructureModel newDDLDDMStructureModel(long groupId) {
		int maxDDLCustomFieldCount =
			InitContextUtil.getMaxDDLCustomFieldCount();

		StringBundler sb = new StringBundler(3 + maxDDLCustomFieldCount * 9);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fields\": [");

		for (int i = 0; i < maxDDLCustomFieldCount; i++) {
			sb.append(
				"{\"dataType\": \"string\", \"indexType\": \"keyword\", ");
			sb.append("\"label\": {\"en_US\": \"Text");
			sb.append(i);
			sb.append("\"}, \"name\": \"");
			sb.append(InitDataFactoryUtil.nextDDLCustomFieldName(groupId, i));
			sb.append("\", \"readOnly\": false, \"repeatable\": false,");
			sb.append("\"required\": false, \"showLabel\": true, \"type\": ");
			sb.append("\"text\"}");
			sb.append(",");
		}

		if (maxDDLCustomFieldCount > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("]}");

		return InitDataFactoryUtil.newDDMStructureModel(
			groupId, InitContextUtil.getSampleUserId(),
			InitDataFactoryUtil.getClassNameId(
				DDLRecordSet.class,
				InitContextUtil.getClassNameModels()),
			"Test DDM Structure", sb.toString(),
			InitContextUtil.getCounter().get(),
			InitContextUtil.getCompanyId(), DataFactoryConstants.SAMPLE_USER_NAME,
			InitContextUtil.getFutureDateCounter());
	}

	public List<PortletPreferencesModel>
		newDDLPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public DDLRecordModel newDDLRecordModel(
		DDLRecordSetModel dDLRecordSetModel) {

		DDLRecordModel ddlRecordModel = new DDLRecordModelImpl();

		ddlRecordModel.setUuid(SequentialUUID.generate());
		ddlRecordModel.setRecordId(InitContextUtil.getCounter().get());
		ddlRecordModel.setGroupId(dDLRecordSetModel.getGroupId());
		ddlRecordModel.setCompanyId(InitContextUtil.getCompanyId());
		ddlRecordModel.setUserId(InitContextUtil.getSampleUserId());
		ddlRecordModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordModel.setVersionUserId(
			InitContextUtil.getSampleUserId());
		ddlRecordModel.setVersionUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordModel.setCreateDate(new Date());
		ddlRecordModel.setModifiedDate(new Date());
		ddlRecordModel.setDDMStorageId(
			InitContextUtil.getCounter().get());
		ddlRecordModel.setRecordSetId(dDLRecordSetModel.getRecordSetId());
		ddlRecordModel.setVersion(DDLRecordConstants.VERSION_DEFAULT);
		ddlRecordModel.setDisplayIndex(
			DDLRecordConstants.DISPLAY_INDEX_DEFAULT);
		ddlRecordModel.setLastPublishDate(new Date());

		return ddlRecordModel;
	}

	public DDLRecordSetModel newDDLRecordSetModel(
		DDMStructureModel ddmStructureModel, int currentIndex) {

		DDLRecordSetModel ddlRecordSetModel = new DDLRecordSetModelImpl();

		ddlRecordSetModel.setUuid(SequentialUUID.generate());
		ddlRecordSetModel.setRecordSetId(
			InitContextUtil.getCounter().get());
		ddlRecordSetModel.setGroupId(ddmStructureModel.getGroupId());
		ddlRecordSetModel.setCompanyId(InitContextUtil.getCompanyId());
		ddlRecordSetModel.setUserId(InitContextUtil.getSampleUserId());
		ddlRecordSetModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordSetModel.setCreateDate(new Date());
		ddlRecordSetModel.setModifiedDate(new Date());
		ddlRecordSetModel.setDDMStructureId(ddmStructureModel.getStructureId());
		ddlRecordSetModel.setRecordSetKey(
			String.valueOf(InitContextUtil.getCounter().get()));

		StringBundler sb = new StringBundler(5);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append("Test DDL Record Set ");
		sb.append(currentIndex);
		sb.append("</name></root>");

		ddlRecordSetModel.setName(sb.toString());

		ddlRecordSetModel.setMinDisplayRows(
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT);
		ddlRecordSetModel.setScope(
			DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS);
		ddlRecordSetModel.setSettings(StringPool.BLANK);
		ddlRecordSetModel.setLastPublishDate(new Date());

		return ddlRecordSetModel;
	}

	public DDLRecordVersionModel newDDLRecordVersionModel(
		DDLRecordModel dDLRecordModel) {

		DDLRecordVersionModel ddlRecordVersionModel =
			new DDLRecordVersionModelImpl();

		ddlRecordVersionModel.setRecordVersionId(
			InitContextUtil.getCounter().get());
		ddlRecordVersionModel.setGroupId(dDLRecordModel.getGroupId());
		ddlRecordVersionModel.setCompanyId(
			InitContextUtil.getCompanyId());
		ddlRecordVersionModel.setUserId(
			InitContextUtil.getSampleUserId());
		ddlRecordVersionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordVersionModel.setCreateDate(dDLRecordModel.getModifiedDate());
		ddlRecordVersionModel.setDDMStorageId(dDLRecordModel.getDDMStorageId());
		ddlRecordVersionModel.setRecordSetId(dDLRecordModel.getRecordSetId());
		ddlRecordVersionModel.setRecordId(dDLRecordModel.getRecordId());
		ddlRecordVersionModel.setVersion(dDLRecordModel.getVersion());
		ddlRecordVersionModel.setDisplayIndex(dDLRecordModel.getDisplayIndex());
		ddlRecordVersionModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		ddlRecordVersionModel.setStatusDate(dDLRecordModel.getModifiedDate());

		return ddlRecordVersionModel;
	}

	public DDMContentModel newDDMContentModel(
		DDLRecordModel ddlRecordModel, int currentIndex) {

		int maxDDLCustomFieldCount =
			InitContextUtil.getMaxDDLCustomFieldCount();

		StringBundler sb = new StringBundler(3 + maxDDLCustomFieldCount * 7);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [");

		for (int i = 0; i < maxDDLCustomFieldCount; i++) {
			sb.append("{\"instanceId\": \"");
			sb.append(StringUtil.randomId());
			sb.append("\", \"name\": \"");
			sb.append(
				InitDataFactoryUtil.nextDDLCustomFieldName(
					ddlRecordModel.getGroupId(), i));
			sb.append("\", \"value\": {\"en_US\": \"Test Record ");
			sb.append(currentIndex);
			sb.append("\"}},");
		}

		if (maxDDLCustomFieldCount > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("]}");

		return newDDMContentModel(
			ddlRecordModel.getDDMStorageId(), ddlRecordModel.getGroupId(),
			sb.toString());
	}

	public DDMContentModel newDDMContentModel(
		DLFileEntryModel dlFileEntryModel) {

		StringBundler sb = new StringBundler(6);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [{");
		sb.append("\"instanceId\": \"");
		sb.append(StringUtil.randomId());
		sb.append("\", \"name\": \"CONTENT_TYPE\", \"value\": {\"en_US\": ");
		sb.append("\"text/plain\"}}]}");

		return newDDMContentModel(
			InitContextUtil.getCounter().get(),
			dlFileEntryModel.getGroupId(), sb.toString());
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		return JournalDataFactory.newDDMStorageLinkModel(
			journalArticleModel, structureId);
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		long ddmStorageLinkId, DDMContentModel ddmContentModel,
		long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(ddmStorageLinkId);
		ddmStorageLinkModel.setClassNameId(
			InitDataFactoryUtil.getClassNameId(
				DDMContent.class, InitContextUtil.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(ddmContentModel.getContentId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DDLRecordSetModel ddlRecordSetModel) {

		return newDDMStructureLinkModel(
			InitDataFactoryUtil.getClassNameId(
				DDLRecordSet.class,
				InitContextUtil.getClassNameModels()),
			ddlRecordSetModel.getRecordSetId(),
			ddlRecordSetModel.getDDMStructureId());
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DLFileEntryMetadataModel dLFileEntryMetadataModel) {

		return newDDMStructureLinkModel(
			InitDataFactoryUtil.getClassNameId(
				DLFileEntryMetadata.class,
				InitContextUtil.getClassNameModels()),
			dLFileEntryMetadataModel.getFileEntryMetadataId(),
			dLFileEntryMetadataModel.getDDMStructureId());
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

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

		dlFileEntryMetadataModel.setUuid(SequentialUUID.generate());
		dlFileEntryMetadataModel.setFileEntryMetadataId(
			InitContextUtil.getCounter().get());
		dlFileEntryMetadataModel.setDDMStorageId(ddmStorageLinkId);
		dlFileEntryMetadataModel.setDDMStructureId(ddmStructureId);
		dlFileEntryMetadataModel.setFileEntryId(
			dlFileVersionModel.getFileEntryId());
		dlFileEntryMetadataModel.setFileVersionId(
			dlFileVersionModel.getFileVersionId());

		return dlFileEntryMetadataModel;
	}

	public List<DLFileEntryModel> newDlFileEntryModels(
		DLFolderModel dlFolerModel) {

		int maxDLFileEntryCount =
			InitContextUtil.getMaxDLFileEntryCount();

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			maxDLFileEntryCount);

		for (int i = 1; i <= maxDLFileEntryCount; i++) {
			dlFileEntryModels.add(
				InitDataFactoryUtil.newDlFileEntryModel(
					dlFolerModel, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(), DataFactoryConstants.SAMPLE_USER_NAME,
					InitContextUtil.getFutureDateCounter(),
					InitContextUtil.getMaxDLFileEntrySize()));
		}

		return dlFileEntryModels;
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		dlFileVersionModel.setUuid(SequentialUUID.generate());
		dlFileVersionModel.setFileVersionId(
			InitContextUtil.getCounter().get());
		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());
		dlFileVersionModel.setCompanyId(InitContextUtil.getCompanyId());
		dlFileVersionModel.setUserId(InitContextUtil.getSampleUserId());
		dlFileVersionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		dlFileVersionModel.setModifiedDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		dlFileVersionModel.setRepositoryId(dlFileEntryModel.getRepositoryId());
		dlFileVersionModel.setFolderId(dlFileEntryModel.getFolderId());
		dlFileVersionModel.setFileEntryId(dlFileEntryModel.getFileEntryId());
		dlFileVersionModel.setFileName(dlFileEntryModel.getFileName());
		dlFileVersionModel.setExtension(dlFileEntryModel.getExtension());
		dlFileVersionModel.setMimeType(dlFileEntryModel.getMimeType());
		dlFileVersionModel.setTitle(dlFileEntryModel.getTitle());
		dlFileVersionModel.setFileEntryTypeId(
			dlFileEntryModel.getFileEntryTypeId());
		dlFileVersionModel.setVersion(dlFileEntryModel.getVersion());
		dlFileVersionModel.setSize(dlFileEntryModel.getSize());
		dlFileVersionModel.setLastPublishDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));

		return dlFileVersionModel;
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		int maxDLFolderCount = InitContextUtil.getMaxDLFolderCount();

		List<DLFolderModel> dlFolderModels = new ArrayList<>(maxDLFolderCount);

		for (int i = 1; i <= maxDLFolderCount; i++) {
			dlFolderModels.add(
				InitDataFactoryUtil.newDLFolderModel(
					groupId, parentFolderId, i,
					InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(), DataFactoryConstants.SAMPLE_USER_NAME,
					InitContextUtil.getFutureDateCounter(),
					InitContextUtil.getDefaultDLFileEntryTypeModel()));
		}

		return dlFolderModels;
	}

	public GroupModel newGroupModel(UserModel userModel) throws Exception {
		return InitDataFactoryUtil.newGroupModel(
			InitContextUtil.getCounter().get(),
			InitDataFactoryUtil.getClassNameId(
				User.class, InitContextUtil.getClassNameModels()),
			userModel.getUserId(), userModel.getScreenName(), false,
			InitContextUtil.getCompanyId(),
			InitContextUtil.getSampleUserId());
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

		return JournalDataFactory.newJournalArticleResourceModel(
			groupId, _journalArticleResourceUUIDs);
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		return JournalDataFactory.newJournalContentSearchModel(
			journalArticleModel, layoutId);
	}

	public List<PortletPreferencesModel>
		newJournalPortletPreferencesModels(long plid) {

		return JournalDataFactory.newJournalPortletPreferencesModels(plid);
	}

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		LayoutFriendlyURLModel layoutFriendlyURLModel =
			new LayoutFriendlyURLModelImpl();

		layoutFriendlyURLModel.setUuid(SequentialUUID.generate());
		layoutFriendlyURLModel.setLayoutFriendlyURLId(
			InitContextUtil.getCounter().get());
		layoutFriendlyURLModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLModel.setCompanyId(
			InitContextUtil.getCompanyId());
		layoutFriendlyURLModel.setUserId(
			InitContextUtil.getSampleUserId());
		layoutFriendlyURLModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
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

		SimpleCounter simpleCounter = _layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			_layoutCounters.put(groupId, simpleCounter);
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

		layoutSetModels.add(
			InitDataFactoryUtil.newLayoutSetModel(
				groupId, true, 0, InitContextUtil.getCounter().get(),
				InitContextUtil.getCompanyId()));
		layoutSetModels.add(
			InitDataFactoryUtil.newLayoutSetModel(
				groupId, false, publicLayoutSetPageCount,
				InitContextUtil.getCounter().get(),
				InitContextUtil.getCompanyId()));

		return layoutSetModels;
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		int maxMBCategoryCount = InitContextUtil.getMaxMBCategoryCount();

		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			maxMBCategoryCount);

		for (int i = 1; i <= maxMBCategoryCount; i++) {
			mbCategoryModels.add(
				InitDataFactoryUtil.newMBCategoryModel(
					groupId, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(), DataFactoryConstants.SAMPLE_USER_NAME,
					InitContextUtil.getMaxMBThreadCount(),
					InitContextUtil.getMaxMBMessageCount()));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		mbDiscussionModel.setUuid(SequentialUUID.generate());
		mbDiscussionModel.setDiscussionId(
			InitContextUtil.getCounter().get());
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
		mbMailingListModel.setMailingListId(
			InitContextUtil.getCounter().get());
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

		mbStatsUserModel.setStatsUserId(
			InitContextUtil.getCounter().get());
		mbStatsUserModel.setGroupId(groupId);
		mbStatsUserModel.setUserId(InitContextUtil.getSampleUserId());
		mbStatsUserModel.setMessageCount(
			maxMBCategoryCount * maxMBThreadCount * maxMBMessageCount);
		mbStatsUserModel.setLastPostDate(new Date());

		return mbStatsUserModel;
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel)
	{
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		mbThreadFlagModel.setUuid(SequentialUUID.generate());
		mbThreadFlagModel.setThreadFlagId(
			InitContextUtil.getCounter().get());
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
			return InitDataFactoryUtil.newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		SimpleCounter counter = _assetPublisherQueryCounter.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetPublisherQueryCounter.put(groupId, counter);
		}

		String[] assetPublisherQueryValues = null;

		if (InitContextUtil.getAssetPublisherQueryName().equals(
				"assetCategories")) {

			List<AssetCategoryModel> assetCategoryModels =
				InitContextUtil.getAssetCategoryModelsArray()[size];

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return InitDataFactoryUtil.newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			int maxAssetEntryToAssetCategoryCount =
				InitContextUtil.getMaxAssetEntryToAssetCategoryCount();
			assetPublisherQueryValues =
				InitDataFactoryUtil.getAssetPublisherAssetCategoriesQueryValues(
					assetCategoryModels, (int)counter.get(),
					maxAssetEntryToAssetCategoryCount);
		}
		else {
			List<AssetTagModel> assetTagModels =
				InitContextUtil.getAssetTagModelsArray()[size];

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return InitDataFactoryUtil.newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			assetPublisherQueryValues =
				InitDataFactoryUtil.getAssetPublisherAssetTagsQueryValues(
					assetTagModels, (int)counter.get(),
					InitContextUtil.getMaxAssetEntryToAssetTagCount());
		}

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)InitContextUtil.
				getDefaultAssetPublisherPortletPreference().clone();

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

		return InitDataFactoryUtil.newPortletPreferencesModel(
			plid, portletId,
			_portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue("editable", "true");
		jxPortletPreferences.setValue(
			"recordSetId", String.valueOf(ddlRecordSetModel.getRecordSetId()));
		jxPortletPreferences.setValue("spreadsheet", "false");

		return InitDataFactoryUtil.newPortletPreferencesModel(
			plid, portletId,
			_portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		return JournalDataFactory.newPortletPreferencesModel(
			plid, portletId, journalArticleResourceModel,
			_portletPreferencesFactory);
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
			String.valueOf(assetCategoryModel.getCategoryId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetTagModel assetTagModel) {

		return newResourcePermissionModels(
			AssetTag.class.getName(), String.valueOf(assetTagModel.getTagId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() ==
				InitContextUtil.getDefaultUserId()) {

			return Collections.singletonList(
				InitDataFactoryUtil.newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					InitContextUtil.getOwnerRoleModel().getRoleId(),
					InitContextUtil.getDefaultUserId(),
					InitContextUtil.getResourcePermissionCounter().get(),
					InitContextUtil.getCompanyId()));
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
			String.valueOf(blogsEntryModel.getEntryId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getDefaultUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = InitDataFactoryUtil.getResourcePermissionModelName(
			DDMStructure.class.getName(),
			InitDataFactoryUtil.getClassName(
				ddmStructureModel.getClassNameId(),
				InitContextUtil.getClassNameModels()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getGuestRoleModel().getRoleId(), 0,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				ddmStructureModel.getUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getUserRoleModel().getRoleId(), 0,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = InitDataFactoryUtil.getResourcePermissionModelName(
			DDMTemplate.class.getName(), InitDataFactoryUtil.getClassName(
				ddmTemplateModel.getResourceClassNameId(),
				InitContextUtil.getClassNameModels()));
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getGuestRoleModel().getRoleId(), 0,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				ddmTemplateModel.getUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getUserRoleModel().getRoleId(), 0,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getSampleUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
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
			String.valueOf(mbCategoryModel.getCategoryId()),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				MBMessage.class.getName(),
				String.valueOf(mbMessageModel.getMessageId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getSampleUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
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
			InitDataFactoryUtil.newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getSampleUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey),
			InitContextUtil.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				userModel.getUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
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
			String.valueOf(wikiPageModel.getResourcePrimKey()),
			InitContextUtil.getSampleUserId());
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel) {

		return newSocialActivityModel(
			blogsEntryModel.getGroupId(), InitDataFactoryUtil.getClassNameId(
				BlogsEntry.class, InitContextUtil.getClassNameModels()),
			blogsEntryModel.getEntryId(), BlogsActivityKeys.ADD_ENTRY,
			"{\"title\":\"" + blogsEntryModel.getTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel) {

		return newSocialActivityModel(
			dlFileEntryModel.getGroupId(), InitDataFactoryUtil.getClassNameId(
				DLFileEntry.class, InitContextUtil.getClassNameModels()),
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
			InitDataFactoryUtil.getClassNameId(
				JournalArticle.class,
				InitContextUtil.getClassNameModels()),
			journalArticleModel.getResourcePrimKey(), type,
			"{\"title\":\"" + journalArticleModel.getUrlTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		MBMessageModel mbMessageModel) {

		long classNameId = mbMessageModel.getClassNameId();
		long classPK = mbMessageModel.getClassPK();

		int type = 0;
		String extraData = null;

		if (classNameId == InitDataFactoryUtil.getClassNameId(
				WikiPage.class, InitContextUtil.getClassNameModels())) {

			extraData = "{\"version\":1}";
			type = WikiActivityKeys.ADD_PAGE;
		}
		else if (classNameId == 0) {
			extraData = "{\"title\":\"" + mbMessageModel.getSubject() + "\"}";

			type = MBActivityKeys.ADD_MESSAGE;

			classNameId = InitDataFactoryUtil.getClassNameId(
				MBMessage.class, InitContextUtil.getClassNameModels());
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

	public SubscriptionModel newSubscriptionModel(MBThreadModel mBThreadModel)
	{
		return newSubscriptionModel(
			InitDataFactoryUtil.getClassNameId(
				MBThread.class, InitContextUtil.getClassNameModels()),
			mBThreadModel.getThreadId());
	}

	public SubscriptionModel newSubscriptionModel(WikiPageModel wikiPageModel)
	{
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
				"test" + InitContextUtil.getUserScreenNameCounter().
					get();
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

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel)
	{
		int maxWikiPageCount = InitContextUtil.getMaxWikiPageCount();

		List<WikiPageModel> wikiPageModels = new ArrayList<>(maxWikiPageCount);

		for (int i = 1; i <= maxWikiPageCount; i++) {
			wikiPageModels.add(
				InitDataFactoryUtil.newWikiPageModel(
					wikiNodeModel, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME));
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

	protected DDMContentModel newDDMContentModel(
		long contentId, long groupId, String data) {

		DDMContentModel ddmContentModel = new DDMContentModelImpl();

		ddmContentModel.setUuid(SequentialUUID.generate());
		ddmContentModel.setContentId(contentId);
		ddmContentModel.setGroupId(groupId);
		ddmContentModel.setCompanyId(InitContextUtil.getCompanyId());
		ddmContentModel.setUserId(InitContextUtil.getSampleUserId());
		ddmContentModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddmContentModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		ddmContentModel.setModifiedDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		ddmContentModel.setName(DDMStorageLink.class.getName());
		ddmContentModel.setData(data);

		return ddmContentModel;
	}

	protected DDMStructureLinkModel newDDMStructureLinkModel(
		long classNameId, long classPK, long structureId) {

		DDMStructureLinkModel ddmStructureLinkModel =
			new DDMStructureLinkModelImpl();

		ddmStructureLinkModel.setStructureLinkId(
			InitContextUtil.getCounter().get());
		ddmStructureLinkModel.setClassNameId(classNameId);
		ddmStructureLinkModel.setClassPK(classPK);
		ddmStructureLinkModel.setStructureId(structureId);

		return ddmStructureLinkModel;
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
		mbThreadModel.setRootMessageUserId(
			InitContextUtil.getSampleUserId());
		mbThreadModel.setMessageCount(messageCount);
		mbThreadModel.setLastPostByUserId(
			InitContextUtil.getSampleUserId());
		mbThreadModel.setLastPostDate(new Date());
		mbThreadModel.setLastPublishDate(new Date());
		mbThreadModel.setStatusDate(new Date());

		return mbThreadModel;
	}

	protected List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getGuestRoleModel().getRoleId(), 0,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getOwnerRoleModel().getRoleId(), ownerId,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getSiteMemberRoleModel().getRoleId(), 0,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));

		return resourcePermissionModels;
	}

	protected SocialActivityModel newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		socialActivityModel.setActivityId(
			InitContextUtil.getSocialActivityCounter().get());
		socialActivityModel.setGroupId(groupId);
		socialActivityModel.setCompanyId(InitContextUtil.getCompanyId());
		socialActivityModel.setUserId(InitContextUtil.getSampleUserId());
		socialActivityModel.setCreateDate(
			_CURRENT_TIME + InitContextUtil.getTimeCounter().get());
		socialActivityModel.setClassNameId(classNameId);
		socialActivityModel.setClassPK(classPK);
		socialActivityModel.setType(type);
		socialActivityModel.setExtraData(extraData);

		return socialActivityModel;
	}

	protected SubscriptionModel newSubscriptionModel(
		long classNameId, long classPK) {

		SubscriptionModel subscriptionModel = new SubscriptionModelImpl();

		subscriptionModel.setSubscriptionId(
			InitContextUtil.getCounter().get());
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

	private static final long _CURRENT_TIME = System.currentTimeMillis();

	private static final PortletPreferencesFactory _portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();

	private final Map<Long, SimpleCounter> _assetCategoryCounters =
		new HashMap<>();
	private final Map<Long, SimpleCounter> _assetPublisherQueryCounter =
		new HashMap<>();
	private final Class<?> _clazz = getClass();
	private final Map<Long, String> _journalArticleResourceUUIDs =
		new HashMap<>();
	private final Map<Long, SimpleCounter> _layoutCounters = new HashMap<>();

}