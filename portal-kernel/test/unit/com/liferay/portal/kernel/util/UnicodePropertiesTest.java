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

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Chow
 * @author Xiangyue Cai
 */
public class UnicodePropertiesTest {

	@Test
	public void testFastLoad() throws Exception {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		_testLoad(
			props -> unicodeProperties.fastLoad(props), unicodeProperties);
	}

	@Test
	public void testGetProperty() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		// with key

		unicodeProperties.put(_TEST_KEY_1, _TEST_VALUE_1);

		Assert.assertEquals(
			_TEST_VALUE_1, unicodeProperties.getProperty(_TEST_KEY_1));
		Assert.assertEquals(
			_TEST_VALUE_1,
			unicodeProperties.getProperty(_TEST_KEY_1, "testDefaultValue"));

		// with key and defaultValue

		Assert.assertNull(unicodeProperties.getProperty(_TEST_KEY_2));
		Assert.assertEquals(
			"testDefaultValue",
			unicodeProperties.getProperty(_TEST_KEY_2, "testDefaultValue"));
	}

	@Test
	public void testGetToStringLength() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		Assert.assertEquals(-1, unicodeProperties.getToStringLength());
	}

	@Test
	public void testIsSafe() {
		UnicodeProperties unicodeProperties1 = new UnicodeProperties();

		Assert.assertFalse(
			"since _safe is set to false by default, isSafe() should return " +
				"false",
			unicodeProperties1.isSafe());

		UnicodeProperties unicodeProperties2 = new UnicodeProperties(false);

		Assert.assertFalse(
			"isSafe() should return false if _safe was set false",
			unicodeProperties2.isSafe());

		UnicodeProperties unicodeProperties3 = new UnicodeProperties(true);

		Assert.assertTrue(
			"isSafe() should return false if _safe was set true",
			unicodeProperties3.isSafe());
	}

	@Test
	public void testLoad() throws Exception {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		_testLoad(props -> unicodeProperties.load(props), unicodeProperties);
	}

	@Test
	public void testPutAll() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		unicodeProperties.putAll(_testMap);

		Assert.assertEquals(_testMap, unicodeProperties);
	}

	@Test
	public void testPutLines() {
		_testPutLines(false);
		_testPutLines(true);
	}

	@Test
	public void testPutLinesLog() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					UnicodeProperties.class.getName(), Level.ALL)) {

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());

			// line without EQUAL(=)

			unicodeProperties.put(_TEST_KEY_1);

			logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Invalid property on line " + _TEST_KEY_1,
				logRecord.getMessage());
		}
	}

	@Test
	public void testRemove() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		unicodeProperties.put(_TEST_KEY_1, _TEST_VALUE_1);

		Assert.assertNull(unicodeProperties.remove(null));

		Assert.assertEquals(
			Collections.singletonMap(_TEST_KEY_1, _TEST_VALUE_1),
			unicodeProperties);

		Assert.assertEquals(
			_TEST_VALUE_1, unicodeProperties.remove(_TEST_KEY_1));

		Assert.assertNull(unicodeProperties.get(_TEST_KEY_1));
	}

	@Test
	public void testSetProperty() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		Assert.assertNull(unicodeProperties.setProperty(null, null));
		Assert.assertTrue(
			"nothing will be put in if key is null",
			unicodeProperties.isEmpty());

		Assert.assertNull(unicodeProperties.setProperty(_TEST_KEY_1, null));
		Assert.assertTrue(
			"nothing will be put in if value is null",
			unicodeProperties.isEmpty());

		Assert.assertNull(
			unicodeProperties.setProperty(_TEST_KEY_1, _TEST_VALUE_1));
		Assert.assertEquals(
			Collections.singletonMap(_TEST_KEY_1, _TEST_VALUE_1),
			unicodeProperties);

		Assert.assertEquals(
			_TEST_VALUE_1, unicodeProperties.setProperty(_TEST_KEY_1, null));
		Assert.assertTrue(
			"setProperty() of null value must remove entry",
			unicodeProperties.isEmpty());
	}

	@Test
	public void testToSortedString() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		unicodeProperties.toSortedString();
	}

	@Test
	public void testToString() {
		_testToString(false);
		_testToString(true);
	}

	private void _testLoad(
			UnsafeConsumer<String, Exception> load,
			UnicodeProperties unicodeProperties)
		throws Exception {

		load.accept(null);

		Assert.assertTrue(
			"nothing will be put in if props is null",
			unicodeProperties.isEmpty());

		load.accept(_TEST_LINE_1);

		Assert.assertEquals(
			Collections.singletonMap(_TEST_KEY_1, _TEST_VALUE_1),
			unicodeProperties);

		load.accept(_TEST_PROPS);

		Assert.assertEquals(_testMap, unicodeProperties);
	}

	private void _testPutLines(boolean safe) {
		UnicodeProperties unicodeProperties = new UnicodeProperties(safe);

		// line with empty

		unicodeProperties.put("");

		Assert.assertTrue(
			"nothing will be put in if call put(\"\")",
			unicodeProperties.isEmpty());

		// line with POUND(#)

		unicodeProperties.put("#");

		Assert.assertTrue(
			"nothing will be put in happen if call put(\"#\")",
			unicodeProperties.isEmpty());

		// line with _TEST_LINE_1(testKey1=testValue1)

		unicodeProperties.put(_TEST_LINE_1);

		Assert.assertEquals(
			Collections.singletonMap(_TEST_KEY_1, _TEST_VALUE_1),
			unicodeProperties);

		// safe is true

		if (safe) {
			unicodeProperties.put(_TEST_LINE_1 + _TEST_SAFE_NEWLINE_CHARACTER);

			Assert.assertEquals(
				Collections.singletonMap(
					_TEST_KEY_1, _TEST_VALUE_1 + StringPool.NEW_LINE),
				unicodeProperties);
		}
	}

	private void _testToString(boolean safe) {
		UnicodeProperties unicodeProperties = new UnicodeProperties(safe);

		Assert.assertEquals(StringPool.BLANK, unicodeProperties.toString());

		unicodeProperties.put(_TEST_KEY_1, _TEST_VALUE_1);
		unicodeProperties.put(_TEST_KEY_2, StringPool.BLANK);
		unicodeProperties.put(_TEST_KEY_3, _TEST_VALUE_3);

		Assert.assertEquals(
			_TEST_LINE_1 + StringPool.NEW_LINE + _TEST_LINE_3 +
				StringPool.NEW_LINE,
			unicodeProperties.toString());

		if (safe) {
			unicodeProperties.put(
				_TEST_KEY_3, _TEST_VALUE_3 + StringPool.NEW_LINE);

			Assert.assertEquals(
				_TEST_LINE_1 + StringPool.NEW_LINE + _TEST_LINE_3 +
					_TEST_SAFE_NEWLINE_CHARACTER + StringPool.NEW_LINE,
				unicodeProperties.toString());
		}
	}

	private static final String _TEST_KEY_1 = "testKey1";

	private static final String _TEST_KEY_2 = "testKey2";

	private static final String _TEST_KEY_3 = "testKey3";

	private static final String _TEST_LINE_1;

	private static final String _TEST_LINE_2;

	private static final String _TEST_LINE_3;

	private static final String _TEST_PROPS;

	private static final String _TEST_SAFE_NEWLINE_CHARACTER =
		"_SAFE_NEWLINE_CHARACTER_";

	private static final String _TEST_VALUE_1 = "testValue1";

	private static final String _TEST_VALUE_2 = "testValue2";

	private static final String _TEST_VALUE_3 = "testValue3";

	private static final Map<String, String> _testMap =
		new HashMap<String, String>() {
			{
				put(_TEST_KEY_1, _TEST_VALUE_1);
				put(_TEST_KEY_2, _TEST_VALUE_2);
				put(_TEST_KEY_3, _TEST_VALUE_3);
			}
		};

	static {
		_TEST_LINE_1 = _TEST_KEY_1 + StringPool.EQUAL + _TEST_VALUE_1;

		_TEST_LINE_2 = _TEST_KEY_2 + StringPool.EQUAL + _TEST_VALUE_2;

		_TEST_LINE_3 = _TEST_KEY_3 + StringPool.EQUAL + _TEST_VALUE_3;

		_TEST_PROPS =
			_TEST_LINE_1 + StringPool.NEW_LINE + _TEST_LINE_2 +
				StringPool.NEW_LINE + _TEST_LINE_3;
	}

}