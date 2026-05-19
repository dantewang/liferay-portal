/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.virtual.host.configuration.provider;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Dante Wang
 */
public class VirtualHostConfigurationProviderUtil {

	public static boolean isDefaultInstanceURLBypassAllowed(long companyId) {
		try {
			VirtualHostConfigurationProvider virtualHostConfigurationProvider =
				_virtualHostConfigurationProviderSnapshot.get();

			return virtualHostConfigurationProvider.
				isDefaultInstanceURLBypassAllowed(companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get virtual host configuration for company " +
					companyId,
				configurationException);
		}

		return false;
	}

	public static boolean isStrictModeEnabled(long companyId) {
		try {
			VirtualHostConfigurationProvider virtualHostConfigurationProvider =
				_virtualHostConfigurationProviderSnapshot.get();

			return virtualHostConfigurationProvider.isStrictModeEnabled(
				companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get virtual host configuration for company " +
					companyId,
				configurationException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VirtualHostConfigurationProviderUtil.class);

	private static final Snapshot<VirtualHostConfigurationProvider>
		_virtualHostConfigurationProviderSnapshot = new Snapshot<>(
			VirtualHostConfigurationProviderUtil.class,
			VirtualHostConfigurationProvider.class);

}
