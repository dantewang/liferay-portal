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
import com.liferay.portal.kernel.util.Props;

import java.io.Serializable;

import javax.management.MBeanServer;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Xiangyue Cai
 */
@Component(
	immediate = true,
	property = PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.SINGLE_VM,
	service = PortalCacheManager.class
)
public class SingleVMCache2kPortalCacheManager<K extends Serializable, V>
	extends Cache2kPortalCacheManager<K, V> {

	@Activate
	protected void activate(BundleContext bundleContext) {
		setPortalCacheManagerName(PortalCacheManagerNames.SINGLE_VM);

		initialize();
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	@Reference(unbind = "-")
	protected void setMBeanServer(MBeanServer mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	@Reference(unbind = "-")
	protected void setPortalCacheListenerFactory(
			PortalCacheListenerFactory portalCacheListenerFactory) {

	}

	@Reference(unbind = "-")
	protected void setPortalCacheBootstrapLoaderFactory(
			PortalCacheBootstrapLoaderFactory
					portalCacheBootstrapLoaderFactory) {
		this.portalCacheBootstrapLoaderFactory =
				portalCacheBootstrapLoaderFactory;
	}

	@Reference(unbind = "-")
	protected void setPortalCacheManagerListenerFactory(
			PortalCacheManagerListenerFactory<PortalCacheManager<K, V>>
					portalCacheManagerListenerFactory) {

	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

}