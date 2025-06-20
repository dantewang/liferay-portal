/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.concurrent.FutureListener;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationObserver;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionBuilder;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.connection.constants.ConnectionConstants;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.constants.SidecarConstants;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.elasticsearch7.sidecar.Sidecar;
import com.liferay.portal.search.elasticsearch7.sidecar.SidecarRuntimeConfiguration;
import com.liferay.portal.search.elasticsearch7.sidecar.activator.SearchElasticsearch7SidecarApiBundleActivator;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.Future;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(enabled = true, service = {})
public class SidecarManager implements ElasticsearchConfigurationObserver {

	@Override
	public int compareTo(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver) {

		return elasticsearchConfigurationWrapper.compare(
			this, elasticsearchConfigurationObserver);
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public void onElasticsearchConfigurationUpdate() {
		applyConfigurations();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		elasticsearchConfigurationWrapper.register(this);

		applyConfigurations();
	}

	protected void applyConfigurations() {
		if (elasticsearchConfigurationWrapper.isProductionModeEnabled()) {
			elasticsearchConnectionManager.removeElasticsearchConnection(
				ConnectionConstants.SIDECAR_CONNECTION_ID);
		}
		else {
			_startupSuccessful = false;

			SidecarRuntimeConfiguration sidecarRuntimeConfiguration =
				SidecarRuntimeConfigurationFactory.create(
					elasticsearchConfigurationWrapper,
					_getElasticsearchInstancePaths());

			Serializer serializer = new Serializer();

			serializer.writeObject(sidecarRuntimeConfiguration);

			ByteBuffer byteBuffer = serializer.toByteBuffer();

			byte[] bytes = byteBuffer.array();

			Checksum checksum = new CRC32();

			checksum.update(bytes);

			try {
				Sidecar sidecar =
					SearchElasticsearch7SidecarApiBundleActivator.getSidecar();

				if (sidecar != null) {
					_sidecar = sidecar;
				}

				if ((sidecar != null) &&
					(checksum.getValue() ==
						SearchElasticsearch7SidecarApiBundleActivator.
							getChecksum())) {

					_sidecar = sidecar;

					ElasticsearchConnectionBuilder
						elasticsearchConnectionBuilder =
							new ElasticsearchConnectionBuilder();

					elasticsearchConnectionManager.addElasticsearchConnection(
						elasticsearchConnectionBuilder.active(
							true
						).connectionId(
							ConnectionConstants.SIDECAR_CONNECTION_ID
						).maxConnections(
							elasticsearchConfigurationWrapper.maxConnections()
						).maxConnectionsPerRoute(
							elasticsearchConfigurationWrapper.
								maxConnectionsPerRoute()
						).networkHostAddresses(
							new String[] {_sidecar.getNetworkHostAddress()}
						).postCloseRunnable(
							_sidecar::stop
						).build());

					_startupSuccessful = true;

					return;
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Failed to start Sidecar with persisted configuration",
						exception);
				}
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Liferay automatically starts a child process of ",
						"Elasticsearch named sidecar for convenient ",
						"development and demonstration purposes. Do NOT use ",
						"sidecar in production. Refer to the documentation ",
						"for details on the limitations of sidecar and ",
						"instructions on configuring a remote Elasticsearch ",
						"connection in the Control Panel."));
			}

			if (_sidecar != null) {
				_sidecar.stop();
			}

			_sidecar = new Sidecar(
				processExecutor, new RestartFutureListener(this),
				sidecarRuntimeConfiguration);

			ElasticsearchConnectionBuilder elasticsearchConnectionBuilder =
				new ElasticsearchConnectionBuilder();

