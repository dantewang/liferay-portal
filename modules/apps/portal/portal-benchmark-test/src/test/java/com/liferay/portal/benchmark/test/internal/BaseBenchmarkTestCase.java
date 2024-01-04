/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.internal;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Tina Tian
 */
public abstract class BaseBenchmarkTestCase {

	@Before
	public void setUp() {
		_executorService = new ThreadPoolExecutor(
			getThreadPoolSize(), getThreadPoolSize(), 0, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>());
	}

	@After
	public void tearDown() {
		_executorService.shutdownNow();
	}

	protected abstract int getThreadPoolSize();

	protected abstract String[][] getUserData() throws Exception;

	protected void homePage() throws Exception {
		HttpUtil.HttpResponse httpResponse = HttpUtil.doGet("/");

		_assertResult(httpResponse, _KEY_HOME_PAGE);

		Matcher matcher = _csrfTokenPattern.matcher(httpResponse.getText());

		if (matcher.find()) {
			ContextUtil.Context context = ContextUtil.getContext();

			context.setCSRFToken(matcher.group(1));
		}
	}

	protected void login() throws Exception {
		ContextUtil.Context context = ContextUtil.getContext();

		String[][] parameters = {
			{
				_P_P_ID_PREFIX + "_formDate",
				String.valueOf(System.currentTimeMillis())
			},
			{_P_P_ID_PREFIX + "_saveLastPath", "false"},
			{_P_P_ID_PREFIX + "_redirect", ""},
			{_P_P_ID_PREFIX + "_doActionAfterLogin", "false"},
			{_P_P_ID_PREFIX + "_login", context.getUserEmail()},
			{_P_P_ID_PREFIX + "_password", "test"},
			{_P_P_ID_PREFIX + "_checkboxNames", "rememberMe"}
		};

		_assertRedirect(
			HttpUtil.doPost(_URL_LOGIN_POST, parameters), _URL_REDIRECT);

		_assertRedirect(HttpUtil.doGet(_URL_REDIRECT), _URL_LOGIN_REDIRECT);

		_assertResult(HttpUtil.doGet(_URL_LOGIN_REDIRECT), _KEY_LOGIN);
	}

	protected void logout() throws Exception {
		_assertResult(HttpUtil.doGet(_URL_LOGOUT), null);

		_csrfToken.remove();
	}

	protected void runParallel(Callable<Void> callable, int runCount)
		throws Exception {

		List<Future<Void>> futures = new ArrayList<>();

		for (int i = 0; i < runCount; i++) {
			futures.add(
				_executorService.submit(
					() -> {
						ContextUtil.setContext(
							new ContextUtil.Context(_getNextUser()));

						callable.call();

						ContextUtil.setContext(null);

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}
	}

	protected void runSequential(Callable<Void> callable, int runCount)
		throws Exception {

		for (int i = 0; i < runCount; i++) {
			ContextUtil.setContext(new ContextUtil.Context(_getNextUser()));

			callable.call();

			ContextUtil.setContext(null);
		}
	}

	protected void viewLoginPage() throws Exception {
		_assertRedirect(
			HttpUtil.doGet(_URL_LOGIN_POPUP), _URL_LOGIN_POPUP_REDIRECT);

		_assertResult(
			HttpUtil.doGet(_URL_LOGIN_POPUP_REDIRECT), _KEY_LOGIN_POPUP);
	}

	private void _assertRedirect(
			HttpUtil.HttpResponse httpResponse, String expectedRedirect)
		throws Exception {

		Assert.assertEquals(302, httpResponse.getStatusCode());

		Assert.assertEquals(
			"http://localhost:8080" + expectedRedirect,
			httpResponse.getRedirect());
	}

	private void _assertResult(HttpUtil.HttpResponse httpResponse, String key) {
		Assert.assertEquals(200, httpResponse.getStatusCode());

		if (key != null) {
			String httpResponseString = httpResponse.toString();

			Assert.assertTrue(httpResponseString.contains(key));
		}
	}

	private String[] _getNextUser() throws Exception {
		String[][] userData = getUserData();

		return userData
			[_userCounter.getAndUpdate(
				current -> {
					if (current == (userData.length - 1)) {
						return 0;
					}

					return current + 1;
				})];
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

	private static final ThreadLocal<String> _csrfToken =
		CentralizedThreadLocal.withInitial(() -> null);
	private static final Pattern _csrfTokenPattern = Pattern.compile(
		"Liferay\\.authToken = '(.*)';");

	private ExecutorService _executorService;
	private final AtomicInteger _userCounter = new AtomicInteger(0);

}