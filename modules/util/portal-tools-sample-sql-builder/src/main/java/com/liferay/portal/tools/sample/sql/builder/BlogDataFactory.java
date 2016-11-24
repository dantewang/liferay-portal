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
import com.liferay.friendly.url.model.FriendlyURLModel;
import com.liferay.friendly.url.model.impl.FriendlyURLModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class BlogDataFactory extends BaseDataFactory {

	public BlogDataFactory(InitContext initContext) {
		super(initContext);
	}

	public long getBlogsEntryClassNameId() {
		return getClassNameId(
			BlogsEntry.class, initContext.getClassNameModels());
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		int maxBlogsEntryCount = initContext.getMaxBlogsEntryCount();

		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			maxBlogsEntryCount);

		for (int i = 1; i <= maxBlogsEntryCount; i++) {
			blogEntryModels.add(newBlogsEntryModel(groupId, i));
		}

		return blogEntryModels;
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		BlogsStatsUserModel blogsStatsUserModel = new BlogsStatsUserModelImpl();

		SimpleCounter counter = initContext.getCounter();

		blogsStatsUserModel.setStatsUserId(counter.get());

		blogsStatsUserModel.setGroupId(groupId);
		blogsStatsUserModel.setCompanyId(initContext.getCompanyId());
		blogsStatsUserModel.setUserId(initContext.getSampleUserId());
		blogsStatsUserModel.setEntryCount(initContext.getMaxBlogsEntryCount());
		blogsStatsUserModel.setLastPostDate(new Date());

		return blogsStatsUserModel;
	}

	public FriendlyURLModel newFriendlyURLModel(
		BlogsEntryModel blogsEntryModel) {

		FriendlyURLModel friendlyURLModel = new FriendlyURLModelImpl();

		SimpleCounter counter = initContext.getCounter();

		friendlyURLModel.setUuid(SequentialUUID.generate());
		friendlyURLModel.setFriendlyURLId(counter.get());
		friendlyURLModel.setGroupId(blogsEntryModel.getGroupId());
		friendlyURLModel.setCompanyId(initContext.getCompanyId());
		friendlyURLModel.setCreateDate(new Date());
		friendlyURLModel.setModifiedDate(new Date());
		friendlyURLModel.setClassNameId(
			getClassNameId(BlogsEntry.class, initContext.getClassNameModels()));
		friendlyURLModel.setClassPK(blogsEntryModel.getEntryId());
		friendlyURLModel.setUrlTitle(blogsEntryModel.getUrlTitle());
		friendlyURLModel.setMain(true);

		return friendlyURLModel;
	}

	protected BlogsEntryModel newBlogsEntryModel(long groupId, int index) {
		BlogsEntryModel blogsEntryModel = new BlogsEntryModelImpl();

		SimpleCounter counter = initContext.getCounter();

		blogsEntryModel.setUuid(SequentialUUID.generate());
		blogsEntryModel.setEntryId(counter.get());
		blogsEntryModel.setGroupId(groupId);
		blogsEntryModel.setCompanyId(initContext.getCompanyId());
		blogsEntryModel.setUserId(initContext.getSampleUserId());
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
		blogsEntryModel.setStatusByUserId(initContext.getSampleUserId());
		blogsEntryModel.setStatusDate(new Date());

		return blogsEntryModel;
	}

}