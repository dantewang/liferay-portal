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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.List;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.FactoryConfiguration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class RMIMultiVMEhcachePortalCacheManagerConfiguratorTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		_rmiMultiVMEhcachePortalCacheManagerConfigurator =
			new RMIMultiVMEhcachePortalCacheManagerConfigurator();
	}

	@After
	public void tearDown() {
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

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.activate();

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
			PropsInvocationHandler.
				EHCACHE_RMI_PEER_PROVIDER_FACTORY_CLASS_VALUE,
			peerProviderFactoryClass);
		Assert.assertEquals(
			_getPortalPropertiesString(
				PropsInvocationHandler.
					EHCACHE_RMI_PEER_LISTENER_FACTORY_PROPERTIES_VALUE),
			peerListenerFactoryPropertiesString);
		Assert.assertSame(
			PropsInvocationHandler.
				EHCACHE_RMI_PEER_LISTENER_FACTORY_CLASS_VALUE,
			peerListenerFactoryClass);
		Assert.assertEquals(
			_getPortalPropertiesString(
				PropsInvocationHandler.
					EHCACHE_RMI_PEER_PROVIDER_FACTORY_PROPERTIES_VALUE),
			peerProviderFactoryPropertiesString);
	}

	@Test
	public void testManageConfiguration() {
		Configuration configuration = new Configuration();

		PortalCacheManagerConfiguration portalCacheManagerConfiguration =
			new PortalCacheManagerConfiguration(null, null, null);

		_activate(false);

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		Assert.assertTrue(
			ListUtil.isEmpty(
				configuration.
					getCacheManagerPeerProviderFactoryConfiguration()));
		Assert.assertTrue(
			ListUtil.isEmpty(
				configuration.
					getCacheManagerPeerListenerFactoryConfigurations()));

		_activate(true);

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.manageConfiguration(
			configuration, portalCacheManagerConfiguration);

		List<FactoryConfiguration>
			cacheManagerPeerProviderFactoryConfigurationLsit =
				configuration.getCacheManagerPeerProviderFactoryConfiguration();

		FactoryConfiguration peerProviderFactoryConfiguration =
			cacheManagerPeerProviderFactoryConfigurationLsit.get(0);

		Assert.assertSame(
			PropsInvocationHandler.
				EHCACHE_RMI_PEER_PROVIDER_FACTORY_CLASS_VALUE,
			peerProviderFactoryConfiguration.getFullyQualifiedClassPath());
		Assert.assertEquals(
			_getPortalPropertiesString(
				PropsInvocationHandler.
					EHCACHE_RMI_PEER_PROVIDER_FACTORY_PROPERTIES_VALUE),
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
			PropsInvocationHandler.
				EHCACHE_RMI_PEER_LISTENER_FACTORY_CLASS_VALUE,
			peerListenerFacotryConfiguration.getFullyQualifiedClassPath());
		Assert.assertEquals(
			_getPortalPropertiesString(
				PropsInvocationHandler.
					EHCACHE_RMI_PEER_LISTENER_FACTORY_PROPERTIES_VALUE),
			peerListenerFacotryConfiguration.getProperties());
		Assert.assertSame(
			StringPool.COMMA,
			peerListenerFacotryConfiguration.getPropertySeparator());
	}

	private void _activate(boolean clusterEnabled) {
		Props proxyProps = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(clusterEnabled));

		ReflectionTestUtil.setFieldValue(
			_rmiMultiVMEhcachePortalCacheManagerConfigurator, "props",
			proxyProps);

		_rmiMultiVMEhcachePortalCacheManagerConfigurator.activate();
	}

	private String _getPortalPropertiesString(String[] array) {
		if (array.length == 0) {
			return null;
		}

		if (array.length == 1) {
			return array[0];
		}

		StringBundler sb = new StringBundler(array.length * 2);

		for (String value : array) {
			sb.append(value);
			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static final ClassLoader _classLoader =
		RMIMultiVMEhcachePortalCacheManagerConfiguratorTest.class.
			getClassLoader();

	private RMIMultiVMEhcachePortalCacheManagerConfigurator
		_rmiMultiVMEhcachePortalCacheManagerConfigurator;

}