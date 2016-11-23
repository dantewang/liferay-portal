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
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryMetadataModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class DLDataFactory extends BaseDataFactory {

	public DLDataFactory(
			InitContext initContext, UserDataFactory userDataFactory)
		throws Exception {

		super(initContext);
		_userDataFactory = userDataFactory;
		_initDLDDMStructureContent();
		_initDLFileEntryTypeModel();
	}

	public long getDefaultDLDDMStructureId() {
		return _defaultDLDDMStructureModel.getStructureId();
	}

	public DDMStructureLayoutModel
		getDefaultDLDDMStructureLayoutModel() {

		return _defaultDLDDMStructureLayoutModel;
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return _defaultDLDDMStructureModel;
	}

	public DDMStructureVersionModel
		getDefaultDLDDMStructureVersionModel() {

		return _defaultDLDDMStructureVersionModel;
	}

	public DLFileEntryTypeModel getDefaultDLFileEntryTypeModel() {
		return _defaultDLFileEntryTypeModel;
	}

	public long getDLFileEntryClassNameId() {
		return getClassNameId(
			DLFileEntry.class, initContext.getClassNameModels());
	}

	public DDMContentModel newDDMContentModel(
		DLFileEntryModel dlFileEntryModel) {

		StringBundler sb = new StringBundler(6);

		SimpleCounter counter = initContext.getCounter();

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [{");
		sb.append("\"instanceId\": \"");
		sb.append(StringUtil.randomId());
		sb.append("\", \"name\": \"CONTENT_TYPE\", \"value\": {\"en_US\": ");
		sb.append("\"text/plain\"}}]}");

		return newDDMContentModel(
			counter.get(), dlFileEntryModel.getGroupId(), sb.toString());
	}

	public DLFileEntryMetadataModel newDLFileEntryMetadataModel(
		long ddmStorageLinkId, long ddmStructureId,
		DLFileVersionModel dlFileVersionModel) {

		DLFileEntryMetadataModel dlFileEntryMetadataModel =
			new DLFileEntryMetadataModelImpl();

		SimpleCounter counter = initContext.getCounter();

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

		int maxDLFileEntryCount = initContext.getMaxDLFileEntryCount();

		List<DLFileEntryModel> dlFileEntryModels = new ArrayList<>(
			maxDLFileEntryCount);

		for (int i = 1; i <= maxDLFileEntryCount; i++) {
			dlFileEntryModels.add(newDlFileEntryModel(dlFolerModel, i));
		}

		return dlFileEntryModels;
	}

	public DLFileVersionModel newDLFileVersionModel(
		DLFileEntryModel dlFileEntryModel) {

		DLFileVersionModel dlFileVersionModel = new DLFileVersionModelImpl();

		SimpleCounter counter = initContext.getCounter();

		dlFileVersionModel.setUuid(SequentialUUID.generate());
		dlFileVersionModel.setFileVersionId(counter.get());
		dlFileVersionModel.setGroupId(dlFileEntryModel.getGroupId());
		dlFileVersionModel.setCompanyId(initContext.getCompanyId());
		dlFileVersionModel.setUserId(initContext.getSampleUserId());
		dlFileVersionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFileVersionModel.setCreateDate(
			nextFutureDate(initContext.getFutureDateCounter()));
		dlFileVersionModel.setModifiedDate(
			nextFutureDate(initContext.getFutureDateCounter()));
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
			nextFutureDate(initContext.getFutureDateCounter()));

		return dlFileVersionModel;
	}

	public List<DLFolderModel> newDLFolderModels(
		long groupId, long parentFolderId) {

		int maxDLFolderCount = initContext.getMaxDLFolderCount();

		List<DLFolderModel> dlFolderModels = new ArrayList<>(maxDLFolderCount);

		for (int i = 1; i <= maxDLFolderCount; i++) {
			dlFolderModels.add(newDLFolderModel(groupId, parentFolderId, i));
		}

		return dlFolderModels;
	}

	protected DLFileEntryModel newDlFileEntryModel(
		DLFolderModel dlFolerModel, int index) {

		DLFileEntryModel dlFileEntryModel = new DLFileEntryModelImpl();

		SimpleCounter counter = initContext.getCounter();
		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		String name = DataFactoryConstants.DL_ENTRY_NAME_PREFIX + index;

		String fileName = name + "." + DataFactoryConstants.DL_EXTENSION;

		dlFileEntryModel.setUuid(SequentialUUID.generate());
		dlFileEntryModel.setFileEntryId(counter.get());
		dlFileEntryModel.setGroupId(dlFolerModel.getGroupId());
		dlFileEntryModel.setCompanyId(initContext.getCompanyId());
		dlFileEntryModel.setUserId(initContext.getSampleUserId());
		dlFileEntryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFileEntryModel.setCreateDate(nextFutureDate(futureDateCounter));
		dlFileEntryModel.setModifiedDate(nextFutureDate(futureDateCounter));
		dlFileEntryModel.setRepositoryId(dlFolerModel.getRepositoryId());
		dlFileEntryModel.setFolderId(dlFolerModel.getFolderId());
		dlFileEntryModel.setName(name);
		dlFileEntryModel.setFileName(fileName);
		dlFileEntryModel.setExtension(DataFactoryConstants.DL_EXTENSION);
		dlFileEntryModel.setMimeType(ContentTypes.TEXT_PLAIN);
		dlFileEntryModel.setTitle(fileName);
		dlFileEntryModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		dlFileEntryModel.setVersion(DLFileEntryConstants.VERSION_DEFAULT);
		dlFileEntryModel.setSize(initContext.getMaxDLFileEntrySize());
		dlFileEntryModel.setLastPublishDate(nextFutureDate(futureDateCounter));

		return dlFileEntryModel;
	}

	protected DLFolderModel newDLFolderModel(
		long groupId, long parentFolderId, int index) {

		DLFolderModel dlFolderModel = new DLFolderModelImpl();

		SimpleCounter counter = initContext.getCounter();
		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		dlFolderModel.setUuid(SequentialUUID.generate());
		dlFolderModel.setFolderId(counter.get());
		dlFolderModel.setGroupId(groupId);
		dlFolderModel.setCompanyId(initContext.getCompanyId());
		dlFolderModel.setUserId(initContext.getSampleUserId());
		dlFolderModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		dlFolderModel.setCreateDate(nextFutureDate(futureDateCounter));
		dlFolderModel.setModifiedDate(nextFutureDate(futureDateCounter));
		dlFolderModel.setRepositoryId(groupId);
		dlFolderModel.setParentFolderId(parentFolderId);
		dlFolderModel.setName(
			DataFactoryConstants.DL_FOLDER_NAME_PREFIX + index);
		dlFolderModel.setLastPostDate(nextFutureDate(futureDateCounter));
		dlFolderModel.setDefaultFileEntryTypeId(
			_defaultDLFileEntryTypeModel.getFileEntryTypeId());
		dlFolderModel.setLastPublishDate(nextFutureDate(futureDateCounter));
		dlFolderModel.setStatusDate(nextFutureDate(futureDateCounter));

		return dlFolderModel;
	}

	private void _initDLDDMStructureContent() throws Exception {
		_dlDDMStructureContent = getResource(
			DataFactoryConstants.DL_DDM_STRUCTURE_CONTENT);
		_dlDDMStructureLayoutContent = getResource(
			DataFactoryConstants.DL_DDM_STRUCTURE_LAYOUT_CONTENT);
	}

	private void _initDLFileEntryTypeModel() {
		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		Date createDate = nextFutureDate(futureDateCounter);
		Date lastPublishDate = nextFutureDate(futureDateCounter);
		Date modifiedDate = nextFutureDate(futureDateCounter);

		long groupId = _userDataFactory.getGlobalGroupId();
		long userId = initContext.getDefaultUserId();

		Map<String, ClassNameModel> classNameModels =
			initContext.getClassNameModels();

		_defaultDLFileEntryTypeModel = new DLFileEntryTypeModelImpl();

		_defaultDLFileEntryTypeModel.setUuid(SequentialUUID.generate());
		_defaultDLFileEntryTypeModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		_defaultDLFileEntryTypeModel.setCreateDate(createDate);
		_defaultDLFileEntryTypeModel.setModifiedDate(modifiedDate);
		_defaultDLFileEntryTypeModel.setFileEntryTypeKey(
			StringUtil.toUpperCase(
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT));

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT);
		sb.append("</name></root>");

		_defaultDLFileEntryTypeModel.setName(sb.toString());

		_defaultDLFileEntryTypeModel.setLastPublishDate(lastPublishDate);

		_defaultDLDDMStructureModel = newDDMStructureModel(
			groupId, userId, getClassNameId(DLFileEntry.class, classNameModels),
			RawMetadataProcessor.TIKA_RAW_METADATA, _dlDDMStructureContent);

		_defaultDLDDMStructureVersionModel = newDDMStructureVersionModel(
			_defaultDLDDMStructureModel);

		_defaultDLDDMStructureLayoutModel = newDDMStructureLayoutModel(
			groupId, userId,
			_defaultDLDDMStructureVersionModel.getStructureVersionId(),
			_dlDDMStructureLayoutContent);
	}

	private DDMStructureLayoutModel _defaultDLDDMStructureLayoutModel;
	private DDMStructureModel _defaultDLDDMStructureModel;
	private DDMStructureVersionModel _defaultDLDDMStructureVersionModel;
	private DLFileEntryTypeModel _defaultDLFileEntryTypeModel;
	private String _dlDDMStructureContent;
	private String _dlDDMStructureLayoutContent;
	private final UserDataFactory _userDataFactory;

}