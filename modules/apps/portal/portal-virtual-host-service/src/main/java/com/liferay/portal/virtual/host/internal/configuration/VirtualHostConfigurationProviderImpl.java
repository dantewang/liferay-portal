/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.virtual.host.internal.configuration;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.virtual.host.configuration.provider.VirtualHostConfigurationProvider;
import com.liferay.portal.virtual.host.configuration.VirtualHostConfiguration;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(service = VirtualHostConfigurationProvider.class)
public class VirtualHostConfigurationProviderImpl
	implements VirtualHostConfigurationProvider {

	@Override
	public boolean isDefaultInstanceURLBypassAllowed(long companyId)
		throws ConfigurationException {

		VirtualHostConfiguration virtualHostConfiguration =
			_configurationProvider.getCompanyConfiguration(
				VirtualHostConfiguration.class, companyId);

		return virtualHostConfiguration.allowDefaultInstanceURLBypass();
	}

	@Override
	public boolean isStrictModeEnabled(long companyId)
		throws ConfigurationException {

		VirtualHostConfiguration virtualHostConfiguration =
			_configurationProvider.getCompanyConfiguration(
				VirtualHostConfiguration.class, companyId);

		return virtualHostConfiguration.strictModeEnabled();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}