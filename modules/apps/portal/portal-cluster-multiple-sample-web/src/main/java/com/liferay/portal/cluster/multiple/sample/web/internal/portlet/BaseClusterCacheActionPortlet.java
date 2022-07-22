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

package com.liferay.portal.cluster.multiple.sample.web.internal.portlet;

import com.liferay.portal.cluster.multiple.sample.web.internal.constants.ClusterPortletKeys;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Janis Zhang
 */
public class BaseClusterCacheActionPortlet extends MVCPortlet {

	@ProcessAction(name = "/cache_put")
	public void clusterCachePut(
		ActionRequest request, ActionResponse response) {

		_portalCache.put(
			ParamUtil.getString(request, "key"),
			ParamUtil.getString(request, "value"));
	}

	@ProcessAction(name = "/cache_remove")
	public void clusterCacheRemove(
		ActionRequest request, ActionResponse response) {

		String cacheKey = ParamUtil.getString(request, "curKey");

		_portalCache.remove(cacheKey);
	}

	@ProcessAction(name = "/cache_removeAll")
	public void clusterCacheRemoveAll(
		ActionRequest request, ActionResponse response) {

		_portalCache.removeAll();
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			ClusterPortletKeys.PORTAL_CACHE_NAME, _portalCache);

		super.render(renderRequest, renderResponse);
	}

	private final PortalCache<String, String> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM,
			ClusterPortletKeys.PORTAL_CACHE_NAME);

}