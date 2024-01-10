/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class LoginBenchmarkTest extends BaseBenchmarkTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testLogin() throws Exception {
		String[][] data = _loadDataFromDB();

		if (!isSkipWarmUp()) {
			System.out.println("\nStart warm up data...");

			_execute(data, 1, data.length, integer -> integer);

			System.out.println("\nWarm up finished!");
		}

		System.out.println("\nStart running test...");

		_execute(
			data, getThreadCount(), getRunCount(),
			integer -> integer % data.length);

		System.out.println("\nTest run finished!");
	}

	private void _execute(
			String[][] data, int threadCount, int runCount,
			Function<Integer, Integer> indexFunction)
		throws Exception {

		ExecutorService executorService = new ThreadPoolExecutor(
			threadCount, threadCount, 0, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>());

		List<Future<Void>> futures = new ArrayList<>();

		Statistics statistics = new Statistics(runCount);

		statistics.start();

		for (int i = 0; i < runCount; i++) {
			String[] user = data[indexFunction.apply(i)];

			futures.add(
				executorService.submit(
					() -> {
						Login login = new Login(
							user[0], 8080, user[1], user[2], statistics);

						login.execute();

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}

		statistics.finish();
	}

	private String[][] _loadDataFromDB() throws Exception {
		List<String[]> data = new ArrayList<>();

		StringBundler sb = new StringBundler(7);

		sb.append("select hostname, emailAddress, password_ from Company as ");
		sb.append("company, VirtualHost as virtualHost, User_ as user where ");

		List<String> excludedCompanies = getExcludedCompanies();

		if (!excludedCompanies.isEmpty()) {
			sb.append("company.webId not in (");

			for (int i = 0; i < excludedCompanies.size(); i++) {
				sb.append("'");
				sb.append(excludedCompanies.get(i));
				sb.append("'");

				if (i < (excludedCompanies.size() - 1)) {
					sb.append(",");
				}
			}

			sb.append(") and ");
		}

		sb.append("user.type_ = 1 and user.companyId = company.companyId and ");
		sb.append("virtualHost.companyId = company.companyId;");

		String sql = sb.toString();

		System.out.println("\nDatabase load SQL : " + sql);

		try (Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String password = resultSet.getString("password_");

				if (password.startsWith(StringPool.OPEN_CURLY_BRACE)) {
					password = password.substring(
						password.indexOf(StringPool.CLOSE_CURLY_BRACE) + 1);
				}

				data.add(
					new String[] {
						resultSet.getString("hostname"),
						resultSet.getString("emailAddress"), password
					});
			}
		}

		return data.toArray(new String[0][0]);
	}

}