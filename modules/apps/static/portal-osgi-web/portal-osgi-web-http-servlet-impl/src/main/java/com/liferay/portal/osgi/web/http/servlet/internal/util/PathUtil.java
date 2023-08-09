/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.util;

/**
 * @author Dante Wang
 */
public class PathUtil {

	public static String extractRequestURI(String path) {
		int index = path.indexOf('?');

		if (index == -1) {
			return path;
		}

		return path.substring(0, index);
	}

}