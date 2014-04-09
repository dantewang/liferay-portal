/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.cache.hazelcast;

import com.hazelcast.core.IMap;

import com.liferay.portal.kernel.cache.CacheListener;
import com.liferay.portal.kernel.cache.CacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCache;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Tina Tian
 */
public class HazelcastPortalCache <K extends Serializable, V>
	implements PortalCache<K, V> {

	public HazelcastPortalCache(IMap<K, V> cache) {
		_cache = cache;
	}

	@Override
	public V get(K key) {
		return _cache.get(key);
	}

	@Override
	public String getName() {
		return _cache.getName();
	}

	@Override
	public void put(K key, V value) {
		_cache.put(key, value);
	}

	@Override
	public void put(K key, V value, int timeToLive) {
		_cache.put(key, value, timeToLive, TimeUnit.SECONDS);
	}

	@Override
	public void putQuiet(K key, V value) {
		SkipListenerThreadLocal.setEnabled(true);

		_cache.put(key, value);

		SkipListenerThreadLocal.setEnabled(false);
	}

	@Override
	public void putQuiet(K key, V value, int timeToLive) {
		SkipListenerThreadLocal.setEnabled(true);

		_cache.put(key, value, timeToLive, TimeUnit.SECONDS);

		SkipListenerThreadLocal.setEnabled(false);
	}

	@Override
	public void registerCacheListener(CacheListener<K, V> cacheListener) {
		registerCacheListener(cacheListener, CacheListenerScope.ALL);
	}

	@Override
	public void registerCacheListener(
		CacheListener<K, V> cacheListener,
		CacheListenerScope cacheListenerScope) {

		if (_cacheEventListeners.containsKey(cacheListener)) {
			return;
		}

		HazelcastCacheEventListener<K, V> entryListener =
			new HazelcastCacheEventListener<K, V>(cacheListener, this);

		String registeredId = _cache.addEntryListener(entryListener, true);

		entryListener.setRegisteredId(registeredId);

		_cacheEventListeners.put(cacheListener, entryListener);
	}

	@Override
	public void remove(K key) {
		_cache.remove(key);
	}

	@Override
	public void removeAll() {
		_cache.clear();
	}

	@Override
	public void unregisterCacheListener(CacheListener<K, V> cacheListener) {
		HazelcastCacheEventListener<K, V> entryListener =
			_cacheEventListeners.remove(cacheListener);

		if (entryListener == null) {
			return;
		}

		_cache.removeEntryListener(entryListener.getRegisteredId());
	}

	@Override
	public void unregisterCacheListeners() {
		for (HazelcastCacheEventListener<K, V> entryListener :
				_cacheEventListeners.values()) {

			_cache.removeEntryListener(entryListener.getRegisteredId());
		}

		_cacheEventListeners.clear();
	}

	private IMap<K, V> _cache;
	private Map<CacheListener<K, V>, HazelcastCacheEventListener<K, V>>
		_cacheEventListeners =
			new ConcurrentHashMap<
				CacheListener<K, V>, HazelcastCacheEventListener<K, V>>();

}