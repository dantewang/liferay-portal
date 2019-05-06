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

package com.liferay.portal.kernel.lar.lifecycle;

import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEventListenerRegistryUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class ExportImportLifecycleEventListenerRegistryUtilTest {

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetAsyncExportImportLifecycleListeners() {
		Registry registry = RegistryUtil.getRegistry();

		ExportImportLifecycleListener asyncExportImportLifecycleListener =
			new ExportImportLifecycleListener() {

				@Override
				public boolean isParallel() {
					return true;
				}

				@Override
				public void onExportImportLifecycleEvent(
					ExportImportLifecycleEvent exportImportLifecycleEvent) {
				}

			};

		_serviceRegistration = registry.registerService(
			ExportImportLifecycleListener.class,
			asyncExportImportLifecycleListener);

		_assertExportImportLifecycleListeners(
			asyncExportImportLifecycleListener,
			ExportImportLifecycleEventListenerRegistryUtil.
				getAsyncExportImportLifecycleListeners());
	}

	@Test
	public void testGetSyncExportImportLifecycleListeners() {
		Registry registry = RegistryUtil.getRegistry();

		ExportImportLifecycleListener syncExportImportLifecycleListener =
			new ExportImportLifecycleListener() {

				@Override
				public boolean isParallel() {
					return false;
				}

				@Override
				public void onExportImportLifecycleEvent(
					ExportImportLifecycleEvent exportImportLifecycleEvent) {
				}

			};

		_serviceRegistration = registry.registerService(
			ExportImportLifecycleListener.class,
			syncExportImportLifecycleListener);

		_assertExportImportLifecycleListeners(
			syncExportImportLifecycleListener,
			ExportImportLifecycleEventListenerRegistryUtil.
				getSyncExportImportLifecycleListeners());
	}

	private void _assertExportImportLifecycleListeners(
		ExportImportLifecycleListener expectedExportImportLifecycleListener,
		Set<ExportImportLifecycleListener> exportImportLifecycleListeners) {

		Assert.assertTrue(
			expectedExportImportLifecycleListener + " not found in " +
				exportImportLifecycleListeners,
			exportImportLifecycleListeners.removeIf(
				exportImportLifecycleListener ->
					expectedExportImportLifecycleListener ==
						exportImportLifecycleListener));
	}

	private static ServiceRegistration<ExportImportLifecycleListener>
		_serviceRegistration;

}