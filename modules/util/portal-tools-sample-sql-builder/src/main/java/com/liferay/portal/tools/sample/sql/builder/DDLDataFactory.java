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

import com.liferay.dynamic.data.lists.model.DDLRecordConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordModel;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.lists.model.DDLRecordVersionModel;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetModelImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.util.SimpleCounter;

import java.util.Date;

/**
 * @author Lily Chi
 */
public class DDLDataFactory {

	public DDLDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public long getDDLRecordSetClassNameId() {
		return _initContext.getClassNameId(
			DDLRecordSet.class, _initContext.getClassNameModels());
	}

	public long getDefaultDLDDMStructureId() {
		return _initContext.getDefaultDLDDMStructureModel().getStructureId();
	}

	public DDMStructureLayoutModel
		getDefaultDLDDMStructureLayoutModel() {

			return _initContext.getDefaultDLDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultDLDDMStructureModel() {
		return _initContext.getDefaultDLDDMStructureModel();
	}

	public DDMStructureVersionModel
		getDefaultDLDDMStructureVersionModel() {

			return _initContext.getDefaultDLDDMStructureVersionModel();
	}

	public int getMaxDDLRecordCount() {
		return _initContext.getMaxDDLRecordCount();
	}

	public int getMaxDDLRecordSetCount() {
		return _initContext.getMaxDDLRecordSetCount();
	}

	public DDMStructureLayoutModel newDDLDDMStructureLayoutModel(
		long groupId, DDMStructureVersionModel ddmStructureVersionModel) {

		int maxDDLCustomFieldCount = _initContext.getMaxDDLCustomFieldCount();

		StringBundler sb = new StringBundler(4 + maxDDLCustomFieldCount * 4);

		SimpleCounter counter = _initContext.getCounter();

		sb.append("{\"defaultLanguageId\": \"en_US\", \"pages\": [{\"rows\": ");
		sb.append("[");

		for (int i = 0; i < maxDDLCustomFieldCount; i++) {
			sb.append("{\"columns\": [{\"fieldNames\": [\"");
			sb.append(nextDDLCustomFieldName(groupId, i));
			sb.append("\"], \"size\": 12}]}");
			sb.append(", ");
		}

		if (maxDDLCustomFieldCount > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("], \"title\": {\"en_US\": \"\"}}],\"paginationMode\": ");
		sb.append("\"single-page\"}");

		return _initContext.newDDMStructureLayoutModel(
			_initContext.getGlobalGroupId(), _initContext.getDefaultUserId(),
			ddmStructureVersionModel.getStructureVersionId(), sb.toString(),
			counter.get(), _initContext.getCompanyId(),
			DataFactoryConstants.SAMPLE_USER_NAME,
			_initContext.getFutureDateCounter());
	}

	public DDMStructureModel newDDLDDMStructureModel(long groupId) {
		int maxDDLCustomFieldCount = _initContext.getMaxDDLCustomFieldCount();

		StringBundler sb = new StringBundler(3 + maxDDLCustomFieldCount * 9);

		SimpleCounter counter = _initContext.getCounter();

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fields\": [");

		for (int i = 0; i < maxDDLCustomFieldCount; i++) {
			sb.append(
				"{\"dataType\": \"string\", \"indexType\": \"keyword\", ");
			sb.append("\"label\": {\"en_US\": \"Text");
			sb.append(i);
			sb.append("\"}, \"name\": \"");
			sb.append(nextDDLCustomFieldName(groupId, i));
			sb.append("\", \"readOnly\": false, \"repeatable\": false,");
			sb.append("\"required\": false, \"showLabel\": true, \"type\": ");
			sb.append("\"text\"}");
			sb.append(",");
		}

		if (maxDDLCustomFieldCount > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("]}");

		return _initContext.newDDMStructureModel(
			groupId, _initContext.getSampleUserId(),
			_initContext.getClassNameId(
				DDLRecordSet.class, _initContext.getClassNameModels()),
			"Test DDM Structure", sb.toString(), counter.get(),
			_initContext.getCompanyId(), DataFactoryConstants.SAMPLE_USER_NAME,
			_initContext.getFutureDateCounter());
	}

	public DDLRecordModel newDDLRecordModel(
		DDLRecordSetModel dDLRecordSetModel) {

		DDLRecordModel ddlRecordModel = new DDLRecordModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		ddlRecordModel.setUuid(SequentialUUID.generate());
		ddlRecordModel.setRecordId(counter.get());
		ddlRecordModel.setGroupId(dDLRecordSetModel.getGroupId());
		ddlRecordModel.setCompanyId(_initContext.getCompanyId());
		ddlRecordModel.setUserId(_initContext.getSampleUserId());
		ddlRecordModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordModel.setVersionUserId(_initContext.getSampleUserId());
		ddlRecordModel.setVersionUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordModel.setCreateDate(new Date());
		ddlRecordModel.setModifiedDate(new Date());
		ddlRecordModel.setDDMStorageId(counter.get());
		ddlRecordModel.setRecordSetId(dDLRecordSetModel.getRecordSetId());
		ddlRecordModel.setVersion(DDLRecordConstants.VERSION_DEFAULT);
		ddlRecordModel.setDisplayIndex(
			DDLRecordConstants.DISPLAY_INDEX_DEFAULT);
		ddlRecordModel.setLastPublishDate(new Date());

		return ddlRecordModel;
	}

	public DDLRecordSetModel newDDLRecordSetModel(
		DDMStructureModel ddmStructureModel, int currentIndex) {

		DDLRecordSetModel ddlRecordSetModel = new DDLRecordSetModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		ddlRecordSetModel.setUuid(SequentialUUID.generate());
		ddlRecordSetModel.setRecordSetId(counter.get());
		ddlRecordSetModel.setGroupId(ddmStructureModel.getGroupId());
		ddlRecordSetModel.setCompanyId(_initContext.getCompanyId());
		ddlRecordSetModel.setUserId(_initContext.getSampleUserId());
		ddlRecordSetModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordSetModel.setCreateDate(new Date());
		ddlRecordSetModel.setModifiedDate(new Date());
		ddlRecordSetModel.setDDMStructureId(ddmStructureModel.getStructureId());
		ddlRecordSetModel.setRecordSetKey(
			String.valueOf(counter.get()));

		StringBundler sb = new StringBundler(5);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append("Test DDL Record Set ");
		sb.append(currentIndex);
		sb.append("</name></root>");

		ddlRecordSetModel.setName(sb.toString());

		ddlRecordSetModel.setMinDisplayRows(
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT);
		ddlRecordSetModel.setScope(
			DDLRecordSetConstants.SCOPE_DYNAMIC_DATA_LISTS);
		ddlRecordSetModel.setSettings(StringPool.BLANK);
		ddlRecordSetModel.setLastPublishDate(new Date());

		return ddlRecordSetModel;
	}

	public DDLRecordVersionModel newDDLRecordVersionModel(
		DDLRecordModel dDLRecordModel) {

		DDLRecordVersionModel ddlRecordVersionModel =
			new DDLRecordVersionModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		ddlRecordVersionModel.setRecordVersionId(counter.get());
		ddlRecordVersionModel.setGroupId(dDLRecordModel.getGroupId());
		ddlRecordVersionModel.setCompanyId(_initContext.getCompanyId());
		ddlRecordVersionModel.setUserId(_initContext.getSampleUserId());
		ddlRecordVersionModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		ddlRecordVersionModel.setCreateDate(dDLRecordModel.getModifiedDate());
		ddlRecordVersionModel.setDDMStorageId(dDLRecordModel.getDDMStorageId());
		ddlRecordVersionModel.setRecordSetId(dDLRecordModel.getRecordSetId());
		ddlRecordVersionModel.setRecordId(dDLRecordModel.getRecordId());
		ddlRecordVersionModel.setVersion(dDLRecordModel.getVersion());
		ddlRecordVersionModel.setDisplayIndex(dDLRecordModel.getDisplayIndex());
		ddlRecordVersionModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		ddlRecordVersionModel.setStatusDate(dDLRecordModel.getModifiedDate());

		return ddlRecordVersionModel;
	}

	public DDMContentModel newDDMContentModel(
		DDLRecordModel ddlRecordModel, int currentIndex) {

		int maxDDLCustomFieldCount = _initContext.getMaxDDLCustomFieldCount();

		StringBundler sb = new StringBundler(3 + maxDDLCustomFieldCount * 7);

		sb.append("{\"availableLanguageIds\": [\"en_US\"],");
		sb.append("\"defaultLanguageId\": \"en_US\", \"fieldValues\": [");

		for (int i = 0; i < maxDDLCustomFieldCount; i++) {
			sb.append("{\"instanceId\": \"");
			sb.append(StringUtil.randomId());
			sb.append("\", \"name\": \"");
			sb.append(nextDDLCustomFieldName(ddlRecordModel.getGroupId(), i));
			sb.append("\", \"value\": {\"en_US\": \"Test Record ");
			sb.append(currentIndex);
			sb.append("\"}},");
		}

		if (maxDDLCustomFieldCount > 0) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("]}");

		return _initContext.newDDMContentModel(
			ddlRecordModel.getDDMStorageId(), ddlRecordModel.getGroupId(),
			sb.toString());
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		long ddmStorageLinkId, DDMContentModel ddmContentModel,
		long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(ddmStorageLinkId);
		ddmStorageLinkModel.setClassNameId(
			_initContext.getClassNameId(
				DDMContent.class, _initContext.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(ddmContentModel.getContentId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
	}

	public DDMStructureLinkModel newDDMStructureLinkModel(
		DDLRecordSetModel ddlRecordSetModel) {

		return _initContext.newDDMStructureLinkModel(
			_initContext.getClassNameId(
				DDLRecordSet.class, _initContext.getClassNameModels()),
			ddlRecordSetModel.getRecordSetId(),
			ddlRecordSetModel.getDDMStructureId());
	}

	protected String nextDDLCustomFieldName(
		long groupId, int customFieldIndex) {

		StringBundler sb = new StringBundler(4);

		sb.append("custom_field_text_");
		sb.append(groupId);
		sb.append("_");
		sb.append(customFieldIndex);

		return sb.toString();
	}

	private final InitContext _initContext;

}