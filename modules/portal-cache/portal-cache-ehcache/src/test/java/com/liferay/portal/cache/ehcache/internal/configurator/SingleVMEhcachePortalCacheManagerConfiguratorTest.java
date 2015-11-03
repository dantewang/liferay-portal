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

import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.ehcache.EhcacheConstants;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.util.Props;

import java.util.ArrayList;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Dante Wang
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleVMEhcachePortalCacheManagerConfiguratorTest extends Mockito {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		_props = mock(Props.class);

		_singleVMEhcachePortalCacheManagerConfigurator =
			new SingleVMEhcachePortalCacheManagerConfigurator();

		_singleVMEhcachePortalCacheManagerConfigurator.setProps(_props);
	}

	@Test
	public void testGetConfigurationObjectValuePair() {
		exception.expect(NullPointerException.class);

		exception.expectMessage("Configuration path is null");

		_singleVMEhcachePortalCacheManagerConfigurator.
			getConfigurationObjectValuePair("Name", null, true);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testClearListenerConfigrations() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName("Test.Cache");

		cacheConfiguration.addBootstrapCacheLoaderFactory(
			new BootstrapCacheLoaderFactoryConfiguration());

		cacheConfiguration.addCacheEventListenerFactory(
			new CacheEventListenerFactoryConfiguration());

		Configuration configuration = new Configuration();

		configuration.addCache(cacheConfiguration);

		FactoryConfiguration eventListenerFactoryConfiguration =
			new FactoryConfiguration();

		eventListenerFactoryConfiguration.setClass(
			"com.liferay.factory.ClassName");

		configuration.addCacheManagerEventListenerFactory(
			eventListenerFactoryConfiguration);

		configuration.addCacheManagerPeerListenerFactory(
			new FactoryConfiguration());

		configuration.addCacheManagerPeerProviderFactory(
			new FactoryConfiguration());

		_singleVMEhcachePortalCacheManagerConfigurator.
			clearListenerConfigrations(configuration);

		// Assert CacheManager Configuration clean-up

		Assert.assertNull(
			eventListenerFactoryConfiguration.getFullyQualifiedClassPath());

		List<?> listenerFactoryConfigurations =
			configuration.getCacheManagerPeerListenerFactoryConfigurations();

		Assert.assertTrue(listenerFactoryConfigurations.isEmpty());

		List<?> providerFactoryConfigurations =
			configuration.getCacheManagerPeerProviderFactoryConfiguration();

		Assert.assertTrue(providerFactoryConfigurations.isEmpty());

		// Assert CacheConfiguration clean-up

		Assert.assertNull(
			cacheConfiguration.getBootstrapCacheLoaderFactoryConfiguration());

		List<?> cacheEventListenerfactoryConfigurations =
			cacheConfiguration.getCacheEventListenerConfigurations();

		Assert.assertTrue(cacheEventListenerfactoryConfigurations.isEmpty());
	}

	@Test
	public void testGetPortalPropertyKey() {
		Assert.assertNull(
			_singleVMEhcachePortalCacheManagerConfigurator.getPortalPropertyKey(
				null));
		Assert.assertNull(
			_singleVMEhcachePortalCacheManagerConfigurator.getPortalPropertyKey(
				"propertiesString"));

		Assert.assertNull(
			_singleVMEhcachePortalCacheManagerConfigurator.getPortalPropertyKey(
				"key=value"));

		Assert.assertEquals(
			"portal.property.key",
			_singleVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertyKey(
					_PORTAL_PROPERTY_KEY + "=portal.property.key"));
	}

	@Test
	public void testIsClearCacheManagerPeerConfigurations() {
		boolean result = _singleVMEhcachePortalCacheManagerConfigurator.
			isClearCacheManagerPeerConfigurations();

		Assert.assertTrue(result);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIsRequireSerialization() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		// flags about overflow

		cacheConfiguration.setOverflowToDisk(true);
		cacheConfiguration.setOverflowToOffHeap(false);
		cacheConfiguration.setDiskPersistent(false);

		Assert.assertTrue(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));

		cacheConfiguration.setOverflowToDisk(false);
		cacheConfiguration.setOverflowToOffHeap(true);
		cacheConfiguration.setDiskPersistent(false);

		Assert.assertTrue(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));

		cacheConfiguration.setOverflowToDisk(false);
		cacheConfiguration.setOverflowToOffHeap(false);
		cacheConfiguration.setDiskPersistent(true);

		Assert.assertTrue(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));

		cacheConfiguration.setOverflowToDisk(false);
		cacheConfiguration.setOverflowToOffHeap(false);
		cacheConfiguration.setDiskPersistent(false);

		Assert.assertFalse(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));

		// persistence configuration

		cacheConfiguration = new CacheConfiguration();

		PersistenceConfiguration persistenceConfiguration =
			new PersistenceConfiguration();

		persistenceConfiguration.strategy(Strategy.LOCALTEMPSWAP);

		cacheConfiguration.persistence(persistenceConfiguration);

		Assert.assertTrue(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));

		persistenceConfiguration.strategy(Strategy.NONE);

		Assert.assertFalse(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));
	}

	@Test
	public void testIsValidCacheEventListener() {
		Assert.assertFalse(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isValidCacheEventListener(null, true));

		Assert.assertTrue(
			_singleVMEhcachePortalCacheManagerConfigurator.
				isValidCacheEventListener(null, false));
	}

	@Test
	public void testParseBootstrapCacheLoaderConfigurations() {
		Properties properties =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseBootstrapCacheLoaderConfigurations(
					new BootstrapCacheLoaderFactoryConfiguration());

		Assert.assertNull(properties);
	}

	@Test
	public void testParseCacheEventListenerConfigurations() {
		CacheEventListenerFactoryConfiguration
			cacheEventListenerFactoryConfiguration =
			new CacheEventListenerFactoryConfiguration();

		cacheEventListenerFactoryConfiguration.setClass(
			"com.liferay.factory.ClassName");

		cacheEventListenerFactoryConfiguration.setListenFor("ALL");

		List<CacheEventListenerFactoryConfiguration>
			cacheEventListenerFactoryConfigurations = new ArrayList<>();

		cacheEventListenerFactoryConfigurations.add(
			cacheEventListenerFactoryConfiguration);

		Set<Properties> cacheEventListenerConfigurations =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheEventListenerConfigurations(
					cacheEventListenerFactoryConfigurations, false);

		Assert.assertTrue(cacheEventListenerConfigurations.size() == 1);

		Iterator<Properties> itr = cacheEventListenerConfigurations.iterator();

		Properties properties = itr.next();

		Assert.assertEquals(
			"com.liferay.factory.ClassName", properties.getProperty(
				EhcacheConstants.CACHE_EVENT_LISTENER_FACTORY_CLASS_NAME));

		Assert.assertEquals(
			PortalCacheListenerScope.ALL, properties.get(
				PortalCacheConfiguration.PORTAL_CACHE_LISTENER_SCOPE));
	}

	@Test
	public void testParseCacheListenerConfigurations() {
		
	}

	@Test
	public void testParseCacheManagerEventListenerConfigurations() {
		
	}

	@Test
	public void testParseListenerConfigurations() {
		
	}

	@Test
	public void testResolvePortalProperty() {
		
	}

	@Test
	public void testParseProperties() {
		Properties properties =
			_singleVMEhcachePortalCacheManagerConfigurator.parseProperties(
				null, ",");

		Assert.assertTrue(properties.isEmpty());

		properties =
			_singleVMEhcachePortalCacheManagerConfigurator.parseProperties(
				"key1=value1,key2=value2", ",");

		Assert.assertTrue("value1".equals(properties.getProperty("key1")));
		Assert.assertTrue("value2".equals(properties.getProperty("key2")));
	}

	private static final String _PORTAL_PROPERTY_KEY = "portalPropertyKey";

	private Props _props;
	private SingleVMEhcachePortalCacheManagerConfigurator
		_singleVMEhcachePortalCacheManagerConfigurator;

}