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

package com.liferay.portal.cache.ehcache.internal.configurator;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PropsKeys;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Properties;

/**
 * @author Leon Chi
 */
public class PropsInvocationHandler implements InvocationHandler {

	public static final String[]
		EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_DEFAULT_VALUE =
			{"value5", "value6"};

	public static final String[] EHCACHE_REPLICATOR_PROPERTIES_DEFAULT_VALUE =
		{"value7", "value8"};

	public static final String EHCACHE_RMI_PEER_LISTENER_FACTORY_CLASS_VALUE =
		"ehcache.rmi.peer.listener.factory.class.value";

	public static final String[]
		EHCACHE_RMI_PEER_LISTENER_FACTORY_PROPERTIES_VALUE =
			{"value1", "value2"};

	public static final String EHCACHE_RMI_PEER_PROVIDER_FACTORY_CLASS_VALUE =
		"ehcache.rmi.peer.provider.factory.class.value";

	public static final String[]
		EHCACHE_RMI_PEER_PROVIDER_FACTORY_PROPERTIES_VALUE =
			{"value3", "value4"};

	public PropsInvocationHandler() {
		this(true);
	}

	public PropsInvocationHandler(boolean clusterEnabled) {
		_clusterEnabled = clusterEnabled;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();

		if (methodName.equals("get")) {
			String key = (String)args[0];

			if (PropsKeys.EHCACHE_RMI_PEER_LISTENER_FACTORY_CLASS.equals(key)) {
				return EHCACHE_RMI_PEER_LISTENER_FACTORY_CLASS_VALUE;
			}

			if (PropsKeys.EHCACHE_RMI_PEER_PROVIDER_FACTORY_CLASS.equals(key)) {
				return EHCACHE_RMI_PEER_PROVIDER_FACTORY_CLASS_VALUE;
			}

			if (PropsKeys.CLUSTER_LINK_ENABLED.equals(key)) {
				if (_clusterEnabled) {
					return "true";
				}
				else {
					return "false";
				}
			}
		}

		if (methodName.equals("getArray")) {
			String key = (String)args[0];

			if (PropsKeys.EHCACHE_RMI_PEER_LISTENER_FACTORY_PROPERTIES.equals(
					key)) {

				return EHCACHE_RMI_PEER_LISTENER_FACTORY_PROPERTIES_VALUE;
			}

			if (PropsKeys.EHCACHE_RMI_PEER_PROVIDER_FACTORY_PROPERTIES.equals(
					key)) {

				return EHCACHE_RMI_PEER_PROVIDER_FACTORY_PROPERTIES_VALUE;
			}

			if (PropsKeys.EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_DEFAULT.
					equals(key)) {

				return EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_DEFAULT_VALUE;
			}

			if (PropsKeys.EHCACHE_REPLICATOR_PROPERTIES_DEFAULT.equals(key)) {
				return EHCACHE_REPLICATOR_PROPERTIES_DEFAULT_VALUE;
			}
		}

		if (methodName.equals("getProperties")) {
			String key = (String)args[0];

			if (key.equals(
					PropsKeys.EHCACHE_REPLICATOR_PROPERTIES +
						StringPool.PERIOD)) {

				return new Properties();
			}
		}

		return null;
	}

	private boolean _clusterEnabled;

}