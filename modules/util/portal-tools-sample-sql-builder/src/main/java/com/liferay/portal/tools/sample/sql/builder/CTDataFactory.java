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

import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.change.tracking.model.CTPreferencesModel;
import com.liferay.change.tracking.model.impl.CTCollectionModelImpl;
import com.liferay.change.tracking.model.impl.CTPreferencesModelImpl;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderModel;
import com.liferay.journal.model.impl.JournalFolderModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.social.kernel.model.SocialActivityModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author Lily Chi
 */
public class CTDataFactory extends BaseDataFactory {

	public CTDataFactory() throws Exception {
	}

	public List<AssetEntryModel> newAssetEntryModels(
		List<JournalArticleModel> journalArticleModels,
		List<JournalArticleLocalizationModel>
			journalArticleLocalizationModels) {

		List<AssetEntryModel> assetEntryModels = new ArrayList<>(
			_totalArticleCount);

		for (int i = 0; i < journalArticleModels.size(); i++) {
			JournalArticleModel journalArticleModel = journalArticleModels.get(
				i);

			JournalArticleLocalizationModel journalArticleLocalizationModel =
				journalArticleLocalizationModels.get(i);

			long resourcePrimKey = journalArticleModel.getResourcePrimKey();

			String resourceUUID = journalArticleResourceUUIDs.get(
				resourcePrimKey);

			assetEntryModels.add(
				newAssetEntryModel(
					journalArticleModel.getGroupId(),
					journalArticleModel.getCreateDate(),
					journalArticleModel.getModifiedDate(),
					getClassNameId(JournalArticle.class), resourcePrimKey,
					resourceUUID,
					defaultJournalDDMStructureModel.getStructureId(),
					journalArticleModel.isIndexable(), true,
					ContentTypes.TEXT_HTML,
					journalArticleLocalizationModel.getTitle(),
					journalArticleModel.getUserId(),
					journalArticleModel.getCtCollectionId()));
		}

		return assetEntryModels;
	}

	public List<AssetEntryModel> newAssetEntryModels(
		List<JournalFolderModel> journalFolderModels) {

		List<AssetEntryModel> assetEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		journalFolderModels.forEach(
			journalFolderModel -> assetEntryModels.add(
				newAssetEntryModel(
					journalFolderModel.getGroupId(),
					journalFolderModel.getCreateDate(),
					journalFolderModel.getModifiedDate(),
					getClassNameId(JournalFolder.class),
					journalFolderModel.getFolderId(),
					journalFolderModel.getUuid(), 0, true, true,
					ContentTypes.TEXT_PLAIN, journalFolderModel.getName(),
					journalFolderModel.getUserId(),
					journalFolderModel.getCtCollectionId())));

		return assetEntryModels;
	}

	public List<CTCollectionModel> newCTCollectionModels(UserModel userModel) {
		List<CTCollectionModel> cTCollectionModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_COUNT; i++) {
			StringBundler sb = new StringBundler(4);

			sb.append("Change List ");
			sb.append(i + 1);
			sb.append(" of ");
			sb.append(userModel.getScreenName());

			cTCollectionModels.add(
				newCTCollectionModel(userModel, sb.toString()));
		}

