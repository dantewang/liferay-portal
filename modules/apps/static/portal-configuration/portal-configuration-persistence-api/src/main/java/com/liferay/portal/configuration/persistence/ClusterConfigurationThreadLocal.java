/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Brian Wing Shun Chan
 */
public class ClusterConfigurationThreadLocal {

	public static boolean isProcessingClusterConfigurationEvent() {
		return _processingClusterConfigurationEvent.get();
	}

	public static void setProcessingClusterConfigurationEvent(
		boolean processingClusterConfigurationEvent) {

		_processingClusterConfigurationEvent.set(
			processingClusterConfigurationEvent);
	}

	private static final ThreadLocal<Boolean>
		_processingClusterConfigurationEvent = new CentralizedThreadLocal<>(
			ClusterConfigurationThreadLocal.class + "._localUpdate",
			() -> Boolean.FALSE, false);

}