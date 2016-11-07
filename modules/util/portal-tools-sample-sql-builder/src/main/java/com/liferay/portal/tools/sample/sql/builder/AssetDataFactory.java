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
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBDiscussion;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBThread;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
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
public class AssetDataFactory {

	public AssetDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public List<Long> getAssetCategoryIds(long groupId) {
		Map<Long, SimpleCounter> assetCategoryCounters =
			_initContext.getAssetCategoryCounters();
		int maxAssetEntryToAssetCategoryCount =
			_initContext.getMaxAssetEntryToAssetCategoryCount();

		SimpleCounter counter = assetCategoryCounters.get(groupId);

		int size = (int)groupId - 1;

		if (counter == null) {
			counter = new SimpleCounter(0);

			assetCategoryCounters.put(groupId, counter);
		}

		List<AssetCategoryModel> assetCategoryModels =
			_initContext.getAssetCategoryModelsArray()[size];

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
				 _initContext.getAssetCategoryModelsArray()) {

			allAssetCategoryModels.addAll(assetCategoryModels);
		}

		return allAssetCategoryModels;
	}

	public List<Long> getAssetTagIds(long groupId) {
		SimpleCounter counter = _assetTagCounters.get(groupId);
		int maxAssetEntryToAssetTagCount =
			_initContext.getMaxAssetEntryToAssetTagCount();

		if (counter == null) {
			counter = new SimpleCounter(0);

			_assetTagCounters.put(groupId, counter);
		}

		List<AssetTagModel> assetTagModels =
			_initContext.getAssetTagModelsArray()[(int)groupId - 1];

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

		for (List<AssetTagModel> assetTagModels :
				 _initContext.getAssetTagModelsArray()) {

			allAssetTagModels.addAll(assetTagModels);
		}

		return allAssetTagModels;
	}

	public List<AssetTagStatsModel> getAssetTagStatsModels() {
		List<AssetTagStatsModel> allAssetTagStatsModels = new ArrayList<>();

		for (List<AssetTagStatsModel> assetTagStatsModels :
				 _initContext.getAssetTagStatsModelsArray()) {

			allAssetTagStatsModels.addAll(assetTagStatsModels);
		}

		return allAssetTagStatsModels;
	}

	public List<AssetVocabularyModel> getAssetVocabularyModels() {
		List<AssetVocabularyModel> allAssetVocabularyModels = new ArrayList<>();

		allAssetVocabularyModels.add(
			_initContext.getDefaultAssetVocabularyModel());

		for (List<AssetVocabularyModel> assetVocabularyModels :
				 _initContext.getAssetVocabularyModelsArray()) {

			allAssetVocabularyModels.addAll(assetVocabularyModels);
		}

		return allAssetVocabularyModels;
	}

	public int getMaxAssetPublisherPageCount() {
		return _initContext.getMaxAssetPublisherPageCount();
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(),
			_initContext.getClassNameId(
				BlogsEntry.class, _initContext.getClassNameModels()),
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(),
			_initContext.getClassNameId(
				DLFileEntry.class, _initContext.getClassNameModels()),
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), _initContext.getClassNameId(
				DLFolder.class, _initContext.getClassNameModels()),
			dLFolderModel.getFolderId(), dLFolderModel.getUuid(), 0, true, true,
			null, dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = _initContext.getClassNameId(
				MBDiscussion.class, _initContext.getClassNameModels());
		}
		else {
			classNameId = _initContext.getClassNameId(
				MBMessage.class, _initContext.getClassNameModels());
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
			mbThreadModel.getModifiedDate(), _initContext.getClassNameId(
				MBThread.class, _initContext.getClassNameModels()),
			mbThreadModel.getThreadId(), mbThreadModel.getUuid(), 0, true,
			false, StringPool.BLANK, String.valueOf(
				mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair, Map<Long, String> journalArticleResourceUUIDs) {

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		String resourceUUID = journalArticleResourceUUIDs.get(resourcePrimKey);

		return newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			_initContext.getClassNameId(
				JournalArticle.class, _initContext.getClassNameModels()),
			resourcePrimKey, resourceUUID,
			_initContext.getDefaultJournalDDMStructureModel().getStructureId(),
			journalArticleModel.isIndexable(), true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), _initContext.getClassNameId(
				WikiPage.class, _initContext.getClassNameModels()),
			wikiPageModel.getResourcePrimKey(), wikiPageModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_HTML, wikiPageModel.getTitle());
	}

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		assetEntryModel.setEntryId(counter.get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(_initContext.getCompanyId());
		assetEntryModel.setUserId(_initContext.getSampleUserId());
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
			_initContext.nextFutureDate(_initContext.getFutureDateCounter()));
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(
			_initContext.nextFutureDate(_initContext.getFutureDateCounter()));
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	private final Map<Long, SimpleCounter> _assetTagCounters = new HashMap<>();
	private final InitContext _initContext;

}