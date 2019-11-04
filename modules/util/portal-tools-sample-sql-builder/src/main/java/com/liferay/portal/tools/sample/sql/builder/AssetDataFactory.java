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
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
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
			DataFactoryContext dataFactoryContext,
			UserDataFactory userDataFactory)
		throws Exception {

		super(dataFactoryContext);

		_userDataFactory = userDataFactory;

		_assetClassNameIds = new long[] {
			getClassNameId(
				BlogsEntry.class, dataFactoryContext.getClassNameModels()),
			getClassNameId(
				JournalArticle.class, dataFactoryContext.getClassNameModels()),
			getClassNameId(
				WikiPage.class, dataFactoryContext.getClassNameModels())
		};

		_initAssetCategoryModels();
		_initAssetTagModels();
		_initAssetPublisherPortletPreference();
	}

	public List<Long> getAssetCategoryIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
			_assetCategoryModelsMaps[(int)assetEntryModel.getGroupId() - 1];

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
					new HashMap<?, ?>[dataFactoryContext.getMaxGroupCount()];
		}

		SimpleCounter counter = _getSimpleCounter(
			_assetCategoryCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		int maxAssetEntryToAssetCategoryCount =
			dataFactoryContext.getMaxAssetEntryToAssetCategoryCount();

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

		for (Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap :
				_assetCategoryModelsMaps) {

			for (List<AssetCategoryModel> assetCategoryModels :
					assetCategoryModelsMap.values()) {

				allAssetCategoryModels.addAll(assetCategoryModels);
			}
		}

		return allAssetCategoryModels;
	}

	public Map<Long, List<AssetCategoryModel>>[] getAssetCategoryModelsMaps() {
		return _assetCategoryModelsMaps;
	}

	public long[] getAssetClassNameIds() {
		return _assetClassNameIds;
	}

	public List<Long> getAssetTagIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetTagModel>> assetTagModelsMap =
			_assetTagModelsMaps[(int)assetEntryModel.getGroupId() - 1];

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
					new HashMap<?, ?>[dataFactoryContext.getMaxGroupCount()];
		}

		SimpleCounter counter = _getSimpleCounter(
			_assetTagCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		int maxAssetEntryToAssetTagCount =
			dataFactoryContext.getMaxAssetEntryToAssetTagCount();

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

		for (Map<Long, List<AssetTagModel>> assetTagModelsMap :
				_assetTagModelsMaps) {

			for (List<AssetTagModel> assetTagModels :
					assetTagModelsMap.values()) {

				allAssetTagModels.addAll(assetTagModels);
			}
		}

		return allAssetTagModels;
	}

	public Map<Long, List<AssetTagModel>>[] getAssetTagModelsMaps() {
		return _assetTagModelsMaps;
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

	public PortletPreferencesImpl
		getDefaultAssetPublisherPortletPreferencesImpl() {

		return _defaultAssetPublisherPortletPreferencesImpl;
	}

	public AssetVocabularyModel getDefaultAssetVocabularyModel() {
		return _defaultAssetVocabularyModel;
	}

	public String getPortletId(String portletPrefix) {
		return portletPrefix.concat(PortletIdCodec.generateInstanceId());
	}

	public PortletPreferencesFactory getPortletPreferencesFactory() {
		return _portletPreferencesFactory;
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(),
			getClassNameId(
				BlogsEntry.class, dataFactoryContext.getClassNameModels()),
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(),
			getClassNameId(
				DLFileEntry.class, dataFactoryContext.getClassNameModels()),
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(),
			getClassNameId(
				DLFolder.class, dataFactoryContext.getClassNameModels()),
			dLFolderModel.getFolderId(), dLFolderModel.getUuid(), 0, true, true,
			null, dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		SimpleCounter futureDateCounter =
			dataFactoryContext.getFutureDateCounter();

		Date endDate = nextFutureDate(futureDateCounter);

		Date expirationDate = nextFutureDate(futureDateCounter);

		assetEntryModel.setEntryId(counter.get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(dataFactoryContext.getCompanyId());
		assetEntryModel.setUserId(dataFactoryContext.getSampleUserId());
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
		assetEntryModel.setEndDate(endDate);
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(expirationDate);
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = getClassNameId(
				MBDiscussion.class, dataFactoryContext.getClassNameModels());
		}
		else {
			classNameId = getClassNameId(
				MBMessage.class, dataFactoryContext.getClassNameModels());
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
			mbThreadModel.getModifiedDate(),
			getClassNameId(
				MBThread.class, dataFactoryContext.getClassNameModels()),
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

		Map<Long, String> journalArticleResourceUUIDs =
			_journalDataFactory.getJournalArticleResourceUUIDs();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		String resourceUUID = journalArticleResourceUUIDs.get(resourcePrimKey);

		DDMStructureModel defaultJournalDDMStructureModel =
			_journalDataFactory.getDefaultJournalDDMStructureModel();

		return newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			getClassNameId(
				JournalArticle.class, dataFactoryContext.getClassNameModels()),
			resourcePrimKey, resourceUUID,
			defaultJournalDDMStructureModel.getStructureId(),
			journalArticleModel.isIndexable(), true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(),
			getClassNameId(
				WikiPage.class, dataFactoryContext.getClassNameModels()),
			wikiPageModel.getResourcePrimKey(), wikiPageModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_HTML, wikiPageModel.getTitle());
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		BlogsEntryModel blogsEntryModel) {

		Map<String, ClassNameModel> classNameModels =
			dataFactoryContext.getClassNameModels();

		ClassNameModel classNameModel = classNameModels.get(
			dataFactoryContext.getMBDiscussionCombinedClassName(
				BlogsEntry.class));

		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), classNameModel.getClassNameId(),
			blogsEntryModel.getEntryId(), "", 0, true, false, "",
			String.valueOf(blogsEntryModel.getGroupId()));
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		WikiPageModel wikiPageModel) {

		Map<String, ClassNameModel> classNameModels =
			dataFactoryContext.getClassNameModels();

		ClassNameModel classNameModel = classNameModels.get(
			dataFactoryContext.getMBDiscussionCombinedClassName(
				WikiPage.class));

		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), classNameModel.getClassNameId(),
			wikiPageModel.getResourcePrimKey(), "", 0, true, false, "",
			String.valueOf(wikiPageModel.getGroupId()));
	}

	public void setJournalDataFactory(JournalDataFactory journalDataFactory) {
		_journalDataFactory = journalDataFactory;
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

	private void _initAssetCategoryModels() {
		int maxGroupsCount = dataFactoryContext.getMaxGroupCount();
		int maxAssetVocabularyCount =
			dataFactoryContext.getMaxAssetVocabularyCount();
		int maxAssetCategoryCount =
			dataFactoryContext.getMaxAssetCategoryCount();

		_assetCategoryModelsArray =
			(List<AssetCategoryModel>[])new List<?>[maxGroupsCount];
		_assetCategoryModelsMaps =
			(Map<Long, List<AssetCategoryModel>>[])
				new HashMap<?, ?>[maxGroupsCount];
		_assetVocabularyModelsArray =
			(List<AssetVocabularyModel>[])new List<?>[maxGroupsCount];
		_defaultAssetVocabularyModel = _newAssetVocabularyModel(
			_userDataFactory.getGlobalGroupId(),
			dataFactoryContext.getDefaultUserId(), null,
			PropsValues.ASSET_VOCABULARY_DEFAULT);

		StringBundler sb = new StringBundler(4);

		for (int i = 1; i <= maxGroupsCount; i++) {
			List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>(
				maxAssetVocabularyCount);
			List<AssetCategoryModel> assetCategoryModels = new ArrayList<>(
				maxAssetVocabularyCount * maxAssetCategoryCount);

			for (int j = 0; j < maxAssetVocabularyCount; j++) {
				sb.setIndex(0);

				sb.append(DataFactoryConstants.ASSET_VOCABULARY_NAME_PREFIX);
				sb.append(i);
				sb.append(StringPool.UNDERLINE);
				sb.append(j);

				AssetVocabularyModel assetVocabularyModel =
					_newAssetVocabularyModel(
						i, dataFactoryContext.getSampleUserId(),
						DataFactoryConstants.SAMPLE_USER_NAME, sb.toString());

				assetVocabularyModels.add(assetVocabularyModel);

				for (int k = 0; k < maxAssetCategoryCount; k++) {
					sb.setIndex(0);

					sb.append(DataFactoryConstants.ASSET_CATEGORY_NAME_PREFIX);
					sb.append(assetVocabularyModel.getVocabularyId());
					sb.append(StringPool.UNDERLINE);
					sb.append(k);

					AssetCategoryModel assetCategoryModel =
						_newAssetCategoryModel(
							i, sb.toString(),
							assetVocabularyModel.getVocabularyId());

					assetCategoryModels.add(assetCategoryModel);
				}
			}

			_assetCategoryModelsArray[i - 1] = assetCategoryModels;
			_assetVocabularyModelsArray[i - 1] = assetVocabularyModels;

			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				new HashMap<>();

			int pageSize =
				assetCategoryModels.size() / _assetClassNameIds.length;

			for (int j = 0; j < _assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;

				int toIndex = (j + 1) * pageSize;

				if (j == (_assetClassNameIds.length - 1)) {
					toIndex = assetCategoryModels.size();
				}

				assetCategoryModelsMap.put(
					_assetClassNameIds[j],
					assetCategoryModels.subList(fromIndex, toIndex));
			}

			_assetCategoryModelsMaps[i - 1] = assetCategoryModelsMap;
		}
	}

	private void _initAssetPublisherPortletPreference() throws Exception {
		String defaultAssetPublisherPreference = StringUtil.read(
			getResourceInputStream(
				DataFactoryConstants.DEFAULT_ASSET_PUBLISHER_PREFERENCE));

		_defaultAssetPublisherPortletPreferencesImpl =
			(PortletPreferencesImpl)_portletPreferencesFactory.fromDefaultXML(
				defaultAssetPublisherPreference);
	}

	private void _initAssetTagModels() {
		int maxGroupsCount = dataFactoryContext.getMaxGroupCount();
		SimpleCounter counter = dataFactoryContext.getCounter();

		_assetTagModelsArray =
			(List<AssetTagModel>[])new List<?>[maxGroupsCount];
		_assetTagModelsMaps =
			(Map<Long, List<AssetTagModel>>[])new HashMap<?, ?>[maxGroupsCount];

		int maxAssetTagCount = dataFactoryContext.getMaxAssetTagCount();

		for (int i = 1; i <= maxGroupsCount; i++) {
			List<AssetTagModel> assetTagModels = new ArrayList<>(
				maxAssetTagCount);

			for (int j = 0; j < maxAssetTagCount; j++) {
				AssetTagModel assetTagModel = new AssetTagModelImpl();

				assetTagModel.setUuid(SequentialUUID.generate());
				assetTagModel.setTagId(counter.get());
				assetTagModel.setGroupId(i);
				assetTagModel.setCompanyId(dataFactoryContext.getCompanyId());
				assetTagModel.setUserId(dataFactoryContext.getSampleUserId());
				assetTagModel.setUserName(
					DataFactoryConstants.SAMPLE_USER_NAME);
				assetTagModel.setCreateDate(new Date());
				assetTagModel.setModifiedDate(new Date());
				assetTagModel.setName(
					StringBundler.concat(
						DataFactoryConstants.ASSET_TAG_NAME_PREFIX,
						String.valueOf(i), "_", String.valueOf(j)));
				assetTagModel.setLastPublishDate(new Date());

				assetTagModels.add(assetTagModel);
			}

			_assetTagModelsArray[i - 1] = assetTagModels;

			Map<Long, List<AssetTagModel>> assetTagModelsMap = new HashMap<>();

			int pageSize = assetTagModels.size() / _assetClassNameIds.length;

			for (int j = 0; j < _assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;

				int toIndex = (j + 1) * pageSize;

				if (j == (_assetClassNameIds.length - 1)) {
					toIndex = assetTagModels.size();
				}

				assetTagModelsMap.put(
					_assetClassNameIds[j],
					assetTagModels.subList(fromIndex, toIndex));
			}

			_assetTagModelsMaps[i - 1] = assetTagModelsMap;
		}
	}

	private AssetCategoryModel _newAssetCategoryModel(
		long groupId, String name, long vocabularyId) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		assetCategoryModel.setUuid(SequentialUUID.generate());
		assetCategoryModel.setCategoryId(counter.get());
		assetCategoryModel.setGroupId(groupId);
		assetCategoryModel.setCompanyId(dataFactoryContext.getCompanyId());
		assetCategoryModel.setUserId(dataFactoryContext.getSampleUserId());
		assetCategoryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
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

	private AssetVocabularyModel _newAssetVocabularyModel(
		long grouId, long userId, String userName, String name) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		assetVocabularyModel.setUuid(SequentialUUID.generate());
		assetVocabularyModel.setVocabularyId(counter.get());
		assetVocabularyModel.setGroupId(grouId);
		assetVocabularyModel.setCompanyId(dataFactoryContext.getCompanyId());
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

	private Map<Long, SimpleCounter>[] _assetCategoryCounters;
	private List<AssetCategoryModel>[] _assetCategoryModelsArray;
	private Map<Long, List<AssetCategoryModel>>[] _assetCategoryModelsMaps;
	private final long[] _assetClassNameIds;
	private Map<Long, SimpleCounter>[] _assetTagCounters;
	private List<AssetTagModel>[] _assetTagModelsArray;
	private Map<Long, List<AssetTagModel>>[] _assetTagModelsMaps;
	private List<AssetVocabularyModel>[] _assetVocabularyModelsArray;
	private PortletPreferencesImpl _defaultAssetPublisherPortletPreferencesImpl;
	private AssetVocabularyModel _defaultAssetVocabularyModel;
	private JournalDataFactory _journalDataFactory;
	private final PortletPreferencesFactory _portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();
	private final UserDataFactory _userDataFactory;

}