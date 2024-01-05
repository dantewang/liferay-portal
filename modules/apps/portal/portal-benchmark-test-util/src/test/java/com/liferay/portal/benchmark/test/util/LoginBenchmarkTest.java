/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class LoginBenchmarkTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testLogin() throws Exception {
		int threadCount = GetterUtil.getInteger(
			System.getProperty("threadCount"));
		int runCount = GetterUtil.getInteger(System.getProperty("runCount"));

		ExecutorService executorService = new ThreadPoolExecutor(
			threadCount, threadCount, 0, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>());

		List<Future<Void>> futures = new ArrayList<>();

		String[][] userData = _getUserData(
			System.getProperty("databaseHost"),
			System.getProperty("databaseName"),
			System.getProperty("databaseUser"),
			System.getProperty("databasePassword"));

		for (int i = 0; i < runCount; i++) {
			String[] user = userData[i % userData.length];

			futures.add(
				executorService.submit(
					() -> {
						Login login = new Login(user[0], 8080);

						login.execute(user[1], "test");

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}
	}

	private String[][] _getUserData(
			String databaseHost, String databaseName, String user,
			String password)
		throws Exception {

		Class.forName(
			"com.mysql.cj.jdbc.Driver"
		).newInstance();

		List<String[]> users = new ArrayList<>();

		String url = StringBundler.concat(
			"jdbc:mysql://", databaseHost, "/", databaseName,
			"?characterEncoding=UTF-8&dontTrackOpenResources=true&",
			"holdResultsOpenOverStatementClose=true&",
			"useFastDateParsing=false&useUnicode=true&");

		try (Connection connection = DriverManager.getConnection(
				url, user, password);
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select hostname, emailAddress from VirtualHost as vh, User_ " +
					"as u where u.emailAddress like 'test%' and u.companyId " +
						"= vh.companyId;");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				users.add(
					new String[] {
						resultSet.getString("hostname"),
						resultSet.getString("emailAddress")
					});
			}
		}

		return users.toArray(new String[0][0]);
	}

}