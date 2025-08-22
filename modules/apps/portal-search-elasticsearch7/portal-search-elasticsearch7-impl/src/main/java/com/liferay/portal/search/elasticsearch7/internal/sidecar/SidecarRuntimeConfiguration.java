/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessConfig;

import java.nio.file.Path;

/**
 * @author Dante Wang
 */
public class SidecarRuntimeConfiguration {

	public SidecarRuntimeConfiguration(
		long heartbeatInterval, Path homePath, String nodeName,
		ProcessConfig processConfig, long shutdownTimeout,
		SidecarServerArgs sidecarServerArgs, Path tempDirPath, String version) {

		_heartbeatInterval = heartbeatInterval;
		_homePath = homePath;
		_nodeName = nodeName;
		_processConfig = processConfig;
		_shutdownTimeout = shutdownTimeout;
		_sidecarServerArgs = sidecarServerArgs;
		_tempDirPath = tempDirPath;
		_version = version;
	}

	public long getHeartbeatInterval() {
		return _heartbeatInterval;
	}

	public Path getHomePath() {
		return _homePath;
	}

	public String getNodeName() {
		return _nodeName;
	}

	public ProcessConfig getProcessConfig() {
		return _processConfig;
	}

	public long getShutdownTimeout() {
		return _shutdownTimeout;
	}

	public SidecarServerArgs getSidecarServerArgs() {
		return _sidecarServerArgs;
	}

	public Path getTempDirPath() {
		return _tempDirPath;
	}

	public String getVersion() {
		return _version;
	}

	private final long _heartbeatInterval;
	private final Path _homePath;
	private final String _nodeName;
	private final ProcessConfig _processConfig;
	private final long _shutdownTimeout;
	private final SidecarServerArgs _sidecarServerArgs;
	private final Path _tempDirPath;
	private final String _version;

}