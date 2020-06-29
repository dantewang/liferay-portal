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

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.change.tracking.model.CTEntryModel;
import com.liferay.change.tracking.model.CTPreferencesModel;
import com.liferay.change.tracking.model.impl.CTCollectionModelImpl;
import com.liferay.change.tracking.model.impl.CTEntryModelImpl;
import com.liferay.change.tracking.model.impl.CTPreferencesModelImpl;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderModel;
import com.liferay.journal.model.impl.JournalFolderModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

		_cTEntryMap.put(
			AssetEntry.class.getName() + "-article", assetEntryModels);

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

		_cTEntryMap.put(
			AssetEntry.class.getName() + "-folder", assetEntryModels);

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

	public List<CTEntryModel> newCTEntryModels(
		CTCollectionModel cTCollectionModel) {

		List<CTEntryModel> cTEntryModels = new ArrayList<>(_cTEntryMap.size());

		_cTEntryMap.forEach(
			(className, baseModels) -> {
				if (className.contains("-")) {
					int endIndex = className.lastIndexOf('-');

					className = className.substring(0, endIndex);
				}

				long modelClassNameId = getClassNameId(className);

				if (className.equals(JournalFolder.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							JournalFolderModel journalFolderModel =
								(JournalFolderModel)baseModel;

							long modelClassPK =
								journalFolderModel.getFolderId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(AssetEntry.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							AssetEntryModel assetEntryModel =
								(AssetEntryModel)baseModel;

							long modelClassPK = assetEntryModel.getEntryId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(
							JournalArticleResource.class.getName())) {

					baseModels.forEach(
						baseModel -> {
							JournalArticleResourceModel
								journalArticleResourceModel =
									(JournalArticleResourceModel)baseModel;

							long modelClassPK =
								journalArticleResourceModel.
									getResourcePrimKey();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(JournalArticle.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							JournalArticleModel journalArticleModel =
								(JournalArticleModel)baseModel;

							long modelClassPK = journalArticleModel.getId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(
							JournalArticleLocalization.class.getName())) {

					baseModels.forEach(
						baseModel -> {
							JournalArticleLocalizationModel
								journalArticleLocalizationModel =
									(JournalArticleLocalizationModel)baseModel;

							long modelClassPK =
								journalArticleLocalizationModel.
									getArticleLocalizationId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(DDMTemplateLink.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							DDMTemplateLinkModel dDMTemplateLinkModel =
								(DDMTemplateLinkModel)baseModel;

							long modelClassPK =
								dDMTemplateLinkModel.getTemplateLinkId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(DDMStorageLink.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							DDMStorageLinkModel dDMStorageLinkModel =
								(DDMStorageLinkModel)baseModel;

							long modelClassPK =
								dDMStorageLinkModel.getStorageLinkId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(SocialActivity.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							SocialActivityModel socialActivityModel =
								(SocialActivityModel)baseModel;

							long modelClassPK =
								socialActivityModel.getActivityId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(Layout.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							LayoutModel layoutModel = (LayoutModel)baseModel;

							long modelClassPK = layoutModel.getPlid();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(LayoutFriendlyURL.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							LayoutFriendlyURLModel layoutFriendlyURLModel =
								(LayoutFriendlyURLModel)baseModel;

							long modelClassPK =
								layoutFriendlyURLModel.getLayoutFriendlyURLId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(PortletPreferences.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							PortletPreferencesModel portletPreferencesModel =
								(PortletPreferencesModel)baseModel;

							long modelClassPK =
								portletPreferencesModel.
									getPortletPreferencesId();

							cTEntryModels.add(
								newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
			});

		return cTEntryModels;
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

		_cTEntryMap.put(DDMStorageLink.class.getName(), dDMStorageLinkModels);

		return dDMStorageLinkModels;
	}

	public List<DDMTemplateLinkModel> newDDMTemplateLinkModels(
		List<JournalArticleModel> journalArticleModels, long templateId) {

		List<DDMTemplateLinkModel> dDMTemplateLinkModels = new ArrayList<>(
			_totalArticleCount);

		journalArticleModels.forEach(
			journalArticleModel -> dDMTemplateLinkModels.add(
				newDDMTemplateLinkModel(journalArticleModel, templateId)));

		_cTEntryMap.put(DDMTemplateLink.class.getName(), dDMTemplateLinkModels);

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

		_cTEntryMap.put(
			JournalArticleLocalization.class.getName(),
			journalArticleLocalizationModels);

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

		_cTEntryMap.put(JournalArticle.class.getName(), journalArticleModels);

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

		_cTEntryMap.put(
			PortletPreferences.class.getName() + "-journalArticle",
			portletPreferencesModels);

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

		_cTEntryMap.put(
			JournalArticleResource.class.getName(),
			journalArticleResourceModels);

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

		_cTEntryMap.put(JournalFolder.class.getName(), journalFolderModels);

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

		_cTEntryMap.put(
			PortletPreferences.class.getName() + "-journalPage",
			portletPreferencesModels);

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

		_cTEntryMap.put(AssetEntry.class.getName() + "-page", assetEntryModels);

		return assetEntryModels;
	}

	public List<LayoutFriendlyURLModel> newLayoutFriendlyURLModels(
		List<LayoutModel> layoutModels) {

		List<LayoutFriendlyURLModel> layoutFriendlyURLModels = new ArrayList<>(
			layoutModels.size());

		layoutModels.forEach(
			layoutModel -> layoutFriendlyURLModels.add(
				newLayoutFriendlyURLModel(layoutModel)));

		_cTEntryMap.put(
			LayoutFriendlyURL.class.getName(), layoutFriendlyURLModels);

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

		_cTEntryMap.put(Layout.class.getName(), layoutModels);

		return layoutModels;
	}

	public List<SocialActivityModel> newSocialActivityModels(
		List<JournalArticleModel> journalArticleModels) {

		List<SocialActivityModel> socialActivityModels = new ArrayList<>(
			_totalArticleCount);

		journalArticleModels.forEach(
			journalArticleModel -> socialActivityModels.add(
				newSocialActivityModel(journalArticleModel)));

		_cTEntryMap.put(SocialActivity.class.getName(), socialActivityModels);

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

	protected CTEntryModel newCTEntryModel(
		CTCollectionModel cTCollectionModel, long modelClassNameId,
		long modelClassPK) {

		CTEntryModel cTEntryModel = new CTEntryModelImpl();

		cTEntryModel.setCtEntryId(cTEntryCounter.get());
		cTEntryModel.setCompanyId(cTCollectionModel.getCompanyId());
		cTEntryModel.setUserId(cTCollectionModel.getUserId());
		cTEntryModel.setCreateDate(new Date());
		cTEntryModel.setModifiedDate(new Date());
		cTEntryModel.setCtCollectionId(cTCollectionModel.getCtCollectionId());
		cTEntryModel.setModelClassNameId(modelClassNameId);
		cTEntryModel.setModelClassPK(modelClassPK);

		return cTEntryModel;
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

	private static final Map<String, List<?>> _cTEntryMap = new HashMap<>();

	private int _totalArticleCount =
		BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
			BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT;

}