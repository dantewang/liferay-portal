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

import org.junit.AfterClass;
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

		Registry registry = RegistryUtil.getRegistry();

		_asyncExportImportLifecycleListener =
			new TestAsyncExportImportLifecycleListener();

		_serviceRegistration1 = registry.registerService(
			ExportImportLifecycleListener.class,
			_asyncExportImportLifecycleListener);

		_syncExportImportLifecycleListener =
			new TestSyncExportImportLifecycleListener();

		_serviceRegistration2 = registry.registerService(
			ExportImportLifecycleListener.class,
			_syncExportImportLifecycleListener);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration1.unregister();
		_serviceRegistration2.unregister();
	}

	@Test
	public void testGetAsyncExportImportLifecycleListeners() {
		_assertExportImportLifecycleListeners(
			_asyncExportImportLifecycleListener,
			ExportImportLifecycleEventListenerRegistryUtil.
				getAsyncExportImportLifecycleListeners());
	}

	@Test
	public void testGetSyncExportImportLifecycleListeners() {
		_assertExportImportLifecycleListeners(
			_syncExportImportLifecycleListener,
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

	private static ExportImportLifecycleListener
		_asyncExportImportLifecycleListener;
	private static ServiceRegistration<ExportImportLifecycleListener>
		_serviceRegistration1;
	private static ServiceRegistration<ExportImportLifecycleListener>
		_serviceRegistration2;
	private static ExportImportLifecycleListener
		_syncExportImportLifecycleListener;

	private static class TestAsyncExportImportLifecycleListener
		implements ExportImportLifecycleListener {

		@Override
		public boolean isParallel() {
			return true;
		}

		@Override
		public void onExportImportLifecycleEvent(
			ExportImportLifecycleEvent exportImportLifecycleEvent) {
		}

	}

	private static class TestSyncExportImportLifecycleListener
		implements ExportImportLifecycleListener {

		@Override
		public boolean isParallel() {
			return false;
		}

		@Override
		public void onExportImportLifecycleEvent(
			ExportImportLifecycleEvent exportImportLifecycleEvent) {
		}

	}

}