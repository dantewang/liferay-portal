/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar.activator;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.SidecarRuntimeConfiguration;

import java.io.File;

import java.util.concurrent.ExecutorService;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Dante Wang
 */
public class SearchElasticsearch7ImplBundleActivator
	implements BundleActivator {

	public static long getChecksum() {
		return _checksum;
	}

	public static Sidecar getSidecar() throws Exception {
		return _defaultNoticeableFuture.get();
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		File file = bundleContext.getDataFile(Sidecar.class.getName());

		if (!file.isFile() || StartupHelperUtil.isDBNew()) {
			_defaultNoticeableFuture.set(null);

			return;
		}

		byte[] bytes = FileUtil.getBytes(file);

		Checksum checksum = new CRC32();

		checksum.update(bytes);

		_checksum = checksum.getValue();

		ServiceReference<ProcessExecutor> serviceReference =
			bundleContext.getServiceReference(ProcessExecutor.class);

		SidecarRuntimeConfiguration sidecarRuntimeConfiguration =
			SidecarRuntimeConfiguration.from(bytes);

		Sidecar sidecar = new Sidecar(
			bundleContext.getService(serviceReference), null,
			sidecarRuntimeConfiguration);

		ExecutorService executorService =
			SystemExecutorServiceUtil.getExecutorService();

		executorService.submit(
			() -> {
				try {
					sidecar.start();

					_defaultNoticeableFuture.set(sidecar);
				}
				catch (Throwable throwable) {
					_defaultNoticeableFuture.setException(throwable);
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Sidecar sidecar = getSidecar();

		if (sidecar == null) {
			return;
		}

		sidecar.stop();
	}

	private static volatile long _checksum;
	private static final DefaultNoticeableFuture<Sidecar>
		_defaultNoticeableFuture = new DefaultNoticeableFuture<>();

}