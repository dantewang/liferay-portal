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

import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFileVersionModel;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lily Chi
 */
public class DLDataFactory extends BaseDDMDataFactory {

	public static DLDataFactory getInstance() {
		return _dlDataFactory;
	}

	public long getDefaultDLDDMStructureId() {
		return _defaultDLDDMStructureId;
	}

	public int getMaxDLFolderDepth() {
		return BenchmarksPropsValues.MAX_DL_FOLDER_DEPTH;
	}

	public DDMContentModel newDDMContentModel(
		DLFileEntryModel dlFileEntryModel) {

		StringBundler sb = new StringBundler(6);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [{");
		sb.append("\"instanceId\": \"");
		sb.append(StringUtil.randomId());
		sb.append("\", \"name\": \"CONTENT_TYPE\", \"value\": {\"en_US\": ");
		sb.append("\"text/plain\"}}]}");

		return newDDMContentModel(
			counter.get(), dlFileEntryModel.getGroupId(), sb.toString());
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		long ddmStorageLinkId, DDMContentModel ddmContentModel,
		long structureId, long classNameId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		// UUID

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());

		// PK fields

		ddmStorageLinkModel.setStorageLinkId(ddmStorageLinkId);

		// Other fields

		ddmStorageLinkModel.setClassNameId(classNameId);
		ddmStorageLinkModel.setClassPK(ddmContentModel.getContentId());
		ddmStorageLinkModel.setStructureId(structureId);
		ddmStorageLinkModel.setStructureVersionId(
			_defaultDLDDMStructureVersionId);

		return ddmStorageLinkModel;
	}

	public DDMStructureLayoutModel newDefaultDLDDMStructureLayoutModel() {
		return newDDMStructureLayoutModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, _defaultDLDDMStructureVersionId,
			_dlDDMStructureLayoutContent);
	}

