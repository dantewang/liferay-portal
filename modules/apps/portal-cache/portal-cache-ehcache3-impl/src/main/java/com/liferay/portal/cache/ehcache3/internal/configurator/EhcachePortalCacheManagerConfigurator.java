/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.configurator;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.PortalCacheReplicator;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.cache.ehcache3.internal.EhcachePortalCacheConfiguration;
import com.liferay.portal.cache.ehcache3.internal.configuration.EhcachePortalCacheManagerConfiguration;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.net.URL;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceType;
import org.ehcache.xml.XmlConfiguration;

/**
 * @author Tina Tian
 */
public class EhcachePortalCacheManagerConfigurator {

	public EhcachePortalCacheManagerConfigurator(
		Properties replicatorProperties,
		String defaultReplicatorPropertiesString) {

		_replicatorProperties = replicatorProperties;
		_defaultReplicatorPropertiesString = defaultReplicatorPropertiesString;
	}

	public ObjectValuePair<Configuration, EhcachePortalCacheManagerConfiguration>
		getConfigurationObjectValuePair(
			URL configurationURL, ClassLoader classLoader) {

		if (configurationURL == null) {
			throw new NullPointerException("Configuration path is null");
		}

		XmlConfiguration xmlConfiguration = new XmlConfiguration(
			configurationURL, classLoader);

		EhcachePortalCacheManagerConfiguration ehcachePortalCacheManagerConfiguration =
			_createPortalCacheManagerConfiguration(xmlConfiguration);

		ehcachePortalCacheManagerConfiguration.setDefaultCacheConfigurationBuilderSupplier(
			() -> xmlConfiguration.newCacheConfigurationBuilderFromTemplate("default", Object.class, Object.class));

		_populateCacheReplicator(ehcachePortalCacheManagerConfiguration);

		return new ObjectValuePair<>(
			xmlConfiguration, ehcachePortalCacheManagerConfiguration);
	}

	protected Properties parseProperties(
		String propertiesString, String propertySeparator) {

		Properties properties = new Properties();

		if (propertiesString == null) {
			return properties;
		}

		String propertyLines = propertiesString.trim();

		propertyLines = StringUtil.replace(
			propertyLines, propertySeparator, StringPool.NEW_LINE);

		try {
			properties.load(new UnsyncStringReader(propertyLines));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return properties;
	}

	private boolean _isRequireSerialization(
		CacheConfiguration<?, ?> cacheConfiguration) {

		ResourcePools resourcePools = cacheConfiguration.getResourcePools();

		for (ResourceType<?> resourceType : resourcePools.getResourceTypeSet()) {
			if (resourceType.requiresSerialization()) {
				return true;
			}
		}

		return false;
	}

	private EhcachePortalCacheManagerConfiguration _createPortalCacheManagerConfiguration(
		Configuration configuration) {

		Set<PortalCacheConfiguration> portalCacheConfigurations =
			new HashSet<>();

		Map<String, CacheConfiguration<?, ?>> cacheConfigurations =
			configuration.getCacheConfigurations();

		for (Map.Entry<String, CacheConfiguration<?, ?>> entry :
				cacheConfigurations.entrySet()) {

			CacheConfiguration<?, ?> cacheConfiguration = entry.getValue();

			portalCacheConfigurations.add(
				new EhcachePortalCacheConfiguration(
					entry.getKey(), Collections.emptySet(),
					_isRequireSerialization(cacheConfiguration)));
		}

		return new EhcachePortalCacheManagerConfiguration(
			Collections.emptySet(), null, portalCacheConfigurations);
	}

	private void _populateCacheReplicator(
		PortalCacheConfiguration portalCacheConfiguration,
		String replicatorPropertiesString) {

		Properties replicatorProperties = parseProperties(
			replicatorPropertiesString, StringPool.COMMA);

		replicatorProperties.put(PortalCacheReplicator.REPLICATOR, true);

		Set<Properties> portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		portalCacheListenerPropertiesSet.add(replicatorProperties);
	}

	private void _populateCacheReplicator(
		PortalCacheManagerConfiguration portalCacheManagerConfiguration) {

		if (_replicatorProperties == null) {
			return;
		}

		Set<String> portalCacheNames = new HashSet<>(
			_replicatorProperties.stringPropertyNames());

		portalCacheNames.addAll(
			portalCacheManagerConfiguration.getPortalCacheNames());

		for (String portalCacheName : portalCacheNames) {
			_populateCacheReplicator(
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName),
				GetterUtil.getString(
					_replicatorProperties.getProperty(portalCacheName),
					_defaultReplicatorPropertiesString));
		}

		_populateCacheReplicator(
			portalCacheManagerConfiguration.
				getDefaultPortalCacheConfiguration(),
			_defaultReplicatorPropertiesString);
	}

	private final String _defaultReplicatorPropertiesString;
	private final Properties _replicatorProperties;

}