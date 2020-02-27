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
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

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
		_testUpgradeConfig(true, true, false);
	}

	@Test
	public void testUpgradeConfigWithDBWithoutFile() throws Exception {
		_testUpgradeConfig(false, true, false);
	}

	@Test
	public void testUpgradeConfigWithoutDBWithFile() throws Exception {
		_testUpgradeConfig(true, false, false);
	}

	@Test
	public void testUpgradeFactoryConfigWithDBAndFile() throws Exception {
		_testUpgradeConfig(true, true, true);
	}

	@Test
	public void testUpgradeFactoryConfigWithDBWithoutFile() throws Exception {
		_testUpgradeConfig(false, true, true);
	}

	@Test
	public void testUpgradeFactoryConfigWithoutDBWithFile() throws Exception {
		_testUpgradeConfig(true, false, true);
	}

	private void _testUpgradeConfig(
			boolean configFileExist, boolean dataExist, boolean factory)
		throws Exception {

		Configuration configuration = null;

		File oldConfigFile = null;

		File newConfigFile = null;

		String oldPid = _OLD_PID;

		String newPid = _NEW_PID;

		try {
			if (factory) {
				if (dataExist) {
					configuration =
						_configurationAdmin.createFactoryConfiguration(
							oldPid, StringPool.QUESTION);

					oldPid = configuration.getPid();

					newPid = StringUtil.replace(oldPid, _OLD_PID, _NEW_PID);

					oldConfigFile = new File(
						PropsUtil.get(PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR),
						oldPid + ".config");

					URI uri = oldConfigFile.toURI();

					ConfigurationTestUtil.saveConfiguration(
						configuration,
						MapUtil.singletonDictionary(
							"felix.fileinstall.filename", uri.toString()));
				}

				if (configFileExist) {
					if (dataExist) {
						newConfigFile = new File(
							PropsUtil.get(
								PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR),
							newPid + ".config");
					}
					else {
						oldConfigFile = new File(
							PropsUtil.get(
								PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR),
							oldPid + "-instance.config");

						newConfigFile = new File(
							PropsUtil.get(
								PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR),
							newPid + "-instance.config");
					}

					oldConfigFile.createNewFile();
				}
			}
			else {
				if (dataExist) {
					configuration = _configurationAdmin.getConfiguration(
						oldPid);
				}

				if (configFileExist) {
					oldConfigFile = new File(
						PropsUtil.get(PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR),
						oldPid + ".config");

					oldConfigFile.createNewFile();

					newConfigFile = new File(
						PropsUtil.get(PropsKeys.MODULE_FRAMEWORK_CONFIGS_DIR),
						newPid + ".config");
				}
			}

			if (dataExist) {
				Assert.assertTrue(
					"Configuration " + oldPid + " does not exist",
					_persistenceManager.exists(oldPid));
			}

			if (configFileExist) {
				Assert.assertTrue(
					"Configuration file " + oldConfigFile + " does not exist",
					oldConfigFile.exists());
			}

			UpgradeStep upgradeStep =
				_configurationUpgradeStepFactory.createUpgradeStep(
					_OLD_PID, _NEW_PID);

			upgradeStep.upgrade(null);

			if (dataExist) {
				Assert.assertFalse(
					"Configuration " + oldPid + " still exists",
					_persistenceManager.exists(oldPid));
				Assert.assertTrue(
					"Configuration " + newPid + " does not exist",
					_persistenceManager.exists(newPid));
			}

			if (configFileExist) {
				Assert.assertFalse(
					"Configuration file " + oldConfigFile + " still exists",
					oldConfigFile.exists());
				Assert.assertTrue(
					"Configuration file " + newConfigFile + " does not exist",
					newConfigFile.exists());
			}
		}
		finally {
			if (configFileExist) {
				oldConfigFile.delete();
				newConfigFile.delete();
			}

			if (!factory || dataExist) {
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

			if (configuration != null) {
				ConfigurationTestUtil.deleteConfiguration(configuration);
			}
		}
	}

	private static final String _NEW_PID = "test.new.pid";

	private static final String _OLD_PID = "test.old.pid";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

	@Inject
	private PersistenceManager _persistenceManager;

}