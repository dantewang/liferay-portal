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

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.portal.cache.ehcache.EhcacheConstants;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCacheReplicator;
import com.liferay.portal.kernel.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
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
 * @author Tina Tian
 */
public abstract class AbstractEhcachePortalCacheManagerConfigurator {

	public URL getConfigFileURL(String configFile, ClassLoader classLoader) {
		if (classLoader != null) {
			if (Validator.isNull(configFile)) {
				return null;
			}

			return classLoader.getResource(configFile);
		}

		URL configFileURL =
			AbstractEhcachePortalCacheManagerConfigurator.class.getResource(
				configFile);

		if (configFileURL == null) {
			ClassLoader portalClassLoader =
				PortalClassLoaderUtil.getClassLoader();

			configFileURL = portalClassLoader.getResource(configFile);
		}

		return configFileURL;
	}

	public ObjectValuePair
		<Configuration, PortalCacheManagerConfiguration>
			getConfigurationObjectValuePair(
				String portalCacheManagerName, URL configurationURL,
				boolean clusterAware, boolean usingDefault) {

		if (configurationURL == null) {
			throw new NullPointerException("Configuration path is null");
		}

		Configuration ehcacheConfiguration =
			ConfigurationFactory.parseConfiguration(configurationURL);

		ehcacheConfiguration.setName(portalCacheManagerName);

		handleCacheManagerPeerFactoryConfigurations(
			ehcacheConfiguration.
				getCacheManagerPeerProviderFactoryConfiguration(),
			clusterAware);

		handleCacheManagerPeerFactoryConfigurations(
			ehcacheConfiguration.
				getCacheManagerPeerListenerFactoryConfigurations(),
			clusterAware);

		Set<Properties> cacheManagerListenerPropertiesSet =
			parseCacheManagerListenerPropertiesSet(ehcacheConfiguration);

		PortalCacheConfiguration defaultPortalCacheConfiguration =
			getPortalCacheConfiguration(
				ehcacheConfiguration.getDefaultCacheConfiguration(),
				clusterAware, usingDefault);

		Set<PortalCacheConfiguration> portalCacheConfigurations =
			new HashSet<>();

		Map<String, CacheConfiguration> cacheConfigurations =
			ehcacheConfiguration.getCacheConfigurations();

		for (Map.Entry<String, CacheConfiguration> entry :
				cacheConfigurations.entrySet()) {

			portalCacheConfigurations.add(
				getPortalCacheConfiguration(
					entry.getValue(), clusterAware, usingDefault));
		}

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			new PortalCacheManagerConfiguration(
				cacheManagerListenerPropertiesSet,
				defaultPortalCacheConfiguration, portalCacheConfigurations);

		return new ObjectValuePair<>(
			ehcacheConfiguration, portalCacheManagerConfiguration);
	}

	protected void activate() {
		_clusterEnabled = GetterUtil.getBoolean(
			props.get(PropsKeys.CLUSTER_LINK_ENABLED));
		_clusterLinkReplicationEnabled = GetterUtil.getBoolean(
			props.get(PropsKeys.EHCACHE_CLUSTER_LINK_REPLICATION_ENABLED));
	}

	protected PortalCacheConfiguration getPortalCacheConfiguration(
		CacheConfiguration cacheConfiguration, boolean clusterAware,
		boolean usingDefault) {

		if (cacheConfiguration == null) {
			return null;
		}

		String portalCacheName = cacheConfiguration.getName();

		if (portalCacheName == null) {
			portalCacheName =
				PortalCacheConfiguration.DEFAULT_PORTAL_CACHE_NAME;
		}

		Set<Properties> portalCacheListenerPropertiesSet =
			parseCacheEventListenerFactoryConfiguration(
				cacheConfiguration, clusterAware, usingDefault);

		Properties portalCacheBootstrapLoaderProperties =
			parseCacheBootstrapLoaderFactoryConfiguration(
				cacheConfiguration, clusterAware);

		boolean requireSerialization = isRequireSerialization(
			cacheConfiguration, clusterAware);

		return new EhcachePortalCacheConfiguration(
			portalCacheName, portalCacheListenerPropertiesSet,
			portalCacheBootstrapLoaderProperties, requireSerialization);
	}

	@SuppressWarnings("rawtypes")
	protected void handleCacheManagerPeerFactoryConfigurations(
		List<FactoryConfiguration> factoryConfigurations,
		boolean clusterAware) {

		if (factoryConfigurations.isEmpty()) {
			return;
		}

		if (!clusterAware || !_clusterEnabled ||
			_clusterLinkReplicationEnabled) {

			factoryConfigurations.clear();

			return;
		}

		for (FactoryConfiguration factoryConfiguration :
				factoryConfigurations) {

			Properties properties = null;

			factoryConfiguration.setClass(
				EhcachePropertyHelperUtil.parseFactoryClassName(
					factoryConfiguration.getFullyQualifiedClassPath(), props));

			String propertiesString = factoryConfiguration.getProperties();

			if (Validator.isNull(propertiesString)) {
				properties = new Properties();
			}
			else {
				properties = EhcachePropertyHelperUtil.parseProperties(
					propertiesString,
					factoryConfiguration.getPropertySeparator(), props);
			}

			properties.put(PropsKeys.CLUSTER_LINK_ENABLED, _clusterEnabled);
			properties.put(
				PropsKeys.EHCACHE_CLUSTER_LINK_REPLICATION_ENABLED,
				_clusterLinkReplicationEnabled);

			factoryConfiguration.setProperties(
				EhcachePropertyHelperUtil.getPropertiesString(
					properties, factoryConfiguration.getPropertySeparator()));
		}
	}

