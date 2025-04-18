/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Dante Wang
 */
public class HookHotDeployListenerTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_propsValueBooleanPropertyNames = SetUtil.fromArray(
			(String[])ReflectionTestUtil.getFieldValue(
				_hookHotDeployListener, "_PROPS_VALUES_BOOLEAN"));

		_propsValueIntegerPropertyNames = SetUtil.fromArray(
			(String[])ReflectionTestUtil.getFieldValue(
				_hookHotDeployListener, "_PROPS_VALUES_INTEGER"));

		_propsValueStringPropertyNames = SetUtil.fromArray(
			(String[])ReflectionTestUtil.getFieldValue(
				_hookHotDeployListener, "_PROPS_VALUES_STRING"));
	}

	@Test
	public void testInitPortalProperties() throws Exception {
		Properties initialProperties = _generateProperties(
			Map.of(
				_propsValueBooleanPropertyNames, () -> "false",
				_propsValueIntegerPropertyNames, () -> "1",
				_propsValueStringPropertyNames, RandomTestUtil::randomString));

		PropsUtil.addProperties(initialProperties);

		_assertPropsValues(initialProperties, null);

		Properties overrideProperties = _generateProperties(
			Map.of(
				_propsValueBooleanPropertyNames, () -> "true",
				_propsValueIntegerPropertyNames,
				() -> String.valueOf(
					RandomTestUtil.randomInt(2, Integer.MAX_VALUE)),
				_propsValueStringPropertyNames, RandomTestUtil::randomString));

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				HookHotDeployListener.class.getName(), Level.SEVERE)) {

			_hookHotDeployListener.initPortalProperties(
				StringPool.BLANK, null, overrideProperties, null);

			_assertPropsValues(overrideProperties, logCapture);
		}
	}

	private void _assertPropsValues(
		Properties properties, LogCapture logCapture) {

		List<String> messages = null;

		if (logCapture != null) {
			messages = TransformUtil.transform(
				logCapture.getLogEntries(), LogEntry::getMessage);
		}

		for (String stringPropertyName : properties.stringPropertyNames()) {
			Assert.assertEquals(
				stringPropertyName, properties.getProperty(stringPropertyName),
				PropsUtil.get(stringPropertyName));

			String propsValuesFieldName = StringUtil.replace(
				StringUtil.toUpperCase(stringPropertyName), CharPool.PERIOD,
				CharPool.UNDERLINE);

			Object value = null;

			try {
				value = ReflectionTestUtil.getFieldValue(
					PropsValues.class, propsValuesFieldName);
			}
			catch (Exception exception) {

				// Some properties does not have PropsValue fields

				if (exception instanceof NoSuchFieldException) {
					if (messages != null) {
						messages.remove(
							StringBundler.concat(
								"Error setting field ", propsValuesFieldName,
								": ", propsValuesFieldName));
					}

					continue;
				}

				throw exception;
			}

			if (_propsValueBooleanPropertyNames.contains(stringPropertyName)) {
				value = String.valueOf((boolean)value);
			}
			else if (_propsValueIntegerPropertyNames.contains(
						stringPropertyName)) {

				value = String.valueOf((int)value);
			}

			Assert.assertEquals(
				stringPropertyName, properties.getProperty(stringPropertyName),
				value);
		}

		if (messages != null) {
			Assert.assertTrue(messages.toString(), messages.isEmpty());
		}
	}

	private Properties _generateProperties(
		Map<Set<String>, Supplier<String>> map) {

		Properties properties = new Properties();

		map.forEach(
			(keys, supplier) -> {
				for (String key : keys) {
					properties.setProperty(key, supplier.get());
				}
			});

		return properties;
	}

	private final HookHotDeployListener _hookHotDeployListener =
		new HookHotDeployListener();
	private Set<String> _propsValueBooleanPropertyNames;
	private Set<String> _propsValueIntegerPropertyNames;
	private Set<String> _propsValueStringPropertyNames;

}