/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.external.data.source.test.internal;

import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceProvider;
import com.liferay.portal.kernel.util.PropsUtil;

import javax.sql.DataSource;

/**
 * @author Preston Crary
 */
public class DataSourceProviderImpl implements DataSourceProvider {

	@Override
	public DataSource getDataSource() {
		try {
			System.out.println(
				"######## Creating data source for the e-d-s test service!");

			DataSource dataSource = DataSourceFactoryUtil.initDataSource(
				PropsUtil.getProperties("jdbc.test.", true));

			System.out.println("######## " + (dataSource == null));

			return dataSource;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

}