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
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.List;
import java.util.Properties;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.FactoryConfiguration;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class RMIMultiVMEhcachePortalCacheManagerConfiguratorTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testActivate() {
		RMIMultiVMEhcachePortalCacheManagerConfigurator
			rmiMultiVMEhcachePortalCacheManagerConfigurator =
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(false);

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerListenerFactoryClass"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerListenerFactoryPropertiesString"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerProviderFactoryClass"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerProviderFactoryPropertiesString"));

		rmiMultiVMEhcachePortalCacheManagerConfigurator =
			_getRMIMultiVMEhcachePortalCacheManagerConfigurator(true);

		Assert.assertSame(
			"net.sf.ehcache.distribution." +
				"TestRMICacheManagerPeerProviderFactory",
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerProviderFactoryClass"));
		Assert.assertEquals(
			"key1=value1,key2=value2",
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerListenerFactoryPropertiesString"));
		Assert.assertSame(
			"com.liferay.portal.cache.ehcache.internal.rmi." +
				"TestLiferayRMICacheManagerPeerListenerFactory",
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerListenerFactoryClass"));
		Assert.assertEquals(
			"key1=value1,key2=value2",
			ReflectionTestUtil.getFieldValue(
				rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerProviderFactoryPropertiesString"));
	}

	@Test
	public void testManageConfiguration() {
		Configuration configuration = new Configuration();

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			new PortalCacheManagerConfiguration(null, null, null);

		RMIMultiVMEhcachePortalCacheManagerConfigurator
			rmiMultiVMEhcachePortalCacheManagerConfigurator =
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(false);

		rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		List<FactoryConfiguration>
			cacheManagerPeerProviderFactoryConfiguration =
				configuration.getCacheManagerPeerProviderFactoryConfiguration();
		List<FactoryConfiguration>
			cacheManagerPeerListenerFactoryConfiguration =
				configuration.
					getCacheManagerPeerListenerFactoryConfigurations();

		Assert.assertTrue(
			cacheManagerPeerProviderFactoryConfiguration.toString(),
			cacheManagerPeerProviderFactoryConfiguration.isEmpty());
		Assert.assertTrue(
			cacheManagerPeerListenerFactoryConfiguration.toString(),
			cacheManagerPeerListenerFactoryConfiguration.isEmpty());

		rmiMultiVMEhcachePortalCacheManagerConfigurator =
			_getRMIMultiVMEhcachePortalCacheManagerConfigurator(true);

		rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		List<FactoryConfiguration>
			cacheManagerPeerProviderFactoryConfigurations =
				configuration.getCacheManagerPeerProviderFactoryConfiguration();

		FactoryConfiguration peerProviderFactoryConfiguration =
			cacheManagerPeerProviderFactoryConfigurations.get(0);

		Assert.assertSame(
			"net.sf.ehcache.distribution." +
				"TestRMICacheManagerPeerProviderFactory",
			peerProviderFactoryConfiguration.getFullyQualifiedClassPath());
		Assert.assertEquals(
			"key1=value1,key2=value2",
			peerProviderFactoryConfiguration.getProperties());
		Assert.assertSame(
			StringPool.COMMA,
			peerProviderFactoryConfiguration.getPropertySeparator());

		List<FactoryConfiguration>
			cacheManagerPeerListenerFactoryConfigurations =
				configuration.
					getCacheManagerPeerListenerFactoryConfigurations();

		FactoryConfiguration peerListenerFacotryConfiguration =
			cacheManagerPeerListenerFactoryConfigurations.get(0);

		Assert.assertSame(
			"com.liferay.portal.cache.ehcache.internal.rmi." +
				"TestLiferayRMICacheManagerPeerListenerFactory",
			peerListenerFacotryConfiguration.getFullyQualifiedClassPath());
		Assert.assertEquals(
			"key1=value1,key2=value2",
			peerListenerFacotryConfiguration.getProperties());
		Assert.assertSame(
			StringPool.COMMA,
			peerListenerFacotryConfiguration.getPropertySeparator());
	}

	private RMIMultiVMEhcachePortalCacheManagerConfigurator
		_getRMIMultiVMEhcachePortalCacheManagerConfigurator(
			boolean clusterEnabled) {

		RMIMultiVMEhcachePortalCacheManagerConfigurator
			rmiMultiVMEhcachePortalCacheManagerConfigurator =
				new RMIMultiVMEhcachePortalCacheManagerConfigurator();

		rmiMultiVMEhcachePortalCacheManagerConfigurator.setProps(
			(Props)ProxyUtil.newProxyInstance(
				_classLoader, new Class<?>[] {Props.class},
				new PropsInvocationHandler(clusterEnabled)));

		rmiMultiVMEhcachePortalCacheManagerConfigurator.activate();

		return rmiMultiVMEhcachePortalCacheManagerConfigurator;
	}

	private static final ClassLoader _classLoader =
		RMIMultiVMEhcachePortalCacheManagerConfiguratorTest.class.
			getClassLoader();

	private class PropsInvocationHandler implements InvocationHandler {

		public PropsInvocationHandler(boolean clusterEnabled) {
			_clusterEnabled = clusterEnabled;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			String methodName = method.getName();

			if (methodName.equals("get")) {
				String key = (String)args[0];

				if (PropsKeys.EHCACHE_RMI_PEER_LISTENER_FACTORY_CLASS.equals(
						key)) {

					return "com.liferay.portal.cache.ehcache.internal.rmi." +
						"TestLiferayRMICacheManagerPeerListenerFactory";
				}

				if (PropsKeys.EHCACHE_RMI_PEER_PROVIDER_FACTORY_CLASS.equals(
						key)) {

					return "net.sf.ehcache.distribution." +
						"TestRMICacheManagerPeerProviderFactory";
				}

				if (PropsKeys.CLUSTER_LINK_ENABLED.equals(key)) {
					return String.valueOf(_clusterEnabled);
				}
			}

			if (methodName.equals("getArray")) {
				return new String[] {"key1=value1", "key2=value2"};
			}

			if (methodName.equals("getProperties")) {
				String key = (String)args[0];

				if (key.equals(
						PropsKeys.EHCACHE_REPLICATOR_PROPERTIES +
							StringPool.PERIOD)) {

					return new Properties();
				}
			}

			return null;
		}

		private final boolean _clusterEnabled;

	}

}