	public DDMStructureModel newDefaultDLDDMStructureModel(long classNameId) {
		return newDDMStructureModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, classNameId,
			RawMetadataProcessor.TIKA_RAW_METADATA, _dlDDMStructureContent,
			_defaultDLDDMStructureId);
	}

	public DDMStructureVersionModel newDefaultDLDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		return newDDMStructureVersionModel(
			ddmStructureModel, _defaultDLDDMStructureVersionId);
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

		// UUID

		dlFileEntryMetadataModel.setUuid(SequentialUUID.generate());

		// PK fields

		dlFileEntryMetadataModel.setFileEntryMetadataId(counter.get());

		// Other fields

		dlFileEntryMetadataModel.setDDMStorageId(ddmStorageLinkId);
		dlFileEntryMetadataModel.setDDMStructureId(ddmStructureId);
		dlFileEntryMetadataModel.setFileEntryId(
			dlFileVersionModel.getFileEntryId());
		dlFileEntryMetadataModel.setFileVersionId(
			dlFileVersionModel.getFileVersionId());

		return dlFileEntryMetadataModel;
	}

	public List<DLFileEntryModel> newDlFileEntryModels(
		DLFolderModel dlFolderModel) {

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_DL_FILE_ENTRY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_DL_FILE_ENTRY_COUNT;
			 i++) {

			dlFileEntryModels.add(_newDlFileEntryModel(dlFolderModel, i));
		}

		return dlFileEntryModels;
	}

	public DLFileEntryTypeModel newDLFileEntryTypeModel() {
		DLFileEntryTypeModel defaultDLFileEntryTypeModel =
			new DLFileEntryTypeModelImpl();

		// UUID

		defaultDLFileEntryTypeModel.setUuid(SequentialUUID.generate());

		// PK fields

		defaultDLFileEntryTypeModel.setFileEntryTypeId(
			_defaultDLFileEntryTypeId);

		// Audit fields

		defaultDLFileEntryTypeModel.setCreateDate(nextFutureDate());
		defaultDLFileEntryTypeModel.setModifiedDate(nextFutureDate());

		// Other fields

		defaultDLFileEntryTypeModel.setFileEntryTypeKey(
			StringUtil.toUpperCase(
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT));

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT);
		sb.append("</name></root>");

		defaultDLFileEntryTypeModel.setName(sb.toString());

		defaultDLFileEntryTypeModel.setLastPublishDate(nextFutureDate());

		return defaultDLFileEntryTypeModel;
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		// UUID

		dlFileVersionModel.setUuid(SequentialUUID.generate());

		// PK fields

		dlFileVersionModel.setFileVersionId(counter.get());

		// Group instance

		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());

		// Audit fields

		dlFileVersionModel.setCompanyId(COMPANY_ID);
		dlFileVersionModel.setUserId(SAMPLE_USER_ID);
		dlFileVersionModel.setUserName(SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(nextFutureDate());
		dlFileVersionModel.setModifiedDate(nextFutureDate());

		// Other fields

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
		dlFileVersionModel.setLastPublishDate(nextFutureDate());

		return dlFileVersionModel;
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		List<DLFolderModel> dlFolderModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_DL_FOLDER_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_DL_FOLDER_COUNT; i++) {
			dlFolderModels.add(_newDLFolderModel(groupId, parentFolderId, i));
		}

		return dlFolderModels;
	}

	private DLDataFactory() {
		_defaultDLDDMStructureId = counter.get();
		_defaultDLDDMStructureVersionId = counter.get();

		_dlDDMStructureContent = readFile("ddm_structure_basic_document.json");
		_dlDDMStructureLayoutContent = readFile(
			"ddm_structure_layout_basic_document.json");
	}

	private DLFileEntryModel _newDlFileEntryModel(
		DLFolderModel dlFolderModel, int index) {

		DLFileEntryModel dlFileEntryModel = new DLFileEntryModelImpl();

		// UUID

		dlFileEntryModel.setUuid(SequentialUUID.generate());

		// PK fields

		dlFileEntryModel.setFileEntryId(counter.get());

		// Group instance

		dlFileEntryModel.setGroupId(dlFolderModel.getGroupId());

		// Audit fields

		dlFileEntryModel.setCompanyId(COMPANY_ID);
		dlFileEntryModel.setUserId(SAMPLE_USER_ID);
		dlFileEntryModel.setUserName(SAMPLE_USER_NAME);
		dlFileEntryModel.setCreateDate(nextFutureDate());
		dlFileEntryModel.setModifiedDate(nextFutureDate());

		// Other fields

		dlFileEntryModel.setRepositoryId(dlFolderModel.getRepositoryId());
		dlFileEntryModel.setFolderId(dlFolderModel.getFolderId());
		dlFileEntryModel.setName("TestFile" + index);
		dlFileEntryModel.setFileName("TestFile" + index + ".txt");
		dlFileEntryModel.setExtension("txt");
		dlFileEntryModel.setMimeType(ContentTypes.TEXT_PLAIN);
		dlFileEntryModel.setTitle("TestFile" + index + ".txt");
		dlFileEntryModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		dlFileEntryModel.setVersion(DLFileEntryConstants.VERSION_DEFAULT);
		dlFileEntryModel.setSize(BenchmarksPropsValues.MAX_DL_FILE_ENTRY_SIZE);
		dlFileEntryModel.setLastPublishDate(nextFutureDate());

		return dlFileEntryModel;
	}

	private DLFolderModel _newDLFolderModel(
		long groupId, long parentFolderId, int index) {

		DLFolderModel dlFolderModel = new DLFolderModelImpl();

		// UUID

		dlFolderModel.setUuid(SequentialUUID.generate());

		// PK fields

		dlFolderModel.setFolderId(counter.get());

		// Group instance

		dlFolderModel.setGroupId(groupId);

		// Audit fields

		dlFolderModel.setCompanyId(COMPANY_ID);
		dlFolderModel.setUserId(SAMPLE_USER_ID);
		dlFolderModel.setUserName(SAMPLE_USER_NAME);
		dlFolderModel.setCreateDate(nextFutureDate());
		dlFolderModel.setModifiedDate(nextFutureDate());

		// Other fields

		dlFolderModel.setRepositoryId(groupId);
		dlFolderModel.setParentFolderId(parentFolderId);
		dlFolderModel.setName("Test Folder " + index);
		dlFolderModel.setLastPostDate(nextFutureDate());
		dlFolderModel.setDefaultFileEntryTypeId(_defaultDLFileEntryTypeId);
		dlFolderModel.setLastPublishDate(nextFutureDate());
		dlFolderModel.setStatusDate(nextFutureDate());

		return dlFolderModel;
	}

	private static DLDataFactory _dlDataFactory = new DLDataFactory();

	private final long _defaultDLDDMStructureId;
	private final long _defaultDLDDMStructureVersionId;
	private long _defaultDLFileEntryTypeId =
		DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT;
	private final String _dlDDMStructureContent;
	private final String _dlDDMStructureLayoutContent;

}