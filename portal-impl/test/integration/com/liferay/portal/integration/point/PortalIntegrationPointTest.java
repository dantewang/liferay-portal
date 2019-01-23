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

package com.liferay.portal.integration.point;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;

import com.liferay.portal.integration.point.bundle.portalintegrationpoint.TestDisplayContextFactory;
import com.liferay.portal.kernel.display.context.DisplayContextFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.util.ServiceProxyFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.module.framework.ModuleFrameworkUtilAdapter;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class PortalIntegrationPointTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_bundleId = ModuleFrameworkUtilAdapter.addBundle(
			PortalIntegrationPointTest.class.getName(), _createBundle());

		ModuleFrameworkUtilAdapter.startBundle(_bundleId);
	}

	@AfterClass
	public static void tearDownClass() throws PortalException {
		if (_bundleId == null) {
			return;
		}

		ModuleFrameworkUtilAdapter.stopBundle(_bundleId);

		ModuleFrameworkUtilAdapter.uninstallBundle(_bundleId);
	}

	@Test
	public void testPortalIntegrationPointWithServiceProxyFactory() {
		_displayContextFactory = ServiceProxyFactory.newServiceTrackedInstance(
			DisplayContextFactory.class, PortalIntegrationPointTest.class,
			"_displayContextFactory", false);

		Class<?> clazz = _displayContextFactory.getClass();

		Assert.assertEquals(
			TestDisplayContextFactory.class.getName(), clazz.getName());
	}

	@Test
	public void testPortalIntegrationPointWithServiceTracker() {
		_testPortalIntegrationPointWithServiceTracker(true);
		_testPortalIntegrationPointWithServiceTracker(false);
	}

	private static InputStream _createBundle() throws Exception {
		URL url = PortalIntegrationPointTest.class.getResource("");

		File baseDir = new File(
			StringUtil.removeSubstring(
				url.getPath(), "com/liferay/portal/integration/point/"));

		try (Builder builder = new Builder()) {
			builder.setBundleSymbolicName(
				PortalIntegrationPointTest.class.getName());
			builder.setBase(baseDir);
			builder.setClasspath(new File[] {baseDir});
			builder.setProperty(
				"Bundle-Description",
				"Test Bundle for PortalIntegrationPointTest");
			builder.setProperty("Bundle-Version", "1.0.0");
			builder.setProperty(
				"Private-Package",
				"com.liferay.portal.integration.point.bundle." +
					"portalintegrationpoint.*");
			builder.setProperty("-dsannotations", "*");

			try (Jar jar = builder.build()) {
				UnsyncByteArrayOutputStream outputStream =
					new UnsyncByteArrayOutputStream();

				jar.write(outputStream);

				return new UnsyncByteArrayInputStream(
					outputStream.unsafeGetByteArray(), 0, outputStream.size());
			}
		}
	}

	private void _testPortalIntegrationPointWithServiceTracker(
		boolean useServiceTrackerCustomizer) {

		Registry registry = RegistryUtil.getRegistry();

		ServiceTracker<DisplayContextFactory, DisplayContextFactory>
			serviceTracker;

		if (useServiceTrackerCustomizer) {
			serviceTracker = registry.trackServices(
				DisplayContextFactory.class,
				new DisplayContextFactoryServiceTrackerCustomizer());
		}
		else {
			serviceTracker = registry.trackServices(
				DisplayContextFactory.class);
		}

		serviceTracker.open();

		try {
			DisplayContextFactory displayContextFactory =
				serviceTracker.getService();

			Class<?> clazz = displayContextFactory.getClass();

			Assert.assertEquals(
				TestDisplayContextFactory.class.getName(), clazz.getName());
		}
		finally {
			serviceTracker.close();
		}
	}

	private static Long _bundleId;
	private static volatile DisplayContextFactory _displayContextFactory;

	private static class DisplayContextFactoryServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<DisplayContextFactory, DisplayContextFactory> {

		@Override
		public DisplayContextFactory addingService(
			ServiceReference<DisplayContextFactory> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			return registry.getService(serviceReference);
		}

		@Override
		public void modifiedService(
			ServiceReference<DisplayContextFactory> serviceReference,
			DisplayContextFactory displayContextFactory) {
		}

		@Override
		public void removedService(
			ServiceReference<DisplayContextFactory> serviceReference,
			DisplayContextFactory displayContextFactory) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);
		}

	}

}