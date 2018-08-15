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
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.management.ManagementFactory;

import java.util.Dictionary;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

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
	public void testReconfigCache() throws Exception {
		_assertMaxElementsInMemory(
			"com.liferay.portal.kernel.template.TemplateResourceLoader.xsl",
			"100000");

		_multiVMPool.getPortalCache(
			ReconfigureEhcachePortalCacheTest.class.getName());

		_assertMaxElementsInMemory(
			ReconfigureEhcachePortalCacheTest.class.getName(), "10000");

		_startPortalCacheConfiguratorSettingsService();

		_assertMaxElementsInMemory(
			"com.liferay.portal.kernel.template.TemplateResourceLoader.xsl",
			"4321");

		_assertMaxElementsInMemory(
			ReconfigureEhcachePortalCacheTest.class.getName(), "1234");
	}

	private void _assertMaxElementsInMemory(
			String cacheName, String expectedMaxElementsInMemory)
		throws Exception {

		ObjectName objectName = new ObjectName(_QUERY_PREFIX.concat(cacheName));

		String maxElementsInMemory = String.valueOf(
			_mBeanServer.getAttribute(objectName, "MaxElementsInMemory"));

		Assert.assertEquals(expectedMaxElementsInMemory, maxElementsInMemory);
	}

	private void _startPortalCacheConfiguratorSettingsService()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ReconfigureEhcachePortalCacheTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		PortalCacheConfiguratorSettings portalCacheConfiguratorSettings =
			new TestEhcachePortalCacheConfiguratorSettings();

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put(
			"portal.cache.manager.name", PortalCacheManagerNames.MULTI_VM);

		ServiceRegistration<PortalCacheConfiguratorSettings>
			serviceRegistration = bundleContext.registerService(
				PortalCacheConfiguratorSettings.class,
				portalCacheConfiguratorSettings, properties);

		ServiceReference<PortalCacheConfiguratorSettings> serviceReference =
			serviceRegistration.getReference();

		ServiceTracker
			<PortalCacheConfiguratorSettings, PortalCacheConfiguratorSettings>
				serviceTracker = new ServiceTracker<>(
					bundleContext, serviceReference, null);

		serviceTracker.open();

		serviceTracker.waitForService(0);
	}

	private static final String _QUERY_PREFIX =
		"net.sf.ehcache:type=CacheConfiguration,CacheManager=" +
			PortalCacheManagerNames.MULTI_VM + ",name=";

	private final MBeanServer _mBeanServer =
		ManagementFactory.getPlatformMBeanServer();

	@Inject
	private MultiVMPool _multiVMPool;

	private class TestEhcachePortalCacheConfiguratorSettings
		extends PortalCacheConfiguratorSettings {

		public TestEhcachePortalCacheConfiguratorSettings() {
			super(
				ReconfigureEhcachePortalCacheTest.class.getClassLoader(),
				"META-INF/module-multi-vm-clustered.xml");
		}

	}

}