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
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPortletKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

/**
 * @author Lily Chi
 */
public class PortletPreferenceDataFactory extends BaseDataFactory {

	public PortletPreferenceDataFactory(InitContext initContext) {
		super(initContext);
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			_newPortletPreferencesModel(
				plid, BlogsPortletKeys.BLOGS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			_newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			_newPortletPreferencesModel(
				plid, WikiPortletKeys.WIKI,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public List<PortletPreferencesModel> newDDLPortletPreferencesModels(
		long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			_newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			_newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			_newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public List<PortletPreferencesModel> newJournalPortletPreferencesModels(
		long plid) {

		return Collections.singletonList(
			_newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		if (currentIndex == 1) {
			return _newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		Map<Long, List<AssetCategoryModel>>[] assetCategoryModelsMaps =
			_assetDataFactory.getAssetCategoryModelsMaps();

		Map<Long, List<AssetTagModel>>[] assetTagModelsMaps =
			_assetDataFactory.getAssetTagModelsMaps();

		String assetPublisherQueryName = "assetCategories";

		if ((currentIndex % 2) == 0) {
			assetPublisherQueryName = "assetTags";
		}

		ObjectValuePair<String[], Integer> objectValuePair = null;

		Integer startIndex = _assetPublisherQueryStartIndexes.get(groupId);

		if (startIndex == null) {
			startIndex = 0;
		}

		if (assetPublisherQueryName.equals("assetCategories")) {
			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				assetCategoryModelsMaps[(int)groupId - 1];

			List<AssetCategoryModel> assetCategoryModels =
				assetCategoryModelsMap.get(getNextAssetClassNameId(groupId));

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return _newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetCategoriesQueryValues(
				assetCategoryModels, startIndex);
		}
		else {
			Map<Long, List<AssetTagModel>> assetTagModelsMap =
				assetTagModelsMaps[(int)groupId - 1];

			List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
				getNextAssetClassNameId(groupId));

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return _newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetTagsQueryValues(
				assetTagModels, startIndex);
		}

		String[] assetPublisherQueryValues = objectValuePair.getKey();

		PortletPreferencesFactory portletPreferencesFactory =
			_assetDataFactory.getPortletPreferencesFactory();

		_assetPublisherQueryStartIndexes.put(
			groupId, objectValuePair.getValue());

		PortletPreferencesImpl defaultAssetPublisherPortletPreferencesImpl =
			_assetDataFactory.getDefaultAssetPublisherPortletPreferencesImpl();

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)
				defaultAssetPublisherPortletPreferencesImpl.clone();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue("queryName0", assetPublisherQueryName);
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue("queryName1", assetPublisherQueryName);
		jxPortletPreferences.setValue(
			"queryValues1", assetPublisherQueryValues[3]);

		return _newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		PortletPreferencesFactory portletPreferencesFactory =
			_assetDataFactory.getPortletPreferencesFactory();

		jxPortletPreferences.setValue("editable", "true");
		jxPortletPreferences.setValue(
			"recordSetId", String.valueOf(ddlRecordSetModel.getRecordSetId()));
		jxPortletPreferences.setValue("spreadsheet", "false");

		return _newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		PortletPreferencesFactory portletPreferencesFactory =
			_assetDataFactory.getPortletPreferencesFactory();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return _newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public void setAssetDataFactory(AssetDataFactory assetDataFactory) {
		_assetDataFactory = assetDataFactory;
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetCategoriesQueryValues(
			List<AssetCategoryModel> assetCategoryModels, int index) {

		int maxAssetEntryToAssetCategoryCount =
			initContext.getMaxAssetEntryToAssetCategoryCount();

		AssetCategoryModel assetCategoryModel0 = assetCategoryModels.get(
			index % assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel1 = assetCategoryModels.get(
			(index + maxAssetEntryToAssetCategoryCount) %
				assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel2 = assetCategoryModels.get(
			(index + maxAssetEntryToAssetCategoryCount * 2) %
				assetCategoryModels.size());

		int lastIndex =
			(index + maxAssetEntryToAssetCategoryCount * 3) %
				assetCategoryModels.size();

		AssetCategoryModel assetCategoryModel3 = assetCategoryModels.get(
			lastIndex);

		return new ObjectValuePair<>(
			new String[] {
				String.valueOf(assetCategoryModel0.getCategoryId()),
				String.valueOf(assetCategoryModel1.getCategoryId()),
				String.valueOf(assetCategoryModel2.getCategoryId()),
				String.valueOf(assetCategoryModel3.getCategoryId())
			},
			lastIndex + maxAssetEntryToAssetCategoryCount);
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetTagsQueryValues(
			List<AssetTagModel> assetTagModels, int index) {

		int maxAssetEntryToAssetTagCount =
			initContext.getMaxAssetEntryToAssetTagCount();

		AssetTagModel assetTagModel0 = assetTagModels.get(
			index % assetTagModels.size());
		AssetTagModel assetTagModel1 = assetTagModels.get(
			(index + maxAssetEntryToAssetTagCount) % assetTagModels.size());
		AssetTagModel assetTagModel2 = assetTagModels.get(
			(index + maxAssetEntryToAssetTagCount * 2) % assetTagModels.size());

		int lastIndex =
			(index + maxAssetEntryToAssetTagCount * 3) % assetTagModels.size();

		AssetTagModel assetTagModel3 = assetTagModels.get(lastIndex);

		return new ObjectValuePair<>(
			new String[] {
				assetTagModel0.getName(), assetTagModel1.getName(),
				assetTagModel2.getName(), assetTagModel3.getName()
			},
			lastIndex + maxAssetEntryToAssetTagCount);
	}

	protected long getNextAssetClassNameId(long groupId) {
		Integer index = _assetClassNameIdsIndexes.get(groupId);
		long[] assetClassNameIds = _assetDataFactory.getAssetClassNameIds();

		if (index == null) {
			index = 0;
		}

		long classNameId = assetClassNameIds[index % assetClassNameIds.length];

		_assetClassNameIdsIndexes.put(groupId, ++index);

		return classNameId;
	}

	private PortletPreferencesModel _newPortletPreferencesModel(
		long plid, String portletId, String preferences) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		SimpleCounter counter = initContext.getCounter();

		portletPreferencesModel.setCompanyId(initContext.getCompanyId());
		portletPreferencesModel.setPortletPreferencesId(counter.get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	private final Map<Long, Integer> _assetClassNameIdsIndexes =
		new HashMap<>();
	private AssetDataFactory _assetDataFactory;
	private final Map<Long, Integer> _assetPublisherQueryStartIndexes =
		new HashMap<>();

}