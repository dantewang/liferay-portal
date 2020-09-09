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

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersionModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionModelImpl;
import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.model.impl.JournalArticleLocalizationModelImpl;
import com.liferay.journal.model.impl.JournalArticleModelImpl;
import com.liferay.journal.model.impl.JournalArticleResourceModelImpl;
import com.liferay.journal.model.impl.JournalContentSearchModelImpl;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portlet.PortletPreferencesImpl;

import java.util.Date;

import javax.portlet.PortletPreferences;

/**
 * @author Lily Chi
 */
public class JournalDataFactory extends BaseDDMDataFactory {

	public static JournalDataFactory getInstance() {
		return _journalDataFactory;
	}

	public String getJournalArticleLayoutColumn(String portletPrefix) {
		StringBundler sb = new StringBundler(
			3 * BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_COUNT;
			 i++) {

			sb.append(portletPrefix);
			sb.append(i);
			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	public int getMaxJournalArticleCount() {
		return BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_COUNT;
	}

	public int getMaxJournalArticlePageCount() {
		return BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_PAGE_COUNT;
	}

	public int getMaxJournalArticleVersionCount() {
		return BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_VERSION_COUNT;
	}

	public DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId,
		long classNameId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		// UUID

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());

		// PK fields

		ddmStorageLinkModel.setStorageLinkId(counter.get());

		// Other fields

		ddmStorageLinkModel.setClassNameId(classNameId);
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);
		ddmStorageLinkModel.setStructureVersionId(
			_defaultJournalDDMStructureVersionId);