			elasticsearchConnectionBuilder.active(
				true
			).connectionId(
				ConnectionConstants.SIDECAR_CONNECTION_ID
			).maxConnections(
				elasticsearchConfigurationWrapper.maxConnections()
			).maxConnectionsPerRoute(
				elasticsearchConfigurationWrapper.maxConnectionsPerRoute()
			).postCloseRunnable(
				_sidecar::stop
			).preConnectElasticsearchConnectionConsumer(
				elasticsearchConnection -> {
					_installElasticsearchIfNeeded(sidecarRuntimeConfiguration);

					_sidecar.start();

					elasticsearchConnection.setNetworkHostAddresses(
						new String[] {_sidecar.getNetworkHostAddress()});

					try {
						File file = _bundleContext.getDataFile(
							Sidecar.class.getName());

						Files.write(file.toPath(), bytes);

						File checksumFile = _bundleContext.getDataFile(
							Sidecar.class.getName() + "_checksum");

						Files.writeString(
							checksumFile.toPath(),
							String.valueOf(checksum.getValue()));
					}
					catch (IOException ioException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to persist Sidecar configuration",
								ioException);
						}
					}
				}
			);

			elasticsearchConnectionManager.addElasticsearchConnection(
				elasticsearchConnectionBuilder.build());

			_startupSuccessful = true;
		}
	}

	@Deactivate
	protected void deactivate() {
		elasticsearchConfigurationWrapper.unregister(this);
	}

	protected boolean isStartupSuccessful() {
		return _startupSuccessful;
	}

	@Reference
	protected ElasticsearchConfigurationWrapper
		elasticsearchConfigurationWrapper;

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	@Reference
	protected ProcessExecutor processExecutor;

	private Distribution _getElasticsearchDistribution(String sidecarVersion) {
		if (sidecarVersion.equals(ElasticsearchDistribution.VERSION)) {
			return new ElasticsearchDistribution();
		}

		throw new IllegalArgumentException(
			"Unsupported Elasticsearch version: " + sidecarVersion);
	}

	private ElasticsearchInstancePaths _getElasticsearchInstancePaths() {
		ElasticsearchInstancePathsBuilder elasticsearchInstancePathsBuilder =
			new ElasticsearchInstancePathsBuilder();

		Path workPath = Paths.get(PropsValues.LIFERAY_HOME);

		Path dataPath = workPath.resolve("data/elasticsearch7");

		return elasticsearchInstancePathsBuilder.dataPath(
			dataPath
		).homePath(
			_resolveHomePath(workPath)
		).workPath(
			workPath
		).build();
	}

	private void _installElasticsearchIfNeeded(
		SidecarRuntimeConfiguration sidecarRuntimeConfiguration) {

		ElasticsearchInstaller.builder(
		).distributablesDirectoryPath(
			sidecarRuntimeConfiguration.getSidecarWorkPath()
		).distribution(
			_getElasticsearchDistribution(
				sidecarRuntimeConfiguration.getSidecarVersion())
		).installationDirectoryPath(
			sidecarRuntimeConfiguration.getSidecarHomePath()
		).build(
		).install();
	}

	private Path _resolveHomePath(Path path) {
		String sidecarHome = elasticsearchConfigurationWrapper.sidecarHome();

		if (sidecarHome.equals("elasticsearch-sidecar")) {
			String versionNumber = ResourceUtil.getResourceAsString(
				getClass(), SidecarConstants.SIDECAR_VERSION_FILE_NAME);

			sidecarHome = sidecarHome + "/" + versionNumber;
		}

		Path relativeSidecarHomePath = path.resolve(sidecarHome);

		if (!Files.isDirectory(relativeSidecarHomePath)) {
			Path absoluteSidecarHomePath = Paths.get(sidecarHome);

			if (Files.isDirectory(absoluteSidecarHomePath)) {
				return absoluteSidecarHomePath;
			}
		}

		return relativeSidecarHomePath;
	}

	private static final Log _log = LogFactoryUtil.getLog(SidecarManager.class);

	private volatile BundleContext _bundleContext;
	private Sidecar _sidecar;
	private boolean _startupSuccessful;

	private static class RestartFutureListener
		implements FutureListener<Serializable> {

		public RestartFutureListener(SidecarManager sidecarManager) {
			_sidecarManager = sidecarManager;
		}

		@Override
		public void complete(Future<Serializable> future) {
			try {
				future.get();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Sidecar Elasticsearch process is aborted", exception);
				}
			}

			if (_sidecarManager.isStartupSuccessful()) {
				if (_log.isInfoEnabled()) {
					_log.info("Restarting sidecar Elasticsearch process");
				}

				_sidecarManager.applyConfigurations();
			}
		}

		private final SidecarManager _sidecarManager;

	}

}