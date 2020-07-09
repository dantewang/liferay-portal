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

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollectionModel;
import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.fragment.model.FragmentEntryModel;
import com.liferay.fragment.model.impl.FragmentCollectionModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryModelImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureModel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelModel;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl;
import com.liferay.layout.util.template.LayoutData;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class FragmentDataFactory extends BaseDataFactory {

	public FragmentCollectionModel newFragmentCollectionModel(long groupId) {
		FragmentCollectionModel fragmentCollectionModel =
			new FragmentCollectionModelImpl();

		fragmentCollectionModel.setUuid(SequentialUUID.generate());
		fragmentCollectionModel.setFragmentCollectionId(counter.get());
		fragmentCollectionModel.setGroupId(groupId);
		fragmentCollectionModel.setCompanyId(COMPANY_ID);
		fragmentCollectionModel.setUserId(SAMPLE_USER_ID);
		fragmentCollectionModel.setCreateDate(new Date());
		fragmentCollectionModel.setModifiedDate(new Date());
		fragmentCollectionModel.setFragmentCollectionKey(
			DataFactoryConstants.FRAGMENT_COLLECTION_KEY);
		fragmentCollectionModel.setName(
			DataFactoryConstants.FRAGMENT_COLLECTION_KEY);

		return fragmentCollectionModel;
	}

	public FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, FragmentEntryModel fragmentEntryModel) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		fragmentEntryLinkModel.setUuid(SequentialUUID.generate());
		fragmentEntryLinkModel.setFragmentEntryLinkId(counter.get());
		fragmentEntryLinkModel.setGroupId(fragmentEntryModel.getGroupId());
		fragmentEntryLinkModel.setCompanyId(COMPANY_ID);
		fragmentEntryLinkModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryLinkModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());
		fragmentEntryLinkModel.setFragmentEntryId(
			fragmentEntryModel.getFragmentEntryId());
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setCss(fragmentEntryModel.getCss());
		fragmentEntryLinkModel.setJs(fragmentEntryModel.getJs());
		fragmentEntryLinkModel.setHtml(fragmentEntryModel.getHtml());
		fragmentEntryLinkModel.setEditableValues(StringPool.BLANK);
		fragmentEntryLinkModel.setNamespace(StringUtil.randomId());
		fragmentEntryLinkModel.setPosition(0);

		return fragmentEntryLinkModel;
	}

	public FragmentEntryModel newFragmentEntryModel(
			long groupId, FragmentCollectionModel fragmentCollectionModel)
		throws Exception {

		FragmentEntryModel fragmentEntryModel = new FragmentEntryModelImpl();

		fragmentEntryModel.setUuid(SequentialUUID.generate());
		fragmentEntryModel.setFragmentEntryId(counter.get());
		fragmentEntryModel.setGroupId(groupId);
		fragmentEntryModel.setCompanyId(COMPANY_ID);
		fragmentEntryModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		fragmentEntryModel.setCreateDate(new Date());
		fragmentEntryModel.setModifiedDate(new Date());
		fragmentEntryModel.setFragmentCollectionId(
			fragmentCollectionModel.getFragmentCollectionId());
		fragmentEntryModel.setFragmentEntryKey(
			DataFactoryConstants.FRAGMENT_ENTRY_KEY);
		fragmentEntryModel.setName(DataFactoryConstants.FRAGMENT_ENTRY_KEY);
		fragmentEntryModel.setCss(StringPool.BLANK);
		fragmentEntryModel.setHtml(
			readFile(DataFactoryConstants.FRAGMENT_HTML_FILE_NAME));
		fragmentEntryModel.setJs(StringPool.BLANK);
		fragmentEntryModel.setType(FragmentConstants.TYPE_COMPONENT);
		fragmentEntryModel.setStatus(WorkflowConstants.STATUS_APPROVED);

		return fragmentEntryModel;
	}

	public LayoutPageTemplateStructureModel newLayoutPageTemplateStructureModel(
		LayoutModel layoutModel) {

		LayoutPageTemplateStructureModel layoutPageTemplateStructureModel =
			new LayoutPageTemplateStructureModelImpl();

		layoutPageTemplateStructureModel.setUuid(SequentialUUID.generate());

		layoutPageTemplateStructureModel.setLayoutPageTemplateStructureId(
			counter.get());

		layoutPageTemplateStructureModel.setGroupId(layoutModel.getGroupId());
		layoutPageTemplateStructureModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		layoutPageTemplateStructureModel.setCreateDate(new Date());
		layoutPageTemplateStructureModel.setModifiedDate(new Date());
		layoutPageTemplateStructureModel.setClassNameId(
			getClassNameId(Layout.class));
		layoutPageTemplateStructureModel.setClassPK(layoutModel.getPlid());

		return layoutPageTemplateStructureModel;
	}

	public LayoutPageTemplateStructureRelModel
		newLayoutPageTemplateStructureRelModel(
			LayoutModel layoutModel,
			LayoutPageTemplateStructureModel layoutPageTemplateStructureModel,
			FragmentEntryLinkModel fragmentEntryLinkModel) {

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			counter.get());
		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());
		layoutPageTemplateStructureRelModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureRelModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureRelModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());
		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(0L);

		LayoutData layoutData = LayoutData.of(
			layoutModel.toEscapedModel(),
			layoutRow -> layoutRow.addLayoutColumns(
				layoutColumn -> {
					List<Long> fragmentEntryLinkIds =
						layoutColumn.getFragmentEntryLinkIds();

					fragmentEntryLinkIds.add(
						fragmentEntryLinkModel.getFragmentEntryLinkId());
				}));

		JSONObject jsonObject = layoutData.getLayoutDataJSONObject();

		layoutPageTemplateStructureRelModel.setData(jsonObject.toString());

		return layoutPageTemplateStructureRelModel;
	}

}