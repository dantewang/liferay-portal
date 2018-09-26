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
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class RMIMultiVMEhcachePortalCacheManagerConfiguratorTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		_rmiMultiVMEhcachePortalCacheManagerConfigurator =
			new RMIMultiVMEhcachePortalCacheManagerConfigurator();
	}

	@Test
	public void testActivate() {
		_activate(false);

		String peerListenerFactoryClass = ReflectionTestUtil.getFieldValue(
			_rmiMultiVMEhcachePortalCacheManagerConfigurator,
			"_peerListenerFactoryClass");
		String peerListenerFactoryPropertiesString =
			ReflectionTestUtil.getFieldValue(
				_rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerListenerFactoryPropertiesString");
		String peerProviderFactoryClass = ReflectionTestUtil.getFieldValue(
			_rmiMultiVMEhcachePortalCacheManagerConfigurator,
			"_peerProviderFactoryClass");
		String peerProviderFactoryPropertiesString =
			ReflectionTestUtil.getFieldValue(
				_rmiMultiVMEhcachePortalCacheManagerConfigurator,
				"_peerProviderFactoryPropertiesString");

		Assert.assertNull(peerListenerFactoryClass);
		Assert.assertNull(peerListenerFactoryPropertiesString);
		Assert.assertNull(peerProviderFactoryClass);
		Assert.assertNull(peerProviderFactoryPropertiesString);

		_activate(true);

		peerListenerFactoryClass = ReflectionTestUtil.getFieldValue(
			_rmiMultiVMEhcachePortalCacheManagerConfigurator,
			"_peerListenerFactoryClass");
		peerListenerFactoryPropertiesString = ReflectionTestUtil.getFieldValue(
			_rmiMultiVMEhcachePortalCacheManagerConfigurator,
			"_peerListenerFactoryPropertiesString");
		peerProviderFactoryClass = ReflectionTestUtil.getFieldValue(
			_rmiMultiVMEhcachePortalCacheManagerConfigurator,
			"_peerProviderFactoryClass");
		peerProviderFactoryPropertiesString = ReflectionTestUtil.getFieldValue(
			_rmiMultiVMEhcachePortalCacheManagerConfigurator,
			"_peerProviderFactoryPropertiesString");

		Assert.assertSame(
			"net.sf.ehcache.distribution." +
				"TestRMICacheManagerPeerProviderFactory",
			peerProviderFactoryClass);
		Assert.assertEquals(
			"key1=value1,key2=value2", peerListenerFactoryPropertiesString);
		Assert.assertSame(
			"com.liferay.portal.cache.ehcache.internal.rmi." +
				"TestLiferayRMICacheManagerPeerListenerFactory",
			peerListenerFactoryClass);
		Assert.assertEquals(
			"key1=value1,key2=value2", peerProviderFactoryPropertiesString);
	}

	@Test
	public void testManageConfiguration() {
		Configuration configuration = new Configuration();

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			new PortalCacheManagerConfiguration(null, null, null);

		_activate(false);

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		List<FactoryConfiguration>
			cacheManagerPeerProviderFactoryConfiguration =
				configuration.getCacheManagerPeerProviderFactoryConfiguration();
		List<FactoryConfiguration>
			cacheManagerPeerListenerFactoryConfiguration =
				configuration.
					getCacheManagerPeerListenerFactoryConfigurations();

		Assert.assertTrue(
			cacheManagerPeerProviderFactoryConfiguration.isEmpty());
		Assert.assertTrue(
			cacheManagerPeerListenerFactoryConfiguration.isEmpty());

		_activate(true);

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
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

	private void _activate(boolean clusterEnabled) {
		Props proxyProps = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(clusterEnabled));

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.setProps(proxyProps);

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.activate();
	}

	private static final ClassLoader _classLoader =
		RMIMultiVMEhcachePortalCacheManagerConfiguratorTest.class.
			getClassLoader();

	private RMIMultiVMEhcachePortalCacheManagerConfigurator
		_rmiMultiVMEhcachePortalCacheManagerConfigurator;

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