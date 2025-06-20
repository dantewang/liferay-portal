/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import java.io.Serializable;

import java.nio.file.Path;

import java.util.Map;

/**
 * See org.elasticsearch.bootstrap.ServerArgs
 *
 * @author Dante Wang
 */
public class SidecarServerArgs implements Serializable {

	public SidecarServerArgs(
		String configDir, boolean daemonize, String logsDir, boolean quiet,
		Map<String, Serializable> settings) {

		_configDir = configDir;
		_daemonize = daemonize;
		_logsDir = logsDir;
		_quiet = quiet;
		_settings = settings;
	}

	public String getConfigDir() {
		return _configDir;
	}

	public String getLogsDir() {
		return _logsDir;
	}

	public Path getPidFile() {
		return null;
	}

	public Map<String, Serializable> getSettings() {
		return _settings;
	}

	public boolean isDaemonize() {
		return _daemonize;
	}

	public boolean isQuiet() {
		return _quiet;
	}

	private final String _configDir;
	private final boolean _daemonize;
	private final String _logsDir;
	private final boolean _quiet;
	private final Map<String, Serializable> _settings;

}