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

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.model.BlogsEntryModel;
import com.liferay.blogs.kernel.model.BlogsStatsUserModel;
import com.liferay.portlet.blogs.model.impl.BlogsEntryModelImpl;
import com.liferay.portlet.blogs.model.impl.BlogsStatsUserModelImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class BlogDataFactory {

	public static long getBlogsEntryClassNameId() {
		return InitDataFactoryUtil.getClassNameId(
			BlogsEntry.class, InitContextUtil.getClassNameModels());
	}

	public static int getMaxBlogsEntryCommentCount() {
		return InitContextUtil.getMaxBlogsEntryCommentCount();
	}

	public static List<BlogsEntryModel> newBlogsEntryModels(long groupId) {
		int maxBlogsEntryCount = InitContextUtil.getMaxBlogsEntryCount();

		List<BlogsEntryModel> blogEntryModels = new ArrayList<>(
			maxBlogsEntryCount);

		for (int i = 1; i <= maxBlogsEntryCount; i++) {
			blogEntryModels.add(
				newBlogsEntryModel(
					groupId, i, InitContextUtil.getCounter().get(),
					InitContextUtil.getCompanyId(),
					InitContextUtil.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME));
		}

		return blogEntryModels;
	}

	public static BlogsStatsUserModel newBlogsStatsUserModel(long groupId) {
		BlogsStatsUserModel blogsStatsUserModel = new BlogsStatsUserModelImpl();

		blogsStatsUserModel.setStatsUserId(InitContextUtil.getCounter().get());
		blogsStatsUserModel.setGroupId(groupId);
		blogsStatsUserModel.setCompanyId(InitContextUtil.getCompanyId());
		blogsStatsUserModel.setUserId(InitContextUtil.getSampleUserId());
		blogsStatsUserModel.setEntryCount(
			InitContextUtil.getMaxBlogsEntryCount());
		blogsStatsUserModel.setLastPostDate(new Date());

		return blogsStatsUserModel;
	}

	public static BlogsEntryModel newBlogsEntryModel(
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
		blogsEntryModel.setStatusDate(new Date());

		return blogsEntryModel;
	}

}