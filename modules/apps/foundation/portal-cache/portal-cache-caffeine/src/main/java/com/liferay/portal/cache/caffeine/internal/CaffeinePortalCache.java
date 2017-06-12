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

import com.liferay.portal.cache.BasePortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Leon Chi
 */
public class CaffeinePortalCache<K extends Serializable, V>
	extends BasePortalCache<K, V> {

	public CaffeinePortalCache(
		PortalCacheManager<K, V> portalCacheManager, Cache<K, V> cache,
		String cacheName) {

		super(portalCacheManager);

		this.cache = cache;

		_cacheName = cacheName;
	}

	@Override
	public List<K> getKeys() {
		ConcurrentMap<K, V> concurrentMap = cache.asMap();

		Set<K> keySet = concurrentMap.keySet();

		List<K> keyList = new ArrayList<>();

		keyList.addAll(keySet);

		return keyList;
	}

	@Override
	public String getPortalCacheName() {
		return _cacheName;
	}

	@Override
	public void removeAll() {
		cache.invalidateAll();
	}

	@Override
	protected V doGet(K key) {
		return (V)cache.getIfPresent(key);
	}

	@Override
	protected void doPut(K key, V value, int timeToLive) {
		cache.put(key, value);
	}

	@Override
	protected V doPutIfAbsent(K key, V value, int timeToLive) {
		V oldValue = cache.getIfPresent(key);

		if (oldValue != null) {
			return oldValue;
		}
		else {
			cache.put(key, value);

			return value;
		}
	}

	@Override
	protected void doRemove(K key) {
		cache.invalidate(key);
	}

	@Override
	protected boolean doRemove(K key, V value) {
		V currentValue = cache.getIfPresent(key);

		if (currentValue == null) {
			return false;
		}

		if (currentValue.equals(value)) {
			cache.invalidate(key);

			return true;
		}

		return false;
	}

	@Override
	protected V doReplace(K key, V value, int timeToLive) {
		V oldValue = cache.getIfPresent(key);

		cache.put(key, value);

		return oldValue;
	}

	@Override
	protected boolean doReplace(K key, V oldValue, V newValue, int timeToLive) {
		V currentValue = cache.getIfPresent(key);

		if (currentValue == null) {
			return false;
		}

		if (currentValue.equals(oldValue)) {
			cache.put(key, newValue);

			return true;
		}

		return false;
	}

	protected volatile Cache<K, V> cache;

	private final String _cacheName;

}