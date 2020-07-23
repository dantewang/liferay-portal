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
import com.liferay.blogs.model.BlogsStatsUserModel;
import com.liferay.blogs.model.impl.BlogsEntryModelImpl;
import com.liferay.blogs.model.impl.BlogsStatsUserModelImpl;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class BlogDataFactory extends BaseDataFactory {

	public long getBlogsEntryClassNameId() {
		return getClassNameId(BlogsEntry.class);
	}

	public int getMaxBlogsEntryCommentCount() {
		return BenchmarksPropsValues.MAX_BLOGS_ENTRY_COMMENT_COUNT;
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_BLOGS_ENTRY_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_BLOGS_ENTRY_COUNT; i++) {
			blogEntryModels.add(newBlogsEntryModel(groupId, i));
		}

		return blogEntryModels;
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		BlogsStatsUserModel blogsStatsUserModel = new BlogsStatsUserModelImpl();

		blogsStatsUserModel.setStatsUserId(counter.get());
		blogsStatsUserModel.setGroupId(groupId);
		blogsStatsUserModel.setCompanyId(COMPANY_ID);
		blogsStatsUserModel.setUserId(SAMPLE_USER_ID);
		blogsStatsUserModel.setEntryCount(
			BenchmarksPropsValues.MAX_BLOGS_ENTRY_COUNT);
		blogsStatsUserModel.setLastPostDate(new Date());

		return blogsStatsUserModel;
	}

	public FriendlyURLEntryLocalizationModel
		newFriendlyURLEntryLocalizationModel(
			FriendlyURLEntryModel friendlyURLEntryModel,
			BlogsEntryModel blogsEntryModel) {

		FriendlyURLEntryLocalizationModel friendlyURLEntryLocalizationModel =
			new FriendlyURLEntryLocalizationModelImpl();

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
		friendlyURLEntryLocalizationModel.setLanguageId(
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
		friendlyURLEntryLocalizationModel.setUrlTitle(
			blogsEntryModel.getUrlTitle());

		return friendlyURLEntryLocalizationModel;
	}

	public FriendlyURLEntryMappingModel newFriendlyURLEntryMapping(
		FriendlyURLEntryModel friendlyURLEntryModel) {

		FriendlyURLEntryMappingModel friendlyURLEntryMappingModel =
			new FriendlyURLEntryMappingModelImpl();

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

		friendlyURLEntryModel.setDefaultLanguageId("en_US");
		friendlyURLEntryModel.setUuid(SequentialUUID.generate());
		friendlyURLEntryModel.setFriendlyURLEntryId(counter.get());
		friendlyURLEntryModel.setGroupId(blogsEntryModel.getGroupId());
		friendlyURLEntryModel.setCompanyId(COMPANY_ID);
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

		userNotificationDeliveryModel.setUserNotificationDeliveryId(
			counter.get());
		userNotificationDeliveryModel.setCompanyId(COMPANY_ID);
		userNotificationDeliveryModel.setUserId(SAMPLE_USER_ID);
		userNotificationDeliveryModel.setPortletId(portletId);
		userNotificationDeliveryModel.setDeliveryType(
			UserNotificationDeliveryConstants.TYPE_WEBSITE);
		userNotificationDeliveryModel.setDeliver(true);

		return userNotificationDeliveryModel;
	}

	protected BlogsEntryModel newBlogsEntryModel(long groupId, int index) {
		BlogsEntryModel blogsEntryModel = new BlogsEntryModelImpl();

		blogsEntryModel.setUuid(SequentialUUID.generate());
		blogsEntryModel.setEntryId(counter.get());
		blogsEntryModel.setGroupId(groupId);
		blogsEntryModel.setCompanyId(COMPANY_ID);
		blogsEntryModel.setUserId(SAMPLE_USER_ID);
		blogsEntryModel.setUserName(SAMPLE_USER_NAME);
		blogsEntryModel.setCreateDate(new Date());
		blogsEntryModel.setModifiedDate(new Date());
		blogsEntryModel.setTitle("Test Blog " + index);
		blogsEntryModel.setSubtitle("Subtitle of Test Blog " + index);
		blogsEntryModel.setUrlTitle("testblog" + index);
		blogsEntryModel.setContent("This is test blog " + index + ".");
		blogsEntryModel.setDisplayDate(new Date());
		blogsEntryModel.setLastPublishDate(new Date());
		blogsEntryModel.setStatusByUserId(SAMPLE_USER_ID);
		blogsEntryModel.setStatusDate(new Date());

		return blogsEntryModel;
	}

}