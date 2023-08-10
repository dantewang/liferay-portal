/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.util;

import com.liferay.petra.string.StringPool;

/**
 * @author Dante Wang
 */
public class PatternUtil {

	public static void checkPattern(String pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern cannot be null");
		}

		if (pattern.indexOf(Const.STAR_DOT) == 0) {
			return;
		}

		if (!pattern.startsWith(StringPool.SLASH) ||
			(pattern.endsWith(StringPool.SLASH) &&
			 !pattern.equals(StringPool.SLASH))) {

			throw new IllegalArgumentException(
				"Invalid pattern '" + pattern + "'");
		}
	}

}