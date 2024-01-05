/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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
			System.getProperty("threadCount"), 1);
		int runCount = GetterUtil.getInteger(System.getProperty("runCount"), 1);

		ExecutorService executorService = new ThreadPoolExecutor(
			threadCount, threadCount, 0, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>());

		List<Future<Void>> futures = new ArrayList<>();

		for (int i = 1; i <= runCount; i++) {
			futures.add(
				executorService.submit(
					() -> {
						Login login = new Login("localhost", 8080);

						login.execute("test@liferay.com", "test");

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}
	}

}