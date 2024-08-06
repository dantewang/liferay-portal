/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.transaction;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Dante Wang
 */
public class TransactionDataSourceTypeThreadLocal {

	public static void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	public static void setUseWriteDataSource(boolean useWriteDataSource) {
		if (_enabled) {
			_transactionDataSourceTypeThreadLocal.set(useWriteDataSource);
		}
	}

	public static boolean useWriteDataSource() {
		if (_enabled) {
			return _transactionDataSourceTypeThreadLocal.get();
		}

		return false;
	}

	private static boolean _enabled;
	private static final ThreadLocal<Boolean>
		_transactionDataSourceTypeThreadLocal = new CentralizedThreadLocal<>(
			TransactionDataSourceTypeThreadLocal.class +
				"._transactionDataSourceTypeThreadLocal",
			() -> false, false);

}