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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.lang.management.ManagementFactory;

import java.util.Dictionary;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
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

	@Test
	public void testReconfigMultiVMCache() throws Exception {
		PortalCacheConfiguratorSettings portalCacheConfiguratorSettings =
			new TestPortalCacheConfiguratorSettings(
				"META-INF/module-multi-vm-clustered.xml");

		_testReconfig(
			"com.liferay.portal.kernel.template.TemplateResourceLoader.xsl",
			"100000", "4321", PortalCacheManagerNames.MULTI_VM,
			portalCacheConfiguratorSettings);
	}

	@Test
	public void testReconfigSingleVMCache() throws Exception {
		PortalCacheConfiguratorSettings portalCacheConfiguratorSettings =
			new TestPortalCacheConfiguratorSettings(
				"META-INF/module-single-vm.xml");

		_testReconfig(
			"com.liferay.portal.util.WebCachePool", "10000", "4321",
			PortalCacheManagerNames.SINGLE_VM, portalCacheConfiguratorSettings);
	}

	private void _assertMaxElementsInMemory(
			String cacheName, String expectedMaxElementsInMemory,
			String portalCacheManagerName)
		throws Exception {

		ObjectName objectName = new ObjectName(
			StringBundler.concat(
				"net.sf.ehcache:type=CacheConfiguration,CacheManager=",
				portalCacheManagerName, ",name=", cacheName));

		String maxElementsInMemory = String.valueOf(
			_mBeanServer.getAttribute(objectName, "MaxElementsInMemory"));

		Assert.assertEquals(expectedMaxElementsInMemory, maxElementsInMemory);
	}

	private void _testReconfig(
			String cacheName, String maxSizeBeforeReconfig,
			String maxSizeAfterReconfig, String portalCacheManagerName,
			PortalCacheConfiguratorSettings portalCacheConfiguratorSettings)
		throws Exception {

		_assertMaxElementsInMemory(
			cacheName, maxSizeBeforeReconfig, portalCacheManagerName);

		try {
			_assertMaxElementsInMemory(
				ReconfigureEhcachePortalCacheTest.class.getName(), "10000",
				portalCacheManagerName);

			Assert.fail();
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof InstanceNotFoundException);
		}

		Bundle bundle = FrameworkUtil.getBundle(
			ReconfigureEhcachePortalCacheTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("portal.cache.manager.name", portalCacheManagerName);

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

		try {
			serviceTracker.waitForService(0);

			_assertMaxElementsInMemory(
				cacheName, maxSizeAfterReconfig, portalCacheManagerName);

			_assertMaxElementsInMemory(
				ReconfigureEhcachePortalCacheTest.class.getName(), "1234",
				portalCacheManagerName);
		}
		finally {
			serviceTracker.close();
		}
	}

	private final MBeanServer _mBeanServer =
		ManagementFactory.getPlatformMBeanServer();

	private class TestPortalCacheConfiguratorSettings
		extends PortalCacheConfiguratorSettings {

		public TestPortalCacheConfiguratorSettings(String configFile) {
			super(
				ReconfigureEhcachePortalCacheTest.class.getClassLoader(),
				configFile);
		}

	}

}