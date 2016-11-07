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
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
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

	public PortletPreferenceDataFactory(InitContext initContext) {
		_initContext = initContext;
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, BlogsPortletKeys.BLOGS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, WikiPortletKeys.WIKI,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public List<PortletPreferencesModel>
		newDDLPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public List<PortletPreferencesModel>
		newJournalPortletPreferencesModels(long plid) {

		return Collections.singletonList(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		int size = (int)groupId - 1;

		if (currentIndex == 1) {
			return newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		Map<Long, SimpleCounter> assetPublisherQueryCounter =
			_initContext.getAssetPublisherQueryCounter();

		SimpleCounter counter = assetPublisherQueryCounter.get(groupId);

		if (counter == null) {
			counter = new SimpleCounter(0);

			assetPublisherQueryCounter.put(groupId, counter);
		}

		String[] assetPublisherQueryValues = null;

		if (_initContext.getAssetPublisherQueryName().equals(
				"assetCategories")) {

			List<AssetCategoryModel> assetCategoryModels =
				_initContext.getAssetCategoryModelsArray()[size];

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			int maxAssetEntryToAssetCategoryCount =
				_initContext.getMaxAssetEntryToAssetCategoryCount();

			assetPublisherQueryValues =
				getAssetPublisherAssetCategoriesQueryValues(
					assetCategoryModels, (int)counter.get(),
					maxAssetEntryToAssetCategoryCount);
		}
		else {
			List<AssetTagModel> assetTagModels =
				_initContext.getAssetTagModelsArray()[size];

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			assetPublisherQueryValues = getAssetPublisherAssetTagsQueryValues(
				assetTagModels, (int)counter.get(),
				_initContext.getMaxAssetEntryToAssetTagCount());
		}

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)_initContext.
				getDefaultAssetPublisherPortletPreference().clone();
		PortletPreferencesFactory portletPreferencesFactory =
			_initContext.getPortletPreferencesFactory();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue(
			"queryName0", _initContext.getAssetPublisherQueryName());
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue(
			"queryName1", _initContext.getAssetPublisherQueryName());
		jxPortletPreferences.setValue(
			"queryValues1", assetPublisherQueryValues[3]);

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();
		PortletPreferencesFactory portletPreferencesFactory =
			_initContext.getPortletPreferencesFactory();

		jxPortletPreferences.setValue("editable", "true");
		jxPortletPreferences.setValue(
			"recordSetId", String.valueOf(ddlRecordSetModel.getRecordSetId()));
		jxPortletPreferences.setValue("spreadsheet", "false");

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
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

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	protected String[] getAssetPublisherAssetCategoriesQueryValues(
		List<AssetCategoryModel> assetCategoryModels, int index,
		int maxAssetEntryToAssetCategoryCount) {

		AssetCategoryModel assetCategoryModel0 = assetCategoryModels.get(
			index % assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel1 = assetCategoryModels.get(
			(index + maxAssetEntryToAssetCategoryCount) %
				assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel2 = assetCategoryModels.get(
			(index + maxAssetEntryToAssetCategoryCount * 2) %
				assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel3 = assetCategoryModels.get(
			(index + maxAssetEntryToAssetCategoryCount * 3) %
				assetCategoryModels.size());

		return new String[] {
			String.valueOf(assetCategoryModel0.getCategoryId()),
			String.valueOf(assetCategoryModel1.getCategoryId()),
			String.valueOf(assetCategoryModel2.getCategoryId()),
			String.valueOf(assetCategoryModel3.getCategoryId())
		};
	}

	protected String[] getAssetPublisherAssetTagsQueryValues(
		List<AssetTagModel> assetTagModels, int index,
		int maxAssetEntryToAssetTagCount) {

		AssetTagModel assetTagModel0 = assetTagModels.get(
			index % assetTagModels.size());
		AssetTagModel assetTagModel1 = assetTagModels.get(
			(index + maxAssetEntryToAssetTagCount) % assetTagModels.size());
		AssetTagModel assetTagModel2 = assetTagModels.get(
			(index + maxAssetEntryToAssetTagCount * 2) % assetTagModels.size());
		AssetTagModel assetTagModel3 = assetTagModels.get(
			(index + maxAssetEntryToAssetTagCount * 3) % assetTagModels.size());

		return new String[] {
			assetTagModel0.getName(), assetTagModel1.getName(),
			assetTagModel2.getName(), assetTagModel3.getName()
		};
	}

	protected PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId, String preferences) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		portletPreferencesModel.setPortletPreferencesId(counter.get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	private final InitContext _initContext;

}