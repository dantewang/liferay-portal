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
import com.liferay.document.library.kernel.model.DLFileEntryMetadataModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFileVersionModel;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringBundler;
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

	public DLDataFactory(UserDataFactory userDataFactory) throws Exception {
		_userDataFactory = userDataFactory;

		_initDLDDMStructureContent();
		_initDLFileEntryTypeModel();
	}

	public long getDefaultDLDDMStructureId() {
		return _defaultDLDDMStructureModel.getStructureId();
	}

	public DDMStructureLayoutModel getDefaultDLDDMStructureLayoutModel() {
		return _defaultDLDDMStructureLayoutModel;
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return _defaultDLDDMStructureModel;
	}

	public DDMStructureVersionModel getDefaultDLDDMStructureVersionModel() {
		return _defaultDLDDMStructureVersionModel;
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return _defaultDLFileEntryTypeModel;
	}

	public long getDLFileEntryClassNameId() {
		return getClassNameId(DLFileEntry.class);
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
		long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(ddmStorageLinkId);
		ddmStorageLinkModel.setClassNameId(getClassNameId(DDMContent.class));
		ddmStorageLinkModel.setClassPK(ddmContentModel.getContentId());
		ddmStorageLinkModel.setStructureId(structureId);
		ddmStorageLinkModel.setStructureVersionId(
			_defaultDLDDMStructureVersionModel.getStructureVersionId());

		return ddmStorageLinkModel;
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

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
		DLFolderModel dlFolderModel) {

		int maxDLFileEntryCount = PropsValues.MAX_DL_FILE_ENTRY_COUNT;

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			maxDLFileEntryCount);

		for (int i = 1; i <= maxDLFileEntryCount; i++) {
			dlFileEntryModels.add(_newDlFileEntryModel(dlFolderModel, i));
		}

		return dlFileEntryModels;
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		dlFileVersionModel.setUuid(SequentialUUID.generate());
		dlFileVersionModel.setFileVersionId(counter.get());
		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());
		dlFileVersionModel.setCompanyId(COMPANY_ID);
		dlFileVersionModel.setUserId(SAMPLE_USER_ID);
		dlFileVersionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(nextFutureDate());
		dlFileVersionModel.setModifiedDate(nextFutureDate());
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

		int maxDLFolderCount = PropsValues.MAX_DL_FOLDER_COUNT;

		List<DLFolderModel> dlFolderModels = new ArrayList<>(maxDLFolderCount);

		for (int i = 1; i <= maxDLFolderCount; i++) {
			dlFolderModels.add(_newDLFolderModel(groupId, parentFolderId, i));
		}

		return dlFolderModels;
	}

	private void _initDLDDMStructureContent() throws Exception {
		_dlDDMStructureContent = getResource(
			DataFactoryConstants.DL_DDM_STRUCTURE_CONTENT);
		_dlDDMStructureLayoutContent = getResource(
			DataFactoryConstants.DL_DDM_STRUCTURE_LAYOUT_CONTENT);
	}

	private void _initDLFileEntryTypeModel() {
		long groupId = _userDataFactory.getGlobalGroupId();

		_defaultDLFileEntryTypeModel = new DLFileEntryTypeModelImpl();

		_defaultDLFileEntryTypeModel.setUuid(SequentialUUID.generate());
		_defaultDLFileEntryTypeModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		_defaultDLFileEntryTypeModel.setCreateDate(nextFutureDate());
		_defaultDLFileEntryTypeModel.setModifiedDate(nextFutureDate());
		_defaultDLFileEntryTypeModel.setFileEntryTypeKey(
			StringUtil.toUpperCase(
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT));

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT);
		sb.append("</name></root>");

		_defaultDLFileEntryTypeModel.setName(sb.toString());

		_defaultDLFileEntryTypeModel.setLastPublishDate(nextFutureDate());

		_defaultDLDDMStructureModel = newDDMStructureModel(
			groupId, DEFAULT_USER_ID, getClassNameId(DLFileEntry.class),
			RawMetadataProcessor.TIKA_RAW_METADATA, _dlDDMStructureContent);

		_defaultDLDDMStructureVersionModel = newDDMStructureVersionModel(
			_defaultDLDDMStructureModel);

		_defaultDLDDMStructureLayoutModel = newDDMStructureLayoutModel(
			groupId, DEFAULT_USER_ID,
			_defaultDLDDMStructureVersionModel.getStructureVersionId(),
			_dlDDMStructureLayoutContent);
	}

	private DLFileEntryModel _newDlFileEntryModel(
		DLFolderModel dlFolderModel, int index) {

		DLFileEntryModel dlFileEntryModel = new DLFileEntryModelImpl();

		String name = DataFactoryConstants.DL_ENTRY_NAME_PREFIX + index;

		String fileName = name + "." + DataFactoryConstants.DL_EXTENSION;

		dlFileEntryModel.setUuid(SequentialUUID.generate());
		dlFileEntryModel.setFileEntryId(counter.get());
		dlFileEntryModel.setGroupId(dlFolderModel.getGroupId());
		dlFileEntryModel.setCompanyId(COMPANY_ID);
		dlFileEntryModel.setUserId(SAMPLE_USER_ID);
		dlFileEntryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFileEntryModel.setCreateDate(nextFutureDate());
		dlFileEntryModel.setModifiedDate(nextFutureDate());
		dlFileEntryModel.setRepositoryId(dlFolderModel.getRepositoryId());
		dlFileEntryModel.setFolderId(dlFolderModel.getFolderId());
		dlFileEntryModel.setName(name);
		dlFileEntryModel.setFileName(fileName);
		dlFileEntryModel.setExtension(DataFactoryConstants.DL_EXTENSION);
		dlFileEntryModel.setMimeType(ContentTypes.TEXT_PLAIN);
		dlFileEntryModel.setTitle(fileName);
		dlFileEntryModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		dlFileEntryModel.setVersion(DLFileEntryConstants.VERSION_DEFAULT);
		dlFileEntryModel.setSize(PropsValues.MAX_DL_FILE_ENTRY_SIZE);
		dlFileEntryModel.setLastPublishDate(nextFutureDate());

		return dlFileEntryModel;
	}

	private DLFolderModel _newDLFolderModel(
		long groupId, long parentFolderId, int index) {

		DLFolderModel dlFolderModel = new DLFolderModelImpl();

		dlFolderModel.setUuid(SequentialUUID.generate());
		dlFolderModel.setFolderId(counter.get());
		dlFolderModel.setGroupId(groupId);
		dlFolderModel.setCompanyId(COMPANY_ID);
		dlFolderModel.setUserId(SAMPLE_USER_ID);
		dlFolderModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFolderModel.setCreateDate(nextFutureDate());
		dlFolderModel.setModifiedDate(nextFutureDate());
		dlFolderModel.setRepositoryId(groupId);
		dlFolderModel.setParentFolderId(parentFolderId);
		dlFolderModel.setName(
			DataFactoryConstants.DL_FOLDER_NAME_PREFIX + index);
		dlFolderModel.setLastPostDate(nextFutureDate());
		dlFolderModel.setDefaultFileEntryTypeId(
			_defaultDLFileEntryTypeModel.getFileEntryTypeId());
		dlFolderModel.setLastPublishDate(nextFutureDate());
		dlFolderModel.setStatusDate(nextFutureDate());

		return dlFolderModel;
	}

	private DDMStructureLayoutModel _defaultDLDDMStructureLayoutModel;
	private DDMStructureModel _defaultDLDDMStructureModel;
	private DDMStructureVersionModel _defaultDLDDMStructureVersionModel;
	private DLFileEntryTypeModel _defaultDLFileEntryTypeModel;
	private String _dlDDMStructureContent;
	private String _dlDDMStructureLayoutContent;
	private final UserDataFactory _userDataFactory;

}