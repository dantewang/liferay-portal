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

package com.liferay.portal.poller;

import com.liferay.portal.kernel.poller.PollerProcessor;
import com.liferay.portal.kernel.resiliency.spi.SPIRegistryUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.resiliency.spi.SPIRegistryImpl;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class PollerProcessorUtilTest {

	@BeforeClass
	public static void setUpClass() {
		SPIRegistryUtil spiRegistryUtil = new SPIRegistryUtil();

		spiRegistryUtil.setSPIRegistry(new SPIRegistryImpl());

		RegistryUtil.setRegistry(new BasicRegistryImpl());

		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			PollerProcessor.class, _pollerProcessor,
			new HashMap<String, Object>() {
				{
					put("javax.portlet.name", "PollerProcessorUtilTest");
				}
			});
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetPollerProcessor() {
		Assert.assertSame(
			_pollerProcessor,
			PollerProcessorUtil.getPollerProcessor("PollerProcessorUtilTest"));
	}

	private static final PollerProcessor _pollerProcessor =
		ProxyFactory.newDummyInstance(PollerProcessor.class);
	private static ServiceRegistration<PollerProcessor> _serviceRegistration;

}