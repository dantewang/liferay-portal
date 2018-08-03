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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.lang.management.ManagementFactory;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class ReconfigureEhcachePortalCacheTest {

	@Test
	public void testEhcachePortalCacheReconfigured()
		throws Exception {

		MBeanServer _mBeanServer = ManagementFactory.getPlatformMBeanServer();

		Set<ObjectName> objectNames = _mBeanServer.queryNames(
			null, new ObjectName("net.sf.ehcache".concat(":*")));

		Stream<ObjectName> stream = objectNames.stream();

		Optional<ObjectName> result = stream.filter(
			objectName ->
				"CacheConfiguration".equals(objectName.getKeyProperty("type"))
		).filter(
			objectName ->
				PortalCacheManagerNames.MULTI_VM.equals(
					objectName.getKeyProperty("CacheManager"))
		).filter(
			objectName -> {
				String name = objectName.getKeyProperty("name");

				return name.equals(
					TestEhcachePortalCacheConfiguratorSettings.class.getName());
			}
		).findFirst();

		Assert.assertTrue(result.isPresent());

		String maxElementsInMemory =
			String.valueOf(
				_mBeanServer.getAttribute(result.get(), "MaxElementsInMemory"));

		Assert.assertEquals("1", maxElementsInMemory);
	}

}