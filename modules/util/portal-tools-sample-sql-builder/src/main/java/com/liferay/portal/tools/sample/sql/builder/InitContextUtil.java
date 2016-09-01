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

import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiPage;

import java.io.IOException;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class InitContextUtil {

	public static long getAccountId() {
		return _accountId;
	}

	public static AccountModel getAccountModel() {
		return _accountModel;
	}

	public static RoleModel getAdministratorRoleModel() {
		return _administratorRoleModel;
	}

	public static List<AssetCategoryModel>[] getAssetCategoryModelsArray() {
		return _assetCategoryModelsArray;
	}

	public static String getAssetPublisherQueryName() {
		return _assetPublisherQueryName;
	}

	public static List<AssetTagModel>[] getAssetTagModelsArray() {
		return _assetTagModelsArray;
	}

	public static List<AssetTagStatsModel>[] getAssetTagStatsModelsArray() {
		return _assetTagStatsModelsArray;
	}

	public static List<AssetVocabularyModel>[] getAssetVocabularyModelsArray() {
		return _assetVocabularyModelsArray;
	}

	public static Map<String, ClassNameModel> getClassNameModels() {
		return _classNameModels;
	}

	public static long getCompanyId() {
		return _companyId;
	}

	public static CompanyModel getCompanyModel() {
		return _companyModel;
	}

	public static SimpleCounter getCounter() {
		return _counter;
	}

	public static PortletPreferencesImpl
		getDefaultAssetPublisherPortletPreference() {

		return _defaultAssetPublisherPortletPreference;
	}

	public static AssetVocabularyModel getDefaultAssetVocabularyModel() {
		return _defaultAssetVocabularyModel;
	}

	public static DDMStructureLayoutModel
		getDefaultDLDDMStructureLayoutModel() {

		return _defaultDLDDMStructureLayoutModel;
	}

	public static DDMStructureModel getDefaultDLDDMStructureModel() {
		return _defaultDLDDMStructureModel;
	}

	public static DDMStructureVersionModel
		getDefaultDLDDMStructureVersionModel() {

		return _defaultDLDDMStructureVersionModel;
	}

	public static DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return _defaultDLFileEntryTypeModel;
	}

	public static DDMStructureLayoutModel
		getDefaultJournalDDMStructureLayoutModel() {

		return _defaultJournalDDMStructureLayoutModel;
	}

	public static DDMStructureModel getDefaultJournalDDMStructureModel() {
		return _defaultJournalDDMStructureModel;
	}

	public static DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return _defaultJournalDDMStructureVersionModel;
	}

	public static DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return _defaultJournalDDMTemplateModel;
	}

	public static long getDefaultUserId() {
		return _defaultUserId;
	}

	public static UserModel getDefaultUserModel() {
		return _defaultUserModel;
	}

	public static String getDlDDMStructureContent() {
		return _dlDDMStructureContent;
	}

	public static String getDlDDMStructureLayoutContent() {
		return _dlDDMStructureLayoutContent;
	}

	public static List<String> getFirstNames() {
		return _firstNames;
	}

	public static SimpleCounter getFutureDateCounter() {
		return _futureDateCounter;
	}

	public static long getGlobalGroupId() {
		return _globalGroupId;
	}

	public static GroupModel getGlobalGroupModel() {
		return _globalGroupModel;
	}

	public static List<GroupModel> getGroupModels() {
		return _groupModels;
	}

	public static long getGuestGroupId() {
		return _guestGroupId;
	}

	public static GroupModel getGuestGroupModel() {
		return _guestGroupModel;
	}

	public static RoleModel getGuestRoleModel() {
		return _guestRoleModel;
	}

	public static UserModel getGuestUserModel() {
		return _guestUserModel;
	}

	public static String getJournalArticleContent() {
		return _journalArticleContent;
	}

	public static String getJournalDDMStructureContent() {
		return _journalDDMStructureContent;
	}

	public static String getJournalDDMStructureLayoutContent() {
		return _journalDDMStructureLayoutContent;
	}

	public static List<String> getLastNames() {
		return _lastNames;
	}

	public static int getMaxAssetCategoryCount() {
		return _maxAssetCategoryCount;
	}

	public static int getMaxAssetEntryToAssetCategoryCount() {
		return _maxAssetEntryToAssetCategoryCount;
	}

	public static int getMaxAssetEntryToAssetTagCount() {
		return _maxAssetEntryToAssetTagCount;
	}

	public static int getMaxAssetPublisherPageCount() {
		return _maxAssetPublisherPageCount;
	}

	public static int getMaxAssetTagCount() {
		return _maxAssetTagCount;
	}

	public static int getMaxAssetVocabularyCount() {
		return _maxAssetVocabularyCount;
	}

	public static int getMaxBlogsEntryCommentCount() {
		return _maxBlogsEntryCommentCount;
	}

	public static int getMaxBlogsEntryCount() {
		return _maxBlogsEntryCount;
	}

	public static int getMaxDDLCustomFieldCount() {
		return _maxDDLCustomFieldCount;
	}

	public static int getMaxDDLRecordCount() {
		return _maxDDLRecordCount;
	}

	public static int getMaxDDLRecordSetCount() {
		return _maxDDLRecordSetCount;
	}

	public static int getMaxDLFileEntryCount() {
		return _maxDLFileEntryCount;
	}

	public static int getMaxDLFileEntrySize() {
		return _maxDLFileEntrySize;
	}

	public static int getMaxDLFolderCount() {
		return _maxDLFolderCount;
	}

	public static int getMaxDLFolderDepth() {
		return _maxDLFolderDepth;
	}

	public static int getMaxGroupsCount() {
		return _maxGroupsCount;
	}

	public static int getMaxJournalArticleCount() {
		return _maxJournalArticleCount;
	}

	public static int getMaxJournalArticlePageCount() {
		return _maxJournalArticlePageCount;
	}

	public static int getMaxJournalArticleVersionCount() {
		return _maxJournalArticleVersionCount;
	}

	public static int getMaxMBCategoryCount() {
		return _maxMBCategoryCount;
	}

	public static int getMaxMBMessageCount() {
		return _maxMBMessageCount;
	}

	public static int getMaxMBThreadCount() {
		return _maxMBThreadCount;
	}

	public static int getMaxUserCount() {
		return _maxUserCount;
	}

	public static int getMaxUserToGroupCount() {
		return _maxUserToGroupCount;
	}

	public static int getMaxWikiNodeCount() {
		return _maxWikiNodeCount;
	}

	public static int getMaxWikiPageCommentCount() {
		return _maxWikiPageCommentCount;
	}

	public static int getMaxWikiPageCount() {
		return _maxWikiPageCount;
	}

	public static RoleModel getOwnerRoleModel() {
		return _ownerRoleModel;
	}

	public static RoleModel getPowerUserRoleModel() {
		return _powerUserRoleModel;
	}

	public static SimpleCounter getResourcePermissionCounter() {
		return _resourcePermissionCounter;
	}

	public static List<RoleModel> getRoleModels() {
		return _roleModels;
	}

	public static long getSampleUserId() {
		return _sampleUserId;
	}

	public static UserModel getSampleUserModel() {
		return _sampleUserModel;
	}

	public static Format getSimpleDateFormat() {
		return _simpleDateFormat;
	}

	public static RoleModel getSiteMemberRoleModel() {
		return _siteMemberRoleModel;
	}

	public static SimpleCounter getSocialActivityCounter() {
		return _socialActivityCounter;
	}

	public static SimpleCounter getTimeCounter() {
		return _timeCounter;
	}

	public static RoleModel getUserRoleModel() {
		return _userRoleModel;
	}

	public static SimpleCounter getUserScreenNameCounter() {
		return _userScreenNameCounter;
	}

	public static VirtualHostModel getVirtualHostModel() {
		return _virtualHostModel;
	}

	public static void initAssetCategoryModels(String userName) {
		_assetCategoryModelsArray =
			(List<AssetCategoryModel>[])new List<?>[_maxGroupsCount];
		_assetVocabularyModelsArray =
			(List<AssetVocabularyModel>[])new List<?>[_maxGroupsCount];
		_defaultAssetVocabularyModel =
			InitDataFactoryUtil.newAssetVocabularyModel(
				_globalGroupId, _defaultUserId, null,
				PropsValues.ASSET_VOCABULARY_DEFAULT, _counter.get(),
				_companyId);

		StringBundler sb = new StringBundler(4);

		for (int i = 1; i <= _maxGroupsCount; i++) {
			List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>(
				_maxAssetVocabularyCount);
			List<AssetCategoryModel> assetCategoryModels = new ArrayList<>(
				_maxAssetVocabularyCount * _maxAssetCategoryCount);

			long lastRightCategoryId = 2;

			for (int j = 0; j < _maxAssetVocabularyCount; j++) {
				sb.setIndex(0);

				sb.append("TestVocabulary_");
				sb.append(i);
				sb.append(StringPool.UNDERLINE);
				sb.append(j);

				AssetVocabularyModel assetVocabularyModel =
					InitDataFactoryUtil.newAssetVocabularyModel(
						i, _sampleUserId, userName, sb.toString(),
						_counter.get(), _companyId);

				assetVocabularyModels.add(assetVocabularyModel);

				for (int k = 0; k < _maxAssetCategoryCount; k++) {
					sb.setIndex(0);

					sb.append("TestCategory_");
					sb.append(assetVocabularyModel.getVocabularyId());
					sb.append(StringPool.UNDERLINE);
					sb.append(k);

					AssetCategoryModel assetCategoryModel =
						InitDataFactoryUtil.newAssetCategoryModel(
							i, lastRightCategoryId, sb.toString(),
							assetVocabularyModel.getVocabularyId(),
							_counter.get(), _companyId, _sampleUserId,
							userName);

					lastRightCategoryId += 2;

					assetCategoryModels.add(assetCategoryModel);
				}
			}

			_assetCategoryModelsArray[i - 1] = assetCategoryModels;
			_assetVocabularyModelsArray[i - 1] = assetVocabularyModels;
		}
	}

	public static void initAssetTagModels(String userName) {
		_assetTagModelsArray =
			(List<AssetTagModel>[])new List<?>[_maxGroupsCount];
		_assetTagStatsModelsArray =
			(List<AssetTagStatsModel>[])new List<?>[_maxGroupsCount];

		for (int i = 1; i <= _maxGroupsCount; i++) {
			List<AssetTagModel> assetTagModels = new ArrayList<>(
				_maxAssetTagCount);
			List<AssetTagStatsModel> assetTagStatsModels = new ArrayList<>(
				_maxAssetTagCount * 3);

			for (int j = 0; j < _maxAssetTagCount; j++) {
				AssetTagModel assetTagModel = new AssetTagModelImpl();

				assetTagModel.setUuid(SequentialUUID.generate());
				assetTagModel.setTagId(_counter.get());
				assetTagModel.setGroupId(i);
				assetTagModel.setCompanyId(_companyId);
				assetTagModel.setUserId(_sampleUserId);
				assetTagModel.setUserName(userName);
				assetTagModel.setCreateDate(new Date());
				assetTagModel.setModifiedDate(new Date());
				assetTagModel.setName("TestTag_" + i + "_" + j);
				assetTagModel.setLastPublishDate(new Date());

				assetTagModels.add(assetTagModel);

				AssetTagStatsModel assetTagStatsModel =
					InitDataFactoryUtil.newAssetTagStatsModel(
						assetTagModel.getTagId(),
						InitDataFactoryUtil.getClassNameId(
							BlogsEntry.class, _classNameModels),
						_counter.get());

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = InitDataFactoryUtil.newAssetTagStatsModel(
					assetTagModel.getTagId(),
					InitDataFactoryUtil.getClassNameId(
						JournalArticle.class, _classNameModels),
					_counter.get());

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = InitDataFactoryUtil.newAssetTagStatsModel(
					assetTagModel.getTagId(),
					InitDataFactoryUtil.getClassNameId(
						WikiPage.class, _classNameModels),
					_counter.get());

				assetTagStatsModels.add(assetTagStatsModel);
			}

			_assetTagModelsArray[i - 1] = assetTagModels;
			_assetTagStatsModelsArray[i - 1] = assetTagStatsModels;
		}
	}

	public static void initCompanyModels() {
		_companyModel = InitDataFactoryUtil.initCompanyModel(
			_companyId, _accountId);
		_accountModel = InitDataFactoryUtil.initAccountModel(
			_companyId, _accountId);
	}

	public static void initContext(Properties properties) {
		String timeZoneId = properties.getProperty("sample.sql.db.time.zone");

		if (Validator.isNotNull(timeZoneId)) {
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

			if (timeZone != null) {
				TimeZone.setDefault(timeZone);

				_simpleDateFormat =
					FastDateFormatFactoryUtil.getSimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", timeZone);
			}
		}

		_assetPublisherQueryName = GetterUtil.getString(
			properties.getProperty("sample.sql.asset.publisher.query.name"));

		if (!_assetPublisherQueryName.equals("assetCategories")) {
			_assetPublisherQueryName = "assetTags";
		}

		_maxAssetCategoryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.asset.category.count"));
		_maxAssetEntryToAssetCategoryCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.asset.entry.to.asset.category.count"));
		_maxAssetEntryToAssetTagCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.asset.entry.to.asset.tag.count"));
		_maxAssetPublisherPageCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.asset.publisher.page.count"));
		_maxAssetTagCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.asset.tag.count"));
		_maxAssetVocabularyCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.asset.vocabulary.count"));
		_maxBlogsEntryCommentCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.blogs.entry.comment.count"));
		_maxBlogsEntryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.blogs.entry.count"));
		_maxDDLCustomFieldCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.ddl.custom.field.count"));
		_maxDDLRecordCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.ddl.record.count"));
		_maxDDLRecordSetCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.ddl.record.set.count"));
		_maxDLFileEntryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.file.entry.count"));
		_maxDLFileEntrySize = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.file.entry.size"));
		_maxDLFolderCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.folder.count"));
		_maxDLFolderDepth = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.dl.folder.depth"));
		_maxGroupsCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.group.count"));
		_maxJournalArticleCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.journal.article.count"));
		_maxJournalArticlePageCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.journal.article.page.count"));
		_maxJournalArticleVersionCount = GetterUtil.getInteger(
			properties.getProperty(
				"sample.sql.max.journal.article.version.count"));
		_maxMBCategoryCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.mb.category.count"));
		_maxMBMessageCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.mb.message.count"));
		_maxMBThreadCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.mb.thread.count"));
		_maxUserCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.user.count"));
		_maxUserToGroupCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.user.to.group.count"));
		_maxWikiNodeCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.wiki.node.count"));
		_maxWikiPageCommentCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.wiki.page.comment.count"));
		_maxWikiPageCount = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.wiki.page.count"));

		int maxJournalArticleSize = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.journal.article.size"));

		_journalArticleContent = InitDataFactoryUtil.initJournalArticleContent(
			maxJournalArticleSize);

		_virtualHostModel = InitDataFactoryUtil.initVirtualHostModel(
			properties.getProperty("sample.sql.virtual.hostname"),
			_counter.get(), _companyId);
	}

	public static void initDLFileEntryTypeModel(String userName) {
		_defaultDLFileEntryTypeModel = new DLFileEntryTypeModelImpl();

		_defaultDLFileEntryTypeModel.setUuid(SequentialUUID.generate());
		_defaultDLFileEntryTypeModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		_defaultDLFileEntryTypeModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(_futureDateCounter));
		_defaultDLFileEntryTypeModel.setModifiedDate(
			InitDataFactoryUtil.nextFutureDate(_futureDateCounter));
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
			InitDataFactoryUtil.nextFutureDate(_futureDateCounter));

		_defaultDLDDMStructureModel = InitDataFactoryUtil.newDDMStructureModel(
			_globalGroupId, _defaultUserId,
			InitDataFactoryUtil.getClassNameId(
				DLFileEntry.class, _classNameModels),
			RawMetadataProcessor.TIKA_RAW_METADATA, _dlDDMStructureContent,
			_counter.get(), _companyId, userName, _futureDateCounter);

		_defaultDLDDMStructureVersionModel =
			InitDataFactoryUtil.newDDMStructureVersionModel(
				_defaultDLDDMStructureModel, userName);

		_defaultDLDDMStructureLayoutModel =
			InitDataFactoryUtil.newDDMStructureLayoutModel(
				_globalGroupId, _defaultUserId,
				_defaultDLDDMStructureVersionModel.getStructureVersionId(),
				_dlDDMStructureLayoutContent, _counter.get(), _companyId,
				userName, _futureDateCounter);

		_defaultJournalDDMStructureModel =
			InitDataFactoryUtil.newDDMStructureModel(
				_globalGroupId, _defaultUserId,
				InitDataFactoryUtil.getClassNameId(
					JournalArticle.class, _classNameModels),
				"BASIC-WEB-CONTENT", _journalDDMStructureContent,
				_counter.get(), _companyId, userName, _futureDateCounter);

		_defaultJournalDDMStructureVersionModel =
			InitDataFactoryUtil.newDDMStructureVersionModel(
				_defaultJournalDDMStructureModel, userName);

		_defaultJournalDDMStructureLayoutModel =
			InitDataFactoryUtil.newDDMStructureLayoutModel(
				_globalGroupId, _defaultUserId,
				_defaultJournalDDMStructureVersionModel.getStructureVersionId(),
				_journalDDMStructureLayoutContent, _counter.get(), _companyId,
				userName, _futureDateCounter);

		_defaultJournalDDMTemplateModel =
			InitDataFactoryUtil.newDDMTemplateModel(
				_globalGroupId, _defaultUserId,
				_defaultJournalDDMStructureModel.getStructureId(),
				InitDataFactoryUtil.getClassNameId(
					JournalArticle.class, _classNameModels),
				_counter.get(), _companyId, _futureDateCounter,
				_classNameModels, _counter.get(), userName);
	}

	public static void initGroupModels() throws Exception {
		_globalGroupModel = InitDataFactoryUtil.initGroupModel(
			_globalGroupId, InitDataFactoryUtil.getClassNameId(
				Company.class, _classNameModels),
			_companyId, GroupConstants.GLOBAL, false, _companyId,
			_sampleUserId);

		_guestGroupModel = InitDataFactoryUtil.initGroupModel(
			_guestGroupId, InitDataFactoryUtil.getGroupClassNameId(),
			_guestGroupId, GroupConstants.GUEST, true, _companyId,
			_sampleUserId);

		_groupModels = new ArrayList<>(_maxGroupsCount);

		for (int i = 1; i <= _maxGroupsCount; i++) {
			GroupModel groupModel = InitDataFactoryUtil.initGroupModel(
				i, InitDataFactoryUtil.getGroupClassNameId(), i, "Site " + i,
				true, _companyId, _sampleUserId);
			_groupModels.add(groupModel);
		}
	}

	public static void initParameter() {
		_classNameModels = InitDataFactoryUtil.initClassNameModels(_counter);
		_accountId = _counter.get();
		_companyId = _counter.get();
		_defaultUserId = _counter.get();
		_globalGroupId = _counter.get();
		_guestGroupId = _counter.get();
		_sampleUserId = _counter.get();
	}

	public static void initResource(
			Class<?> clazz, PortletPreferencesFactory portletPreferencesFactory)
		throws Exception {

		_dlDDMStructureContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_basic_document.json");
		_dlDDMStructureLayoutContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_layout_basic_document.json");
		_journalDDMStructureContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_basic_web_content.json");
		_journalDDMStructureLayoutContent = InitDataFactoryUtil.getResource(
			clazz, "ddm_structure_layout_basic_web_content.json");

		String defaultAssetPublisherPreference = StringUtil.read(
			InitDataFactoryUtil.getResourceInputStream(
				clazz, "default_asset_publisher_preference.xml"));

		_defaultAssetPublisherPortletPreference =
			(PortletPreferencesImpl)portletPreferencesFactory.fromDefaultXML(
				defaultAssetPublisherPreference);
	}

	public static void initRoleModels(String userName) {
		long classNameId = InitDataFactoryUtil.getClassNameId(
			Role.class, _classNameModels);

		_roleModels = new ArrayList<>();

		// Administrator

		_administratorRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_administratorRoleModel);

		// Guest

		_guestRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.GUEST, RoleConstants.TYPE_REGULAR, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_guestRoleModel);

		// Organization Administrator

		RoleModel organizationAdministratorRoleModel =
			InitDataFactoryUtil.newRoleModel(
				RoleConstants.ORGANIZATION_ADMINISTRATOR,
				RoleConstants.TYPE_ORGANIZATION, _counter.get(), _companyId,
				_sampleUserId, userName, classNameId);

		_roleModels.add(organizationAdministratorRoleModel);

		// Organization Owner

		RoleModel organizationOwnerRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.ORGANIZATION_OWNER, RoleConstants.TYPE_ORGANIZATION,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(organizationOwnerRoleModel);

		// Organization User

		RoleModel organizationUserRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.ORGANIZATION_USER, RoleConstants.TYPE_ORGANIZATION,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(organizationUserRoleModel);

		// Owner

		_ownerRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_ownerRoleModel);

		// Power User

		_powerUserRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_powerUserRoleModel);

		// Site Administrator

		RoleModel siteAdministratorRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(siteAdministratorRoleModel);

		// Site Member

		_siteMemberRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_siteMemberRoleModel);

		// Site Owner

		RoleModel siteOwnerRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(siteOwnerRoleModel);

		// User

		_userRoleModel = InitDataFactoryUtil.newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_userRoleModel);
	}

	public static void initUserModels(String userName) {
		_defaultUserModel = InitDataFactoryUtil.newUserModel(
			_defaultUserId, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, true, _counter.get(), _companyId);

		_guestUserModel = InitDataFactoryUtil.newUserModel(
			_counter.get(), "Test", "Test", "Test", false, _counter.get(),
			_companyId);

		_sampleUserModel = InitDataFactoryUtil.newUserModel(
			_sampleUserId, userName, userName, userName, false, _counter.get(),
			_companyId);
	}

	public static void initUserNames(Class<?> clazz) throws IOException {
		_firstNames = InitDataFactoryUtil.initUserFirstNames(clazz);

		_lastNames = InitDataFactoryUtil.initUserLastNames(clazz);
	}

	private static long _accountId;
	private static AccountModel _accountModel;
	private static RoleModel _administratorRoleModel;
	private static List<AssetCategoryModel>[] _assetCategoryModelsArray;
	private static String _assetPublisherQueryName;
	private static List<AssetTagModel>[] _assetTagModelsArray;
	private static List<AssetTagStatsModel>[] _assetTagStatsModelsArray;
	private static List<AssetVocabularyModel>[] _assetVocabularyModelsArray;
	private static Map<String, ClassNameModel> _classNameModels;
	private static long _companyId;
	private static CompanyModel _companyModel;
	private static final SimpleCounter _counter;
	private static PortletPreferencesImpl
		_defaultAssetPublisherPortletPreference;
	private static AssetVocabularyModel _defaultAssetVocabularyModel;
	private static DDMStructureLayoutModel _defaultDLDDMStructureLayoutModel;
	private static DDMStructureModel _defaultDLDDMStructureModel;
	private static DDMStructureVersionModel _defaultDLDDMStructureVersionModel;
	private static DLFileEntryTypeModel _defaultDLFileEntryTypeModel;
	private static DDMStructureLayoutModel
		_defaultJournalDDMStructureLayoutModel;
	private static DDMStructureModel _defaultJournalDDMStructureModel;
	private static DDMStructureVersionModel
		_defaultJournalDDMStructureVersionModel;
	private static DDMTemplateModel _defaultJournalDDMTemplateModel;
	private static long _defaultUserId;
	private static UserModel _defaultUserModel;
	private static String _dlDDMStructureContent;
	private static String _dlDDMStructureLayoutContent;
	private static List<String> _firstNames;
	private static final SimpleCounter _futureDateCounter;
	private static long _globalGroupId;
	private static GroupModel _globalGroupModel;
	private static List<GroupModel> _groupModels;
	private static long _guestGroupId;
	private static GroupModel _guestGroupModel;
	private static RoleModel _guestRoleModel;
	private static UserModel _guestUserModel;
	private static String _journalArticleContent;
	private static String _journalDDMStructureContent;
	private static String _journalDDMStructureLayoutContent;
	private static List<String> _lastNames;
	private static int _maxAssetCategoryCount;
	private static int _maxAssetEntryToAssetCategoryCount;
	private static int _maxAssetEntryToAssetTagCount;
	private static int _maxAssetPublisherPageCount;
	private static int _maxAssetTagCount;
	private static int _maxAssetVocabularyCount;
	private static int _maxBlogsEntryCommentCount;
	private static int _maxBlogsEntryCount;
	private static int _maxDDLCustomFieldCount;
	private static int _maxDDLRecordCount;
	private static int _maxDDLRecordSetCount;
	private static int _maxDLFileEntryCount;
	private static int _maxDLFileEntrySize;
	private static int _maxDLFolderCount;
	private static int _maxDLFolderDepth;
	private static int _maxGroupsCount;
	private static int _maxJournalArticleCount;
	private static int _maxJournalArticlePageCount;
	private static int _maxJournalArticleVersionCount;
	private static int _maxMBCategoryCount;
	private static int _maxMBMessageCount;
	private static int _maxMBThreadCount;
	private static int _maxUserCount;
	private static int _maxUserToGroupCount;
	private static int _maxWikiNodeCount;
	private static int _maxWikiPageCommentCount;
	private static int _maxWikiPageCount;
	private static RoleModel _ownerRoleModel;
	private static RoleModel _powerUserRoleModel;
	private static final SimpleCounter _resourcePermissionCounter;
	private static List<RoleModel> _roleModels;
	private static long _sampleUserId;
	private static UserModel _sampleUserModel;
	private static Format _simpleDateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static RoleModel _siteMemberRoleModel;
	private static final SimpleCounter _socialActivityCounter;
	private static final SimpleCounter _timeCounter;
	private static RoleModel _userRoleModel;
	private static final SimpleCounter _userScreenNameCounter;
	private static VirtualHostModel _virtualHostModel;

	static {
		_counter = new SimpleCounter(_maxGroupsCount + 1);
		_timeCounter = new SimpleCounter();
		_futureDateCounter = new SimpleCounter();
		_resourcePermissionCounter = new SimpleCounter();
		_socialActivityCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();
	}

}