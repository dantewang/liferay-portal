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
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayoutModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.dynamic.data.mapping.model.impl.DDMContentModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureLayoutModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.util.SimpleCounter;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public long getClassNameId(
		Class<?> clazz, Map<String, ClassNameModel> classNameModels) {

		ClassNameModel classNameModel = classNameModels.get(clazz.getName());

		return classNameModel.getClassNameId();
	}

	public String getDateLong(Date date) {
		return String.valueOf(date.getTime());
	}

	public String getDateString(Date date) {
		if (date == null) {
			return null;
		}

		return initContext.getSimpleDateFormat().format(date);
	}

	public InitContext getInitContext() {
		return initContext;
	}

	public String getResource(String resourceName) throws Exception {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(getResourceInputStream(resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	public InputStream getResourceInputStream(String resourceName) {
		ClassLoader classLoader = _clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
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

	public DDMTemplateModel newDDMTemplateModel(
		long groupId, long userId, long structureId, long sourceClassNameId) {

		SimpleCounter futureDateCounter = initContext.getFutureDateCounter();

		Date createDate = nextFutureDate(futureDateCounter);
		Date lastPublishDate = nextFutureDate(futureDateCounter);
		Date modifiedDate = nextFutureDate(futureDateCounter);

		DDMTemplateModel ddmTemplateModel = new DDMTemplateModelImpl();

		ddmTemplateModel.setUuid(SequentialUUID.generate());
		ddmTemplateModel.setTemplateId(initContext.getCounter().get());
		ddmTemplateModel.setGroupId(groupId);
		ddmTemplateModel.setCompanyId(initContext.getCompanyId());
		ddmTemplateModel.setUserId(userId);
		ddmTemplateModel.setCreateDate(createDate);
		ddmTemplateModel.setModifiedDate(modifiedDate);
		ddmTemplateModel.setClassNameId(
			getClassNameId(
				DDMStructure.class, initContext.getClassNameModels()));
		ddmTemplateModel.setClassPK(structureId);
		ddmTemplateModel.setResourceClassNameId(sourceClassNameId);
		ddmTemplateModel.setTemplateKey(
			String.valueOf(initContext.getCounter().get()));
		ddmTemplateModel.setVersion(DDMTemplateConstants.VERSION_DEFAULT);
		ddmTemplateModel.setVersionUserId(userId);
		ddmTemplateModel.setVersionUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);

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
		ddmTemplateModel.setLastPublishDate(lastPublishDate);

		return ddmTemplateModel;
	}

	public IntegerWrapper newInteger() {
		return new IntegerWrapper();
	}

	public <K, V> ObjectValuePair<K, V> newObjectValuePair(K key, V value) {
		return new ObjectValuePair<>(key, value);
	}

	public UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser) {

		String greeting =
			DataFactoryConstants.GREETING_PREFIX + screenName +
				StringPool.EXCLAMATION;

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(initContext.getCompanyId());
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(initContext.getCounter().get());
		userModel.setPassword(DataFactoryConstants.USER_PASSWORD);
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion(
			DataFactoryConstants.REMINDER_QUERY_QUESTION);
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(
			screenName + DataFactoryConstants.EMAIL_POSTFIX);
		userModel.setScreenName(screenName);
		userModel.setLanguageId(DataFactoryConstants.LANGUAGE_ID);
		userModel.setGreeting(greeting);
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

	public Date nextFutureDate(SimpleCounter futureDateCounter) {
		return new Date(_FUTURE_TIME + (futureDateCounter.get() * Time.SECOND));
	}

	protected BaseDataFactory(InitContext initContext) throws Exception {
		this.initContext = initContext;
	}

	protected final InitContext initContext;

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/";

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private final Class<?> _clazz = getClass();

}