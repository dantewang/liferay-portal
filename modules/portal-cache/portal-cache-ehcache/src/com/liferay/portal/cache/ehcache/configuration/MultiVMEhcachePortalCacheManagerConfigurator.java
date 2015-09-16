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

package com.liferay.portal.cache.ehcache.configuration;

import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.Props;

import net.sf.ehcache.config.Configuration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(
	immediate = true,
	property = {
		PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM
	},
	service = PortalCacheManagerConfigurator.class
)
public class MultiVMEhcachePortalCacheManagerConfigurator
	extends AbstractEhcachePortalCacheManagerConfigurator {

	@Override
	public CacheManagerConfigurator<Configuration>
		getCacheManagerConfigurator() {

		return _cacheManagerConfigurator;
	}

	@Override
	public PortalCacheManagerConfiguration
		getPortalCacheManagerConfiguration() {

		return _portalCacheManagerConfiguration;
	}

	@Reference
	public void setCacheManagerConfigurator(
		CacheManagerConfigurator<Configuration> cacheManagerConfigurator) {

		_cacheManagerConfigurator = cacheManagerConfigurator;
	}

	@Activate
	protected void activate() {
		Configuration configuration =
			_cacheManagerConfigurator.getCacheManagerConfiguration();
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

	protected Props props;

	private CacheManagerConfigurator<Configuration> _cacheManagerConfigurator;
	private PortalCacheManagerConfiguration
		_portalCacheManagerConfiguration;

}