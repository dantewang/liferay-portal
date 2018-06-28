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

package com.liferay.portal.cache.cache2k.internal.event;

import com.liferay.portal.cache.PortalCacheListenerFactory;
import com.liferay.portal.cache.PortalCacheReplicator;
import com.liferay.portal.cache.PortalCacheReplicatorFactory;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheException;
import com.liferay.portal.kernel.cache.PortalCacheListener;

import java.io.Serializable;

import java.util.Properties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Xiangyue Cai
 */
@Component(immediate = true, service = PortalCacheListenerFactory.class)
public class Cache2kPortalCacheListenerFactory
		implements PortalCacheListenerFactory {

	@Override
	public <K extends Serializable, V> PortalCacheListener<K, V> create(
			Properties properties) {
		return null;
	}

	protected ClassLoader getClassLoader() {
		Class<?> clazz = getClass();

		return clazz.getClassLoader();
	}

	@Reference(unbind = "-")
	protected void setPortalCacheReplicatorFactory(
		PortalCacheReplicatorFactory portalCacheReplicatorFactory) {

	}

	private class EhcachePortalCacheReplicator
		<K extends Serializable, V extends Serializable>
			implements PortalCacheReplicator<K, V>{


		@Override
		public void dispose() {

		}

		@Override
		public void notifyEntryEvicted(
				PortalCache<K, V> portalCache, K key, V value, int timeToLive)
				throws PortalCacheException {

		}

		@Override
		public void notifyEntryExpired(
				PortalCache<K, V> portalCache, K key, V value, int timeToLive)
				throws PortalCacheException {

		}

		@Override
		public void notifyEntryPut(
				PortalCache<K, V> portalCache, K key, V value, int timeToLive)
				throws PortalCacheException {

		}

		@Override
		public void notifyEntryRemoved(
				PortalCache<K, V> portalCache, K key, V value, int timeToLive)
				throws PortalCacheException {

		}

		@Override
		public void notifyEntryUpdated
				(PortalCache<K, V> portalCache, K key, V value, int timeToLive)
				throws PortalCacheException {

		}

		@Override
		public void notifyRemoveAll(
				PortalCache<K, V> portalCache)
				throws PortalCacheException {

		}
	}

	private PortalCacheReplicatorFactory _portalCacheReplicatorFactory;

}