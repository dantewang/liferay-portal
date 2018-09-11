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

	public static final String[] ARRAY_0 = {};

	public static final String[] ARRAY_1 = {"value"};

	public static final String[] ARRAY_2 = {"value1", "value2"};

	public static final String[]
		EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_DEFAULT_VALUE =
			{"value5", "value6"};

	public static final Properties
		EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE = new Properties();

	public static final Properties
		EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1 = new Properties();

	public static final String[] EHCACHE_REPLICATOR_PROPERTIES_DEFAULT_VALUE =
		{"value7", "value8"};

	public static final Properties EHCACHE_REPLICATOR_PROPERTIES_VALUE =
		new Properties();

	public static final Properties EHCACHE_REPLICATOR_PROPERTIES_VALUE1 =
		new Properties();

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

	public static final String PORTAL_PROPERTY_KEY0 = "portal.property.Key0";

	public static final String PORTAL_PROPERTY_KEY1 = "portal.property.Key1";

	public static final String PORTAL_PROPERTY_KEY2 = "portal.property.Key2";

	static {
		EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1.setProperty(
			"name1", "value1");
		EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1.setProperty(
			"name2", "value2");
		EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1.setProperty(
			"name3", "value3");

		EHCACHE_REPLICATOR_PROPERTIES_VALUE1.setProperty("name1", "value1");
		EHCACHE_REPLICATOR_PROPERTIES_VALUE1.setProperty("name2", "value2");
		EHCACHE_REPLICATOR_PROPERTIES_VALUE1.setProperty("name3", "value3");
	}

	public PropsInvocationHandler(boolean clusterEnabled) {
		_clusterEnabled = clusterEnabled;
	}

	public PropsInvocationHandler(
		boolean clusterEnabled, boolean bootstrapLoaderEnabled) {

		_clusterEnabled = clusterEnabled;
		_bootstrapLoaderEnabled = bootstrapLoaderEnabled;
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

			if (PropsKeys.EHCACHE_BOOTSTRAP_CACHE_LOADER_ENABLED.equals(key)) {
				if (_bootstrapLoaderEnabled) {
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

			if (PORTAL_PROPERTY_KEY0.equals(key)) {
				return ARRAY_0;
			}

			if (PORTAL_PROPERTY_KEY1.equals(key)) {
				return ARRAY_1;
			}

			if (PORTAL_PROPERTY_KEY2.equals(key)) {
				return ARRAY_2;
			}
		}

		if (methodName.equals("getProperties")) {
			String key = (String)args[0];

			if (key.equals(
					PropsKeys.EHCACHE_REPLICATOR_PROPERTIES +
						StringPool.PERIOD)) {

				return EHCACHE_REPLICATOR_PROPERTIES_VALUE;
			}

			if (key.equals(
					PropsKeys.EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES +
						StringPool.PERIOD)) {

				return EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE;
			}
		}

		return null;
	}

	private boolean _bootstrapLoaderEnabled;
	private final boolean _clusterEnabled;

}