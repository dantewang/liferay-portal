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

import com.liferay.portal.cache.BasePortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.cache2k.Cache;

/**
 * @author Leon Chi
 */
public class Cache2kPortalCache<K extends Serializable, V>
	extends BasePortalCache<K, V> {

	public Cache2kPortalCache(
		PortalCacheManager<K, V> portalCacheManager, Cache<K, V> cache) {

		super(portalCacheManager);

		this.cache = cache;
	}

	@Override
	public List<K> getKeys() {
		ConcurrentMap<K, V> concurrentMap = cache.asMap();

		List<K> keys = new ArrayList<>(concurrentMap.keySet());

		return keys;
	}

	@Override
	public String getPortalCacheName() {
		return cache.getName();
	}

	@Override
	public void removeAll() {
		cache.removeAll();
	}

	@Override
	protected V doGet(K key) {
		return (V)cache.get(key);
	}

	@Override
	protected void doPut(K key, V value, int timeToLive) {
		cache.put(key, value);

		if (timeToLive != DEFAULT_TIME_TO_LIVE) {
			cache.expireAt(
				key, System.currentTimeMillis() + (long)timeToLive * 1000);
		}
	}

	@Override
	protected V doPutIfAbsent(K key, V value, int timeToLive) {
		if (cache.putIfAbsent(key, value)) {
			cache.expireAt(
				key, System.currentTimeMillis() + (long)timeToLive * 1000);

			return null;
		}

		return (V)cache.get(key);
	}

	@Override
	protected void doRemove(K key) {
		cache.remove(key);
	}

	@Override
	protected boolean doRemove(K key, V value) {
		return cache.removeIfEquals(key, value);
	}

	@Override
	protected V doReplace(K key, V value, int timeToLive) {
		if (cache.containsKey(key)) {
			V oldValue = (V)cache.get(key);

			cache.put(key, value);

			cache.expireAt(
				key, System.currentTimeMillis() + (long)timeToLive * 1000);

			return oldValue;
		}

		return null;
	}

	@Override
	protected boolean doReplace(K key, V oldValue, V newValue, int timeToLive) {
		if (cache.replaceIfEquals(key, oldValue, newValue)) {
			cache.expireAt(
				key, System.currentTimeMillis() + (long)timeToLive * 1000);

			return true;
		}

		return false;
	}

	protected volatile Cache<K, V> cache;

}