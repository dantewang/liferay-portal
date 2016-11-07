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

	public DLDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return _initContext.getDefaultDLFileEntryTypeModel();
	}

	public long getDLFileEntryClassNameId() {
		return _initContext.getClassNameId(
			DLFileEntry.class, _initContext.getClassNameModels());
	}

	public int getMaxDLFolderDepth() {
		return _initContext.getMaxDLFolderDepth();
	}

	public DDMContentModel newDDMContentModel(
		DLFileEntryModel dlFileEntryModel) {

		StringBundler sb = new StringBundler(6);

		SimpleCounter counter = _initContext.getCounter();

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [{");
		sb.append("\"instanceId\": \"");
		sb.append(StringUtil.randomId());
		sb.append("\", \"name\": \"CONTENT_TYPE\", \"value\": {\"en_US\": ");
		sb.append("\"text/plain\"}}]}");

		return _initContext.newDDMContentModel(
			counter.get(), dlFileEntryModel.getGroupId(),sb.toString());
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DLFileEntryMetadataModel dLFileEntryMetadataModel) {

		return _initContext.newDDMStructureLinkModel(
			_initContext.getClassNameId(
				DLFileEntryMetadata.class, _initContext.getClassNameModels()),
			dLFileEntryMetadataModel.getFileEntryMetadataId(),
			dLFileEntryMetadataModel.getDDMStructureId());
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		dlFileEntryMetadataModel.setUuid(SequentialUUID.generate());
		dlFileEntryMetadataModel.setFileEntryMetadataId(counter.get());
		dlFileEntryMetadataModel.setDDMStorageId(ddmStorageLinkId);
		dlFileEntryMetadataModel.setDDMStructureId(ddmStructureId);
		dlFileEntryMetadataModel.setFileEntryId(
			dlFileVersionModel.getFileEntryId());
		dlFileEntryMetadataModel.setFileVersionId(
			dlFileVersionModel.getFileVersionId());

		return dlFileEntryMetadataModel;
	}

	public List<DLFileEntryModel> newDlFileEntryModels(
		DLFolderModel dlFolerModel) {

		int maxDLFileEntryCount = _initContext.getMaxDLFileEntryCount();

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			maxDLFileEntryCount);

		SimpleCounter counter = _initContext.getCounter();

		for (int i = 1; i <= maxDLFileEntryCount; i++) {
			dlFileEntryModels.add(
				newDlFileEntryModel(
					dlFolerModel, i, counter.get(),
					_initContext.getCompanyId(), _initContext.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME,
					_initContext.getFutureDateCounter(),
					_initContext.getMaxDLFileEntrySize()));
		}

		return dlFileEntryModels;
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		dlFileVersionModel.setUuid(SequentialUUID.generate());
		dlFileVersionModel.setFileVersionId(counter.get());
		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());
		dlFileVersionModel.setCompanyId(_initContext.getCompanyId());
		dlFileVersionModel.setUserId(_initContext.getSampleUserId());
		dlFileVersionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(
			_initContext.nextFutureDate(_initContext.getFutureDateCounter()));
		dlFileVersionModel.setModifiedDate(
			_initContext.nextFutureDate(_initContext.getFutureDateCounter()));
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
			_initContext.nextFutureDate(_initContext.getFutureDateCounter()));

		return dlFileVersionModel;
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		int maxDLFolderCount = _initContext.getMaxDLFolderCount();

		List<DLFolderModel> dlFolderModels = new ArrayList<>(maxDLFolderCount);

		SimpleCounter counter = _initContext.getCounter();

		for (int i = 1; i <= maxDLFolderCount; i++) {
			dlFolderModels.add(
				newDLFolderModel(
					groupId, parentFolderId, i, counter.get(),
					_initContext.getCompanyId(), _initContext.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME,
					_initContext.getFutureDateCounter(),
					_initContext.getDefaultDLFileEntryTypeModel()));
		}

		return dlFolderModels;
	}

	protected DLFileEntryModel newDlFileEntryModel(
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
			_initContext.nextFutureDate(futureDateCounter));
		dlFileEntryModel.setModifiedDate(
			_initContext.nextFutureDate(futureDateCounter));
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
			_initContext.nextFutureDate(futureDateCounter));

		return dlFileEntryModel;
	}

	protected DLFolderModel newDLFolderModel(
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
			_initContext.nextFutureDate(futureDateCounter));
		dlFolderModel.setModifiedDate(
			_initContext.nextFutureDate(futureDateCounter));
		dlFolderModel.setRepositoryId(groupId);
		dlFolderModel.setParentFolderId(parentFolderId);
		dlFolderModel.setName("Test Folder " + index);
		dlFolderModel.setLastPostDate(
			_initContext.nextFutureDate(futureDateCounter));
		dlFolderModel.setDefaultFileEntryTypeId(
			defaultDLFileEntryTypeModel.getFileEntryTypeId());
		dlFolderModel.setLastPublishDate(
			_initContext.nextFutureDate(futureDateCounter));
		dlFolderModel.setStatusDate(
			_initContext.nextFutureDate(futureDateCounter));

		return dlFolderModel;
	}

	private final InitContext _initContext;

}