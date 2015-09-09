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
import com.liferay.portal.kernel.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration;
import net.sf.ehcache.config.CacheConfiguration.CacheEventListenerFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Dante Wang
 */
@RunWith(MockitoJUnitRunner.class)
public class EhcacheConfigurationHelperUtilTest extends Mockito {

	@BeforeClass
	public static void setUpClass() {
		new EhcacheConfigurationHelperUtil();
	}

	@Before
	public void setUp() {
		_props = mock(Props.class);
	}

	@Test
	public void testGetCacheManagerListenerPropertiesSet() {
		Configuration configuration = new Configuration();

		Set<Properties> propertiesSet =
			EhcacheConfigurationHelperUtil.getCacheManagerListenerPropertiesSet(
				configuration, _props);

		Assert.assertTrue(propertiesSet.isEmpty());

		FactoryConfiguration<?> factoryConfiguration =
			new FactoryConfiguration<>();

		factoryConfiguration.setClass(_FACTORY_CLASSNAME_1);

		configuration.addCacheManagerEventListenerFactory(factoryConfiguration);

		propertiesSet =
			EhcacheConfigurationHelperUtil.getCacheManagerListenerPropertiesSet(
				configuration, _props);

		Assert.assertNull(factoryConfiguration.getFullyQualifiedClassPath());
		Assert.assertTrue(propertiesSet.size() == 1);

		Iterator<Properties> iterator = propertiesSet.iterator();

		Properties properties = iterator.next();

		Assert.assertTrue(
			_FACTORY_CLASSNAME_1.equals(
				properties.getProperty(
					EhcacheConstants.
						CACHE_MANAGER_LISTENER_FACTORY_CLASS_NAME)));
	}

	@Test
	public void testGetPropertiesString() {
		Properties properties = new Properties();

		Assert.assertTrue(
			StringPool.BLANK.equals(
				EhcacheConfigurationHelperUtil.getPropertiesString(
					properties, null)));

		properties.put(_KEY_1, _VALUE_1);
		properties.put(_KEY_2, _VALUE_2);

		String expectedPropertiesString1 =
			_KEY_2 + "=" + _VALUE_2 + StringPool.COMMA + _KEY_1 + "=" +
				_VALUE_1;

		String expectedPropertiesString2 =
			_KEY_2 + "=" + _VALUE_2 + StringPool.PIPE + _KEY_1 + "=" + _VALUE_1;

		Assert.assertTrue(
			expectedPropertiesString1.equals(
				EhcacheConfigurationHelperUtil.getPropertiesString(
					properties, null)));

		Assert.assertTrue(
			expectedPropertiesString2.equals(
				EhcacheConfigurationHelperUtil.getPropertiesString(
					properties, StringPool.PIPE)));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testHandlePeerFactoryConfigurations() {
		List<FactoryConfiguration> factoryConfigurations =
			Collections.emptyList();

		EhcacheConfigurationHelperUtil.handlePeerFactoryConfigurations(
			factoryConfigurations, true, true, true, _props);

		Assert.assertTrue(factoryConfigurations.isEmpty());

		FactoryConfiguration factoryConfiguration = new FactoryConfiguration();

		factoryConfiguration.setClass(_FACTORY_CLASSNAME_1);

		FactoryConfiguration factoryConfigurationClone =
			factoryConfiguration.clone();

		factoryConfigurations = new ArrayList<>();

		factoryConfigurations.add(factoryConfigurationClone);

		EhcacheConfigurationHelperUtil.handlePeerFactoryConfigurations(
			factoryConfigurations, true, true, true, _props);

		Assert.assertTrue(factoryConfigurations.isEmpty());
		Assert.assertTrue(
			factoryConfiguration.equals(factoryConfigurationClone));

		factoryConfigurations.add(factoryConfigurationClone);

		EhcacheConfigurationHelperUtil.handlePeerFactoryConfigurations(
			factoryConfigurations, false, true, true, _props);

		Assert.assertTrue(factoryConfigurations.isEmpty());
		Assert.assertTrue(
			factoryConfiguration.equals(factoryConfigurationClone));

		factoryConfigurations.add(factoryConfigurationClone);

		EhcacheConfigurationHelperUtil.handlePeerFactoryConfigurations(
			factoryConfigurations, true, false, true, _props);

		Assert.assertTrue(factoryConfigurations.isEmpty());
		Assert.assertTrue(
			factoryConfiguration.equals(factoryConfigurationClone));

		factoryConfigurationClone = factoryConfiguration.clone();

		factoryConfigurations.add(factoryConfigurationClone);

		EhcacheConfigurationHelperUtil.handlePeerFactoryConfigurations(
			factoryConfigurations, true, true, false, _props);

		Assert.assertTrue(
			_FACTORY_CLASSNAME_1.equals(
				factoryConfigurationClone.getFullyQualifiedClassPath()));

		String propertiesString = factoryConfigurationClone.getProperties();

		Assert.assertTrue(
			propertiesString.contains(PropsKeys.CLUSTER_LINK_ENABLED));

		Assert.assertTrue(
			propertiesString.contains(
				PropsKeys.EHCACHE_CLUSTER_LINK_REPLICATION_ENABLED));

		factoryConfigurationClone = factoryConfiguration.clone();

		String property = _KEY_1 + "=" + _VALUE_1;

		factoryConfigurationClone.setProperties(property);

		EhcacheConfigurationHelperUtil.handlePeerFactoryConfigurations(
			factoryConfigurations, true, true, false, _props);

		propertiesString = factoryConfigurationClone.getProperties();

		Assert.assertTrue(propertiesString.contains(property));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIsRequireSerialization() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		// cluster aware and cluster enabled

		Assert.assertTrue(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfiguration, true, true));

		Assert.assertFalse(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfiguration, true, false));

