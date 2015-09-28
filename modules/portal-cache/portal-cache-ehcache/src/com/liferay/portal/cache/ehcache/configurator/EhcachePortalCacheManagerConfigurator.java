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

package com.liferay.portal.cache.ehcache.configurator;

import com.liferay.portal.cache.ehcache.EhcacheConstants;
import com.liferay.portal.cache.ehcache.internal.EhcachePortalCacheConfiguration;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration;
import net.sf.ehcache.config.CacheConfiguration.CacheEventListenerFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.event.NotificationScope;

/**
 * @author Dante Wang
 */
public abstract class EhcachePortalCacheManagerConfigurator {

	public Configuration getEhcacheManagerConfiguration() {
		return configuration;
	}

	public PortalCacheManagerConfiguration
		getPortalCacheManagerConfiguration() {

		return portalCacheManagerConfiguration;
	}

	public boolean usingDefault() {
		return usingDefault;
	}

	protected Set<Properties>
		getCacheManagerListenerPropertiesSet(
			Configuration ehcacheConfiguration, Props props) {

		FactoryConfiguration<?> factoryConfiguration =
			ehcacheConfiguration.
				getCacheManagerEventListenerFactoryConfiguration();

		if (factoryConfiguration == null) {
			return Collections.emptySet();
		}

		Properties properties = CacheConfigurationHelperUtil.parseProperties(
			factoryConfiguration.getProperties(),
			factoryConfiguration.getPropertySeparator(), props);

		properties.put(
			EhcacheConstants.CACHE_MANAGER_LISTENER_FACTORY_CLASS_NAME,
			CacheConfigurationHelperUtil.parseFactoryClassName(
				factoryConfiguration.getFullyQualifiedClassPath(), props));

		factoryConfiguration.setClass(null);

		return Collections.singleton(properties);
	}

	protected abstract void handleBootstrapCacheLoader(
		Properties portalCacheBootstrapLoaderProperties,
		BootstrapCacheLoaderFactoryConfiguration
			bootstrapCacheLoaderFactoryConfiguration);

	protected void handleCacheEventListener(
		Set<Properties> portalCacheListenerPropertiesSet,
		String factoryClassName,
		PortalCacheListenerScope portalCacheListenerScope,
		Properties properties, boolean usingDefault, Props props) {

		if (!usingDefault) {
			properties.put(
				EhcacheConstants.CACHE_EVENT_LISTENER_FACTORY_CLASS_NAME,
				factoryClassName);
			properties.put(
				PortalCacheConfiguration.PORTAL_CACHE_LISTENER_SCOPE,
				portalCacheListenerScope);

			portalCacheListenerPropertiesSet.add(properties);
		}
	}

	@SuppressWarnings("deprecation")
	protected boolean isRequireSerialization(
		CacheConfiguration cacheConfiguration) {

		if (cacheConfiguration.isOverflowToDisk() ||
			cacheConfiguration.isOverflowToOffHeap() ||
			cacheConfiguration.isDiskPersistent()) {

			return true;
		}

		PersistenceConfiguration persistenceConfiguration =
			cacheConfiguration.getPersistenceConfiguration();

		if (persistenceConfiguration != null) {
			PersistenceConfiguration.Strategy strategy =
				persistenceConfiguration.getStrategy();

			if (!strategy.equals(PersistenceConfiguration.Strategy.NONE)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	protected PortalCacheConfiguration parseCacheConfiguration(
		CacheConfiguration cacheConfiguration, boolean usingDefault,
		Props props) {

		if (cacheConfiguration == null) {
			return null;
		}

		String portalCacheName = cacheConfiguration.getName();

		if (portalCacheName == null) {
			portalCacheName =
				PortalCacheConfiguration.DEFAULT_PORTAL_CACHE_NAME;
		}

		Set<Properties> portalCacheListenerPropertiesSet = new HashSet<>();

		List<CacheEventListenerFactoryConfiguration>
			cacheEventListenerConfigurations =
				cacheConfiguration.getCacheEventListenerConfigurations();

		for (CacheEventListenerFactoryConfiguration
				cacheEventListenerFactoryConfiguration :
					cacheEventListenerConfigurations) {

			String factoryClassName =
				CacheConfigurationHelperUtil.parseFactoryClassName(
					cacheEventListenerFactoryConfiguration.
					getFullyQualifiedClassPath(), props);

			Properties properties =
				CacheConfigurationHelperUtil.parseProperties(
					cacheEventListenerFactoryConfiguration.getProperties(),
					cacheEventListenerFactoryConfiguration.
						getPropertySeparator(),
					props);

			PortalCacheListenerScope portalCacheListenerScope =
				_portalCacheListenerScopes.get(
					cacheEventListenerFactoryConfiguration.getListenFor());

			handleCacheEventListener(
				portalCacheListenerPropertiesSet, factoryClassName,
				portalCacheListenerScope, properties, usingDefault, props);
		}

		cacheEventListenerConfigurations.clear();

		Properties portalCacheBootstrapLoaderProperties = null;

		BootstrapCacheLoaderFactoryConfiguration
			bootstrapCacheLoaderFactoryConfiguration =
				cacheConfiguration.
					getBootstrapCacheLoaderFactoryConfiguration();

		if (bootstrapCacheLoaderFactoryConfiguration != null) {
			portalCacheBootstrapLoaderProperties =
				CacheConfigurationHelperUtil.parseProperties(
					bootstrapCacheLoaderFactoryConfiguration.getProperties(),
					bootstrapCacheLoaderFactoryConfiguration.
						getPropertySeparator(),
					props);

			handleBootstrapCacheLoader(
				portalCacheBootstrapLoaderProperties,
				bootstrapCacheLoaderFactoryConfiguration);

			cacheConfiguration.addBootstrapCacheLoaderFactory(null);
		}

		boolean requireSerialization = isRequireSerialization(
			cacheConfiguration);

		return new EhcachePortalCacheConfiguration(
			portalCacheName, portalCacheListenerPropertiesSet,
			portalCacheBootstrapLoaderProperties, requireSerialization);
	}

	protected void processConfigFile(
		String configFile, String defaultConfigFile, String cacheManagerName) {

		if (Validator.isNull(configFile)) {
			configFile = defaultConfigFile;

			usingDefault = true;
		}

		URL configFileURL = getClass().getResource(configFile);

		if (configFileURL == null) {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			configFileURL = classLoader.getResource(configFile);
		}

		configuration = ConfigurationFactory.parseConfiguration(configFileURL);

		configuration.setName(cacheManagerName);
	}

	protected abstract void processEhcacheConfiguration();

	protected Configuration configuration;
	protected PortalCacheManagerConfiguration portalCacheManagerConfiguration;
	protected Props props;
	protected boolean usingDefault = false;

	private static final Map<NotificationScope, PortalCacheListenerScope>
		_portalCacheListenerScopes = new EnumMap<>(NotificationScope.class);

	static {
		_portalCacheListenerScopes.put(
			NotificationScope.ALL, PortalCacheListenerScope.ALL);
		_portalCacheListenerScopes.put(
			NotificationScope.LOCAL, PortalCacheListenerScope.LOCAL);
		_portalCacheListenerScopes.put(
			NotificationScope.REMOTE, PortalCacheListenerScope.REMOTE);
	}

}