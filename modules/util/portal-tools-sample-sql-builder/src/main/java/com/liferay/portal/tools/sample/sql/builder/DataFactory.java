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
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.model.BlogsEntryModel;
import com.liferay.blogs.kernel.model.BlogsStatsUserModel;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFileVersionModel;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.model.DDLRecordModel;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
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
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.social.JournalActivityKeys;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
import com.liferay.message.boards.kernel.model.MBDiscussionModel;
import com.liferay.message.boards.kernel.model.MBMailingListModel;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBStatsUserModel;
import com.liferay.message.boards.kernel.model.MBThread;
import com.liferay.message.boards.kernel.model.MBThreadFlagModel;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.SubscriptionConstants;
import com.liferay.portal.kernel.model.SubscriptionModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.SubscriptionModelImpl;
import com.liferay.portlet.blogs.social.BlogsActivityKeys;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.portlet.messageboards.social.MBActivityKeys;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.WikiPageResourceModel;
import com.liferay.wiki.social.WikiActivityKeys;

import java.util.ArrayList;
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
		return BlogDataFactory.getBlogsEntryClassNameId();
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
		return InitContextUtil.getDefaultDLDDMStructureModel().getStructureId();
	}

	public DDMStructureLayoutModel getDefaultDLDDMStructureLayoutModel() {
		return DDLDataFactory.getDefaultDLDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return DDLDataFactory.getDefaultDLDDMStructureModel();
	}

	public DDMStructureVersionModel getDefaultDLDDMStructureVersionModel()
	{
		return DDLDataFactory.getDefaultDLDDMStructureVersionModel();
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return DLDataFactory.getDefaultDLFileEntryTypeModel();
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
		return InitDataFactoryUtil.getClassNameId(
			Layout.class, InitContextUtil.getClassNameModels());
	}

	public int getMaxAssetPublisherPageCount() {
		return AssetDataFactory.getMaxAssetPublisherPageCount();
	}

	public int getMaxBlogsEntryCommentCount() {
		return BlogDataFactory.getMaxBlogsEntryCommentCount();
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
		return WikiDataFactory.getMaxWikiPageCommentCount();
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
		return WikiDataFactory.getWikiPageClassNameId();
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

		return PortletPreferenceDataFactory.
			newAssetPublisherPortletPreferencesModels(plid);
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		return BlogDataFactory.newBlogsEntryModels(groupId);
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		return BlogDataFactory.newBlogsStatsUserModel(groupId);
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

		return DDLDataFactory.newDDLDDMStructureLayoutModel(
			groupId, ddmStructureVersionModel);
	}

	public DDMStructureModel newDDLDDMStructureModel(long groupId) {

		return DDLDataFactory.newDDLDDMStructureModel(groupId);
	}

	public List<PortletPreferencesModel>
		newDDLPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> dDLPortletPreferencesModels =
			PortletPreferenceDataFactory.newDDLPortletPreferencesModels(plid);

		return dDLPortletPreferencesModels;
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

		List<PortletPreferencesModel> journalPortletPreferencesModels =
			PortletPreferenceDataFactory.newJournalPortletPreferencesModels(
				plid);
		return journalPortletPreferencesModels;
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

	public MBThreadFlagModel newMBThreadFlagModel(MBThreadModel mbThreadModel)
	{
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

		return PortletPreferenceDataFactory.newPortletPreferencesModel(
			plid, groupId, portletId, currentIndex);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		return PortletPreferenceDataFactory.newPortletPreferencesModel(
			plid, portletId, ddlRecordSetModel);
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		PortletPreferencesFactory portletPreferencesFactory =
			InitContextUtil.getPortletPreferencesFactory();

		return PortletPreferenceDataFactory.newPortletPreferencesModel(
			plid, portletId, journalArticleResourceModel,
			portletPreferencesFactory);
	}

	public List<LayoutModel> newPublicLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		return LayoutDataFactory.newPublicLayoutModels(groupId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				assetCategoryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetTagModel assetTagModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				assetTagModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				assetVocabularyModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				blogsEntryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				ddlRecordSetModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				ddmStructureModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				ddmTemplateModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				dlFileEntryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				dlFolderModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				groupModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				journalArticleResourceModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				layoutModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				mbCategoryModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				mbMessageModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		PortletPreferencesModel portletPreferencesModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				portletPreferencesModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				roleModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				name, primKey);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				userModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				wikiNodeModel);

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			ResourcePermissionDataFactory.newResourcePermissionModels(
				wikiPageModel);

		return resourcePermissionModels;
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
				JournalArticle.class, InitContextUtil.getClassNameModels()),
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

		return WikiDataFactory.newWikiNodeModels(groupId);
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel)
	{

		return WikiDataFactory.newWikiPageModels(wikiNodeModel);
	}

	public WikiPageResourceModel newWikiPageResourceModel(
		WikiPageModel wikiPageModel) {

		return WikiDataFactory.newWikiPageResourceModel(wikiPageModel);
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

	private static final long _CURRENT_TIME = System.currentTimeMillis();
	private final Class<?> _clazz = getClass();
}