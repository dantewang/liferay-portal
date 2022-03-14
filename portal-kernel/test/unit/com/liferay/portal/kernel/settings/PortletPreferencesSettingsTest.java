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

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.test.rule.CountingInvocationHandler;

import java.util.Map;
import java.util.Objects;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Iván Zaera
 */
public class PortletPreferencesSettingsTest {

	@Before
	public void setUp() throws ReadOnlyException {
		_portletPreferences = new MockPortletPreferences() {

			@Override
			public String getValue(String key, String defaultValue) {
				if (Objects.equals(key, _PORTLET_PREFERENCES_SINGLE_KEY)) {
					return _PORTLET_PREFERENCES_SINGLE_VALUE;
				}

				return null;
			}

			@Override
			public String[] getValues(String key, String[] defaultValues) {
				if (Objects.equals(key, _PORTLET_PREFERENCES_MULTIPLE_KEY)) {
					return _PORTLET_PREFERENCES_MULTIPLE_VALUES;
				}

				return null;
			}

			@Override
			public void store() {
				_storeCalled = true;
			}

		};

		ModifiableSettings modifiableSettings = new MemorySettings();

		modifiableSettings.setValue(
			_DEFAULT_SETTINGS_SINGLE_KEY, _DEFAULT_SETTINGS_SINGLE_VALUE);
		modifiableSettings.setValues(
			_DEFAULT_SETTINGS_MULTIPLE_KEY, _DEFAULT_SETTINGS_MULTIPLE_VALUES);

		_portletPreferencesSettings = new PortletPreferencesSettings(
			_portletPreferences, modifiableSettings);

		CountingInvocationHandler.invocationCount = 0;
	}

	@Test
	public void testGetValuesWithExistingDefaultSettingsKey() {
		Assert.assertArrayEquals(
			_DEFAULT_SETTINGS_MULTIPLE_VALUES,
			_portletPreferencesSettings.getValues(
				_DEFAULT_SETTINGS_MULTIPLE_KEY, null));
	}

	@Test
	public void testGetValuesWithExistingPortletPreferencesKey() {
		Assert.assertArrayEquals(
			_PORTLET_PREFERENCES_MULTIPLE_VALUES,
			_portletPreferencesSettings.getValues(
				_PORTLET_PREFERENCES_MULTIPLE_KEY, null));
	}

	@Test
	public void testGetValuesWithMissingKey() {
		String[] defaultValue = {"a", "b"};

		Assert.assertArrayEquals(
			defaultValue,
			_portletPreferencesSettings.getValues("missingKeys", defaultValue));
	}

	@Test
	public void testGetValueWithExistingDefaultSettingsKey() {
		Assert.assertEquals(
			_DEFAULT_SETTINGS_SINGLE_VALUE,
			_portletPreferencesSettings.getValue(
				_DEFAULT_SETTINGS_SINGLE_KEY, null));
	}

	@Test
	public void testGetValueWithExistingPortletPreferencesKey() {
		Assert.assertEquals(
			_PORTLET_PREFERENCES_SINGLE_VALUE,
			_portletPreferencesSettings.getValue(
				_PORTLET_PREFERENCES_SINGLE_KEY, null));
	}

	@Test
	public void testGetValueWithMissingKey() {
		Assert.assertEquals(
			"default",
			_portletPreferencesSettings.getValue("missingKey", "default"));
	}

	@Test
	public void testSetValueSetsPropertyInPortletPreferences() {
		_portletPreferencesSettings.setValue("key", "value");

		Map<String, String[]> map = _portletPreferences.getMap();

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertArrayEquals(new String[] {"value"}, map.get("key"));
	}

	@Test
	public void testSetValuesSetsPropertyInPortletPreferences() {
		String[] values = {"a", "b"};

		_portletPreferencesSettings.setValues("key", values);

		Map<String, String[]> map = _portletPreferences.getMap();

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertArrayEquals(values, map.get("key"));
	}

	@Test
	public void testStoreIsPerformedOnPortletPreferences() throws Exception {
		_portletPreferencesSettings.store();

		Assert.assertTrue(_storeCalled);
	}

	private static final String _DEFAULT_SETTINGS_MULTIPLE_KEY = "defaultKeys";

	private static final String[] _DEFAULT_SETTINGS_MULTIPLE_VALUES = {
		"defaultValue0", "defaultValue1"
	};

	private static final String _DEFAULT_SETTINGS_SINGLE_KEY = "defaultKey";

	private static final String _DEFAULT_SETTINGS_SINGLE_VALUE = "defaultValue";

	private static final String _PORTLET_PREFERENCES_MULTIPLE_KEY =
		"portletKeys";

	private static final String[] _PORTLET_PREFERENCES_MULTIPLE_VALUES = {
		"portletValue0", "portletValue1"
	};

	private static final String _PORTLET_PREFERENCES_SINGLE_KEY = "portletKey";

	private static final String _PORTLET_PREFERENCES_SINGLE_VALUE =
		"portletValue";

	private PortletPreferences _portletPreferences;
	private PortletPreferencesSettings _portletPreferencesSettings;
	private boolean _storeCalled;

}