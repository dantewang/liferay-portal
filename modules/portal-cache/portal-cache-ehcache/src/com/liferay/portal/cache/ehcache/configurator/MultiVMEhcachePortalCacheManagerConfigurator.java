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
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.PortalCacheReplicator;
import com.liferay.portal.kernel.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(
	immediate = true,
	service = MultiVMEhcachePortalCacheManagerConfigurator.class
)
public class MultiVMEhcachePortalCacheManagerConfigurator
	extends EhcachePortalCacheManagerConfigurator {

	@Activate
	protected void activate() {
		String configFile = props.get(
			PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION);

		processConfigFile(
			configFile, _DEFAULT_CONFIG_FILE_NAME,
			PortalCacheManagerNames.MULTI_VM);

		processEhcacheConfiguration();
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

	@Override
	protected void processEhcacheConfiguration() {
		_clusterEnabled = GetterUtil.getBoolean(
			props.get(PropsKeys.CLUSTER_LINK_ENABLED));

		_clusterLinkReplicationEnabled = GetterUtil.getBoolean(
			props.get(PropsKeys.EHCACHE_CLUSTER_LINK_REPLICATION_ENABLED));

		Set<Properties> cacheManagerListenerPropertiesSet =
			getCacheManagerListenerPropertiesSet(configuration, props);

		processPeerFactoryConfigurations(
			configuration.
				getCacheManagerPeerProviderFactoryConfiguration(),
			props);

		processPeerFactoryConfigurations(
			configuration.
				getCacheManagerPeerListenerFactoryConfigurations(),
			props);

		PortalCacheConfiguration defaultPortalCacheConfiguration =
			parseCacheConfiguration(
				configuration.getDefaultCacheConfiguration(), usingDefault,
				props);

		Set<PortalCacheConfiguration> portalCacheConfigurations =
			new HashSet<>();

		Map<String, CacheConfiguration> cacheConfigurations =
			configuration.getCacheConfigurations();

		for (Map.Entry<String, CacheConfiguration> entry :
				cacheConfigurations.entrySet()) {

			portalCacheConfigurations.add(
				parseCacheConfiguration(entry.getValue(), usingDefault, props));
		}

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			cacheManagerListenerPropertiesSet, defaultPortalCacheConfiguration,
			portalCacheConfigurations);
	}

	@SuppressWarnings("rawtypes")
	protected void processPeerFactoryConfigurations(
		List<FactoryConfiguration> factoryConfigurations, Props props) {

		if (!_clusterEnabled || _clusterLinkReplicationEnabled) {
			factoryConfigurations.clear();

			return;
		}

		if (factoryConfigurations.isEmpty()) {
			return;
		}

		for (FactoryConfiguration factoryConfiguration :
				factoryConfigurations) {

			Properties properties = null;

			factoryConfiguration.setClass(
				CacheConfigurationHelperUtil.parseFactoryClassName(
					factoryConfiguration.getFullyQualifiedClassPath(), props));

			String propertiesString = factoryConfiguration.getProperties();

			if (Validator.isNull(propertiesString)) {
				properties = new Properties();
			}
			else {
				properties = CacheConfigurationHelperUtil.parseProperties(
					propertiesString,
					factoryConfiguration.getPropertySeparator(), props);
			}

			properties.put(PropsKeys.CLUSTER_LINK_ENABLED, _clusterEnabled);
			properties.put(
					PropsKeys.EHCACHE_CLUSTER_LINK_REPLICATION_ENABLED,
					_clusterLinkReplicationEnabled);

			factoryConfiguration.setProperties(
				CacheConfigurationHelperUtil.getPropertiesString(
					properties, factoryConfiguration.getPropertySeparator()));
		}
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-multi-vm-clustered.xml";

	private boolean _clusterEnabled = false;
	private boolean _clusterLinkReplicationEnabled = false;

}