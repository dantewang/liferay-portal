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

package com.liferay.portal.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.SystemPropsKeys;

/**
 * @author Jiaxu Wei
 */
public class SystemPropsValues {

	public static final String LIFERAY_HOME = SystemProperties.get(
		SystemPropsKeys.LIFERAY_HOME);

	public static final String[] MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS =
		SystemProperties.getArray(
			SystemPropsKeys.MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS);

	public static final long MODULE_FRAMEWORK_AUTO_DEPLOY_INTERVAL =
		GetterUtil.getLong(
			SystemProperties.get(
				SystemPropsKeys.MODULE_FRAMEWORK_AUTO_DEPLOY_INTERVAL),
			2000);

	public static final String MODULE_FRAMEWORK_BASE_DIR = SystemProperties.get(
		SystemPropsKeys.MODULE_FRAMEWORK_BASE_DIR);

	public static final int MODULE_FRAMEWORK_BEGINNING_START_LEVEL =
		GetterUtil.getInteger(
			SystemProperties.get(
				SystemPropsKeys.MODULE_FRAMEWORK_BEGINNING_START_LEVEL));

	public static final boolean MODULE_FRAMEWORK_CONCURRENT_STARTUP_ENABLED =
		GetterUtil.getBoolean(
			SystemProperties.get(
				SystemPropsKeys.MODULE_FRAMEWORK_CONCURRENT_STARTUP_ENABLED));

	public static final String MODULE_FRAMEWORK_CONFIGS_DIR =
		SystemProperties.get(SystemPropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR);

	public static final String[]
		MODULE_FRAMEWORK_CONFIGURATION_BUNDLE_SYMBOLIC_NAMES =
			SystemProperties.getArray(
				SystemPropsKeys.
					MODULE_FRAMEWORK_CONFIGURATION_BUNDLE_SYMBOLIC_NAMES);

	public static final int MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL =
		GetterUtil.getInteger(
			SystemProperties.get(
				SystemPropsKeys.MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL));

	public static final boolean MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES =
		GetterUtil.getBoolean(
			SystemProperties.get(
				SystemPropsKeys.MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES));

	public static final String MODULE_FRAMEWORK_MARKETPLACE_DIR =
		SystemProperties.get(SystemPropsKeys.MODULE_FRAMEWORK_MARKETPLACE_DIR);

	public static final String MODULE_FRAMEWORK_MODULES_DIR =
		SystemProperties.get(SystemPropsKeys.MODULE_FRAMEWORK_MODULES_DIR);

	public static final String MODULE_FRAMEWORK_PORTAL_DIR =
		SystemProperties.get(SystemPropsKeys.MODULE_FRAMEWORK_PORTAL_DIR);

	public static final String MODULE_FRAMEWORK_RESOLVER_REVISION_BATCH_SIZE =
		SystemProperties.get(
			SystemPropsKeys.MODULE_FRAMEWORK_RESOLVER_REVISION_BATCH_SIZE);

	public static final int MODULE_FRAMEWORK_RUNTIME_START_LEVEL =
		GetterUtil.getInteger(
			SystemProperties.get(
				SystemPropsKeys.MODULE_FRAMEWORK_RUNTIME_START_LEVEL));

	public static final String MODULE_FRAMEWORK_STATE_DIR =
		SystemProperties.get(SystemPropsKeys.MODULE_FRAMEWORK_STATE_DIR);

	public static final String MODULE_FRAMEWORK_WAR_DIR = SystemProperties.get(
		SystemPropsKeys.MODULE_FRAMEWORK_WAR_DIR);

	public static final String RESOURCE_REPOSITORIES_ROOT =
		SystemProperties.get(SystemPropsKeys.RESOURCE_REPOSITORIES_ROOT);

}