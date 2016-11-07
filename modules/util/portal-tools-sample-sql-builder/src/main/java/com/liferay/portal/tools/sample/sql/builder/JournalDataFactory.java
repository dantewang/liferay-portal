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

import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkModelImpl;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.model.impl.JournalArticleLocalizationModelImpl;
import com.liferay.journal.model.impl.JournalArticleModelImpl;
import com.liferay.journal.model.impl.JournalArticleResourceModelImpl;
import com.liferay.journal.model.impl.JournalContentSearchModelImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.util.SimpleCounter;

import java.util.Date;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class JournalDataFactory {

	public JournalDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public DDMStructureLayoutModel
		getDefaultJournalDDMStructureLayoutModel() {

		return _initContext.getDefaultJournalDDMStructureLayoutModel();
	}

	public DDMStructureModel getDefaultJournalDDMStructureModel() {
		return _initContext.getDefaultJournalDDMStructureModel();
	}

	public DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return _initContext.getDefaultJournalDDMStructureVersionModel();
	}

	public DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return _initContext.getDefaultJournalDDMTemplateModel();
	}

	public long getJournalArticleClassNameId() {
		return _initContext.getClassNameId(
			JournalArticle.class, _initContext.getClassNameModels());
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		int maxJournalArticleCount = _initContext.getMaxJournalArticleCount();

		StringBundler sb = new StringBundler(3 * maxJournalArticleCount);

		for (int i = 1; i <= maxJournalArticleCount; i++) {
			sb.append(portletPrefix);
			sb.append(i);
			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	public int getMaxJournalArticleCount() {
		return _initContext.getMaxJournalArticleCount();
	}

	public int getMaxJournalArticlePageCount() {
		return _initContext.getMaxJournalArticlePageCount();
	}

	public int getMaxJournalArticleVersionCount() {
		return _initContext.getMaxJournalArticleVersionCount();
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(counter.get());
		ddmStorageLinkModel.setClassNameId(
			_initContext.getClassNameId(
				JournalArticle.class, _initContext.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		SimpleCounter counter = _initContext.getCounter();
		
		ddmTemplateLinkModel.setCompanyId(_initContext.getCompanyId());
		ddmTemplateLinkModel.setTemplateLinkId(counter.get());
		ddmTemplateLinkModel.setClassNameId(
			_initContext.getClassNameId(
				JournalArticle.class, _initContext.getClassNameModels()));
		ddmTemplateLinkModel.setClassPK(journalArticleModel.getId());
		ddmTemplateLinkModel.setTemplateId(templateId);

		return ddmTemplateLinkModel;
	}

	public JournalArticleLocalizationModel
		newJournalArticleLocalizationModel(
			JournalArticleModel journalArticleModel, int articleIndex,
			int versionIndex) {

		JournalArticleLocalizationModel journalArticleLocalizationModel =
			new JournalArticleLocalizationModelImpl();

		StringBundler sb = new StringBundler(4);

		SimpleCounter counter = _initContext.getCounter();

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		journalArticleLocalizationModel.setArticleLocalizationId(counter.get());
		journalArticleLocalizationModel.setCompanyId(
			journalArticleModel.getCompanyId());
		journalArticleLocalizationModel.setArticlePK(
			journalArticleModel.getId());
		journalArticleLocalizationModel.setTitle(sb.toString());
		journalArticleLocalizationModel.setLanguageId(
			journalArticleModel.getDefaultLanguageId());

		return journalArticleLocalizationModel;
	}

	public JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex)
		throws PortalException {

		JournalArticleModel journalArticleModel = new JournalArticleModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		journalArticleModel.setUuid(SequentialUUID.generate());
		journalArticleModel.setId(counter.get());
		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());
		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());
		journalArticleModel.setCompanyId(_initContext.getCompanyId());
		journalArticleModel.setUserId(_initContext.getSampleUserId());
		journalArticleModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		journalArticleModel.setCreateDate(new Date());
		journalArticleModel.setModifiedDate(new Date());
		journalArticleModel.setClassNameId(
			JournalArticleConstants.CLASSNAME_ID_DEFAULT);
		journalArticleModel.setArticleId(
			journalArticleResourceModel.getArticleId());
		journalArticleModel.setVersion(versionIndex);

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		String urlTitle = sb.toString();

		journalArticleModel.setUrlTitle(urlTitle);

		journalArticleModel.setContent(_initContext.getJournalArticleContent());
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDDMStructureKey(
			_initContext.getDefaultJournalDDMStructureModel().
				getStructureKey());
		journalArticleModel.setDDMTemplateKey(
			_initContext.getDefaultJournalDDMTemplateModel().getTemplateKey());
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(
			_initContext.nextFutureDate(_initContext.getFutureDateCounter()));
		journalArticleModel.setReviewDate(new Date());
		journalArticleModel.setIndexable(true);
		journalArticleModel.setLastPublishDate(new Date());
		journalArticleModel.setStatusDate(new Date());

		return journalArticleModel;
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId, Map<Long, String> journalArticleResourceUUIDs) {

		JournalArticleResourceModel journalArticleResourceModel =
			new JournalArticleResourceModelImpl();

		SimpleCounter counter = _initContext.getCounter();
		
		journalArticleResourceModel.setUuid(SequentialUUID.generate());
		journalArticleResourceModel.setResourcePrimKey(counter.get());
		journalArticleResourceModel.setGroupId(groupId);
		journalArticleResourceModel.setArticleId(String.valueOf(counter.get()));

		journalArticleResourceUUIDs.put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		JournalContentSearchModel journalContentSearchModel =
			new JournalContentSearchModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		journalContentSearchModel.setContentSearchId(counter.get());
		journalContentSearchModel.setGroupId(journalArticleModel.getGroupId());
		journalContentSearchModel.setCompanyId(_initContext.getCompanyId());
		journalContentSearchModel.setLayoutId(layoutId);
		journalContentSearchModel.setPortletId(
			"com_liferay_journal_content_web_portlet_JournalContentPortlet");
		journalContentSearchModel.setArticleId(
			journalArticleModel.getArticleId());

		return journalContentSearchModel;
	}

	private final InitContext _initContext;

}