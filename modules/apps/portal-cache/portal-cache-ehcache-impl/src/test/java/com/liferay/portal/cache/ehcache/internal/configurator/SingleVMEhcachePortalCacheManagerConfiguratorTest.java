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

package com.liferay.portal.cache.ehcache.internal.configurator;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.cache.ehcache.internal.EhcacheConstants;
import com.liferay.portal.cache.ehcache.internal.EhcachePortalCacheConfiguration;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.CacheEventListenerFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Xiangyue Cai
 */
public class SingleVMEhcachePortalCacheManagerConfiguratorTest {

	@ClassRule
	@Rule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		new CodeCoverageAssertor() {

			@Override
			public void appendAssertClasses(List<Class<?>> assertClasses) {
				assertClasses.add(
					BaseEhcachePortalCacheManagerConfigurator.class);
			}

		};

	@Before
	public void setUp() {
		_singleVMEhcachePortalCacheManagerConfigurator =
			new SingleVMEhcachePortalCacheManagerConfigurator();
	}

	@Test
	public void testClearListenerConfigurationsWithCacheConfiguration() {
		CacheConfiguration cacheConfiguration = null;

		try {
			_singleVMEhcachePortalCacheManagerConfigurator.
				clearListenerConfigrations(cacheConfiguration);
		}
		catch (NullPointerException npe) {
			Assert.fail("Should not throw exception!");
		}

		cacheConfiguration = new CacheConfiguration();

		CacheEventListenerFactoryConfiguration
			cacheEventListenerFactoryConfiguration =
				new CacheEventListenerFactoryConfiguration();

		cacheConfiguration.addCacheEventListenerFactory(
			cacheEventListenerFactoryConfiguration);

		_singleVMEhcachePortalCacheManagerConfigurator.
			clearListenerConfigrations(cacheConfiguration);
		List<?> factoryConfigurations =
			cacheConfiguration.getCacheEventListenerConfigurations();

		Assert.assertTrue(factoryConfigurations.isEmpty());
	}

	@Test
	public void testClearListenerConfigurationsWithConfiguration() {
		Configuration configuration = new Configuration();

		FactoryConfiguration<?> factoryConfiguration =
			new FactoryConfiguration();

		configuration.addCacheManagerPeerListenerFactory(factoryConfiguration);

		configuration.addCacheManagerPeerProviderFactory(factoryConfiguration);

		configuration.addCacheManagerEventListenerFactory(factoryConfiguration);

		List<?> listenerFactoryConfigurations =
			configuration.getCacheManagerPeerListenerFactoryConfigurations();

		List<?> providerFactoryConfigurations =
			configuration.getCacheManagerPeerProviderFactoryConfiguration();
		_singleVMEhcachePortalCacheManagerConfigurator.
			clearListenerConfigrations(configuration);

		Map<String, CacheConfiguration> cacheConfigurations =
			configuration.getCacheConfigurations();

		Assert.assertTrue(listenerFactoryConfigurations.isEmpty());

		Assert.assertTrue(providerFactoryConfigurations.isEmpty());

		Assert.assertNull(configuration.getDefaultCacheConfiguration());

		Assert.assertNull(factoryConfiguration.getFullyQualifiedClassPath());

		Assert.assertTrue(cacheConfigurations.isEmpty());
	}

	@Test
	public void testGetConfigurationObjectValuePair() {
		try {
			_singleVMEhcachePortalCacheManagerConfigurator.
				getConfigurationObjectValuePair("", null, true);
			Assert.fail("no exception thrown!");
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof NullPointerException);
		}

		String defaultConfigFile = "/ehcache/liferay-single-vm.xml";

		URL configFileURL =
			BaseEhcachePortalCacheManagerConfigurator.class.getResource(
				defaultConfigFile);

		ObjectValuePair<Configuration, PortalCacheManagerConfiguration>
			objectValuePair =
				_singleVMEhcachePortalCacheManagerConfigurator.
					getConfigurationObjectValuePair(
						PortalCacheManagerNames.SINGLE_VM, configFileURL, true);

		Configuration configuration = objectValuePair.getKey();

		Assert.assertEquals(
			PortalCacheManagerNames.SINGLE_VM, configuration.getName());

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			objectValuePair.getValue();