		Assert.assertFalse(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfiguration, false, true));

		// flags about overflow

		CacheConfiguration cacheConfigurationClone = cacheConfiguration.clone();

		cacheConfigurationClone.setOverflowToDisk(true);
		cacheConfigurationClone.setOverflowToOffHeap(false);
		cacheConfigurationClone.setDiskPersistent(false);

		Assert.assertTrue(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfigurationClone, false, false));

		cacheConfigurationClone.setOverflowToDisk(false);
		cacheConfigurationClone.setOverflowToOffHeap(true);
		cacheConfigurationClone.setDiskPersistent(false);

		Assert.assertTrue(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfigurationClone, false, false));

		cacheConfigurationClone.setOverflowToDisk(false);
		cacheConfigurationClone.setOverflowToOffHeap(false);
		cacheConfigurationClone.setDiskPersistent(true);

		Assert.assertTrue(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfigurationClone, false, false));

		// persistence configuration

		PersistenceConfiguration persistenceConfiguration =
			new PersistenceConfiguration();

		persistenceConfiguration.strategy(Strategy.LOCALTEMPSWAP);

		cacheConfiguration.persistence(persistenceConfiguration);

		Assert.assertTrue(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfiguration, false, false));

		persistenceConfiguration.strategy(Strategy.NONE);

		Assert.assertFalse(
			EhcacheConfigurationHelperUtil.isRequireSerialization(
				cacheConfiguration, false, false));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testParseCacheConfiguration() {
		Assert.assertNull(
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				null, false, false, false, false, _props));

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		PortalCacheConfiguration portalCacheConfiguration =
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				cacheConfiguration, false, true, false, false, _props);

		Assert.assertTrue(
			PortalCacheConfiguration.DEFAULT_PORTAL_CACHE_NAME.equals(
				portalCacheConfiguration.getPortalCacheName()));

		cacheConfiguration.setName(_PORTAL_CACHE_NAME);

		portalCacheConfiguration =
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				cacheConfiguration, false, true, false, false, _props);

		Assert.assertTrue(
			_PORTAL_CACHE_NAME.equals(
				portalCacheConfiguration.getPortalCacheName()));

		// factory class name not in props and using default: ignored

		CacheEventListenerFactoryConfiguration
			cacheEventListenerConfiguration1 =
				new CacheEventListenerFactoryConfiguration();

		cacheEventListenerConfiguration1.setClass(_FACTORY_CLASSNAME_1);

		cacheConfiguration.addCacheEventListenerFactory(
			cacheEventListenerConfiguration1);

		CacheConfiguration cacheConfigurationClone = cacheConfiguration.clone();

		portalCacheConfiguration =
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				cacheConfigurationClone, false, true, false, false, _props);

		Set<Properties> portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertTrue(portalCacheListenerPropertiesSet.isEmpty());

		List<CacheEventListenerFactoryConfiguration>
			cacheEventListenerConfigurations =
				cacheConfigurationClone.getCacheEventListenerConfigurations();

		Assert.assertTrue(cacheEventListenerConfigurations.isEmpty());

		// cache event listener factory and bootstrap cache loader factory

		when(
			_props.get(PropsKeys.EHCACHE_CACHE_EVENT_LISTENER_FACTORY)
		).thenReturn(
			_FACTORY_CLASSNAME_2
		);

		CacheEventListenerFactoryConfiguration
			cacheEventListenerConfiguration2 =
				new CacheEventListenerFactoryConfiguration();

		cacheEventListenerConfiguration2.setClass(_FACTORY_CLASSNAME_2);

		cacheConfiguration.addCacheEventListenerFactory(
			cacheEventListenerConfiguration2);

		BootstrapCacheLoaderFactoryConfiguration
			bootstrapCacheLoaderFactoryConfiguration =
				new BootstrapCacheLoaderFactoryConfiguration();

		bootstrapCacheLoaderFactoryConfiguration.setClass(_FACTORY_CLASSNAME_3);

		cacheConfiguration.addBootstrapCacheLoaderFactory(
			bootstrapCacheLoaderFactoryConfiguration);

		cacheConfigurationClone = cacheConfiguration.clone();

		portalCacheConfiguration =
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				cacheConfigurationClone, true, false, true, false, _props);

		cacheEventListenerConfigurations =
			cacheConfigurationClone.getCacheEventListenerConfigurations();

		Assert.assertTrue(cacheEventListenerConfigurations.isEmpty());

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertTrue(portalCacheListenerPropertiesSet.size() == 2);

		boolean hasFactoryClassName1 = false;
		boolean hasFactoryClassName2 = false;

		for (Properties properties : portalCacheListenerPropertiesSet) {
			if (properties.containsValue(_FACTORY_CLASSNAME_1)) {
				hasFactoryClassName1 = true;

				continue;
			}

			if (properties.containsValue(_FACTORY_CLASSNAME_2)) {
				hasFactoryClassName2 = true;
			}
		}

		Assert.assertTrue(hasFactoryClassName1);
		Assert.assertTrue(hasFactoryClassName2);

		Properties bootstrapLoaderProperties =
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties();

		Assert.assertNull(
			cacheConfigurationClone.
				getBootstrapCacheLoaderFactoryConfiguration());

		Assert.assertTrue(
			_FACTORY_CLASSNAME_3.equals(
				bootstrapLoaderProperties.getProperty(
					EhcacheConstants.
						BOOTSTRAP_CACHE_LOADER_FACTORY_CLASS_NAME)));

		cacheConfigurationClone = cacheConfiguration.clone();

		portalCacheConfiguration =
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				cacheConfiguration.clone(), true, false, true, true, _props);

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertTrue(portalCacheListenerPropertiesSet.size() == 2);

		hasFactoryClassName1 = false;
		hasFactoryClassName2 = false;

		for (Properties properties : portalCacheListenerPropertiesSet) {
			if (properties.containsValue(_FACTORY_CLASSNAME_1)) {
				hasFactoryClassName1 = true;

				continue;
			}

			if (properties.containsValue(_FACTORY_CLASSNAME_2)) {
				hasFactoryClassName2 = true;
			}
		}

		Assert.assertTrue(hasFactoryClassName1);
		Assert.assertFalse(hasFactoryClassName2);

		bootstrapLoaderProperties =
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties();

		Assert.assertTrue(bootstrapLoaderProperties.isEmpty());

		cacheConfigurationClone = cacheConfiguration.clone();

		portalCacheConfiguration =
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				cacheConfiguration.clone(), false, false, true, true, _props);

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertTrue(portalCacheListenerPropertiesSet.size() == 1);

		bootstrapLoaderProperties =
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties();

		Assert.assertTrue(bootstrapLoaderProperties.isEmpty());

		cacheConfigurationClone = cacheConfiguration.clone();

		portalCacheConfiguration =
			EhcacheConfigurationHelperUtil.parseCacheConfiguration(
				cacheConfiguration.clone(), true, false, false, true, _props);

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertTrue(portalCacheListenerPropertiesSet.size() == 1);

		bootstrapLoaderProperties =
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties();

		Assert.assertTrue(bootstrapLoaderProperties.isEmpty());
	}

	@Test
	public void testParseFactoryClassName() {
		Assert.assertTrue(
			_FACTORY_CLASSNAME_1.equals(
				EhcacheConfigurationHelperUtil.parseFactoryClassName(
					_FACTORY_CLASSNAME_1, _props)));

		// valid portal property key

		when(
			_props.get(PropsKeys.EHCACHE_CACHE_EVENT_LISTENER_FACTORY)
		).thenReturn(
			_FACTORY_CLASSNAME_1
		);

		String factoryClassNameProperty =
			EhcacheConfigurationHelperUtil.PORTAL_PROPERTY_KEY + "=" +
				PropsKeys.EHCACHE_CACHE_EVENT_LISTENER_FACTORY;

		Assert.assertTrue(
			_FACTORY_CLASSNAME_1.equals(
				EhcacheConfigurationHelperUtil.parseFactoryClassName(
					factoryClassNameProperty, _props)));

		// invalid value of portal property key

		factoryClassNameProperty =
			EhcacheConfigurationHelperUtil.PORTAL_PROPERTY_KEY + "=" + _KEY_1;

		Assert.assertNull(
			EhcacheConfigurationHelperUtil.parseFactoryClassName(
				factoryClassNameProperty, _props));

		// invalid portal property key

		factoryClassNameProperty =
			"property=" + PropsKeys.EHCACHE_CACHE_EVENT_LISTENER_FACTORY;

		Assert.assertTrue(
			factoryClassNameProperty.equals(
				EhcacheConfigurationHelperUtil.parseFactoryClassName(
					factoryClassNameProperty, _props)));
	}

	@Test
	public void testParseProperties() {

		// null propertiesString

		Properties properties = EhcacheConfigurationHelperUtil.parseProperties(
			null, StringPool.COMMA, _props);

		Assert.assertTrue(properties.isEmpty());

		// simple propertiesString

		String propertiesString = _KEY_1 + "=" + _VALUE_1;

		properties = EhcacheConfigurationHelperUtil.parseProperties(
			propertiesString, StringPool.COMMA, _props);

		Assert.assertTrue(properties.size() == 1);
		Assert.assertTrue(_VALUE_1.equals(properties.getProperty(_KEY_1)));

		// invalid value of portal property key

		String fakePortalPropertyName = "fake.portal.property.name";

		when(
			_props.getArray(fakePortalPropertyName)
		).thenReturn(
			new String[] {}
		);

		propertiesString =
			_KEY_1 + "=" + _VALUE_1 + StringPool.COMMA +
				EhcacheConfigurationHelperUtil.PORTAL_PROPERTY_KEY + "=" +
					fakePortalPropertyName;

		properties = EhcacheConfigurationHelperUtil.parseProperties(
			propertiesString, StringPool.COMMA, _props);

		Assert.assertTrue(properties.size() == 1);
		Assert.assertTrue(_VALUE_1.equals(properties.getProperty(_KEY_1)));

		// valid portal property key

		String portalPropertyName = "ehcache.portal.property";

		String portalPropertyValue =
			_KEY_2 + "=" + _VALUE_2 + StringPool.COMMA + _KEY_3 + "=&amp;" +
				_VALUE_3 + StringPool.COMMA + "invalid";

		when(
			_props.getArray(portalPropertyName)
		).thenReturn(
			StringUtil.split(portalPropertyValue, CharPool.COMMA)
		);

		propertiesString =
			_KEY_1 + "=" + _VALUE_1 + StringPool.COMMA +
				EhcacheConfigurationHelperUtil.PORTAL_PROPERTY_KEY + "=" +
					portalPropertyName;

		properties = EhcacheConfigurationHelperUtil.parseProperties(
			propertiesString, StringPool.COMMA, _props);

		String expectedValue = "&" + _VALUE_3;

		Assert.assertTrue(properties.size() == 3);
		Assert.assertTrue(_VALUE_1.equals(properties.getProperty(_KEY_1)));
		Assert.assertTrue(_VALUE_2.equals(properties.getProperty(_KEY_2)));
		Assert.assertTrue(expectedValue.equals(properties.getProperty(_KEY_3)));

		// fall back when propertySeperator is null

		Properties properties2 = EhcacheConfigurationHelperUtil.parseProperties(
			propertiesString, null, _props);

		Assert.assertTrue(properties2.size() == properties.size());

		for (Object property : properties.keySet()) {
			Object value = properties.getProperty((String)property);

			Assert.assertTrue(
				value.equals(properties2.getProperty((String)property)));
		}
	}

	@Test
	public void testProcessEhcacheConfiguration() {
		CacheConfiguration defaultCacheConfiguration = new CacheConfiguration();

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName(_PORTAL_CACHE_NAME);

		Configuration configuration = new Configuration();

		configuration.setDefaultCacheConfiguration(defaultCacheConfiguration);
		configuration.addCache(cacheConfiguration);

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			EhcacheConfigurationHelperUtil.processEhcacheConfiguration(
				configuration, false, false, _props);

		Set<Properties> portalCacheManagerListenerPropertiesSet =
			portalCacheManagerConfiguration.
				getPortalCacheManagerListenerPropertiesSet();

		Assert.assertTrue(portalCacheManagerListenerPropertiesSet.isEmpty());

		Assert.assertNotNull(
			portalCacheManagerConfiguration.getPortalCacheConfiguration(
				_PORTAL_CACHE_NAME));

		Assert.assertNotNull(
			portalCacheManagerConfiguration.
				getDefaultPortalCacheConfiguration());
	}

	private static final String _FACTORY_CLASSNAME_1 = "factory.class.Name1";

	private static final String _FACTORY_CLASSNAME_2 = "factory.class.Name2";

	private static final String _FACTORY_CLASSNAME_3 = "factory.class.Name3";

	private static final String _KEY_1 = "key1";

	private static final String _KEY_2 = "key2";

	private static final String _KEY_3 = "key3";

	private static final String _PORTAL_CACHE_NAME = "portal.cache.name";

	private static final String _VALUE_1 = "value1";

	private static final String _VALUE_2 = "value2";

	private static final String _VALUE_3 = "value3";

	private Props _props;

}