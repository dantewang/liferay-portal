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

import com.liferay.blogs.web.constants.BlogsPortletKeys;
import com.liferay.document.library.web.constants.DLPortletKeys;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.message.boards.web.constants.MBPortletKeys;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.LayoutSetModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPortletKeys;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class LayoutDataFactory {

	public static LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		LayoutFriendlyURLModel layoutFriendlyURLModel =
			new LayoutFriendlyURLModelImpl();

		layoutFriendlyURLModel.setUuid(SequentialUUID.generate());
		layoutFriendlyURLModel.setLayoutFriendlyURLId(
			InitContextUtil.getCounter().get());
		layoutFriendlyURLModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLModel.setCompanyId(InitContextUtil.getCompanyId());
		layoutFriendlyURLModel.setUserId(InitContextUtil.getSampleUserId());
		layoutFriendlyURLModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		layoutFriendlyURLModel.setCreateDate(new Date());
		layoutFriendlyURLModel.setModifiedDate(new Date());
		layoutFriendlyURLModel.setPlid(layoutModel.getPlid());
		layoutFriendlyURLModel.setFriendlyURL(layoutModel.getFriendlyURL());
		layoutFriendlyURLModel.setLanguageId("en_US");
		layoutFriendlyURLModel.setLastPublishDate(new Date());

		return layoutFriendlyURLModel;
	}

	public static LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		Map<Long, SimpleCounter> layoutCounters =
			InitContextUtil.getLayoutCounters();

		SimpleCounter simpleCounter = layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());
		layoutModel.setPlid(InitContextUtil.getCounter().get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(InitContextUtil.getCompanyId());
		layoutModel.setUserId(InitContextUtil.getSampleUserId());
		layoutModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());
		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_PORTLET);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsProperties = new UnicodeProperties(true);

		typeSettingsProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "2_columns_ii");
		typeSettingsProperties.setProperty("column-1", column1);
		typeSettingsProperties.setProperty("column-2", column2);

		String typeSettings = StringUtil.replace(
			typeSettingsProperties.toString(), '\n', "\\n");

		layoutModel.setTypeSettings(typeSettings);
		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	public static LayoutSetModel newLayoutSetModel(
		long groupId, boolean privateLayout, int pageCount, long layoutSetId,
		long companyId) {

		LayoutSetModel layoutSetModel = new LayoutSetModelImpl();

		layoutSetModel.setLayoutSetId(layoutSetId);
		layoutSetModel.setGroupId(groupId);
		layoutSetModel.setCompanyId(companyId);
		layoutSetModel.setCreateDate(new Date());
		layoutSetModel.setModifiedDate(new Date());
		layoutSetModel.setPrivateLayout(privateLayout);
		layoutSetModel.setThemeId("classic_WAR_classictheme");
		layoutSetModel.setColorSchemeId("01");
		layoutSetModel.setPageCount(pageCount);

		return layoutSetModel;
	}

	public static List<LayoutSetModel> newLayoutSetModels(
		long groupId, int publicLayoutSetPageCount) {

		List<LayoutSetModel> layoutSetModels = new ArrayList<>(2);

		layoutSetModels.add(
			newLayoutSetModel(
				groupId, true, 0, InitContextUtil.getCounter().get(),
				InitContextUtil.getCompanyId()));
		layoutSetModels.add(
			newLayoutSetModel(
				groupId, false, publicLayoutSetPageCount,
				InitContextUtil.getCounter().get(),
				InitContextUtil.getCompanyId()));

		return layoutSetModels;
	}

	public static List<LayoutModel> newPublicLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		layoutModels.add(
			newLayoutModel(
				groupId, "welcome", LoginPortletKeys.LOGIN + ",",
				"com_liferay_hello_world_web_portlet_HelloWorldPortlet,"));
		layoutModels.add(
			newLayoutModel(groupId, "blogs", "", BlogsPortletKeys.BLOGS + ","));
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

}