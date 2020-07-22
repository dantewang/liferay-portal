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

import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
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
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalContentSearchModel;
import com.liferay.journal.model.JournalFolderModel;
import com.liferay.journal.model.impl.JournalArticleLocalizationModelImpl;
import com.liferay.journal.model.impl.JournalArticleModelImpl;
import com.liferay.journal.model.impl.JournalArticleResourceModelImpl;
import com.liferay.journal.model.impl.JournalContentSearchModelImpl;
import com.liferay.journal.model.impl.JournalFolderModelImpl;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portlet.PortletPreferencesImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletPreferences;

/**
 * @author Lily Chi
 */
public class JournalDataFactory extends BaseDDMDataFactory {

	public JournalDataFactory() throws Exception {
		_defaultJournalDDMStructureVersionId = counter.get();
		_defaultJournalDDMTemplateId = counter.get();

		_initJournalArticleContent();
		_initJournalDDMStructureContent();
	}

	public long getJournalArticleClassNameId() {
		return getClassNameId(JournalArticle.class);
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
		JournalArticleModel journalArticleModel, long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		if (journalArticleModel.getCtCollectionId() != 0) {
			ddmStorageLinkModel.setCtCollectionId(
				journalArticleModel.getCtCollectionId());
		}

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(counter.get());
		ddmStorageLinkModel.setClassNameId(
			getClassNameId(JournalArticle.class));
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);
		ddmStorageLinkModel.setStructureVersionId(
			_defaultJournalDDMStructureVersionId);

