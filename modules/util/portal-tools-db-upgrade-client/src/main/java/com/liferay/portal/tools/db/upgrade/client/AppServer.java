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

package com.liferay.portal.tools.db.upgrade.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author David Truong
 */
public class AppServer {

	public static AppServer getAppServer(
		String appServerName, Properties property) {

		return new AppServer(
			property.getProperty(appServerName + "-" + _PROPERTY_NAMES[0]),
			property.getProperty(appServerName + "-" + _PROPERTY_NAMES[1]),
			property.getProperty(appServerName + "-" + _PROPERTY_NAMES[2]),
			property.getProperty(appServerName + "-" + _PROPERTY_NAMES[3]),
			property.getProperty(appServerName + "-" + _PROPERTY_NAMES[4]));
	}

	public static Map<String, AppServer> getAppServers() {
		Properties supportedAppServerProperties = new Properties();
		InputStream inputStream = AppServer.class.getResourceAsStream(
			"/app-server-supported.properties");

		try {
			supportedAppServerProperties.load(inputStream);
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		Map<String, AppServer> appServers = new LinkedHashMap<>();

		_appServerNames.forEach(
			appServerName -> appServers.put(
				appServerName,
				getAppServer(appServerName, supportedAppServerProperties)));

		return appServers;
	}

	public AppServer(
		String dirName, String extraLibDirNames, String globalLibDirName,
		String portalDirName, String serverDetectorServerId) {

		_setDirName(dirName);

		_extraLibDirNames = extraLibDirNames;
		_globalLibDirName = globalLibDirName;
		_portalDirName = portalDirName;
		_serverDetectorServerId = serverDetectorServerId;
	}

	public File getDir() {
		return _dir;
	}

	public String getExtraLibDirNames() {
		return _extraLibDirNames;
	}

	public List<File> getExtraLibDirs() {
		List<File> extraLibDirs = new ArrayList<>();

		if ((_extraLibDirNames != null) && !_extraLibDirNames.isEmpty()) {
			for (String extraLibDirName : _extraLibDirNames.split(",")) {
				extraLibDirs.add(new File(_dir, extraLibDirName));
			}
		}

		return extraLibDirs;
	}

	public File getGlobalLibDir() {
		return new File(_dir, _globalLibDirName);
	}

	public String getGlobalLibDirName() {
		return _globalLibDirName;
	}

	public File getPortalClassesDir() {
		return new File(getPortalDir(), "/WEB-INF/classes");
	}

	public File getPortalDir() {
		return new File(_dir, _portalDirName);
	}

	public String getPortalDirName() {
		return _portalDirName;
	}

	public File getPortalLibDir() {
		return new File(getPortalDir(), "/WEB-INF/lib");
	}

	public String getServerDetectorServerId() {
		return _serverDetectorServerId;
	}

	public void setDirName(String dirName) {
		_setDirName(dirName);
	}

	public void setExtraLibDirNames(String extraLibDirNames) {
		_extraLibDirNames = extraLibDirNames;
	}

	public void setGlobalLibDirName(String globalLibDirName) {
		_globalLibDirName = globalLibDirName;
	}

	public void setPortalDirName(String portalDirName) {
		_portalDirName = portalDirName;
	}

	private void _setDirName(String dirName) {
		try {
			_dir = new File(dirName);

			if (!_dir.isAbsolute()) {
				_dir = _dir.getCanonicalFile();
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static final String[] _PROPERTY_NAMES = {
		"dir", "extra.lib.dirs", "global.lib.dir", "portal.dir",
		"server.detector.server.id"
	};

	private static final Set<String> _appServerNames = new HashSet<String>() {
		{
			add("jboss");
			add("tcserver");
			add("tomcat");
			add("weblogic");
			add("websphere");
			add("wildfly");
		}
	};

	private File _dir;
	private String _extraLibDirNames;
	private String _globalLibDirName;
	private String _portalDirName;
	private final String _serverDetectorServerId;

}