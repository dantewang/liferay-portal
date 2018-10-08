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
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.AspectJNewEnvTestRule;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration;
import net.sf.ehcache.config.CacheConfiguration.CacheEventListenerFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Xiangyue Cai
 */
public class SingleVMEhcachePortalCacheManagerConfiguratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			AspectJNewEnvTestRule.INSTANCE,
			new CodeCoverageAssertor() {

				@Override
				public void appendAssertClasses(List<Class<?>> assertClasses) {
					assertClasses.add(
						BaseEhcachePortalCacheManagerConfigurator.class);
				}

			});

	@Test
	public void testClearListenerConfigurationsWithCacheConfiguration() {

		// Test clearListenerConfigurations(null)

		_singleVMEhcachePortalCacheManagerConfigurator.
			clearListenerConfigrations((CacheConfiguration)null);

		// Test cleanListenerConfigurations(CacheConfiguration)

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.bootstrapCacheLoaderFactory(
			new BootstrapCacheLoaderFactoryConfiguration());

		cacheConfiguration.addCacheEventListenerFactory(
			new CacheEventListenerFactoryConfiguration());

		Assert.assertNotNull(
			cacheConfiguration.getBootstrapCacheLoaderFactoryConfiguration());
		Assert.assertNotEquals(
			Collections.emptyList(),
			cacheConfiguration.getCacheEventListenerConfigurations());

		_singleVMEhcachePortalCacheManagerConfigurator.
			clearListenerConfigrations(cacheConfiguration);

		Assert.assertNull(
			cacheConfiguration.getBootstrapCacheLoaderFactoryConfiguration());
		Assert.assertEquals(
			Collections.emptyList(),
			cacheConfiguration.getCacheEventListenerConfigurations());
	}

	@Test
	public void testClearListenerConfigurationsWithConfiguration() {
		Configuration configuration = new Configuration();

		FactoryConfiguration<?> factoryConfiguration =
			new FactoryConfiguration();

		factoryConfiguration.setClass(
			SingleVMEhcachePortalCacheManagerConfiguratorTest.class.getName());

		configuration.addCacheManagerEventListenerFactory(factoryConfiguration);

		configuration.addCacheManagerPeerListenerFactory(
			new FactoryConfiguration());
		configuration.addCacheManagerPeerProviderFactory(
			new FactoryConfiguration());

		CacheConfiguration defaultCacheConfiguration = new CacheConfiguration();

		defaultCacheConfiguration.addCacheEventListenerFactory(
			new CacheEventListenerFactoryConfiguration());

		configuration.setDefaultCacheConfiguration(defaultCacheConfiguration);

		CacheConfiguration cacheConfiguration =
			defaultCacheConfiguration.clone();

		cacheConfiguration.setName(_TEST_CACHE_NAME);

		configuration.addCache(cacheConfiguration);

		Assert.assertSame(
			factoryConfiguration,
			configuration.getCacheManagerEventListenerFactoryConfiguration());
		Assert.assertNotNull(factoryConfiguration.getFullyQualifiedClassPath());
		Assert.assertNotEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerListenerFactoryConfigurations());
		Assert.assertNotEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerProviderFactoryConfiguration());
		Assert.assertNotEquals(
			Collections.emptyList(),
			defaultCacheConfiguration.getCacheEventListenerConfigurations());
		Assert.assertNotEquals(
			Collections.emptyList(),
			cacheConfiguration.getCacheEventListenerConfigurations());

		_singleVMEhcachePortalCacheManagerConfigurator.
			clearListenerConfigrations(configuration);

		Assert.assertSame(
			factoryConfiguration,
			configuration.getCacheManagerEventListenerFactoryConfiguration());
		Assert.assertNull(factoryConfiguration.getFullyQualifiedClassPath());
		Assert.assertEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerListenerFactoryConfigurations());
		Assert.assertEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerProviderFactoryConfiguration());
		Assert.assertEquals(
			Collections.emptyList(),
			defaultCacheConfiguration.getCacheEventListenerConfigurations());
		Assert.assertEquals(
			Collections.emptyList(),
			cacheConfiguration.getCacheEventListenerConfigurations());
	}

	@Test
	public void testGetConfigurationObjectValuePair() {
		try {
			_singleVMEhcachePortalCacheManagerConfigurator.
				getConfigurationObjectValuePair("", null, true);

			Assert.fail("NullPointerException was not thrown");
		}
		catch (Exception e) {
			Assert.assertEquals(NullPointerException.class, e.getClass());
			Assert.assertEquals("Configuration path is null", e.getMessage());
		}

		ObjectValuePair<Configuration, PortalCacheManagerConfiguration>
			objectValuePair =
				_singleVMEhcachePortalCacheManagerConfigurator.
					getConfigurationObjectValuePair(
						PortalCacheManagerNames.SINGLE_VM,
						SingleVMEhcachePortalCacheManagerConfiguratorTest.
							class.getResource("/ehcache/test-single-vm.xml"),
						true);

		Configuration configuration = objectValuePair.getKey();

		Assert.assertEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerListenerFactoryConfigurations());
		Assert.assertEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerProviderFactoryConfiguration());
		Assert.assertEquals(
			PortalCacheManagerNames.SINGLE_VM, configuration.getName());

		CacheConfiguration defaultCacheConfiguration =
			configuration.getDefaultCacheConfiguration();

		Assert.assertEquals(
			9999, defaultCacheConfiguration.getMaxElementsInMemory());

		Map<String, CacheConfiguration> cacheConfigurations =
			configuration.getCacheConfigurations();

		CacheConfiguration cacheConfiguration = cacheConfigurations.get(
			"com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilter");

		Assert.assertEquals(10000, cacheConfiguration.getMaxElementsInMemory());

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			objectValuePair.getValue();

		PortalCacheConfiguration portalCacheConfiguration =
			portalCacheManagerConfiguration.
				getDefaultPortalCacheConfiguration();

		Assert.assertEquals(
			PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT,
			portalCacheConfiguration.getPortalCacheName());
	}

	@Test
	public void testIsRequireSerializationByCacheConfiguration() {
		_assertIsRequireSerializationByCacheConfiguration(
			true, "setOverflowToDisk");
		_assertIsRequireSerializationByCacheConfiguration(
			true, "setOverflowToOffHeap");
		_assertIsRequireSerializationByCacheConfiguration(
			true, "setDiskPersistent");
		_assertIsRequireSerializationByCacheConfiguration(false, null);
	}

	@Test
	public void testIsRequireSerializationByPersistenceConfiguration() {
		_assertIsRequireSerializationByPersistenceStrategy(
			true, PersistenceConfiguration.Strategy.LOCALTEMPSWAP);
		_assertIsRequireSerializationByPersistenceStrategy(
			true, PersistenceConfiguration.Strategy.LOCALRESTARTABLE);
		_assertIsRequireSerializationByPersistenceStrategy(
			true, PersistenceConfiguration.Strategy.DISTRIBUTED);
		_assertIsRequireSerializationByPersistenceStrategy(
			false, PersistenceConfiguration.Strategy.NONE);
	}

	@Test
	public void testParseCacheEventListenerConfigurations() {
		Assert.assertEquals(
			Collections.emptySet(),
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheEventListenerConfigurations(null, true));

		CacheEventListenerFactoryConfiguration
			cacheEventListenerFactoryConfiguration =
				new CacheEventListenerFactoryConfiguration();

		cacheEventListenerFactoryConfiguration.setClass(
			SingleVMEhcachePortalCacheManagerConfiguratorTest.class.getName());
		cacheEventListenerFactoryConfiguration.setListenFor("ALL");

		Properties expectedProperties = new Properties();

		expectedProperties.put(
			EhcacheConstants.CACHE_LISTENER_PROPERTIES_KEY_FACTORY_CLASS_NAME,
			SingleVMEhcachePortalCacheManagerConfiguratorTest.class.getName());
		expectedProperties.put(
			PortalCacheConfiguration.PORTAL_CACHE_LISTENER_PROPERTIES_KEY_SCOPE,
			PortalCacheListenerScope.ALL);

		Assert.assertEquals(
			Collections.singleton(expectedProperties),
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheEventListenerConfigurations(
					Collections.singletonList(
						cacheEventListenerFactoryConfiguration),
					false));
	}

	@Test
	public void testParseCacheListenerConfigurations() {
		EhcachePortalCacheConfiguration ehcachePortalCacheConfiguration =
			(EhcachePortalCacheConfiguration)
				_singleVMEhcachePortalCacheManagerConfigurator.
					parseCacheListenerConfigurations(
						new CacheConfiguration(_TEST_CACHE_NAME, 0), true);

		Assert.assertEquals(
			ehcachePortalCacheConfiguration.getPortalCacheName(),
			_TEST_CACHE_NAME);
		Assert.assertEquals(
			Collections.emptySet(),
			ehcachePortalCacheConfiguration.
				getPortalCacheListenerPropertiesSet());
		Assert.assertFalse(
			"isRequireSerialization() should return false",
			ehcachePortalCacheConfiguration.isRequireSerialization());
	}

	@Test
	public void testParseCacheManagerEventListenerConfigurations() {
		Assert.assertEquals(
			Collections.emptySet(),
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheManagerEventListenerConfigurations(null));

		FactoryConfiguration<?> factoryConfiguration =
			new FactoryConfiguration<>();

		factoryConfiguration.setClass(
			SingleVMEhcachePortalCacheManagerConfiguratorTest.class.getName());

		Properties expectedProperties = new Properties();

		expectedProperties.put(
			EhcacheConstants.
				CACHE_MANAGER_LISTENER_PROPERTIES_KEY_FACTORY_CLASS_NAME,
			SingleVMEhcachePortalCacheManagerConfiguratorTest.class.getName());

		Assert.assertEquals(
			Collections.singleton(expectedProperties),
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseCacheManagerEventListenerConfigurations(
					factoryConfiguration));
	}

	@Test
	public void testParseListenerConfigurations() {
		Configuration configuration = new Configuration();

		configuration.addCache(new CacheConfiguration(_TEST_CACHE_NAME, 0));

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			_singleVMEhcachePortalCacheManagerConfigurator.
				parseListenerConfigurations(configuration, true);

		Assert.assertNotNull(
			portalCacheManagerConfiguration.getPortalCacheConfiguration(
				_TEST_CACHE_NAME));
		Assert.assertEquals(
			Collections.emptySet(),
			portalCacheManagerConfiguration.
				getPortalCacheManagerListenerPropertiesSet());

		PortalCacheConfiguration defaultPortalCacheConfiguration =
			portalCacheManagerConfiguration.
				getDefaultPortalCacheConfiguration();

		Assert.assertEquals(
			PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT,
			defaultPortalCacheConfiguration.getPortalCacheName());
	}

	@Test
	public void testParseProperties() {
		Assert.assertEquals(
			new Properties(),
			_singleVMEhcachePortalCacheManagerConfigurator.parseProperties(
				null, StringPool.COMMA));

		Properties expectedProperties = new Properties();

		expectedProperties.put("key1", "value1");
		expectedProperties.put("key2", "value2");

		Assert.assertEquals(
			expectedProperties,
			_singleVMEhcachePortalCacheManagerConfigurator.parseProperties(
				"key1=value1,key2=value2".concat(StringPool.SPACE),
				StringPool.COMMA));
	}

	@AdviseWith(adviceClasses = UnsyncStringReaderAdvice.class)
	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testParsePropertiesException() {
		try {
			_singleVMEhcachePortalCacheManagerConfigurator.parseProperties(
				"key1=value1", StringPool.COMMA);

			Assert.fail("RuntimeException was not thrown");
		}
		catch (RuntimeException re) {
			Assert.assertSame(_IO_EXCEPTION, re.getCause());
		}
	}

	@Test
	public void testSetProps() {
		Props props = ProxyFactory.newDummyInstance(Props.class);

		_singleVMEhcachePortalCacheManagerConfigurator.setProps(props);

		Assert.assertSame(
			props, _singleVMEhcachePortalCacheManagerConfigurator.props);
	}

	@Aspect
	public static class UnsyncStringReaderAdvice {

		@Around(
			"execution(public int com.liferay.portal.kernel.io.unsync." +
				"UnsyncStringReader.read(char[]))"
		)
		public Object read() throws IOException {
			throw _IO_EXCEPTION;
		}

	}

	private void _assertIsRequireSerializationByCacheConfiguration(
		boolean expectedIsRequireSerialization, String methodName) {

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		if (methodName != null) {
			ReflectionTestUtil.invoke(
				cacheConfiguration, methodName, new Class<?>[] {boolean.class},
				true);
		}

		Assert.assertEquals(
			expectedIsRequireSerialization,
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));
	}

	private void _assertIsRequireSerializationByPersistenceStrategy(
		boolean expectedIsRequireSerialization,
		PersistenceConfiguration.Strategy strategy) {

		PersistenceConfiguration persistenceConfiguration =
			new PersistenceConfiguration();

		persistenceConfiguration.setStrategy(String.valueOf(strategy));

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.addPersistence(persistenceConfiguration);

		Assert.assertEquals(
			expectedIsRequireSerialization,
			_singleVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));
	}

	private static final IOException _IO_EXCEPTION = new IOException();

	private static final String _TEST_CACHE_NAME = "testCacheName";

	private final SingleVMEhcachePortalCacheManagerConfigurator
		_singleVMEhcachePortalCacheManagerConfigurator =
			new SingleVMEhcachePortalCacheManagerConfigurator();

}