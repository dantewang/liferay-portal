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
import com.liferay.portal.kernel.dao.db.DBContext;
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
	public void testCreateFactoryUpgradeStep() throws Exception {
		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				_OLD_PID, StringPool.QUESTION);

		String oldPid = configuration.getPid();

		String newPid = StringUtil.replace(oldPid, _OLD_PID, _NEW_PID);

		try {
			File file = new File(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
				StringBundler.concat(
					configuration.getFactoryPid(), StringPool.DASH,
					oldPid.substring(oldPid.lastIndexOf('.') + 1), ".config"));

			file = file.getAbsoluteFile();

			URI uri = file.toURI();

			ConfigurationTestUtil.saveConfiguration(
				configuration,
				MapUtil.singletonDictionary(
					"felix.fileinstall.filename", uri.toString()));

			Assert.assertTrue(_persistenceManager.exists(oldPid));

			UpgradeStep upgradeStep =
				_configurationUpgradeStepFactory.createFactoryUpgradeStep(
					_OLD_PID, _NEW_PID);

			upgradeStep.upgrade(_DB_PROCESS_CONTEXT);

			Assert.assertFalse(_persistenceManager.exists(oldPid));
			Assert.assertTrue(_persistenceManager.exists(newPid));
		}
		finally {
			_persistenceManager.delete(oldPid);
			_persistenceManager.delete(newPid);

			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	@Test
	public void testCreateUpgradeStep() throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			_OLD_PID);

		try {
			Assert.assertTrue(_persistenceManager.exists(_OLD_PID));

			UpgradeStep upgradeStep =
				_configurationUpgradeStepFactory.createUpgradeStep(
					_OLD_PID, _NEW_PID);

			upgradeStep.upgrade(_DB_PROCESS_CONTEXT);

			Assert.assertFalse(_persistenceManager.exists(_OLD_PID));
			Assert.assertTrue(_persistenceManager.exists(_NEW_PID));
		}
		finally {
			_persistenceManager.delete(_OLD_PID);
			_persistenceManager.delete(_NEW_PID);

			ConfigurationTestUtil.deleteConfiguration(configuration);
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