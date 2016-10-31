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
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.model.BlogsStatsUserModel;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFileVersionModel;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.model.DDLRecordModel;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.lists.model.DDLRecordVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.friendly.url.model.FriendlyURLModel;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
import com.liferay.message.boards.kernel.model.MBDiscussionModel;
import com.liferay.message.boards.kernel.model.MBMailingListModel;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBStatsUserModel;
import com.liferay.message.boards.kernel.model.MBThreadFlagModel;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.SubscriptionModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.WikiPageResourceModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory {

	public DataFactory(Properties properties) throws Exception {
		_initContext = new InitContext(properties);

		_initContext.init();

		initDataFactory();
	}

	public AccountModel getAccountModel() {
		return _userDataFactory.getAccountModel();
	}

	public RoleModel getAdministratorRoleModel() {
		return _userDataFactory.getAdministratorRoleModel();
	}

	public List<Long> getAssetCategoryIds(long groupId) {
		return _assetDataFactory.getAssetCategoryIds(groupId);
	}

	public List<AssetCategoryModel> getAssetCategoryModels() {
		return _assetDataFactory.getAssetCategoryModels();
	}

	public List<Long> getAssetTagIds(long groupId) {
		return _assetDataFactory.getAssetTagIds(groupId);
	}

	public List<AssetTagModel> getAssetTagModels() {
		return _assetDataFactory.getAssetTagModels();
	}

	public List<AssetTagStatsModel> getAssetTagStatsModels() {
		return _assetDataFactory.getAssetTagStatsModels();
	}

	public List<AssetVocabularyModel> getAssetVocabularyModels() {
		return _assetDataFactory.getAssetVocabularyModels();
	}

	public long getBlogsEntryClassNameId() {
		return _blogDataFactory.getBlogsEntryClassNameId();
	}

	public Collection<ClassNameModel> getClassNameModelValues() {
		return _initContext.getClassNameModelValues();
	}

	public CompanyModel getCompanyModel() {
		return _userDataFactory.getCompanyModel();
	}

	public SimpleCounter getCounter() {
		return _counterDataFactory.getCounter();
	}

	public long getCounterNext() {
		return _counterDataFactory.getCounterNext();
	}

	public String getDateLong(Date date) {
		return _initContext.getDateLong(date);
	}

	public String getDateString(Date date) {

		return _initContext.getDateString(date);
	}

	public long getDDLRecordSetClassNameId() {

		return _dDLDataFactory.getDDLRecordSetClassNameId();
	}

	public long getDefaultDLDDMStructureId() {
		return _dDLDataFactory.getDefaultDLDDMStructureId();
	}

	public DDMStructureLayoutModel getDefaultDLDDMStructureLayoutModel() {
		return _dDLDataFactory.getDefaultDLDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return _dDLDataFactory.getDefaultDLDDMStructureModel();
	}

	public DDMStructureVersionModel getDefaultDLDDMStructureVersionModel() {
		return _dDLDataFactory.getDefaultDLDDMStructureVersionModel();
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return _dLDataFactory.getDefaultDLFileEntryTypeModel();
	}

	public DDMStructureLayoutModel getDefaultJournalDDMStructureLayoutModel() {
		return _journalDataFactory.getDefaultJournalDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultJournalDDMStructureModel() {
		return _journalDataFactory.getDefaultJournalDDMStructureModel();
	}

	public DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return _journalDataFactory.getDefaultJournalDDMStructureVersionModel();
	}

	public DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return _journalDataFactory.getDefaultJournalDDMTemplateModel();
	}

	public UserModel getDefaultUserModel() {
		return _userDataFactory.getDefaultUserModel();
	}

	public long getDLFileEntryClassNameId() {
		return _dLDataFactory.getDLFileEntryClassNameId();
	}

	public GroupModel getGlobalGroupModel() {
		return _userDataFactory.getGlobalGroupModel();
	}

	public List<GroupModel> getGroupModels() {
		return _userDataFactory.getGroupModels();
	}

	public GroupModel getGuestGroupModel() {
		return _userDataFactory.getGuestGroupModel();
	}

	public UserModel getGuestUserModel() {
		return _userDataFactory.getGuestUserModel();
	}

	public long getJournalArticleClassNameId() {
		return _journalDataFactory.getJournalArticleClassNameId();
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		return _journalDataFactory.getJournalArticleLayoutColumn(portletPrefix);
	}

	public long getLayoutClassNameId() {
		return _layoutDataFactory.getLayoutClassNameId();
	}

	public int getMaxAssetPublisherPageCount() {
		return _assetDataFactory.getMaxAssetPublisherPageCount();
	}

	public int getMaxBlogsEntryCommentCount() {
		return _blogDataFactory.getMaxBlogsEntryCommentCount();
	}

	public int getMaxDDLRecordCount() {
		return _dDLDataFactory.getMaxDDLRecordCount();
	}

	public int getMaxDDLRecordSetCount() {
		return _dDLDataFactory.getMaxDDLRecordSetCount();
	}

	public int getMaxDLFolderDepth() {
		return _dLDataFactory.getMaxDLFolderDepth();
	}

	public int getMaxGroupCount() {
		return _userDataFactory.getMaxGroupCount();
	}

	public int getMaxJournalArticleCount() {
		return _journalDataFactory.getMaxJournalArticleCount();
	}

	public int getMaxJournalArticlePageCount() {
		return _journalDataFactory.getMaxJournalArticlePageCount();
	}

	public int getMaxJournalArticleVersionCount() {
		return _journalDataFactory.getMaxJournalArticleVersionCount();
	}

	public int getMaxWikiPageCommentCount() {
		return _wikiDataFactory.getMaxWikiPageCommentCount();
	}

	public List<Long> getNewUserGroupIds(long groupId) {

		return _userDataFactory.getNewUserGroupIds(groupId);
	}

	public RoleModel getPowerUserRoleModel() {
		return _userDataFactory.getPowerUserRoleModel();
	}

	public List<RoleModel> getRoleModels() {
		return _userDataFactory.getRoleModels();
	}

	public UserModel getSampleUserModel() {
		return _userDataFactory.getSampleUserModel();
	}

	public List<Integer> getSequence(int size) {

		return _counterDataFactory.getSequence(size);
	}

	public RoleModel getUserRoleModel() {
		return _userDataFactory.getUserRoleModel();
	}

	public VirtualHostModel getVirtualHostModel() {
		return _userDataFactory.getVirtualHostModel();
	}

	public long getWikiPageClassNameId() {
		return _wikiDataFactory.getWikiPageClassNameId();
	}

	public void initDataFactory() {

	_assetDataFactory = new AssetDataFactory(_initContext);
	_blogDataFactory = new BlogDataFactory(_initContext);
	_counterDataFactory = new CounterDataFactory(_initContext);
	_dLDataFactory = new DLDataFactory(_initContext);
	_dDLDataFactory = new DDLDataFactory(_initContext);
	_journalDataFactory = new JournalDataFactory(_initContext);
	_layoutDataFactory = new LayoutDataFactory(_initContext);
	_messageBoardDataFactory = new MessageBoardDataFactory(_initContext);
	_portletPreferenceDataFactory = new PortletPreferenceDataFactory(
		_initContext);
	_resourcePermissionDataFactory = new ResourcePermissionDataFactory(
		_initContext);
	_socialActivityDataFactory = new SocialActivityDataFactory(_initContext);
	_subscriptionDataFactory = new SubscriptionDataFactory(_initContext);
	_userDataFactory = new UserDataFactory(_initContext);
	_wikiDataFactory = new WikiDataFactory(_initContext);

	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return _assetDataFactory.newAssetEntryModel(blogsEntryModel);
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return _assetDataFactory.newAssetEntryModel(dLFileEntryModel);
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return _assetDataFactory.newAssetEntryModel(dLFolderModel);
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		return _assetDataFactory.newAssetEntryModel(mbMessageModel);
	}

	public AssetEntryModel newAssetEntryModel(MBThreadModel mbThreadModel) {
		return _assetDataFactory.newAssetEntryModel(mbThreadModel);
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		Map<Long, String> journalArticleResourceUUIDs =
			_initContext.getJournalArticleResourceUUIDs();

		return _assetDataFactory.newAssetEntryModel(
			objectValuePair, journalArticleResourceUUIDs);
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return _assetDataFactory.newAssetEntryModel(wikiPageModel);
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		return _portletPreferenceDataFactory.
			newAssetPublisherPortletPreferencesModels(plid);
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {

		return _blogDataFactory.newBlogsEntryModels(groupId);
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {

		return _blogDataFactory.newBlogsStatsUserModel(groupId);
	}

	public ContactModel newContactModel(UserModel userModel) {

		return _userDataFactory.newContactModel(userModel);
	}

	public List<CounterModel> newCounterModels() {

		return _counterDataFactory.newCounterModels();
	}

	public DDMStructureLayoutModel newDDLDDMStructureLayoutModel(
		long groupId, DDMStructureVersionModel ddmStructureVersionModel) {

		return _dDLDataFactory.newDDLDDMStructureLayoutModel(
			groupId, ddmStructureVersionModel);
	}

	public DDMStructureModel newDDLDDMStructureModel(long groupId) {
		return _dDLDataFactory.newDDLDDMStructureModel(groupId);
	}

	public List<PortletPreferencesModel>
		newDDLPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> dDLPortletPreferencesModels =
			_portletPreferenceDataFactory.newDDLPortletPreferencesModels(plid);

		return dDLPortletPreferencesModels;
	}

	public DDLRecordModel newDDLRecordModel(
		DDLRecordSetModel dDLRecordSetModel) {

		return _dDLDataFactory.newDDLRecordModel(dDLRecordSetModel);
	}

	public DDLRecordSetModel newDDLRecordSetModel(
		DDMStructureModel ddmStructureModel, int currentIndex) {

		return _dDLDataFactory.newDDLRecordSetModel(
			ddmStructureModel, currentIndex);
	}

	public DDLRecordVersionModel newDDLRecordVersionModel(
		DDLRecordModel dDLRecordModel) {

		return _dDLDataFactory.newDDLRecordVersionModel(dDLRecordModel);
	}

	public DDMContentModel newDDMContentModel(
		DDLRecordModel ddlRecordModel, int currentIndex) {

		return _dDLDataFactory.newDDMContentModel(ddlRecordModel, currentIndex);
	}

	public DDMContentModel newDDMContentModel(
		DLFileEntryModel dlFileEntryModel) {

		return _dLDataFactory.newDDMContentModel(dlFileEntryModel);
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		return _journalDataFactory.newDDMStorageLinkModel(
			journalArticleModel, structureId);
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		long ddmStorageLinkId, DDMContentModel ddmContentModel,
		long structureId) {

		return _dDLDataFactory.newDDMStorageLinkModel(
			ddmStorageLinkId, ddmContentModel, structureId);
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DDLRecordSetModel ddlRecordSetModel) {

		return _dDLDataFactory.newDDMStructureLinkModel(ddlRecordSetModel);
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DLFileEntryMetadataModel dLFileEntryMetadataModel) {

		return _dLDataFactory.newDDMStructureLinkModel(
			dLFileEntryMetadataModel);
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		return _initContext.newDDMStructureVersionModel(
			ddmStructureModel, DataFactoryConstants.SAMPLE_USER_NAME);
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		return _journalDataFactory.newDDMTemplateLinkModel(
			journalArticleModel, templateId);
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		return _dLDataFactory.newDLFileEntryMetadataModel(
			ddmStorageLinkId, ddmStructureId, dlFileVersionModel);
	}

	public List<DLFileEntryModel> newDlFileEntryModels(
		DLFolderModel dlFolerModel) {

		return _dLDataFactory.newDlFileEntryModels(dlFolerModel);
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		return _dLDataFactory.newDLFileVersionModel(dlFileEntryModel);
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		return _dLDataFactory.newDLFolderModels(groupId, parentFolderId);
	}

	public FriendlyURLModel newFriendlyURLModel(
		BlogsEntryModel blogsEntryModel) {

		return _blogDataFactory.newFriendlyURLModel(blogsEntryModel);
	}

	public GroupModel newGroupModel(UserModel userModel) throws Exception {

		return _userDataFactory.newGroupModel(userModel);
	}

	public IntegerWrapper newInteger() {
		return new IntegerWrapper();
	}

	public JournalArticleLocalizationModel newJournalArticleLocalizationModel(
		JournalArticleModel journalArticleModel, int articleIndex,
		int versionIndex) {

		return _journalDataFactory.newJournalArticleLocalizationModel(
			journalArticleModel, articleIndex, versionIndex);
	}

	public JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex)
		throws PortalException {

		return _journalDataFactory.newJournalArticleModel(
			journalArticleResourceModel, articleIndex, versionIndex);
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId) {

		Map<Long, String> journalArticleResourceUUIDs =
			_initContext.getJournalArticleResourceUUIDs();

		return _journalDataFactory.newJournalArticleResourceModel(
			groupId, journalArticleResourceUUIDs);
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		return _journalDataFactory.newJournalContentSearchModel(
			journalArticleModel, layoutId);
	}

	public List<PortletPreferencesModel>
		newJournalPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> journalPortletPreferencesModels =
			_portletPreferenceDataFactory.newJournalPortletPreferencesModels(
				plid);

		return journalPortletPreferencesModels;
	}

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		return _layoutDataFactory.newLayoutFriendlyURLModel(layoutModel);
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		return _layoutDataFactory.newLayoutModel(
			groupId, name, column1, column2);
	}

	public List<LayoutSetModel> newLayoutSetModels(
		long groupId, int publicLayoutSetPageCount) {

		return _layoutDataFactory.newLayoutSetModels(
			groupId, publicLayoutSetPageCount);
	}

	public List<MBCategoryModel> newMBCategoryModels(long groupId) {
		return _messageBoardDataFactory.newMBCategoryModels(groupId);
	}

	public MBDiscussionModel newMBDiscussionModel(
		long groupId, long classNameId, long classPK, long threadId) {

		return _messageBoardDataFactory.newMBDiscussionModel(
			groupId, classNameId, classPK, threadId);
	}

	public MBMailingListModel newMBMailingListModel(
		MBCategoryModel mbCategoryModel) {

		return _messageBoardDataFactory.newMBMailingListModel(mbCategoryModel);
	}

	public MBMessageModel newMBMessageModel(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int index) {

		return _messageBoardDataFactory.newMBMessageModel(
			mbThreadModel, classNameId, classPK, index);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel) {

		return _messageBoardDataFactory.newMBMessageModels(mbThreadModel);
	}

	public List<MBMessageModel> newMBMessageModels(
		MBThreadModel mbThreadModel, long classNameId, long classPK,
		int maxMessageCount) {

		return _messageBoardDataFactory.newMBMessageModels(
			mbThreadModel, classNameId, classPK, maxMessageCount);
	}

	public MBStatsUserModel newMBStatsUserModel(long groupId) {

		return _messageBoardDataFactory.newMBStatsUserModel(groupId);
	}

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel) {

		return _messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel);
	}

	public MBThreadModel newMBThreadModel(
		long threadId, long groupId, long rootMessageId, int messageCount) {

		return _messageBoardDataFactory.newMBThreadModel(
			threadId, groupId, rootMessageId, messageCount);
	}

	public List<MBThreadModel> newMBThreadModels(
		MBCategoryModel mbCategoryModel) {

		return _messageBoardDataFactory.newMBThreadModels(mbCategoryModel);
	}

	public <K, V> ObjectValuePair<K, V> newObjectValuePair(K key, V value) {
		return new ObjectValuePair<>(key, value);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		return _portletPreferenceDataFactory.newPortletPreferencesModel(
			plid, groupId, portletId, currentIndex);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		return _portletPreferenceDataFactory.newPortletPreferencesModel(
			plid, portletId, ddlRecordSetModel);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		PortletPreferencesFactory portletPreferencesFactory =
			_initContext.getPortletPreferencesFactory();

		return _portletPreferenceDataFactory.newPortletPreferencesModel(
			plid, portletId, journalArticleResourceModel,
			portletPreferencesFactory);
	}

	public List<LayoutModel> newPublicLayoutModels(long groupId) {

		return _layoutDataFactory.newPublicLayoutModels(groupId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				assetCategoryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetTagModel assetTagModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				assetTagModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				assetVocabularyModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				blogsEntryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				ddlRecordSetModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				ddmStructureModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				ddmTemplateModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				dlFileEntryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				dlFolderModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				groupModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				journalArticleResourceModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				layoutModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				mbCategoryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				mbMessageModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		PortletPreferencesModel portletPreferencesModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				portletPreferencesModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				roleModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				name, primKey);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				userModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				wikiNodeModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			_resourcePermissionDataFactory.newResourcePermissionModels(
				wikiPageModel);

		return resourcePermissionModels;
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel) {

		return _socialActivityDataFactory.newSocialActivityModel(
			blogsEntryModel);
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel) {

		return _socialActivityDataFactory.newSocialActivityModel(
			dlFileEntryModel);
	}

	public SocialActivityModel newSocialActivityModel(
		JournalArticleModel journalArticleModel) {

		return _socialActivityDataFactory.newSocialActivityModel(
			journalArticleModel);
	}

	public SocialActivityModel newSocialActivityModel(
		MBMessageModel mbMessageModel) {

		return _socialActivityDataFactory.newSocialActivityModel(
			mbMessageModel);
	}

	public SubscriptionModel newSubscriptionModel(
		BlogsEntryModel blogsEntryModel) {

		return _subscriptionDataFactory.newSubscriptionModel(blogsEntryModel);
	}

	public SubscriptionModel newSubscriptionModel(MBThreadModel mBThreadModel) {

		return _subscriptionDataFactory.newSubscriptionModel(mBThreadModel);
	}

	public SubscriptionModel newSubscriptionModel(WikiPageModel wikiPageModel) {

		return _subscriptionDataFactory.newSubscriptionModel(wikiPageModel);
	}

	public List<UserModel> newUserModels() {

		return _userDataFactory.newUserModels();
	}

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		return _wikiDataFactory.newWikiNodeModels(groupId);
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel) {
		return _wikiDataFactory.newWikiPageModels(wikiNodeModel);
	}

	public WikiPageResourceModel newWikiPageResourceModel(
		WikiPageModel wikiPageModel) {

		return _wikiDataFactory.newWikiPageResourceModel(wikiPageModel);
	}

	private final InitContext _initContext;
	private AssetDataFactory _assetDataFactory;
	private BlogDataFactory _blogDataFactory;
	private CounterDataFactory _counterDataFactory;
	private DLDataFactory _dLDataFactory;
	private DDLDataFactory _dDLDataFactory;
	private JournalDataFactory _journalDataFactory;
	private LayoutDataFactory _layoutDataFactory;
	private MessageBoardDataFactory _messageBoardDataFactory;
	private PortletPreferenceDataFactory _portletPreferenceDataFactory;
	private ResourcePermissionDataFactory _resourcePermissionDataFactory;
	private SocialActivityDataFactory _socialActivityDataFactory;
	private SubscriptionDataFactory _subscriptionDataFactory;
	private UserDataFactory _userDataFactory;
	private WikiDataFactory _wikiDataFactory;
}