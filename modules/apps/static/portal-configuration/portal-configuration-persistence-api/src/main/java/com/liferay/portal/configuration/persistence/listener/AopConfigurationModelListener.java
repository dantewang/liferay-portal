/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.listener;

import com.liferay.portal.aop.AopService;

/**
 * @author Dante Wang
 */
public interface AopConfigurationModelListener
	extends AopService, ConfigurationModelListener {

	@Override
	public default Class<?>[] getAopInterfaces() {
		return new Class<?>[] {ConfigurationModelListener.class};
	}

}