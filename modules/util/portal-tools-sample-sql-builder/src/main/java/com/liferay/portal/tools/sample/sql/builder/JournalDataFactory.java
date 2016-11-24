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
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.util.SimpleCounter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class JournalDataFactory extends BaseDataFactory {

	public JournalDataFactory(
			InitContext initContext, UserDataFactory userDataFactory)
		throws Exception {

		super(initContext);
		_userDataFactory = userDataFactory;
		_initJournalArticleContent();
		_initJournalDDMStructureContent();
		_initJournalTypeModel();
	}

	public DDMStructureLayoutModel
		getDefaultJournalDDMStructureLayoutModel() {

		return _defaultJournalDDMStructureLayoutModel;
	}

	public DDMStructureModel getDefaultJournalDDMStructureModel() {
		return _defaultJournalDDMStructureModel;
	}

	public DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return _defaultJournalDDMStructureVersionModel;
	}

	public DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return _defaultJournalDDMTemplateModel;
	}

	public long getJournalArticleClassNameId() {
		return getClassNameId(
			JournalArticle.class, initContext.getClassNameModels());
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		int maxJournalArticleCount = initContext.getMaxJournalArticleCount();

		StringBundler sb = new StringBundler(3 * maxJournalArticleCount);

		for (int i = 1; i <= maxJournalArticleCount; i++) {
			sb.append(portletPrefix);
			sb.append(i);
			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	public Map<Long, String> getJournalArticleResourceUUIDs() {
		return _journalArticleResourceUUIDs;
	}

	public String initJournalArticleContent(int maxJournalArticleSize) {
		StringBundler sb = new StringBundler(6);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><dynamic-element name=\"content");
		sb.append("\" type=\"text_area\" index-type=\"keyword\" index=\"0\">");
		sb.append("<dynamic-content language-id=\"en_US\"><![CDATA[");

		if (maxJournalArticleSize <= 0) {
			maxJournalArticleSize = 1;
		}

		char[] chars = new char[maxJournalArticleSize];

		for (int i = 0; i < maxJournalArticleSize; i++) {
			chars[i] = (char)(CharPool.LOWER_CASE_A + (i % 26));
		}

		sb.append(new String(chars));

		sb.append("]]></dynamic-content></dynamic-element></root>");

		return sb.toString();
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		SimpleCounter counter = initContext.getCounter();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(counter.get());
		ddmStorageLinkModel.setClassNameId(
			getClassNameId(
				JournalArticle.class, initContext.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		SimpleCounter counter = initContext.getCounter();

		ddmTemplateLinkModel.setCompanyId(initContext.getCompanyId());
		ddmTemplateLinkModel.setTemplateLinkId(counter.get());
		ddmTemplateLinkModel.setClassNameId(
			getClassNameId(
				JournalArticle.class, initContext.getClassNameModels()));
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

		SimpleCounter counter = initContext.getCounter();

		sb.append(DataFactoryConstants.JOURNAL_ARTICLE_TITLE_PREFIX);
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

		SimpleCounter counter = initContext.getCounter();

		journalArticleModel.setUuid(SequentialUUID.generate());
		journalArticleModel.setId(counter.get());
		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());
		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());
		journalArticleModel.setCompanyId(initContext.getCompanyId());
		journalArticleModel.setUserId(initContext.getSampleUserId());
		journalArticleModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		journalArticleModel.setCreateDate(new Date());
		journalArticleModel.setModifiedDate(new Date());
		journalArticleModel.setClassNameId(
			JournalArticleConstants.CLASSNAME_ID_DEFAULT);
		journalArticleModel.setArticleId(
			journalArticleResourceModel.getArticleId());
		journalArticleModel.setVersion(versionIndex);

		StringBundler sb = new StringBundler(4);

		sb.append(DataFactoryConstants.JOURNAL_ARTICLE_TITLE_PREFIX);
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		String urlTitle = sb.toString();

		journalArticleModel.setUrlTitle(urlTitle);

		journalArticleModel.setContent(_journalArticleContent);
		journalArticleModel.setDefaultLanguageId(
			DataFactoryConstants.LANGUAGE_ID);
		journalArticleModel.setDDMStructureKey(
			_defaultJournalDDMStructureModel.getStructureKey());
		journalArticleModel.setDDMTemplateKey(
			_defaultJournalDDMTemplateModel.getTemplateKey());
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(
			nextFutureDate(initContext.getFutureDateCounter()));
		journalArticleModel.setReviewDate(new Date());
		journalArticleModel.setIndexable(true);
		journalArticleModel.setLastPublishDate(new Date());
		journalArticleModel.setStatusDate(new Date());

		return journalArticleModel;
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId) {

		JournalArticleResourceModel journalArticleResourceModel =
			new JournalArticleResourceModelImpl();

		SimpleCounter counter = initContext.getCounter();

		journalArticleResourceModel.setUuid(SequentialUUID.generate());
		journalArticleResourceModel.setResourcePrimKey(counter.get());
		journalArticleResourceModel.setGroupId(groupId);
		journalArticleResourceModel.setArticleId(String.valueOf(counter.get()));

		_journalArticleResourceUUIDs.put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		JournalContentSearchModel journalContentSearchModel =
			new JournalContentSearchModelImpl();

		SimpleCounter counter = initContext.getCounter();

		journalContentSearchModel.setContentSearchId(counter.get());

		journalContentSearchModel.setGroupId(journalArticleModel.getGroupId());
		journalContentSearchModel.setCompanyId(initContext.getCompanyId());
		journalContentSearchModel.setLayoutId(layoutId);
		journalContentSearchModel.setPortletId(
			DataFactoryConstants.JOURNAL_CONTENT_PORTLET_ID);
		journalContentSearchModel.setArticleId(
			journalArticleModel.getArticleId());

		return journalContentSearchModel;
	}

	private void _initJournalArticleContent() {
		int maxJournalArticleSize = initContext.getMaxJournalArticleSize();

		_journalArticleContent = initJournalArticleContent(
			maxJournalArticleSize);
	}

	private void _initJournalDDMStructureContent() throws Exception {
		_journalDDMStructureContent = getResource(
			DataFactoryConstants.JOURNAL_DDM_STRUCTURE_CONTENT);
		_journalDDMStructureLayoutContent = getResource(
			DataFactoryConstants.JOURNAL_DDM_STRUCTURE_LAYOUT_CONTENT);
	}

	private void _initJournalTypeModel() {
		long groupId = _userDataFactory.getGlobalGroupId();
		long userId = initContext.getDefaultUserId();

		Map<String, ClassNameModel> classNameModels =
			initContext.getClassNameModels();

		_defaultJournalDDMStructureModel = newDDMStructureModel(
			groupId, userId, getClassNameId(
				JournalArticle.class, classNameModels),
			DataFactoryConstants.JOURNAL_STRUCTURE_KEY,
			_journalDDMStructureContent);

		_defaultJournalDDMStructureVersionModel = newDDMStructureVersionModel(
			_defaultJournalDDMStructureModel);

		_defaultJournalDDMStructureLayoutModel = newDDMStructureLayoutModel(
			groupId, userId,
			_defaultJournalDDMStructureVersionModel.getStructureVersionId(),
			_journalDDMStructureLayoutContent);

		_defaultJournalDDMTemplateModel = newDDMTemplateModel(
			groupId, userId, _defaultJournalDDMStructureModel.getStructureId(),
			getClassNameId(JournalArticle.class, classNameModels));
	}

	private DDMStructureLayoutModel _defaultJournalDDMStructureLayoutModel;
	private DDMStructureModel _defaultJournalDDMStructureModel;
	private DDMStructureVersionModel _defaultJournalDDMStructureVersionModel;
	private DDMTemplateModel _defaultJournalDDMTemplateModel;
	private String _journalArticleContent;
	private final Map<Long, String> _journalArticleResourceUUIDs =
		new HashMap<>();
	private String _journalDDMStructureContent;
	private String _journalDDMStructureLayoutContent;
	private final UserDataFactory _userDataFactory;

}