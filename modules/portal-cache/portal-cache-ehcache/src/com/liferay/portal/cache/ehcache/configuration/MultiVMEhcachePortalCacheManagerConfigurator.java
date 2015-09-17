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

package com.liferay.portal.cache.ehcache.configuration;

import com.liferay.portal.cache.ehcache.EhcacheConstants;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.PortalCacheReplicator;
import com.liferay.portal.kernel.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.FactoryConfiguration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(
	immediate = true,
	property = {
		PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM
	},
	service = PortalCacheManagerConfigurator.class
)
public class MultiVMEhcachePortalCacheManagerConfigurator
	extends AbstractEhcachePortalCacheManagerConfigurator {

	@Override
	public CacheManagerConfigurator<Configuration>
		getCacheManagerConfigurator() {

		return cacheManagerConfigurator;
	}

	@Override
	public PortalCacheManagerConfiguration
		getPortalCacheManagerConfiguration() {

		return portalCacheManagerConfiguration;
	}

	@Activate
	protected void activate() {
		Configuration ehcacheConfiguration =
			cacheManagerConfigurator.getCacheManagerConfiguration();

		_clusterEnabled = GetterUtil.getBoolean(
			props.get(PropsKeys.CLUSTER_LINK_ENABLED));

		_clusterLinkReplicationEnabled = GetterUtil.getBoolean(
			props.get(PropsKeys.EHCACHE_CLUSTER_LINK_REPLICATION_ENABLED));

		Set<Properties> cacheManagerListenerPropertiesSet =
			getCacheManagerListenerPropertiesSet(ehcacheConfiguration, props);

		Properties properties = new Properties();

		properties.put(PropsKeys.CLUSTER_LINK_ENABLED, _clusterEnabled);
		properties.put(
			PropsKeys.EHCACHE_CLUSTER_LINK_REPLICATION_ENABLED,
			_clusterLinkReplicationEnabled);

		_processPeerFactoryConfigurations(
			ehcacheConfiguration.
				getCacheManagerPeerProviderFactoryConfiguration(), properties,
			props);

		_processPeerFactoryConfigurations(
			ehcacheConfiguration.
				getCacheManagerPeerListenerFactoryConfigurations(), properties,
			props);

		PortalCacheConfiguration defaultPortalCacheConfiguration =
			parseCacheConfiguration(
				ehcacheConfiguration.getDefaultCacheConfiguration(),
				cacheManagerConfigurator.usingDefault(), props);

		Set<PortalCacheConfiguration> portalCacheConfigurations =
			new HashSet<>();

		Map<String, CacheConfiguration> cacheConfigurations =
			ehcacheConfiguration.getCacheConfigurations();

		for (Map.Entry<String, CacheConfiguration> entry :
				cacheConfigurations.entrySet()) {

			portalCacheConfigurations.add(
				parseCacheConfiguration(
					entry.getValue(), cacheManagerConfigurator.usingDefault(),
					props));
		}

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			cacheManagerListenerPropertiesSet, defaultPortalCacheConfiguration,
			portalCacheConfigurations);
	}

	@Override
	protected void handleBootstrapCacheLoader(
		Properties portalCacheBootstrapLoaderProperties,
		BootstrapCacheLoaderFactoryConfiguration
			bootstrapCacheLoaderFactoryConfiguration) {

		if (_clusterEnabled) {
			if (!_clusterLinkReplicationEnabled) {
				portalCacheBootstrapLoaderProperties.put(
					EhcacheConstants.
						BOOTSTRAP_CACHE_LOADER_FACTORY_CLASS_NAME,
					CacheConfigurationHelperUtil.parseFactoryClassName(
						bootstrapCacheLoaderFactoryConfiguration.
							getFullyQualifiedClassPath(), props));
			}
		}
	}

	@Override
	protected void handleCacheEventListener(
		Set<Properties> portalCacheListenerPropertiesSet,
		String factoryClassName,
		PortalCacheListenerScope portalCacheListenerScope,
		Properties properties, boolean usingDefault, Props props) {

		if (factoryClassName.equals(
				props.get(
					PropsKeys.EHCACHE_CACHE_EVENT_LISTENER_FACTORY))) {

			if (_clusterEnabled) {
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
		else {
			super.handleCacheEventListener(
				portalCacheListenerPropertiesSet, factoryClassName,
				portalCacheListenerScope, properties, usingDefault, props);
		}
	}

	@Override
	protected boolean isRequireSerialization(
		CacheConfiguration cacheConfiguration) {

		return (_clusterEnabled ||
			super.isRequireSerialization(cacheConfiguration));
	}

	@Reference
	protected void setCacheManagerConfigurator(
		CacheManagerConfigurator<Configuration> cacheManagerConfigurator) {

		this.cacheManagerConfigurator = cacheManagerConfigurator;
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

	@SuppressWarnings("rawtypes")
	private void _processPeerFactoryConfigurations(
		List<FactoryConfiguration> factoryConfigurations, Properties properties,
		Props props) {

		if (!_clusterEnabled || _clusterLinkReplicationEnabled) {
			factoryConfigurations.clear();

			return;
		}

		handlePeerFactoryConfigurations(
			factoryConfigurations, properties, props);
	}

	private boolean _clusterEnabled = false;
	private boolean _clusterLinkReplicationEnabled = false;

}