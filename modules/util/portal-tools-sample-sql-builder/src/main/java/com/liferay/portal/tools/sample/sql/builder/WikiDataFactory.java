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
public class WikiDataFactory extends BaseDataFactory {

	public WikiDataFactory(InitContext initContext) {
		super(initContext);
	}

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		int maxWikiNodeCount = initContext.getMaxWikiNodeCount();

		List<WikiNodeModel> wikiNodeModels = new ArrayList<>(maxWikiNodeCount);

		for (int i = 1; i <= maxWikiNodeCount; i++) {
			wikiNodeModels.add(newWikiNodeModel(groupId, i));
		}

		return wikiNodeModels;
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel)
	{
		int maxWikiPageCount = initContext.getMaxWikiPageCount();

		List<WikiPageModel> wikiPageModels = new ArrayList<>(maxWikiPageCount);

		for (int i = 1; i <= maxWikiPageCount; i++) {
			wikiPageModels.add(newWikiPageModel(wikiNodeModel, i));
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
		return getClassNameId(WikiPage.class, initContext.getClassNameModels());
	}

	protected WikiPageModel newWikiPageModel(
		WikiNodeModel wikiNodeModel, int index) {

		WikiPageModel wikiPageModel = new WikiPageModelImpl();

		SimpleCounter counter = initContext.getCounter();

		wikiPageModel.setUuid(SequentialUUID.generate());
		wikiPageModel.setPageId(counter.get());
		wikiPageModel.setResourcePrimKey(counter.get());
		wikiPageModel.setGroupId(wikiNodeModel.getGroupId());
		wikiPageModel.setCompanyId(initContext.getCompanyId());
		wikiPageModel.setUserId(initContext.getSampleUserId());
		wikiPageModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		wikiPageModel.setCreateDate(new Date());
		wikiPageModel.setModifiedDate(new Date());
		wikiPageModel.setNodeId(wikiNodeModel.getNodeId());
		wikiPageModel.setTitle(
			DataFactoryConstants.WIKI_PAGE_TITLE_PREFIX + index);
		wikiPageModel.setVersion(WikiPageConstants.VERSION_DEFAULT);
		wikiPageModel.setContent(
			DataFactoryConstants.WIKI_PAGE_CONTENT_PREFIX + index + ".");
		wikiPageModel.setFormat(DataFactoryConstants.WIKI_PAGE_FORMAT);
		wikiPageModel.setHead(true);
		wikiPageModel.setLastPublishDate(new Date());

		return wikiPageModel;
	}

	protected WikiNodeModel newWikiNodeModel(long groupId, int index) {

		WikiNodeModel wikiNodeModel = new WikiNodeModelImpl();

		SimpleCounter counter = initContext.getCounter();

		wikiNodeModel.setUuid(SequentialUUID.generate());
		wikiNodeModel.setNodeId(counter.get());
		wikiNodeModel.setGroupId(groupId);
		wikiNodeModel.setCompanyId(initContext.getCompanyId());
		wikiNodeModel.setUserId(initContext.getSampleUserId());
		wikiNodeModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		wikiNodeModel.setCreateDate(new Date());
		wikiNodeModel.setModifiedDate(new Date());
		wikiNodeModel.setName(
			DataFactoryConstants.WIKI_NODE_NAME_PREFIX + index);
		wikiNodeModel.setLastPostDate(new Date());
		wikiNodeModel.setLastPublishDate(new Date());
		wikiNodeModel.setStatusDate(new Date());

		return wikiNodeModel;
	}

}