/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.configuration;

import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.cache.ehcache.internal.EhcacheExpiryPolicy;

import java.util.Set;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.FluentCacheConfigurationBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;

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
			CacheConfigurationBuilder<Object, Object>
				cacheConfigurationBuilder =
					CacheConfigurationBuilder.newCacheConfigurationBuilder(
						Object.class, Object.class,
						ResourcePoolsBuilder.heap(100000));

			return cacheConfigurationBuilder.withExpiry(
				new EhcacheExpiryPolicy(ExpiryPolicy.NO_EXPIRY));
		}

		FluentCacheConfigurationBuilder<Object, Object, ?>
			fluentCacheConfigurationBuilder =
				_defaultCacheConfiguration.derive();

		return fluentCacheConfigurationBuilder.withExpiry(
			new EhcacheExpiryPolicy(
				_defaultCacheConfiguration.getExpiryPolicy()));
	}

	public void setDefaultCacheConfiguration(
		CacheConfiguration<Object, Object> defaultCacheConfiguration) {

		_defaultCacheConfiguration = defaultCacheConfiguration;
	}

	private CacheConfiguration<Object, Object> _defaultCacheConfiguration;

}