		return ddmStorageLinkModel;
	}

	public List<DDMStorageLinkModel> newDDMStorageLinkModels(
		List<JournalArticleModel> journalArticleModels, long templateId) {

		List<DDMStorageLinkModel> dDMStorageLinkModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
				BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		journalArticleModels.forEach(
			journalArticleModel -> dDMStorageLinkModels.add(
				newDDMStorageLinkModel(journalArticleModel, templateId)));

		return dDMStorageLinkModels;
	}

	public DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		if (journalArticleModel.getCtCollectionId() != 0) {
			ddmTemplateLinkModel.setCtCollectionId(
				journalArticleModel.getCtCollectionId());
		}

		ddmTemplateLinkModel.setCompanyId(COMPANY_ID);
		ddmTemplateLinkModel.setTemplateLinkId(counter.get());
		ddmTemplateLinkModel.setClassNameId(
			getClassNameId(JournalArticle.class));
		ddmTemplateLinkModel.setClassPK(journalArticleModel.getId());
		ddmTemplateLinkModel.setTemplateId(templateId);

		return ddmTemplateLinkModel;
	}

	public List<DDMTemplateLinkModel> newDDMTemplateLinkModels(
		List<JournalArticleModel> journalArticleModels, long templateId) {

		List<DDMTemplateLinkModel> dDMTemplateLinkModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
				BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		journalArticleModels.forEach(
			journalArticleModel -> dDMTemplateLinkModels.add(
				newDDMTemplateLinkModel(journalArticleModel, templateId)));

		return dDMTemplateLinkModels;
	}

	public DDMStructureLayoutModel newDefaultJournalDDMStructureLayoutModel() {
		return newDDMStructureLayoutModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID,
			_defaultJournalDDMStructureVersionId,
			_journalDDMStructureLayoutContent);
	}

	public DDMStructureModel newDefaultJournalDDMStructureModel() {
		return newDDMStructureModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID,
			getClassNameId(JournalArticle.class), _JOURNAL_STRUCTURE_KEY,
			_journalDDMStructureContent, DEFAULT_JOURNAL_DDM_STRUCTURE_ID);
	}

	public DDMStructureVersionModel newDefaultJournalDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		return newDDMStructureVersionModel(
			ddmStructureModel, _defaultJournalDDMStructureVersionId);
	}

	public DDMTemplateModel newDefaultJournalDDMTemplateModel() {
		return newDDMTemplateModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, DEFAULT_JOURNAL_DDM_STRUCTURE_ID,
			getClassNameId(JournalArticle.class), _defaultJournalDDMTemplateId);
	}

	public DDMTemplateVersionModel newDefaultJournalDDMTemplateVersionModel() {
		DDMTemplateVersionModelImpl ddmTemplateVersionModelImpl =
			new DDMTemplateVersionModelImpl();

		ddmTemplateVersionModelImpl.setTemplateVersionId(counter.get());
		ddmTemplateVersionModelImpl.setGroupId(GLOBAL_GROUP_ID);
		ddmTemplateVersionModelImpl.setCompanyId(COMPANY_ID);
		ddmTemplateVersionModelImpl.setUserId(DEFAULT_USER_ID);
		ddmTemplateVersionModelImpl.setCreateDate(nextFutureDate());
		ddmTemplateVersionModelImpl.setTemplateId(_defaultJournalDDMTemplateId);
		ddmTemplateVersionModelImpl.setClassPK(
			DEFAULT_JOURNAL_DDM_STRUCTURE_ID);
		ddmTemplateVersionModelImpl.setClassNameId(
			getClassNameId(DDMStructure.class));
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

		return newJournalArticleLocalizationModel(
			journalArticleModel, articleIndex, versionIndex, 0);
	}

	public JournalArticleLocalizationModel newJournalArticleLocalizationModel(
		JournalArticleModel journalArticleModel, int articleIndex,
		int versionIndex, long ctCollectionId) {

		JournalArticleLocalizationModel journalArticleLocalizationModel =
			new JournalArticleLocalizationModelImpl();

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		journalArticleLocalizationModel.setArticleLocalizationId(counter.get());
		journalArticleLocalizationModel.setCompanyId(
			journalArticleModel.getCompanyId());
		journalArticleLocalizationModel.setCtCollectionId(ctCollectionId);
		journalArticleLocalizationModel.setArticlePK(
			journalArticleModel.getId());
		journalArticleLocalizationModel.setTitle(sb.toString());
		journalArticleLocalizationModel.setLanguageId(
			journalArticleModel.getDefaultLanguageId());

		return journalArticleLocalizationModel;
	}

	public List<JournalArticleLocalizationModel>
		newJournalArticleLocalizationModels(
			List<JournalArticleModel> journalArticleModels,
			List<JournalFolderModel> journalFolderModels) {

		List<JournalArticleLocalizationModel> journalArticleLocalizationModels =
			new ArrayList<>(
				BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
					BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		int i = 0;

		for (JournalFolderModel journalFolderModel : journalFolderModels) {
			int j = 0;

			while (true) {
				journalArticleLocalizationModels.add(
					newJournalArticleLocalizationModel(
						journalArticleModels.get(i), j + 1, CT_ARTICLE_VERSION,
						journalFolderModel.getCtCollectionId()));
				i++;
				j++;

				if ((i % BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT) ==
						0) {

					break;
				}
			}
		}

		return journalArticleLocalizationModels;
	}

	public JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex)
		throws PortalException {

		JournalArticleModel journalArticleModel = newJournalArticleModel(
			journalArticleResourceModel, articleIndex, versionIndex, 0,
			SAMPLE_USER_ID, SAMPLE_USER_NAME, 0, "/");

		if (Validator.isNull(_defaultJournalArticleId)) {
			_defaultJournalArticleId = journalArticleModel.getArticleId();
		}

		return journalArticleModel;
	}

	public List<JournalArticleModel> newJournalArticleModels(
			List<JournalArticleResourceModel> journalArticleResourceModels,
			List<JournalFolderModel> journalFolderModels)
		throws PortalException {

		List<JournalArticleModel> journalArticleModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
				BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		int i = 0;

		for (JournalFolderModel journalFolderModel : journalFolderModels) {
			int j = 0;

			while (true) {
				journalArticleModels.add(
					newJournalArticleModel(
						journalArticleResourceModels.get(i), j + 1,
						CT_ARTICLE_VERSION,
						journalFolderModel.getCtCollectionId(),
						journalFolderModel.getUserId(),
						journalFolderModel.getUserName(),
						journalFolderModel.getFolderId(),
						"/" + journalFolderModel.getFolderId() + "/"));
				i++;
				j++;

				if ((i % BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT) ==
						0) {

					break;
				}
			}
		}

		return journalArticleModels;
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId) {

		return newJournalArticleResourceModel(groupId, 0);
	}

	public JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId, long ctCollectionId) {

		JournalArticleResourceModel journalArticleResourceModel =
			new JournalArticleResourceModelImpl();

		journalArticleResourceModel.setUuid(SequentialUUID.generate());
		journalArticleResourceModel.setResourcePrimKey(counter.get());
		journalArticleResourceModel.setGroupId(groupId);
		journalArticleResourceModel.setCompanyId(COMPANY_ID);
		journalArticleResourceModel.setCtCollectionId(ctCollectionId);
		journalArticleResourceModel.setArticleId(String.valueOf(counter.get()));

		journalArticleResourceUUIDs.put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public List<JournalArticleResourceModel> newJournalArticleResourceModels(
		long groupId, List<JournalFolderModel> journalFolderModels) {

		List<JournalArticleResourceModel> journalArticleResourceModels =
			new ArrayList<>(
				BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT *
					BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		journalFolderModels.forEach(
			journalFolderModel -> {
				for (int i = 0;
					 i < BenchmarksPropsValues.MAX_CT_JOURNAL_ARTICLE_COUNT;
					 i++) {

					journalArticleResourceModels.add(
						newJournalArticleResourceModel(
							groupId, journalFolderModel.getCtCollectionId()));
				}
			});

		return journalArticleResourceModels;
	}

	public PortletPreferencesModel newJournalContentPortletPreferencesModel(
			FragmentEntryLinkModel fragmentEntryLinkModel)
		throws Exception {

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue("articleId", _defaultJournalArticleId);

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		portletPreferencesModel.setPortletPreferencesId(counter.get());
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

		journalContentSearchModel.setContentSearchId(counter.get());
		journalContentSearchModel.setGroupId(journalArticleModel.getGroupId());
		journalContentSearchModel.setCompanyId(COMPANY_ID);
		journalContentSearchModel.setLayoutId(layoutId);
		journalContentSearchModel.setPortletId(
			JournalContentPortletKeys.JOURNAL_CONTENT);
		journalContentSearchModel.setArticleId(
			journalArticleModel.getArticleId());

		return journalContentSearchModel;
	}

	public List<JournalFolderModel> newJournalFolderModels(
		CTCollectionModel cTCollectionModel, long groupId) {

		List<JournalFolderModel> journalFolderModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT;
			 i++) {

			journalFolderModels.add(
				newJournalFolderModel(
					cTCollectionModel, groupId, "Journal Folder " + (i + 1)));
		}

		return journalFolderModels;
	}

	public <K, V> ObjectValuePair<K, V> newObjectValuePair(K key, V value) {
		return new ObjectValuePair<>(key, value);
	}

	protected DDMTemplateModel newDDMTemplateModel(
		long groupId, long userId, long structureId, long sourceClassNameId,
		long templateId) {

		DDMTemplateModel ddmTemplateModel = new DDMTemplateModelImpl();

		ddmTemplateModel.setUuid(SequentialUUID.generate());
		ddmTemplateModel.setTemplateId(templateId);
		ddmTemplateModel.setGroupId(groupId);
		ddmTemplateModel.setCompanyId(COMPANY_ID);
		ddmTemplateModel.setUserId(userId);
		ddmTemplateModel.setCreateDate(nextFutureDate());
		ddmTemplateModel.setModifiedDate(nextFutureDate());
		ddmTemplateModel.setClassNameId(getClassNameId(DDMStructure.class));
		ddmTemplateModel.setClassPK(structureId);
		ddmTemplateModel.setResourceClassNameId(sourceClassNameId);
		ddmTemplateModel.setTemplateKey(_JOURNAL_STRUCTURE_KEY);
		ddmTemplateModel.setVersion(DDMTemplateConstants.VERSION_DEFAULT);
		ddmTemplateModel.setVersionUserId(userId);
		ddmTemplateModel.setVersionUserName(SAMPLE_USER_NAME);

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

	protected JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex, long ctCollectionId,
			long userId, String userName, long folderId, String treePath)
		throws PortalException {

		JournalArticleModel journalArticleModel = new JournalArticleModelImpl();

		journalArticleModel.setUuid(SequentialUUID.generate());
		journalArticleModel.setId(counter.get());
		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());
		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());
		journalArticleModel.setCompanyId(COMPANY_ID);
		journalArticleModel.setCtCollectionId(ctCollectionId);
		journalArticleModel.setUserId(userId);
		journalArticleModel.setUserName(userName);
		journalArticleModel.setCreateDate(new Date());
		journalArticleModel.setModifiedDate(new Date());
		journalArticleModel.setClassNameId(
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT);
		journalArticleModel.setArticleId(
			journalArticleResourceModel.getArticleId());
		journalArticleModel.setFolderId(folderId);
		journalArticleModel.setTreePath(treePath);
		journalArticleModel.setVersion(versionIndex);

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		journalArticleModel.setUrlTitle(sb.toString());

		journalArticleModel.setContent(_journalArticleContent);
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDDMStructureKey(_JOURNAL_STRUCTURE_KEY);
		journalArticleModel.setDDMTemplateKey(_JOURNAL_STRUCTURE_KEY);
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

	protected JournalFolderModel newJournalFolderModel(
		CTCollectionModel cTCollectionModel, long groupId, String name) {

		JournalFolderModel journalFolderModel = new JournalFolderModelImpl();

		long folderId = counter.get();

		journalFolderModel.setUuid(SequentialUUID.generate());
		journalFolderModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());
		journalFolderModel.setFolderId(folderId);
		journalFolderModel.setGroupId(groupId);
		journalFolderModel.setCompanyId(cTCollectionModel.getCompanyId());
		journalFolderModel.setUserId(cTCollectionModel.getUserId());
		journalFolderModel.setCreateDate(new Date());
		journalFolderModel.setModifiedDate(new Date());
		journalFolderModel.setParentFolderId(0);
		journalFolderModel.setTreePath("/" + folderId + "/");
		journalFolderModel.setName(name);

		return journalFolderModel;
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

	private void _initJournalDDMStructureContent() throws Exception {
		_journalDDMStructureContent = readFile(
			"ddm_structure_basic_web_content.json");
		_journalDDMStructureLayoutContent = readFile(
			"ddm_structure_layout_basic_web_content.json");
	}

	private static final String _JOURNAL_STRUCTURE_KEY = "BASIC-WEB-CONTENT";

	private String _defaultJournalArticleId;
	private final long _defaultJournalDDMStructureVersionId;
	private final long _defaultJournalDDMTemplateId;
	private String _journalArticleContent;
	private String _journalDDMStructureContent;
	private String _journalDDMStructureLayoutContent;

}