	@SuppressWarnings("deprecation")
	protected boolean isRequireSerialization(
		CacheConfiguration cacheConfiguration, boolean clusterAware) {

		if (clusterAware && _clusterEnabled) {
			return true;
		}

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

	protected Properties parseCacheBootstrapLoaderFactoryConfiguration(
		CacheConfiguration cacheConfiguration, boolean clusterAware) {

		Properties portalCacheBootstrapLoaderProperties = null;

		BootstrapCacheLoaderFactoryConfiguration
			bootstrapCacheLoaderFactoryConfiguration =
				cacheConfiguration.
					getBootstrapCacheLoaderFactoryConfiguration();

		if (bootstrapCacheLoaderFactoryConfiguration != null) {
			portalCacheBootstrapLoaderProperties =
				EhcachePropertyHelperUtil.parseProperties(
					bootstrapCacheLoaderFactoryConfiguration.getProperties(),
					bootstrapCacheLoaderFactoryConfiguration.
						getPropertySeparator(), props);

			if (clusterAware && _clusterEnabled) {
				if (!_clusterLinkReplicationEnabled) {
					portalCacheBootstrapLoaderProperties.put(
						EhcacheConstants.
							BOOTSTRAP_CACHE_LOADER_FACTORY_CLASS_NAME,
						EhcachePropertyHelperUtil.parseFactoryClassName(
							bootstrapCacheLoaderFactoryConfiguration.
								getFullyQualifiedClassPath(), props));
				}
			}
		}

		cacheConfiguration.addBootstrapCacheLoaderFactory(null);

		return portalCacheBootstrapLoaderProperties;
	}

	protected Set<Properties> parseCacheEventListenerFactoryConfiguration(
		CacheConfiguration cacheConfiguration, boolean clusterAware,
		boolean usingDefault) {

		Set<Properties> portalCacheListenerPropertiesSet = new HashSet<>();

		List<CacheEventListenerFactoryConfiguration>
			cacheEventListenerConfigurations =
				cacheConfiguration.getCacheEventListenerConfigurations();

		for (CacheEventListenerFactoryConfiguration
				cacheEventListenerFactoryConfiguration :
					cacheEventListenerConfigurations) {

			String factoryClassName =
				EhcachePropertyHelperUtil.parseFactoryClassName(
					cacheEventListenerFactoryConfiguration.
						getFullyQualifiedClassPath(), props);

			Properties properties = EhcachePropertyHelperUtil.parseProperties(
				cacheEventListenerFactoryConfiguration.getProperties(),
				cacheEventListenerFactoryConfiguration. getPropertySeparator(),
				props);

			PortalCacheListenerScope portalCacheListenerScope =
				_portalCacheListenerScopes.get(
					cacheEventListenerFactoryConfiguration.getListenFor());

			if (factoryClassName.equals(
					props.get(
						PropsKeys.EHCACHE_CACHE_EVENT_LISTENER_FACTORY))) {

				if (clusterAware && _clusterEnabled) {
					if (!_clusterLinkReplicationEnabled) {
						properties.put(
							EhcacheConstants.
								CACHE_EVENT_LISTENER_FACTORY_CLASS_NAME,
							factoryClassName);
					}

					properties.put(
						PortalCacheConfiguration.PORTAL_CACHE_LISTENER_SCOPE,
						portalCacheListenerScope);
					properties.put(PortalCacheReplicator.REPLICATOR, true);

					portalCacheListenerPropertiesSet.add(properties);
				}
			}
			else if (!usingDefault) {
				properties.put(
					EhcacheConstants.CACHE_EVENT_LISTENER_FACTORY_CLASS_NAME,
					factoryClassName);
				properties.put(
					PortalCacheConfiguration.PORTAL_CACHE_LISTENER_SCOPE,
					portalCacheListenerScope);

				portalCacheListenerPropertiesSet.add(properties);
			}
		}

		cacheEventListenerConfigurations.clear();

		return portalCacheListenerPropertiesSet;
	}

	protected Set<Properties>
		parseCacheManagerListenerPropertiesSet(
			Configuration ehcacheConfiguration) {

		FactoryConfiguration<?> factoryConfiguration =
			ehcacheConfiguration.
				getCacheManagerEventListenerFactoryConfiguration();

		if (factoryConfiguration == null) {
			return Collections.emptySet();
		}

		Properties properties = EhcachePropertyHelperUtil.parseProperties(
			factoryConfiguration.getProperties(),
			factoryConfiguration.getPropertySeparator(), props);

		properties.put(
			EhcacheConstants.CACHE_MANAGER_LISTENER_FACTORY_CLASS_NAME,
			EhcachePropertyHelperUtil.parseFactoryClassName(
				factoryConfiguration.getFullyQualifiedClassPath(), props));

		factoryConfiguration.setClass(null);

		return Collections.singleton(properties);
	}

	protected volatile Props props;

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

	private boolean _clusterEnabled;
	private boolean _clusterLinkReplicationEnabled;

}