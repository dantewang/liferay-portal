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

package com.liferay.portal.kernel.parsers.bbcode;

import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class BBCodeTranslatorUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestBBCodeTranslator testBBCodeTranslator = new TestBBCodeTranslator();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			BBCodeTranslator.class, testBBCodeTranslator, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testEmoticonDescriptions() {
		String[] emoticonDescriptions =
			BBCodeTranslatorUtil.getEmoticonDescriptions();

		Assert.assertEquals(
			Arrays.toString(emoticonDescriptions), 3,
			emoticonDescriptions.length);
	}

	@Test
	public void testEmoticonFiles() {
		String[] emoticonFiles = BBCodeTranslatorUtil.getEmoticonFiles();

		Assert.assertEquals(
			Arrays.toString(emoticonFiles), 2, emoticonFiles.length);
	}

	@Test
	public void testEmoticonSymbols() {
		String[] emoticonSymbols = BBCodeTranslatorUtil.getEmoticonSymbols();

		Assert.assertEquals(
			Arrays.toString(emoticonSymbols), 4, emoticonSymbols.length);
	}

	@Test
	public void testGetBBCodeTranslator() {
		BBCodeTranslator bbCodeTranslator =
			BBCodeTranslatorUtil.getBBCodeTranslator();

		Class<?> clazz = bbCodeTranslator.getClass();

		Assert.assertEquals(
			TestBBCodeTranslator.class.getName(), clazz.getName());
	}

	@Test
	public void testHTML() {
		Assert.assertEquals(
			TestBBCodeTranslator.START_TAG + "1" + TestBBCodeTranslator.END_TAG,
			BBCodeTranslatorUtil.getHTML("1"));
	}

	@Test
	public void testParse() {
		Assert.assertEquals(
			TestBBCodeTranslator.END_TAG,
			BBCodeTranslatorUtil.parse(TestBBCodeTranslator.START_TAG));
	}

	private static ServiceRegistration<BBCodeTranslator> _serviceRegistration;

	private static class TestBBCodeTranslator implements BBCodeTranslator {

		public static final String END_TAG = "</TestBBCcodeTranslator>";

		public static final String START_TAG = "<TestBBCcodeTranslator>";

		@Override
		public String[] getEmoticonDescriptions() {
			return new String[] {"1", "2", "3"};
		}

		@Override
		public String[] getEmoticonFiles() {
			return new String[] {"1", "2"};
		}

		@Override
		public String[][] getEmoticons() {
			return null;
		}

		@Override
		public String[] getEmoticonSymbols() {
			return new String[] {"1", "2", "3", "4"};
		}

		@Override
		public String getHTML(String bbcode) {
			return START_TAG + bbcode + END_TAG;
		}

		@Override
		public String parse(String message) {
			if (message.equals(START_TAG)) {
				return END_TAG;
			}

			return START_TAG;
		}

	}

}