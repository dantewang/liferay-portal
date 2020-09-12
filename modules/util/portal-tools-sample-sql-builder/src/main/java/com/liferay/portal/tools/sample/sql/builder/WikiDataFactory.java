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

import com.liferay.petra.string.StringBundler;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.model.WikiNodeModel;
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

	public static WikiDataFactory getInstance() {
		return _wikiDataFactory;
	}

	public int getMaxWikiPageCommentCount() {
		return BenchmarksPropsValues.MAX_WIKI_PAGE_COMMENT_COUNT;
	}

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		List<WikiNodeModel> wikiNodeModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_WIKI_NODE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_WIKI_NODE_COUNT; i++) {
			wikiNodeModels.add(_newWikiNodeModel(groupId, i));
		}

		return wikiNodeModels;
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel) {
		List<WikiPageModel> wikiPageModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_WIKI_PAGE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_WIKI_PAGE_COUNT; i++) {
			wikiPageModels.add(_newWikiPageModel(wikiNodeModel, i));
		}

		return wikiPageModels;
	}

	public WikiPageResourceModel newWikiPageResourceModel(
		WikiPageModel wikiPageModel) {

		WikiPageResourceModel wikiPageResourceModel =
			new WikiPageResourceModelImpl();

		// UUID

		wikiPageResourceModel.setUuid(SequentialUUID.generate());

		// PK fields

		wikiPageResourceModel.setResourcePrimKey(
			wikiPageModel.getResourcePrimKey());

		// Other fields

		wikiPageResourceModel.setNodeId(wikiPageModel.getNodeId());
		wikiPageResourceModel.setTitle(wikiPageModel.getTitle());

		return wikiPageResourceModel;
	}

	private WikiDataFactory() {
	}

	private WikiNodeModel _newWikiNodeModel(long groupId, int index) {
		WikiNodeModel wikiNodeModel = new WikiNodeModelImpl();

		// UUID

		wikiNodeModel.setUuid(SequentialUUID.generate());

		// PK fields

		wikiNodeModel.setNodeId(counter.get());

		// Group instance

		wikiNodeModel.setGroupId(groupId);

		// Audit fields

		wikiNodeModel.setCompanyId(COMPANY_ID);
		wikiNodeModel.setUserId(SAMPLE_USER_ID);
		wikiNodeModel.setUserName(SAMPLE_USER_NAME);
		wikiNodeModel.setCreateDate(new Date());
		wikiNodeModel.setModifiedDate(new Date());

		// Other fields

		wikiNodeModel.setName("Test Node " + index);
		wikiNodeModel.setLastPostDate(new Date());
		wikiNodeModel.setLastPublishDate(new Date());
		wikiNodeModel.setStatusDate(new Date());

		return wikiNodeModel;
	}

	private WikiPageModel _newWikiPageModel(
		WikiNodeModel wikiNodeModel, int index) {

		WikiPageModel wikiPageModel = new WikiPageModelImpl();

		// UUID

		wikiPageModel.setUuid(SequentialUUID.generate());

		// PK fields

		wikiPageModel.setPageId(counter.get());

		// Resource

		wikiPageModel.setResourcePrimKey(counter.get());

		// Group instance

		wikiPageModel.setGroupId(wikiNodeModel.getGroupId());

		// Audit fields

		wikiPageModel.setCompanyId(COMPANY_ID);
		wikiPageModel.setUserId(SAMPLE_USER_ID);
		wikiPageModel.setUserName(SAMPLE_USER_NAME);
		wikiPageModel.setCreateDate(new Date());
		wikiPageModel.setModifiedDate(new Date());

		// Other fields

		wikiPageModel.setNodeId(wikiNodeModel.getNodeId());
		wikiPageModel.setTitle("Test Page " + index);
		wikiPageModel.setVersion(WikiPageConstants.VERSION_DEFAULT);
		wikiPageModel.setContent(
			StringBundler.concat(
				"This is Test Page ", index, " of ", wikiNodeModel.getName(),
				"."));
		wikiPageModel.setFormat("creole");
		wikiPageModel.setHead(true);
		wikiPageModel.setLastPublishDate(new Date());

		return wikiPageModel;
	}

	private static WikiDataFactory _wikiDataFactory = new WikiDataFactory();

}