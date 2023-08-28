/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lily Chi
 */
@RunWith(Arquillian.class)
public class AddUsersByVIIAPITest {

	@Test
	public void testAddUsersByVIIAPI() throws Exception {
		String jsonTemplate = new String(
			FileUtil.getBytes(getClass(), "/test.json"));

		int userCount = 10;

		_alternateNames = _generateString(10);

		long totalDelats = 0;

		System.out.println("##########Starting add users");

		for (String alternateName : _alternateNames) {
			String json = jsonTemplate;

			json = StringUtil.replace(json, _ALTER_NAME_TOKEN, alternateName);

			json = StringUtil.replace(
				json, _EMAILADDRESS_TOKEN, alternateName + _EMAIL_PREFIX);

			long before = System.currentTimeMillis();

			_invoke(JSONFactoryUtil.createJSONObject(json));

			long after = System.currentTimeMillis();

			long delta = after - before;

			totalDelats += delta;
		}

		System.out.println(
			StringBundler.concat(
				"##########Finishe adding ", userCount, " users with ",
				totalDelats, " ms"));
	}

	private List<String> _generateString(int size) {
		List<String> strings = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			strings.add(_getRandomString());
		}

		return strings;
	}

	private String _getRandomString() {
		String str =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		Random random = new Random();

		StringBundler sb = new StringBundler();

		for (int i = 0; i < 8; i++) {
			int number = random.nextInt(62);

			sb.append(str.charAt(number));
		}

		return sb.toString();
	}

	private String _invoke(JSONObject jsonObject) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(jsonObject.toString(), "application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path(
			"http://localhost:8080/o/headless-admin-user/v1.0/user-accounts");
		httpInvoker.userNameAndPassword("test@liferay.com:test");

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	private static final String _ALTER_NAME_TOKEN = "@alternateName@";

	private static final String _EMAIL_PREFIX = "@VodafoneIdea.com";

	private static final String _EMAILADDRESS_TOKEN = "@emailAddress@";

	private List<String> _alternateNames = new ArrayList<>();

}