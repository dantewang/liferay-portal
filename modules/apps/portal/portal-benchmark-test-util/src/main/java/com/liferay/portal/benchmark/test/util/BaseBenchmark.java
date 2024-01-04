/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.benchmark.test.util.http.HttpResponse;
import com.liferay.portal.benchmark.test.util.http.HttpUtil;

import java.net.URL;

import org.junit.Assert;

/**
 * @author Tina Tian
 */
public abstract class BaseBenchmark {

	public BaseBenchmark(String hostName, int port) {
		_hostName = hostName;
		_port = port;
	}

	public String homePage() throws Exception {
		HttpResponse httpResponse = HttpUtil.doGet(_newURL("/"), null);

		_assertResult(httpResponse, _KEY_HOME_PAGE);

		return httpResponse.getCSRFToken();
	}

	public void login(String userEmail, String password, String csrfToken)
		throws Exception {

		_assertRedirect(
			HttpUtil.doPost(
				_newURL(_URL_LOGIN_POST),
				new String[][] {
					{
						_P_P_ID_PREFIX + "_formDate",
						String.valueOf(System.currentTimeMillis())
					},
					{_P_P_ID_PREFIX + "_saveLastPath", "false"},
					{_P_P_ID_PREFIX + "_redirect", ""},
					{_P_P_ID_PREFIX + "_doActionAfterLogin", "false"},
					{_P_P_ID_PREFIX + "_login", userEmail},
					{_P_P_ID_PREFIX + "_password", password},
					{_P_P_ID_PREFIX + "_checkboxNames", "rememberMe"}
				},
				csrfToken, null),
			_URL_REDIRECT);

		_assertRedirect(
			HttpUtil.doGet(_newURL(_URL_REDIRECT), csrfToken),
			_URL_LOGIN_REDIRECT);

		_assertResult(
			HttpUtil.doGet(_newURL(_URL_LOGIN_REDIRECT), csrfToken),
			_KEY_LOGIN);
	}

	public void logout() throws Exception {
		HttpUtil.doGet(_newURL(_URL_LOGOUT), null);
	}

	public void viewLoginPage(String csrfToken) throws Exception {
		_assertRedirect(
			HttpUtil.doGet(_newURL(_URL_LOGIN_POPUP), csrfToken),
			_URL_LOGIN_POPUP_REDIRECT);

		_assertResult(
			HttpUtil.doGet(_newURL(_URL_LOGIN_POPUP_REDIRECT), csrfToken),
			_KEY_LOGIN_POPUP);
	}

	private void _assertRedirect(
			HttpResponse httpResponse, String expectedRedirect)
		throws Exception {

		Assert.assertEquals(302, httpResponse.getStatusCode());

		URL url = _newURL(expectedRedirect);

		Assert.assertEquals(url.toString(), httpResponse.getRedirect());
	}

	private void _assertResult(HttpResponse httpResponse, String key) {
		Assert.assertEquals(200, httpResponse.getStatusCode());

		if (key != null) {
			String httpResponseString = httpResponse.toString();

			Assert.assertTrue(httpResponseString.contains(key));
		}
	}

	private URL _newURL(String path) throws Exception {
		return new URL("http", _hostName, _port, path);
	}

	private static final String _KEY_HOME_PAGE = "Liferay.currentURL";

	private static final String _KEY_LOGIN =
		"ProductNavigationUserPersonalBarPortlet";

	private static final String _KEY_LOGIN_POPUP = "Remember Me";

	private static final String _P_P_ID =
		"com_liferay_login_web_portlet_LoginPortlet";

	private static final String _P_P_ID_PREFIX = "_" + _P_P_ID;

	private static final String _URL_LOGIN_POPUP =
		"/c/portal/login?windowState=exclusive";

	private static final String _URL_LOGIN_POPUP_REDIRECT =
		StringBundler.concat(
			"/home?p_p_id=", _P_P_ID, "&p_p_lifecycle=0&",
			"p_p_state=exclusive&p_p_mode=view&", _P_P_ID_PREFIX,
			"_mvcRenderCommandName=/login/login&saveLastPath=false");

	private static final String _URL_LOGIN_POST = StringBundler.concat(
		"/home?p_p_id=", _P_P_ID, "&p_p_lifecycle=1&",
		"p_p_state=normal&p_p_mode=view&", _P_P_ID_PREFIX,
		"_javax.portlet.action=/login/login&", _P_P_ID_PREFIX,
		"_mvcRenderCommandName=/login/login");

	private static final String _URL_LOGIN_REDIRECT = StringPool.SLASH;

	private static final String _URL_LOGOUT = "/c/portal/logout";

	private static final String _URL_REDIRECT = "/c";

	private final String _hostName;
	private final int _port;

}