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
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
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
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Company;
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
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portal.model.impl.SubscriptionModelImpl;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.blogs.model.impl.BlogsStatsUserModelImpl;
import com.liferay.portlet.blogs.social.BlogsActivityKeys;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
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
		InitDataFactoryContext.initContext(properties);
		InitDataFactoryContext.initParameter();
		InitDataFactoryContext.initResource(_clazz,_portletPreferencesFactory);
		InitDataFactoryContext.initCompanyModels();
		InitDataFactoryContext.initUserNames(_clazz);
		InitDataFactoryContext.initGroupModels();
		InitDataFactoryContext.initUserModels(_SAMPLE_USER_NAME);
		InitDataFactoryContext.initAssetCategoryModels(_SAMPLE_USER_NAME);
		InitDataFactoryContext.initAssetTagModels(_SAMPLE_USER_NAME);
		initDLFileEntryTypeModel();
		initRoleModels();
	}

	public AccountModel getAccountModel() {
		return InitDataFactoryContext.getAccountModel();
	}

	public RoleModel getAdministratorRoleModel() {
		return _administratorRoleModel;
	}

	public List<Long> getAssetCategoryIds(long groupId) {
		SimpleCounter counter = _assetCategoryCounters.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetCategoryCounters.put(groupId, counter);
		}

		List<AssetCategoryModel> assetCategoryModels =
			InitDataFactoryContext.getAssetCategoryModelsArray()[(int)groupId - 1];

		if ((assetCategoryModels == null) || assetCategoryModels.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> assetCategoryIds = new ArrayList<>(
			InitDataFactoryContext.getMaxAssetEntryToAssetCategoryCount());

		for (int i =
		 0; i <
		 InitDataFactoryContext.getMaxAssetEntryToAssetCategoryCount(); i++) {

			int index = (int)counter.get() % assetCategoryModels.size();

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index);

			assetCategoryIds.add(assetCategoryModel.getCategoryId());
		}

		return assetCategoryIds;
	}

	public List<AssetCategoryModel> getAssetCategoryModels() {
		List<AssetCategoryModel> allAssetCategoryModels = new ArrayList<>();

		for (List<AssetCategoryModel> assetCategoryModels :
				InitDataFactoryContext.getAssetCategoryModelsArray()) {

			allAssetCategoryModels.addAll(assetCategoryModels);
		}

		return allAssetCategoryModels;
	}

	public List<Long> getAssetTagIds(long groupId) {
		SimpleCounter counter = _assetTagCounters.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetTagCounters.put(groupId, counter);
		}

		List<AssetTagModel> assetTagModels =
			InitDataFactoryContext.getAssetTagModelsArray()[(int)groupId - 1];

		if ((assetTagModels == null) || assetTagModels.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> assetTagIds = new ArrayList<>(
			InitDataFactoryContext.getMaxAssetEntryToAssetTagCount());

		for (int i =
		 0; i < InitDataFactoryContext.getMaxAssetEntryToAssetTagCount(); i++) {

			int index = (int)counter.get() % assetTagModels.size();

			AssetTagModel assetTagModel = assetTagModels.get(index);

			assetTagIds.add(assetTagModel.getTagId());
		}

		return assetTagIds;
	}

	public List<AssetTagModel> getAssetTagModels() {
		List<AssetTagModel> allAssetTagModels = new ArrayList<>();

		for (List<AssetTagModel> assetTagModels : InitDataFactoryContext.getAssetTagModelsArray()) {
			allAssetTagModels.addAll(assetTagModels);
		}

		return allAssetTagModels;
	}

	public List<AssetTagStatsModel> getAssetTagStatsModels() {
		List<AssetTagStatsModel> allAssetTagStatsModels = new ArrayList<>();

		for (List<AssetTagStatsModel> assetTagStatsModels :
				InitDataFactoryContext.getAssetTagStatsModelsArray()) {

			allAssetTagStatsModels.addAll(assetTagStatsModels);
		}

		return allAssetTagStatsModels;
	}

	public List<AssetVocabularyModel> getAssetVocabularyModels() {
		List<AssetVocabularyModel> allAssetVocabularyModels = new ArrayList<>();

		allAssetVocabularyModels.add(InitDataFactoryContext.getDefaultAssetVocabularyModel());

		for (List<AssetVocabularyModel> assetVocabularyModels :
				InitDataFactoryContext.getAssetVocabularyModelsArray()) {

			allAssetVocabularyModels.addAll(assetVocabularyModels);
		}

		return allAssetVocabularyModels;
	}

	public long getBlogsEntryClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			BlogsEntry.class, InitDataFactoryContext.getClassNameModels());
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return InitDataFactoryContext.getClassNameModels().values();
	}

	public CompanyModel getCompanyModel() {
		return InitDataFactoryContext.getCompanyModel();
	}

	public SimpleCounter getCounter() {
		return InitDataFactoryContext.getCounter();
	}

	public long getCounterNext() {
		return InitDataFactoryContext.getCounter().get();
	}

	public String getDateLong(Date date) {
		return String.valueOf(date.getTime());
	}

	public String getDateString(Date date) {
		if (date == null) {
			return null;
		}

		return InitDataFactoryContext.getSimpleDateFormat().format(date);
	}

	public long getDDLRecordSetClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
				DDLRecordSet.class,
				InitDataFactoryContext.getClassNameModels());
	}

	public long getDefaultDLDDMStructureId() {
		return _defaultDLDDMStructureModel.getStructureId();
	}

	public DDMStructureLayoutModel getDefaultDLDDMStructureLayoutModel() {
		return _defaultDLDDMStructureLayoutModel;
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return _defaultDLDDMStructureModel;
	}

	public DDMStructureVersionModel getDefaultDLDDMStructureVersionModel() {
		return _defaultDLDDMStructureVersionModel;
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return _defaultDLFileEntryTypeModel;
	}

	public DDMStructureLayoutModel getDefaultJournalDDMStructureLayoutModel() {
		return _defaultJournalDDMStructureLayoutModel;
	}

	public DDMStructureModel getDefaultJournalDDMStructureModel() {
		return _defaultJournalDDMStructureModel;
	}

	public DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return _defaultJournalDDMStructureVersionModel;
	}

	public DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return _defaultJournalDDMTemplateModel;
	}

	public UserModel getDefaultUserModel() {
		return InitDataFactoryContext.getDefaultUserModel();
	}

	public long getDLFileEntryClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			DLFileEntry.class, InitDataFactoryContext.getClassNameModels());
	}

	public GroupModel getGlobalGroupModel() {
		return InitDataFactoryContext.getGlobalGroupModel();
	}

	public List<GroupModel> getGroupModels() {
		return InitDataFactoryContext.getGroupModels();
	}

	public GroupModel getGuestGroupModel() {
		return InitDataFactoryContext.getGuestGroupModel();
	}

	public UserModel getGuestUserModel() {
		return InitDataFactoryContext.getGuestUserModel();
	}

	public long getJournalArticleClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
				JournalArticle.class,
				InitDataFactoryContext.getClassNameModels());
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		StringBundler sb = new StringBundler(
			3 * InitDataFactoryContext.getMaxJournalArticleCount());

		for (int i =
		 1; i <= InitDataFactoryContext.getMaxJournalArticleCount(); i++) {

			sb.append(portletPrefix);
			sb.append(i);
			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	public long getLayoutClassNameId() {
		return InitDataFactoryUtil.getClassNameId(Layout.class,
			InitDataFactoryContext.getClassNameModels());
	}

	public int getMaxAssetPublisherPageCount() {
		return InitDataFactoryContext.getMaxAssetPublisherPageCount();
	}

	public int getMaxBlogsEntryCommentCount() {
		return InitDataFactoryContext.getMaxBlogsEntryCommentCount();
	}

	public int getMaxDDLRecordCount() {
		return InitDataFactoryContext.getMaxDDLRecordCount();
	}

	public int getMaxDDLRecordSetCount() {
		return InitDataFactoryContext.getMaxDDLRecordSetCount();
	}

	public int getMaxDLFolderDepth() {
		return InitDataFactoryContext.getMaxDLFolderDepth();
	}

	public int getMaxGroupCount() {
		return InitDataFactoryContext.getMaxGroupsCount();
	}

	public int getMaxJournalArticleCount() {
		return InitDataFactoryContext.getMaxJournalArticleCount();
	}

	public int getMaxJournalArticlePageCount() {
		return InitDataFactoryContext.getMaxJournalArticlePageCount();
	}

	public int getMaxJournalArticleVersionCount() {
		return InitDataFactoryContext.getMaxJournalArticleVersionCount();
	}

	public int getMaxWikiPageCommentCount() {
		return InitDataFactoryContext.getMaxWikiPageCommentCount();
	}

	public List<Long> getNewUserGroupIds(long groupId) {
		List<Long> groupIds = new ArrayList<>(
			InitDataFactoryContext.getMaxUserToGroupCount() + 1);

		groupIds.add(InitDataFactoryContext.getGuestGroupModel().getGroupId());

		if ((groupId + InitDataFactoryContext.getMaxUserToGroupCount()) >
				InitDataFactoryContext.getMaxGroupsCount()) {

			groupId =
				groupId - InitDataFactoryContext.getMaxUserToGroupCount() + 1;
		}

		for (int i =
		 0; i < InitDataFactoryContext.getMaxUserToGroupCount(); i++) {

			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public RoleModel getPowerUserRoleModel() {
		return _powerUserRoleModel;
	}

	public List<RoleModel> getRoleModels() {
		return _roleModels;
	}

	public UserModel getSampleUserModel() {
		return InitDataFactoryContext.getSampleUserModel();
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public RoleModel getUserRoleModel() {
		return _userRoleModel;
	}

	public VirtualHostModel getVirtualHostModel() {
		return InitDataFactoryContext.getVirtualHostModel();
	}

	public long getWikiPageClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			WikiPage.class, InitDataFactoryContext.getClassNameModels());
	}

	public void initDLFileEntryTypeModel() {
		_defaultDLFileEntryTypeModel = new DLFileEntryTypeModelImpl();

		_defaultDLFileEntryTypeModel.setUuid(SequentialUUID.generate());
		_defaultDLFileEntryTypeModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		_defaultDLFileEntryTypeModel.setCreateDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		_defaultDLFileEntryTypeModel.setModifiedDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		_defaultDLFileEntryTypeModel.setFileEntryTypeKey(
			StringUtil.toUpperCase(
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT));

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT);
		sb.append("</name></root>");

		_defaultDLFileEntryTypeModel.setName(sb.toString());
		_defaultDLFileEntryTypeModel.setLastPublishDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));

		_defaultDLDDMStructureModel = InitDataFactoryUtil.newDDMStructureModel(
			InitDataFactoryContext.getGlobalGroupId(), InitDataFactoryContext.getDefaultUserId(), InitDataFactoryUtil.getClassNameId(
					DLFileEntry.class,
					InitDataFactoryContext.getClassNameModels()),
			RawMetadataProcessor.TIKA_RAW_METADATA, InitDataFactoryContext.getDlDDMStructureContent(),
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(), _SAMPLE_USER_NAME,
			InitDataFactoryContext.getFutureDateCounter());

		_defaultDLDDMStructureVersionModel = newDDMStructureVersionModel(
			_defaultDLDDMStructureModel);

		_defaultDLDDMStructureLayoutModel =
			InitDataFactoryUtil.newDDMStructureLayoutModel(
				InitDataFactoryContext.getGlobalGroupId(),
				InitDataFactoryContext.getDefaultUserId(),
			_defaultDLDDMStructureVersionModel.getStructureVersionId(),
			InitDataFactoryContext.getDlDDMStructureLayoutContent(),
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(), _SAMPLE_USER_NAME,
			InitDataFactoryContext.getFutureDateCounter());

		_defaultJournalDDMStructureModel =
			InitDataFactoryUtil.newDDMStructureModel(
				InitDataFactoryContext.getGlobalGroupId(),
				InitDataFactoryContext.getDefaultUserId(),
			InitDataFactoryUtil.getClassNameId(
			JournalArticle.class,
			InitDataFactoryContext.getClassNameModels()), "BASIC-WEB-CONTENT",
			InitDataFactoryContext.getJournalDDMStructureContent(),
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(), _SAMPLE_USER_NAME,
			InitDataFactoryContext.getFutureDateCounter());

		_defaultJournalDDMStructureVersionModel = newDDMStructureVersionModel(
			_defaultJournalDDMStructureModel);

		_defaultJournalDDMStructureLayoutModel =
			InitDataFactoryUtil.newDDMStructureLayoutModel(
				InitDataFactoryContext.getGlobalGroupId(),
				InitDataFactoryContext.getDefaultUserId(),
			_defaultJournalDDMStructureVersionModel.getStructureVersionId(),
			InitDataFactoryContext.getJournalDDMStructureLayoutContent(),
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(), _SAMPLE_USER_NAME,
			InitDataFactoryContext.getFutureDateCounter());

		_defaultJournalDDMTemplateModel =
			InitDataFactoryUtil.newDDMTemplateModel(
				InitDataFactoryContext.getGlobalGroupId(),
				InitDataFactoryContext.getDefaultUserId(),
			_defaultJournalDDMStructureModel.getStructureId(),
			InitDataFactoryUtil.getClassNameId(
			JournalArticle.class, InitDataFactoryContext.getClassNameModels()),
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getFutureDateCounter(),
			InitDataFactoryContext.getClassNameModels(),
			InitDataFactoryContext.getCounter().get(), _SAMPLE_USER_NAME);
	}

	public void initRoleModels() {
		long classNameId = InitDataFactoryUtil.getClassNameId(
			Role.class, InitDataFactoryContext.getClassNameModels());

		_roleModels = new ArrayList<>();

		// Administrator

		_administratorRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(_administratorRoleModel);

		// Guest

		_guestRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.GUEST,
			RoleConstants.TYPE_REGULAR,
				InitDataFactoryContext.getCounter().get(),
				InitDataFactoryContext.getCompanyId(),
				InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
				classNameId);

		_roleModels.add(_guestRoleModel);

		// Organization Administrator

		RoleModel organizationAdministratorRoleModel =
			InitDataFactoryUtil.newRoleModel(
					RoleConstants.ORGANIZATION_ADMINISTRATOR,
					RoleConstants.TYPE_ORGANIZATION,
					InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId(),
					InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
					classNameId);

		_roleModels.add(organizationAdministratorRoleModel);

		// Organization Owner

		RoleModel organizationOwnerRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.ORGANIZATION_OWNER, RoleConstants.TYPE_ORGANIZATION,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(organizationOwnerRoleModel);

		// Organization User

		RoleModel organizationUserRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.ORGANIZATION_USER, RoleConstants.TYPE_ORGANIZATION,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(organizationUserRoleModel);

		// Owner

		_ownerRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(_ownerRoleModel);

		// Power User

		_powerUserRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(_powerUserRoleModel);

		// Site Administrator

		RoleModel siteAdministratorRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(siteAdministratorRoleModel);

		// Site Member

		_siteMemberRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(_siteMemberRoleModel);

		// Site Owner

		RoleModel siteOwnerRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(siteOwnerRoleModel);

		// User

		_userRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR,
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
			classNameId);

		_roleModels.add(_userRoleModel);
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			BlogsEntry.class,
			InitDataFactoryContext.getClassNameModels()), blogsEntryModel.getEntryId(),
			blogsEntryModel.getUuid(), 0, true, true, ContentTypes.TEXT_HTML,
			blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(),
			InitDataFactoryUtil.getClassNameId(
			DLFileEntry.class, InitDataFactoryContext.getClassNameModels()),
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			DLFolder.class,
			InitDataFactoryContext.getClassNameModels()), dLFolderModel.getFolderId(),
			dLFolderModel.getUuid(), 0, true, true, null,
			dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = InitDataFactoryUtil.getClassNameId(
					MBDiscussion.class,
					InitDataFactoryContext.getClassNameModels());
		}
		else {
			classNameId = InitDataFactoryUtil.getClassNameId(
					MBMessage.class,
					InitDataFactoryContext.getClassNameModels());
			visible = true;
		}

		return newAssetEntryModel(
			mbMessageModel.getGroupId(), mbMessageModel.getCreateDate(),
			mbMessageModel.getModifiedDate(), classNameId,
			mbMessageModel.getMessageId(), mbMessageModel.getUuid(), 0, true,
			visible, ContentTypes.TEXT_HTML, mbMessageModel.getSubject());
	}

	public AssetEntryModel newAssetEntryModel(MBThreadModel mbThreadModel) {
		return newAssetEntryModel(
			mbThreadModel.getGroupId(), mbThreadModel.getCreateDate(),
			mbThreadModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			MBThread.class,
			InitDataFactoryContext.getClassNameModels()), mbThreadModel.getThreadId(),
			mbThreadModel.getUuid(), 0, true, false, StringPool.BLANK,
			String.valueOf(mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		String resourceUUID = _journalArticleResourceUUIDs.get(resourcePrimKey);

		return newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			InitDataFactoryUtil.getClassNameId(
			JournalArticle.class, InitDataFactoryContext.getClassNameModels()),
			resourcePrimKey, resourceUUID,
			_defaultJournalDDMStructureModel.getStructureId(),
			journalArticleModel.isIndexable(), true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			WikiPage.class,
			InitDataFactoryContext.getClassNameModels()), wikiPageModel.getResourcePrimKey(),
			wikiPageModel.getUuid(), 0, true, true, ContentTypes.TEXT_HTML,
			wikiPageModel.getTitle());
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
		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			InitDataFactoryContext.getMaxBlogsEntryCount());

		for (int i =
		 1; i <= InitDataFactoryContext.getMaxBlogsEntryCount(); i++) {

			blogEntryModels.add(
					InitDataFactoryUtil.newBlogsEntryModel(
					groupId, i, InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId(),
					InitDataFactoryContext.getSampleUserId(),
					_SAMPLE_USER_NAME));
		}

		return blogEntryModels;
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		BlogsStatsUserModel blogsStatsUserModel = new BlogsStatsUserModelImpl();

		blogsStatsUserModel.setStatsUserId(
			InitDataFactoryContext.getCounter().get());
		blogsStatsUserModel.setGroupId(groupId);
		blogsStatsUserModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		blogsStatsUserModel.setUserId(InitDataFactoryContext.getSampleUserId());
		blogsStatsUserModel.setEntryCount(
			InitDataFactoryContext.getMaxBlogsEntryCount());
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
		contactModel.setClassNameId(InitDataFactoryUtil.getClassNameId(
		User.class, InitDataFactoryContext.getClassNameModels()));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setAccountId(InitDataFactoryContext.getAccountId());
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
		counterModel.setCurrentId(InitDataFactoryContext.getCounter().get());

		counterModels.add(counterModel);

		// ResourcePermission

		counterModel = new CounterModelImpl();

		counterModel.setName(ResourcePermission.class.getName());
		counterModel.setCurrentId(
			InitDataFactoryContext.getResourcePermissionCounter().get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(
			InitDataFactoryContext.getSocialActivityCounter().get());

		counterModels.add(counterModel);

		return counterModels;
	}

	public DDMStructureLayoutModel newDDLDDMStructureLayoutModel(
		long groupId, DDMStructureVersionModel ddmStructureVersionModel) {

		StringBundler sb = new StringBundler(
			4 + InitDataFactoryContext.getMaxDDLCustomFieldCount() * 4);

		sb.append("{\"defaultLanguageId\": \"en_US\", \"pages\": [{\"rows\": ");
		sb.append("[");

		for (int i =
		 0; i < InitDataFactoryContext.getMaxDDLCustomFieldCount(); i++) {

			sb.append("{\"columns\": [{\"fieldNames\": [\"");
			sb.append(InitDataFactoryUtil.nextDDLCustomFieldName(groupId, i));
			sb.append("\"], \"size\": 12}]}");
			sb.append(", ");
		}

		if (InitDataFactoryContext.getMaxDDLCustomFieldCount() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("], \"title\": {\"en_US\": \"\"}}],\"paginationMode\": ");
		sb.append("\"single-page\"}");

		return InitDataFactoryUtil.newDDMStructureLayoutModel(
			InitDataFactoryContext.getGlobalGroupId(),
			InitDataFactoryContext.getDefaultUserId(),
			ddmStructureVersionModel.getStructureVersionId(), sb.toString(),
			InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(), _SAMPLE_USER_NAME,
			InitDataFactoryContext.getFutureDateCounter());
	}

	public DDMStructureModel newDDLDDMStructureModel(long groupId) {
		StringBundler sb = new StringBundler(
			3 + InitDataFactoryContext.getMaxDDLCustomFieldCount() * 9);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fields\": [");

		for (int i =
		 0; i < InitDataFactoryContext.getMaxDDLCustomFieldCount(); i++) {

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

		if (InitDataFactoryContext.getMaxDDLCustomFieldCount() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("]}");

		return InitDataFactoryUtil.newDDMStructureModel(
			groupId, InitDataFactoryContext.getSampleUserId(), InitDataFactoryUtil.getClassNameId(
			DDLRecordSet.class,
			InitDataFactoryContext.getClassNameModels()), "Test DDM Structure",
			sb.toString(), InitDataFactoryContext.getCounter().get(),
			InitDataFactoryContext.getCompanyId(), _SAMPLE_USER_NAME,
			InitDataFactoryContext.getFutureDateCounter());
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

		DDLRecordModel ddlRecordModel = new DDLRecordModelImpl();

		ddlRecordModel.setUuid(SequentialUUID.generate());
		ddlRecordModel.setRecordId(InitDataFactoryContext.getCounter().get());
		ddlRecordModel.setGroupId(dDLRecordSetModel.getGroupId());
		ddlRecordModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		ddlRecordModel.setUserId(InitDataFactoryContext.getSampleUserId());
		ddlRecordModel.setUserName(_SAMPLE_USER_NAME);
		ddlRecordModel.setVersionUserId(
			InitDataFactoryContext.getSampleUserId());
		ddlRecordModel.setVersionUserName(_SAMPLE_USER_NAME);
		ddlRecordModel.setCreateDate(new Date());
		ddlRecordModel.setModifiedDate(new Date());
		ddlRecordModel.setDDMStorageId(
			InitDataFactoryContext.getCounter().get());
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
			InitDataFactoryContext.getCounter().get());
		ddlRecordSetModel.setGroupId(ddmStructureModel.getGroupId());
		ddlRecordSetModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		ddlRecordSetModel.setUserId(InitDataFactoryContext.getSampleUserId());
		ddlRecordSetModel.setUserName(_SAMPLE_USER_NAME);
		ddlRecordSetModel.setCreateDate(new Date());
		ddlRecordSetModel.setModifiedDate(new Date());
		ddlRecordSetModel.setDDMStructureId(ddmStructureModel.getStructureId());
		ddlRecordSetModel.setRecordSetKey(
			String.valueOf(InitDataFactoryContext.getCounter().get()));

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
			InitDataFactoryContext.getCounter().get());
		ddlRecordVersionModel.setGroupId(dDLRecordModel.getGroupId());
		ddlRecordVersionModel.setCompanyId(
			InitDataFactoryContext.getCompanyId());
		ddlRecordVersionModel.setUserId(
			InitDataFactoryContext.getSampleUserId());
		ddlRecordVersionModel.setUserName(_SAMPLE_USER_NAME);
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

		StringBundler sb = new StringBundler(
			3 + InitDataFactoryContext.getMaxDDLCustomFieldCount() * 7);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [");

		for (int i =
		 0; i < InitDataFactoryContext.getMaxDDLCustomFieldCount(); i++) {

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

		if (InitDataFactoryContext.getMaxDDLCustomFieldCount() > 0) {
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
			InitDataFactoryContext.getCounter().get(),
			dlFileEntryModel.getGroupId(), sb.toString());
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(
			InitDataFactoryContext.getCounter().get());
		ddmStorageLinkModel.setClassNameId(
			InitDataFactoryUtil.getClassNameId(
					JournalArticle.class,
					InitDataFactoryContext.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		long ddmStorageLinkId, DDMContentModel ddmContentModel,
		long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(ddmStorageLinkId);
		ddmStorageLinkModel.setClassNameId(InitDataFactoryUtil.getClassNameId(
			DDMContent.class, InitDataFactoryContext.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(ddmContentModel.getContentId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DDLRecordSetModel ddlRecordSetModel) {

		return newDDMStructureLinkModel(
			InitDataFactoryUtil.getClassNameId(
					DDLRecordSet.class,
					InitDataFactoryContext.getClassNameModels()),
			ddlRecordSetModel.getRecordSetId(),
			ddlRecordSetModel.getDDMStructureId());
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DLFileEntryMetadataModel dLFileEntryMetadataModel) {

		return newDDMStructureLinkModel(
			InitDataFactoryUtil.getClassNameId(
					DLFileEntryMetadata.class,
					InitDataFactoryContext.getClassNameModels()),
			dLFileEntryMetadataModel.getFileEntryMetadataId(),
			dLFileEntryMetadataModel.getDDMStructureId());
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		DDMStructureVersionModel ddmStructureVersionModel =
			new DDMStructureVersionModelImpl();

		ddmStructureVersionModel.setStructureVersionId(
			InitDataFactoryContext.getCounter().get());
		ddmStructureVersionModel.setGroupId(ddmStructureModel.getGroupId());
		ddmStructureVersionModel.setCompanyId(
			InitDataFactoryContext.getCompanyId());
		ddmStructureVersionModel.setUserId(ddmStructureModel.getUserId());
		ddmStructureVersionModel.setUserName(_SAMPLE_USER_NAME);
		ddmStructureVersionModel.setCreateDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		ddmStructureVersionModel.setStructureId(
			ddmStructureModel.getStructureId());
		ddmStructureVersionModel.setVersion(
			DDMStructureConstants.VERSION_DEFAULT);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(ddmStructureModel.getStructureKey());
		sb.append("</name></root>");

		ddmStructureVersionModel.setName(sb.toString());

		ddmStructureVersionModel.setDefinition(
			ddmStructureModel.getDefinition());
		ddmStructureVersionModel.setStorageType(StorageType.JSON.toString());
		ddmStructureVersionModel.setStatusByUserId(
			ddmStructureModel.getUserId());
		ddmStructureVersionModel.setStatusByUserName(_SAMPLE_USER_NAME);
		ddmStructureVersionModel.setStatusDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));

		return ddmStructureVersionModel;
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		ddmTemplateLinkModel.setCompanyId(
			InitDataFactoryContext.getCompanyId());
		ddmTemplateLinkModel.setTemplateLinkId(
			InitDataFactoryContext.getCounter().get());
		ddmTemplateLinkModel.setClassNameId(
			InitDataFactoryUtil.getClassNameId(
					JournalArticle.class,
					InitDataFactoryContext.getClassNameModels()));
		ddmTemplateLinkModel.setClassPK(journalArticleModel.getId());
		ddmTemplateLinkModel.setTemplateId(templateId);

		return ddmTemplateLinkModel;
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

		dlFileEntryMetadataModel.setUuid(SequentialUUID.generate());
		dlFileEntryMetadataModel.setFileEntryMetadataId(
			InitDataFactoryContext.getCounter().get());
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

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			InitDataFactoryContext.getMaxDLFileEntryCount());

		for (int i =
		 1; i <= InitDataFactoryContext.getMaxDLFileEntryCount(); i++) {

			dlFileEntryModels.add(InitDataFactoryUtil.newDlFileEntryModel(
					dlFolerModel, i, InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId(),
					InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
					InitDataFactoryContext.getFutureDateCounter(),
					InitDataFactoryContext.getMaxDLFileEntrySize()));
		}

		return dlFileEntryModels;
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		dlFileVersionModel.setUuid(SequentialUUID.generate());
		dlFileVersionModel.setFileVersionId(
			InitDataFactoryContext.getCounter().get());
		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());
		dlFileVersionModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		dlFileVersionModel.setUserId(InitDataFactoryContext.getSampleUserId());
		dlFileVersionModel.setUserName(_SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		dlFileVersionModel.setModifiedDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
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
					InitDataFactoryContext.getFutureDateCounter()));

		return dlFileVersionModel;
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		List<DLFolderModel> dlFolderModels = new ArrayList<>(
			InitDataFactoryContext.getMaxDLFolderCount());

		for (int i =
		 1; i <= InitDataFactoryContext.getMaxDLFolderCount(); i++) {

			dlFolderModels.add(InitDataFactoryUtil.newDLFolderModel(
					groupId, parentFolderId, i,
					InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId(),
					InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
					InitDataFactoryContext.getFutureDateCounter(),
					_defaultDLFileEntryTypeModel));
		}

		return dlFolderModels;
	}

	public GroupModel newGroupModel(UserModel userModel) throws Exception {
		return InitDataFactoryUtil.newGroupModel(
			InitDataFactoryContext.getCounter().get(), InitDataFactoryUtil.getClassNameId(
			User.class,
			InitDataFactoryContext.getClassNameModels()), userModel.getUserId(),
			userModel.getScreenName(), false,
			InitDataFactoryContext.getCompanyId(),
			InitDataFactoryContext.getSampleUserId());
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
			InitDataFactoryContext.getCounter().get());
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
		journalArticleModel.setId(InitDataFactoryContext.getCounter().get());
		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());
		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());
		journalArticleModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		journalArticleModel.setUserId(InitDataFactoryContext.getSampleUserId());
		journalArticleModel.setUserName(_SAMPLE_USER_NAME);
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
				InitDataFactoryContext.getJournalArticleContent());
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDDMStructureKey(
			_defaultJournalDDMStructureModel.getStructureKey());
		journalArticleModel.setDDMTemplateKey(
			_defaultJournalDDMTemplateModel.getTemplateKey());
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
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
		journalArticleResourceModel.setResourcePrimKey(
			InitDataFactoryContext.getCounter().get());
		journalArticleResourceModel.setGroupId(groupId);
		journalArticleResourceModel.setArticleId(
			String.valueOf(InitDataFactoryContext.getCounter().get()));

		_journalArticleResourceUUIDs.put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		JournalContentSearchModel journalContentSearchModel =
			new JournalContentSearchModelImpl();

		journalContentSearchModel.setContentSearchId(
			InitDataFactoryContext.getCounter().get());
		journalContentSearchModel.setGroupId(journalArticleModel.getGroupId());
		journalContentSearchModel.setCompanyId(
			InitDataFactoryContext.getCompanyId());
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
			InitDataFactoryContext.getCounter().get());
		layoutFriendlyURLModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLModel.setCompanyId(
			InitDataFactoryContext.getCompanyId());
		layoutFriendlyURLModel.setUserId(
			InitDataFactoryContext.getSampleUserId());
		layoutFriendlyURLModel.setUserName(_SAMPLE_USER_NAME);
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
		layoutModel.setPlid(InitDataFactoryContext.getCounter().get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		layoutModel.setUserId(InitDataFactoryContext.getSampleUserId());
		layoutModel.setUserName(_SAMPLE_USER_NAME);
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

		layoutSetModels.add(InitDataFactoryUtil.newLayoutSetModel(
				groupId, true, 0, InitDataFactoryContext.getCounter().get(),
				InitDataFactoryContext.getCompanyId()));
		layoutSetModels.add(
			InitDataFactoryUtil.newLayoutSetModel(groupId, false, publicLayoutSetPageCount,
					InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId()));

		return layoutSetModels;
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		List<MBCategoryModel> mbCategoryModels = new ArrayList<>(
			InitDataFactoryContext.getMaxMBCategoryCount());

		for (int i =
		 1; i <= InitDataFactoryContext.getMaxMBCategoryCount(); i++) {

			mbCategoryModels.add(InitDataFactoryUtil.newMBCategoryModel(
					groupId, i, InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId(),
					InitDataFactoryContext.getSampleUserId(), _SAMPLE_USER_NAME,
					InitDataFactoryContext.getMaxMBThreadCount(),
					InitDataFactoryContext.getMaxMBMessageCount()));
		}

		return mbCategoryModels;
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		MBDiscussionModel mbDiscussionModel = new MBDiscussionModelImpl();

		mbDiscussionModel.setUuid(SequentialUUID.generate());
		mbDiscussionModel.setDiscussionId(
			InitDataFactoryContext.getCounter().get());
		mbDiscussionModel.setGroupId(groupId);
		mbDiscussionModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		mbDiscussionModel.setUserId(InitDataFactoryContext.getSampleUserId());
		mbDiscussionModel.setUserName(_SAMPLE_USER_NAME);
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
			InitDataFactoryContext.getCounter().get());
		mbMailingListModel.setGroupId(mbCategoryModel.getGroupId());
		mbMailingListModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		mbMailingListModel.setUserId(InitDataFactoryContext.getSampleUserId());
		mbMailingListModel.setUserName(_SAMPLE_USER_NAME);
		mbMailingListModel.setCreateDate(new Date());
		mbMailingListModel.setModifiedDate(new Date());
		mbMailingListModel.setCategoryId(mbCategoryModel.getCategoryId());
		mbMailingListModel.setInProtocol("pop3");
		mbMailingListModel.setInServerPort(110);
		mbMailingListModel.setInUserName(
				InitDataFactoryContext.getSampleUserModel().getEmailAddress());
		mbMailingListModel.setInPassword(
				InitDataFactoryContext.getSampleUserModel().getPassword());
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
			messageId = InitDataFactoryContext.getCounter().get();
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

		List<MBMessageModel> mbMessageModels = new ArrayList<>(
			InitDataFactoryContext.getMaxMBMessageCount());

		mbMessageModels.add(
			newMBMessageModel(
				mbThreadModel.getGroupId(), 0, 0, mbThreadModel.getCategoryId(),
				mbThreadModel.getThreadId(), mbThreadModel.getRootMessageId(),
				mbThreadModel.getRootMessageId(),
				MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, "Test Message 1",
				"This is test message 1."));

		for (int i =
		 2; i <= InitDataFactoryContext.getMaxMBMessageCount(); i++) {

			mbMessageModels.add(
				newMBMessageModel(
					mbThreadModel.getGroupId(), 0, 0,
					mbThreadModel.getCategoryId(), mbThreadModel.getThreadId(),
					InitDataFactoryContext.getCounter().get(),
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
		MBStatsUserModel mbStatsUserModel = new MBStatsUserModelImpl();

		mbStatsUserModel.setStatsUserId(
			InitDataFactoryContext.getCounter().get());
		mbStatsUserModel.setGroupId(groupId);
		mbStatsUserModel.setUserId(InitDataFactoryContext.getSampleUserId());
		mbStatsUserModel.setMessageCount(
			InitDataFactoryContext.getMaxMBCategoryCount() * InitDataFactoryContext.getMaxMBThreadCount() * InitDataFactoryContext.getMaxMBMessageCount());
		mbStatsUserModel.setLastPostDate(new Date());

		return mbStatsUserModel;
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {
		MBThreadFlagModel mbThreadFlagModel = new MBThreadFlagModelImpl();

		mbThreadFlagModel.setUuid(SequentialUUID.generate());
		mbThreadFlagModel.setThreadFlagId(
			InitDataFactoryContext.getCounter().get());
		mbThreadFlagModel.setGroupId(mbThreadModel.getGroupId());
		mbThreadFlagModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		mbThreadFlagModel.setUserId(InitDataFactoryContext.getSampleUserId());
		mbThreadFlagModel.setUserName(_SAMPLE_USER_NAME);
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
			InitDataFactoryContext.getMaxMBThreadCount());

		for (int i = 0; i < InitDataFactoryContext.getMaxMBThreadCount(); i++) {
			mbThreadModels.add(
				newMBThreadModel(
					InitDataFactoryContext.getCounter().get(),
					mbCategoryModel.getGroupId(),
					mbCategoryModel.getCategoryId(),
					InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getMaxMBMessageCount()));
		}

		return mbThreadModels;
	}

	public <K, V> ObjectValuePair<K, V> newObjectValuePair(K key, V value) {
		return new ObjectValuePair<>(key, value);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		if (currentIndex == 1) {
			return newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		SimpleCounter counter = _assetPublisherQueryCounter.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetPublisherQueryCounter.put(groupId, counter);
		}

		String[] assetPublisherQueryValues = null;

		if (InitDataFactoryContext.getAssetPublisherQueryName().equals(
				"assetCategories")) {

			List<AssetCategoryModel> assetCategoryModels =
				InitDataFactoryContext.getAssetCategoryModelsArray()[(int)groupId - 1];

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			assetPublisherQueryValues =
				InitDataFactoryUtil.getAssetPublisherAssetCategoriesQueryValues(
					assetCategoryModels, (int)counter.get(),
					InitDataFactoryContext.getMaxAssetEntryToAssetCategoryCount());
		}
		else {
			List<AssetTagModel> assetTagModels =
				InitDataFactoryContext.getAssetTagModelsArray()[(int)groupId - 1];

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			assetPublisherQueryValues =
					InitDataFactoryUtil.getAssetPublisherAssetTagsQueryValues(
							assetTagModels, (int)counter.get(),
							InitDataFactoryContext.getMaxAssetEntryToAssetTagCount());
		}

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)InitDataFactoryContext.getDefaultAssetPublisherPortletPreference().clone();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue(
			"queryName0", InitDataFactoryContext.getAssetPublisherQueryName());
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue(
			"queryName1", InitDataFactoryContext.getAssetPublisherQueryName());
		jxPortletPreferences.setValue(
			"queryValues1", assetPublisherQueryValues[3]);

		return newPortletPreferencesModel(
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

		return newPortletPreferencesModel(
			plid, portletId,
			_portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return newPortletPreferencesModel(
			plid, portletId,
			_portletPreferencesFactory.toXML(jxPortletPreferences));
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
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetTagModel assetTagModel) {

		return newResourcePermissionModels(
			AssetTag.class.getName(), String.valueOf(assetTagModel.getTagId()),
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() ==
				InitDataFactoryContext.getDefaultUserId()) {

			return Collections.singletonList(
				InitDataFactoryUtil.newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					_ownerRoleModel.getRoleId(),
					InitDataFactoryContext.getDefaultUserId(),
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()),
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				_ownerRoleModel.getRoleId(),
				InitDataFactoryContext.getDefaultUserId(),
				InitDataFactoryContext.getResourcePermissionCounter().get(),
				InitDataFactoryContext.getCompanyId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = InitDataFactoryUtil.getResourcePermissionModelName(
			DDMStructure.class.getName(),
			InitDataFactoryUtil.getClassName(ddmStructureModel.getClassNameId(),
				InitDataFactoryContext.getClassNameModels()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _guestRoleModel.getRoleId(),
				0,
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _ownerRoleModel.getRoleId(),
				ddmStructureModel.getUserId(),
				InitDataFactoryContext.getResourcePermissionCounter().get(),
				InitDataFactoryContext.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _userRoleModel.getRoleId(),
				0,
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = InitDataFactoryUtil.getResourcePermissionModelName(
			DDMTemplate.class.getName(), InitDataFactoryUtil.getClassName(
					ddmTemplateModel.getResourceClassNameId(),
					InitDataFactoryContext.getClassNameModels()));
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _guestRoleModel.getRoleId(),
				0,
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _ownerRoleModel.getRoleId(),
				ddmTemplateModel.getUserId(),
				InitDataFactoryContext.getResourcePermissionCounter().get(),
				InitDataFactoryContext.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _userRoleModel.getRoleId(),
				0,
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()),
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()),
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				_ownerRoleModel.getRoleId(),
				InitDataFactoryContext.getSampleUserId(),
				InitDataFactoryContext.getResourcePermissionCounter().get(),
				InitDataFactoryContext.getCompanyId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			InitDataFactoryContext.getSampleUserId());
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
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				MBMessage.class.getName(),
				String.valueOf(mbMessageModel.getMessageId()),
				_ownerRoleModel.getRoleId(),
				InitDataFactoryContext.getSampleUserId(),
				InitDataFactoryContext.getResourcePermissionCounter().get(),
				InitDataFactoryContext.getCompanyId()));
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
				_ownerRoleModel.getRoleId(),
				InitDataFactoryContext.getSampleUserId(),
				InitDataFactoryContext.getResourcePermissionCounter().get(),
				InitDataFactoryContext.getCompanyId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey),
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			InitDataFactoryUtil.newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				_ownerRoleModel.getRoleId(), userModel.getUserId(),
				InitDataFactoryContext.getResourcePermissionCounter().get(),
				InitDataFactoryContext.getCompanyId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		return newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			InitDataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		return newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()),
			InitDataFactoryContext.getSampleUserId());
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel) {

		return newSocialActivityModel(
			blogsEntryModel.getGroupId(), InitDataFactoryUtil.getClassNameId(
			BlogsEntry.class,
			InitDataFactoryContext.getClassNameModels()), blogsEntryModel.getEntryId(),
			BlogsActivityKeys.ADD_ENTRY,
			"{\"title\":\""+ blogsEntryModel.getTitle() +"\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel) {

		return newSocialActivityModel(
			dlFileEntryModel.getGroupId(), InitDataFactoryUtil.getClassNameId(
			DLFileEntry.class, InitDataFactoryContext.getClassNameModels()),
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
					InitDataFactoryContext.getClassNameModels()),
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
				WikiPage.class, InitDataFactoryContext.getClassNameModels())) {
			extraData = "{\"version\":1}";

			type = WikiActivityKeys.ADD_PAGE;
		}
		else if (classNameId == 0) {
			extraData = "{\"title\":\"" + mbMessageModel.getSubject() + "\"}";

			type = MBActivityKeys.ADD_MESSAGE;

			classNameId = InitDataFactoryUtil.getClassNameId(
					MBMessage.class,
					InitDataFactoryContext.getClassNameModels());
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
			BlogsEntry.class, InitDataFactoryContext.getClassNameModels()),
			blogsEntryModel.getEntryId());
	}

	public SubscriptionModel newSubscriptionModel(MBThreadModel mBThreadModel) {
		return newSubscriptionModel(
			InitDataFactoryUtil.getClassNameId(MBThread.class, InitDataFactoryContext.getClassNameModels()),
			mBThreadModel.getThreadId());
	}

	public SubscriptionModel newSubscriptionModel(WikiPageModel wikiPageModel) {
		return newSubscriptionModel(
			InitDataFactoryUtil.getClassNameId(WikiPage.class, InitDataFactoryContext.getClassNameModels()),
			wikiPageModel.getResourcePrimKey());
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			InitDataFactoryContext.getMaxUserCount());

		for (int i = 0; i < InitDataFactoryContext.getMaxUserCount(); i++) {
			String[] userName = nextUserName(i);
			userModels.add(
				InitDataFactoryUtil.newUserModel(
					InitDataFactoryContext.getCounter().get(), userName[0],
					userName[1],
					"test" + InitDataFactoryContext.getUserScreenNameCounter().get(),
					false, InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId()));
		}

		return userModels;
	}

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		List<WikiNodeModel> wikiNodeModels = new ArrayList<>(
			InitDataFactoryContext.getMaxWikiNodeCount());

		for (int i =
		 1; i <= InitDataFactoryContext.getMaxWikiNodeCount(); i++) {

			wikiNodeModels.add(InitDataFactoryUtil.newWikiNodeModel(groupId, i,
					InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId(),
					InitDataFactoryContext.getSampleUserId(),
					_SAMPLE_USER_NAME));
		}

		return wikiNodeModels;
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel) {
		List<WikiPageModel> wikiPageModels = new ArrayList<>(
			InitDataFactoryContext.getMaxWikiPageCount());

		for (int i =
		 1; i <= InitDataFactoryContext.getMaxWikiPageCount(); i++) {

			wikiPageModels.add(InitDataFactoryUtil.newWikiPageModel(
					wikiNodeModel, i, InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCounter().get(),
					InitDataFactoryContext.getCompanyId(),
					InitDataFactoryContext.getSampleUserId(),
					_SAMPLE_USER_NAME));
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

	public String[] nextUserName(long index) {
		String[] userName = new String[2];

		userName[0] = InitDataFactoryContext.getFirstNames().get(
			(int)(index / InitDataFactoryContext.getLastNames().size()) % InitDataFactoryContext.getFirstNames().size());
		userName[1] = InitDataFactoryContext.getLastNames().get((int)(index % InitDataFactoryContext.getLastNames().size()));

		return userName;
	}

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		assetEntryModel.setEntryId(InitDataFactoryContext.getCounter().get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		assetEntryModel.setUserId(InitDataFactoryContext.getSampleUserId());
		assetEntryModel.setUserName(_SAMPLE_USER_NAME);
		assetEntryModel.setCreateDate(createDate);
		assetEntryModel.setModifiedDate(modifiedDate);
		assetEntryModel.setClassNameId(classNameId);
		assetEntryModel.setClassPK(classPK);
		assetEntryModel.setClassUuid(uuid);
		assetEntryModel.setClassTypeId(classTypeId);
		assetEntryModel.setListable(listable);
		assetEntryModel.setVisible(visible);
		assetEntryModel.setStartDate(createDate);
		assetEntryModel.setEndDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	protected DDMContentModel newDDMContentModel(
		long contentId, long groupId, String data) {

		DDMContentModel ddmContentModel = new DDMContentModelImpl();

		ddmContentModel.setUuid(SequentialUUID.generate());
		ddmContentModel.setContentId(contentId);
		ddmContentModel.setGroupId(groupId);
		ddmContentModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		ddmContentModel.setUserId(InitDataFactoryContext.getSampleUserId());
		ddmContentModel.setUserName(_SAMPLE_USER_NAME);
		ddmContentModel.setCreateDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		ddmContentModel.setModifiedDate(
				InitDataFactoryUtil.nextFutureDate(
					InitDataFactoryContext.getFutureDateCounter()));
		ddmContentModel.setName(DDMStorageLink.class.getName());
		ddmContentModel.setData(data);

		return ddmContentModel;
	}

	protected DDMStructureLinkModel newDDMStructureLinkModel(
		long classNameId, long classPK, long structureId) {

		DDMStructureLinkModel ddmStructureLinkModel =
			new DDMStructureLinkModelImpl();

		ddmStructureLinkModel.setStructureLinkId(
			InitDataFactoryContext.getCounter().get());
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
		mBMessageModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		mBMessageModel.setUserId(InitDataFactoryContext.getSampleUserId());
		mBMessageModel.setUserName(_SAMPLE_USER_NAME);
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
		mbThreadModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		mbThreadModel.setUserId(InitDataFactoryContext.getSampleUserId());
		mbThreadModel.setUserName(_SAMPLE_USER_NAME);
		mbThreadModel.setCreateDate(new Date());
		mbThreadModel.setModifiedDate(new Date());
		mbThreadModel.setCategoryId(categoryId);
		mbThreadModel.setRootMessageId(rootMessageId);
		mbThreadModel.setRootMessageUserId(
			InitDataFactoryContext.getSampleUserId());
		mbThreadModel.setMessageCount(messageCount);
		mbThreadModel.setLastPostByUserId(
			InitDataFactoryContext.getSampleUserId());
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
			InitDataFactoryContext.getCounter().get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	protected List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _guestRoleModel.getRoleId(),
				0,
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _ownerRoleModel.getRoleId(),
				ownerId,
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));
		resourcePermissionModels.add(
			InitDataFactoryUtil.newResourcePermissionModel(
				name, primKey, _siteMemberRoleModel.getRoleId(),
				0,
					InitDataFactoryContext.getResourcePermissionCounter().get(),
					InitDataFactoryContext.getCompanyId()));

		return resourcePermissionModels;
	}

	protected SocialActivityModel newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		socialActivityModel.setActivityId(
			InitDataFactoryContext.getSocialActivityCounter().get());
		socialActivityModel.setGroupId(groupId);
		socialActivityModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		socialActivityModel.setUserId(InitDataFactoryContext.getSampleUserId());
		socialActivityModel.setCreateDate(
			_CURRENT_TIME + InitDataFactoryContext.getTimeCounter().get());
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
			InitDataFactoryContext.getCounter().get());
		subscriptionModel.setCompanyId(InitDataFactoryContext.getCompanyId());
		subscriptionModel.setUserId(InitDataFactoryContext.getSampleUserId());
		subscriptionModel.setUserName(_SAMPLE_USER_NAME);
		subscriptionModel.setCreateDate(new Date());
		subscriptionModel.setModifiedDate(new Date());
		subscriptionModel.setClassNameId(classNameId);
		subscriptionModel.setClassPK(classPK);
		subscriptionModel.setFrequency(SubscriptionConstants.FREQUENCY_INSTANT);

		return subscriptionModel;
	}

	private static final long _CURRENT_TIME = System.currentTimeMillis();

	private static final String _SAMPLE_USER_NAME = "Sample";

	private static final PortletPreferencesFactory _portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();

	private RoleModel _administratorRoleModel;
	private final Map<Long, SimpleCounter> _assetCategoryCounters =
		new HashMap<>();
	private final Map<Long, SimpleCounter> _assetPublisherQueryCounter =
		new HashMap<>();
	private final Map<Long, SimpleCounter> _assetTagCounters = new HashMap<>();
	private final Class<?> _clazz = getClass();
	private DDMStructureLayoutModel _defaultDLDDMStructureLayoutModel;
	private DDMStructureModel _defaultDLDDMStructureModel;
	private DDMStructureVersionModel _defaultDLDDMStructureVersionModel;
	private DLFileEntryTypeModel _defaultDLFileEntryTypeModel;
	private DDMStructureLayoutModel _defaultJournalDDMStructureLayoutModel;
	private DDMStructureModel _defaultJournalDDMStructureModel;
	private DDMStructureVersionModel _defaultJournalDDMStructureVersionModel;
	private DDMTemplateModel _defaultJournalDDMTemplateModel;
	private RoleModel _guestRoleModel;
	private final Map<Long, String> _journalArticleResourceUUIDs =
		new HashMap<>();
	private final Map<Long, SimpleCounter> _layoutCounters = new HashMap<>();
	private RoleModel _ownerRoleModel;
	private RoleModel _powerUserRoleModel;
	private List<RoleModel> _roleModels;
	private RoleModel _siteMemberRoleModel;
	private RoleModel _userRoleModel;
}