package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.model.BlogsEntryModel;
import com.liferay.blogs.web.constants.BlogsPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBDiscussion;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBThread;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPortletKeys;
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

	public static List<AssetCategoryModel> getAssetCategoryModels() {
		List<AssetCategoryModel> allAssetCategoryModels = new ArrayList<>();

		for (List<AssetCategoryModel> assetCategoryModels :
				InitDataFactoryContext.getAssetCategoryModelsArray()) {

			allAssetCategoryModels.addAll(assetCategoryModels);
		}

		return allAssetCategoryModels;
	}

	public static List<Long> getAssetTagIds(long groupId) {
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

	public static List<AssetTagModel> getAssetTagModels() {
		List<AssetTagModel> allAssetTagModels = new ArrayList<>();

		for (List<AssetTagModel> assetTagModels :
				InitDataFactoryContext.getAssetTagModelsArray()) {

			allAssetTagModels.addAll(assetTagModels);
		}

		return allAssetTagModels;
	}

	public static List<AssetTagStatsModel> getAssetTagStatsModels() {
		List<AssetTagStatsModel> allAssetTagStatsModels = new ArrayList<>();

		for (List<AssetTagStatsModel> assetTagStatsModels :
				InitDataFactoryContext.getAssetTagStatsModelsArray()) {

			allAssetTagStatsModels.addAll(assetTagStatsModels);
		}

		return allAssetTagStatsModels;
	}

	public static List<AssetVocabularyModel> getAssetVocabularyModels() {
		List<AssetVocabularyModel> allAssetVocabularyModels = new ArrayList<>();

		allAssetVocabularyModels.add(
			InitDataFactoryContext.getDefaultAssetVocabularyModel());

		for (List<AssetVocabularyModel> assetVocabularyModels :
				InitDataFactoryContext.getAssetVocabularyModelsArray()) {

			allAssetVocabularyModels.addAll(assetVocabularyModels);
		}

		return allAssetVocabularyModels;
	}

	public static int getMaxAssetPublisherPageCount() {
		return InitDataFactoryContext.getMaxAssetPublisherPageCount();
	}

	public static AssetEntryModel newAssetEntryModel(
		BlogsEntryModel blogsEntryModel) {

		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			BlogsEntry.class,
			InitDataFactoryContext.getClassNameModels()), blogsEntryModel.getEntryId(),
			blogsEntryModel.getUuid(), 0, true, true, ContentTypes.TEXT_HTML,
			blogsEntryModel.getTitle());
	}

	public static AssetEntryModel newAssetEntryModel(
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

	public static AssetEntryModel newAssetEntryModel(
		DLFolderModel dLFolderModel) {

		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			DLFolder.class,
			InitDataFactoryContext.getClassNameModels()), dLFolderModel.getFolderId(),
			dLFolderModel.getUuid(), 0, true, true, null,
			dLFolderModel.getName());
	}

	public static AssetEntryModel newAssetEntryModel(
		MBMessageModel mbMessageModel) {

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

	public static AssetEntryModel newAssetEntryModel(
		MBThreadModel mbThreadModel) {

		return newAssetEntryModel(
			mbThreadModel.getGroupId(), mbThreadModel.getCreateDate(),
			mbThreadModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			MBThread.class,
			InitDataFactoryContext.getClassNameModels()), mbThreadModel.getThreadId(),
			mbThreadModel.getUuid(), 0, true, false, StringPool.BLANK,
			String.valueOf(mbThreadModel.getRootMessageId()));
	}

	public static AssetEntryModel newAssetEntryModel(
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
			InitDataFactoryUtil.getClassNameId(
			JournalArticle.class, InitDataFactoryContext.getClassNameModels()),
			resourcePrimKey, resourceUUID,
			InitDataFactoryContext.getDefaultJournalDDMStructureModel().getStructureId(),
			journalArticleModel.isIndexable(), true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public static AssetEntryModel newAssetEntryModel(
		WikiPageModel wikiPageModel) {

		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), InitDataFactoryUtil.getClassNameId(
			WikiPage.class,
			InitDataFactoryContext.getClassNameModels()), wikiPageModel.getResourcePrimKey(),
			wikiPageModel.getUuid(), 0, true, true, ContentTypes.TEXT_HTML,
			wikiPageModel.getTitle());
	}

	public static List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, BlogsPortletKeys.BLOGS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, WikiPortletKeys.WIKI,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	protected static AssetEntryModel newAssetEntryModel(
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

	private static final String _SAMPLE_USER_NAME = "Sample";

	private static final Map<Long, SimpleCounter> _assetTagCounters =
		new HashMap<>();

}