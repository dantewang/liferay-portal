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

package com.liferay.portal.configuration.persistence.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBContext;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBProcessContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.OutputStream;

import java.net.URI;

import org.apache.felix.cm.PersistenceManager;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Hai Yu
 */
@RunWith(Arquillian.class)
public class ConfigurationUpgradeStepFactoryTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeConfigWithDBAndFile() throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			_OLD_PID);

		File oldConfigFile = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, _OLD_PID + ".config");

		try {
			oldConfigFile.createNewFile();

			_testUpgradeConfig(
				oldConfigFile,
				new File(
					PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
					_NEW_PID + ".config"),
				_OLD_PID, _NEW_PID, true, false);
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testUpgradeConfigWithDBWithoutFile() throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			_OLD_PID);

		try {
			_testUpgradeConfig(null, null, _OLD_PID, _NEW_PID, true, false);
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testUpgradeConfigWithoutDBWithFile() throws Exception {
		File oldConfigFile = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, _OLD_PID + ".config");

		oldConfigFile.createNewFile();

		_testUpgradeConfig(
			oldConfigFile,
			new File(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, _NEW_PID + ".config"),
			_OLD_PID, _NEW_PID, false, false);
	}

	@Test
	public void testUpgradeFactoryConfigWithDBAndFile() throws Exception {
		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				_OLD_PID, StringPool.QUESTION);

		String oldPid = configuration.getPid();

		String newPid = StringUtil.replace(oldPid, _OLD_PID, _NEW_PID);

		File oldConfigFile = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, oldPid + ".config");

		URI uri = oldConfigFile.toURI();

		try {
			ConfigurationTestUtil.saveConfiguration(
				configuration,
				MapUtil.singletonDictionary(
					"felix.fileinstall.filename", uri.toString()));

			oldConfigFile.createNewFile();

			_testUpgradeConfig(
				oldConfigFile,
				new File(
					PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
					newPid + ".config"),
				oldPid, newPid, true, true);
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testUpgradeFactoryConfigWithDBWithoutFile() throws Exception {
		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				_OLD_PID, StringPool.QUESTION);

		String oldPid = configuration.getPid();

		String newPid = StringUtil.replace(oldPid, _OLD_PID, _NEW_PID);

		File oldConfigFile = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, oldPid + ".config");

		URI uri = oldConfigFile.toURI();

		try {
			ConfigurationTestUtil.saveConfiguration(
				configuration,
				MapUtil.singletonDictionary(
					"felix.fileinstall.filename", uri.toString()));

			_testUpgradeConfig(null, null, oldPid, newPid, true, true);
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testUpgradeFactoryConfigWithoutDBWithFile() throws Exception {
		File oldConfigFile = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
			_OLD_PID + "-instance.config");

		oldConfigFile.createNewFile();

		_testUpgradeConfig(
			oldConfigFile,
			new File(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
				_NEW_PID + "-instance.config"),
			_OLD_PID + "-instance", _NEW_PID + "-instance", false, true);
	}

	private void _testUpgradeConfig(
			File oldConfigFile, File newConfigFile, String oldPid,
			String newPid, boolean configurationDataExist, boolean factory)
		throws Exception {

		try {
			if (configurationDataExist) {
				Assert.assertTrue(
					"Configuration " + oldPid + " does not exist",
					_persistenceManager.exists(oldPid));
			}

			if (oldConfigFile != null) {
				Assert.assertTrue(
					"Configuration file " + oldPid + ".config does not exist",
					oldConfigFile.exists());
			}

			UpgradeStep upgradeStep =
				_configurationUpgradeStepFactory.createUpgradeStep(
					_OLD_PID, _NEW_PID);

			upgradeStep.upgrade(_DB_PROCESS_CONTEXT);

			if (configurationDataExist) {
				Assert.assertFalse(
					"Configuration " + oldPid + " still exists",
					_persistenceManager.exists(oldPid));
				Assert.assertTrue(
					"Configuration " + newPid + " does not exist",
					_persistenceManager.exists(newPid));
			}

			if (oldConfigFile != null) {
				Assert.assertFalse(
					"Configuration file " + oldPid + " .config still exists",
					oldConfigFile.exists());
				Assert.assertTrue(
					"Configuration file " + newPid + " .config does not exist",
					newConfigFile.exists());
			}
		}
		finally {
			if (oldConfigFile != null) {
				oldConfigFile.delete();
				newConfigFile.delete();
			}

			if (!factory || configurationDataExist) {
				_persistenceManager.delete(oldPid);
				_persistenceManager.delete(newPid);
			}
			else {
				DB db = DBManagerUtil.getDB();

				db.runSQL(
					StringBundler.concat(
						"delete from Configuration_ where configurationId ",
						"like '", _OLD_PID, "%' or configurationId like '",
						_NEW_PID, "%'"));
			}
		}
	}

	private static final DBProcessContext _DB_PROCESS_CONTEXT =
		new DBProcessContext() {

			@Override
			public DBContext getDBContext() {
				return new DBContext();
			}

			@Override
			public OutputStream getOutputStream() {
				return null;
			}

		};

	private static final String _NEW_PID = "test.new.pid";

	private static final String _OLD_PID = "test.old.pid";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

	@Inject
	private PersistenceManager _persistenceManager;

}