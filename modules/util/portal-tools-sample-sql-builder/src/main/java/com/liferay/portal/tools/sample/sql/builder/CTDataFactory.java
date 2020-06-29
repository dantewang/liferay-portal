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
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderModel;
import com.liferay.journal.model.impl.JournalFolderModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.util.ContentTypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class CTDataFactory extends BaseDataFactory {

	public CTDataFactory() throws Exception {
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

}