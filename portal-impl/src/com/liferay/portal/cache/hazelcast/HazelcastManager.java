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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.io.Serializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tina Tian
 */
public class HazelcastManager <K extends Serializable, V> {

	public HazelcastManager(HazelcastInstance hazelcastInstance) {
		_hazelcastInstance = hazelcastInstance;
	}

	public boolean cacheExists(String name) {
		return _hazelcastIMaps.containsKey(name);
	}

	public void clearAll() {
		for (IMap<K, V> imap : _hazelcastIMaps.values()) {
			imap.clear();
		}
	}

	public void destroy() {
		for (IMap<K, V> imap : _hazelcastIMaps.values()) {
			imap.destroy();
		}

		_hazelcastInstance.shutdown();
	}

	public IMap<K, V> getCache(String name) {
		IMap<K, V> imap = _hazelcastIMaps.get(name);

		if (imap != null) {
			return imap;
		}

		imap = _hazelcastInstance.getMap(name);

		_hazelcastIMaps.put(name, imap);

		return imap;
	}

	public boolean regisiterCacheManagerEventListener(
		HazelcastCacheManagerEventListener hazelcastCacheManagerEventListener) {

		String registerId = _hazelcastInstance.addDistributedObjectListener(
			hazelcastCacheManagerEventListener);

		hazelcastCacheManagerEventListener.setRegisteredId(registerId);

		return true;
	}

	public void removeCache(String name) {
		IMap<K, V> imap = _hazelcastIMaps.remove(name);

		if (imap != null) {
			imap.destroy();
		}
	}

	public boolean unregisiterCacheManagerEventListener(
		HazelcastCacheManagerEventListener hazelcastCacheManagerEventListener) {

		return _hazelcastInstance.removeDistributedObjectListener(
			hazelcastCacheManagerEventListener.getRegisteredId());
	}

	private Map<String, IMap<K, V>> _hazelcastIMaps =
		new ConcurrentHashMap<String, IMap<K, V>>();
	private HazelcastInstance _hazelcastInstance;

}