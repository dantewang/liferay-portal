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

		_assertMaxElementsInMemory(portalCache1, 100000);
		_assertMaxElementsInMemory(portalCache2, 10000);

		_invokeReconfigure(
			_multiVMPool.getPortalCacheManager(),
			new TestPortalCacheConfiguratorSettings(
				"META-INF/module-multi-vm-clustered.xml"));

		_assertMaxElementsInMemory(portalCache1, 4321);
		_assertMaxElementsInMemory(portalCache2, 1234);
	}

	@Test
	public void testReconfigSingleVMCache() throws Exception {
		PortalCache<?, ?> portalCache1 = _singleVMPool.getPortalCache(
			"com.liferay.portal.util.WebCachePool");
		PortalCache<?, ?> portalCache2 = _singleVMPool.getPortalCache(
			ReconfigureEhcachePortalCacheTest.class.getName());

		_assertMaxElementsInMemory(portalCache1, 10000);
		_assertMaxElementsInMemory(portalCache2, 10000);

		_invokeReconfigure(
			_singleVMPool.getPortalCacheManager(),
			new TestPortalCacheConfiguratorSettings(
				"META-INF/module-single-vm.xml"));

		_assertMaxElementsInMemory(portalCache1, 4321);
		_assertMaxElementsInMemory(portalCache2, 1234);
	}

	private void _assertMaxElementsInMemory(
			PortalCache<?, ?> portalCache, int expectedMaxElementsInMemory) {

		Ehcache ehcache = ReflectionTestUtil.getFieldValue(
			portalCache, "ehcache");

		CacheConfiguration cacheConfiguration = ehcache.getCacheConfiguration();

		int maxElementsInMemory = cacheConfiguration.getMaxElementsInMemory();

		Assert.assertEquals(expectedMaxElementsInMemory, maxElementsInMemory);
	}

	private void _invokeReconfigure(
			PortalCacheManager<?, ?> portalCacheManager,
			PortalCacheConfiguratorSettings portalCacheConfiguratorSettings)
		throws Exception {

		Class<?> clazz = portalCacheManager.getClass();

		Method method = ReflectionTestUtil.getMethod(
			clazz, "reconfigure", PortalCacheConfiguratorSettings.class);

		method.invoke(portalCacheManager, portalCacheConfiguratorSettings);
	}

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SingleVMPool _singleVMPool;

	private class TestPortalCacheConfiguratorSettings
		extends PortalCacheConfiguratorSettings {

		public TestPortalCacheConfiguratorSettings(String configFile) {
			super(
				ReconfigureEhcachePortalCacheTest.class.getClassLoader(),
				configFile);
		}

	}

}