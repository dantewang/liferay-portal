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

import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageConstants;
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.WikiPageResourceModel;
import com.liferay.wiki.model.impl.WikiNodeModelImpl;
import com.liferay.wiki.model.impl.WikiPageModelImpl;
import com.liferay.wiki.model.impl.WikiPageResourceModelImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class WikiDataFactory {

	public WikiDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		int maxWikiNodeCount = _initContext.getMaxWikiNodeCount();

		List<WikiNodeModel> wikiNodeModels = new ArrayList<>(maxWikiNodeCount);

		SimpleCounter counter = _initContext.getCounter();

		for (int i = 1; i <= maxWikiNodeCount; i++) {
			wikiNodeModels.add(
				newWikiNodeModel(
					groupId, i, counter.get(),
					_initContext.getCompanyId(), _initContext.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME));
		}

		return wikiNodeModels;
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel)
	{
		int maxWikiPageCount = _initContext.getMaxWikiPageCount();

		List<WikiPageModel> wikiPageModels = new ArrayList<>(maxWikiPageCount);
		
		SimpleCounter counter = _initContext.getCounter();

		for (int i = 1; i <= maxWikiPageCount; i++) {
			wikiPageModels.add(
				newWikiPageModel(
					wikiNodeModel, i, counter.get(),counter.get(),
					_initContext.getCompanyId(), _initContext.getSampleUserId(),
					DataFactoryConstants.SAMPLE_USER_NAME));
		}

		return wikiPageModels;
	}

	public WikiPageResourceModel newWikiPageResourceModel(
		WikiPageModel wikiPageModel) {

		WikiPageResourceModel wikiPageResourceModel =
			new WikiPageResourceModelImpl();

		wikiPageResourceModel.setUuid(SequentialUUID.generate());
		wikiPageResourceModel.setResourcePrimKey(
			wikiPageModel.getResourcePrimKey());
		wikiPageResourceModel.setNodeId(wikiPageModel.getNodeId());
		wikiPageResourceModel.setTitle(wikiPageModel.getTitle());

		return wikiPageResourceModel;
	}

	public long getWikiPageClassNameId() {
		return _initContext.getClassNameId(
			WikiPage.class, _initContext.getClassNameModels());
	}

	public int getMaxWikiPageCommentCount() {
		return _initContext.getMaxWikiPageCommentCount();
	}

	protected WikiPageModel newWikiPageModel(
		WikiNodeModel wikiNodeModel, int index, long pageId,
		long resourcePrimKey, long companyId, long userId, String userName) {

		WikiPageModel wikiPageModel = new WikiPageModelImpl();

		wikiPageModel.setUuid(SequentialUUID.generate());
		wikiPageModel.setPageId(pageId);
		wikiPageModel.setResourcePrimKey(resourcePrimKey);
		wikiPageModel.setGroupId(wikiNodeModel.getGroupId());
		wikiPageModel.setCompanyId(companyId);
		wikiPageModel.setUserId(userId);
		wikiPageModel.setUserName(userName);
		wikiPageModel.setCreateDate(new Date());
		wikiPageModel.setModifiedDate(new Date());
		wikiPageModel.setNodeId(wikiNodeModel.getNodeId());
		wikiPageModel.setTitle("Test Page " + index);
		wikiPageModel.setVersion(WikiPageConstants.VERSION_DEFAULT);
		wikiPageModel.setContent("This is test page " + index + ".");
		wikiPageModel.setFormat("creole");
		wikiPageModel.setHead(true);
		wikiPageModel.setLastPublishDate(new Date());

		return wikiPageModel;
	}

	protected WikiNodeModel newWikiNodeModel(
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

	private InitContext _initContext;
}