		return ddmStorageLinkModel;
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId,
		long classNameId) {

		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		// PK fields

		ddmTemplateLinkModel.setTemplateLinkId(counter.get());

		// Audit fields

		ddmTemplateLinkModel.setCompanyId(COMPANY_ID);

		// Other fields

		ddmTemplateLinkModel.setClassNameId(classNameId);
		ddmTemplateLinkModel.setClassPK(journalArticleModel.getId());
		ddmTemplateLinkModel.setTemplateId(templateId);

		return ddmTemplateLinkModel;
	}

	public DDMStructureLayoutModel newDefaultJournalDDMStructureLayoutModel() {
		return newDDMStructureLayoutModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID,
			_defaultJournalDDMStructureVersionId,
			_journalDDMStructureLayoutContent);
	}

	public DDMStructureModel newDefaultJournalDDMStructureModel(
		long classNameId) {

		return newDDMStructureModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, classNameId,
			_JOURNAL_STRUCTURE_KEY, _journalDDMStructureContent,
			DEFAULT_JOURNAL_DDM_STRUCTURE_ID);
	}

	public DDMStructureVersionModel newDefaultJournalDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		return newDDMStructureVersionModel(
			ddmStructureModel, _defaultJournalDDMStructureVersionId);
	}

	public DDMTemplateModel newDefaultJournalDDMTemplateModel(
		long journalArticleClassNameId, long ddmStructureClassNameId) {

		return _newDDMTemplateModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, DEFAULT_JOURNAL_DDM_STRUCTURE_ID,
			journalArticleClassNameId, _defaultJournalDDMTemplateId,
			ddmStructureClassNameId);
	}

	public DDMTemplateVersionModel newDefaultJournalDDMTemplateVersionModel(
		long classNameId) {

		DDMTemplateVersionModelImpl ddmTemplateVersionModelImpl =
			new DDMTemplateVersionModelImpl();

		// PK fields

		ddmTemplateVersionModelImpl.setTemplateVersionId(counter.get());

		// Group instance

		ddmTemplateVersionModelImpl.setGroupId(GLOBAL_GROUP_ID);

		// Audit fields

		ddmTemplateVersionModelImpl.setCompanyId(COMPANY_ID);
		ddmTemplateVersionModelImpl.setUserId(DEFAULT_USER_ID);
		ddmTemplateVersionModelImpl.setCreateDate(nextFutureDate());

		// Other fields

		ddmTemplateVersionModelImpl.setClassNameId(classNameId);
		ddmTemplateVersionModelImpl.setClassPK(
			DEFAULT_JOURNAL_DDM_STRUCTURE_ID);
		ddmTemplateVersionModelImpl.setTemplateId(_defaultJournalDDMTemplateId);
		ddmTemplateVersionModelImpl.setVersion(
			DDMTemplateConstants.VERSION_DEFAULT);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(_JOURNAL_STRUCTURE_KEY);
		sb.append("</name></root>");

		ddmTemplateVersionModelImpl.setName(sb.toString());

		ddmTemplateVersionModelImpl.setStatusByUserId(DEFAULT_USER_ID);
		ddmTemplateVersionModelImpl.setStatusDate(nextFutureDate());

		return ddmTemplateVersionModelImpl;
	}

	public JournalArticleLocalizationModel newJournalArticleLocalizationModel(
		JournalArticleModel journalArticleModel, int articleIndex,
		int versionIndex) {

		JournalArticleLocalizationModel journalArticleLocalizationModel =
			new JournalArticleLocalizationModelImpl();

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		// PK fields

		journalArticleLocalizationModel.setArticleLocalizationId(counter.get());

		// Audit fields

		journalArticleLocalizationModel.setCompanyId(
			journalArticleModel.getCompanyId());

		// Other fields

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

		// UUID

		journalArticleModel.setUuid(SequentialUUID.generate());

		// PK fields

		journalArticleModel.setId(counter.get());

		// Resource

		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());

		// Group instance

		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());

		// Audit fields

		journalArticleModel.setCompanyId(COMPANY_ID);
		journalArticleModel.setUserId(SAMPLE_USER_ID);
		journalArticleModel.setUserName(SAMPLE_USER_NAME);
		journalArticleModel.setCreateDate(new Date());
		journalArticleModel.setModifiedDate(new Date());

		// Other fields

		journalArticleModel.setClassNameId(
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT);
		journalArticleModel.setTreePath("/");
		journalArticleModel.setArticleId(
			journalArticleResourceModel.getArticleId());
		journalArticleModel.setVersion(versionIndex);

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		journalArticleModel.setUrlTitle(sb.toString());

		journalArticleModel.setContent(_journalArticleContent);
		journalArticleModel.setDDMStructureKey(_JOURNAL_STRUCTURE_KEY);
		journalArticleModel.setDDMTemplateKey(_JOURNAL_STRUCTURE_KEY);
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(nextFutureDate());
		journalArticleModel.setReviewDate(new Date());
		journalArticleModel.setIndexable(true);
		journalArticleModel.setLastPublishDate(new Date());
		journalArticleModel.setStatusDate(new Date());

		if (Validator.isNull(_defaultJournalArticleId)) {
			_defaultJournalArticleId = journalArticleModel.getArticleId();
		}

		return journalArticleModel;
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId) {

		JournalArticleResourceModel journalArticleResourceModel =
			new JournalArticleResourceModelImpl();

		// UUID

		journalArticleResourceModel.setUuid(SequentialUUID.generate());

		// PK fields

		journalArticleResourceModel.setResourcePrimKey(counter.get());

		// Group instance

		journalArticleResourceModel.setGroupId(groupId);

		// Audit fields

		journalArticleResourceModel.setCompanyId(COMPANY_ID);

		// Other fields

		journalArticleResourceModel.setArticleId(String.valueOf(counter.get()));

		journalArticleResourceUUIDs.put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public PortletPreferencesModel newJournalContentPortletPreferencesModel(
			FragmentEntryLinkModel fragmentEntryLinkModel,
			PortletPreferencesFactory portletPreferencesFactory)
		throws Exception {

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue("articleId", _defaultJournalArticleId);

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		// PK fields

		portletPreferencesModel.setPortletPreferencesId(counter.get());

		// Other fields

		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(fragmentEntryLinkModel.getClassPK());
		portletPreferencesModel.setPortletId(
			PortletIdCodec.encode(
				JournalContentPortletKeys.JOURNAL_CONTENT,
				fragmentEntryLinkModel.getNamespace()));
		portletPreferencesModel.setPreferences(
			portletPreferencesFactory.toXML(portletPreferences));

		return portletPreferencesModel;
	}

	public JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		JournalContentSearchModel journalContentSearchModel =
			new JournalContentSearchModelImpl();

		// PK fields

		journalContentSearchModel.setContentSearchId(counter.get());

		// Group instance

		journalContentSearchModel.setGroupId(journalArticleModel.getGroupId());

		// Audit fields

		journalContentSearchModel.setCompanyId(COMPANY_ID);

		// Other fields

		journalContentSearchModel.setLayoutId(layoutId);
		journalContentSearchModel.setPortletId(
			JournalContentPortletKeys.JOURNAL_CONTENT);
		journalContentSearchModel.setArticleId(
			journalArticleModel.getArticleId());

		return journalContentSearchModel;
	}

	public <K, V> ObjectValuePair<K, V> newObjectValuePair(K key, V value) {
		return new ObjectValuePair<>(key, value);
	}

	private JournalDataFactory() {
		_defaultJournalDDMStructureVersionId = counter.get();
		_defaultJournalDDMTemplateId = counter.get();

		_initJournalArticleContent();
		_initJournalDDMStructureContent();
	}

	private void _initJournalArticleContent() {
		int maxJournalArticleSize =
			BenchmarksPropsValues.MAX_JOURNAL_ARTICLE_SIZE;

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

		_journalArticleContent = sb.toString();
	}

	private void _initJournalDDMStructureContent() {
		_journalDDMStructureContent = readFile(
			"ddm_structure_basic_web_content.json");
		_journalDDMStructureLayoutContent = readFile(
			"ddm_structure_layout_basic_web_content.json");
	}

	private DDMTemplateModel _newDDMTemplateModel(
		long groupId, long userId, long structureId, long sourceClassNameId,
		long templateId, long classNameId) {

		DDMTemplateModel ddmTemplateModel = new DDMTemplateModelImpl();

		// UUID

		ddmTemplateModel.setUuid(SequentialUUID.generate());

		// PK fields

		ddmTemplateModel.setTemplateId(templateId);

		// Group instance

		ddmTemplateModel.setGroupId(groupId);

		// Audit fields

		ddmTemplateModel.setCompanyId(COMPANY_ID);
		ddmTemplateModel.setUserId(userId);
		ddmTemplateModel.setVersionUserId(userId);
		ddmTemplateModel.setVersionUserName(SAMPLE_USER_NAME);
		ddmTemplateModel.setCreateDate(nextFutureDate());
		ddmTemplateModel.setModifiedDate(nextFutureDate());

		// Other fields

		ddmTemplateModel.setClassNameId(classNameId);
		ddmTemplateModel.setClassPK(structureId);
		ddmTemplateModel.setResourceClassNameId(sourceClassNameId);
		ddmTemplateModel.setTemplateKey(_JOURNAL_STRUCTURE_KEY);
		ddmTemplateModel.setVersion(DDMTemplateConstants.VERSION_DEFAULT);

		StringBundler sb = new StringBundler(3);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append("Basic Web Content</name></root>");

		ddmTemplateModel.setName(sb.toString());

		ddmTemplateModel.setType(DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY);
		ddmTemplateModel.setMode(DDMTemplateConstants.TEMPLATE_MODE_CREATE);
		ddmTemplateModel.setLanguage(TemplateConstants.LANG_TYPE_FTL);
		ddmTemplateModel.setScript("${content.getData()}");
		ddmTemplateModel.setCacheable(true);
		ddmTemplateModel.setSmallImage(false);
		ddmTemplateModel.setLastPublishDate(nextFutureDate());

		return ddmTemplateModel;
	}

	private static final String _JOURNAL_STRUCTURE_KEY = "BASIC-WEB-CONTENT";

	private static JournalDataFactory _journalDataFactory =
		new JournalDataFactory();

	private String _defaultJournalArticleId;
	private final long _defaultJournalDDMStructureVersionId;
	private final long _defaultJournalDDMTemplateId;
	private String _journalArticleContent;
	private String _journalDDMStructureContent;
	private String _journalDDMStructureLayoutContent;

}