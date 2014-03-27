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

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;

import com.liferay.portal.cache.transactional.TransactionalPortalCache;
import com.liferay.portal.kernel.cache.BlockingPortalCache;
import com.liferay.portal.kernel.cache.CacheManagerListener;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resiliency.spi.SPIUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tina Tian
 */
public class HazelcastPortalCacheManager <K extends Serializable, V>
	implements PortalCacheManager<K, V> {

	public void afterPropertiesSet() {
		if ((_hazelcastManager != null) || (_mpiOnly && SPIUtil.isSPI())) {
			return;
		}

//		String configurationPath = PropsUtil.get(_configPropertyKey);

		String configurationPath = "/ehcache/liferay-multi-vm-hazelcast.xml";

		InputStream inputStream =
			HazelcastPortalCacheManager.class.getResourceAsStream(
				configurationPath);

		try {
			Config config = new XmlConfigBuilder(inputStream).build();

//			config.setClassLoader(PortalClassLoaderUtil.getClassLoader());

			config.setProperty("hazelcast.shutdownhook.enabled", "false");

			_hazelcastManager = new HazelcastManager<K, V>(
				Hazelcast.newHazelcastInstance(config));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clearAll() {
		_hazelcastManager.clearAll();
	}

	@Override
	public void destroy() {
		_portalCaches.clear();

		_hazelcastManager.destroy();
	}

	@Override
	public PortalCache<K, V> getCache(String name) {
		return getCache(name, false);
	}

	@Override
	public PortalCache<K, V> getCache(String name, boolean blocking) {
		PortalCache<K, V> portalCache = _portalCaches.get(name);

		if (portalCache == null) {
			synchronized (_hazelcastManager) {
				portalCache = _portalCaches.get(name);

				if (portalCache == null) {
					IMap<K, V> imap = _hazelcastManager.getCache(name);

					portalCache = new HazelcastPortalCache<K, V>(imap);

					if (PropsValues.TRANSACTIONAL_CACHE_ENABLED &&
						isTransactionalPortalCache(name)) {

						portalCache = new TransactionalPortalCache<K, V>(
							portalCache);
					}

					if (PropsValues.EHCACHE_BLOCKING_CACHE_ALLOWED &&
						blocking) {

						portalCache = new BlockingPortalCache<K, V>(
							portalCache);
					}

					_portalCaches.put(name, portalCache);
				}
			}
		}

		return portalCache;
	}

	@Override
	public Set<CacheManagerListener> getCacheManagerListeners() {
		return _cacheManagerListeners.keySet();
	}

	public HazelcastManager<K, V> getHazelcastManager() {
		return _hazelcastManager;
	}

	@Override
	public void reconfigureCaches(URL configurationURL) {
	}

	@Override
	public boolean registerCacheManagerListener(
		CacheManagerListener cacheManagerListener) {

		HazelcastCacheManagerEventListener hazelcastCacheManagerEventListener =
			new HazelcastCacheManagerEventListener(cacheManagerListener);

		_hazelcastManager.regisiterCacheManagerEventListener(
			hazelcastCacheManagerEventListener);

		_cacheManagerListeners.put(
			cacheManagerListener, hazelcastCacheManagerEventListener);

		return true;
	}

	@Override
	public void removeCache(String name) {
		_hazelcastManager.removeCache(name);
		_portalCaches.remove(name);
	}

	public void setConfigPropertyKey(String configPropertyKey) {
		_configPropertyKey = configPropertyKey;
	}

	public void setMpiOnly(boolean mpiOnly) {
		_mpiOnly = mpiOnly;
	}

	@Override
	public boolean unregisterCacheManagerListener(
		CacheManagerListener cacheManagerListener) {

		HazelcastCacheManagerEventListener hazelcastCacheManagerEventListener =
			_cacheManagerListeners.get(cacheManagerListener);

		if (hazelcastCacheManagerEventListener == null) {
			return false;
		}

		return _hazelcastManager.unregisiterCacheManagerEventListener(
			hazelcastCacheManagerEventListener);
	}

	@Override
	public void unregisterCacheManagerListeners() {
		for (HazelcastCacheManagerEventListener
			hazelcastCacheManagerEventListener :
				_cacheManagerListeners.values()) {

			_hazelcastManager.unregisiterCacheManagerEventListener(
				hazelcastCacheManagerEventListener);
		}
	}

	protected boolean isTransactionalPortalCache(String name) {
		for (String namePattern : PropsValues.TRANSACTIONAL_CACHE_NAMES) {
			if (StringUtil.wildcardMatches(
					name, namePattern, CharPool.QUESTION, CharPool.STAR,
					CharPool.PERCENT, true)) {

				return true;
			}
		}

		return false;
	}

	private static Log _log = LogFactoryUtil.getLog(
		HazelcastPortalCacheManager.class);

	private Map<CacheManagerListener, HazelcastCacheManagerEventListener>
		_cacheManagerListeners =
			new ConcurrentHashMap<
				CacheManagerListener, HazelcastCacheManagerEventListener>();
	private String _configPropertyKey;
	private HazelcastManager<K, V> _hazelcastManager;
	private boolean _mpiOnly;
	private Map<String, PortalCache<K, V>> _portalCaches =
		new HashMap<String, PortalCache<K, V>>();

}