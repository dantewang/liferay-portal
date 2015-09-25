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

import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

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
	service = SingleVMEhcachePortalCacheManagerConfigurator.class
)
public class SingleVMEhcachePortalCacheManagerConfigurator
	extends EhcachePortalCacheManagerConfigurator {

	@Activate
	protected void activate() {
		String configFile = props.get(
			PropsKeys.EHCACHE_SINGLE_VM_CONFIG_LOCATION);

		processConfigFile(
			configFile, _DEFAULT_CONFIG_FILE_NAME,
			PortalCacheManagerNames.SINGLE_VM);

		processEhcacheConfiguration();
	}

	@Override
	protected void handleBootstrapCacheLoader(
		Properties portalCacheBootstrapLoaderProperties,
		BootstrapCacheLoaderFactoryConfiguration
			bootstrapCacheLoaderFactoryConfiguration) {
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void processEhcacheConfiguration() {
		Set<Properties> cacheManagerListenerPropertiesSet =
			getCacheManagerListenerPropertiesSet(configuration, props);

		List<FactoryConfiguration> factoryConfigurations =
			configuration.getCacheManagerPeerProviderFactoryConfiguration();

		factoryConfigurations.clear();

		factoryConfigurations =
			configuration.getCacheManagerPeerListenerFactoryConfigurations();

		factoryConfigurations.clear();

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

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-single-vm.xml";

}