		Assert.assertEquals(
			PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT,
			portalCacheManagerConfiguration.
				getDefaultPortalCacheConfiguration().getPortalCacheName());
	}

	@Test
	public void testIsRequireSerialization() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setOverflowToDisk(true);

		Boolean requireSerialization =
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration);

		Assert.assertTrue(requireSerialization);

		cacheConfiguration.setOverflowToDisk(false);

		cacheConfiguration.setOverflowToOffHeap(true);

		requireSerialization =
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration);

		Assert.assertTrue(requireSerialization);

		cacheConfiguration.setOverflowToDisk(false);
		cacheConfiguration.setOverflowToOffHeap(false);
		cacheConfiguration.setDiskPersistent(true);

		requireSerialization =
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration);

		Assert.assertTrue(requireSerialization);

		CacheConfiguration testPersistenceCacheConfiguration =
			new CacheConfiguration();
		PersistenceConfiguration persistenceConfiguration =
			new PersistenceConfiguration();

		persistenceConfiguration.setStrategy(
			String.valueOf(Strategy.DISTRIBUTED));

		testPersistenceCacheConfiguration.addPersistence(
			persistenceConfiguration);

		requireSerialization =
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(testPersistenceCacheConfiguration);

		Assert.assertTrue(requireSerialization);

		persistenceConfiguration.setStrategy(String.valueOf(Strategy.NONE));

		testPersistenceCacheConfiguration.addPersistence(
			persistenceConfiguration);

		requireSerialization =
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(testPersistenceCacheConfiguration);

		Assert.assertFalse(requireSerialization);
	}

	@Test
	public void testParseCacheEventListenerConfigurations() {
		Set<Properties> emptySet =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheEventListenerConfigurations(null, true);

		Assert.assertTrue(emptySet.isEmpty());

		CacheEventListenerFactoryConfiguration
			cacheEventListenerFactoryConfiguration =
				new CacheEventListenerFactoryConfiguration();

		cacheEventListenerFactoryConfiguration.setProperties(
			PropsKeys.EHCACHE_REPLICATOR_PROPERTIES);
		cacheEventListenerFactoryConfiguration.setPropertySeparator(
			StringPool.COMMA);
		cacheEventListenerFactoryConfiguration.setClass(getClass().getName());

		List<CacheEventListenerFactoryConfiguration>
			cacheEventListenerFactoryConfigurationList = new ArrayList<>();

		cacheEventListenerFactoryConfigurationList.add(
			cacheEventListenerFactoryConfiguration);

		Set<Properties> portalCacheListenerPropertiesSet =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheEventListenerConfigurations(
					cacheEventListenerFactoryConfigurationList, false);

		for (Properties properties : portalCacheListenerPropertiesSet) {
			String factoryClassName =
				properties.getProperty(EhcacheConstants.
					CACHE_LISTENER_PROPERTIES_KEY_FACTORY_CLASS_NAME);

			Assert.assertEquals(getClass().getName(), factoryClassName);
		}
	}

	@Test
	public void testParseCacheListenerConfigurations() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName(_TEST_CACHE_NAME);

		EhcachePortalCacheConfiguration ehcachePortalCacheConfiguration =
			(EhcachePortalCacheConfiguration)
				_singleVMEhcachePortalCacheManagerConfigurator.
					parseCacheListenerConfigurations(cacheConfiguration, true);

		Set<Properties> portalCacheListenerPropertiesSet =
			ehcachePortalCacheConfiguration.
				getPortalCacheListenerPropertiesSet();

		Assert.assertEquals(
			ehcachePortalCacheConfiguration.getPortalCacheName(),
			_TEST_CACHE_NAME);

		Assert.assertTrue(portalCacheListenerPropertiesSet.isEmpty());

		Assert.assertFalse(
			ehcachePortalCacheConfiguration.isRequireSerialization());
	}

	@Test
	public void testParseCacheManagerEventListenerConfigurations() {
		Set<Properties> emptySet =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheManagerEventListenerConfigurations(null);

		Assert.assertTrue(emptySet.isEmpty());

		FactoryConfiguration<?> factoryConfiguration =
			new FactoryConfiguration<>();

		factoryConfiguration.setClass(getClass().getName());

		Set<Properties> propertiesSet =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheManagerEventListenerConfigurations(
					factoryConfiguration);

		for (Properties properties : propertiesSet) {
			String factoryClassName =
				properties.getProperty(EhcacheConstants.
					CACHE_MANAGER_LISTENER_PROPERTIES_KEY_FACTORY_CLASS_NAME);

			Assert.assertEquals(getClass().getName(), factoryClassName);
		}
	}

	@Test
	public void testParseListenerConfigurations() {
		Configuration configuration = new Configuration();
		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName(_TEST_CACHE_NAME);

		configuration.addCache(cacheConfiguration);

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseListenerConfigurations(configuration, true);

		PortalCacheConfiguration portalCacheConfiguration =
			portalCacheManagerConfiguration.getPortalCacheConfiguration(
				_TEST_CACHE_NAME);

		Assert.assertEquals(
			portalCacheConfiguration.getPortalCacheName(), _TEST_CACHE_NAME);

		Set<Properties> cacheManagerListenerPropertiesSet =
			portalCacheManagerConfiguration.
				getPortalCacheManagerListenerPropertiesSet();

		Assert.assertTrue(cacheManagerListenerPropertiesSet.isEmpty());

		Assert.assertEquals(portalCacheManagerConfiguration.
			getDefaultPortalCacheConfiguration().getPortalCacheName(),
			PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT);
	}

	@Test
	public void testParseProperties() {
		Properties properties =
			_singleVMEhcachePortalCacheManagerConfigurator.parseProperties(
				null, null);

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	public void testSetProps() {
		Props props = (Props)ProxyUtil.newProxyInstance(
			Props.class.getClassLoader(), new Class<?>[] {Props.class},
			new InvocationHandler() {

				@Override
				public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

					return null;
				}

			});

		_singleVMEhcachePortalCacheManagerConfigurator.setProps(props);

		Assert.assertNotNull(
			_singleVMEhcachePortalCacheManagerConfigurator.props);
	}

	private static final String _TEST_CACHE_NAME = "testCacheName";

	private SingleVMEhcachePortalCacheManagerConfigurator
		_singleVMEhcachePortalCacheManagerConfigurator;

}