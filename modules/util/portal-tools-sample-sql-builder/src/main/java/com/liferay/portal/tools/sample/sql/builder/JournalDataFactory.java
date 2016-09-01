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
import com.liferay.journal.constants.JournalPortletKeys;
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
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.PortletPreferencesImpl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

/**
 * @author Lily Chi
 */
public class JournalDataFactory {

	public static DDMStructureLayoutModel
		getDefaultJournalDDMStructureLayoutModel() {

		return InitContextUtil.
			getDefaultJournalDDMStructureLayoutModel();
	}

	public static DDMStructureModel getDefaultJournalDDMStructureModel() {
		return InitContextUtil.getDefaultJournalDDMStructureModel();
	}

	public static DDMStructureVersionModel
		getDefaultJournalDDMStructureVersionModel() {

		return InitContextUtil.
			getDefaultJournalDDMStructureVersionModel();
	}

	public static DDMTemplateModel getDefaultJournalDDMTemplateModel() {
		return InitContextUtil.getDefaultJournalDDMTemplateModel();
	}

	public static long getJournalArticleClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			JournalArticle.class, InitContextUtil.getClassNameModels());
	}

	public static String getJournalArticleLayoutColumn(String portletPrefix) {
		int maxJournalArticleCount =
			InitContextUtil.getMaxJournalArticleCount();

		StringBundler sb = new StringBundler(3 * maxJournalArticleCount);

		for (int i = 1; i <= maxJournalArticleCount; i++) {
			sb.append(portletPrefix);
			sb.append(i);
			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	public static int getMaxJournalArticleCount() {
		return InitContextUtil.getMaxJournalArticleCount();
	}

	public static int getMaxJournalArticlePageCount() {
		return InitContextUtil.getMaxJournalArticlePageCount();
	}

	public static int getMaxJournalArticleVersionCount() {
		return InitContextUtil.getMaxJournalArticleVersionCount();
	}

	public static DDMStorageLinkModel newDDMStorageLinkModel(
		JournalArticleModel journalArticleModel, long structureId) {

		DDMStorageLinkModel ddmStorageLinkModel = new DDMStorageLinkModelImpl();

		ddmStorageLinkModel.setUuid(SequentialUUID.generate());
		ddmStorageLinkModel.setStorageLinkId(
			InitContextUtil.getCounter().get());
		ddmStorageLinkModel.setClassNameId(
			InitDataFactoryUtil.getClassNameId(
				JournalArticle.class,
				InitContextUtil.getClassNameModels()));
		ddmStorageLinkModel.setClassPK(journalArticleModel.getId());
		ddmStorageLinkModel.setStructureId(structureId);

		return ddmStorageLinkModel;
	}

	public static DDMTemplateLinkModel newDDMTemplateLinkModel(
		JournalArticleModel journalArticleModel, long templateId) {

		DDMTemplateLinkModel ddmTemplateLinkModel =
			new DDMTemplateLinkModelImpl();

		ddmTemplateLinkModel.setCompanyId(
			InitContextUtil.getCompanyId());
		ddmTemplateLinkModel.setTemplateLinkId(
			InitContextUtil.getCounter().get());
		ddmTemplateLinkModel.setClassNameId(
			InitDataFactoryUtil.getClassNameId(
				JournalArticle.class,
				InitContextUtil.getClassNameModels()));
		ddmTemplateLinkModel.setClassPK(journalArticleModel.getId());
		ddmTemplateLinkModel.setTemplateId(templateId);

		return ddmTemplateLinkModel;
	}

	public static JournalArticleLocalizationModel
		newJournalArticleLocalizationModel(
			JournalArticleModel journalArticleModel, int articleIndex,
			int versionIndex) {

		JournalArticleLocalizationModel journalArticleLocalizationModel =
			new JournalArticleLocalizationModelImpl();

		StringBundler sb = new StringBundler(4);

		sb.append("TestJournalArticle_");
		sb.append(articleIndex);
		sb.append(StringPool.UNDERLINE);
		sb.append(versionIndex);

		journalArticleLocalizationModel.setArticleLocalizationId(
			InitContextUtil.getCounter().get());
		journalArticleLocalizationModel.setCompanyId(
			journalArticleModel.getCompanyId());
		journalArticleLocalizationModel.setArticlePK(
			journalArticleModel.getId());
		journalArticleLocalizationModel.setTitle(sb.toString());
		journalArticleLocalizationModel.setLanguageId(
			journalArticleModel.getDefaultLanguageId());

		return journalArticleLocalizationModel;
	}

	public static JournalArticleModel newJournalArticleModel(
			JournalArticleResourceModel journalArticleResourceModel,
			int articleIndex, int versionIndex)
		throws PortalException {

		JournalArticleModel journalArticleModel = new JournalArticleModelImpl();

		journalArticleModel.setUuid(SequentialUUID.generate());
		journalArticleModel.setId(InitContextUtil.getCounter().get());
		journalArticleModel.setResourcePrimKey(
			journalArticleResourceModel.getResourcePrimKey());
		journalArticleModel.setGroupId(
			journalArticleResourceModel.getGroupId());
		journalArticleModel.setCompanyId(InitContextUtil.getCompanyId());
		journalArticleModel.setUserId(InitContextUtil.getSampleUserId());
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

		journalArticleModel.setContent(
			InitContextUtil.getJournalArticleContent());
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDDMStructureKey(
			InitContextUtil.getDefaultJournalDDMStructureModel().
				getStructureKey());
		journalArticleModel.setDDMTemplateKey(
			InitContextUtil.getDefaultJournalDDMTemplateModel().
				getTemplateKey());
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
		journalArticleModel.setReviewDate(new Date());
		journalArticleModel.setIndexable(true);
		journalArticleModel.setLastPublishDate(new Date());
		journalArticleModel.setStatusDate(new Date());

		return journalArticleModel;
	}

	public static JournalArticleResourceModel newJournalArticleResourceModel(
		long groupId, Map<Long, String> journalArticleResourceUUIDs) {

		JournalArticleResourceModel journalArticleResourceModel =
			new JournalArticleResourceModelImpl();

		journalArticleResourceModel.setUuid(SequentialUUID.generate());
		journalArticleResourceModel.setResourcePrimKey(
			InitContextUtil.getCounter().get());
		journalArticleResourceModel.setGroupId(groupId);
		journalArticleResourceModel.setArticleId(
			String.valueOf(InitContextUtil.getCounter().get()));

		journalArticleResourceUUIDs.put(
			journalArticleResourceModel.getPrimaryKey(),
			journalArticleResourceModel.getUuid());

		return journalArticleResourceModel;
	}

	public static JournalContentSearchModel newJournalContentSearchModel(
		JournalArticleModel journalArticleModel, long layoutId) {

		JournalContentSearchModel journalContentSearchModel =
			new JournalContentSearchModelImpl();

		journalContentSearchModel.setContentSearchId(
			InitContextUtil.getCounter().get());
		journalContentSearchModel.setGroupId(journalArticleModel.getGroupId());
		journalContentSearchModel.setCompanyId(
			InitContextUtil.getCompanyId());
		journalContentSearchModel.setLayoutId(layoutId);
		journalContentSearchModel.setPortletId(
			"com_liferay_journal_content_web_portlet_JournalContentPortlet");
		journalContentSearchModel.setArticleId(
			journalArticleModel.getArticleId());

		return journalContentSearchModel;
	}

	public static List<PortletPreferencesModel>
		newJournalPortletPreferencesModels(long plid) {

		return Collections.singletonList(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public static PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel,
			PortletPreferencesFactory portletPreferencesFactory)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return InitDataFactoryUtil.newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

}