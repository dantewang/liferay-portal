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
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class ReconfigureEhcachePortalCacheTest {

	@Test
	public void testExistingCacheReconfigured() throws Exception {
		_assertMaxElementsInMemory("com.liferay.portal.model.Portlet", "4321");
	}

	@Test
	public void testNewCacheConfigured() throws Exception {
		_assertMaxElementsInMemory(
			TestEhcachePortalCacheConfiguratorSettings.class.getName(), "1234");
	}

	private void _assertMaxElementsInMemory(
			String cacheName, String expectedMaxElementsInMemory)
		throws Exception {

		ObjectName objectName = new ObjectName(_QUERY_PREFIX.concat(cacheName));

		String maxElementsInMemory = String.valueOf(
			_mBeanServer.getAttribute(objectName, "MaxElementsInMemory"));

		Assert.assertEquals(expectedMaxElementsInMemory, maxElementsInMemory);
	}

	private static final String _QUERY_PREFIX =
		"net.sf.ehcache:type=CacheConfiguration,CacheManager=" +
			PortalCacheManagerNames.MULTI_VM + ",name=";

	private final MBeanServer _mBeanServer =
		ManagementFactory.getPlatformMBeanServer();

}