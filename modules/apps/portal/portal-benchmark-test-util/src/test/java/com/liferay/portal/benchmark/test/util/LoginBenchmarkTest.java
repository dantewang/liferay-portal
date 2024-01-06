/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
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

	@Before
	public void setUp() throws Exception {
		_benchmarksDir = new File(System.getProperty("benchmarksDir"));
		_threadCount = GetterUtil.getInteger(System.getProperty("threadCount"));
		_runCount = GetterUtil.getInteger(System.getProperty("runCount"));

		System.out.println(
			StringBundler.concat(
				"\nBenchmarks directory: ", _benchmarksDir.getCanonicalPath(),
				"\nRun count: ", _runCount, "\nThread count: ", _threadCount));

		File userCSVFile = new File(_benchmarksDir, "user.csv");

		if (userCSVFile.exists()) {
			System.out.println(
				"Loading test data from " + userCSVFile.getCanonicalPath());

			_userData = _parseCSVFile(userCSVFile);
		}
		else {
			_userData = new String[][] {new String[] {"localhost", "", "test"}};
		}
	}

	@Test
	public void testLogin() throws Exception {
		ExecutorService executorService = new ThreadPoolExecutor(
			_threadCount, _threadCount, 0, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>());

		List<Future<Void>> futures = new ArrayList<>();

		for (int i = 0; i < _runCount; i++) {
			String[] user = _userData[i % _userData.length];

			futures.add(
				executorService.submit(
					() -> {
						Login login = new Login(user[0], 8080);

						login.execute(user[2] + "@liferay.com", "test");

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}
	}

	private String[][] _parseCSVFile(File csvFile) {
		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(csvFile))) {

			List<String[]> entries = new ArrayList<>();

			String line = unsyncBufferedReader.readLine();

			int columnCount = 0;

			while ((line != null) && (line.length() > 0)) {
				List<String> entry = StringUtil.split(line);

				if (columnCount == 0) {
					columnCount = entry.size();
				}
				else if (columnCount != entry.size()) {
					throw new Exception(
						"Bad csv file format, line " + (entries.size() + 1) +
							" missing \",\".");
				}

				entries.add(entry.toArray(new String[columnCount]));

				line = unsyncBufferedReader.readLine();
			}

			return entries.toArray(new String[0][]);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private File _benchmarksDir;
	private int _runCount;
	private int _threadCount;
	private String[][] _userData;

}