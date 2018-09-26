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
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
		_activate(true, true, true, true);

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
		Assert.assertTrue(clusterEnabled);
		Assert.assertEquals(new Properties(), bootstrapLoaderProperties);
		Assert.assertEquals(
			"key1=value1,key2=value2", defaultBootstrapLoaderPropertiesString);
		Assert.assertEquals(
			"key1=value1,key2=value2", defaultReplicatorPropertiesString);
		Assert.assertEquals(new Properties(), replicatorProperties);
	}

	@Test
	public void testGetMergedPropertiesMap() {

		// Test 1: _bootstrapLoaderEnabled is true, _bootstrapLoaderProperties
		// and _replicatorProperties are empty

		_activate(true, true, true, true);

		Map<String, ObjectValuePair<Properties, Properties>>
			mergedPropertiesMap = ReflectionTestUtil.invoke(
				_multiVMEhcachePortalCacheManagerConfigurator,
				"_getMergedPropertiesMap", null, null);

		Assert.assertTrue(mergedPropertiesMap.isEmpty());

		// Test 2: _bootstrapLoaderEnabled is true, _bootstrapLoaderProperties
		// and _replicatorProperties are non-empty

		_activate(true, true, false, false);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		Set<String> keySet = mergedPropertiesMap.keySet();

		Properties exceptProperties = new Properties();

		exceptProperties.setProperty("portalCacheName1", "key1=value1");
		exceptProperties.setProperty("portalCacheName2X", "key2X=value2X");
		exceptProperties.setProperty("portalCacheName2Y", "key2Y=value2Y");

		Assert.assertTrue(keySet.containsAll(exceptProperties.keySet()));

		Assert.assertEquals(keySet.toString(), 3, keySet.size());

		ObjectValuePair objectValuePair = mergedPropertiesMap.get(
			"portalCacheName1");

		exceptProperties = new Properties();

		exceptProperties.put("key1", "value1");

		Properties bootstrapLoaderProperties =
			(Properties)objectValuePair.getKey();

		Assert.assertEquals(exceptProperties, bootstrapLoaderProperties);

		exceptProperties.put("replicator", true);

		Properties replicatorProperties =
			(Properties)objectValuePair.getValue();

		Assert.assertEquals(exceptProperties, replicatorProperties);

		objectValuePair = mergedPropertiesMap.get("portalCacheName2X");

		exceptProperties = new Properties();

		exceptProperties.put("key2X", "value2X");

		bootstrapLoaderProperties = (Properties)objectValuePair.getKey();

		Assert.assertEquals(exceptProperties, bootstrapLoaderProperties);

		replicatorProperties = (Properties)objectValuePair.getValue();

		Assert.assertNull(replicatorProperties);

		objectValuePair = mergedPropertiesMap.get("portalCacheName2Y");

		bootstrapLoaderProperties = (Properties)objectValuePair.getKey();

		Assert.assertNull(bootstrapLoaderProperties);

		exceptProperties = new Properties();

		exceptProperties.put("key2Y", "value2Y");
		exceptProperties.put("replicator", true);

		replicatorProperties = (Properties)objectValuePair.getValue();

		Assert.assertEquals(exceptProperties, replicatorProperties);

		// Test 3: _bootstrapLoaderEnabled is false, _bootstrapLoaderProperties
		// and _replicatorProperties are non-empty

		_activate(true, false, false, false);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		keySet = mergedPropertiesMap.keySet();

		exceptProperties = new Properties();

		exceptProperties.setProperty("portalCacheName1", "key1=value1");
		exceptProperties.setProperty("portalCacheName2Y", "key2Y=value2Y");

		Assert.assertTrue(keySet.containsAll(exceptProperties.keySet()));

		Assert.assertEquals(keySet.toString(), 2, keySet.size());

		objectValuePair = mergedPropertiesMap.get("portalCacheName1");

		bootstrapLoaderProperties = (Properties)objectValuePair.getKey();

		Assert.assertNull(bootstrapLoaderProperties);

		exceptProperties = new Properties();

		exceptProperties.put("key1", "value1");
		exceptProperties.put("replicator", true);

		replicatorProperties = (Properties)objectValuePair.getValue();

		Assert.assertEquals(exceptProperties, replicatorProperties);

		objectValuePair = mergedPropertiesMap.get("portalCacheName2Y");

		bootstrapLoaderProperties = (Properties)objectValuePair.getKey();

		Assert.assertNull(bootstrapLoaderProperties);

		exceptProperties = new Properties();

		exceptProperties.put("key2Y", "value2Y");
		exceptProperties.put("replicator", true);

		replicatorProperties = (Properties)objectValuePair.getValue();

		Assert.assertEquals(exceptProperties, replicatorProperties);
	}

	@Test
	public void testGetPortalPropertiesString() {
		_activate(true, true, true, true);

		String portalPropertiesString =
			_multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString("portal.property.Key0");

		Assert.assertNull(portalPropertiesString);

		portalPropertiesString =
			_multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString("portal.property.Key1");

		Assert.assertSame("key=value", portalPropertiesString);

		portalPropertiesString =
			_multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString("portal.property.Key2");

		Assert.assertEquals("key1=value1,key2=value2", portalPropertiesString);
	}

	@Test
	public void testIsRequireSerialization() {
		_activate(true, true, true, true);

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		Assert.assertTrue(
			_multiVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));

		_activate(false, true, true, true);

		Assert.assertFalse(
			_multiVMEhcachePortalCacheManagerConfigurator.
				isRequireSerialization(cacheConfiguration));
	}

	@Test
	public void testManageConfiguration() {
		_activate(false, true, true, true);

		// Test 1: clusterEnabled is false

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

		// Test 2: clusterEnabled is true, _bootstrapLoaderProperties and
		// _replicatorProperties are non-empty

		_activate(true, true, false, false);

		Set<Properties> portalCacheListenerPropertiesSet = new HashSet<>();

		Properties properties1 = new Properties();

		properties1.put(PortalCacheReplicator.REPLICATOR, true);

		portalCacheListenerPropertiesSet.add(properties1);

		Properties properties2 = new Properties();

		properties2.put(PortalCacheReplicator.REPLICATOR, false);

		portalCacheListenerPropertiesSet.add(properties2);

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

		PortalCacheConfiguration portalCacheConfiguration =
			portalCacheManagerConfiguration.getPortalCacheConfiguration(
				"portalCacheName1");

		ObjectValuePair<Properties, Properties> propertiesPair =
			mergedPropertiesMap.get("portalCacheName1");

		Assert.assertEquals(
			propertiesPair.getKey(),
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertEquals(
			portalCacheListenerPropertiesSet.toString(), 2,
			portalCacheListenerPropertiesSet.size());
		Assert.assertFalse(
			portalCacheListenerPropertiesSet.contains(properties1));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.contains(
				propertiesPair.getValue()));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.contains(properties2));

		portalCacheConfiguration =
			portalCacheManagerConfiguration.getPortalCacheConfiguration(
				"portalCacheName2X");

		propertiesPair = mergedPropertiesMap.get("portalCacheName2X");

		Assert.assertEquals(
			propertiesPair.getKey(),
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertEquals(
			portalCacheListenerPropertiesSet.toString(), 2,
			portalCacheListenerPropertiesSet.size());
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.contains(properties1));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.contains(properties2));

		portalCacheConfiguration =
			portalCacheManagerConfiguration.getPortalCacheConfiguration(
				"portalCacheName2Y");

		propertiesPair = mergedPropertiesMap.get("portalCacheName2Y");

		Assert.assertNull(
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertEquals(
			portalCacheListenerPropertiesSet.toString(), 2,
			portalCacheListenerPropertiesSet.size());
		Assert.assertFalse(
			portalCacheListenerPropertiesSet.contains(properties1));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.contains(
				propertiesPair.getValue()));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.contains(properties2));

		// Test 3: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties is empty, _replicatorProperties is
		// non-empty

		_activate(true, true, true, false);

		defaultPortalCacheConfiguration = new PortalCacheConfiguration(
			PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT, null, null);

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

			portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName);

			Properties portalCacheBootstrapLoaderProperties =
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties();

			Assert.assertNull(portalCacheBootstrapLoaderProperties);

			propertiesPair = entry.getValue();

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(
				portalCacheListenerPropertiesSet.contains(
					propertiesPair.getValue()));
		}

		// Test 4: clusterEnabled is true, _bootstrapLoaderEnabled is false,
		// _bootstrapLoaderProperties is empty, _replicatorProperties is
		// non-empty

		_activate(true, false, true, false);

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

			portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName);

			Properties portalCacheBootstrapLoaderProperties =
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties();

			Assert.assertNull(portalCacheBootstrapLoaderProperties);

			propertiesPair = entry.getValue();

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(
				portalCacheListenerPropertiesSet.contains(
					propertiesPair.getValue()));
		}

		// Test 5: clusterEnabled is true, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties is non-empty, _replicatorProperties is
		// empty

		_activate(true, true, false, true);

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

			portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					portalCacheName);

			propertiesPair = entry.getValue();

			Assert.assertEquals(
				propertiesPair.getKey(),
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties());

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(portalCacheListenerPropertiesSet.isEmpty());
		}

		// Test 6: clusterEnabled is true, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are non-empty

		_activate(true, true, false, false);

		final AtomicBoolean calledNewPortalCacheConfiguration =
			new AtomicBoolean(false);

		portalCacheListenerPropertiesSet = new HashSet<>();

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

			portalCacheConfiguration = new PortalCacheConfiguration(
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

		// Test 1: clusterEnabled is false, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are empty

		_activate(false, true, true, true);

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName("TestName");

		PortalCacheConfiguration portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());
		Assert.assertNull(
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		Set<Properties> portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertTrue(portalCacheListenerPropertiesSet.isEmpty());

		// Test 2: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties and _replicatorProperties are empty

		_activate(true, true, true, true);

		portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());

		Properties expectProperties = new Properties();

		expectProperties.put("key1", "value1");
		expectProperties.put("key2", "value2");

		Assert.assertEquals(
			expectProperties,
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		for (Properties replicatorProperties :
				portalCacheListenerPropertiesSet) {

			for (Object key : expectProperties.keySet()) {
				Object expectValue = expectProperties.get(key);
				Object resultValue = replicatorProperties.get(key);

				Assert.assertEquals(expectValue, resultValue);
			}

			Assert.assertTrue(
				(Boolean)replicatorProperties.get(
					PortalCacheReplicator.REPLICATOR));
		}

		// Test 3: clusterEnabled is true, _bootstrapLoaderEnabled is false,
		// _bootstrapLoaderProperties and _replicatorProperties are empty

		_activate(true, false, true, true);

		portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());
		Assert.assertNull(
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		for (Properties replicatorProperties :
				portalCacheListenerPropertiesSet) {

			for (Object key : expectProperties.keySet()) {
				Object expectValue = expectProperties.get(key);
				Object resultValue = replicatorProperties.get(key);

				Assert.assertEquals(expectValue, resultValue);
			}

			Assert.assertTrue(
				(Boolean)replicatorProperties.get(
					PortalCacheReplicator.REPLICATOR));
		}

		// Test 4: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties and _replicatorProperties are non-empty

		_activate(true, true, false, false);

		cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName("portalCacheName1");

		portalCacheConfiguration =
			_multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Properties bootstrapLoaderProperties = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties");
		Properties replicatorProperties = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties");

		Assert.assertNull(bootstrapLoaderProperties.get("portalCacheName1"));
		Assert.assertNull(replicatorProperties.get("portalCacheName1"));
		Assert.assertEquals(
			"portalCacheName1", portalCacheConfiguration.getPortalCacheName());

		Properties portalCacheBootstrapLoaderProperties =
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties();

		Assert.assertEquals(
			"value1", portalCacheBootstrapLoaderProperties.get("key1"));

		portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		for (Properties properties : portalCacheListenerPropertiesSet) {
			Assert.assertEquals("value1", properties.get("key1"));
			Assert.assertTrue(
				(Boolean)properties.get(PortalCacheReplicator.REPLICATOR));
		}
	}

	@Test
	public void testSetProps() {
		Props props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(true, true, true, true));

		_multiVMEhcachePortalCacheManagerConfigurator.setProps(props);

		Props proxy = ReflectionTestUtil.getFieldValue(
			_multiVMEhcachePortalCacheManagerConfigurator, "props");

		Assert.assertSame(props, proxy);
	}

	private void _activate(
		boolean clusterEnabled, boolean bootstrapLoaderEnabled,
		boolean bootstrapLoaderPropertiesIsEmpty,
		boolean replicatorPropertiesIsEmpty) {

		Props props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(
				clusterEnabled, bootstrapLoaderEnabled,
				bootstrapLoaderPropertiesIsEmpty, replicatorPropertiesIsEmpty));

		_multiVMEhcachePortalCacheManagerConfigurator.setProps(props);

		_multiVMEhcachePortalCacheManagerConfigurator.activate();
	}

	private static final ClassLoader _classLoader =
		MultiVMEhcachePortalCacheManagerConfiguratorTest.class.getClassLoader();

	private MultiVMEhcachePortalCacheManagerConfigurator
		_multiVMEhcachePortalCacheManagerConfigurator;

	private class PropsInvocationHandler implements InvocationHandler {

		public PropsInvocationHandler(
			boolean clusterEnabled, boolean bootstrapLoaderEnabled,
			boolean bootstrapLoaderPropertiesIsEmpty,
			boolean replicatorPropertiesIsEmpty) {

			_clusterEnabled = clusterEnabled;
			_bootstrapLoaderEnabled = bootstrapLoaderEnabled;
			_bootstrapLoaderPropertiesIsEmpty =
				bootstrapLoaderPropertiesIsEmpty;
			_replicatorPropertiesIsEmpty = replicatorPropertiesIsEmpty;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			String methodName = method.getName();

			if (methodName.equals("get")) {
				String key = (String)args[0];

				if (PropsKeys.CLUSTER_LINK_ENABLED.equals(key)) {
					return String.valueOf(_clusterEnabled);
				}

				if (PropsKeys.EHCACHE_BOOTSTRAP_CACHE_LOADER_ENABLED.equals(
						key)) {

					return String.valueOf(_bootstrapLoaderEnabled);
				}
			}

			if (methodName.equals("getArray")) {
				String key = (String)args[0];

				if ("portal.property.Key0".equals(key)) {
					return new String[0];
				}

				if ("portal.property.Key1".equals(key)) {
					return new String[] {"key=value"};
				}

				return new String[] {"key1=value1", "key2=value2"};
			}

			if (methodName.equals("getProperties")) {
				String key = (String)args[0];

				Properties properties = new Properties();

				if (key.equals(
						PropsKeys.EHCACHE_BOOTSTRAP_CACHE_LOADER_PROPERTIES +
							StringPool.PERIOD)) {

					if (!_bootstrapLoaderPropertiesIsEmpty) {
						properties.setProperty(
							"portalCacheName1", "key1=value1");
						properties.setProperty(
							"portalCacheName2X", "key2X=value2X");
					}
				}

				if (key.equals(
						PropsKeys.EHCACHE_REPLICATOR_PROPERTIES +
							StringPool.PERIOD)) {

					if (!_replicatorPropertiesIsEmpty) {
						properties.setProperty(
							"portalCacheName1", "key1=value1");
						properties.setProperty(
							"portalCacheName2Y", "key2Y=value2Y");
					}
				}

				return properties;
			}

			return null;
		}

		private final boolean _bootstrapLoaderEnabled;
		private final boolean _bootstrapLoaderPropertiesIsEmpty;
		private final boolean _clusterEnabled;
		private final boolean _replicatorPropertiesIsEmpty;

	}

}