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

import java.util.Collections;
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
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(false),
				"_peerListenerFactoryClass"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(false),
				"_peerListenerFactoryPropertiesString"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(false),
				"_peerProviderFactoryClass"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(false),
				"_peerProviderFactoryPropertiesString"));

		Assert.assertSame(
			"net.sf.ehcache.distribution." +
				"TestRMICacheManagerPeerProviderFactory",
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(true),
				"_peerProviderFactoryClass"));
		Assert.assertEquals(
			"key1=value1,key2=value2",
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(true),
				"_peerListenerFactoryPropertiesString"));
		Assert.assertSame(
			"com.liferay.portal.cache.ehcache.internal.rmi." +
				"TestLiferayRMICacheManagerPeerListenerFactory",
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(true),
				"_peerListenerFactoryClass"));
		Assert.assertEquals(
			"key1=value1,key2=value2",
			ReflectionTestUtil.getFieldValue(
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(true),
				"_peerProviderFactoryPropertiesString"));
	}

	@Test
	public void testManageConfiguration() {
		Configuration configuration = new Configuration();

		RMIMultiVMEhcachePortalCacheManagerConfigurator
			rmiMultiVMEhcachePortalCacheManagerConfigurator =
				_getRMIMultiVMEhcachePortalCacheManagerConfigurator(false);

		rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration,
			new PortalCacheManagerConfiguration(null, null, null));

		Assert.assertEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerProviderFactoryConfiguration());
		Assert.assertEquals(
			Collections.emptyList(),
			configuration.getCacheManagerPeerListenerFactoryConfigurations());

		rmiMultiVMEhcachePortalCacheManagerConfigurator =
			_getRMIMultiVMEhcachePortalCacheManagerConfigurator(true);

		rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration,
			new PortalCacheManagerConfiguration(null, null, null));

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
				RMIMultiVMEhcachePortalCacheManagerConfiguratorTest.class.
					getClassLoader(),
				new Class<?>[] {Props.class},
				new PropsInvocationHandler(clusterEnabled)));

		rmiMultiVMEhcachePortalCacheManagerConfigurator.activate();

		return rmiMultiVMEhcachePortalCacheManagerConfigurator;
	}

	private class PropsInvocationHandler implements InvocationHandler {

		public PropsInvocationHandler(boolean clusterEnabled) {
			_clusterEnabled = clusterEnabled;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			String methodName = method.getName();

			if ("get".equals(methodName)) {
				if (PropsKeys.EHCACHE_RMI_PEER_LISTENER_FACTORY_CLASS.equals(
						args[0])) {

					return "com.liferay.portal.cache.ehcache.internal.rmi." +
						"TestLiferayRMICacheManagerPeerListenerFactory";
				}

				if (PropsKeys.EHCACHE_RMI_PEER_PROVIDER_FACTORY_CLASS.equals(
						args[0])) {

					return "net.sf.ehcache.distribution." +
						"TestRMICacheManagerPeerProviderFactory";
				}

				if (PropsKeys.CLUSTER_LINK_ENABLED.equals(args[0])) {
					return String.valueOf(_clusterEnabled);
				}
			}

			if ("getArray".equals(methodName)) {
				return new String[] {"key1=value1", "key2=value2"};
			}

			if ("getProperties".equals(methodName)) {
				if (args[0].equals(
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