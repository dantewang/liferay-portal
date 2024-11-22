/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.configuration;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Properties;
import java.util.Set;

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
		getDefaultCacheConfigurationBuilderSupplier() {

		return _defaultCacheConfigurationBuilderSupplier;
	}

	public CacheConfigurationBuilder<Object, Object>
			newCacheConfigurationBuilder()
		throws Exception {

		return _defaultCacheConfigurationBuilderSupplier.get();
	}

	public void setDefaultCacheConfigurationBuilderSupplier(
		UnsafeSupplier<CacheConfigurationBuilder<Object, Object>, Exception>
			defaultCacheConfigurationBuilderSupplier) {

		_defaultCacheConfigurationBuilderSupplier =
			defaultCacheConfigurationBuilderSupplier;

		try {
			if (_defaultCacheConfigurationBuilderSupplier.get() != null) {
				return;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		_defaultCacheConfigurationBuilderSupplier =
			() -> CacheConfigurationBuilder.newCacheConfigurationBuilder(
				Object.class, Object.class, ResourcePoolsBuilder.heap(100000));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EhcachePortalCacheManagerConfiguration.class.getName());

	private UnsafeSupplier<CacheConfigurationBuilder<Object, Object>, Exception>
		_defaultCacheConfigurationBuilderSupplier;

}