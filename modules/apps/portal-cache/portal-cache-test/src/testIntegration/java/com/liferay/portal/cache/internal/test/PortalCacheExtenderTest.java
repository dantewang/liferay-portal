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

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cache.test.module.CacheModule;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class PortalCacheExtenderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(CacheModule.class);

		if (bundle.getState() != Bundle.ACTIVE) {
			bundle.start();
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(CacheModule.class);

		if (bundle.getState() == Bundle.ACTIVE) {
			bundle.stop();
		}
	}

	@Test
	public void testExtendModuleMultiVMConfig() throws Exception {
		_assertCacheConfig(
			"MULTI_VM_PORTAL_CACHE_MANAGER", "test.cache.multi.vm");
	}

	@Test
	public void testExtendModuleSingleVMConfig() throws Exception {
		_assertCacheConfig(
			"SINGLE_VM_PORTAL_CACHE_MANAGER", "test.cache.single.vm");
	}

	private void _assertCacheConfig(String cacheManagerName, String cacheName)
		throws Exception {

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		ObjectName objectName = new ObjectName(
			StringBundler.concat(
				"net.sf.ehcache:type=CacheConfiguration,CacheManager=",
				cacheManagerName, ",name=", cacheName));

		Assert.assertEquals(
			false, mBeanServer.getAttribute(objectName, "Eternal"));

		Assert.assertEquals(
			1000, mBeanServer.getAttribute(objectName, "MaxElementsInMemory"));

		Assert.assertEquals(
			cacheName, mBeanServer.getAttribute(objectName, "Name"));

		Assert.assertEquals(
			true, mBeanServer.getAttribute(objectName, "OverflowToDisk"));

		Assert.assertEquals(
			50L, mBeanServer.getAttribute(objectName, "TimeToIdleSeconds"));
	}

}