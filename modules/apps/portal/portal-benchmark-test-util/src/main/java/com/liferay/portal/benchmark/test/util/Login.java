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

	public Login(String hostName, int port) {
		super(hostName, port);
	}

	public void execute(
			String userEmail, String password, Statistics statistics)
		throws Exception {

		ThreadLocalCookieStore.setCookieStore(new SimpleCookieStore());

		HttpResponse httpResponse = homePage();

		statistics.record("homePage", httpResponse.getDuration());

		statistics.record(
			"viewLoginPage", viewLoginPage(httpResponse.getCSRFToken()));

		statistics.record(
			"login", login(userEmail, password, httpResponse.getCSRFToken()));

		statistics.record("logout", logout());

		ThreadLocalCookieStore.removeCookieStore();
	}

}