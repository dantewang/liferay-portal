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

import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.blogs.web.constants.BlogsPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPortletKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

/**
 * @author Lily Chi
 */
public class PortletPreferenceDataFactory {

	public static List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, BlogsPortletKeys.BLOGS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, WikiPortletKeys.WIKI,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public static List<PortletPreferencesModel>
		newDDLPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public static List<PortletPreferencesModel>
		newJournalPortletPreferencesModels(long plid) {

		return Collections.singletonList(
			InitDataFactoryUtil.newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public static PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		int size = (int)groupId - 1;

		if (currentIndex == 1) {
			return InitDataFactoryUtil.newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		Map<Long, SimpleCounter> assetPublisherQueryCounter =
			InitContextUtil.getAssetPublisherQueryCounter();

		SimpleCounter counter = assetPublisherQueryCounter.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			assetPublisherQueryCounter.put(groupId, counter);
		}

		String[] assetPublisherQueryValues = null;

		if (InitContextUtil.getAssetPublisherQueryName().equals(
				"assetCategories")) {

			List<AssetCategoryModel> assetCategoryModels =
				InitContextUtil.getAssetCategoryModelsArray()[size];

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return InitDataFactoryUtil.newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			int maxAssetEntryToAssetCategoryCount =
				InitContextUtil.getMaxAssetEntryToAssetCategoryCount();
			assetPublisherQueryValues =
				InitDataFactoryUtil.getAssetPublisherAssetCategoriesQueryValues(
					assetCategoryModels, (int)counter.get(),
					maxAssetEntryToAssetCategoryCount);
		}
		else {
			List<AssetTagModel> assetTagModels =
				InitContextUtil.getAssetTagModelsArray()[size];

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return InitDataFactoryUtil.newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			assetPublisherQueryValues =
				InitDataFactoryUtil.getAssetPublisherAssetTagsQueryValues(
					assetTagModels, (int)counter.get(),
					InitContextUtil.getMaxAssetEntryToAssetTagCount());
		}

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)InitContextUtil.
				getDefaultAssetPublisherPortletPreference().clone();
		PortletPreferencesFactory portletPreferencesFactory =
			InitContextUtil.getPortletPreferencesFactory();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue(
			"queryName0", InitContextUtil.getAssetPublisherQueryName());
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue(
			"queryName1", InitContextUtil.getAssetPublisherQueryName());
		jxPortletPreferences.setValue(
			"queryValues1", assetPublisherQueryValues[3]);

		return InitDataFactoryUtil.newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public static PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();
		PortletPreferencesFactory portletPreferencesFactory =
			InitContextUtil.getPortletPreferencesFactory();

		jxPortletPreferences.setValue("editable", "true");
		jxPortletPreferences.setValue(
			"recordSetId", String.valueOf(ddlRecordSetModel.getRecordSetId()));
		jxPortletPreferences.setValue("spreadsheet", "false");

		return InitDataFactoryUtil.newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public static PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel,
			PortletPreferencesFactory portletPreferencesFactory)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return InitDataFactoryUtil.newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

}