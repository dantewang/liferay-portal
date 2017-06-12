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

package com.liferay.portal.cache.caffeine.internal;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import com.liferay.portal.cache.BasePortalCacheManager;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.cache.PortalCache;

import java.io.Serializable;

import java.net.URL;

import java.util.concurrent.TimeUnit;

/**
 * @author Leon Chi
 */
public class CaffeinePortalCacheManager<K extends Serializable, V>
	extends BasePortalCacheManager<K, V> {

	@Override
	public void reconfigurePortalCaches(URL configurationURL) {
	}

	@Override
	protected PortalCache<K, V> createPortalCache(
		PortalCacheConfiguration portalCacheConfiguration) {

		Caffeine<Object, Object> caffeine = Caffeine.newBuilder();

		caffeine = caffeine.maximumSize(10000);

		caffeine = caffeine.expireAfterWrite(10, TimeUnit.MINUTES);

		Cache<K, V> cache = caffeine.build();

		PortalCache<K, V> portalCache = new CaffeinePortalCache<>(
			this, cache, portalCacheConfiguration.getPortalCacheName());

		return portalCache;
	}

	@Override
	protected void doClearAll() {
		for (String cacheName : portalCaches.keySet()) {
			PortalCache<K, V> portalCache = portalCaches.get(cacheName);

			if (portalCache != null) {
				portalCache.removeAll();
			}
		}
	}

	@Override
	protected void doDestroy() {
		portalCaches.clear();
	}

	@Override
	protected void doRemovePortalCache(String portalCacheName) {
		portalCaches.remove(portalCacheName);
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
	}

	@Override
	protected void removeConfigurableEhcachePortalCacheListeners(
		PortalCache<K, V> portalCache) {
	}

	private PortalCacheManagerConfiguration _portalCacheManagerConfiguration;

}