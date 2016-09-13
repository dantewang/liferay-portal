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

import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.kernel.model.BlogsEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntryTypeModel;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagStatsModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.impl.WikiNodeModelImpl;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class InitDataFactoryUtil {

	public static String getResource(Class<?> clazz, String resourceName)
		throws Exception {

		List<String> lines = new ArrayList<>();

		StringUtil.readLines(
			getResourceInputStream(clazz, resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	public static InputStream getResourceInputStream(
		Class<?> clazz, String resourceName) {

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	public static Map<String, ClassNameModel> initClassNameModels(
		SimpleCounter simpleCounter) {

		Map<String, ClassNameModel> classNameModels = new HashMap<>();
		List<String> models = ModelHintsUtil.getModels();
		SimpleCounter counter = simpleCounter;

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			long classNameId = counter.get();

			classNameModel.setClassNameId(classNameId);

			classNameModel.setValue(model);

			classNameModels.put(model, classNameModel);
		}

		return classNameModels;
	}

	public static AccountModel initAccountModel(
		long companyId, long accountId) {

		AccountModel accountModel = new AccountModelImpl();

		accountModel.setAccountId(accountId);
		accountModel.setCompanyId(companyId);
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());
		accountModel.setName("Liferay");
		accountModel.setLegalName("Liferay, Inc.");

		return accountModel;
	}

	public static CompanyModel initCompanyModel(
		long companyId, long accountId) {

		CompanyModel companyModel = new CompanyModelImpl();

		companyModel.setCompanyId(companyId);
		companyModel.setAccountId(accountId);
		companyModel.setWebId("liferay.com");
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);

		return companyModel;
	}

	public static String initJournalArticleContent(int maxJournalArticleSize)
	{
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

	public static GroupModel newGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site, long companyId, long sampleUserId)
		throws Exception {

		GroupModel groupModel = new GroupModelImpl();

		groupModel.setUuid(SequentialUUID.generate());
		groupModel.setGroupId(groupId);
		groupModel.setCompanyId(companyId);
		groupModel.setCreatorUserId(sampleUserId);
		groupModel.setClassNameId(classNameId);
		groupModel.setClassPK(classPK);
		groupModel.setTreePath(
			StringPool.SLASH + groupModel.getGroupId() + StringPool.SLASH);
		groupModel.setGroupKey(name);
		groupModel.setName(name);
		groupModel.setManualMembership(true);
		groupModel.setMembershipRestriction(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION);
		groupModel.setFriendlyURL(
			StringPool.FORWARD_SLASH +
				FriendlyURLNormalizerUtil.normalize(name));
		groupModel.setSite(site);
		groupModel.setActive(true);

		return groupModel;
	}

	public static Date nextFutureDate(SimpleCounter futureDateCounter) {
		return new Date(_FUTURE_TIME + (futureDateCounter.get() * Time.SECOND));
	}

	public static GroupModel initGroupModel(
			long groupId, long classNameId, long classPK, String name,
			boolean site, long companyId, long sampleUserId)
		throws Exception {

		GroupModel globalGroupModel = newGroupModel(
			groupId, classNameId, classPK, name, site, companyId, sampleUserId);
		return globalGroupModel;
	}

	public static RoleModel newRoleModel(
		String name, int type, long roleId, long companyId, long sampleUserId,
		String sampleUserName, long classNameId) {

		RoleModel roleModel = new RoleModelImpl();

		roleModel.setUuid(SequentialUUID.generate());
		roleModel.setRoleId(roleId);
		roleModel.setCompanyId(companyId);
		roleModel.setUserId(sampleUserId);
		roleModel.setUserName(sampleUserName);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());
		roleModel.setClassNameId(classNameId);
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}

	public static long getClassNameId(
		Class<?> clazz, Map<String, ClassNameModel> classNameModels) {

		ClassNameModel classNameModel = classNameModels.get(clazz.getName());

		return classNameModel.getClassNameId();
	}

	public static List<String> initUserFirstNames(Class<?> clazz)
		throws IOException {

		List<String> firstNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				getResourceInputStream(clazz, "first_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			firstNames.add(line);
		}

		unsyncBufferedReader.close();

		return firstNames;
	}

	public static List<String> initUserLastNames(Class<?> clazz)
		throws IOException {

		List<String> lastNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				getResourceInputStream(clazz, "last_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			lastNames.add(line);
		}

		unsyncBufferedReader.close();

		return lastNames;
	}

	public static UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser, long contactId, long companyId) {

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(companyId);
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(contactId);
		userModel.setPassword("test");
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion("What is your screen name?");
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(screenName + "@liferay.com");
		userModel.setScreenName(screenName);
		userModel.setLanguageId("en_US");
		userModel.setGreeting("Welcome " + screenName + StringPool.EXCLAMATION);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		userModel.setLoginDate(new Date());
		userModel.setLastLoginDate(new Date());
		userModel.setLastFailedLoginDate(new Date());
		userModel.setLockoutDate(new Date());
		userModel.setAgreedToTermsOfUse(true);
		userModel.setEmailAddressVerified(true);

		return userModel;
	}

	public static VirtualHostModel initVirtualHostModel(
		String hostname, long virtualHostId, long companyId) {

		VirtualHostModel virtualHostModel = new VirtualHostModelImpl();

		virtualHostModel.setVirtualHostId(virtualHostId);
		virtualHostModel.setCompanyId(companyId);
		virtualHostModel.setHostname(hostname);
		return virtualHostModel;
	}

	public static AssetCategoryModel newAssetCategoryModel(
		long groupId, long lastRightCategoryId, String name, long vocabularyId,
		long categoryId, long companyId, long userId, String userName) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		assetCategoryModel.setUuid(SequentialUUID.generate());
		assetCategoryModel.setCategoryId(categoryId);
		assetCategoryModel.setGroupId(groupId);
		assetCategoryModel.setCompanyId(companyId);
		assetCategoryModel.setUserId(userId);
		assetCategoryModel.setUserName(userName);
		assetCategoryModel.setCreateDate(new Date());
		assetCategoryModel.setModifiedDate(new Date());
		assetCategoryModel.setParentCategoryId(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		assetCategoryModel.setLeftCategoryId(lastRightCategoryId++);
		assetCategoryModel.setRightCategoryId(lastRightCategoryId++);
		assetCategoryModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetCategoryModel.setTitle(sb.toString());

		assetCategoryModel.setVocabularyId(vocabularyId);
		assetCategoryModel.setLastPublishDate(new Date());

		return assetCategoryModel;
	}

	public static AssetVocabularyModel newAssetVocabularyModel(
		long grouId, long userId, String userName, String name,
		long vocabularyId, long companyId) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		assetVocabularyModel.setUuid(SequentialUUID.generate());
		assetVocabularyModel.setVocabularyId(vocabularyId);
		assetVocabularyModel.setGroupId(grouId);
		assetVocabularyModel.setCompanyId(companyId);
		assetVocabularyModel.setUserId(userId);
		assetVocabularyModel.setUserName(userName);
		assetVocabularyModel.setCreateDate(new Date());
		assetVocabularyModel.setModifiedDate(new Date());
		assetVocabularyModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetVocabularyModel.setTitle(sb.toString());

		assetVocabularyModel.setSettings(
			"multiValued=true\\nselectedClassNameIds=0");
		assetVocabularyModel.setLastPublishDate(new Date());

		return assetVocabularyModel;
	}

	public static AssetTagStatsModel newAssetTagStatsModel(
		long tagId, long classNameId, long tagStatsId) {

		AssetTagStatsModel assetTagStatsModel = new AssetTagStatsModelImpl();

		assetTagStatsModel.setTagStatsId(tagStatsId);
		assetTagStatsModel.setTagId(tagId);
		assetTagStatsModel.setClassNameId(classNameId);

		return assetTagStatsModel;
	}

	public static DDMStructureLayoutModel newDDMStructureLayoutModel(
		long groupId, long userId, long structureVersionId, String definition,
		long structureLayoutId, long companyId, String userName,
		SimpleCounter futureDateCounter) {

		DDMStructureLayoutModel ddmStructureLayoutModel =
			new DDMStructureLayoutModelImpl();

		ddmStructureLayoutModel.setUuid(SequentialUUID.generate());
		ddmStructureLayoutModel.setStructureLayoutId(structureLayoutId);
		ddmStructureLayoutModel.setGroupId(groupId);
		ddmStructureLayoutModel.setCompanyId(companyId);
		ddmStructureLayoutModel.setUserId(userId);
		ddmStructureLayoutModel.setUserName(userName);
		ddmStructureLayoutModel.setCreateDate(
			nextFutureDate(futureDateCounter));
		ddmStructureLayoutModel.setModifiedDate(
			nextFutureDate(futureDateCounter));
		ddmStructureLayoutModel.setStructureVersionId(structureVersionId);
		ddmStructureLayoutModel.setDefinition(definition);

		return ddmStructureLayoutModel;
	}

	public static DDMStructureModel newDDMStructureModel(
		long groupId, long userId, long classNameId, String structureKey,
		String definition, long structureId, long companyId, String userName,
		SimpleCounter futureDateCounter) {

		DDMStructureModel ddmStructureModel = new DDMStructureModelImpl();

		ddmStructureModel.setUuid(SequentialUUID.generate());
		ddmStructureModel.setStructureId(structureId);
		ddmStructureModel.setGroupId(groupId);
		ddmStructureModel.setCompanyId(companyId);
		ddmStructureModel.setUserId(userId);
		ddmStructureModel.setUserName(userName);
		ddmStructureModel.setVersionUserId(userId);
		ddmStructureModel.setVersionUserName(userName);
		ddmStructureModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
		ddmStructureModel.setModifiedDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));
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
		ddmStructureModel.setLastPublishDate(
			InitDataFactoryUtil.nextFutureDate(futureDateCounter));

		return ddmStructureModel;
	}

	public static DDMTemplateModel newDDMTemplateModel(
		long groupId, long userId, long structureId, long sourceClassNameId,
		long templateId, long companyId, SimpleCounter futureDateCounter,
		Map<String, ClassNameModel> classNameModels, long templateKey,
		String versionUserName) {

		DDMTemplateModel ddmTemplateModel = new DDMTemplateModelImpl();

		ddmTemplateModel.setUuid(SequentialUUID.generate());
		ddmTemplateModel.setTemplateId(templateId);
		ddmTemplateModel.setGroupId(groupId);
		ddmTemplateModel.setCompanyId(companyId);
		ddmTemplateModel.setUserId(userId);
		ddmTemplateModel.setCreateDate(nextFutureDate(futureDateCounter));
		ddmTemplateModel.setModifiedDate(nextFutureDate(futureDateCounter));
		ddmTemplateModel.setClassNameId(
			getClassNameId(DDMStructure.class, classNameModels));
		ddmTemplateModel.setClassPK(structureId);
		ddmTemplateModel.setResourceClassNameId(sourceClassNameId);
		ddmTemplateModel.setTemplateKey(String.valueOf(templateKey));
		ddmTemplateModel.setVersion(DDMTemplateConstants.VERSION_DEFAULT);
		ddmTemplateModel.setVersionUserId(userId);
		ddmTemplateModel.setVersionUserName(versionUserName);

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
		ddmTemplateModel.setLastPublishDate(nextFutureDate(futureDateCounter));

		return ddmTemplateModel;
	}

	public static WikiNodeModel newWikiNodeModel(
		long groupId, int index, long nodeId, long companyId, long userId,
		String userName) {

		WikiNodeModel wikiNodeModel = new WikiNodeModelImpl();

		wikiNodeModel.setUuid(SequentialUUID.generate());
		wikiNodeModel.setNodeId(nodeId);
		wikiNodeModel.setGroupId(groupId);
		wikiNodeModel.setCompanyId(companyId);
		wikiNodeModel.setUserId(userId);
		wikiNodeModel.setUserName(userName);
		wikiNodeModel.setCreateDate(new Date());
		wikiNodeModel.setModifiedDate(new Date());
		wikiNodeModel.setName("Test Node " + index);
		wikiNodeModel.setLastPostDate(new Date());
		wikiNodeModel.setLastPublishDate(new Date());
		wikiNodeModel.setStatusDate(new Date());

		return wikiNodeModel;
	}

	public static DDMStructureVersionModel newDDMStructureVersionModel(
		DDMStructureModel ddmStructureModel, String userName) {

		DDMStructureVersionModel ddmStructureVersionModel =
			new DDMStructureVersionModelImpl();

		ddmStructureVersionModel.setStructureVersionId(
			InitContextUtil.getCounter().get());
		ddmStructureVersionModel.setGroupId(ddmStructureModel.getGroupId());
		ddmStructureVersionModel.setCompanyId(InitContextUtil.getCompanyId());
		ddmStructureVersionModel.setUserId(ddmStructureModel.getUserId());
		ddmStructureVersionModel.setUserName(userName);
		ddmStructureVersionModel.setCreateDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));
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
		ddmStructureVersionModel.setStatusByUserName(userName);
		ddmStructureVersionModel.setStatusDate(
			InitDataFactoryUtil.nextFutureDate(
				InitContextUtil.getFutureDateCounter()));

		return ddmStructureVersionModel;
	}

	public static String[] nextUserName(long index) {
		String[] userName = new String[2];
		int firstNameSize = InitContextUtil.getFirstNames().size();
		int lastNameSize = InitContextUtil.getLastNames().size();

		userName[0] = InitContextUtil.getFirstNames().get(
			(int)(index / lastNameSize) % firstNameSize);
		userName[1] = InitContextUtil.getLastNames().get(
			(int)(index % lastNameSize));

		return userName;
	}

	public static long getGroupClassNameId() {
		return getClassNameId(
			Group.class, InitContextUtil.getClassNameModels());
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;
}