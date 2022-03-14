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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Chow
 * @author Manuel de la Peña
 * @author Raymond Augé
 */
public class DateUtilTest {

	@Test
	public void testEquals() throws Exception {
		Assert.assertEquals(
			DateUtil.equals(null, new Date()),
			DateUtil.equals(new Date(), null));
	}

	@Test
	public void testGetDaysBetweenLeap() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		_testGetDaysBetween(
			dateFormat.parse("2-28-2012"), dateFormat.parse("3-1-2012"), 2);
	}

	@Test
	public void testGetDaysBetweenMonth() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		_testGetDaysBetween(
			dateFormat.parse("12-31-2011"), dateFormat.parse("1-1-2012"), 1);
	}

	@Test
	public void testGetDaysBetweenReverse() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		_testGetDaysBetween(
			dateFormat.parse("3-1-2012"), dateFormat.parse("2-28-2012"), 2);
	}

	@Test
	public void testGetDaysBetweenSame() throws Exception {
		_testGetDaysBetween(new Date(), new Date(), 0);
	}

	@Test
	public void testGetDaysBetweenYear() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		_testGetDaysBetween(
			dateFormat.parse("1-1-2011"), dateFormat.parse("1-1-2012"), 365);
	}

	@Test
	public void testGetISOFormatAny() {
		_testGetISOFormat("", "yyyyMMddHHmmssz");
	}

	@Test
	public void testGetISOFormatLength8() {
		_testGetISOFormat("01234567", "yyyyMMdd");
	}

	@Test
	public void testGetISOFormatLength12() {
		_testGetISOFormat("012345678901", "yyyyMMddHHmm");
	}

	@Test
	public void testGetISOFormatLength13() {
		_testGetISOFormat("0123456789012", "yyyyMMdd'T'HHmm");
	}

	@Test
	public void testGetISOFormatLength14() {
		_testGetISOFormat("01234567890123", "yyyyMMddHHmmss");
	}

	@Test
	public void testGetISOFormatLength15() {
		_testGetISOFormat("012345678901234", "yyyyMMdd'T'HHmmss");
	}

	@Test
	public void testGetISOFormatT() {
		_testGetISOFormat("01234567T9012345", "yyyyMMdd'T'HHmmssz");
	}

	@Test
	public void testGetUTCFormat() {
		_testGetUTCFormat("19721223", "yyyyMMdd");
	}

	private void _testGetDaysBetween(Date date1, Date date2, int expected) {
		CalendarFactoryUtil calendarFactoryUtil = new CalendarFactoryUtil();

		calendarFactoryUtil.setCalendarFactory(
			(CalendarFactory)ProxyUtil.newProxyInstance(
				CalendarFactory.class.getClassLoader(),
				new Class<?>[] {CalendarFactory.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getCalendar")) {
						return new GregorianCalendar();
					}

					return null;
				}));

		Assert.assertEquals(
			expected, DateUtil.getDaysBetween(date1, date2, null));
	}

	private void _testGetISOFormat(String text, String pattern) {
		DateFormatFactoryUtil dateFormatFactoryUtil =
			new DateFormatFactoryUtil();

		dateFormatFactoryUtil.setDateFormatFactory(
			(DateFormatFactory)ProxyUtil.newProxyInstance(
				DateFormatFactory.class.getClassLoader(),
				new Class<?>[] {DateFormatFactory.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getSimpleDateFormat")) {

						return new SimpleDateFormat(pattern, LocaleUtil.SPAIN);
					}

					return null;
				}));

		DateFormat dateFormat = DateUtil.getISOFormat(text);

		SimpleDateFormat simpleDateFormat = (SimpleDateFormat)dateFormat;

		Assert.assertEquals(pattern, simpleDateFormat.toPattern());
	}

	private void _testGetUTCFormat(String date, String pattern) {
		DateFormatFactoryUtil dateFormatFactoryUtil =
			new DateFormatFactoryUtil();

		dateFormatFactoryUtil.setDateFormatFactory(
			(DateFormatFactory)ProxyUtil.newProxyInstance(
				DateFormatFactory.class.getClassLoader(),
				new Class<?>[] {DateFormatFactory.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getSimpleDateFormat")) {

						return new TestSimpleDateFormat((String)args[0]);
					}

					return null;
				}));

		DateFormat utcDateFormat = DateUtil.getUTCFormat(date);

		Assert.assertNotNull(utcDateFormat);
		Assert.assertTrue(utcDateFormat instanceof SimpleDateFormat);

		TestSimpleDateFormat testSimpleDateFormat =
			(TestSimpleDateFormat)utcDateFormat;

		Assert.assertEquals(testSimpleDateFormat.getPattern(), pattern);
	}

	private static class TestSimpleDateFormat extends SimpleDateFormat {

		public TestSimpleDateFormat(String pattern) {
			super(pattern);

			_pattern = pattern;
		}

		public String getPattern() {
			return _pattern;
		}

		private final String _pattern;

	}

}