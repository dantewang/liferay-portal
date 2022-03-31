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

package com.liferay.layout.taglib.servlet.taglib;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.taglib.util.IncludeTag;

import java.util.Date;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * @author Víctor Galán
 */
public class RenderFragmentLayoutTag extends IncludeTag {

	public long getGroupId() {
		return _groupId;
	}

	public String getMainItemId() {
		return _mainItemId;
	}

	public String getMode() {
		return _mode;
	}

	public long getPlid() {
		return _plid;
	}

	public boolean getShowPreview() {
		return _showPreview;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setMainItemId(String mainItemId) {
		_mainItemId = mainItemId;
	}

	public void setMode(String mode) {
		_mode = mode;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPlid(long plid) {
		_plid = plid;
	}

	public void setShowPreview(boolean showPreview) {
		_showPreview = showPreview;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_groupId = 0;
		_layoutStructure = null;
		_mainItemId = null;
		_mode = FragmentEntryLinkConstants.VIEW;
		_plid = 0;
		_showPreview = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		httpServletRequest.setAttribute(
			"liferay-layout:render-fragment-layout:layoutStructure",
			_getLayoutStructure(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-layout:render-fragment-layout:mainItemId", _mainItemId);
		httpServletRequest.setAttribute(
			"liferay-layout:render-fragment-layout:mode", _mode);
		httpServletRequest.setAttribute(
			"liferay-layout:render-fragment-layout:showPreview", _showPreview);
	}

	private String _getKey(
		long layoutPageTemplateStructureId, long segmentsExperienceId,
		Date modifiedDate) {

		StringBundler cacheKeyDSB = new StringBundler(5);

		cacheKeyDSB.append(layoutPageTemplateStructureId);
		cacheKeyDSB.append(StringPool.DASH);
		cacheKeyDSB.append(segmentsExperienceId);
		cacheKeyDSB.append(StringPool.DASH);
		cacheKeyDSB.append(modifiedDate.getTime());

		return cacheKeyDSB.toString();
	}

	private Layout _getLayout(HttpServletRequest httpServletRequest) {
		Layout layout = LayoutLocalServiceUtil.fetchLayout(
			_getPlid(httpServletRequest));

		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			layout = virtualLayout.getSourceLayout();
		}

		return layout;
	}

	private LayoutStructure _getLayoutStructure(
		HttpServletRequest httpServletRequest) {

		if (_layoutStructure != null) {
			return _layoutStructure;
		}

		try {
			Layout layout = _getLayout(httpServletRequest);

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				LayoutPageTemplateStructureLocalServiceUtil.
					fetchLayoutPageTemplateStructure(
						layout.getGroupId(), layout.getPlid(), true);

			long segmentsExperienceId = _getSegmentsExperienceId();

			String data = layoutPageTemplateStructure.getData(
				segmentsExperienceId);

			if (Validator.isNull(data)) {
				return _layoutStructure;
			}

			String masterLayoutData = null;

			LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(
						layout.getMasterLayoutPlid());

			LayoutPageTemplateStructure masterLayoutPageTemplateStructure =
				null;

			if (masterLayoutPageTemplateEntry != null) {
				masterLayoutPageTemplateStructure =
					LayoutPageTemplateStructureLocalServiceUtil.
						fetchLayoutPageTemplateStructure(
							masterLayoutPageTemplateEntry.getGroupId(),
							masterLayoutPageTemplateEntry.getPlid());

				if (masterLayoutPageTemplateStructure != null) {
					masterLayoutData =
						masterLayoutPageTemplateStructure.
							getDefaultSegmentsExperienceData();
				}
			}

			String dataKey = _getKey(
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperienceId,
				layoutPageTemplateStructure.getModifiedDate());

			if (Validator.isNull(masterLayoutData)) {
				return _getLayoutStructure(
					dataKey, () -> LayoutStructure.of(data));
			}

			String masterLayoutDataKey = _getKey(
				masterLayoutPageTemplateStructure.
					getLayoutPageTemplateStructureId(),
				_getMasterSegmentsExperienceId(
					masterLayoutPageTemplateEntry.getPlid()),
				masterLayoutPageTemplateStructure.getModifiedDate());

			String copyMasterLayoutData = masterLayoutData;

			return _getLayoutStructure(
				dataKey + StringPool.DASH + masterLayoutDataKey,
				() -> _mergeLayoutStructure(data, copyMasterLayoutData));
		}
		catch (Exception exception) {
			_log.error("Unable to get layout structure", exception);

			return null;
		}
	}

	private LayoutStructure _getLayoutStructure(
		String key, Supplier<LayoutStructure> layoutStructureSupplier) {

		PortalCache<String, LayoutStructure> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.MULTI_VM,
				RenderFragmentLayoutTag.class.getName());

		LayoutStructure layoutStructure = portalCache.get(key);

		if (layoutStructure != null) {
			_layoutStructure = layoutStructure;
		}
		else {
			_layoutStructure = layoutStructureSupplier.get();

			portalCache.put(key, _layoutStructure);
		}

		return _layoutStructure;
	}

	private long _getMasterSegmentsExperienceId(long plid) {
		return SegmentsExperienceLocalServiceUtil.
			fetchDefaultSegmentsExperienceId(plid);
	}

	private long _getPlid(HttpServletRequest httpServletRequest) {
		long plid = getPlid();

		if (plid > 0) {
			return plid;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getPlid();
	}

	private long _getSegmentsExperienceId() {
		HttpServletRequest httpServletRequest = getRequest();

		long selectedSegmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId", -1);

		if (selectedSegmentsExperienceId != -1) {
			return selectedSegmentsExperienceId;
		}

		SegmentsExperienceManager segmentsExperienceManager =
			new SegmentsExperienceManager(
				ServletContextUtil.getSegmentsExperienceLocalService());

		return segmentsExperienceManager.getSegmentsExperienceId(
			httpServletRequest);
	}

	private LayoutStructure _mergeLayoutStructure(
		String data, String masterLayoutData) {

		LayoutStructure masterLayoutStructure = LayoutStructure.of(
			masterLayoutData);

		LayoutStructure layoutStructure = LayoutStructure.of(data);

		for (LayoutStructureItem layoutStructureItem :
				layoutStructure.getLayoutStructureItems()) {

			masterLayoutStructure.addLayoutStructureItem(layoutStructureItem);
		}

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)
				masterLayoutStructure.getDropZoneLayoutStructureItem();

		dropZoneLayoutStructureItem.addChildrenItem(
			layoutStructure.getMainItemId());

		LayoutStructureItem rootStructureItem =
			masterLayoutStructure.getLayoutStructureItem(
				layoutStructure.getMainItemId());

		rootStructureItem.setParentItemId(
			dropZoneLayoutStructureItem.getItemId());

		return masterLayoutStructure;
	}

	private static final String _PAGE = "/render_fragment_layout/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		RenderFragmentLayoutTag.class);

	private long _groupId;
	private LayoutStructure _layoutStructure;
	private String _mainItemId;
	private String _mode = FragmentEntryLinkConstants.VIEW;
	private long _plid;
	private boolean _showPreview;

}