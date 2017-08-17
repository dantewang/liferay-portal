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

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Truong
 */
public class AppServer {

	public static AppServer getJBossEAPAppServer() {
		return new AppServer(
			"/opt/liferay/jboss-eap-6.4.0", _getJBossExtraLibDirNames(),
			"/modules/com/liferay/portal/main",
			"/standalone/deployments/ROOT.war", "jboss");
	}

	public static AppServer getJOnASAppServer() {
		return new AppServer(
			"/opt/liferay/jonas-5.2.3", "", "/lib/ext",
			"/deploy/liferay-portal", "jonas");
	}

	public static AppServer getResinAppServer() {
		return new AppServer(
			"/opt/liferay/resin-4.0.44", "", "/ext-lib", "/webapps/ROOT",
			"resin");
	}

	public static AppServer getTCServerAppServer() {
		return new AppServer(
			"/opt/liferay/tc-server-2.9.11", "/tomcat-7.0.64.B.RELEASE/lib",
			"/liferay/lib", "/liferay/webapps/ROOT", "tomcat");
	}

	public static AppServer getTomcatAppServer() {
		return new AppServer(
			"/opt/liferay/tomcat-8.0.32", "/bin", "/lib", "/webapps/ROOT",
			"tomcat");
	}

	public static AppServer getWebLogicAppServer() {
		return new AppServer(
			"/opt/liferay/weblogic-12.1.3", "/bin", "/domains/liferay/lib",
			"/domains/liferay/autodeploy/ROOT", "weblogic");
	}

	public static AppServer getWebSphereAppServer() {
		return new AppServer(
			"/opt/liferay/websphere-8.5.5.0", "", "/lib",
			"/profiles/liferay/installedApps/liferay-cell/liferay-portal.ear" +
				"/liferay-portal.war",
			"websphere");
	}

	public static AppServer getWildFlyAppServer() {
		return new AppServer(
			"/opt/liferay/wildfly-10.0.0", _getJBossExtraLibDirNames(),
			"/modules/com/liferay/portal/main",
			"/standalone/deployments/ROOT.war", "wildfly");
	}

	public AppServer(
		String dirName, String extraLibDirNames, String globalLibDirName,
		String portalDirName, String serverDetectorServerId) {

		_dirName = dirName;
		_globalLibDirName = globalLibDirName;
		_portalDirName = portalDirName;
		_serverDetectorServerId = serverDetectorServerId;

		_setExtraLibDirNames(extraLibDirNames);
	}

	public File getDir() {
		return new File(_dirName);
	}

	public String getExtraLibDirNames() {
		return StringUtil.join(_extraLibDirNames, ',');
	}

	public List<File> getExtraLibDirs() {
		List<File> extraLibDirs = new ArrayList<>();

		for (String extraLibDirName : _extraLibDirNames) {
			extraLibDirs.add(new File(getDir(), extraLibDirName));
		}

		return extraLibDirs;
	}

	public File getGlobalLibDir() {
		return new File(getDir(), _globalLibDirName);
	}

	public String getGlobalLibDirName() {
		return _globalLibDirName;
	}

	public File getPortalClassesDir() {
		return new File(getPortalDir(), "/WEB-INF/classes");
	}

	public File getPortalDir() {
		return new File(getDir(), _portalDirName);
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
		_dirName = dirName;
	}

	public void setExtraLibDirNames(String extraLibDirNames) {
		_setExtraLibDirNames(extraLibDirNames);
	}

	public void setGlobalLibDirName(String globalLibDirName) {
		_globalLibDirName = globalLibDirName;
	}

	public void setPortalDirName(String portalDirName) {
		_portalDirName = portalDirName;
	}

	private static String _getJBossExtraLibDirNames() {
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

		return sb.toString();
	}

	private void _setExtraLibDirNames(String extraLibDirNames) {
		if ((extraLibDirNames != null) && !extraLibDirNames.isEmpty()) {
			for (String extraLibDirName : extraLibDirNames.split(",")) {
				_extraLibDirNames.add(extraLibDirName);
			}
		}
	}

	private String _dirName;
	private final List<String> _extraLibDirNames = new ArrayList<>();
	private String _globalLibDirName;
	private String _portalDirName;
	private final String _serverDetectorServerId;

}