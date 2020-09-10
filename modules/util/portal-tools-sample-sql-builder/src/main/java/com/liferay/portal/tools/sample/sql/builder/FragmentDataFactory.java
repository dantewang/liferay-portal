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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;

/**
 * @author Lily Chi
 */
public class FragmentDataFactory extends BaseDataFactory {

	public static FragmentDataFactory getInstance() {
		return _fragmentDataFactory;
	}

	public FragmentCollectionModel newFragmentCollectionModel(long groupId) {
		FragmentCollectionModel fragmentCollectionModel =
			new FragmentCollectionModelImpl();

		// UUID

		fragmentCollectionModel.setUuid(SequentialUUID.generate());

		// PK fields

		fragmentCollectionModel.setFragmentCollectionId(counter.get());

		// Group instance

		fragmentCollectionModel.setGroupId(groupId);

		// Audit fields

		fragmentCollectionModel.setCompanyId(COMPANY_ID);
		fragmentCollectionModel.setUserId(SAMPLE_USER_ID);
		fragmentCollectionModel.setCreateDate(new Date());
		fragmentCollectionModel.setModifiedDate(new Date());

		// Other fields

		fragmentCollectionModel.setFragmentCollectionKey("fragmentcollection");
		fragmentCollectionModel.setName("fragmentcollection");

		return fragmentCollectionModel;
	}

	public FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, FragmentEntryModel fragmentEntryModel,
		long classNameId) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		// UUID

		fragmentEntryLinkModel.setUuid(SequentialUUID.generate());

		// PK fields

		fragmentEntryLinkModel.setFragmentEntryLinkId(counter.get());

		// Group instance

		fragmentEntryLinkModel.setGroupId(fragmentEntryModel.getGroupId());

		// Audit fields

		fragmentEntryLinkModel.setCompanyId(COMPANY_ID);
		fragmentEntryLinkModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryLinkModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());

		// Other fields

		fragmentEntryLinkModel.setFragmentEntryId(
			fragmentEntryModel.getFragmentEntryId());
		fragmentEntryLinkModel.setClassNameId(classNameId);
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setCss(fragmentEntryModel.getCss());
		fragmentEntryLinkModel.setHtml(fragmentEntryModel.getHtml());
		fragmentEntryLinkModel.setJs(fragmentEntryModel.getJs());
		fragmentEntryLinkModel.setEditableValues(StringPool.BLANK);
		fragmentEntryLinkModel.setNamespace(StringUtil.randomId());
		fragmentEntryLinkModel.setPosition(0);

		return fragmentEntryLinkModel;
	}

	public FragmentEntryModel newFragmentEntryModel(
			long groupId, FragmentCollectionModel fragmentCollectionModel)
		throws Exception {

		FragmentEntryModel fragmentEntryModel = new FragmentEntryModelImpl();

		// UUID

		fragmentEntryModel.setUuid(SequentialUUID.generate());

		// PK fields

		fragmentEntryModel.setFragmentEntryId(counter.get());

		// Group instance

		fragmentEntryModel.setGroupId(groupId);

		// Audit fields

		fragmentEntryModel.setCompanyId(COMPANY_ID);
		fragmentEntryModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryModel.setCreateDate(new Date());
		fragmentEntryModel.setModifiedDate(new Date());

		// Other fields

		fragmentEntryModel.setFragmentCollectionId(
			fragmentCollectionModel.getFragmentCollectionId());
		fragmentEntryModel.setFragmentEntryKey("web_content");
		fragmentEntryModel.setName("web_content");
		fragmentEntryModel.setCss(StringPool.BLANK);
		fragmentEntryModel.setHtml(readFile("web_content.html"));
		fragmentEntryModel.setJs(StringPool.BLANK);
		fragmentEntryModel.setType(FragmentConstants.TYPE_COMPONENT);
		fragmentEntryModel.setStatus(WorkflowConstants.STATUS_APPROVED);

		// Autogenerated fields

		fragmentEntryModel.setHeadId(counter.get());

		return fragmentEntryModel;
	}

	public LayoutPageTemplateStructureModel newLayoutPageTemplateStructureModel(
		LayoutModel layoutModel, long classNameId) {

		LayoutPageTemplateStructureModel layoutPageTemplateStructureModel =
			new LayoutPageTemplateStructureModelImpl();

		// UUID

		layoutPageTemplateStructureModel.setUuid(SequentialUUID.generate());

		// PK fields

		layoutPageTemplateStructureModel.setLayoutPageTemplateStructureId(
			counter.get());

		// Group instance

		layoutPageTemplateStructureModel.setGroupId(layoutModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureModel.setCreateDate(new Date());
		layoutPageTemplateStructureModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureModel.setClassNameId(classNameId);
		layoutPageTemplateStructureModel.setClassPK(layoutModel.getPlid());

		return layoutPageTemplateStructureModel;
	}

	public LayoutPageTemplateStructureRelModel
			newLayoutPageTemplateStructureRelModel(
				LayoutModel layoutModel,
				LayoutPageTemplateStructureModel
					layoutPageTemplateStructureModel,
				FragmentEntryLinkModel fragmentEntryLinkModel)
		throws Exception {

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		// UUID

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());

		// PK fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			counter.get());

		// Group instance

		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());

		// Audit fields

		layoutPageTemplateStructureRelModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureRelModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureRelModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());

		// Other fields

		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());
		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(0L);
		layoutPageTemplateStructureRelModel.setData(
			StringUtil.replace(
				_layoutPageTemplateStructureRelData, "${fragmentEntryLinkId}",
				String.valueOf(
					fragmentEntryLinkModel.getFragmentEntryLinkId())));

		return layoutPageTemplateStructureRelModel;
	}

	private FragmentDataFactory() {
		_layoutPageTemplateStructureRelData = readFile(
			"layout_page_template_structure_rel_data.json");
	}

	private static FragmentDataFactory _fragmentDataFactory =
		new FragmentDataFactory();

	private final String _layoutPageTemplateStructureRelData;

}