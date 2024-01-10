/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.dao.jdbc.DataSourceFactoryImpl;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactory;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;

/**
 * @author Tina Tian
 */
public abstract class BaseBenchmarkTestCase {

	@Before
	public void setUp() throws Exception {
		_excludedCompanies = StringUtil.split(
			System.getProperty("benchmark.test.excluded.companies"));
		_driverClassName = System.getProperty(
			"benchmark.test.jdbc.driverClassName");
		_jdbcUrl = System.getProperty("benchmark.test.jdbc.url");
		_jdbcUserName = System.getProperty("benchmark.test.jdbc.username");
		_jdbcPassword = System.getProperty("benchmark.test.jdbc.password");
		_runCount = GetterUtil.getInteger(
			System.getProperty("benchmark.test.run.count"));
		_skipWarmUp = GetterUtil.getBoolean(
			System.getProperty("benchmark.test.skip.warm.up"));
		_threadCount = GetterUtil.getInteger(
			System.getProperty("benchmark.test.thread.count"));

		_logProperties();

		DataSourceFactory dataSourceFactory = new DataSourceFactoryImpl();

		_dataSource = dataSourceFactory.initDataSource(
			_driverClassName, _jdbcUrl, _jdbcUserName, _jdbcPassword,
			StringPool.BLANK);
	}

	protected Connection getConnection() throws Exception {
		return _dataSource.getConnection();
	}

	protected List<String> getExcludedCompanies() {
		return _excludedCompanies;
	}

	protected int getRunCount() {
		return _runCount;
	}

	protected int getThreadCount() {
		return _threadCount;
	}

	protected boolean isSkipWarmUp() {
		return _skipWarmUp;
	}

	private void _logProperties() {
		StringBundler sb = new StringBundler(14);

		sb.append("\nCurrent properties:\n	Excluded Companies:");
		sb.append(_excludedCompanies);
		sb.append("\n	JDBC Driver Class Name:");
		sb.append(_driverClassName);
		sb.append("\n	JDBC URL:");
		sb.append(_jdbcUrl);
		sb.append("\n	JDBC User Name:");
		sb.append(_jdbcUserName);
		sb.append("\n	JDBC Password:");
		sb.append(_jdbcPassword);
		sb.append("\n	Run Count:");
		sb.append(_runCount);
		sb.append("\n	Thread Count:");
		sb.append(_threadCount);

		System.out.println(sb);
	}

	private DataSource _dataSource;
	private String _driverClassName;
	private List<String> _excludedCompanies;
	private String _jdbcPassword;
	private String _jdbcUrl;
	private String _jdbcUserName;
	private int _runCount;
	private boolean _skipWarmUp;
	private int _threadCount;

}