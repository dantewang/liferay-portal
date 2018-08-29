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

import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Leon Chi
 */
public class UnicodeFormatterTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testBytesToHex() {
		byte[] bytes = {0, (byte)255};

		Assert.assertEquals("00ff", UnicodeFormatter.bytesToHex(bytes));
	}

	@Test
	public void testByteToHexChars() {
		char[] hexes = new char[2];
		char[] expectedHexeChars = {'f', 'f'};

		Assert.assertArrayEquals(
			expectedHexeChars, UnicodeFormatter.byteToHex((byte)255, hexes));

		expectedHexeChars[0] = 'F';
		expectedHexeChars[1] = 'F';

		Assert.assertArrayEquals(
			expectedHexeChars,
			UnicodeFormatter.byteToHex((byte)255, hexes, true));
	}

	@Test
	public void testByteToHexString() {
		Assert.assertEquals("ff", UnicodeFormatter.byteToHex((byte)255));
	}

	@Test
	public void testCharToHex() {
		Assert.assertEquals("ffff", UnicodeFormatter.charToHex((char)0xffff));
	}

	@Test
	public void testConstructor() {
		new UnicodeFormatter();
	}

	@Test
	public void testHexToBytes() {
		Assert.assertArrayEquals(
			new byte[0], UnicodeFormatter.hexToBytes("0ff"));
		Assert.assertArrayEquals(
			new byte[0], UnicodeFormatter.hexToBytes("0 ff"));

		byte[] expectedbytes = {0, (byte)255};

		Assert.assertArrayEquals(
			expectedbytes, UnicodeFormatter.hexToBytes("00ff"));
	}

	@Test
	public void testParseString() {
		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					UnicodeFormatter.class.getName(), Level.SEVERE)) {

			Assert.assertEquals(
				"\\u000061", UnicodeFormatter.parseString("\\u000061"));

			Assert.assertEquals(
				"\\u0 61", UnicodeFormatter.parseString("\\u0 61"));

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 2, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"String is not in hex format", logRecord.getMessage());

			logRecord = logRecords.get(1);

			Assert.assertEquals(
				"java.lang.NumberFormatException: For input string: \"0 61\"",
				logRecord.getMessage());
		}

		Assert.assertEquals("a", UnicodeFormatter.parseString("\\u0061"));
	}

	@Test
	public void testToString() {
		char[] array = {'a'};

		Assert.assertEquals("\\u0061", UnicodeFormatter.toString(array));

		Assert.assertNull(UnicodeFormatter.toString((String)null));

		Assert.assertEquals("\\u0061", UnicodeFormatter.toString("a"));
	}

}