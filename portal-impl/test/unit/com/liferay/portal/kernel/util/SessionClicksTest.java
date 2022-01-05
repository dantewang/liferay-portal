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

import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.test.rule.PortalProps;

import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpSession;

/**
 * @author Dante Wang
 */
public class SessionClicksTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@PortalProps(
		properties = {
			PropsKeys.SESSION_CLICKS_MAX_ALLOWED_VALUES + "=10",
			PropsKeys.SESSION_CLICKS_MAX_SIZE_TERMS + "=10"
		}
	)
	@Test
	public void testPutIntoSession() {
		HttpSession httpSession = new MockHttpSession();

		_testPutIntoSession(httpSession, "key1", "value1", null);

		_testPutIntoSession(
			httpSession, "key2", SessionClicks.class.getName(),
			_MAX_SIZE_TERMS_WARNING_PREFIX);

		_testPutIntoSession(
			httpSession, SessionClicks.class.getName(), "value2",
			_MAX_SIZE_TERMS_WARNING_PREFIX);

		for (int i = 1; i <= 20; i++) {
			String warningMessagePrefix = null;

			if (i > 10) {
				warningMessagePrefix = _MAX_ALLOWED_VALUES_WARNING_PREFIX;
			}

			_testPutIntoSession(
				httpSession, "key" + i, "value" + i, warningMessagePrefix);
		}
	}

	private void _testPutIntoSession(
		HttpSession httpSession, String key, String value,
		String expectedMessagePrefix) {

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				SessionClicks.class.getName(), Level.WARNING)) {

			SessionClicks.put(httpSession, key, value);

			if (expectedMessagePrefix == null) {
				Assert.assertEquals(
					value, SessionClicks.get(httpSession, key, null));

				return;
			}

			Assert.assertNull(SessionClicks.get(httpSession, key, null));

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

}