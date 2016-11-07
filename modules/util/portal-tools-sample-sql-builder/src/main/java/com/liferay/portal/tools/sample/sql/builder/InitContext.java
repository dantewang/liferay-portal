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

import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagStatsModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class InitContext {

	public InitContext(Properties properties) {
		initContext(properties);
		_counter = new SimpleCounter(_maxGroupsCount + 1);
		initMaxJournalArticleSize(properties);
		initVirtualHostModel(properties);
		_timeCounter = new SimpleCounter();
		_futureDateCounter = new SimpleCounter();
		_resourcePermissionCounter = new SimpleCounter();
		_socialActivityCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();
		_assetCategoryCounters = new HashMap<>();
		_portletPreferencesFactory = new PortletPreferencesFactoryImpl();
		_assetPublisherQueryCounter = new HashMap<>();
		_journalArticleResourceUUIDs = new HashMap<>();
		_layoutCounters = new HashMap<>();
	}

	public long getAccountId() {
		return _accountId;
	}

	public AccountModel getAccountModel() {
		return _accountModel;
	}

	public RoleModel getAdministratorRoleModel() {
		return _administratorRoleModel;
	}

	public Map<Long, SimpleCounter> getAssetCategoryCounters() {
		return _assetCategoryCounters;
	}

	public List<AssetCategoryModel>[] getAssetCategoryModelsArray() {
		return _assetCategoryModelsArray;
	}

	public Map<Long, SimpleCounter> getAssetPublisherQueryCounter() {
		return _assetPublisherQueryCounter;
	}

	public String getAssetPublisherQueryName() {
		return _assetPublisherQueryName;
	}

	public List<AssetTagModel>[] getAssetTagModelsArray() {
		return _assetTagModelsArray;
	}

	public List<AssetTagStatsModel>[] getAssetTagStatsModelsArray() {
		return _assetTagStatsModelsArray;
	}

	public List<AssetVocabularyModel>[] getAssetVocabularyModelsArray() {
		return _assetVocabularyModelsArray;
	}

	public long getClassNameId(
		Class<?> clazz, Map<String, ClassNameModel> classNameModels) {

		ClassNameModel classNameModel = classNameModels.get(clazz.getName());

		return classNameModel.getClassNameId();
	}

	public Map<String, ClassNameModel> getClassNameModels() {
		return _classNameModels;
	}

	public Collection<ClassNameModel> getClassNameModelValues() {
		return getClassNameModels().values();
	}

	public long getCompanyId() {
		return _companyId;
	}

	public CompanyModel getCompanyModel() {
		return _companyModel;
	}

	public SimpleCounter getCounter() {
		return _counter;
	}

	public String getDateLong(Date date) {
		return String.valueOf(date.getTime());
	}

	public String getDateString(Date date) {
		if (date == null) {
			return null;
		}

		return getSimpleDateFormat().format(date);
	}

	public PortletPreferencesImpl
		getDefaultAssetPublisherPortletPreference() {

		return _defaultAssetPublisherPortletPreference;
	}

	public AssetVocabularyModel getDefaultAssetVocabularyModel() {
		return _defaultAssetVocabularyModel;
	}

	public DDMStructureLayoutModel
		getDefaultDLDDMStructureLayoutModel() {

		return _defaultDLDDMStructureLayoutModel;
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return _defaultDLDDMStructureModel;
	}

	public DDMStructureVersionModel
		getDefaultDLDDMStructureVersionModel() {

		return _defaultDLDDMStructureVersionModel;
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return _defaultDLFileEntryTypeModel;
	}

	public DDMStructureLayoutModel
		getDefaultJournalDDMStructureLayoutModel() {

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

	public long getDefaultUserId() {
		return _defaultUserId;
	}

	public UserModel getDefaultUserModel() {
		return _defaultUserModel;
	}

	public String getDlDDMStructureContent() {
		return _dlDDMStructureContent;
	}

	public String getDlDDMStructureLayoutContent() {
		return _dlDDMStructureLayoutContent;
	}

	public List<String> getFirstNames() {
		return _firstNames;
	}

	public SimpleCounter getFutureDateCounter() {
		return _futureDateCounter;
	}

	public long getGlobalGroupId() {
		return _globalGroupId;
	}

	public GroupModel getGlobalGroupModel() {
		return _globalGroupModel;
	}

	public List<GroupModel> getGroupModels() {
		return _groupModels;
	}

	public long getGuestGroupId() {
		return _guestGroupId;
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

	public String getJournalArticleContent() {
		return _journalArticleContent;
	}

	public Map<Long, String> getJournalArticleResourceUUIDs() {
		return _journalArticleResourceUUIDs;
	}

	public String getJournalDDMStructureContent() {
		return _journalDDMStructureContent;
	}

	public String getJournalDDMStructureLayoutContent() {
		return _journalDDMStructureLayoutContent;
	}

	public List<String> getLastNames() {
		return _lastNames;
	}

	public Map<Long, SimpleCounter> getLayoutCounters() {
		return _layoutCounters;
	}

	public int getMaxAssetCategoryCount() {
		return _maxAssetCategoryCount;
	}

	public int getMaxAssetEntryToAssetCategoryCount() {
		return _maxAssetEntryToAssetCategoryCount;
	}

	public int getMaxAssetEntryToAssetTagCount() {
		return _maxAssetEntryToAssetTagCount;
	}

	public int getMaxAssetPublisherPageCount() {
		return _maxAssetPublisherPageCount;
	}

	public int getMaxAssetTagCount() {
		return _maxAssetTagCount;
	}

	public int getMaxAssetVocabularyCount() {
		return _maxAssetVocabularyCount;
	}

	public int getMaxBlogsEntryCommentCount() {
		return _maxBlogsEntryCommentCount;
	}

	public int getMaxBlogsEntryCount() {
		return _maxBlogsEntryCount;
	}

	public int getMaxDDLCustomFieldCount() {
		return _maxDDLCustomFieldCount;
	}

	public int getMaxDDLRecordCount() {
		return _maxDDLRecordCount;
	}

	public int getMaxDDLRecordSetCount() {
		return _maxDDLRecordSetCount;
	}

	public int getMaxDLFileEntryCount() {
		return _maxDLFileEntryCount;
	}

	public int getMaxDLFileEntrySize() {
		return _maxDLFileEntrySize;
	}

	public int getMaxDLFolderCount() {
		return _maxDLFolderCount;
	}

	public int getMaxDLFolderDepth() {
		return _maxDLFolderDepth;
	}

	public int getMaxGroupsCount() {
		return _maxGroupsCount;
	}

	public int getMaxJournalArticleCount() {
		return _maxJournalArticleCount;
	}

	public int getMaxJournalArticlePageCount() {
		return _maxJournalArticlePageCount;
	}

	public int getMaxJournalArticleVersionCount() {
		return _maxJournalArticleVersionCount;
	}

	public int getMaxMBCategoryCount() {
		return _maxMBCategoryCount;
	}

	public int getMaxMBMessageCount() {
		return _maxMBMessageCount;
	}

	public int getMaxMBThreadCount() {
		return _maxMBThreadCount;
	}

	public int getMaxUserCount() {
		return _maxUserCount;
	}

	public int getMaxUserToGroupCount() {
		return _maxUserToGroupCount;
	}

	public int getMaxWikiNodeCount() {
		return _maxWikiNodeCount;
	}

	public int getMaxWikiPageCommentCount() {
		return _maxWikiPageCommentCount;
	}

	public int getMaxWikiPageCount() {
		return _maxWikiPageCount;
	}

	public long getGroupClassNameId() {
		return getClassNameId(Group.class, getClassNameModels());
	}

	public Date nextFutureDate(SimpleCounter futureDateCounter) {
		return new Date(_FUTURE_TIME + (futureDateCounter.get() * Time.SECOND));
	}

	public RoleModel getOwnerRoleModel() {
		return _ownerRoleModel;
	}

	public PortletPreferencesFactory getPortletPreferencesFactory() {
		return _portletPreferencesFactory;
	}

	public RoleModel getPowerUserRoleModel() {
		return _powerUserRoleModel;
	}

	public SimpleCounter getResourcePermissionCounter() {
		return _resourcePermissionCounter;
	}

	public String getResource(String resourceName) throws Exception {

		List<String> lines = new ArrayList<>();

		StringUtil.readLines(getResourceInputStream(resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	public InputStream getResourceInputStream(String resourceName) {

		ClassLoader classLoader = _clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public List<RoleModel> getRoleModels() {
		return _roleModels;
	}

	public long getSampleUserId() {
		return _sampleUserId;
	}

	public UserModel getSampleUserModel() {
		return _sampleUserModel;
	}

	public Format getSimpleDateFormat() {
		return _simpleDateFormat;
	}

	public RoleModel getSiteMemberRoleModel() {
		return _siteMemberRoleModel;
	}

	public SimpleCounter getSocialActivityCounter() {
		return _socialActivityCounter;
	}

	public SimpleCounter getTimeCounter() {
		return _timeCounter;
	}

	public RoleModel getUserRoleModel() {
		return _userRoleModel;
	}

	public SimpleCounter getUserScreenNameCounter() {
		return _userScreenNameCounter;
	}

	public VirtualHostModel getVirtualHostModel() {
		return _virtualHostModel;
	}

	public AccountModel initAccountModel(long companyId, long accountId) {

		AccountModel accountModel = new AccountModelImpl();

		accountModel.setAccountId(accountId);
		accountModel.setCompanyId(companyId);
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());
		accountModel.setName("Liferay");
		accountModel.setLegalName("Liferay, Inc.");

		return accountModel;
	}

	public void initAssetCategoryModels(String userName) {
		_assetCategoryModelsArray =
			(List<AssetCategoryModel>[])new List<?>[_maxGroupsCount];
		_assetVocabularyModelsArray =
			(List<AssetVocabularyModel>[])new List<?>[_maxGroupsCount];
		_defaultAssetVocabularyModel = newAssetVocabularyModel(
			_globalGroupId, _defaultUserId, null,
			PropsValues.ASSET_VOCABULARY_DEFAULT, _counter.get(), _companyId);

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
					newAssetVocabularyModel(
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
						newAssetCategoryModel(
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

	public void initAssetTagModels(String userName) {
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

				AssetTagStatsModel assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(
						BlogsEntry.class, _classNameModels),
					_counter.get());

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(
						JournalArticle.class, _classNameModels),
					_counter.get());

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(
						WikiPage.class, _classNameModels),
					_counter.get());

				assetTagStatsModels.add(assetTagStatsModel);
			}

			_assetTagModelsArray[i - 1] = assetTagModels;
			_assetTagStatsModelsArray[i - 1] = assetTagStatsModels;
		}
	}

	public Map<String, ClassNameModel> initClassNameModels(
		SimpleCounter simpleCounter) {

		Map<String, ClassNameModel> classNameModels = new HashMap<>();
		List<String> models = ModelHintsUtil.getModels();
		SimpleCounter counter = simpleCounter;

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}

	public CompanyModel initCompanyModel(long companyId, long accountId) {

		CompanyModel companyModel = new CompanyModelImpl();

		companyModel.setCompanyId(companyId);
		companyModel.setAccountId(accountId);
		companyModel.setWebId("liferay.com");
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);

		return companyModel;
	}

	public void initCompanyModels() {
		_companyModel = initCompanyModel(_companyId, _accountId);
		_accountModel = initAccountModel(_companyId, _accountId);
	}

	public void initContext(Properties properties) {
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
	}

	public void initMaxJournalArticleSize(Properties properties) {
		int maxJournalArticleSize = GetterUtil.getInteger(
			properties.getProperty("sample.sql.max.journal.article.size"));

		_journalArticleContent = initJournalArticleContent(
			maxJournalArticleSize);
	}

	public void initVirtualHostModel(Properties properties) {
		_virtualHostModel = initVirtualHostModel(
			properties.getProperty("sample.sql.virtual.hostname"),
			_counter.get(), _companyId);
	}

	public void init() throws Exception {
		initParameter();
		initResource();
		initCompanyModels();
		initUserNames();
		initGroupModels();
		initUserModels(DataFactoryConstants.SAMPLE_USER_NAME);
		initAssetCategoryModels(DataFactoryConstants.SAMPLE_USER_NAME);
		initAssetTagModels(DataFactoryConstants.SAMPLE_USER_NAME);
		initDLFileEntryTypeModel(DataFactoryConstants.SAMPLE_USER_NAME);
		initRoleModels(DataFactoryConstants.SAMPLE_USER_NAME);
	}

	public void initDLFileEntryTypeModel(String userName) {
		_defaultDLFileEntryTypeModel = new DLFileEntryTypeModelImpl();

		_defaultDLFileEntryTypeModel.setUuid(SequentialUUID.generate());
		_defaultDLFileEntryTypeModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		_defaultDLFileEntryTypeModel.setCreateDate(
			nextFutureDate(_futureDateCounter));
		_defaultDLFileEntryTypeModel.setModifiedDate(
			nextFutureDate(_futureDateCounter));
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
			nextFutureDate(_futureDateCounter));

		_defaultDLDDMStructureModel = newDDMStructureModel(
			_globalGroupId, _defaultUserId, getClassNameId(
				DLFileEntry.class, _classNameModels),
			RawMetadataProcessor.TIKA_RAW_METADATA, _dlDDMStructureContent,
			_counter.get(), _companyId, userName, _futureDateCounter);

		_defaultDLDDMStructureVersionModel = newDDMStructureVersionModel(
			_defaultDLDDMStructureModel, userName);

		_defaultDLDDMStructureLayoutModel = newDDMStructureLayoutModel(
			_globalGroupId, _defaultUserId,
			_defaultDLDDMStructureVersionModel.getStructureVersionId(),
			_dlDDMStructureLayoutContent, _counter.get(), _companyId, userName,
			_futureDateCounter);

		_defaultJournalDDMStructureModel = newDDMStructureModel(
			_globalGroupId, _defaultUserId, getClassNameId(
				JournalArticle.class, _classNameModels),
			"BASIC-WEB-CONTENT", _journalDDMStructureContent, _counter.get(),
			_companyId, userName, _futureDateCounter);

		_defaultJournalDDMStructureVersionModel = newDDMStructureVersionModel(
			_defaultJournalDDMStructureModel, userName);

		_defaultJournalDDMStructureLayoutModel = newDDMStructureLayoutModel(
			_globalGroupId, _defaultUserId,
			_defaultJournalDDMStructureVersionModel.getStructureVersionId(),
			_journalDDMStructureLayoutContent, _counter.get(), _companyId,
			userName, _futureDateCounter);

		_defaultJournalDDMTemplateModel = newDDMTemplateModel(
			_globalGroupId, _defaultUserId,
			_defaultJournalDDMStructureModel.getStructureId(), getClassNameId(
				JournalArticle.class, _classNameModels),
			_counter.get(), _companyId, _futureDateCounter, _classNameModels,
			_counter.get(), userName);
	}

	public List<String> initUserFirstNames(Class<?> clazz) throws IOException {

		List<String> firstNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream("first_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			firstNames.add(line);
		}

		unsyncBufferedReader.close();

		return firstNames;
	}

	public List<String> initUserLastNames(Class<?> clazz) throws IOException {

		List<String> lastNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream("last_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			lastNames.add(line);
		}

		unsyncBufferedReader.close();

		return lastNames;
	}

	public GroupModel initGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site, long companyId, long sampleUserId)
		throws Exception {

		GroupModel globalGroupModel = newGroupModel(
			groupId, classNameId, classPK, name, site, companyId, sampleUserId);

		return globalGroupModel;
	}

	public GroupModel newGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site, long companyId, long sampleUserId)
		throws Exception {

		GroupModel groupModel = new GroupModelImpl();

		groupModel.setUuid(SequentialUUID.generate());
		groupModel.setGroupId(groupId);
		groupModel.setCompanyId(companyId);
		groupModel.setCreatorUserId(sampleUserId);
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

	public void initGroupModels() throws Exception {
		_globalGroupModel = initGroupModel(
			_globalGroupId, getClassNameId(Company.class, _classNameModels),
			_companyId, GroupConstants.GLOBAL, false, _companyId,
			_sampleUserId);

		_guestGroupModel = initGroupModel(
			_guestGroupId, getGroupClassNameId(), _guestGroupId,
			GroupConstants.GUEST, true, _companyId, _sampleUserId);

		_groupModels = new ArrayList<>(_maxGroupsCount);

		for (int i = 1; i <= _maxGroupsCount; i++) {
			GroupModel groupModel = initGroupModel(
				i, getGroupClassNameId(), i, "Site " + i, true, _companyId,
				_sampleUserId);

			_groupModels.add(groupModel);
		}
	}

	public String initJournalArticleContent(int maxJournalArticleSize)
	{
		StringBundler sb = new StringBundler(6);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><dynamic-element name=\"content");
		sb.append("\" type=\"text_area\" index-type=\"keyword\" index=\"0\">");
		sb.append("<dynamic-content language-id=\"en_US\"><![CDATA[");

		if (maxJournalArticleSize <= 0) {
			maxJournalArticleSize = 1;
		}

		char[] chars = new char[maxJournalArticleSize];

		for (int i = 0; i < maxJournalArticleSize; i++) {
			chars[i] = (char)(CharPool.LOWER_CASE_A + (i % 26));
		}

		sb.append(new String(chars));

		sb.append("]]></dynamic-content></dynamic-element></root>");

		return sb.toString();
	}

	public void initParameter() {
		_classNameModels = initClassNameModels(_counter);
		_accountId = _counter.get();
		_companyId = _counter.get();
		_defaultUserId = _counter.get();
		_globalGroupId = _counter.get();
		_guestGroupId = _counter.get();
		_sampleUserId = _counter.get();
	}

	public void initResource() throws Exception {
		_dlDDMStructureContent = getResource(
			"ddm_structure_basic_document.json");
		_dlDDMStructureLayoutContent = getResource(
			"ddm_structure_layout_basic_document.json");
		_journalDDMStructureContent = getResource(
			"ddm_structure_basic_web_content.json");
		_journalDDMStructureLayoutContent = getResource(
			"ddm_structure_layout_basic_web_content.json");

		String defaultAssetPublisherPreference = StringUtil.read(
			getResourceInputStream("default_asset_publisher_preference.xml"));

		_defaultAssetPublisherPortletPreference =
			(PortletPreferencesImpl)_portletPreferencesFactory.fromDefaultXML(
				defaultAssetPublisherPreference);
	}

	public void initRoleModels(String userName) {
		long classNameId = getClassNameId(Role.class, _classNameModels);

		_roleModels = new ArrayList<>();

		// Administrator

		_administratorRoleModel = newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_administratorRoleModel);

		// Guest

		_guestRoleModel = newRoleModel(
			RoleConstants.GUEST, RoleConstants.TYPE_REGULAR, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_guestRoleModel);

		// Organization Administrator

		RoleModel organizationAdministratorRoleModel = newRoleModel(
			RoleConstants.ORGANIZATION_ADMINISTRATOR,
			RoleConstants.TYPE_ORGANIZATION, _counter.get(), _companyId,
			_sampleUserId, userName, classNameId);

		_roleModels.add(organizationAdministratorRoleModel);

		// Organization Owner

		RoleModel organizationOwnerRoleModel = newRoleModel(
			RoleConstants.ORGANIZATION_OWNER, RoleConstants.TYPE_ORGANIZATION,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(organizationOwnerRoleModel);

		// Organization User

		RoleModel organizationUserRoleModel = newRoleModel(
			RoleConstants.ORGANIZATION_USER, RoleConstants.TYPE_ORGANIZATION,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(organizationUserRoleModel);

		// Owner

		_ownerRoleModel = newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_ownerRoleModel);

		// Power User

		_powerUserRoleModel = newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_powerUserRoleModel);

		// Site Administrator

		RoleModel siteAdministratorRoleModel = newRoleModel(
			RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE,
			_counter.get(), _companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(siteAdministratorRoleModel);

		// Site Member

		_siteMemberRoleModel = newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_siteMemberRoleModel);

		// Site Owner

		RoleModel siteOwnerRoleModel = newRoleModel(
			RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(siteOwnerRoleModel);

		// User

		_userRoleModel = newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR, _counter.get(),
			_companyId, _sampleUserId, userName, classNameId);

		_roleModels.add(_userRoleModel);
	}

	public void initUserModels(String userName) {
		_defaultUserModel = newUserModel(
			_defaultUserId, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, true, _counter.get(), _companyId);

		_guestUserModel = newUserModel(
			_counter.get(), "Test", "Test", "Test", false, _counter.get(),
			_companyId);

		_sampleUserModel = newUserModel(
			_sampleUserId, userName, userName, userName, false, _counter.get(),
			_companyId);
	}

	public void initUserNames() throws IOException {
		_firstNames = initUserFirstNames(_clazz);

		_lastNames = initUserLastNames(_clazz);
	}

	public UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser, long contactId, long companyId) {

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(companyId);
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(contactId);
		userModel.setPassword("test");
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion("What is your screen name?");
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(screenName + "@liferay.com");
		userModel.setScreenName(screenName);
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

	protected AssetCategoryModel newAssetCategoryModel(
		long groupId, long lastRightCategoryId, String name, long vocabularyId,
		long categoryId, long companyId, long userId, String userName) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		assetCategoryModel.setUuid(SequentialUUID.generate());
		assetCategoryModel.setCategoryId(categoryId);
		assetCategoryModel.setGroupId(groupId);
		assetCategoryModel.setCompanyId(companyId);
		assetCategoryModel.setUserId(userId);
		assetCategoryModel.setUserName(userName);
		assetCategoryModel.setCreateDate(new Date());
		assetCategoryModel.setModifiedDate(new Date());
		assetCategoryModel.setParentCategoryId(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		assetCategoryModel.setLeftCategoryId(lastRightCategoryId++);
		assetCategoryModel.setRightCategoryId(lastRightCategoryId++);
		assetCategoryModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetCategoryModel.setTitle(sb.toString());

		assetCategoryModel.setVocabularyId(vocabularyId);
		assetCategoryModel.setLastPublishDate(new Date());

		return assetCategoryModel;
	}

	protected AssetVocabularyModel newAssetVocabularyModel(
		long grouId, long userId, String userName, String name,
		long vocabularyId, long companyId) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		assetVocabularyModel.setUuid(SequentialUUID.generate());
		assetVocabularyModel.setVocabularyId(vocabularyId);
		assetVocabularyModel.setGroupId(grouId);
		assetVocabularyModel.setCompanyId(companyId);
		assetVocabularyModel.setUserId(userId);
		assetVocabularyModel.setUserName(userName);
		assetVocabularyModel.setCreateDate(new Date());
		assetVocabularyModel.setModifiedDate(new Date());
		assetVocabularyModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetVocabularyModel.setTitle(sb.toString());

		assetVocabularyModel.setSettings(
			"multiValued=true\\nselectedClassNameIds=0");
		assetVocabularyModel.setLastPublishDate(new Date());

		return assetVocabularyModel;
	}

	public DDMStructureLayoutModel newDDMStructureLayoutModel(
		long groupId, long userId, long structureVersionId, String definition,
		long structureLayoutId, long companyId, String userName,
		SimpleCounter futureDateCounter) {

		DDMStructureLayoutModel ddmStructureLayoutModel =
			new DDMStructureLayoutModelImpl();

		ddmStructureLayoutModel.setUuid(SequentialUUID.generate());
		ddmStructureLayoutModel.setStructureLayoutId(structureLayoutId);
		ddmStructureLayoutModel.setGroupId(groupId);
		ddmStructureLayoutModel.setCompanyId(companyId);
		ddmStructureLayoutModel.setUserId(userId);
		ddmStructureLayoutModel.setUserName(userName);
		ddmStructureLayoutModel.setCreateDate(
			nextFutureDate(futureDateCounter));
		ddmStructureLayoutModel.setModifiedDate(
			nextFutureDate(futureDateCounter));
		ddmStructureLayoutModel.setStructureVersionId(structureVersionId);
		ddmStructureLayoutModel.setDefinition(definition);

		return ddmStructureLayoutModel;
	}

	public DDMStructureModel newDDMStructureModel(
		long groupId, long userId, long classNameId, String structureKey,
		String definition, long structureId, long companyId, String userName,
		SimpleCounter futureDateCounter) {

		DDMStructureModel ddmStructureModel = new DDMStructureModelImpl();

		ddmStructureModel.setUuid(SequentialUUID.generate());
		ddmStructureModel.setStructureId(structureId);
		ddmStructureModel.setGroupId(groupId);
		ddmStructureModel.setCompanyId(companyId);
		ddmStructureModel.setUserId(userId);
		ddmStructureModel.setUserName(userName);
		ddmStructureModel.setVersionUserId(userId);
		ddmStructureModel.setVersionUserName(userName);
		ddmStructureModel.setCreateDate(nextFutureDate(futureDateCounter));
		ddmStructureModel.setModifiedDate(nextFutureDate(futureDateCounter));
		ddmStructureModel.setClassNameId(classNameId);
		ddmStructureModel.setStructureKey(structureKey);
		ddmStructureModel.setVersion(DDMStructureConstants.VERSION_DEFAULT);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(structureKey);
		sb.append("</name></root>");

		ddmStructureModel.setName(sb.toString());

		ddmStructureModel.setDefinition(definition);
		ddmStructureModel.setStorageType(StorageType.JSON.toString());
		ddmStructureModel.setLastPublishDate(nextFutureDate(futureDateCounter));

		return ddmStructureModel;
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel, String userName) {

		DDMStructureVersionModel ddmStructureVersionModel =
			new DDMStructureVersionModelImpl();
		
		ddmStructureVersionModel.setStructureVersionId(_counter.get());
		ddmStructureVersionModel.setGroupId(ddmStructureModel.getGroupId());
		ddmStructureVersionModel.setCompanyId(getCompanyId());
		ddmStructureVersionModel.setUserId(ddmStructureModel.getUserId());
		ddmStructureVersionModel.setUserName(userName);
		ddmStructureVersionModel.setCreateDate(
			nextFutureDate(getFutureDateCounter()));
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
		ddmStructureVersionModel.setStatusByUserName(userName);
		ddmStructureVersionModel.setStatusDate(
			nextFutureDate(getFutureDateCounter()));

		return ddmStructureVersionModel;
	}

	public DDMContentModel newDDMContentModel(
		long contentId, long groupId, String data) {

		DDMContentModel ddmContentModel = new DDMContentModelImpl();

		ddmContentModel.setUuid(SequentialUUID.generate());
		ddmContentModel.setContentId(contentId);
		ddmContentModel.setGroupId(groupId);
		ddmContentModel.setCompanyId(_companyId);
		ddmContentModel.setUserId(_sampleUserId);
		ddmContentModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddmContentModel.setCreateDate(nextFutureDate(getFutureDateCounter()));
		ddmContentModel.setModifiedDate(nextFutureDate(getFutureDateCounter()));
		ddmContentModel.setName(DDMStorageLink.class.getName());
		ddmContentModel.setData(data);

		return ddmContentModel;
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		long classNameId, long classPK, long structureId) {

		DDMStructureLinkModel ddmStructureLinkModel =
			new DDMStructureLinkModelImpl();

		ddmStructureLinkModel.setStructureLinkId(_counter.get());
		ddmStructureLinkModel.setClassNameId(classNameId);
		ddmStructureLinkModel.setClassPK(classPK);
		ddmStructureLinkModel.setStructureId(structureId);

		return ddmStructureLinkModel;
	}

	protected VirtualHostModel initVirtualHostModel(
		String hostname, long virtualHostId, long companyId) {

		VirtualHostModel virtualHostModel = new VirtualHostModelImpl();

		virtualHostModel.setVirtualHostId(virtualHostId);
		virtualHostModel.setCompanyId(companyId);
		virtualHostModel.setHostname(hostname);
		return virtualHostModel;
	}

	protected RoleModel newRoleModel(
		String name, int type, long roleId, long companyId, long sampleUserId,
		String sampleUserName, long classNameId) {

		RoleModel roleModel = new RoleModelImpl();

		roleModel.setUuid(SequentialUUID.generate());
		roleModel.setRoleId(roleId);
		roleModel.setCompanyId(companyId);
		roleModel.setUserId(sampleUserId);
		roleModel.setUserName(sampleUserName);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());
		roleModel.setClassNameId(classNameId);
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}

	protected AssetTagStatsModel newAssetTagStatsModel(
		long tagId, long classNameId, long tagStatsId) {

		AssetTagStatsModel assetTagStatsModel = new AssetTagStatsModelImpl();

		assetTagStatsModel.setTagStatsId(tagStatsId);
		assetTagStatsModel.setTagId(tagId);
		assetTagStatsModel.setClassNameId(classNameId);

		return assetTagStatsModel;
	}

	protected DDMTemplateModel newDDMTemplateModel(
		long groupId, long userId, long structureId, long sourceClassNameId,
		long templateId, long companyId, SimpleCounter futureDateCounter,
		Map<String, ClassNameModel> classNameModels, long templateKey,
		String versionUserName) {

		DDMTemplateModel ddmTemplateModel = new DDMTemplateModelImpl();

		ddmTemplateModel.setUuid(SequentialUUID.generate());
		ddmTemplateModel.setTemplateId(templateId);
		ddmTemplateModel.setGroupId(groupId);
		ddmTemplateModel.setCompanyId(companyId);
		ddmTemplateModel.setUserId(userId);
		ddmTemplateModel.setCreateDate(nextFutureDate(futureDateCounter));
		ddmTemplateModel.setModifiedDate(nextFutureDate(futureDateCounter));
		ddmTemplateModel.setClassNameId(
			getClassNameId(DDMStructure.class, classNameModels));
		ddmTemplateModel.setClassPK(structureId);
		ddmTemplateModel.setResourceClassNameId(sourceClassNameId);
		ddmTemplateModel.setTemplateKey(String.valueOf(templateKey));
		ddmTemplateModel.setVersion(DDMTemplateConstants.VERSION_DEFAULT);
		ddmTemplateModel.setVersionUserId(userId);
		ddmTemplateModel.setVersionUserName(versionUserName);

		StringBundler sb = new StringBundler(3);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append("Basic Web Content</name></root>");

		ddmTemplateModel.setName(sb.toString());

		ddmTemplateModel.setType(DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY);
		ddmTemplateModel.setMode(DDMTemplateConstants.TEMPLATE_MODE_CREATE);
		ddmTemplateModel.setLanguage(TemplateConstants.LANG_TYPE_FTL);
		ddmTemplateModel.setScript("${content.getData()}");
		ddmTemplateModel.setCacheable(true);
		ddmTemplateModel.setSmallImage(false);
		ddmTemplateModel.setLastPublishDate(nextFutureDate(futureDateCounter));

		return ddmTemplateModel;
	}

	private long _accountId;
	private AccountModel _accountModel;
	private RoleModel _administratorRoleModel;
	private final Map<Long, SimpleCounter> _assetCategoryCounters;
	private List<AssetCategoryModel>[] _assetCategoryModelsArray;
	private final Map<Long, SimpleCounter> _assetPublisherQueryCounter;
	private String _assetPublisherQueryName;
	private List<AssetTagModel>[] _assetTagModelsArray;
	private List<AssetTagStatsModel>[] _assetTagStatsModelsArray;
	private List<AssetVocabularyModel>[] _assetVocabularyModelsArray;
	private Map<String, ClassNameModel> _classNameModels;
	private long _companyId;
	private CompanyModel _companyModel;
	private final SimpleCounter _counter;
	private PortletPreferencesImpl _defaultAssetPublisherPortletPreference;
	private AssetVocabularyModel _defaultAssetVocabularyModel;
	private DDMStructureLayoutModel _defaultDLDDMStructureLayoutModel;
	private DDMStructureModel _defaultDLDDMStructureModel;
	private DDMStructureVersionModel _defaultDLDDMStructureVersionModel;
	private DLFileEntryTypeModel _defaultDLFileEntryTypeModel;
	private DDMStructureLayoutModel _defaultJournalDDMStructureLayoutModel;
	private DDMStructureModel _defaultJournalDDMStructureModel;
	private DDMStructureVersionModel _defaultJournalDDMStructureVersionModel;
	private DDMTemplateModel _defaultJournalDDMTemplateModel;
	private long _defaultUserId;
	private UserModel _defaultUserModel;
	private String _dlDDMStructureContent;
	private String _dlDDMStructureLayoutContent;
	private List<String> _firstNames;
	private final SimpleCounter _futureDateCounter;
	private long _globalGroupId;
	private GroupModel _globalGroupModel;
	private List<GroupModel> _groupModels;
	private long _guestGroupId;
	private GroupModel _guestGroupModel;
	private RoleModel _guestRoleModel;
	private UserModel _guestUserModel;
	private String _journalArticleContent;
	private final Map<Long, String> _journalArticleResourceUUIDs;
	private String _journalDDMStructureContent;
	private String _journalDDMStructureLayoutContent;
	private List<String> _lastNames;
	private final Map<Long, SimpleCounter> _layoutCounters;
	private int _maxAssetCategoryCount;
	private int _maxAssetEntryToAssetCategoryCount;
	private int _maxAssetEntryToAssetTagCount;
	private int _maxAssetPublisherPageCount;
	private int _maxAssetTagCount;
	private int _maxAssetVocabularyCount;
	private int _maxBlogsEntryCommentCount;
	private int _maxBlogsEntryCount;
	private int _maxDDLCustomFieldCount;
	private int _maxDDLRecordCount;
	private int _maxDDLRecordSetCount;
	private int _maxDLFileEntryCount;
	private int _maxDLFileEntrySize;
	private int _maxDLFolderCount;
	private int _maxDLFolderDepth;
	private int _maxGroupsCount;
	private int _maxJournalArticleCount;
	private int _maxJournalArticlePageCount;
	private int _maxJournalArticleVersionCount;
	private int _maxMBCategoryCount;
	private int _maxMBMessageCount;
	private int _maxMBThreadCount;
	private int _maxUserCount;
	private int _maxUserToGroupCount;
	private int _maxWikiNodeCount;
	private int _maxWikiPageCommentCount;
	private int _maxWikiPageCount;
	private RoleModel _ownerRoleModel;
	private final PortletPreferencesFactory _portletPreferencesFactory;
	private RoleModel _powerUserRoleModel;
	private final SimpleCounter _resourcePermissionCounter;
	private List<RoleModel> _roleModels;
	private long _sampleUserId;
	private UserModel _sampleUserModel;
	private Format _simpleDateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private RoleModel _siteMemberRoleModel;
	private final SimpleCounter _socialActivityCounter;
	private final SimpleCounter _timeCounter;
	private RoleModel _userRoleModel;
	private final SimpleCounter _userScreenNameCounter;
	private VirtualHostModel _virtualHostModel;
	private final Class<?> _clazz = getClass();
	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";
	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;
}