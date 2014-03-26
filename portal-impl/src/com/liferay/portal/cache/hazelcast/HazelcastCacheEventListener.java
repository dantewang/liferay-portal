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

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;

import com.liferay.portal.kernel.cache.CacheListener;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Serializable;

/**
 * @author Tina Tian
 */
public class HazelcastCacheEventListener<K extends Serializable, V>
	implements EntryListener<K, V> {

	public HazelcastCacheEventListener(
		CacheListener<K, V> cacheListener, PortalCache<K, V> portalCache) {

		_cacheListener = cacheListener;
		_portalCache = portalCache;
	}

	@Override
	public void entryAdded(EntryEvent<K, V> entryEvent) {
		if (SkipListenerThreadLocal.isEnabled()) {
			return;
		}

		K key = entryEvent.getKey();
		V value = entryEvent.getValue();

		_cacheListener.notifyEntryPut(_portalCache, key, value);

		if (_log.isDebugEnabled()) {
			_log.debug("Insert " + key + " into " + _portalCache.getName());
		}
	}

	@Override
	public void entryEvicted(EntryEvent<K, V> entryEvent) {
		if (SkipListenerThreadLocal.isEnabled()) {
			return;
		}

		K key = entryEvent.getKey();
		V value = entryEvent.getValue();

		_cacheListener.notifyEntryEvicted(_portalCache, key, value);

		if (_log.isDebugEnabled()) {
			_log.debug("Evicted " + key + " from " + _portalCache.getName());
		}
	}

	@Override
	public void entryRemoved(EntryEvent<K, V> entryEvent) {
		if (SkipListenerThreadLocal.isEnabled()) {
			return;
		}

		K key = entryEvent.getKey();
		V value = entryEvent.getValue();

		_cacheListener.notifyEntryRemoved(_portalCache, key, value);

		if (_log.isDebugEnabled()) {
			_log.debug("Remove " + key + " from " + _portalCache.getName());
		}
	}

	@Override
	public void entryUpdated(EntryEvent<K, V> entryEvent) {
		if (SkipListenerThreadLocal.isEnabled()) {
			return;
		}

		K key = entryEvent.getKey();
		V value = entryEvent.getValue();

		_cacheListener.notifyEntryUpdated(_portalCache, key, value);

		if (_log.isDebugEnabled()) {
			_log.debug("Update " + key + " in " + _portalCache.getName());
		}
	}

	public CacheListener<K, V> getCacheListener() {
		return _cacheListener;
	}

	public PortalCache<K, V> getPortalCache() {
		return _portalCache;
	}

	public String getRegisteredId() {
		return _registeredId;
	}

	public void setRegisteredId(String registeredId) {
		_registeredId = registeredId;
	}

	private static Log _log = LogFactoryUtil.getLog(
		HazelcastCacheEventListener.class);

	private CacheListener<K, V> _cacheListener;
	private PortalCache<K, V> _portalCache;
	private String _registeredId;

}