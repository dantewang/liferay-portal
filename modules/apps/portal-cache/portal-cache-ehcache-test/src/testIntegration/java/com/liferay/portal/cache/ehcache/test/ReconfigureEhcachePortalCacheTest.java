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
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Method;

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

		_assertEhcache(portalCache1, aggregatedPortalCacheListener1, 100000);
		_assertEhcache(portalCache2, aggregatedPortalCacheListener2, 10000);

		_invokeReconfigure(
			_multiVMPool.getPortalCacheManager(),
			new PortalCacheConfiguratorSettings(
				ReconfigureEhcachePortalCacheTest.class.getClassLoader(),
				"META-INF/test-module-multi-vm-clustered.xml"));

		_assertEhcache(portalCache1, aggregatedPortalCacheListener1, 4321);
		_assertEhcache(portalCache2, aggregatedPortalCacheListener2, 1234);
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

		_assertEhcache(portalCache1, aggregatedPortalCacheListener1, 10000);
		_assertEhcache(portalCache2, aggregatedPortalCacheListener2, 10000);

		_invokeReconfigure(
			_singleVMPool.getPortalCacheManager(),
			new PortalCacheConfiguratorSettings(
				ReconfigureEhcachePortalCacheTest.class.getClassLoader(),
				"META-INF/test-module-single-vm.xml"));

		_assertEhcache(portalCache1, aggregatedPortalCacheListener1, 4321);
		_assertEhcache(portalCache2, aggregatedPortalCacheListener2, 1234);
	}

	private void _assertEhcache(
		PortalCache<?, ?> portalCache,
		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener,
		int expectedMaxElementsInMemory) {

		Ehcache ehcache = ReflectionTestUtil.getFieldValue(
			portalCache, "ehcache");

		CacheConfiguration cacheConfiguration = ehcache.getCacheConfiguration();

		Assert.assertEquals(
			expectedMaxElementsInMemory,
			cacheConfiguration.getMaxElementsInMemory());

		RegisteredEventListeners registeredEventListeners =
			ehcache.getCacheEventNotificationService();

		boolean listenerPresent = false;

		for (CacheEventListener cacheEventListener :
				registeredEventListeners.getCacheEventListeners()) {

			try {
				if (aggregatedPortalCacheListener ==
						ReflectionTestUtil.getFieldValue(
							cacheEventListener,
							"_aggregatedPortalCacheListener")) {

					listenerPresent = true;

					break;
				}
			}
			catch (Exception e) {
			}
		}

		Assert.assertTrue(
			"Expected AggregatedPortalCacheListener is not present!",
			listenerPresent);
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

}