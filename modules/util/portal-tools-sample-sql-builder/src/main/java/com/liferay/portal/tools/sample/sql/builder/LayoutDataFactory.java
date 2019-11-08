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
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.hello.world.web.internal.constants.HelloWorldPortletKeys;
import com.liferay.login.web.internal.constants.LoginPortletKeys;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
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

	public LayoutDataFactory(InitContext initContext) {
		super(initContext);
	}

	public long getLayoutClassNameId() {
		return getClassNameId(Layout.class, initContext.getClassNameModels());
	}

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		LayoutFriendlyURLModel layoutFriendlyURLEntryModel =
			new LayoutFriendlyURLModelImpl();

		SimpleCounter counter = initContext.getCounter();

		layoutFriendlyURLEntryModel.setUuid(SequentialUUID.generate());
		layoutFriendlyURLEntryModel.setLayoutFriendlyURLId(counter.get());
		layoutFriendlyURLEntryModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLEntryModel.setCompanyId(initContext.getCompanyId());
		layoutFriendlyURLEntryModel.setUserId(initContext.getSampleUserId());
		layoutFriendlyURLEntryModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		layoutFriendlyURLEntryModel.setCreateDate(new Date());
		layoutFriendlyURLEntryModel.setModifiedDate(new Date());
		layoutFriendlyURLEntryModel.setPlid(layoutModel.getPlid());
		layoutFriendlyURLEntryModel.setFriendlyURL(
			layoutModel.getFriendlyURL());
		layoutFriendlyURLEntryModel.setLanguageId(
			DataFactoryConstants.LANGUAGE_ID);
		layoutFriendlyURLEntryModel.setLastPublishDate(new Date());

		return layoutFriendlyURLEntryModel;
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		SimpleCounter simpleCounter = _layoutCounters.get(groupId);

		SimpleCounter counter = initContext.getCounter();

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			_layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());

		long plid = counter.get();

		layoutModel.setPlid(plid);

		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(initContext.getCompanyId());
		layoutModel.setUserId(initContext.getSampleUserId());
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

	public List<LayoutSetModel> newLayoutSetModels(long groupId) {
		List<LayoutSetModel> layoutSetModels = new ArrayList<>(2);

		layoutSetModels.add(_newLayoutSetModel(groupId, true));
		layoutSetModels.add(_newLayoutSetModel(groupId, false));

		return layoutSetModels;
	}

	public List<LayoutModel> newPublicLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		layoutModels.add(
			newLayoutModel(
				groupId, DataFactoryConstants.HELLO_WORLD_LAYOUT_NAME,
				LoginPortletKeys.LOGIN + ",",
				HelloWorldPortletKeys.HELLO_WORLD + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, DataFactoryConstants.BLOG_LAYOUT_NAME, "",
				BlogsPortletKeys.BLOGS + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, DataFactoryConstants.COMMERCE_LAYOUT_NAME, "",
				CPPortletKeys.CP_CONTENT_WEB + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, DataFactoryConstants.DL_LAYOUT_NAME, "",
				DLPortletKeys.DOCUMENT_LIBRARY + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, DataFactoryConstants.FORUMS_LAYOUT_NAME, "",
				MBPortletKeys.MESSAGE_BOARDS + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, DataFactoryConstants.WIKI_LAYOUT_NAME, "",
				WikiPortletKeys.WIKI + ","));

		return layoutModels;
	}

	private LayoutSetModel _newLayoutSetModel(
		long groupId, boolean privateLayout) {

		LayoutSetModel layoutSetModel = new LayoutSetModelImpl();

		SimpleCounter counter = initContext.getCounter();

		long layoutSetId = counter.get();

		layoutSetModel.setLayoutSetId(layoutSetId);

		layoutSetModel.setGroupId(groupId);
		layoutSetModel.setCompanyId(initContext.getCompanyId());
		layoutSetModel.setCreateDate(new Date());
		layoutSetModel.setModifiedDate(new Date());
		layoutSetModel.setPrivateLayout(privateLayout);
		layoutSetModel.setThemeId(DataFactoryConstants.LAYOUT_THEME_ID);
		layoutSetModel.setColorSchemeId("01");

		return layoutSetModel;
	}

	private final Map<Long, SimpleCounter> _layoutCounters = new HashMap<>();

}