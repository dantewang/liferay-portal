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

import com.liferay.portal.tools.db.upgrade.client.util.StringUtil;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David Truong
 */
public class AppServer {

	public static AppServer getJBossEAPAppServer() {
		StringBuilder sb = new StringBuilder();

		String extraLibDirPrefix = "/modules/system/layers/base/";

		sb.append(extraLibDirPrefix);

		sb.append("javax/mail,");
		sb.append(extraLibDirPrefix);
		sb.append("javax/persistence,");
		sb.append(extraLibDirPrefix);
		sb.append("javax/servlet,");
		sb.append(extraLibDirPrefix);
		sb.append("javax/transaction");

		return new AppServer(
			"../../jboss-eap-6.4.0", sb.toString(),
			"/modules/com/liferay/portal/main",
			"/standalone/deployments/ROOT.war", "jboss");
	}

	public static AppServer getJOnASAppServer() {
		return new AppServer(
			"../../jonas-5.2.3", "", "/lib/ext", "/deploy/liferay-portal",
			"jonas");
	}

	public static AppServer getResinAppServer() {
		return new AppServer(
			"../../resin-4.0.44", "", "/ext-lib", "/webapps/ROOT", "resin");
	}

	public static AppServer getTCServerAppServer() {
		return new AppServer(
			"../../tc-server-2.9.11", "", "/liferay/lib",
			"/liferay/webapps/ROOT", "tomcat");
	}

	public static AppServer getTomcatAppServer() {
		return new AppServer(
			"../../tomcat-8.0.32", "/bin", "/lib", "/webapps/ROOT", "tomcat");
	}

	public static AppServer getWebLogicAppServer() {
		return new AppServer(
			"../../weblogic-12.1.3", "", "/domains/liferay/lib",
			"/domains/liferay/autodeploy/ROOT", "weblogic");
	}

	public static AppServer getWebSphereAppServer() {
		return new AppServer(
			"../../websphere-8.5.5.0", "", "/lib/ext",
			"/profiles/liferay/installedApps/liferay-cell/liferay-portal.ear" +
				"/liferay-portal.war",
			"websphere");
	}

	public static AppServer getWildFlyAppServer() {
		StringBuilder sb = new StringBuilder();

		String extraLibDirPrefix = "/modules/system/layers/base/";

		sb.append(extraLibDirPrefix);

		sb.append("javax/mail,");
		sb.append(extraLibDirPrefix);
		sb.append("javax/persistence,");
		sb.append(extraLibDirPrefix);
		sb.append("javax/servlet,");
		sb.append(extraLibDirPrefix);
		sb.append("javax/transaction");

		return new AppServer(
			"../../wildfly-10.0.0", sb.toString(),
			"/modules/com/liferay/portal/main",
			"/standalone/deployments/ROOT.war", "wildfly");
	}

	public AppServer(
		String dirName, String extraLibDirNames, String globalLibDirName,
		String portalDirName, String serverDetectorServerId) {

		Path appServerPath = _normalizePath(Paths.get(dirName));

		_dir = appServerPath.toFile();

		if (extraLibDirNames != null) {
			for (String extraLibDirName : extraLibDirNames.split(",")) {
				Path extraLibPath = _resolveToAppServerPath(
					Paths.get(extraLibDirName));

				_extraLibDirs.add(extraLibPath.toFile());
			}
		}

		Path globalLibPath = _resolveToAppServerPath(
			Paths.get(globalLibDirName));

		_globalLibDir = globalLibPath.toFile();

		Path portalPath = _resolveToAppServerPath(Paths.get(portalDirName));

		_portalDir = portalPath.toFile();

		_serverDetectorServerId = serverDetectorServerId;
	}

	public File getDir() {
		return _dir;
	}

	public String getExtraLibDirNames() {
		return StringUtil.join(_extraLibDirs, ',');
	}

	public List<File> getExtraLibDirs() {
		return _extraLibDirs;
	}

	public File getGlobalLibDir() {
		return _globalLibDir;
	}

	public File getPortalClassesDir() {
		return new File(_portalDir, "/WEB-INF/classes");
	}

	public File getPortalDir() {
		return _portalDir;
	}

	public File getPortalLibDir() {
		return new File(_portalDir, "/WEB-INF/lib");
	}

	public String getServerDetectorServerId() {
		return _serverDetectorServerId;
	}

	public void setDirName(String dirName) {
		Path appServerPath = _normalizePath(Paths.get(dirName));

		_dir = appServerPath.toFile();
	}

	public void setExtraLibDirNames(String extraLibDirNames) {
		if (extraLibDirNames != null) {
			for (String extraLibDirName : extraLibDirNames.split(",")) {
				Path extraLibPath = _resolveToAppServerPath(
					Paths.get(extraLibDirName));

				_extraLibDirs.add(extraLibPath.toFile());
			}
		}
	}

	public void setGlobalLibDirName(String globalLibDirName) {
		Path globalLibPath = _resolveToAppServerPath(
			Paths.get(globalLibDirName));

		_globalLibDir = globalLibPath.toFile();
	}

	public void setPortalDirName(String portalDirName) {
		Path portalPath = _resolveToAppServerPath(Paths.get(portalDirName));

		_portalDir = portalPath.toFile();
	}

	private Path _normalizePath(Path path) {
		path = path.toAbsolutePath();
		path = path.normalize();

		return path;
	}

	private Path _resolveToAppServerPath(Path path) {
		Path appServerPath = _dir.toPath();

		path = _normalizePath(path);

		if (path.startsWith(appServerPath)) {
			return path;
		}

		path = Paths.get(appServerPath.toString(), path.toString());

		return _normalizePath(path);
	}

	private File _dir;
	private final List<File> _extraLibDirs = new ArrayList<>();
	private File _globalLibDir;
	private File _portalDir;
	private final String _serverDetectorServerId;

}