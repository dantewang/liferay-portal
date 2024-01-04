/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.internal.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dante Wang
 */
public class DatabaseDataUtil {

	public static String[][] getUsersMap(
			String url, String user, String password)
		throws Exception {

		Class.forName(
			"com.mysql.cj.jdbc.Driver"
		).newInstance();

		List<String[]> users = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection(
				url, user, password);
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select companyId, emailAddress from User_ where " +
					"emailAddress like 'test%'");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select webId from Company where companyId = ?");
			ResultSet resultSet1 = preparedStatement1.executeQuery()) {

			while (resultSet1.next()) {
				long companyId = resultSet1.getLong("companyId");
				String emailAddress = resultSet1.getString("emailAddress");

				preparedStatement2.setLong(1, companyId);

				try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
					String webId = resultSet2.getString(1);

					users.add(new String[] {webId, emailAddress});
				}
			}
		}

		return users.toArray(new String[0][0]);
	}

}