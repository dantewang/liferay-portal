/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.portal.benchmark.test.util.http.HttpResponse;
import com.liferay.portal.benchmark.test.util.http.SimpleCookieStore;
import com.liferay.portal.benchmark.test.util.http.ThreadLocalCookieStore;

/**
 * @author Tina Tian
 */
public class Login extends BaseBenchmark {

	public Login(
		String hostName, int port, String userEmail, String password,
		Statistics statistics) {

		super(hostName, port);

		_userEmail = userEmail;
		_password = password;
		_statistics = statistics;
	}

	public void execute() throws Exception {
		ThreadLocalCookieStore.setCookieStore(new SimpleCookieStore());

		HttpResponse httpResponse = homePage();

		_statistics.record("homePage", httpResponse.getDuration());

		_statistics.record(
			"viewLoginPage", viewLoginPage(httpResponse.getCSRFToken()));

		_statistics.record(
			"login", login(_userEmail, _password, httpResponse.getCSRFToken()));

		_statistics.record("logout", logout());

		ThreadLocalCookieStore.removeCookieStore();
	}

	private final String _password;
	private final Statistics _statistics;
	private final String _userEmail;

}