/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.configuration;

import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;

import java.util.Set;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.FluentCacheConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * @author Tina Tian
 */
public class EhcachePortalCacheManagerConfiguration
	extends PortalCacheManagerConfiguration {

	public EhcachePortalCacheManagerConfiguration(
		CacheConfiguration<Object, Object> defaultCacheConfiguration,
		PortalCacheConfiguration defaultPortalCacheConfiguration,
		Set<PortalCacheConfiguration> portalCacheConfigurations) {

		super(defaultPortalCacheConfiguration, portalCacheConfigurations);

		_defaultCacheConfiguration = defaultCacheConfiguration;
	}

	public CacheConfiguration<Object, Object> getDefaultCacheConfiguration() {
		return _defaultCacheConfiguration;
	}

	public FluentCacheConfigurationBuilder<Object, Object, ?> newBuilder() {
		if (_defaultCacheConfiguration == null) {
			return CacheConfigurationBuilder.newCacheConfigurationBuilder(
				Object.class, Object.class, ResourcePoolsBuilder.heap(100000));
		}

		return _defaultCacheConfiguration.derive();
	}

	public void setDefaultCacheConfiguration(
		CacheConfiguration<Object, Object> defaultCacheConfiguration) {

		_defaultCacheConfiguration = defaultCacheConfiguration;
	}

	private CacheConfiguration<Object, Object> _defaultCacheConfiguration;

}