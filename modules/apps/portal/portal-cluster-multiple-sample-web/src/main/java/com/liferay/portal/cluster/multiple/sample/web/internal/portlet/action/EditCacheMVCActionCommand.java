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

package com.liferay.portal.cluster.multiple.sample.web.internal.portlet.action;

import com.liferay.portal.cluster.multiple.sample.web.internal.constants.ClusterPortletConstants;
import com.liferay.portal.cluster.multiple.sample.web.internal.constants.ClusterPortletKeys;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Janis Zhang
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ClusterPortletKeys.CLUSTER_SAMPLE_CACHE_REPLICATION,
		"mvc.command.name=/cluster_sample_cache_replication/edit_cache"
	},
	service = MVCActionCommand.class
)
public class EditCacheMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	protected void activate() {
		_portalCache = (PortalCache<String, String>)_multiVMPool.getPortalCache(
			ClusterPortletConstants.PORTAL_CACHE_NAME);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(
			actionRequest, ClusterPortletConstants.CMD);

		if (cmd.equals(ClusterPortletConstants.PUT)) {
			_putCache(actionRequest);
		}
		else if (cmd.equals(ClusterPortletConstants.REMOVE)) {
			_removeCache(actionRequest);
		}
		else if (cmd.equals(ClusterPortletConstants.REMOVE_ALL)) {
			_removeAllCache();
		}
	}

	private void _putCache(ActionRequest actionRequest) {
		_portalCache.put(
			ParamUtil.getString(actionRequest, "key"),
			ParamUtil.getString(actionRequest, "value"));
	}

	private void _removeAllCache() {
		_portalCache.removeAll();
	}

	private void _removeCache(ActionRequest actionRequest) {
		String cacheKey = ParamUtil.getString(actionRequest, "curKey");

		_portalCache.remove(cacheKey);
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private volatile PortalCache<String, String> _portalCache;

}