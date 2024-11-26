/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.configuration;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.cache.ehcache3.internal.EhcacheExpiryPolicy;

import java.util.Properties;
import java.util.Set;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * @author Tina Tian
 */
public class EhcachePortalCacheManagerConfiguration
	extends PortalCacheManagerConfiguration {

	public EhcachePortalCacheManagerConfiguration(
		Set<Properties> portalCacheManagerListenerPropertiesSet,
		PortalCacheConfiguration defaultPortalCacheConfiguration,
		Set<PortalCacheConfiguration> portalCacheConfigurations) {

		super(
			portalCacheManagerListenerPropertiesSet,
			defaultPortalCacheConfiguration, portalCacheConfigurations);
	}

	public UnsafeSupplier<CacheConfigurationBuilder<Object, Object>, Exception>
		getDefaultCacheConfigurationBuilderUnsafeSupplier() {

		return _defaultCacheConfigurationBuilderUnsafeSupplier;
	}

	@SuppressWarnings("unchecked")
	public CacheConfigurationBuilder<Object, Object>
			newDefaultCacheConfigurationBuilder()
		throws Exception {

		CacheConfigurationBuilder<Object, Object> cacheConfigurationBuilder =
			_defaultCacheConfigurationBuilderUnsafeSupplier.get();

		if (cacheConfigurationBuilder == null) {
			cacheConfigurationBuilder =
				CacheConfigurationBuilder.newCacheConfigurationBuilder(
					Object.class, Object.class,
					ResourcePoolsBuilder.heap(100000));
		}

		CacheConfiguration<Object, Object> tempCacheConfiguration =
			cacheConfigurationBuilder.build();

		return (CacheConfigurationBuilder<Object, Object>)
			tempCacheConfiguration.derive(
			).withExpiry(
				new EhcacheExpiryPolicy(
					tempCacheConfiguration.getExpiryPolicy())
			);
	}

	public void setDefaultCacheConfigurationBuilderUnsafeSupplier(
		UnsafeSupplier<CacheConfigurationBuilder<Object, Object>, Exception>
			defaultCacheConfigurationBuilderUnsafeSupplier) {

		_defaultCacheConfigurationBuilderUnsafeSupplier =
			defaultCacheConfigurationBuilderUnsafeSupplier;
	}

	private UnsafeSupplier<CacheConfigurationBuilder<Object, Object>, Exception>
		_defaultCacheConfigurationBuilderUnsafeSupplier;

}