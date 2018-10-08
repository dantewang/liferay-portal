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
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class MultiVMEhcachePortalCacheManagerConfiguratorTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testActivate() {
		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator =
				_getMultiVMEhcachePortalCacheManagerConfigurator(
					true, true, true, true);

		Assert.assertTrue(
			"The _bootstrapLoaderEnabled should be true if props.get(" +
				"PropsKeys.EHCACHE_BOOTSTRAP_CACHE_LOADER_ENABLED) return true",
			(Boolean)ReflectionTestUtil.getFieldValue(
				multiVMEhcachePortalCacheManagerConfigurator,
				"_bootstrapLoaderEnabled"));
		Assert.assertTrue(
			"The clusterEnabled should be true if props.get(PropsKeys." +
				"CLUSTER_LINK_ENABLED) return true",
			(Boolean)ReflectionTestUtil.getFieldValue(
				multiVMEhcachePortalCacheManagerConfigurator,
				"clusterEnabled"));
		Assert.assertEquals(
			new Properties(),
			ReflectionTestUtil.getFieldValue(
				multiVMEhcachePortalCacheManagerConfigurator,
				"_bootstrapLoaderProperties"));
		Assert.assertEquals(
			"key1=value1,key2=value2",
			ReflectionTestUtil.getFieldValue(
				multiVMEhcachePortalCacheManagerConfigurator,
				"_defaultBootstrapLoaderPropertiesString"));
		Assert.assertEquals(
			"key1=value1,key2=value2",
			ReflectionTestUtil.getFieldValue(
				multiVMEhcachePortalCacheManagerConfigurator,
				"_defaultReplicatorPropertiesString"));
		Assert.assertEquals(
			new Properties(),
			ReflectionTestUtil.getFieldValue(
				multiVMEhcachePortalCacheManagerConfigurator,
				"_replicatorProperties"));
	}

	@Test
	public void testGetMergedPropertiesMap() {

		// Test 1: _bootstrapLoaderEnabled is true, _bootstrapLoaderProperties
		// and _replicatorProperties are empty

		Map<String, ObjectValuePair<Properties, Properties>>
			mergedPropertiesMap = ReflectionTestUtil.invoke(
				_getMultiVMEhcachePortalCacheManagerConfigurator(
					true, true, true, true),
				"_getMergedPropertiesMap", null, null);

		Assert.assertTrue(
			mergedPropertiesMap.toString(), mergedPropertiesMap.isEmpty());

		// Test 2: _bootstrapLoaderEnabled is true, _bootstrapLoaderProperties
		// and _replicatorProperties are non-empty

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, true, false, false),
			"_getMergedPropertiesMap", null, null);

		Set<String> keySet = mergedPropertiesMap.keySet();

		Properties exceptProperties = new Properties();

		exceptProperties.setProperty("portalCacheName1", "key1=value1");
		exceptProperties.setProperty("portalCacheName2X", "key2X=value2X");
		exceptProperties.setProperty("portalCacheName2Y", "key2Y=value2Y");

		Assert.assertTrue(
			keySet.toString(), keySet.containsAll(exceptProperties.keySet()));

		Assert.assertEquals(keySet.toString(), 3, keySet.size());

		ObjectValuePair objectValuePair = mergedPropertiesMap.get(
			"portalCacheName1");

		exceptProperties = new Properties();

		exceptProperties.put("key1", "value1");

		Assert.assertEquals(exceptProperties, objectValuePair.getKey());

		exceptProperties.put("replicator", true);

		Assert.assertEquals(exceptProperties, objectValuePair.getValue());

		objectValuePair = mergedPropertiesMap.get("portalCacheName2X");

		exceptProperties = new Properties();

		exceptProperties.put("key2X", "value2X");

		Assert.assertEquals(exceptProperties, objectValuePair.getKey());

		Assert.assertNull(objectValuePair.getValue());

		objectValuePair = mergedPropertiesMap.get("portalCacheName2Y");

		Assert.assertNull(objectValuePair.getKey());

		exceptProperties = new Properties();

		exceptProperties.put("key2Y", "value2Y");
		exceptProperties.put("replicator", true);

		Assert.assertEquals(exceptProperties, objectValuePair.getValue());

		// Test 3: _bootstrapLoaderEnabled is false, _bootstrapLoaderProperties
		// and _replicatorProperties are non-empty

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, false, false, false),
			"_getMergedPropertiesMap", null, null);

		keySet = mergedPropertiesMap.keySet();

		exceptProperties = new Properties();

		exceptProperties.setProperty("portalCacheName1", "key1=value1");
		exceptProperties.setProperty("portalCacheName2Y", "key2Y=value2Y");

		Assert.assertTrue(
			keySet.toString(), keySet.containsAll(exceptProperties.keySet()));

		Assert.assertEquals(keySet.toString(), 2, keySet.size());

		objectValuePair = mergedPropertiesMap.get("portalCacheName1");

		Assert.assertNull(objectValuePair.getKey());

		exceptProperties = new Properties();

		exceptProperties.put("key1", "value1");
		exceptProperties.put("replicator", true);

		Assert.assertEquals(exceptProperties, objectValuePair.getValue());

		objectValuePair = mergedPropertiesMap.get("portalCacheName2Y");

		Assert.assertNull(objectValuePair.getKey());

		exceptProperties = new Properties();

		exceptProperties.put("key2Y", "value2Y");
		exceptProperties.put("replicator", true);

		Assert.assertEquals(exceptProperties, objectValuePair.getValue());
	}

	@Test
	public void testGetPortalPropertiesString() {
		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator =
				_getMultiVMEhcachePortalCacheManagerConfigurator(
					true, true, true, true);

		Assert.assertNull(
			multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString("portal.property.Key0"));

		Assert.assertSame(
			"key=value",
			multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString("portal.property.Key1"));

		Assert.assertEquals(
			"key1=value1,key2=value2",
			multiVMEhcachePortalCacheManagerConfigurator.
				getPortalPropertiesString("portal.property.Key2"));
	}

	@Test
	public void testIsRequireSerialization() {
		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator =
				_getMultiVMEhcachePortalCacheManagerConfigurator(
					true, true, true, true);

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		Assert.assertTrue(
			"The true value should be returned",
			multiVMEhcachePortalCacheManagerConfigurator.isRequireSerialization(
				cacheConfiguration));

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				false, true, true, true);

		Assert.assertFalse(
			"The false value should be returned",
			multiVMEhcachePortalCacheManagerConfigurator.isRequireSerialization(
				cacheConfiguration));
	}

	@Test
	public void testManageConfiguration() {
		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator =
				_getMultiVMEhcachePortalCacheManagerConfigurator(
					false, true, true, true);

		// Test 1: clusterEnabled is false

		Configuration configuration = new Configuration();

		final AtomicBoolean calledGetDefaultPortalCacheConfiguration =
			new AtomicBoolean(false);

		multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration,
			new PortalCacheManagerConfiguration(null, null, null) {

				@Override
				public PortalCacheConfiguration
					getDefaultPortalCacheConfiguration() {

					calledGetDefaultPortalCacheConfiguration.set(true);

					return null;
				}

			});

		Assert.assertFalse(
			"The method MultiVMEhcachePortalCacheManagerConfigurator." +
				"manageConfiguration(Configuration, PortalCacheManagerConfigu" +
					"ration) should be returned if clusterEnabled is false",
			calledGetDefaultPortalCacheConfiguration.get());

		// Test 2: clusterEnabled is true, _bootstrapLoaderProperties and
		// _replicatorProperties are non-empty

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, true, false, false);

		Set<Properties> portalCacheListenerPropertiesSet = new HashSet<>();

		Properties properties1 = new Properties();

		properties1.put(PortalCacheReplicator.REPLICATOR, true);

		portalCacheListenerPropertiesSet.add(properties1);

		Properties properties2 = new Properties();

		properties2.put(PortalCacheReplicator.REPLICATOR, false);

		portalCacheListenerPropertiesSet.add(properties2);

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			new PortalCacheManagerConfiguration(
				null,
				new PortalCacheConfiguration(
					PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT,
					portalCacheListenerPropertiesSet, null),
				null);

		multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		Map<String, ObjectValuePair<Properties, Properties>>
			mergedPropertiesMap = ReflectionTestUtil.invoke(
				multiVMEhcachePortalCacheManagerConfigurator,
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
			portalCacheListenerPropertiesSet.toString(),
			portalCacheListenerPropertiesSet.contains(properties1));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.toString(),
			portalCacheListenerPropertiesSet.contains(
				propertiesPair.getValue()));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.toString(),
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
			portalCacheListenerPropertiesSet.toString(),
			portalCacheListenerPropertiesSet.contains(properties1));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.toString(),
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
			portalCacheListenerPropertiesSet.toString(),
			portalCacheListenerPropertiesSet.contains(properties1));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.toString(),
			portalCacheListenerPropertiesSet.contains(
				propertiesPair.getValue()));
		Assert.assertTrue(
			portalCacheListenerPropertiesSet.toString(),
			portalCacheListenerPropertiesSet.contains(properties2));

		// Test 3: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties is empty, _replicatorProperties is
		// non-empty

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, true, true, false);

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null,
			new PortalCacheConfiguration(
				PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT, null, null),
			null);

		multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					entry.getKey());

			Assert.assertNull(
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties());

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

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, false, true, false);

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null,
			new PortalCacheConfiguration(
				PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT, null, null),
			null);

		multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					entry.getKey());

			Assert.assertNull(
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties());

			propertiesPair = entry.getValue();

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(
				portalCacheListenerPropertiesSet.toString(),
				portalCacheListenerPropertiesSet.contains(
					propertiesPair.getValue()));
		}

		// Test 5: clusterEnabled is true, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties is non-empty, _replicatorProperties is
		// empty

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, true, false, true);

		portalCacheManagerConfiguration = new PortalCacheManagerConfiguration(
			null,
			new PortalCacheConfiguration(
				PortalCacheConfiguration.PORTAL_CACHE_NAME_DEFAULT, null, null),
			null);

		multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		mergedPropertiesMap = ReflectionTestUtil.invoke(
			multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			portalCacheConfiguration =
				portalCacheManagerConfiguration.getPortalCacheConfiguration(
					entry.getKey());

			propertiesPair = entry.getValue();

			Assert.assertEquals(
				propertiesPair.getKey(),
				portalCacheConfiguration.
					getPortalCacheBootstrapLoaderProperties());

			portalCacheListenerPropertiesSet =
				portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

			Assert.assertTrue(
				portalCacheListenerPropertiesSet.toString(),
				portalCacheListenerPropertiesSet.isEmpty());
		}

		// Test 6: clusterEnabled is true, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are non-empty

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, true, false, false);

		final AtomicBoolean calledNewPortalCacheConfiguration =
			new AtomicBoolean(false);

		portalCacheListenerPropertiesSet = new HashSet<>();

		PortalCacheConfiguration defaultPortalCacheConfiguration =
			new PortalCacheConfiguration(
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
			multiVMEhcachePortalCacheManagerConfigurator,
			"_getMergedPropertiesMap", null, null);

		for (Map.Entry<String, ObjectValuePair<Properties, Properties>> entry :
				mergedPropertiesMap.entrySet()) {

			String portalCacheName = entry.getKey();

			portalCacheConfiguration = new PortalCacheConfiguration(
				portalCacheName, portalCacheListenerPropertiesSet, null);

			portalCacheManagerConfiguration.putPortalCacheConfiguration(
				portalCacheName, portalCacheConfiguration);
		}

		multiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		Assert.assertFalse(
			"The portalCacheConfiguration do not need be created again it " +
				"already exists",
			calledNewPortalCacheConfiguration.get());
	}

	@Test
	public void testParseCacheListenerConfigurations() {

		// Test 1: clusterEnabled is false, _bootstrapLoaderEnabled is true,
		// _bootstrapLoaderProperties and _replicatorProperties are empty

		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator =
				_getMultiVMEhcachePortalCacheManagerConfigurator(
					false, true, true, true);

		CacheConfiguration cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName("TestName");

		PortalCacheConfiguration portalCacheConfiguration =
			multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());
		Assert.assertNull(
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		Set<Properties> portalCacheListenerPropertiesSet =
			portalCacheConfiguration.getPortalCacheListenerPropertiesSet();

		Assert.assertTrue(
			portalCacheListenerPropertiesSet.toString(),
			portalCacheListenerPropertiesSet.isEmpty());

		// Test 2: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties and _replicatorProperties are empty

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, true, true, true);

		portalCacheConfiguration =
			multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());

		Properties expectProperties = new Properties();

		expectProperties.put("key1", "value1");
		expectProperties.put("key2", "value2");

		Assert.assertEquals(
			expectProperties,
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		for (Properties replicatorProperties :
				portalCacheConfiguration.
					getPortalCacheListenerPropertiesSet()) {

			for (Object key : expectProperties.keySet()) {
				Assert.assertEquals(
					expectProperties.get(key), replicatorProperties.get(key));
			}

			Assert.assertTrue(
				"The value should be true",
				(Boolean)replicatorProperties.get(
					PortalCacheReplicator.REPLICATOR));
		}

		// Test 3: clusterEnabled is true, _bootstrapLoaderEnabled is false,
		// _bootstrapLoaderProperties and _replicatorProperties are empty

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, false, true, true);

		portalCacheConfiguration =
			multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Assert.assertEquals(
			"TestName", portalCacheConfiguration.getPortalCacheName());
		Assert.assertNull(
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties());

		for (Properties replicatorProperties :
				portalCacheConfiguration.
					getPortalCacheListenerPropertiesSet()) {

			for (Object key : expectProperties.keySet()) {
				Assert.assertEquals(
					expectProperties.get(key), replicatorProperties.get(key));
			}

			Assert.assertTrue(
				"The value should be true",
				(Boolean)replicatorProperties.get(
					PortalCacheReplicator.REPLICATOR));
		}

		// Test 4: clusterEnabled and _bootstrapLoaderEnabled are true,
		// _bootstrapLoaderProperties and _replicatorProperties are non-empty

		multiVMEhcachePortalCacheManagerConfigurator =
			_getMultiVMEhcachePortalCacheManagerConfigurator(
				true, true, false, false);

		cacheConfiguration = new CacheConfiguration();

		cacheConfiguration.setName("portalCacheName1");

		portalCacheConfiguration =
			multiVMEhcachePortalCacheManagerConfigurator.
				parseCacheListenerConfigurations(cacheConfiguration, true);

		Properties bootstrapLoaderProperties = ReflectionTestUtil.getFieldValue(
			multiVMEhcachePortalCacheManagerConfigurator,
			"_bootstrapLoaderProperties");
		Properties replicatorProperties = ReflectionTestUtil.getFieldValue(
			multiVMEhcachePortalCacheManagerConfigurator,
			"_replicatorProperties");

		Assert.assertNull(bootstrapLoaderProperties.get("portalCacheName1"));
		Assert.assertNull(replicatorProperties.get("portalCacheName1"));
		Assert.assertEquals(
			"portalCacheName1", portalCacheConfiguration.getPortalCacheName());

		Properties portalCacheBootstrapLoaderProperties =
			portalCacheConfiguration.getPortalCacheBootstrapLoaderProperties();

		Assert.assertEquals(
			"value1", portalCacheBootstrapLoaderProperties.get("key1"));

		for (Properties properties :
				portalCacheConfiguration.
					getPortalCacheListenerPropertiesSet()) {

			Assert.assertEquals("value1", properties.get("key1"));
			Assert.assertTrue(
				"The value should be true",
				(Boolean)properties.get(PortalCacheReplicator.REPLICATOR));
		}
	}

	@Test
	public void testSetProps() {
		Props props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(true, true, true, true));

		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator =
				new RMIMultiVMEhcachePortalCacheManagerConfigurator();

		multiVMEhcachePortalCacheManagerConfigurator.setProps(props);

		Assert.assertSame(
			props,
			ReflectionTestUtil.getFieldValue(
				multiVMEhcachePortalCacheManagerConfigurator, "props"));
	}

	private MultiVMEhcachePortalCacheManagerConfigurator
		_getMultiVMEhcachePortalCacheManagerConfigurator(
			boolean clusterEnabled, boolean bootstrapLoaderEnabled,
			boolean bootstrapLoaderPropertiesIsEmpty,
			boolean replicatorPropertiesIsEmpty) {

		MultiVMEhcachePortalCacheManagerConfigurator
			multiVMEhcachePortalCacheManagerConfigurator =
				new MultiVMEhcachePortalCacheManagerConfigurator();

		multiVMEhcachePortalCacheManagerConfigurator.setProps(
			(Props)ProxyUtil.newProxyInstance(
				_classLoader, new Class<?>[] {Props.class},
				new PropsInvocationHandler(
					clusterEnabled, bootstrapLoaderEnabled,
					bootstrapLoaderPropertiesIsEmpty,
					replicatorPropertiesIsEmpty)));

		multiVMEhcachePortalCacheManagerConfigurator.activate();

		return multiVMEhcachePortalCacheManagerConfigurator;
	}

	private static final ClassLoader _classLoader =
		MultiVMEhcachePortalCacheManagerConfiguratorTest.class.getClassLoader();

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