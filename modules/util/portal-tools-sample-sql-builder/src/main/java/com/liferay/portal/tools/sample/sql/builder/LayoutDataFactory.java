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

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.hello.world.web.internal.constants.HelloWorldPortletKeys;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.LayoutSetModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPortletKeys;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class LayoutDataFactory extends BaseDataFactory {

	public LayoutModel newContentLayoutModel(
		long groupId, String name, String fragmentEntries) {

		SimpleCounter simpleCounter = _layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			_layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());
		layoutModel.setPlid(counter.get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(COMPANY_ID);
		layoutModel.setUserId(SAMPLE_USER_ID);
		layoutModel.setUserName(SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());
		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_CONTENT);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsUnicodeProperties = new UnicodeProperties(
			true);

		typeSettingsUnicodeProperties.setProperty(
			"fragmentEntries", fragmentEntries);

		layoutModel.setTypeSettings(
			StringUtil.replace(
				typeSettingsUnicodeProperties.toString(), '\n', "\\n"));

		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	public List<LayoutModel> newContentLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		for (int i = 0; i < BenchmarksPropsValues.MAX_CONTENT_LAYOUT_COUNT;
			 i++) {

			layoutModels.add(
				newContentLayoutModel(
					groupId, i + "_web_content", "web_content"));
		}

		return layoutModels;
	}

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		LayoutFriendlyURLModel layoutFriendlyURLEntryModel =
			new LayoutFriendlyURLModelImpl();

		if (layoutModel.getCtCollectionId() != 0) {
			layoutFriendlyURLEntryModel.setCtCollectionId(
				layoutModel.getCtCollectionId());
		}

		layoutFriendlyURLEntryModel.setUuid(SequentialUUID.generate());
		layoutFriendlyURLEntryModel.setLayoutFriendlyURLId(counter.get());
		layoutFriendlyURLEntryModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLEntryModel.setCompanyId(COMPANY_ID);
		layoutFriendlyURLEntryModel.setUserId(SAMPLE_USER_ID);
		layoutFriendlyURLEntryModel.setUserName(SAMPLE_USER_NAME);
		layoutFriendlyURLEntryModel.setCreateDate(new Date());
		layoutFriendlyURLEntryModel.setModifiedDate(new Date());
		layoutFriendlyURLEntryModel.setPlid(layoutModel.getPlid());
		layoutFriendlyURLEntryModel.setFriendlyURL(
			layoutModel.getFriendlyURL());
		layoutFriendlyURLEntryModel.setLanguageId("en_US");
		layoutFriendlyURLEntryModel.setLastPublishDate(new Date());

		return layoutFriendlyURLEntryModel;
	}

	public List<LayoutFriendlyURLModel> newLayoutFriendlyURLModels(
		List<LayoutModel> layoutModels) {

		List<LayoutFriendlyURLModel> layoutFriendlyURLModels = new ArrayList<>(
			layoutModels.size());

		layoutModels.forEach(
			layoutModel -> layoutFriendlyURLModels.add(
				newLayoutFriendlyURLModel(layoutModel)));

		return layoutFriendlyURLModels;
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		return newLayoutModel(
			groupId, name, column1, column2, SAMPLE_USER_ID, SAMPLE_USER_NAME,
			0);
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2, long userId,
		String userName, long ctCollectionId) {

		SimpleCounter simpleCounter = _layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			_layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());
		layoutModel.setPlid(counter.get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(COMPANY_ID);
		layoutModel.setCtCollectionId(ctCollectionId);
		layoutModel.setUserId(userId);
		layoutModel.setUserName(userName);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());
		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_PORTLET);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsUnicodeProperties = new UnicodeProperties(
			true);

		typeSettingsUnicodeProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "2_columns_ii");
		typeSettingsUnicodeProperties.setProperty("column-1", column1);
		typeSettingsUnicodeProperties.setProperty("column-2", column2);

		layoutModel.setTypeSettings(
			StringUtil.replace(
				typeSettingsUnicodeProperties.toString(), '\n', "\\n"));

		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	public List<LayoutModel> newLayoutModels(
		long groupId, CTCollectionModel cTCollectionModel) {

		List<LayoutModel> layoutModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_PAGE_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_PAGE_COUNT; i++) {
			String name = groupId + "_journal_article_" + (i + 1);

			String column2 = getJournalArticleLayoutColumn(
				i + 1, BenchmarksPropsValues.MAX_CT_WEBCONTENT_DISPLAY_COUNT);

			layoutModels.add(
				newLayoutModel(
					groupId, name, "", column2, cTCollectionModel.getUserId(),
					"", cTCollectionModel.getCtCollectionId()));
		}

		return layoutModels;
	}

	public List<LayoutSetModel> newLayoutSetModels(long groupId) {
		List<LayoutSetModel> layoutSetModels = new ArrayList<>(2);

		layoutSetModels.add(newLayoutSetModel(groupId, true));
		layoutSetModels.add(newLayoutSetModel(groupId, false));

		return layoutSetModels;
	}

	public List<LayoutModel> newPublicLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		layoutModels.add(
			newLayoutModel(
				groupId, "welcome", LoginPortletKeys.LOGIN + ",",
				HelloWorldPortletKeys.HELLO_WORLD + ","));
		layoutModels.add(
			newLayoutModel(groupId, "blogs", "", BlogsPortletKeys.BLOGS + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "commerce_product", "",
				CPPortletKeys.CP_CONTENT_WEB + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "document_library", "",
				DLPortletKeys.DOCUMENT_LIBRARY + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "forums", "", MBPortletKeys.MESSAGE_BOARDS + ","));
		layoutModels.add(
			newLayoutModel(groupId, "wiki", "", WikiPortletKeys.WIKI + ","));

		return layoutModels;
	}

	protected LayoutSetModel newLayoutSetModel(
		long groupId, boolean privateLayout) {

		LayoutSetModel layoutSetModel = new LayoutSetModelImpl();

		long layoutSetId = counter.get();

		layoutSetModel.setLayoutSetId(layoutSetId);

		layoutSetModel.setGroupId(groupId);
		layoutSetModel.setCompanyId(COMPANY_ID);
		layoutSetModel.setCreateDate(new Date());
		layoutSetModel.setModifiedDate(new Date());
		layoutSetModel.setPrivateLayout(privateLayout);
		layoutSetModel.setThemeId("classic_WAR_classictheme");
		layoutSetModel.setColorSchemeId("01");

		return layoutSetModel;
	}

	private final Map<Long, SimpleCounter> _layoutCounters = new HashMap<>();

}