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

import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.change.tracking.model.CTAutoResolutionInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTMessage;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersionModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.journal.constants.JournalContentPortletKeys;
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
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedWriter;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public static void closeCSVWriters() throws IOException {
		for (Writer writer : _csvWriters.values()) {
			writer.close();
		}
	}

	public static long getClassNameId(Class<?> clazz) {
		ClassNameModel classNameModel = _classNameModels.get(clazz.getName());

		return classNameModel.getClassNameId();
	}

	public BaseDataFactory() throws Exception {
		_dlDDMStructureContent = readFile("ddm_structure_basic_document.json");
		_dlDDMStructureLayoutContent = readFile(
			"ddm_structure_layout_basic_document.json");
		_journalDDMStructureContent = readFile(
			"ddm_structure_basic_web_content.json");
		_journalDDMStructureLayoutContent = readFile(
			"ddm_structure_layout_basic_web_content.json");

		defaultAssetPublisherPortletPreferencesImpl =
			(PortletPreferencesImpl)portletPreferencesFactory.fromDefaultXML(
				readFile("default_asset_publisher_preference.xml"));
		_initDLFileEntryTypeModel();
		_initJournalArticleContent();
	}

	public long getClassNameId(String className) {
		ClassNameModel classNameModel = _classNameModels.get(className);

		return classNameModel.getClassNameId();
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return _classNameModels.values();
	}

	public Writer getCSVWriter(String csvFileName) {
		Writer writer = _csvWriters.get(csvFileName);

		if (writer == null) {
			throw new IllegalArgumentException(
				"Unknown CSV file name: " + csvFileName);
		}

		return writer;
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
			defaultJournalDDMStructureVersionModel.getStructureVersionId());

		return ddmStorageLinkModel;
	}

	public DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel) {

		DDMStructureVersionModel ddmStructureVersionModel =
			new DDMStructureVersionModelImpl();

		ddmStructureVersionModel.setStructureVersionId(counter.get());
		ddmStructureVersionModel.setGroupId(ddmStructureModel.getGroupId());
		ddmStructureVersionModel.setCompanyId(COMPANY_ID);
		ddmStructureVersionModel.setUserId(ddmStructureModel.getUserId());
		ddmStructureVersionModel.setUserName(SAMPLE_USER_NAME);
		ddmStructureVersionModel.setCreateDate(nextFutureDate());
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
		ddmStructureVersionModel.setStatusByUserName(SAMPLE_USER_NAME);
		ddmStructureVersionModel.setStatusDate(nextFutureDate());

		return ddmStructureVersionModel;
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

	public DDMTemplateVersionModel newDDMTemplateVersionModel(
		DDMTemplateModel ddmTemplateModel) {

		DDMTemplateVersionModelImpl ddmTemplateVersionModelImpl =
			new DDMTemplateVersionModelImpl();

		ddmTemplateVersionModelImpl.setTemplateVersionId(counter.get());
		ddmTemplateVersionModelImpl.setGroupId(ddmTemplateModel.getGroupId());
		ddmTemplateVersionModelImpl.setCompanyId(COMPANY_ID);
		ddmTemplateVersionModelImpl.setUserId(ddmTemplateModel.getUserId());
		ddmTemplateVersionModelImpl.setCreateDate(nextFutureDate());
		ddmTemplateVersionModelImpl.setTemplateId(
			ddmTemplateModel.getTemplateId());
		ddmTemplateVersionModelImpl.setClassPK(ddmTemplateModel.getClassPK());
		ddmTemplateVersionModelImpl.setClassNameId(
			ddmTemplateModel.getClassNameId());
		ddmTemplateVersionModelImpl.setVersion(
			DDMTemplateConstants.VERSION_DEFAULT);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><name language-id=\"en_US\">");
		sb.append(ddmTemplateModel.getTemplateKey());
		sb.append("</name></root>");

		ddmTemplateVersionModelImpl.setName(sb.toString());

		ddmTemplateVersionModelImpl.setStatusByUserId(
			ddmTemplateModel.getUserId());
		ddmTemplateVersionModelImpl.setStatusDate(nextFutureDate());

		return ddmTemplateVersionModelImpl;
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

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		LayoutFriendlyURLModel layoutFriendlyURLEntryModel =
			new LayoutFriendlyURLModelImpl();

		if (layoutModel.getCtCollectionId() != 0) {
			layoutFriendlyURLEntryModel.setCtCollectionId(
				layoutModel.getCtCollectionId());
		}

		layoutFriendlyURLEntryModel.setUuid(SequentialUUID.generate());
		layoutFriendlyURLEntryModel.setLayoutFriendlyURLId(counter.get());
		layoutFriendlyURLEntryModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLEntryModel.setCompanyId(COMPANY_ID);
		layoutFriendlyURLEntryModel.setUserId(SAMPLE_USER_ID);
		layoutFriendlyURLEntryModel.setUserName(SAMPLE_USER_NAME);
		layoutFriendlyURLEntryModel.setCreateDate(new Date());
		layoutFriendlyURLEntryModel.setModifiedDate(new Date());
		layoutFriendlyURLEntryModel.setPlid(layoutModel.getPlid());
		layoutFriendlyURLEntryModel.setFriendlyURL(
			layoutModel.getFriendlyURL());
		layoutFriendlyURLEntryModel.setLanguageId("en_US");
		layoutFriendlyURLEntryModel.setLastPublishDate(new Date());

		return layoutFriendlyURLEntryModel;
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2, long userId,
		String userName, long ctCollectionId) {

		SimpleCounter simpleCounter = layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());
		layoutModel.setPlid(counter.get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(COMPANY_ID);
		layoutModel.setCtCollectionId(ctCollectionId);
		layoutModel.setUserId(userId);
		layoutModel.setUserName(userName);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());
		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_PORTLET);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsUnicodeProperties = new UnicodeProperties(
			true);

		typeSettingsUnicodeProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "2_columns_ii");
		typeSettingsUnicodeProperties.setProperty("column-1", column1);
		typeSettingsUnicodeProperties.setProperty("column-2", column2);

		layoutModel.setTypeSettings(
			StringUtil.replace(
				typeSettingsUnicodeProperties.toString(), '\n', "\\n"));

		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	public Date nextFutureDate() {
		return new Date(_FUTURE_TIME + (_FUTURE_COUNTER.get() * Time.SECOND));
	}

	protected static String getMBDiscussionCombinedClassName(Class<?> clazz) {
		return StringBundler.concat(
			MBDiscussion.class.getName(), StringPool.UNDERLINE,
			clazz.getName());
	}

	protected String getClassName(long classNameId) {
		for (ClassNameModel classNameModel : _classNameModels.values()) {
			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	protected InputStream getResourceInputStream(String resourceName) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title, long userId,
		long ctCollectionId) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		assetEntryModel.setEntryId(counter.get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(COMPANY_ID);
		assetEntryModel.setCtCollectionId(ctCollectionId);
		assetEntryModel.setUserId(userId);
		assetEntryModel.setCreateDate(createDate);
		assetEntryModel.setModifiedDate(modifiedDate);
		assetEntryModel.setClassNameId(classNameId);
		assetEntryModel.setClassPK(classPK);
		assetEntryModel.setClassUuid(uuid);
		assetEntryModel.setClassTypeId(classTypeId);
		assetEntryModel.setListable(listable);
		assetEntryModel.setVisible(visible);
		assetEntryModel.setStartDate(createDate);
		assetEntryModel.setEndDate(nextFutureDate());
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(nextFutureDate());
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	protected DDMStructureLayoutModel newDDMStructureLayoutModel(
		long groupId, long userId, long structureVersionId, String definition) {

		DDMStructureLayoutModel ddmStructureLayoutModel =
			new DDMStructureLayoutModelImpl();

		ddmStructureLayoutModel.setUuid(SequentialUUID.generate());
		ddmStructureLayoutModel.setStructureLayoutId(counter.get());
		ddmStructureLayoutModel.setGroupId(groupId);
		ddmStructureLayoutModel.setCompanyId(COMPANY_ID);
		ddmStructureLayoutModel.setUserId(userId);
		ddmStructureLayoutModel.setUserName(SAMPLE_USER_NAME);
		ddmStructureLayoutModel.setCreateDate(nextFutureDate());
		ddmStructureLayoutModel.setModifiedDate(nextFutureDate());
		ddmStructureLayoutModel.setStructureLayoutKey(
			String.valueOf(counter.get()));
		ddmStructureLayoutModel.setStructureVersionId(structureVersionId);
		ddmStructureLayoutModel.setDefinition(definition);

		return ddmStructureLayoutModel;
	}

	protected DDMStructureModel newDDMStructureModel(
		long groupId, long userId, long classNameId, String structureKey,
		String definition) {

		DDMStructureModel ddmStructureModel = new DDMStructureModelImpl();

		ddmStructureModel.setUuid(SequentialUUID.generate());
		ddmStructureModel.setStructureId(counter.get());
		ddmStructureModel.setGroupId(groupId);
		ddmStructureModel.setCompanyId(COMPANY_ID);
		ddmStructureModel.setUserId(userId);
		ddmStructureModel.setUserName(SAMPLE_USER_NAME);
		ddmStructureModel.setVersionUserId(userId);
		ddmStructureModel.setVersionUserName(SAMPLE_USER_NAME);
		ddmStructureModel.setCreateDate(nextFutureDate());
		ddmStructureModel.setModifiedDate(nextFutureDate());
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
		ddmStructureModel.setLastPublishDate(nextFutureDate());

		return ddmStructureModel;
	}

	protected DDMTemplateModel newDDMTemplateModel(
		long groupId, long userId, long structureId, long sourceClassNameId) {

		DDMTemplateModel ddmTemplateModel = new DDMTemplateModelImpl();

		ddmTemplateModel.setUuid(SequentialUUID.generate());
		ddmTemplateModel.setTemplateId(counter.get());
		ddmTemplateModel.setGroupId(groupId);
		ddmTemplateModel.setCompanyId(COMPANY_ID);
		ddmTemplateModel.setUserId(userId);
		ddmTemplateModel.setCreateDate(nextFutureDate());
		ddmTemplateModel.setModifiedDate(nextFutureDate());
		ddmTemplateModel.setClassNameId(getClassNameId(DDMStructure.class));
		ddmTemplateModel.setClassPK(structureId);
		ddmTemplateModel.setResourceClassNameId(sourceClassNameId);
		ddmTemplateModel.setTemplateKey("BASIC-WEB-CONTENT");
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

	protected JournalArticleLocalizationModel
		newJournalArticleLocalizationModel(
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

		journalArticleModel.setContent(journalArticleContent);
		journalArticleModel.setDefaultLanguageId("en_US");
		journalArticleModel.setDDMStructureKey(
			defaultJournalDDMStructureModel.getStructureKey());
		journalArticleModel.setDDMTemplateKey(
			defaultJournalDDMTemplateModel.getTemplateKey());
		journalArticleModel.setDisplayDate(new Date());
		journalArticleModel.setExpirationDate(nextFutureDate());
		journalArticleModel.setReviewDate(new Date());
		journalArticleModel.setIndexable(true);
		journalArticleModel.setLastPublishDate(new Date());
		journalArticleModel.setStatusDate(new Date());

		return journalArticleModel;
	}

	protected JournalArticleResourceModel newJournalArticleResourceModel(
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

	protected PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId, String preferences, long ctCollctionId) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		portletPreferencesModel.setCompanyId(COMPANY_ID);
		portletPreferencesModel.setCtCollectionId(ctCollctionId);
		portletPreferencesModel.setPortletPreferencesId(counter.get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	protected SocialActivityModel newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData, long userId, long ctCollectionId) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		socialActivityModel.setActivityId(socialActivityCounter.get());
		socialActivityModel.setCtCollectionId(ctCollectionId);
		socialActivityModel.setGroupId(groupId);
		socialActivityModel.setCompanyId(COMPANY_ID);
		socialActivityModel.setUserId(userId);
		socialActivityModel.setCreateDate(CURRENT_TIME + timeCounter.get());
		socialActivityModel.setClassNameId(classNameId);
		socialActivityModel.setClassPK(classPK);
		socialActivityModel.setType(type);
		socialActivityModel.setExtraData(extraData);

		return socialActivityModel;
	}

	protected String readFile(String resourceName) throws IOException {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(getResourceInputStream(resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	protected static final long COMPANY_ID;

	protected static final int CPDEFINITION_COUNT =
		BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT *
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;

	protected static final long CURRENT_TIME = System.currentTimeMillis();

	protected static final long DEFAULT_JOURNAL_DDM_STRUCTURE_ID;

	protected static final long DEFAULT_USER_ID;

	protected static final long GLOBAL_GROUP_ID;

	protected static final long GUEST_GROUP_ID;

	protected static final long SAMPLE_USER_ID;

	protected static final String SAMPLE_USER_NAME = "Sample";

	protected static Map<Long, List<AssetCategoryModel>>[]
		assetCategoryModelsMaps;
	protected static long[] assetClassNameIds = new long[3];
	protected static final Map<Long, Integer> assetClassNameIdsIndexes =
		new HashMap<>();
	protected static Map<Long, List<AssetTagModel>>[] assetTagModelsMaps;
	protected static final SimpleCounter counter;
	protected static final SimpleCounter cTCollectionCounter =
		new SimpleCounter();
	protected static final SimpleCounter cTEntryCounter = new SimpleCounter();
	protected static final SimpleCounter cTPreferencesCounter =
		new SimpleCounter();
	protected static DDMStructureLayoutModel defaultDLDDMStructureLayoutModel;
	protected static DDMStructureModel defaultDLDDMStructureModel;
	protected static DDMStructureVersionModel defaultDLDDMStructureVersionModel;
	protected static DLFileEntryTypeModel defaultDLFileEntryTypeModel;
	protected static String journalArticleContent;
	protected static final Map<Long, String> journalArticleResourceUUIDs =
		new HashMap<>();
	protected static final Map<Long, SimpleCounter> layoutCounters =
		new HashMap<>();
	protected static final PortletPreferencesFactory portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();
	protected static final SimpleCounter resourcePermissionCounter =
		new SimpleCounter();
	protected static final SimpleCounter socialActivityCounter =
		new SimpleCounter();
	protected static final SimpleCounter timeCounter = new SimpleCounter();

	protected final PortletPreferencesImpl
		defaultAssetPublisherPortletPreferencesImpl;
	protected DDMStructureLayoutModel defaultJournalDDMStructureLayoutModel;
	protected DDMStructureModel defaultJournalDDMStructureModel;
	protected DDMStructureVersionModel defaultJournalDDMStructureVersionModel;
	protected DDMTemplateModel defaultJournalDDMTemplateModel;
	protected DDMTemplateVersionModel defaultJournalDDMTemplateVersionModel;

	private static void _initClassNameModels() {
		List<String> models = ModelHintsUtil.getModels();

		models.add(Layout.class.getName());
		models.add(UserPersonalSite.class.getName());

		models.add(getMBDiscussionCombinedClassName(BlogsEntry.class));
		models.add(getMBDiscussionCombinedClassName(WikiPage.class));

		models.add(CTAutoResolutionInfo.class.getName());
		models.add(CTCollection.class.getName());
		models.add(CTEntry.class.getName());
		models.add(CTMessage.class.getName());
		models.add(CTPreferences.class.getName());
		models.add(CTProcess.class.getName());

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			_classNameModels.put(model, classNameModel);
		}
	}

	private static void _initCSVWriters() {
		File outputDir = new File(BenchmarksPropsValues.OUTPUT_DIR);

		outputDir.mkdirs();

		for (String csvFileName : BenchmarksPropsValues.OUTPUT_CSV_FILE_NAMES) {
			try {
				_csvWriters.put(
					csvFileName,
					new UnsyncBufferedWriter(
						new OutputStreamWriter(
							new FileOutputStream(
								new File(
									outputDir, csvFileName.concat(".csv")))),
						_WRITER_BUFFER_SIZE) {

						@Override
						public void flush() {

							// Disable FreeMarker from flushing

						}

					});
			}
			catch (FileNotFoundException fileNotFoundException) {
				fileNotFoundException.printStackTrace();
			}
		}
	}

	private void _initDLFileEntryTypeModel() {
		defaultDLFileEntryTypeModel = new DLFileEntryTypeModelImpl();

		defaultDLFileEntryTypeModel.setUuid(SequentialUUID.generate());
		defaultDLFileEntryTypeModel.setFileEntryTypeId(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
		defaultDLFileEntryTypeModel.setCreateDate(nextFutureDate());
		defaultDLFileEntryTypeModel.setModifiedDate(nextFutureDate());
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

		defaultDLDDMStructureModel = newDDMStructureModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, getClassNameId(DLFileEntry.class),
			RawMetadataProcessor.TIKA_RAW_METADATA, _dlDDMStructureContent);

		defaultDLDDMStructureVersionModel = newDDMStructureVersionModel(
			defaultDLDDMStructureModel);

		defaultDLDDMStructureLayoutModel = newDDMStructureLayoutModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID,
			defaultDLDDMStructureVersionModel.getStructureVersionId(),
			_dlDDMStructureLayoutContent);

		defaultJournalDDMStructureModel = newDDMStructureModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID,
			getClassNameId(JournalArticle.class), "BASIC-WEB-CONTENT",
			_journalDDMStructureContent);

		defaultJournalDDMStructureVersionModel = newDDMStructureVersionModel(
			defaultJournalDDMStructureModel);

		defaultJournalDDMStructureLayoutModel = newDDMStructureLayoutModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID,
			defaultJournalDDMStructureVersionModel.getStructureVersionId(),
			_journalDDMStructureLayoutContent);

		defaultJournalDDMTemplateModel = newDDMTemplateModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID,
			defaultJournalDDMStructureModel.getStructureId(),
			getClassNameId(JournalArticle.class));

		defaultJournalDDMTemplateVersionModel = newDDMTemplateVersionModel(
			defaultJournalDDMTemplateModel);
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

		journalArticleContent = sb.toString();
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/data/";

	private static final SimpleCounter _FUTURE_COUNTER = new SimpleCounter();

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private static final Map<String, ClassNameModel> _classNameModels =
		new HashMap<>();
	private static final Map<String, Writer> _csvWriters = new HashMap<>();

	static {
		counter = new SimpleCounter(BenchmarksPropsValues.MAX_GROUP_COUNT + 1);

		_initClassNameModels();
		_initCSVWriters();

		assetClassNameIds[0] = getClassNameId(BlogsEntry.class);
		assetClassNameIds[1] = getClassNameId(JournalArticle.class);
		assetClassNameIds[2] = getClassNameId(WikiPage.class);

		COMPANY_ID = counter.get();
		DEFAULT_USER_ID = counter.get();
		GLOBAL_GROUP_ID = counter.get();
		GUEST_GROUP_ID = counter.get();
		SAMPLE_USER_ID = counter.get();
		DEFAULT_JOURNAL_DDM_STRUCTURE_ID = counter.get();
	}

	private final String _dlDDMStructureContent;
	private final String _dlDDMStructureLayoutContent;
	private final String _journalDDMStructureContent;
	private final String _journalDDMStructureLayoutContent;

}