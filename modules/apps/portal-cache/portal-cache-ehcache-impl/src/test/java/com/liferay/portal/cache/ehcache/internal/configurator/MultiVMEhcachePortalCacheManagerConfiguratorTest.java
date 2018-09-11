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
import com.liferay.portal.cache.PortalCacheReplicator;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class MultiVMEhcachePortalCacheManagerConfiguratorTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		_multiVMEhcachePortalCacheManagerConfigurator =
			new MultiVMEhcachePortalCacheManagerConfigurator();
	}

	@Test
	public void testActivate() {
		_activate(true, true);

		boolean bootstrapLoaderEnabled = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderEnabled");
		Properties bootstrapLoaderProperties = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties");
		boolean clusterEnabled = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator, "clusterEnabled");
		String defaultBootstrapLoaderPropertiesString =
			ReflectionTestUtil.getFieldValue(
				_multiVMEhcachePortalCacheManagerConfigurator,
				"_defaultBootstrapLoaderPropertiesString");
		String defaultReplicatorPropertiesString =
			ReflectionTestUtil.getFieldValue(
				_multiVMEhcachePortalCacheManagerConfigurator,
				"_defaultReplicatorPropertiesString");
		Properties replicatorProperties = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties");

		Assert.assertTrue(bootstrapLoaderEnabled);
		Assert.assertEquals(
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE,
			bootstrapLoaderProperties);
		Assert.assertTrue(clusterEnabled);
		Assert.assertEquals(
			StringUtil.merge(
				PropsInvocationHandler.
					EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_DEFAULT_VALUE,
				StringPool.COMMA),
			defaultBootstrapLoaderPropertiesString);
		Assert.assertEquals(
			StringUtil.merge(
				PropsInvocationHandler.
					EHCACHE_REPLICATOR_PROPERTIES_DEFAULT_VALUE,
				StringPool.COMMA),
			defaultReplicatorPropertiesString);
		Assert.assertEquals(
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE,
			replicatorProperties);
	}

	@Test
	public void testGetMergedPropertiesMap() {
		_activate(true, true);

		// test case1: _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are both setted
		// an empty properties

		Map<String, ObjectValuePair<Properties, Properties>>
			mergedPropertiesMap = ReflectionTestUtil.invoke(
				_multiVMEhcachePortalCacheManagerConfigurator,
				"_getMergedPropertiesMap", null, null);

		Assert.assertTrue(MapUtil.isEmpty(mergedPropertiesMap));

		// test case2: _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are both setted
		// a non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		Set<String> keySet = mergedPropertiesMap.keySet();

		Assert.assertTrue(
			keySet.containsAll(
				PropsInvocationHandler.
					EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1.keySet()));
		Assert.assertTrue(
			keySet.containsAll(
				PropsInvocationHandler.
					EHCACHE_REPLICATOR_PROPERTIES_VALUE1.keySet()));

		Set<Map.Entry<String, ObjectValuePair<Properties, Properties>>>
			entrySet = mergedPropertiesMap.entrySet();

		Assert.assertEquals(entrySet.toString(), 3, entrySet.size());

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				entrySet) {

			String key = entry.getKey();

			ObjectValuePair objectValuePair = entry.getValue();

			Properties keyProperties = (Properties)objectValuePair.getKey();

			String bootstrapLoaderPropertiesValue =
				PropsInvocationHandler.
					EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1.
						getProperty(key);

			Assert.assertEquals(
				"", keyProperties.getProperty(bootstrapLoaderPropertiesValue));

			Properties valueProperties = (Properties)objectValuePair.getValue();
			String replicatorPropertiesValue =
				PropsInvocationHandler.
					EHCACHE_REPLICATOR_PROPERTIES_VALUE1.getProperty(key);

			Assert.assertEquals(
				"", valueProperties.getProperty(replicatorPropertiesValue));

			Assert.assertTrue((boolean)valueProperties.get("replicator"));
		}

		// test case3: _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties is setted an empty properties,
		// _replicatorProperties is setted a non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		keySet = mergedPropertiesMap.keySet();

		Assert.assertTrue(
			keySet.containsAll(
				PropsInvocationHandler.
					EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE.keySet()));
		Assert.assertTrue(
			keySet.containsAll(
				PropsInvocationHandler.
					EHCACHE_REPLICATOR_PROPERTIES_VALUE1.keySet()));

		entrySet = mergedPropertiesMap.entrySet();

		Assert.assertEquals(entrySet.toString(), 3, entrySet.size());

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				entrySet) {

			String key = entry.getKey();

			ObjectValuePair objectValuePair = entry.getValue();

			Properties keyProperties = (Properties)objectValuePair.getKey();

			Assert.assertNull(keyProperties);

			Properties valueProperties = (Properties)objectValuePair.getValue();
			String replicatorPropertiesValue =
				PropsInvocationHandler.
					EHCACHE_REPLICATOR_PROPERTIES_VALUE1.getProperty(key);

			Assert.assertEquals(
				"", valueProperties.getProperty(replicatorPropertiesValue));

			Assert.assertTrue((boolean)valueProperties.get("replicator"));
		}

		// test case4: _bootstrapLoaderEnabled is false,
		// _bootstrapLoaderProperties and _replicatorProperties are both setted
		// a non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderEnabled", false);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		keySet = mergedPropertiesMap.keySet();

		Assert.assertTrue(
			keySet.containsAll(
				PropsInvocationHandler.
					EHCACHE_REPLICATOR_PROPERTIES_VALUE1.keySet()));

		entrySet = mergedPropertiesMap.entrySet();

		Assert.assertEquals(entrySet.toString(), 3, entrySet.size());

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				entrySet) {

			String key = entry.getKey();

			ObjectValuePair objectValuePair = entry.getValue();

			Properties keyProperties = (Properties)objectValuePair.getKey();

			Assert.assertNull(keyProperties);

			Properties valueProperties = (Properties)objectValuePair.getValue();
			String replicatorPropertiesValue =
				PropsInvocationHandler.
					EHCACHE_REPLICATOR_PROPERTIES_VALUE1.getProperty(key);

			Assert.assertEquals(
				"", valueProperties.getProperty(replicatorPropertiesValue));

			Assert.assertTrue((boolean)valueProperties.get("replicator"));
		}
	}

	@Test
	public void testGetPortalPropertiesString() {
		_activate(true, true);

		String portalPropertiesString =
			_multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString(
					PropsInvocationHandler.PORTAL_PROPERTY_KEY0);

		Assert.assertNull(portalPropertiesString);

		portalPropertiesString =
			_multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString(
					PropsInvocationHandler.PORTAL_PROPERTY_KEY1);

		Assert.assertSame(
			PropsInvocationHandler.ARRAY_1[0], portalPropertiesString);

		portalPropertiesString =
			_multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString(
					PropsInvocationHandler.PORTAL_PROPERTY_KEY2);

		Assert.assertEquals(
			StringUtil.merge(PropsInvocationHandler.ARRAY_2, StringPool.COMMA),
			portalPropertiesString);
	}

	@Test
	public void testIsRequireSerialization() {
		_activate(true, true);

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		Assert.assertTrue(
			_multiVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));

		_activate(false, true);

		Assert.assertFalse(
			_multiVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));
	}

	@Test
	public void testManageConfiguration() {
		_activate(false, true);

		// test case1: clusterEnabled is false

		Configuration configuration = new Configuration();

		final AtomicBoolean calledGetDefaultPortalCacheConfiguration =
			new AtomicBoolean(false);

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			new PortalCacheManagerConfiguration(null, null, null) {

				@Override
				public PortalCacheConfiguration
					getDefaultPortalCacheConfiguration() {

					calledGetDefaultPortalCacheConfiguration.set(true);

					return null;
				}

			};

		_multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		Assert.assertFalse(calledGetDefaultPortalCacheConfiguration.get());

		// test case2: clusterEnabled is true, mergedPropertiesMap is empty

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator, "clusterEnabled",
			true);

		final AtomicInteger countGetDefaultPortalCacheConfiguration =
			new AtomicInteger(0);

		portalCacheManagerConfiguration =
			new PortalCacheManagerConfiguration(null, null, null) {

				@Override
				public PortalCacheConfiguration
					getDefaultPortalCacheConfiguration() {

					countGetDefaultPortalCacheConfiguration.addAndGet(1);

					return null;
				}

			};

		_multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		Assert.assertEquals(1, countGetDefaultPortalCacheConfiguration.get());

		// test case3: clusterEnabled is true, _bootstrapLoaderProperties and
		// _replicatorProperties are both setted a non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		Set<Properties> portalCacheListenerPropertiesSet = new HashSet<>();

		Properties properties = new Properties();

		properties.put(PortalCacheReplicator.REPLICATOR, true);

		portalCacheListenerPropertiesSet.add(properties);

		properties = new Properties();

		properties.put(PortalCacheReplicator.REPLICATOR, false);

		portalCacheListenerPropertiesSet.add(properties);

		PortalCacheConfiguration defaultPortalCacheConfiguration =
			new PortalCacheConfiguration(
				PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT,
				portalCacheListenerPropertiesSet, null);

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null, defaultPortalCacheConfiguration, null);

		_multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		Map<String, ObjectValuePair<Properties, Properties>>
			mergedPropertiesMap = ReflectionTestUtil.invoke(
				_multiVMEhcachePortalCacheManagerConfigurator,
				"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			String portalCacheName = entry.getKey();

			PortalCacheConfiguration portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName);

			ObjectValuePair<Properties, Properties> propertiesPair =
				entry.getValue();

			Assert.assertEquals(
				propertiesPair.getKey(),
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties());

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(
				portalCacheListenerPropertiesSet.contains(
					propertiesPair.getValue()));

			properties = propertiesPair.getValue();

			Assert.assertTrue(
				(boolean)properties.get(PortalCacheReplicator.REPLICATOR));

			Iterator<Properties> itr =
				portalCacheListenerPropertiesSet.iterator();

			int count = 0;

			while (itr.hasNext()) {
				properties = itr.next();

				Object value = properties.get(PortalCacheReplicator.REPLICATOR);

				if ((value != null) && (Boolean)value) {
					count++;
				}
			}

			Assert.assertEquals(1, count);
		}

		// test case4: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties is setted an empty properties,
		// _replicatorProperties is setted a non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null, defaultPortalCacheConfiguration, null);

		_multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			String portalCacheName = entry.getKey();

			PortalCacheConfiguration portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName);

			ObjectValuePair<Properties, Properties> propertiesPair =
				entry.getValue();

			Assert.assertNull(propertiesPair.getKey());

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(
				portalCacheListenerPropertiesSet.contains(
					propertiesPair.getValue()));

			properties = propertiesPair.getValue();

			Assert.assertTrue(
				(boolean)properties.get(PortalCacheReplicator.REPLICATOR));
		}

		// test case5: clusterEnabled is true, _bootstrapLoaderEnabled is false,
		// _bootstrapLoaderProperties is setted an empty properties,
		// _replicatorProperties is setted a non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderEnabled", false);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null, defaultPortalCacheConfiguration, null);

		_multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			String portalCacheName = entry.getKey();

			PortalCacheConfiguration portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName);

			ObjectValuePair<Properties, Properties> propertiesPair =
				entry.getValue();

			Assert.assertNull(propertiesPair.getKey());

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(
				portalCacheListenerPropertiesSet.contains(
					propertiesPair.getValue()));

			properties = propertiesPair.getValue();

			Assert.assertTrue(
				(boolean)properties.get(PortalCacheReplicator.REPLICATOR));
		}

		// test case6: clusterEnabled is true, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties is setted a non-empty properties,
		// _replicatorProperties is setted an empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderEnabled", true);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE);

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null, defaultPortalCacheConfiguration, null);

		_multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			String portalCacheName = entry.getKey();

			PortalCacheConfiguration portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName);

			ObjectValuePair<Properties, Properties> propertiesPair =
				entry.getValue();

			Assert.assertEquals(
				propertiesPair.getKey(),
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties());

			Assert.assertNull(propertiesPair.getValue());
		}

		// test case6: clusterEnabled is true, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are both setted
		// a non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderEnabled", true);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		final AtomicBoolean calledNewPortalCacheConfiguration =
			new AtomicBoolean(false);

		defaultPortalCacheConfiguration = new PortalCacheConfiguration(
			PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT,
			portalCacheListenerPropertiesSet, null) {

			@Override
			public PortalCacheConfiguration newPortalCacheConfiguration(
				String portalCacheName) {

				calledNewPortalCacheConfiguration.set(true);

				return null;
			}

		};

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null, defaultPortalCacheConfiguration, null);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			String portalCacheName = entry.getKey();

			PortalCacheConfiguration portalCacheConfiguration =
				new PortalCacheConfiguration(
					portalCacheName, portalCacheListenerPropertiesSet, null);

			portalCacheManagerConfiguration.putPortalCacheConfiguration(
				portalCacheName, portalCacheConfiguration);
		}

		_multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		Assert.assertFalse(calledNewPortalCacheConfiguration.get());
	}

	@Test
	public void testParseCacheListenerConfigurations() {
		_activate(false, true);

		// test case1: clusterEnabled is false, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are setted an
		// empty properties

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName("TestName");

		PortalCacheConfiguration portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());
		Assert.assertNull(
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());
		Assert.assertTrue(
			SetUtil.isEmpty(
				portalCacheConfiguration.
					getPortalCacheListenerPropertiesSet()));

		// test case2: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties and _replicatorProperties are setted an
		// empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator, "clusterEnabled",
			true);

		portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());

		Properties bootstrapLoaderProperties =
			_multiVMEhcachePortalCacheManagerConfigurator.parseProperties(
				StringUtil.merge(
					PropsInvocationHandler.
						EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_DEFAULT_VALUE,
					StringPool.COMMA),
				StringPool.COMMA);

		Assert.assertEquals(
			bootstrapLoaderProperties,
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		Properties replicatorProperties =
			_multiVMEhcachePortalCacheManagerConfigurator.parseProperties(
				StringUtil.merge(
					PropsInvocationHandler.
						EHCACHE_REPLICATOR_PROPERTIES_DEFAULT_VALUE,
					StringPool.COMMA),
				StringPool.COMMA);
		Set<Properties> portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		for (Properties properties : portalCacheListenerPropertiesSet) {
			for (Object key : replicatorProperties.keySet()) {
				Object replicatorValue = replicatorProperties.get(key);
				Object value = properties.get(key);

				Assert.assertEquals(replicatorValue, value);
			}

			Assert.assertTrue(
				(Boolean)properties.get(PortalCacheReplicator.REPLICATOR));
		}

		// test case3: clusterEnabled is true, _bootstrapLoaderEnabled is false,
		// _bootstrapLoaderProperties and _replicatorProperties are setted an
		// empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderEnabled", false);

		portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());

		Assert.assertNull(
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		replicatorProperties =
			_multiVMEhcachePortalCacheManagerConfigurator.parseProperties(
				StringUtil.merge(
					PropsInvocationHandler.
						EHCACHE_REPLICATOR_PROPERTIES_DEFAULT_VALUE,
					StringPool.COMMA),
				StringPool.COMMA);
		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		for (Properties properties : portalCacheListenerPropertiesSet) {
			for (Object key : replicatorProperties.keySet()) {
				Object replicatorValue = replicatorProperties.get(key);
				Object value = properties.get(key);

				Assert.assertEquals(replicatorValue, value);
			}

			Assert.assertTrue(
				(Boolean)properties.get(PortalCacheReplicator.REPLICATOR));
		}

		// test case4: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties and _replicatorProperties are setted a
		// non-empty properties

		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderEnabled", true);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties",
			PropsInvocationHandler.
				EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES_VALUE1);
		ReflectionTestUtil.setFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties",
			PropsInvocationHandler.EHCACHE_REPLICATOR_PROPERTIES_VALUE1);

		cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName("name1");

		portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		bootstrapLoaderProperties = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties");
		replicatorProperties = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties");

		Assert.assertNull(bootstrapLoaderProperties.get("name1"));
		Assert.assertNull(replicatorProperties.get("name1"));
		Assert.assertEquals(
			"name1", portalCacheConfiguration.getPortalCacheName());

		Properties portalCacheBootstrapLoaderProperties =
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties();

		Assert.assertEquals(
			"", portalCacheBootstrapLoaderProperties.get("value1"));

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		for (Properties properties : portalCacheListenerPropertiesSet) {
			Assert.assertEquals("", properties.get("value1"));

			Assert.assertTrue(
				(Boolean)properties.get(PortalCacheReplicator.REPLICATOR));
		}
	}

	@Test
	public void testSetProps() {
		Props proxyProps = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(true, true));

		_multiVMEhcachePortalCacheManagerConfigurator.setProps(proxyProps);

		Props proxy = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator, "props");

		Assert.assertSame(proxyProps, proxy);
	}

	private void _activate(
		boolean clusterEnabled, boolean bootstrapLoaderEnabled) {

		Props proxyProps = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(clusterEnabled, bootstrapLoaderEnabled));

		_multiVMEhcachePortalCacheManagerConfigurator.setProps(proxyProps);

		_multiVMEhcachePortalCacheManagerConfigurator.activate();
	}

	private static final ClassLoader _classLoader =
		MultiVMEhcachePortalCacheManagerConfiguratorTest.class.getClassLoader();

	private MultiVMEhcachePortalCacheManagerConfigurator
		_multiVMEhcachePortalCacheManagerConfigurator;

}