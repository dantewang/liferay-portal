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
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.view.count.model.ViewCountEntryModel;
import com.liferay.view.count.model.impl.ViewCountEntryModelImpl;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory extends BaseDDMDataFactory {

	public DataFactory() throws Exception {
		_defaultAssetPublisherPortletPreferencesImpl =
			(PortletPreferencesImpl)portletPreferencesFactory.fromDefaultXML(
				readFile("default_asset_publisher_preference.xml"));

		initAssetCategoryModels();
		initAssetTagModels();
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

		SimpleCounter counter = getSimpleCounter(
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

	public List<AssetCategoryModel> getAssetCategoryModels() {
		List<AssetCategoryModel> allAssetCategoryModels = new ArrayList<>();

		for (Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap :
				assetCategoryModelsMaps) {

			for (List<AssetCategoryModel> assetCategoryModels :
					assetCategoryModelsMap.values()) {

				allAssetCategoryModels.addAll(assetCategoryModels);
			}
		}

		return allAssetCategoryModels;
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

		SimpleCounter counter = getSimpleCounter(
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

	public List<AssetTagModel> getAssetTagModels() {
		List<AssetTagModel> allAssetTagModels = new ArrayList<>();

		for (Map<Long, List<AssetTagModel>> assetTagModelsMap :
				assetTagModelsMaps) {

			for (List<AssetTagModel> assetTagModels :
					assetTagModelsMap.values()) {

				allAssetTagModels.addAll(assetTagModels);
			}
		}

		return allAssetTagModels;
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

	public int getMaxAssetPublisherPageCount() {
		return BenchmarksPropsValues.MAX_ASSETPUBLISHER_PAGE_COUNT;
	}

	public long getNextAssetClassNameId(long groupId) {
		Integer index = assetClassNameIdsIndexes.get(groupId);

		if (index == null) {
			index = 0;
		}

		long classNameId = assetClassNameIds[index % assetClassNameIds.length];

		assetClassNameIdsIndexes.put(groupId, ++index);

		return classNameId;
	}

	public String getPortletId(String portletPrefix) {
		return portletPrefix.concat(PortletIdCodec.generateInstanceId());
	}

	public void initAssetCategoryModels() {
		_assetCategoryModelsArray =
			(List<AssetCategoryModel>[])
				new List<?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		assetCategoryModelsMaps =
			(Map<Long, List<AssetCategoryModel>>[])
				new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		_assetVocabularyModelsArray =
			(List<AssetVocabularyModel>[])
				new List<?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		_defaultAssetVocabularyModel = newAssetVocabularyModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, null,
			PropsValues.ASSET_VOCABULARY_DEFAULT);

		StringBundler sb = new StringBundler(4);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>(
				BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT);
			List<AssetCategoryModel> assetCategoryModels = new ArrayList<>(
				BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT *
					BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT);

			for (int j = 0;
				 j < BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT; j++) {

				sb.setIndex(0);

				sb.append("TestVocabulary_");
				sb.append(i);
				sb.append(StringPool.UNDERLINE);
				sb.append(j);

				AssetVocabularyModel assetVocabularyModel =
					newAssetVocabularyModel(
						i, SAMPLE_USER_ID, SAMPLE_USER_NAME, sb.toString());

				assetVocabularyModels.add(assetVocabularyModel);

				for (int k = 0;
					 k < BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT; k++) {

					sb.setIndex(0);

					sb.append("TestCategory_");
					sb.append(assetVocabularyModel.getVocabularyId());
					sb.append(StringPool.UNDERLINE);
					sb.append(k);

					assetCategoryModels.add(
						newAssetCategoryModel(
							i, sb.toString(),
							assetVocabularyModel.getVocabularyId()));
				}
			}

			_assetCategoryModelsArray[i - 1] = assetCategoryModels;
			_assetVocabularyModelsArray[i - 1] = assetVocabularyModels;

			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				new HashMap<>();

			int pageSize =
				assetCategoryModels.size() / assetClassNameIds.length;

			for (int j = 0; j < assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;

				int toIndex = (j + 1) * pageSize;

				if (j == (assetClassNameIds.length - 1)) {
					toIndex = assetCategoryModels.size();
				}

				assetCategoryModelsMap.put(
					assetClassNameIds[j],
					assetCategoryModels.subList(fromIndex, toIndex));
			}

			assetCategoryModelsMaps[i - 1] = assetCategoryModelsMap;
		}
	}

	public void initAssetTagModels() {
		_assetTagModelsArray =
			(List<AssetTagModel>[])
				new List<?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		assetTagModelsMaps =
			(Map<Long, List<AssetTagModel>>[])
				new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			List<AssetTagModel> assetTagModels = new ArrayList<>(
				BenchmarksPropsValues.MAX_ASSET_TAG_COUNT);

			for (int j = 0; j < BenchmarksPropsValues.MAX_ASSET_TAG_COUNT;
				 j++) {

				AssetTagModel assetTagModel = new AssetTagModelImpl();

				assetTagModel.setUuid(SequentialUUID.generate());
				assetTagModel.setTagId(counter.get());
				assetTagModel.setGroupId(i);
				assetTagModel.setCompanyId(COMPANY_ID);
				assetTagModel.setUserId(SAMPLE_USER_ID);
				assetTagModel.setUserName(SAMPLE_USER_NAME);
				assetTagModel.setCreateDate(new Date());
				assetTagModel.setModifiedDate(new Date());
				assetTagModel.setName(
					StringBundler.concat("TestTag_", i, "_", j));
				assetTagModel.setLastPublishDate(new Date());

				assetTagModels.add(assetTagModel);
			}

			_assetTagModelsArray[i - 1] = assetTagModels;

			Map<Long, List<AssetTagModel>> assetTagModelsMap = new HashMap<>();

			int pageSize = assetTagModels.size() / assetClassNameIds.length;

			for (int j = 0; j < assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;

				int toIndex = (j + 1) * pageSize;

				if (j == (assetClassNameIds.length - 1)) {
					toIndex = assetTagModels.size();
				}

				assetTagModelsMap.put(
					assetClassNameIds[j],
					assetTagModels.subList(fromIndex, toIndex));
			}

			assetTagModelsMaps[i - 1] = assetTagModelsMap;
		}
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), getClassNameId(BlogsEntry.class),
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(),
			getClassNameId(DLFileEntry.class),
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), getClassNameId(DLFolder.class),
			dLFolderModel.getFolderId(), dLFolderModel.getUuid(), 0, true, true,
			null, dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = getClassNameId(MBDiscussion.class);
		}
		else {
			classNameId = getClassNameId(MBMessage.class);
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
			mbThreadModel.getModifiedDate(), getClassNameId(MBThread.class),
			mbThreadModel.getThreadId(), mbThreadModel.getUuid(), 0, true,
			false, StringPool.BLANK,
			String.valueOf(mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		String resourceUUID = journalArticleResourceUUIDs.get(resourcePrimKey);

		return newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			getClassNameId(JournalArticle.class), resourcePrimKey, resourceUUID,
			DEFAULT_JOURNAL_DDM_STRUCTURE_ID, journalArticleModel.isIndexable(),
			true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), getClassNameId(WikiPage.class),
			wikiPageModel.getResourcePrimKey(), wikiPageModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_HTML, wikiPageModel.getTitle());
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

	public List<AssetEntryModel> newCPDefinitionAssetEntryModels() {
		List<AssetEntryModel> assetEntryModels = new ArrayList<>();

		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			long[] cpDefinitionIds = cpDefinitionIdList.get(productIndex);

			for (int definitionIndex = 0;
				 definitionIndex <
					 BenchmarksPropsValues.
						 MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
				 definitionIndex++) {

				assetEntryModels.add(
					newAssetEntryModel(
						COMMERCE_CATALOG_GROUP_ID, new Date(), new Date(),
						getClassNameId(CPDefinition.class),
						cpDefinitionIds[definitionIndex],
						SequentialUUID.generate(), 0, true, true, "text/plain",
						cpDefinitionLocalizationNames.get(
							cpDefinitionIds[definitionIndex])));
			}
		}

		return assetEntryModels;
	}

	public List<PortletPreferencesModel> newDDLPortletPreferencesModels(
		long plid) {

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

	public List<PortletPreferencesModel> newJournalPortletPreferencesModels(
		long plid) {

		return Collections.singletonList(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		BlogsEntryModel blogsEntryModel) {

		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(),
			getClassNameId(getMBDiscussionCombinedClassName(BlogsEntry.class)),
			blogsEntryModel.getEntryId(), "", 0, true, false, "",
			String.valueOf(blogsEntryModel.getGroupId()));
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		WikiPageModel wikiPageModel) {

		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(),
			getClassNameId(getMBDiscussionCombinedClassName(WikiPage.class)),
			wikiPageModel.getResourcePrimKey(), "", 0, true, false, "",
			String.valueOf(wikiPageModel.getGroupId()));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		if (currentIndex == 1) {
			return newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		String assetPublisherQueryName = "assetCategories";

		if ((currentIndex % 2) == 0) {
			assetPublisherQueryName = "assetTags";
		}

		ObjectValuePair<String[], Integer> objectValuePair = null;

		Integer startIndex = _assetPublisherQueryStartIndexes.get(groupId);

		if (startIndex == null) {
			startIndex = 0;
		}

		if (assetPublisherQueryName.equals("assetCategories")) {
			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				assetCategoryModelsMaps[(int)groupId - 1];

			List<AssetCategoryModel> assetCategoryModels =
				assetCategoryModelsMap.get(getNextAssetClassNameId(groupId));

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetCategoriesQueryValues(
				assetCategoryModels, startIndex);
		}
		else {
			Map<Long, List<AssetTagModel>> assetTagModelsMap =
				assetTagModelsMaps[(int)groupId - 1];

			List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
				getNextAssetClassNameId(groupId));

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetTagsQueryValues(
				assetTagModels, startIndex);
		}

		String[] assetPublisherQueryValues = objectValuePair.getKey();

		_assetPublisherQueryStartIndexes.put(
			groupId, objectValuePair.getValue());

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)
				_defaultAssetPublisherPortletPreferencesImpl.clone();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue("queryName0", assetPublisherQueryName);
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue("queryName1", assetPublisherQueryName);
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

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public ViewCountEntryModel newViewCountEntryModel(
		AssetEntryModel assetEntryModel) {

		return newViewCountEntryModel(
			assetEntryModel.getCompanyId(), getClassNameId(AssetEntry.class),
			assetEntryModel.getPrimaryKey(), 0);
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetCategoriesQueryValues(
			List<AssetCategoryModel> assetCategoryModels, int index) {

		String[] categoryIds = new String[4];

		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				index +=
					BenchmarksPropsValues.
						MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT;
			}

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index % assetCategoryModels.size());

			categoryIds[i] = String.valueOf(assetCategoryModel.getCategoryId());
		}

		return new ObjectValuePair<>(
			categoryIds,
			index +
				BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT);
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetTagsQueryValues(
			List<AssetTagModel> assetTagModels, int index) {

		String[] assetTagNames = new String[4];

		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				index +=
					BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT;
			}

			AssetTagModel assetTagModel = assetTagModels.get(
				index % assetTagModels.size());

			assetTagNames[i] = String.valueOf(assetTagModel.getName());
		}

		return new ObjectValuePair<>(
			assetTagNames,
			index + BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT);
	}

	protected SimpleCounter getSimpleCounter(
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

	protected AssetCategoryModel newAssetCategoryModel(
		long groupId, String name, long vocabularyId) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		assetCategoryModel.setUuid(SequentialUUID.generate());
		assetCategoryModel.setCategoryId(counter.get());
		assetCategoryModel.setGroupId(groupId);
		assetCategoryModel.setCompanyId(COMPANY_ID);
		assetCategoryModel.setUserId(SAMPLE_USER_ID);
		assetCategoryModel.setUserName(SAMPLE_USER_NAME);
		assetCategoryModel.setCreateDate(new Date());
		assetCategoryModel.setModifiedDate(new Date());
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

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		assetEntryModel.setEntryId(counter.get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(COMPANY_ID);
		assetEntryModel.setUserId(SAMPLE_USER_ID);
		assetEntryModel.setUserName(SAMPLE_USER_NAME);
		assetEntryModel.setCreateDate(createDate);
		assetEntryModel.setModifiedDate(modifiedDate);
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

	protected AssetVocabularyModel newAssetVocabularyModel(
		long grouId, long userId, String userName, String name) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		assetVocabularyModel.setUuid(SequentialUUID.generate());
		assetVocabularyModel.setVocabularyId(counter.get());
		assetVocabularyModel.setGroupId(grouId);
		assetVocabularyModel.setCompanyId(COMPANY_ID);
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

	protected PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId, String preferences) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		portletPreferencesModel.setCompanyId(COMPANY_ID);
		portletPreferencesModel.setPortletPreferencesId(counter.get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	protected ViewCountEntryModel newViewCountEntryModel(
		long companyId, long classNameId, long classPK, long viewCount) {

		ViewCountEntryModel viewCountEntryModel = new ViewCountEntryModelImpl();

		viewCountEntryModel.setPrimaryKey(new ViewCountEntryPK());
		viewCountEntryModel.setCompanyId(companyId);
		viewCountEntryModel.setClassNameId(classNameId);
		viewCountEntryModel.setClassPK(classPK);
		viewCountEntryModel.setViewCount(viewCount);

		return viewCountEntryModel;
	}

	private Map<Long, SimpleCounter>[] _assetCategoryCounters;
	private List<AssetCategoryModel>[] _assetCategoryModelsArray;
	private final Map<Long, Integer> _assetPublisherQueryStartIndexes =
		new HashMap<>();
	private Map<Long, SimpleCounter>[] _assetTagCounters;
	private List<AssetTagModel>[] _assetTagModelsArray;
	private List<AssetVocabularyModel>[] _assetVocabularyModelsArray;
	private final PortletPreferencesImpl
		_defaultAssetPublisherPortletPreferencesImpl;
	private AssetVocabularyModel _defaultAssetVocabularyModel;

}