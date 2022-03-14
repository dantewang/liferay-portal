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

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Wesley Gong
 */
public class LocaleUtilTest {

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(
			(Language)ProxyUtil.newProxyInstance(
				Language.class.getClassLoader(),
				new Class<?>[] {Language.class},
				(proxy, method, args) -> {
					if (!Objects.equals(
							method.getName(), "isAvailableLocale")) {

						return null;
					}

					if (Objects.equals(args[0], Locale.US) ||
						Objects.equals(args[0], Locale.SIMPLIFIED_CHINESE) ||
						Objects.equals(args[0], Locale.TRADITIONAL_CHINESE)) {

						return true;
					}

					return false;
				}));
	}

	@Test
	public void testFromLanguageId() {
		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				LocaleUtil.class.getName(), Level.WARNING)) {

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(Locale.US, LocaleUtil.fromLanguageId("en_US"));
			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			logEntries.clear();

			LocaleUtil.fromLanguageId("en");

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"en is not a valid language id", logEntry.getMessage());
		}
	}

	@Test
	public void testFromLanguageIdBCP47() {
		Assert.assertEquals(Locale.US, LocaleUtil.fromLanguageId("en-US"));

		Assert.assertEquals(
			Locale.SIMPLIFIED_CHINESE, LocaleUtil.fromLanguageId("zh-Hans-CN"));

		Assert.assertEquals(
			Locale.TRADITIONAL_CHINESE,
			LocaleUtil.fromLanguageId("zh-Hant-TW"));
	}

}