		return cTCollectionModels;
	}

	public CTPreferencesModel newCTPreferencesModel() {
		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());
		cTPreferencesModel.setCompanyId(COMPANY_ID);

		return cTPreferencesModel;
	}

	public CTPreferencesModel newCTPreferencesModel(
		List<CTCollectionModel> cTCollectionModels) {

		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		if (!cTCollectionModels.isEmpty()) {
			CTCollectionModel cTCollectionModel = cTCollectionModels.get(0);

			cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());
			cTPreferencesModel.setMvccVersion(1);
			cTPreferencesModel.setCompanyId(cTCollectionModel.getCompanyId());
			cTPreferencesModel.setUserId(cTCollectionModel.getUserId());
			cTPreferencesModel.setCtCollectionId(
				cTCollectionModel.getCtCollectionId());
		}

		return cTPreferencesModel;
	}

	public List<DDMStorageLinkModel> newDDMStorageLinkModels(
		List<JournalArticleModel> journalArticleModels, long templateId) {

		List<DDMStorageLinkModel> dDMStorageLinkModels = new ArrayList<>(
			_totalArticleCount);

		journalArticleModels.forEach(
			journalArticleModel -> dDMStorageLinkModels.add(
				newDDMStorageLinkModel(journalArticleModel, templateId)));

		return dDMStorageLinkModels;
	}

	public List<DDMTemplateLinkModel> newDDMTemplateLinkModels(
		List<JournalArticleModel> journalArticleModels, long templateId) {

		List<DDMTemplateLinkModel> dDMTemplateLinkModels = new ArrayList<>(
			_totalArticleCount);

		journalArticleModels.forEach(
			journalArticleModel -> dDMTemplateLinkModels.add(
				newDDMTemplateLinkModel(journalArticleModel, templateId)));

		return dDMTemplateLinkModels;
	}

	public List<JournalArticleLocalizationModel>
		newJournalArticleLocalizationModels(
			List<JournalArticleModel> journalArticleModels,
			List<JournalFolderModel> journalFolderModels) {

		List<JournalArticleLocalizationModel> journalArticleLocalizationModels =
			new ArrayList<>(_totalArticleCount);

		int i = 0;

		for (JournalFolderModel journalFolderModel : journalFolderModels) {
			int j = 0;

			while (true) {
				journalArticleLocalizationModels.add(
					newJournalArticleLocalizationModel(
						journalArticleModels.get(i), j + 1, _CT_ARTICLE_VERSION,
						journalFolderModel.getCtCollectionId()));
				i++;
				j++;

				if ((i % BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT) ==
						0) {

					break;
				}
			}
		}

		return journalArticleLocalizationModels;
	}

	public List<JournalArticleModel> newJournalArticleModels(
			List<JournalArticleResourceModel> journalArticleResourceModels,
			List<JournalFolderModel> journalFolderModels)
		throws PortalException {

		List<JournalArticleModel> journalArticleModels = new ArrayList<>(
			_totalArticleCount);

		int i = 0;

		for (JournalFolderModel journalFolderModel : journalFolderModels) {
			int j = 0;

			while (true) {
				journalArticleModels.add(
					newJournalArticleModel(
						journalArticleResourceModels.get(i), j + 1,
						_CT_ARTICLE_VERSION,
						journalFolderModel.getCtCollectionId(),
						journalFolderModel.getUserId(),
						journalFolderModel.getUserName(),
						journalFolderModel.getFolderId(),
						"/" + journalFolderModel.getFolderId() + "/"));
				i++;
				j++;

				if ((i % BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT) ==
						0) {

					break;
				}
			}
		}

		return journalArticleModels;
	}

	public List<PortletPreferencesModel>
			newJournalArticlePortletPreferencesModels(
				List<LayoutModel> layoutModels,
				List<JournalArticleResourceModel> journalArticleResourceModels)
		throws Exception {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(
				BenchmarksPropsValues.MAX_CT_PAGE_COUNT *
					BenchmarksPropsValues.MAX_CT_WEBCONTENT_DISPLAY_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_PAGE_COUNT; i++) {
			for (int j = 0;
				 j < BenchmarksPropsValues.MAX_CT_WEBCONTENT_DISPLAY_COUNT;
				 j++) {

				portletPreferencesModels.add(
					newPortletPreferencesModel(
						layoutModels.get(i), i + 1, j + 1,
						journalArticleResourceModels));
			}
		}

		return portletPreferencesModels;
	}

	public List<JournalArticleResourceModel> newJournalArticleResourceModels(
		long groupId, List<JournalFolderModel> journalFolderModels) {

		List<JournalArticleResourceModel> journalArticleResourceModels =
			new ArrayList<>(
				BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
					BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		journalFolderModels.forEach(
			journalFolderModel -> {
				for (int i = 0;
					 i < BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT;
					 i++) {

					journalArticleResourceModels.add(
						newJournalArticleResourceModel(
							groupId, journalFolderModel.getCtCollectionId()));
				}
			});

		return journalArticleResourceModels;
	}

	public List<JournalContentSearchModel> newJournalContentSearchModels(
		List<JournalArticleModel> journalArticleModels,
		List<LayoutModel> layoutModels) {

		List<JournalContentSearchModel> journalContentSearchModels =
			new ArrayList<>(layoutModels.size());

		layoutModels.forEach(
			layoutModel -> journalArticleModels.forEach(
				journalArticleModel -> journalContentSearchModels.add(
					newJournalContentSearchModel(
						journalArticleModel, layoutModel.getLayoutId()))));

		return journalContentSearchModels;
	}

	public List<JournalFolderModel> newJournalFolderModels(
		CTCollectionModel cTCollectionModel, long groupId) {

		List<JournalFolderModel> journalFolderModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT;
			 i++) {

			journalFolderModels.add(
				newJournalFolderModel(
					cTCollectionModel, groupId, "Journal Folder " + (i + 1)));
		}

		return journalFolderModels;
	}

	public List<PortletPreferencesModel> newJournalPortletPreferencesModels(
		List<LayoutModel> layoutModels) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(layoutModels.size());

		layoutModels.forEach(
			layoutModel -> portletPreferencesModels.add(
				newPortletPreferencesModel(
					layoutModel.getPlid(), JournalPortletKeys.JOURNAL,
					PortletConstants.DEFAULT_PREFERENCES,
					layoutModel.getCtCollectionId())));

		return portletPreferencesModels;
	}

	public List<AssetEntryModel> newLayoutAssetEntryModels(
		List<LayoutModel> layoutModels) {

		List<AssetEntryModel> assetEntryModels = new ArrayList<>(
			layoutModels.size());

		layoutModels.forEach(
			layoutModel -> {
				String title = layoutModel.getFriendlyURL();

				title = title.substring(1);

				assetEntryModels.add(
					newAssetEntryModel(
						layoutModel.getGroupId(), layoutModel.getCreateDate(),
						layoutModel.getModifiedDate(),
						getClassNameId(Layout.class), layoutModel.getPlid(),
						layoutModel.getUuid(), 0, true, false,
						ContentTypes.TEXT_HTML, title, layoutModel.getUserId(),
						layoutModel.getCtCollectionId()));
			});

		return assetEntryModels;
	}

	public List<LayoutFriendlyURLModel> newLayoutFriendlyURLModels(
		List<LayoutModel> layoutModels) {

		List<LayoutFriendlyURLModel> layoutFriendlyURLModels = new ArrayList<>(
			layoutModels.size());

		layoutModels.forEach(
			layoutModel -> layoutFriendlyURLModels.add(
				newLayoutFriendlyURLModel(layoutModel)));

		return layoutFriendlyURLModels;
	}

	public List<LayoutModel> newLayoutModels(
		long groupId, CTCollectionModel cTCollectionModel) {

		List<LayoutModel> layoutModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_PAGE_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_PAGE_COUNT; i++) {
			String name = groupId + "_journal_article_" + (i + 1);

			String column2 = getJournalArticleLayoutColumn(
				i + 1, BenchmarksPropsValues.MAX_CT_WEBCONTENT_DISPLAY_COUNT);

			layoutModels.add(
				newLayoutModel(
					groupId, name, "", column2, cTCollectionModel.getUserId(),
					"", cTCollectionModel.getCtCollectionId()));
		}

		return layoutModels;
	}

	public List<SocialActivityModel> newSocialActivityModels(
		List<JournalArticleModel> journalArticleModels) {

		List<SocialActivityModel> socialActivityModels = new ArrayList<>(
			_totalArticleCount);

		journalArticleModels.forEach(
			journalArticleModel -> socialActivityModels.add(
				newSocialActivityModel(journalArticleModel)));

		return socialActivityModels;
	}

	protected CTCollectionModel newCTCollectionModel(
		UserModel userModel, String name) {

		CTCollectionModel cTCollectionModel = new CTCollectionModelImpl();

		cTCollectionModel.setCtCollectionId(cTCollectionCounter.get());
		cTCollectionModel.setCompanyId(userModel.getCompanyId());
		cTCollectionModel.setCreateDate(new Date());
		cTCollectionModel.setModifiedDate(new Date());
		cTCollectionModel.setName(name);
		cTCollectionModel.setStatus(2);
		cTCollectionModel.setStatusByUserId(0);
		cTCollectionModel.setUserId(userModel.getUserId());

		return cTCollectionModel;
	}

	protected CTPreferencesModel newCTPreferencesModel(
		CTCollectionModel cTCollectionModel) {

		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());
		cTPreferencesModel.setCompanyId(cTCollectionModel.getCompanyId());
		cTPreferencesModel.setUserId(cTCollectionModel.getUserId());
		cTPreferencesModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());

		return cTPreferencesModel;
	}

	protected JournalFolderModel newJournalFolderModel(
		CTCollectionModel cTCollectionModel, long groupId, String name) {

		JournalFolderModel journalFolderModel = new JournalFolderModelImpl();

		long folderId = counter.get();

		journalFolderModel.setUuid(SequentialUUID.generate());
		journalFolderModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());
		journalFolderModel.setFolderId(folderId);
		journalFolderModel.setGroupId(groupId);
		journalFolderModel.setCompanyId(cTCollectionModel.getCompanyId());
		journalFolderModel.setUserId(cTCollectionModel.getUserId());
		journalFolderModel.setCreateDate(new Date());
		journalFolderModel.setModifiedDate(new Date());
		journalFolderModel.setParentFolderId(0);
		journalFolderModel.setTreePath("/" + folderId + "/");
		journalFolderModel.setName(name);

		return journalFolderModel;
	}

	protected PortletPreferencesModel newPortletPreferencesModel(
			LayoutModel layoutModel, int pageCount, int dispalyCount,
			List<JournalArticleResourceModel> journalArticleResourceModels)
		throws Exception {

		String portletId = StringBundler.concat(
			"com_liferay_journal_content_web_portlet_",
			"JournalContentPortlet_INSTANCE_TEST_", pageCount, "_",
			dispalyCount);

		Random random = new Random();

		int articleCount = random.nextInt(
			BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT);

		JournalArticleResourceModel journalArticleResourceModel =
			journalArticleResourceModels.get(articleCount);

		javax.portlet.PortletPreferences jxPortletPreferences =
			new PortletPreferencesImpl();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return newPortletPreferencesModel(
			layoutModel.getPlid(), portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences),
			layoutModel.getCtCollectionId());
	}

	protected SocialActivityModel newSocialActivityModel(
		JournalArticleModel journalArticleModel) {

		int type = JournalActivityKeys.UPDATE_ARTICLE;

		if (journalArticleModel.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT) {

			type = JournalActivityKeys.ADD_ARTICLE;
		}

		return newSocialActivityModel(
			journalArticleModel.getGroupId(),
			getClassNameId(JournalArticle.class),
			journalArticleModel.getResourcePrimKey(), type,
			"{\"title\":\"" + journalArticleModel.getUrlTitle() + "\"}",
			journalArticleModel.getUserId(),
			journalArticleModel.getCtCollectionId());
	}

	private static final int _CT_ARTICLE_VERSION = 1;

	private int _totalArticleCount =
		BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
			BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT;

}