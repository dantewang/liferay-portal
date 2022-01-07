/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.test.rule.PortalProps;
import com.liferay.portlet.PortalPreferencesImpl;

import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author Dante Wang
 */
@PortalProps(
	properties = {
		PropsKeys.SESSION_CLICKS_MAX_ALLOWED_VALUES + "=10",
		PropsKeys.SESSION_CLICKS_MAX_SIZE_TERMS + "=10"
	}
)
public class SessionClicksTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testPutIntoPortalPreferences() {
		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			(PortletPreferencesFactory)ProxyUtil.newProxyInstance(
				SessionClicksTest.class.getClassLoader(),
				new Class<?>[] {PortletPreferencesFactory.class},
				(proxy, method, args) -> {
					String methodName = method.getName();

					if (methodName.equals("getPortalPreferences") &&
						(args.length == 1) &&
						(args[0] instanceof HttpServletRequest)) {

						return _portalPreferences;
					}

					return null;
				}));

		_testPut(new MockHttpServletRequest());
	}

	@Test
	public void testPutIntoSession() {
		_testPut(new MockHttpSession());
	}

	private void _testPut(Object target) {
		_testPut(target, "key1", "value1", null);

		_testPut(
			target, "key2", SessionClicks.class.getName(),
			_MAX_SIZE_TERMS_WARNING_PREFIX);

		_testPut(
			target, SessionClicks.class.getName(), "value2",
			_MAX_SIZE_TERMS_WARNING_PREFIX);

		for (int i = 1; i <= 20; i++) {
			String warningMessagePrefix = null;

			if (i > 10) {
				warningMessagePrefix = _MAX_ALLOWED_VALUES_WARNING_PREFIX;
			}

			_testPut(target, "key" + i, "value" + i, warningMessagePrefix);
		}
	}

	private void _testPut(
		Object target, String key, String value, String expectedMessagePrefix) {

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				SessionClicks.class.getName(), Level.WARNING)) {

			if (target instanceof HttpServletRequest) {
				SessionClicks.put((HttpServletRequest)target, key, value);
			}
			else {
				SessionClicks.put((HttpSession)target, key, value);
			}

			String actualValue = null;

			if (target instanceof HttpServletRequest) {
				actualValue = SessionClicks.get(
					(HttpServletRequest)target, key, null);
			}
			else {
				actualValue = SessionClicks.get((HttpSession)target, key, null);
			}

			if (expectedMessagePrefix == null) {
				Assert.assertEquals(value, actualValue);

				return;
			}

			Assert.assertNull(actualValue);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					expectedMessagePrefix, " {key=", key, ", value=", value,
					"}"),
				logEntry.getMessage());
		}
	}

	private static final String _MAX_ALLOWED_VALUES_WARNING_PREFIX =
		"Session clicks has attempted to exceed the maximum number of " +
			"allowed values with";

	private static final String _MAX_SIZE_TERMS_WARNING_PREFIX =
		"Session clicks has attempted to exceed the maximum size allowed for " +
			"keys or values with";

	private final PortalPreferences _portalPreferences =
		new PortalPreferencesImpl();

}