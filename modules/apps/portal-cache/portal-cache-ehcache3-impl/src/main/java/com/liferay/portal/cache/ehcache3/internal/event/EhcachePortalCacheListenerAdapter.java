/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.event;

import com.liferay.portal.cache.ehcache3.internal.EhcacheUnwrapUtil;
import com.liferay.portal.cache.io.SerializableObjectWrapper;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import org.ehcache.core.events.CacheEvents;
import org.ehcache.event.CacheEventListener;

import java.io.Serializable;



/**
 * @author Tina Tian
 */
public class EhcachePortalCacheListenerAdapter<K extends Serializable, V>
	implements ConfigurableEhcachePortalCacheListener,
			   PortalCacheListener<K, V> {

	public EhcachePortalCacheListenerAdapter(
		CacheEventListener<Object, Object> cacheEventListener) {

		this.cacheEventListener = cacheEventListener;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void notifyEntryEvicted(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		cacheEventListener.onEvent(
			CacheEvents.eviction(
				_wrapKey(key), _wrapValue(value), EhcacheUnwrapUtil.getEhcache(portalCache)));
	}

	@Override
	public void notifyEntryExpired(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		cacheEventListener.onEvent(
			CacheEvents.expiry(
				_wrapKey(key), _wrapValue(value), EhcacheUnwrapUtil.getEhcache(portalCache)));
	}

	@Override
	public void notifyEntryPut(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		cacheEventListener.onEvent(
			CacheEvents.creation(
				_wrapKey(key), _wrapValue(value), EhcacheUnwrapUtil.getEhcache(portalCache)));
	}

	@Override
	public void notifyEntryRemoved(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		cacheEventListener.onEvent(
			CacheEvents.removal(
				_wrapKey(key), _wrapValue(value), EhcacheUnwrapUtil.getEhcache(portalCache)));
	}

	@Override
	public void notifyEntryUpdated(
			PortalCache<K, V> portalCache, K key, V value, int timeToLive)
		throws PortalCacheException {

		cacheEventListener.onEvent(
			CacheEvents.update(
				_wrapKey(key), null, _wrapValue(value), EhcacheUnwrapUtil.getEhcache(portalCache)));
	}

	@Override
	public void notifyRemoveAll(PortalCache<K, V> portalCache)
		throws PortalCacheException {
	}

	protected final CacheEventListener<Object, Object> cacheEventListener;

	private Object _wrapValue(V value) {
		if (value instanceof Serializable) {
			return new SerializableObjectWrapper((Serializable)value);
		}

		return value;
	}

	private Object _wrapKey(K key) {
		return new SerializableObjectWrapper(key);
	}

}