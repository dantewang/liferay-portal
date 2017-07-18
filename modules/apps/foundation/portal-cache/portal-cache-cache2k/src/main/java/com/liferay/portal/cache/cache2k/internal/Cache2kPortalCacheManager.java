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

import com.liferay.portal.cache.BasePortalCacheManager;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.cache.PortalCache;

import java.io.Serializable;

import java.net.URL;

import javax.management.MBeanServer;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheManager;

/**
 * @author Leon Chi
 */
public class Cache2kPortalCacheManager<K extends Serializable, V>
	extends BasePortalCacheManager<K, V> {

	@Override
	public void reconfigurePortalCaches(URL configurationURL) {
		System.out.println("######################## reconfigurePortalCaches");
	}

	@Override
	protected PortalCache<K, V> createPortalCache(
		PortalCacheConfiguration portalCacheConfiguration) {

		String portalCacheName = portalCacheConfiguration.getPortalCacheName();

		synchronized (_cacheManager) {
			if (_cacheManager.getCache(portalCacheName) == null) {
				Cache2kBuilder cache2kBuilder =
					Cache2kBuilder.forUnknownTypes();

				cache2kBuilder = cache2kBuilder.manager(_cacheManager);
				cache2kBuilder = cache2kBuilder.name(portalCacheName);

				cache2kBuilder.build();
			}
		}

		Cache<K, V> cache = _cacheManager.getCache(portalCacheName);

		return new Cache2kPortalCache<>(this, cache);
	}

	@Override
	protected void doClearAll() {
		Iterable<Cache> caches = _cacheManager.getActiveCaches();

		for (Cache cache : caches) {
			cache.removeAll();
		}
	}

	@Override
	protected void doDestroy() {
		_cacheManager.close();
	}

	@Override
	protected void doRemovePortalCache(String portalCacheName) {
		if ((portalCacheName == null) || (portalCacheName.length() == 0)) {
			return;
		}

		Cache cache = _cacheManager.getCache(portalCacheName);

		if (cache == null) {
			return;
		}

		cache.close();
	}

	@Override
	protected PortalCacheManagerConfiguration
		getPortalCacheManagerConfiguration() {

		return _portalCacheManagerConfiguration;
	}

	@Override
	protected void initPortalCacheManager() {
		PortalCacheConfiguration defaultPortalCacheConfiguration =
			new PortalCacheConfiguration(
				PortalCacheConfiguration.DEFAULT_PORTAL_CACHE_NAME, null, null);

		_portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null, defaultPortalCacheConfiguration, null);

		_cacheManager = CacheManager.getInstance(getPortalCacheManagerName());
	}

	@Override
	protected void removeConfigurableEhcachePortalCacheListeners(
		PortalCache<K, V> portalCache) {
	}

	protected MBeanServer mBeanServer;

	private CacheManager _cacheManager;
	private PortalCacheManagerConfiguration _portalCacheManagerConfiguration;

}