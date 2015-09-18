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

import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.Props;

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
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Dante Wang
 */
@Component(
	immediate = true,
	property = PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.SINGLE_VM,
	service = PortalCacheManagerConfigurator.class
)
public class SingleVMEhcachePortalCacheManagerConfigurator
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
	@SuppressWarnings("rawtypes")
	protected void activate() {
		Configuration ehcacheConfiguration =
			cacheManagerConfigurator.getCacheManagerConfiguration();

		Set<Properties> cacheManagerListenerPropertiesSet =
			getCacheManagerListenerPropertiesSet(ehcacheConfiguration, props);

		List<FactoryConfiguration> factoryConfigurations =
			ehcacheConfiguration.
				getCacheManagerPeerProviderFactoryConfiguration();

		factoryConfigurations.clear();

		factoryConfigurations =
			ehcacheConfiguration.
				getCacheManagerPeerListenerFactoryConfigurations();

		factoryConfigurations.clear();

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
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.STATIC,
		policyOption = ReferencePolicyOption.RELUCTANT,
		target = "(" + PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.SINGLE_VM + ")"
	)
	protected void setCacheManagerConfigurator(
		CacheManagerConfigurator<Configuration> cacheManagerConfigurator) {

		this.cacheManagerConfigurator = cacheManagerConfigurator;
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

}