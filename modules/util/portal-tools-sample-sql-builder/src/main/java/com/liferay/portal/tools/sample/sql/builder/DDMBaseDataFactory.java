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

import com.liferay.dynamic.data.mapping.model.DDMContentModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public abstract class DDMBaseDataFactory extends BaseDataFactory {

	public DDMBaseDataFactory(InitContext initContext) {
		super(initContext);
	}

	public String getResource(String resourceName) throws Exception {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(getResourceInputStream(resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	public DDMContentModel newDDMContentModel(
		long contentId, long groupId, String data) {

		DDMContentModel ddmContentModel = new DDMContentModelImpl();

		long companyId = initContext.getCompanyId();
		long sampleUserId = initContext.getSampleUserId();

		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		Date createDate = nextFutureDate(futureDateCounter);
		Date modifiedDate = nextFutureDate(futureDateCounter);

		ddmContentModel.setUuid(SequentialUUID.generate());
		ddmContentModel.setContentId(contentId);
		ddmContentModel.setGroupId(groupId);
		ddmContentModel.setCompanyId(companyId);
		ddmContentModel.setUserId(sampleUserId);
		ddmContentModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddmContentModel.setCreateDate(createDate);
		ddmContentModel.setModifiedDate(modifiedDate);
		ddmContentModel.setName(DDMStorageLink.class.getName());
		ddmContentModel.setData(data);

		return ddmContentModel;
	}

	public DDMStructureLayoutModel newDDMStructureLayoutModel(
		long groupId, long userId, long structureVersionId, String definition) {

		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		Date createDate = nextFutureDate(futureDateCounter);
		Date modifiedDate = nextFutureDate(futureDateCounter);

		DDMStructureLayoutModel ddmStructureLayoutModel =
			new DDMStructureLayoutModelImpl();

		ddmStructureLayoutModel.setUuid(SequentialUUID.generate());
		ddmStructureLayoutModel.setStructureLayoutId(
			initContext.getCounter().get());
		ddmStructureLayoutModel.setGroupId(groupId);
		ddmStructureLayoutModel.setCompanyId(initContext.getCompanyId());
		ddmStructureLayoutModel.setUserId(userId);
		ddmStructureLayoutModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		ddmStructureLayoutModel.setCreateDate(createDate);
		ddmStructureLayoutModel.setModifiedDate(modifiedDate);
		ddmStructureLayoutModel.setStructureVersionId(structureVersionId);
		ddmStructureLayoutModel.setDefinition(definition);

		return ddmStructureLayoutModel;
	}

	public DDMStructureModel newDDMStructureModel(
		long groupId, long userId, long classNameId, String structureKey,
		String definition) {

		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		Date createDate = nextFutureDate(futureDateCounter);
		Date lastPublishDate = nextFutureDate(futureDateCounter);
		Date modifiedDate = nextFutureDate(futureDateCounter);

		DDMStructureModel ddmStructureModel = new DDMStructureModelImpl();

		ddmStructureModel.setUuid(SequentialUUID.generate());
		ddmStructureModel.setStructureId(initContext.getCounter().get());
		ddmStructureModel.setGroupId(groupId);
		ddmStructureModel.setCompanyId(initContext.getCompanyId());
		ddmStructureModel.setUserId(userId);
		ddmStructureModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		ddmStructureModel.setVersionUserId(userId);
		ddmStructureModel.setVersionUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		ddmStructureModel.setCreateDate(createDate);
		ddmStructureModel.setModifiedDate(modifiedDate);
		ddmStructureModel.setClassNameId(classNameId);
		ddmStructureModel.setStructureKey(structureKey);
		ddmStructureModel.setVersion(DDMStructureConstants.VERSION_DEFAULT);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(structureKey);
		sb.append("</name></root>");

		ddmStructureModel.setName(sb.toString());

		ddmStructureModel.setDefinition(definition);
		ddmStructureModel.setStorageType(StorageType.JSON.toString());
		ddmStructureModel.setLastPublishDate(lastPublishDate);

		return ddmStructureModel;
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		SimpleCounter counter = initContext.getCounter();

		long companyId = initContext.getCompanyId();

		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		Date createDate = nextFutureDate(futureDateCounter);
		Date statusDate = nextFutureDate(futureDateCounter);

		DDMStructureVersionModel ddmStructureVersionModel =
			new DDMStructureVersionModelImpl();

		ddmStructureVersionModel.setStructureVersionId(counter.get());
		ddmStructureVersionModel.setGroupId(ddmStructureModel.getGroupId());
		ddmStructureVersionModel.setCompanyId(companyId);
		ddmStructureVersionModel.setUserId(ddmStructureModel.getUserId());
		ddmStructureVersionModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		ddmStructureVersionModel.setCreateDate(createDate);
		ddmStructureVersionModel.setStructureId(
			ddmStructureModel.getStructureId());
		ddmStructureVersionModel.setVersion(
			DDMStructureConstants.VERSION_DEFAULT);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(ddmStructureModel.getStructureKey());
		sb.append("</name></root>");

		ddmStructureVersionModel.setName(sb.toString());

		ddmStructureVersionModel.setDefinition(
			ddmStructureModel.getDefinition());
		ddmStructureVersionModel.setStorageType(StorageType.JSON.toString());
		ddmStructureVersionModel.setStatusByUserId(
			ddmStructureModel.getUserId());
		ddmStructureVersionModel.setStatusByUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		ddmStructureVersionModel.setStatusDate(statusDate);

		return ddmStructureVersionModel;
	}

}