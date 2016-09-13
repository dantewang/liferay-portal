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

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFileVersionModel;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lily Chi
 */
public class DLDataFactory {

	public static DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return InitContextUtil.getDefaultDLFileEntryTypeModel();
	}

	public static long getDLFileEntryClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			DLFileEntry.class, InitContextUtil.getClassNameModels());
	}

	public static int getMaxDLFolderDepth() {
		return InitContextUtil.getMaxDLFolderDepth();
	}

	public static DDMContentModel newDDMContentModel(
		DLFileEntryModel dlFileEntryModel) {

		StringBundler sb = new StringBundler(6);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [{");
		sb.append("\"instanceId\": \"");
		sb.append(StringUtil.randomId());
		sb.append("\", \"name\": \"CONTENT_TYPE\", \"value\": {\"en_US\": ");
		sb.append("\"text/plain\"}}]}");

		return DDLDataFactory.newDDMContentModel(
			InitContextUtil.getCounter().get(), dlFileEntryModel.getGroupId(),
			sb.toString());
	}

	public static DDMStructureLinkModel newDDMStructureLinkModel(
		DLFileEntryMetadataModel dLFileEntryMetadataModel) {

		return DDLDataFactory.newDDMStructureLinkModel(
			InitDataFactoryUtil.getClassNameId(
				DLFileEntryMetadata.class,
				InitContextUtil.getClassNameModels()),
			dLFileEntryMetadataModel.getFileEntryMetadataId(),
			dLFileEntryMetadataModel.getDDMStructureId());
	}

	public static DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

		dlFileEntryMetadataModel.setUuid(SequentialUUID.generate());
		dlFileEntryMetadataModel.setFileEntryMetadataId(
			InitContextUtil.getCounter().get());
		dlFileEntryMetadataModel.setDDMStorageId(ddmStorageLinkId);
		dlFileEntryMetadataModel.setDDMStructureId(ddmStructureId);
		dlFileEntryMetadataModel.setFileEntryId(
			dlFileVersionModel.getFileEntryId());
		dlFileEntryMetadataModel.setFileVersionId(
			dlFileVersionModel.getFileVersionId());

		return dlFileEntryMetadataModel;
	}

	public static DLFileEntryModel newDlFileEntryModel(
		DLFolderModel dlFolerModel, int index, long fileEntryId, long companyId,
		long userId, String userName, SimpleCounter futureDateCounter,
		int size) {

		DLFileEntryModel dlFileEntryModel = new DLFileEntryModelImpl();

		dlFileEntryModel.setUuid(SequentialUUID.generate());
		dlFileEntryModel.setFileEntryId(fileEntryId);
		dlFileEntryModel.setGroupId(dlFolerModel.getGroupId());
		dlFileEntryModel.setCompanyId(companyId);
		dlFileEntryModel.setUserId(userId);
		dlFileEntryModel.setUserName(userName);
		dlFileEntryModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
		dlFileEntryModel.setModifiedDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
		dlFileEntryModel.setRepositoryId(dlFolerModel.getRepositoryId());
		dlFileEntryModel.setFolderId(dlFolerModel.getFolderId());
		dlFileEntryModel.setName("TestFile" + index);
		dlFileEntryModel.setFileName("TestFile" + index + ".txt");
		dlFileEntryModel.setExtension("txt");
		dlFileEntryModel.setMimeType(ContentTypes.TEXT_PLAIN);
		dlFileEntryModel.setTitle("TestFile" + index + ".txt");
		dlFileEntryModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		dlFileEntryModel.setVersion(DLFileEntryConstants.VERSION_DEFAULT);
		dlFileEntryModel.setSize(size);
		dlFileEntryModel.setLastPublishDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));

		return dlFileEntryModel;
	}

	public static List<DLFileEntryModel> newDlFileEntryModels(
		DLFolderModel dlFolerModel) {

		int maxDLFileEntryCount = InitContextUtil.getMaxDLFileEntryCount();

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			maxDLFileEntryCount);

		for (int i = 1; i <= maxDLFileEntryCount; i++) {
			dlFileEntryModels.add(
				newDlFileEntryModel(
					dlFolerModel, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME,
					InitContextUtil.getFutureDateCounter(),
					InitContextUtil.getMaxDLFileEntrySize()));
		}

		return dlFileEntryModels;
	}

	public static DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		dlFileVersionModel.setUuid(SequentialUUID.generate());
		dlFileVersionModel.setFileVersionId(InitContextUtil.getCounter().get());
		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());
		dlFileVersionModel.setCompanyId(InitContextUtil.getCompanyId());
		dlFileVersionModel.setUserId(InitContextUtil.getSampleUserId());
		dlFileVersionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		dlFileVersionModel.setModifiedDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		dlFileVersionModel.setRepositoryId(dlFileEntryModel.getRepositoryId());
		dlFileVersionModel.setFolderId(dlFileEntryModel.getFolderId());
		dlFileVersionModel.setFileEntryId(dlFileEntryModel.getFileEntryId());
		dlFileVersionModel.setFileName(dlFileEntryModel.getFileName());
		dlFileVersionModel.setExtension(dlFileEntryModel.getExtension());
		dlFileVersionModel.setMimeType(dlFileEntryModel.getMimeType());
		dlFileVersionModel.setTitle(dlFileEntryModel.getTitle());
		dlFileVersionModel.setFileEntryTypeId(
			dlFileEntryModel.getFileEntryTypeId());
		dlFileVersionModel.setVersion(dlFileEntryModel.getVersion());
		dlFileVersionModel.setSize(dlFileEntryModel.getSize());
		dlFileVersionModel.setLastPublishDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));

		return dlFileVersionModel;
	}

	public static DLFolderModel newDLFolderModel(
		long groupId, long parentFolderId, int index, long folderId,
		long companyId, long sampleUserId, String userName,
		SimpleCounter futureDateCounter,
		DLFileEntryTypeModel defaultDLFileEntryTypeModel) {

		DLFolderModel dlFolderModel = new DLFolderModelImpl();

		dlFolderModel.setUuid(SequentialUUID.generate());
		dlFolderModel.setFolderId(folderId);
		dlFolderModel.setGroupId(groupId);
		dlFolderModel.setCompanyId(companyId);
		dlFolderModel.setUserId(sampleUserId);
		dlFolderModel.setUserName(userName);
		dlFolderModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
		dlFolderModel.setModifiedDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
		dlFolderModel.setRepositoryId(groupId);
		dlFolderModel.setParentFolderId(parentFolderId);
		dlFolderModel.setName("Test Folder " + index);
		dlFolderModel.setLastPostDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
		dlFolderModel.setDefaultFileEntryTypeId(
			defaultDLFileEntryTypeModel.getFileEntryTypeId());
		dlFolderModel.setLastPublishDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
		dlFolderModel.setStatusDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));

		return dlFolderModel;
	}

	public static List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		int maxDLFolderCount = InitContextUtil.getMaxDLFolderCount();

		List<DLFolderModel> dlFolderModels = new ArrayList<>(maxDLFolderCount);

		for (int i = 1; i <= maxDLFolderCount; i++) {
			dlFolderModels.add(
				newDLFolderModel(
					groupId, parentFolderId, i,
					InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME,
					InitContextUtil.getFutureDateCounter(),
					InitContextUtil.getDefaultDLFileEntryTypeModel()));
		}

		return dlFolderModels;
	}

}