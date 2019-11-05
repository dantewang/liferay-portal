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

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.model.impl.BlogsEntryModelImpl;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalizationModel;
import com.liferay.friendly.url.model.FriendlyURLEntryMappingModel;
import com.liferay.friendly.url.model.FriendlyURLEntryModel;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryMappingModelImpl;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationDeliveryModel;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.model.impl.UserNotificationDeliveryModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class BlogDataFactory extends BaseDataFactory {

	public BlogDataFactory(DataFactoryContext dataFactoryContext) {
		super(dataFactoryContext);
	}

	public long getBlogsEntryClassNameId() {
		return getClassNameId(BlogsEntry.class);
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		int maxBlogsEntryCount = dataFactoryContext.getMaxBlogsEntryCount();

		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			maxBlogsEntryCount);

		for (int i = 1; i <= maxBlogsEntryCount; i++) {
			blogEntryModels.add(_newBlogsEntryModel(groupId, i));
		}

		return blogEntryModels;
	}

	public FriendlyURLEntryLocalizationModel
		newFriendlyURLEntryLocalizationModel(
			FriendlyURLEntryModel friendlyURLEntryModel,
			BlogsEntryModel blogsEntryModel) {

		FriendlyURLEntryLocalizationModel friendlyURLEntryLocalizationModel =
			new FriendlyURLEntryLocalizationModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		friendlyURLEntryLocalizationModel.setFriendlyURLEntryLocalizationId(
			counter.get());

		friendlyURLEntryLocalizationModel.setFriendlyURLEntryId(
			friendlyURLEntryModel.getFriendlyURLEntryId());
		friendlyURLEntryLocalizationModel.setGroupId(
			friendlyURLEntryModel.getGroupId());
		friendlyURLEntryLocalizationModel.setCompanyId(
			friendlyURLEntryModel.getCompanyId());
		friendlyURLEntryLocalizationModel.setClassNameId(
			friendlyURLEntryModel.getClassNameId());
		friendlyURLEntryLocalizationModel.setClassPK(
			friendlyURLEntryModel.getClassPK());

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		friendlyURLEntryLocalizationModel.setLanguageId(languageId);

		friendlyURLEntryLocalizationModel.setUrlTitle(
			blogsEntryModel.getUrlTitle());

		return friendlyURLEntryLocalizationModel;
	}

	public FriendlyURLEntryMappingModel newFriendlyURLEntryMapping(
		FriendlyURLEntryModel friendlyURLEntryModel) {

		FriendlyURLEntryMappingModel friendlyURLEntryMappingModel =
			new FriendlyURLEntryMappingModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		friendlyURLEntryMappingModel.setFriendlyURLEntryMappingId(
			counter.get());

		friendlyURLEntryMappingModel.setClassNameId(
			friendlyURLEntryModel.getClassNameId());
		friendlyURLEntryMappingModel.setClassPK(
			friendlyURLEntryModel.getClassPK());
		friendlyURLEntryMappingModel.setFriendlyURLEntryId(
			friendlyURLEntryModel.getFriendlyURLEntryId());

		return friendlyURLEntryMappingModel;
	}

	public FriendlyURLEntryModel newFriendlyURLEntryModel(
		BlogsEntryModel blogsEntryModel) {

		FriendlyURLEntryModel friendlyURLEntryModel =
			new FriendlyURLEntryModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		friendlyURLEntryModel.setDefaultLanguageId(
			DataFactoryConstants.LANGUAGE_ID);
		friendlyURLEntryModel.setUuid(SequentialUUID.generate());
		friendlyURLEntryModel.setFriendlyURLEntryId(counter.get());
		friendlyURLEntryModel.setGroupId(blogsEntryModel.getGroupId());
		friendlyURLEntryModel.setCompanyId(dataFactoryContext.getCompanyId());
		friendlyURLEntryModel.setCreateDate(new Date());
		friendlyURLEntryModel.setModifiedDate(new Date());
		friendlyURLEntryModel.setClassNameId(getClassNameId(BlogsEntry.class));
		friendlyURLEntryModel.setClassPK(blogsEntryModel.getEntryId());

		return friendlyURLEntryModel;
	}

	public UserNotificationDeliveryModel newUserNotificationDeliveryModel(
		String portletId) {

		UserNotificationDeliveryModel userNotificationDeliveryModel =
			new UserNotificationDeliveryModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		userNotificationDeliveryModel.setUserNotificationDeliveryId(
			counter.get());

		userNotificationDeliveryModel.setCompanyId(
			dataFactoryContext.getCompanyId());
		userNotificationDeliveryModel.setUserId(
			dataFactoryContext.getSampleUserId());
		userNotificationDeliveryModel.setPortletId(portletId);
		userNotificationDeliveryModel.setDeliveryType(
			UserNotificationDeliveryConstants.TYPE_WEBSITE);
		userNotificationDeliveryModel.setDeliver(true);

		return userNotificationDeliveryModel;
	}

	private BlogsEntryModel _newBlogsEntryModel(long groupId, int index) {
		BlogsEntryModel blogsEntryModel = new BlogsEntryModelImpl();

		SimpleCounter counter = dataFactoryContext.getCounter();

		blogsEntryModel.setUuid(SequentialUUID.generate());
		blogsEntryModel.setEntryId(counter.get());
		blogsEntryModel.setGroupId(groupId);
		blogsEntryModel.setCompanyId(dataFactoryContext.getCompanyId());
		blogsEntryModel.setUserId(dataFactoryContext.getSampleUserId());
		blogsEntryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		blogsEntryModel.setCreateDate(new Date());
		blogsEntryModel.setModifiedDate(new Date());
		blogsEntryModel.setTitle(
			DataFactoryConstants.BLOG_ENTRY_TITLE_PREFIX + index);
		blogsEntryModel.setSubtitle(
			DataFactoryConstants.BLOG_ENTRY_SUBTITLE_PREFIX + index);
		blogsEntryModel.setUrlTitle(
			DataFactoryConstants.BLOG_URL_TITLE_PREFIX + index);
		blogsEntryModel.setContent(
			DataFactoryConstants.BLOG_CONTENT_PREFIX + index + ".");
		blogsEntryModel.setDisplayDate(new Date());
		blogsEntryModel.setLastPublishDate(new Date());
		blogsEntryModel.setStatusByUserId(dataFactoryContext.getSampleUserId());
		blogsEntryModel.setStatusDate(new Date());

		return blogsEntryModel;
	}

}