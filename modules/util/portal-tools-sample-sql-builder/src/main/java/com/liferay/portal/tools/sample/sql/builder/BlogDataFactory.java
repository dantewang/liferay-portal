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
public class BlogDataFactory {

	public BlogDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public long getBlogsEntryClassNameId() {
		return _initContext.getClassNameId(
			BlogsEntry.class, _initContext.getClassNameModels());
	}

	public int getMaxBlogsEntryCommentCount() {
		return _initContext.getMaxBlogsEntryCommentCount();
	}

	public List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		int maxBlogsEntryCount = _initContext.getMaxBlogsEntryCount();

		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			maxBlogsEntryCount);

		SimpleCounter counter = _initContext.getCounter();

		for (int i = 1; i <= maxBlogsEntryCount; i++) {
			blogEntryModels.add(
				newBlogsEntryModel(
					groupId, i, counter.get(),
					_initContext.getCompanyId(), _initContext.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME));
		}

		return blogEntryModels;
	}

	public BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		BlogsStatsUserModel blogsStatsUserModel = new BlogsStatsUserModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		blogsStatsUserModel.setStatsUserId(counter.get());
		blogsStatsUserModel.setGroupId(groupId);
		blogsStatsUserModel.setCompanyId(_initContext.getCompanyId());
		blogsStatsUserModel.setUserId(_initContext.getSampleUserId());
		blogsStatsUserModel.setEntryCount(_initContext.getMaxBlogsEntryCount());
		blogsStatsUserModel.setLastPostDate(new Date());

		return blogsStatsUserModel;
	}

	public FriendlyURLModel newFriendlyURLModel(
		BlogsEntryModel blogsEntryModel) {

		FriendlyURLModel friendlyURLModel = new FriendlyURLModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		friendlyURLModel.setUuid(SequentialUUID.generate());
		friendlyURLModel.setFriendlyURLId(counter.get());
		friendlyURLModel.setGroupId(blogsEntryModel.getGroupId());
		friendlyURLModel.setCompanyId(_initContext.getCompanyId());
		friendlyURLModel.setCreateDate(new Date());
		friendlyURLModel.setModifiedDate(new Date());
		friendlyURLModel.setClassNameId(
			_initContext.getClassNameId(
				BlogsEntry.class, _initContext.getClassNameModels()));
		friendlyURLModel.setClassPK(blogsEntryModel.getEntryId());
		friendlyURLModel.setUrlTitle(blogsEntryModel.getUrlTitle());
		friendlyURLModel.setMain(true);

		return friendlyURLModel;
	}

	protected BlogsEntryModel newBlogsEntryModel(
		long groupId, int index, long entryId, long companyId,
		long sampleUserId, String userName) {

		BlogsEntryModel blogsEntryModel = new BlogsEntryModelImpl();

		blogsEntryModel.setUuid(SequentialUUID.generate());
		blogsEntryModel.setEntryId(entryId);
		blogsEntryModel.setGroupId(groupId);
		blogsEntryModel.setCompanyId(companyId);
		blogsEntryModel.setUserId(sampleUserId);
		blogsEntryModel.setUserName(userName);
		blogsEntryModel.setCreateDate(new Date());
		blogsEntryModel.setModifiedDate(new Date());
		blogsEntryModel.setTitle("Test Blog " + index);
		blogsEntryModel.setSubtitle("Subtitle of Test Blog " + index);
		blogsEntryModel.setUrlTitle("testblog" + index);
		blogsEntryModel.setContent("This is test blog " + index + ".");
		blogsEntryModel.setDisplayDate(new Date());
		blogsEntryModel.setLastPublishDate(new Date());
		blogsEntryModel.setStatusByUserId(sampleUserId);
		blogsEntryModel.setStatusDate(new Date());

		return blogsEntryModel;
	}

	private final InitContext _initContext;

}