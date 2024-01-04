/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.internal;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Dante Wang
 */
public class ContextUtil {

	public static Context getContext() {
		return _context.get();
	}

	public static void setContext(Context context) {
		if (context == null) {
			_context.remove();
		}

		_context.set(context);
	}

	public static class Context {

		public Context(String[] user) {
			_hostName = user[0];
			_userEmail = user[1];
		}

		public String getCSRFToken() {
			return _csrfToken;
		}

		public String getHostName() {
			return _hostName;
		}

		public String getUserEmail() {
			return _userEmail;
		}

		public void setCSRFToken(String csrfToken) {
			_csrfToken = csrfToken;
		}

		private String _csrfToken;
		private final String _hostName;
		private final String _userEmail;

	}

	private static final ThreadLocal<Context> _context =
		CentralizedThreadLocal.withInitial(() -> null);

}