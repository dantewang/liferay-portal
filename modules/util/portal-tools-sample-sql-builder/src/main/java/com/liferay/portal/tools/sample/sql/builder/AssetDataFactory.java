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
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBDiscussion;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBThread;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagStatsModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class AssetDataFactory extends BaseDataFactory {

	public AssetDataFactory(
			InitContext initContext, UserDataFactory userDataFactory)
		throws Exception {

		super(initContext);

		_userDataFactory = userDataFactory;

		_initAssetCategoryModels();
		_initAssetTagModels();
		_initAssetPublisherPortletPreference();
	}

	public List<Long> getAssetCategoryIds(long groupId) {
		int maxAssetEntryToAssetCategoryCount =
			initContext.getMaxAssetEntryToAssetCategoryCount();

		SimpleCounter counter = _assetCategoryCounters.get(groupId);

		int size = (int)groupId - 1;

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetCategoryCounters.put(groupId, counter);
		}

		List<AssetCategoryModel> assetCategoryModels =
			_assetCategoryModelsArray[size];

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
		List<AssetCategoryModel> allAssetCategoryModels = new ArrayList<>();

		for (List<AssetCategoryModel> assetCategoryModels :
				_assetCategoryModelsArray) {

			allAssetCategoryModels.addAll(assetCategoryModels);
		}

		return allAssetCategoryModels;
	}

	public List<AssetCategoryModel>[] getAssetCategoryModelsArray() {
		return _assetCategoryModelsArray;
	}

	public Map<Long, SimpleCounter> getAssetPublisherQueryCounter() {
		return _assetPublisherQueryCounter;
	}

	public List<Long> getAssetTagIds(long groupId) {
		SimpleCounter counter = _assetTagCounters.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetTagCounters.put(groupId, counter);
		}

		int maxAssetEntryToAssetTagCount =
			initContext.getMaxAssetEntryToAssetTagCount();

		List<AssetTagModel> assetTagModels =
			_assetTagModelsArray[(int)groupId - 1];

		if ((assetTagModels == null) || assetTagModels.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> assetTagIds = new ArrayList<>(maxAssetEntryToAssetTagCount);

		for (int i = 0; i < maxAssetEntryToAssetTagCount; i++) {
			int index = (int)counter.get() % assetTagModels.size();

			AssetTagModel assetTagModel = assetTagModels.get(index);

			assetTagIds.add(assetTagModel.getTagId());
		}

		return assetTagIds;
	}

	public List<AssetTagModel> getAssetTagModels() {
		List<AssetTagModel> allAssetTagModels = new ArrayList<>();

		for (List<AssetTagModel> assetTagModels : _assetTagModelsArray) {
			allAssetTagModels.addAll(assetTagModels);
		}

		return allAssetTagModels;
	}

	public List<AssetTagModel>[] getAssetTagModelsArray() {
		return _assetTagModelsArray;
	}

	public List<AssetTagStatsModel> getAssetTagStatsModels() {
		List<AssetTagStatsModel> allAssetTagStatsModels = new ArrayList<>();

		for (List<AssetTagStatsModel> assetTagStatsModels :
				 _assetTagStatsModelsArray) {

			allAssetTagStatsModels.addAll(assetTagStatsModels);
		}

		return allAssetTagStatsModels;
	}

	public List<AssetTagStatsModel>[] getAssetTagStatsModelsArray() {
		return _assetTagStatsModelsArray;
	}

	public List<AssetVocabularyModel> getAssetVocabularyModels() {
		List<AssetVocabularyModel> allAssetVocabularyModels = new ArrayList<>();

		allAssetVocabularyModels.add(_defaultAssetVocabularyModel);

		for (List<AssetVocabularyModel> assetVocabularyModels :
				 _assetVocabularyModelsArray) {

			allAssetVocabularyModels.addAll(assetVocabularyModels);
		}

		return allAssetVocabularyModels;
	}

	public List<AssetVocabularyModel>[] getAssetVocabularyModelsArray() {
		return _assetVocabularyModelsArray;
	}

	public PortletPreferencesImpl
		getDefaultAssetPublisherPortletPreference() {

		return _defaultAssetPublisherPortletPreference;
	}

	public AssetVocabularyModel getDefaultAssetVocabularyModel() {
		return _defaultAssetVocabularyModel;
	}

	public PortletPreferencesFactory getPortletPreferencesFactory() {
		return _portletPreferencesFactory;
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(),
			getClassNameId(BlogsEntry.class, initContext.getClassNameModels()),
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(),
			getClassNameId(DLFileEntry.class, initContext.getClassNameModels()),
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), getClassNameId(
				DLFolder.class, initContext.getClassNameModels()),
			dLFolderModel.getFolderId(), dLFolderModel.getUuid(), 0, true, true,
			null, dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = getClassNameId(
				MBDiscussion.class, initContext.getClassNameModels());
		}
		else {
			classNameId = getClassNameId(
				MBMessage.class, initContext.getClassNameModels());
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
			mbThreadModel.getModifiedDate(), getClassNameId(
				MBThread.class, initContext.getClassNameModels()),
			mbThreadModel.getThreadId(), mbThreadModel.getUuid(), 0, true,
			false, StringPool.BLANK, String.valueOf(
				mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		Map<Long, String> journalArticleResourceUUIDs =
			_journalDataFactory.getJournalArticleResourceUUIDs();

		String resourceUUID = journalArticleResourceUUIDs.get(resourcePrimKey);

		DDMStructureModel defaultJournalDDMStructureModel =
			_journalDataFactory.getDefaultJournalDDMStructureModel();

		return newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			getClassNameId(
				JournalArticle.class, initContext.getClassNameModels()),
			resourcePrimKey, resourceUUID,
			defaultJournalDDMStructureModel.getStructureId(),
			journalArticleModel.isIndexable(), true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), getClassNameId(
				WikiPage.class, initContext.getClassNameModels()),
			wikiPageModel.getResourcePrimKey(), wikiPageModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_HTML, wikiPageModel.getTitle());
	}

	public void setJournalDataFactory(JournalDataFactory journalDataFactory) {
		_journalDataFactory = journalDataFactory;
	}

	protected AssetCategoryModel newAssetCategoryModel(
		long groupId, long lastRightCategoryId, String name,
		long vocabularyId) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		SimpleCounter counter = initContext.getCounter();

		assetCategoryModel.setUuid(SequentialUUID.generate());
		assetCategoryModel.setCategoryId(counter.get());
		assetCategoryModel.setGroupId(groupId);
		assetCategoryModel.setCompanyId(initContext.getCompanyId());
		assetCategoryModel.setUserId(initContext.getSampleUserId());
		assetCategoryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
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

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		SimpleCounter counter = initContext.getCounter();

		assetEntryModel.setEntryId(counter.get());

		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(initContext.getCompanyId());
		assetEntryModel.setUserId(initContext.getSampleUserId());
		assetEntryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
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
			nextFutureDate(initContext.getFutureDateCounter()));
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(
			nextFutureDate(initContext.getFutureDateCounter()));
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	protected AssetTagStatsModel newAssetTagStatsModel(
		long tagId, long classNameId) {

		AssetTagStatsModel assetTagStatsModel = new AssetTagStatsModelImpl();

		SimpleCounter counter = initContext.getCounter();

		assetTagStatsModel.setTagStatsId(counter.get());

		assetTagStatsModel.setTagId(tagId);
		assetTagStatsModel.setClassNameId(classNameId);

		return assetTagStatsModel;
	}

	protected AssetVocabularyModel newAssetVocabularyModel(
		long groupId, long userId, String userName, String name) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		SimpleCounter counter = initContext.getCounter();

		assetVocabularyModel.setUuid(SequentialUUID.generate());
		assetVocabularyModel.setVocabularyId(counter.get());
		assetVocabularyModel.setGroupId(groupId);
		assetVocabularyModel.setCompanyId(initContext.getCompanyId());
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

	private void _initAssetCategoryModels() {
		int maxGroupsCount = initContext.getMaxGroupsCount();
		int maxAssetVocabularyCount = initContext.getMaxAssetVocabularyCount();
		int maxAssetCategoryCount = initContext.getMaxAssetCategoryCount();

		_assetCategoryModelsArray =
			(List<AssetCategoryModel>[])new List<?>[maxGroupsCount];
		_assetVocabularyModelsArray =
			(List<AssetVocabularyModel>[])new List<?>[maxGroupsCount];

		_defaultAssetVocabularyModel = newAssetVocabularyModel(
			_userDataFactory.getGlobalGroupId(), initContext.getDefaultUserId(),
			null, PropsValues.ASSET_VOCABULARY_DEFAULT);

		StringBundler sb = new StringBundler(4);

		for (int i = 1; i <= maxGroupsCount; i++) {
			List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>(
				maxAssetVocabularyCount);
			List<AssetCategoryModel> assetCategoryModels = new ArrayList<>(
				maxAssetVocabularyCount * maxAssetCategoryCount);

			long lastRightCategoryId = 2;

			for (int j = 0; j < maxAssetVocabularyCount; j++) {
				sb.setIndex(0);

				sb.append(DataFactoryConstants.ASSET_VOCABULARY_NAME_PREFIX);
				sb.append(i);
				sb.append(StringPool.UNDERLINE);
				sb.append(j);

				AssetVocabularyModel assetVocabularyModel =
					newAssetVocabularyModel(
						i, initContext.getSampleUserId(),
						DataFactoryConstants.SAMPLE_USER_NAME, sb.toString());

				assetVocabularyModels.add(assetVocabularyModel);

				for (int k = 0; k < maxAssetCategoryCount; k++) {
					sb.setIndex(0);

					sb.append(DataFactoryConstants.ASSET_CATEGORY_NAME_PREFIX);
					sb.append(assetVocabularyModel.getVocabularyId());
					sb.append(StringPool.UNDERLINE);
					sb.append(k);

					AssetCategoryModel assetCategoryModel =
						newAssetCategoryModel(
							i, lastRightCategoryId, sb.toString(),
							assetVocabularyModel.getVocabularyId());

					lastRightCategoryId += 2;

					assetCategoryModels.add(assetCategoryModel);
				}
			}

			_assetCategoryModelsArray[i - 1] = assetCategoryModels;
			_assetVocabularyModelsArray[i - 1] = assetVocabularyModels;
		}
	}

	private void _initAssetPublisherPortletPreference() throws Exception {
		String defaultAssetPublisherPreference = StringUtil.read(
			getResourceInputStream(
				DataFactoryConstants.DEFAULT_ASSET_PUBLISHER_PREFERENCE));

		_defaultAssetPublisherPortletPreference =
			(PortletPreferencesImpl)_portletPreferencesFactory.fromDefaultXML(
				defaultAssetPublisherPreference);
	}

	private void _initAssetTagModels() {
		int maxGroupsCount = initContext.getMaxGroupsCount();
		int maxAssetTagCount = initContext.getMaxAssetTagCount();

		SimpleCounter counter = initContext.getCounter();

		_assetTagModelsArray =
			(List<AssetTagModel>[])new List<?>[maxGroupsCount];
		_assetTagStatsModelsArray =
			(List<AssetTagStatsModel>[])new List<?>[maxGroupsCount];

		for (int i = 1; i <= maxGroupsCount; i++) {
			List<AssetTagModel> assetTagModels = new ArrayList<>(
				maxAssetTagCount);
			List<AssetTagStatsModel> assetTagStatsModels = new ArrayList<>(
				maxAssetTagCount * 3);

			for (int j = 0; j < maxAssetTagCount; j++) {
				AssetTagModel assetTagModel = new AssetTagModelImpl();

				assetTagModel.setUuid(SequentialUUID.generate());
				assetTagModel.setTagId(counter.get());
				assetTagModel.setGroupId(i);
				assetTagModel.setCompanyId(initContext.getCompanyId());
				assetTagModel.setUserId(initContext.getSampleUserId());
				assetTagModel.setUserName(
					DataFactoryConstants.SAMPLE_USER_NAME);
				assetTagModel.setCreateDate(new Date());
				assetTagModel.setModifiedDate(new Date());
				assetTagModel.setName(
					DataFactoryConstants.ASSET_TAG_NAME_PREFIX + i + "_" + j);
				assetTagModel.setLastPublishDate(new Date());

				assetTagModels.add(assetTagModel);

				AssetTagStatsModel assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(
						BlogsEntry.class, initContext.getClassNameModels()));

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(
						JournalArticle.class,
						initContext.getClassNameModels()));

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(
						WikiPage.class, initContext.getClassNameModels()));

				assetTagStatsModels.add(assetTagStatsModel);
			}

			_assetTagModelsArray[i - 1] = assetTagModels;
			_assetTagStatsModelsArray[i - 1] = assetTagStatsModels;
		}
	}

	private final Map<Long, SimpleCounter> _assetCategoryCounters =
		new HashMap<>();
	private List<AssetCategoryModel>[] _assetCategoryModelsArray;
	private final Map<Long, SimpleCounter> _assetPublisherQueryCounter =
		new HashMap<>();
	private final Map<Long, SimpleCounter> _assetTagCounters = new HashMap<>();
	private List<AssetTagModel>[] _assetTagModelsArray;
	private List<AssetTagStatsModel>[] _assetTagStatsModelsArray;
	private List<AssetVocabularyModel>[] _assetVocabularyModelsArray;
	private PortletPreferencesImpl _defaultAssetPublisherPortletPreference;
	private AssetVocabularyModel _defaultAssetVocabularyModel;
	private JournalDataFactory _journalDataFactory;
	private final PortletPreferencesFactory _portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();
	private final UserDataFactory _userDataFactory;

}