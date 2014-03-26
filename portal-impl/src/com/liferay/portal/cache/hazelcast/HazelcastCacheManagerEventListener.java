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

import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.map.MapService;

import com.liferay.portal.kernel.cache.CacheManagerListener;

/**
 * @author Tina Tian
 */
public class HazelcastCacheManagerEventListener
	implements DistributedObjectListener {

	public HazelcastCacheManagerEventListener(
		CacheManagerListener cacheManagerListener) {

		_cacheManagerListener = cacheManagerListener;
	}

	@Override
	public void distributedObjectCreated(
		DistributedObjectEvent distributedObjectEvent) {

		String serviceName = distributedObjectEvent.getServiceName();

		if (!serviceName.equals(MapService.SERVICE_NAME)) {
			return;
		}

		_cacheManagerListener.notifyCacheAdded(
			distributedObjectEvent.getDistributedObject().getName());
	}

	@Override
	public void distributedObjectDestroyed(
		DistributedObjectEvent distributedObjectEvent) {

		String serviceName = distributedObjectEvent.getServiceName();

		if (!serviceName.equals(MapService.SERVICE_NAME)) {
			return;
		}

		_cacheManagerListener.notifyCacheRemoved(
			distributedObjectEvent.getDistributedObject().getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof HazelcastCacheManagerEventListener)) {
			return false;
		}

		HazelcastCacheManagerEventListener portalCacheManagerEventListener =
			(HazelcastCacheManagerEventListener)obj;

		if (_registeredId.equals(
				portalCacheManagerEventListener._registeredId) &&
			_cacheManagerListener.equals(
				portalCacheManagerEventListener._cacheManagerListener)) {

			return true;
		}

		return false;
	}

	public String getRegisteredId() {
		return _registeredId;
	}

	@Override
	public int hashCode() {
		return _cacheManagerListener.hashCode() * 11 + _registeredId.hashCode();
	}

	public void setRegisteredId(String registeredId) {
		_registeredId = registeredId;
	}

	private CacheManagerListener _cacheManagerListener;
	private String _registeredId;

}