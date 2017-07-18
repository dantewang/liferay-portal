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

package com.liferay.portal.cache.cache2k.internal;

import com.liferay.portal.cache.PortalCacheBootstrapLoaderFactory;
import com.liferay.portal.cache.PortalCacheListenerFactory;
import com.liferay.portal.cache.PortalCacheManagerListenerFactory;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ClassLoaderUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leon Chi
 */
@Component(
	immediate = true,
	property = {
		PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM
	},
	service = PortalCacheManager.class
)
public class MultiVMCache2kPortalCacheManager
	<K extends Serializable, V extends Serializable>
		extends Cache2kPortalCacheManager<K, V> {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		setClusterAware(false);
		setPortalCacheManagerName(PortalCacheManagerNames.MULTI_VM);
		setMpiOnly(true);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		Class<?> clazz = getClass();

		ClassLoaderUtil.setContextClassLoader(
			AggregateClassLoader.getAggregateClassLoader(
				contextClassLoader, clazz.getClassLoader()));

		try {
			initialize();
		}
		finally {
			ClassLoaderUtil.setContextClassLoader(contextClassLoader);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Activated " + PortalCacheManagerNames.MULTI_VM);
		}
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	@Reference(unbind = "-")
	protected void setPortalCacheBootstrapLoaderFactory(
		PortalCacheBootstrapLoaderFactory portalCacheBootstrapLoaderFactory) {

		this.portalCacheBootstrapLoaderFactory =
			portalCacheBootstrapLoaderFactory;
	}

	@Reference(unbind = "-")
	protected void setPortalCacheListenerFactory(
		PortalCacheListenerFactory portalCacheListenerFactory) {

		this.portalCacheListenerFactory = portalCacheListenerFactory;
	}

	@Reference(unbind = "-")
	protected void setPortalCacheManagerListenerFactory(
		PortalCacheManagerListenerFactory<PortalCacheManager<K, V>>
			portalCacheManagerListenerFactory) {

		this.portalCacheManagerListenerFactory =
			portalCacheManagerListenerFactory;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MultiVMCache2kPortalCacheManager.class);

}