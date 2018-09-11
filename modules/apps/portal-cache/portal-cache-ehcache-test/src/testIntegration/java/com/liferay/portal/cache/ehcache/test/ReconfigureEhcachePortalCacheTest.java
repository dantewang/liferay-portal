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

package com.liferay.portal.cache.ehcache.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.cache.AggregatedPortalCacheListener;
import com.liferay.portal.cache.ehcache.spi.event.ConfigurableEhcachePortalCacheListener;
import com.liferay.portal.cache.ehcache.test.event.TestCacheEventListener;
import com.liferay.portal.cache.test.util.TestPortalCacheListener;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheListener;
import com.liferay.portal.kernel.cache.PortalCacheListenerScope;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Method;

import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.RegisteredEventListeners;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class ReconfigureEhcachePortalCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testReconfigMultiVMCache() throws Exception {
		PortalCache<?, ?> portalCache1 = _multiVMPool.getPortalCache(
			"com.liferay.portal.kernel.template.TemplateResourceLoader.xsl");
		PortalCache<?, ?> portalCache2 = _multiVMPool.getPortalCache(
			ReconfigureEhcachePortalCacheTest.class.getName());

		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener1 =
			ReflectionTestUtil.getFieldValue(
				portalCache1, "aggregatedPortalCacheListener");
		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener2 =
			ReflectionTestUtil.getFieldValue(
				portalCache2, "aggregatedPortalCacheListener");

		portalCache2.registerPortalCacheListener(
			_testPortalCacheListener, PortalCacheListenerScope.ALL);

		_assertMaxElementsInMemory(portalCache1, 100000);
		_assertMaxElementsInMemory(portalCache2, 10000);

		_assertListeners(
			portalCache1, aggregatedPortalCacheListener1, null, false);
		_assertListeners(
			portalCache2, aggregatedPortalCacheListener2,
			_testPortalCacheListener, false);

		_invokeReconfigure(
			_multiVMPool.getPortalCacheManager(),
			new PortalCacheConfiguratorSettings(
				ReconfigureEhcachePortalCacheTest.class.getClassLoader(),
				"META-INF/test-module-multi-vm-clustered.xml"));

		_assertMaxElementsInMemory(portalCache1, 4321);
		_assertMaxElementsInMemory(portalCache2, 1234);

		_assertListeners(
			portalCache1, aggregatedPortalCacheListener1, null, false);
		_assertListeners(
			portalCache2, aggregatedPortalCacheListener2,
			_testPortalCacheListener, true);
	}

	@Test
	public void testReconfigSingleVMCache() throws Exception {
		PortalCache<?, ?> portalCache1 = _singleVMPool.getPortalCache(
			"com.liferay.portal.util.WebCachePool");
		PortalCache<?, ?> portalCache2 = _singleVMPool.getPortalCache(
			ReconfigureEhcachePortalCacheTest.class.getName());

		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener1 =
			ReflectionTestUtil.getFieldValue(
				portalCache1, "aggregatedPortalCacheListener");
		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener2 =
			ReflectionTestUtil.getFieldValue(
				portalCache2, "aggregatedPortalCacheListener");

		portalCache2.registerPortalCacheListener(
			_testPortalCacheListener, PortalCacheListenerScope.ALL);

		_assertMaxElementsInMemory(portalCache1, 10000);
		_assertMaxElementsInMemory(portalCache2, 10000);

		_assertListeners(
			portalCache1, aggregatedPortalCacheListener1, null, false);
		_assertListeners(
			portalCache2, aggregatedPortalCacheListener2,
			_testPortalCacheListener, false);

		_invokeReconfigure(
			_singleVMPool.getPortalCacheManager(),
			new PortalCacheConfiguratorSettings(
				ReconfigureEhcachePortalCacheTest.class.getClassLoader(),
				"META-INF/test-module-single-vm.xml"));

		_assertMaxElementsInMemory(portalCache1, 4321);
		_assertMaxElementsInMemory(portalCache2, 1234);

		_assertListeners(
			portalCache1, aggregatedPortalCacheListener1, null, false);
		_assertListeners(
			portalCache2, aggregatedPortalCacheListener2,
			_testPortalCacheListener, true);
	}

	private void _assertListeners(
		PortalCache<?, ?> portalCache,
		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener,
		PortalCacheListener<?, ?> registeredPortalCacheListener,
		boolean configuredPortalCacheListener) {

		// Assert the provided AggregatedPortalCacheListener is registered on
		// the Ehcache

		Ehcache ehcache = ReflectionTestUtil.getFieldValue(
			portalCache, "ehcache");

		RegisteredEventListeners registeredEventListeners =
			ehcache.getCacheEventNotificationService();

		boolean hasAggregatedPortalCacheListener = false;

		for (CacheEventListener cacheEventListener :
				registeredEventListeners.getCacheEventListeners()) {

			try {
				if (aggregatedPortalCacheListener ==
						ReflectionTestUtil.getFieldValue(
							cacheEventListener,
							"_aggregatedPortalCacheListener")) {

					hasAggregatedPortalCacheListener = true;

					break;
				}
			}
			catch (Exception e) {
			}
		}

		Assert.assertTrue(
			"Expected AggregatedPortalCacheListener is not present!",
			hasAggregatedPortalCacheListener);

		// Assert the provided PortalCacheListener is registered on the
		// PortalCache

		Map<? extends PortalCacheListener<?, ?>, PortalCacheListenerScope>
			portalCacheListeners =
				aggregatedPortalCacheListener.getPortalCacheListeners();

		if (registeredPortalCacheListener != null) {
			Assert.assertEquals(
				PortalCacheListenerScope.ALL,
				portalCacheListeners.get(registeredPortalCacheListener));
		}

		// Assert the configured Ehcache listener is registered on the
		// PortalCache

		boolean registeredConfiguredPortalCacheListener = false;

		for (PortalCacheListener<?, ?> portalCacheListener :
				portalCacheListeners.keySet()) {

			if (portalCacheListener instanceof
					ConfigurableEhcachePortalCacheListener) {

				CacheEventListener cacheEventListener =
					ReflectionTestUtil.getFieldValue(
						portalCacheListener, "cacheEventListener");

				if (cacheEventListener instanceof TestCacheEventListener) {
					registeredConfiguredPortalCacheListener = true;

					break;
				}
			}
		}

		Assert.assertEquals(
			configuredPortalCacheListener,
			registeredConfiguredPortalCacheListener);
	}

	private void _assertMaxElementsInMemory(
		PortalCache<?, ?> portalCache, int expectedMaxElementsInMemory) {

		Ehcache ehcache = ReflectionTestUtil.getFieldValue(
			portalCache, "ehcache");

		CacheConfiguration cacheConfiguration = ehcache.getCacheConfiguration();

		Assert.assertEquals(
			expectedMaxElementsInMemory,
			cacheConfiguration.getMaxElementsInMemory());
	}

	private void _invokeReconfigure(
			PortalCacheManager<?, ?> portalCacheManager,
			PortalCacheConfiguratorSettings portalCacheConfiguratorSettings)
		throws Exception {

		Method method = ReflectionTestUtil.getMethod(
			portalCacheManager.getClass(), "reconfigure",
			PortalCacheConfiguratorSettings.class);

		method.invoke(portalCacheManager, portalCacheConfiguratorSettings);
	}

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SingleVMPool _singleVMPool;

	private final PortalCacheListener _testPortalCacheListener =
		new TestPortalCacheListener();

}