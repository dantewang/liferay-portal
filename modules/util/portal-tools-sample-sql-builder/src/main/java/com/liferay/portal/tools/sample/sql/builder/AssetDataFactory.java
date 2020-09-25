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
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.commerce.product.model.CPDefinitionModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.util.SimpleCounter;
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

	public static AssetDataFactory getInstance() {
		return _assetDataFactory;
	}

	public List<Long> getAssetCategoryIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
			assetCategoryModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		if ((assetCategoryModelsMap == null) ||
			assetCategoryModelsMap.isEmpty()) {

			return Collections.emptyList();
		}

		List<AssetCategoryModel> assetCategoryModels =
			assetCategoryModelsMap.get(assetEntryModel.getClassNameId());

		if ((assetCategoryModels == null) || assetCategoryModels.isEmpty()) {
			return Collections.emptyList();
		}

		if (_assetCategoryCounters == null) {
			_assetCategoryCounters =
				(Map<Long, SimpleCounter>[])
					new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		}

		SimpleCounter counter = _getSimpleCounter(
			_assetCategoryCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetCategoryIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT);

		for (int i = 0;
			 i < BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT;
			 i++) {

			int index = (int)counter.get() % assetCategoryModels.size();

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index);

			assetCategoryIds.add(assetCategoryModel.getCategoryId());
		}

		return assetCategoryIds;
	}

	public List<Long> getAssetTagIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetTagModel>> assetTagModelsMap =
			assetTagModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		if ((assetTagModelsMap == null) || assetTagModelsMap.isEmpty()) {
			return Collections.emptyList();
		}

		List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
			assetEntryModel.getClassNameId());

		if ((assetTagModels == null) || assetTagModels.isEmpty()) {
			return Collections.emptyList();
		}

		if (_assetTagCounters == null) {
			_assetTagCounters =
				(Map<Long, SimpleCounter>[])
					new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		}

		SimpleCounter counter = _getSimpleCounter(
			_assetTagCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetTagIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT);

		for (int i = 0;
			 i < BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT;
			 i++) {

			int index = (int)counter.get() % assetTagModels.size();

			AssetTagModel assetTagModel = assetTagModels.get(index);

			assetTagIds.add(assetTagModel.getTagId());
		}

		return assetTagIds;
	}

	public int getMaxAssetPublisherPageCount() {
		return BenchmarksPropsValues.MAX_ASSETPUBLISHER_PAGE_COUNT;
	}

	public List<AssetCategoryModel> newAssetCategoryModels(
		long groupId, List<AssetVocabularyModel> assetVocabularyModels,
		long[] assetClassNameIds) {

		List<AssetCategoryModel> assetCategoryModels = new ArrayList<>();

		StringBundler sb = new StringBundler(4);

		List<AssetCategoryModel> groupAssetCategoryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT *
				BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT);

		for (AssetVocabularyModel assetVocabularyModel :
				assetVocabularyModels) {

			for (int k = 0; k < BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT;
				 k++) {

				sb.setIndex(0);

				sb.append("TestCategory_");
				sb.append(assetVocabularyModel.getVocabularyId());
				sb.append(StringPool.UNDERLINE);
				sb.append(k);

				AssetCategoryModel assetCategoryModel = _newAssetCategoryModel(
					groupId, sb.toString(),
					assetVocabularyModel.getVocabularyId());

				groupAssetCategoryModels.add(assetCategoryModel);

				assetCategoryModels.add(assetCategoryModel);
			}
		}

		Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
			new HashMap<>();

		int pageSize =
			groupAssetCategoryModels.size() / assetClassNameIds.length;

		for (int j = 0; j < assetClassNameIds.length; j++) {
			int fromIndex = j * pageSize;

			int toIndex = (j + 1) * pageSize;

			if (j == (assetClassNameIds.length - 1)) {
				toIndex = groupAssetCategoryModels.size();
			}

			assetCategoryModelsMap.put(
				assetClassNameIds[j],
				groupAssetCategoryModels.subList(fromIndex, toIndex));
		}

		assetCategoryModelsMaps[(int)groupId - 1] = assetCategoryModelsMap;

		return assetCategoryModels;
	}

	public AssetEntryModel newAssetEntryModel(
		BlogsEntryModel blogsEntryModel, long[] classNameIds) {

		return _newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), classNameIds[0],
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel, long[] classNameIds) {

		return _newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(), classNameIds[0],
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFolderModel dLFolderModel, long[] classNameIds) {

		return _newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), classNameIds[0],
			dLFolderModel.getFolderId(), dLFolderModel.getUuid(), 0, true, true,
			null, dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(
		MBMessageModel mbMessageModel, long[] classNameIds) {

		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = classNameIds[0];
		}
		else {
			classNameId = classNameIds[1];
			visible = true;
		}

		return _newAssetEntryModel(
			mbMessageModel.getGroupId(), mbMessageModel.getCreateDate(),
			mbMessageModel.getModifiedDate(), classNameId,
			mbMessageModel.getMessageId(), mbMessageModel.getUuid(), 0, true,
			visible, ContentTypes.TEXT_HTML, mbMessageModel.getSubject());
	}

	public AssetEntryModel newAssetEntryModel(
		MBThreadModel mbThreadModel, long[] classNameIds) {

		return _newAssetEntryModel(
			mbThreadModel.getGroupId(), mbThreadModel.getCreateDate(),
			mbThreadModel.getModifiedDate(), classNameIds[0],
			mbThreadModel.getThreadId(), mbThreadModel.getUuid(), 0, true,
			false, StringPool.BLANK,
			String.valueOf(mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair,
		long[] classNameIds) {

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		String resourceUUID = journalArticleResourceUUIDs.get(resourcePrimKey);

		return _newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(), classNameIds[0],
			resourcePrimKey, resourceUUID, DEFAULT_JOURNAL_DDM_STRUCTURE_ID,
			journalArticleModel.isIndexable(), true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		WikiPageModel wikiPageModel, long[] classNameIds) {

		return _newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), classNameIds[0],
			wikiPageModel.getResourcePrimKey(), wikiPageModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_HTML, wikiPageModel.getTitle());
	}

	public List<AssetTagModel> newAssetTagModels(
		long groupId, long[] assetClassNameIds) {

		List<AssetTagModel> assetTagModels = new ArrayList<>();

		List<AssetTagModel> groupAssetTagModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_TAG_COUNT);

		for (int j = 0; j < BenchmarksPropsValues.MAX_ASSET_TAG_COUNT; j++) {
			AssetTagModel assetTagModel = new AssetTagModelImpl();

			assetTagModel.setUuid(SequentialUUID.generate());
			assetTagModel.setTagId(counter.get());
			assetTagModel.setGroupId(groupId);
			assetTagModel.setCompanyId(COMPANY_ID);
			assetTagModel.setUserId(SAMPLE_USER_ID);
			assetTagModel.setUserName(SAMPLE_USER_NAME);
			assetTagModel.setCreateDate(new Date());
			assetTagModel.setModifiedDate(new Date());
			assetTagModel.setName(
				StringBundler.concat("TestTag_", groupId, "_", j));
			assetTagModel.setLastPublishDate(new Date());

			groupAssetTagModels.add(assetTagModel);

			assetTagModels.add(assetTagModel);
		}

		Map<Long, List<AssetTagModel>> assetTagModelsMap = new HashMap<>();

		int pageSize = groupAssetTagModels.size() / assetClassNameIds.length;

		for (int j = 0; j < assetClassNameIds.length; j++) {
			int fromIndex = j * pageSize;

			int toIndex = (j + 1) * pageSize;

			if (j == (assetClassNameIds.length - 1)) {
				toIndex = groupAssetTagModels.size();
			}

			assetTagModelsMap.put(
				assetClassNameIds[j],
				groupAssetTagModels.subList(fromIndex, toIndex));
		}

		assetTagModelsMaps[(int)groupId - 1] = assetTagModelsMap;

		return assetTagModels;
	}

	public List<AssetVocabularyModel> newAssetVocabularyModels(long groupId) {
		List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>();

		StringBundler sb = new StringBundler(4);

		for (int j = 0; j < BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT;
			 j++) {

			sb.setIndex(0);

			sb.append("TestVocabulary_");
			sb.append(groupId);
			sb.append(StringPool.UNDERLINE);
			sb.append(j);

			AssetVocabularyModel assetVocabularyModel =
				_newAssetVocabularyModel(
					groupId, SAMPLE_USER_ID, SAMPLE_USER_NAME, sb.toString());

			assetVocabularyModels.add(assetVocabularyModel);
		}

		return assetVocabularyModels;
	}

	public AssetEntryModel newCPDefinitionModelAssetEntryModel(
		CPDefinitionModel cpDefinitionModel,
		GroupModel commerceCatalogGroupModel, long classNameId) {

		return _newAssetEntryModel(
			commerceCatalogGroupModel.getGroupId(), new Date(), new Date(),
			classNameId, cpDefinitionModel.getCPDefinitionId(),
			SequentialUUID.generate(), 0, true, true, "text/plain",
			"Definition " + cpDefinitionModel.getCPDefinitionId());
	}

	public AssetVocabularyModel newDefaultAssetVocabularyModel() {
		return _newAssetVocabularyModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, null,
			PropsValues.ASSET_VOCABULARY_DEFAULT);
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		BlogsEntryModel blogsEntryModel, long classNameId) {

		return _newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), classNameId,
			blogsEntryModel.getEntryId(), "", 0, true, false, "",
			String.valueOf(blogsEntryModel.getGroupId()));
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		WikiPageModel wikiPageModel, long classNameId) {

		return _newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), classNameId,
			wikiPageModel.getResourcePrimKey(), "", 0, true, false, "",
			String.valueOf(wikiPageModel.getGroupId()));
	}

	private AssetDataFactory() {
	}

	private SimpleCounter _getSimpleCounter(
		Map<Long, SimpleCounter>[] simpleCountersArray, long groupId,
		long classNameId) {

		Map<Long, SimpleCounter> simpleCounters =
			simpleCountersArray[(int)groupId - 1];

		if (simpleCounters == null) {
			simpleCounters = new HashMap<>();

			simpleCountersArray[(int)groupId - 1] = simpleCounters;
		}

		SimpleCounter simpleCounter = simpleCounters.get(classNameId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter(0);

			simpleCounters.put(classNameId, simpleCounter);
		}

		return simpleCounter;
	}

	private AssetCategoryModel _newAssetCategoryModel(
		long groupId, String name, long vocabularyId) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		// UUID

		assetCategoryModel.setUuid(SequentialUUID.generate());

		// PK fields

		assetCategoryModel.setCategoryId(counter.get());

		// Group instance

		assetCategoryModel.setGroupId(groupId);

		// Audit fields

		assetCategoryModel.setCompanyId(COMPANY_ID);
		assetCategoryModel.setUserId(SAMPLE_USER_ID);
		assetCategoryModel.setUserName(SAMPLE_USER_NAME);
		assetCategoryModel.setCreateDate(new Date());
		assetCategoryModel.setModifiedDate(new Date());

		// Other fields

		assetCategoryModel.setParentCategoryId(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		assetCategoryModel.setTreePath(
			"/" + assetCategoryModel.getCategoryId() + "/");
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

	private AssetEntryModel _newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		// PK fields

		assetEntryModel.setEntryId(counter.get());

		// Group instance

		assetEntryModel.setGroupId(groupId);

		// Audit fields

		assetEntryModel.setCompanyId(COMPANY_ID);
		assetEntryModel.setUserId(SAMPLE_USER_ID);
		assetEntryModel.setUserName(SAMPLE_USER_NAME);
		assetEntryModel.setCreateDate(createDate);
		assetEntryModel.setModifiedDate(modifiedDate);

		// Other fields

		assetEntryModel.setClassNameId(classNameId);
		assetEntryModel.setClassPK(classPK);
		assetEntryModel.setClassUuid(uuid);
		assetEntryModel.setClassTypeId(classTypeId);
		assetEntryModel.setListable(listable);
		assetEntryModel.setVisible(visible);
		assetEntryModel.setStartDate(createDate);
		assetEntryModel.setEndDate(nextFutureDate());
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(nextFutureDate());
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	private AssetVocabularyModel _newAssetVocabularyModel(
		long grouId, long userId, String userName, String name) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		// UUID

		assetVocabularyModel.setUuid(SequentialUUID.generate());

		// PK fields

		assetVocabularyModel.setVocabularyId(counter.get());

		// Group instance

		assetVocabularyModel.setGroupId(grouId);

		// Audit fields

		assetVocabularyModel.setCompanyId(COMPANY_ID);
		assetVocabularyModel.setUserId(userId);
		assetVocabularyModel.setUserName(userName);
		assetVocabularyModel.setCreateDate(new Date());
		assetVocabularyModel.setModifiedDate(new Date());

		// Other fields

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

	private static AssetDataFactory _assetDataFactory = new AssetDataFactory();

	private Map<Long, SimpleCounter>[] _assetCategoryCounters;
	private Map<Long, SimpleCounter>[] _assetTagCounters;

}