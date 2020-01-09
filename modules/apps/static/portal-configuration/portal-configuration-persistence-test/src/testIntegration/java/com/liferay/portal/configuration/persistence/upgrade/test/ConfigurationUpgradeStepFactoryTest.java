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
				"test.old.pid", StringPool.QUESTION);

		String oldPid = configuration.getPid();

		int index = oldPid.lastIndexOf('.');

		StringBundler sb = new StringBundler(4);

		sb.append(configuration.getFactoryPid());
		sb.append(StringPool.DASH);
		sb.append(oldPid.substring(index + 1));
		sb.append(".config");

		File file = new File(
			PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, sb.toString());

		file = file.getAbsoluteFile();

		URI uri = file.toURI();

		ConfigurationTestUtil.saveConfiguration(
			configuration,
			MapUtil.singletonDictionary(
				"felix.fileinstall.filename", uri.toString()));

		Assert.assertTrue(_persistenceManager.exists(oldPid));

		UpgradeStep upgradeStep =
			_configurationUpgradeStepFactory.createFactoryUpgradeStep(
				"test.old.pid", "test.new.pid");

		upgradeStep.upgrade(
			new DBProcessContext() {

				@Override
				public DBContext getDBContext() {
					return new DBContext();
				}

				@Override
				public OutputStream getOutputStream() {
					return null;
				}

			});

		String newPid = StringUtil.replace(
			oldPid, "test.old.pid", "test.new.pid");

		Assert.assertFalse(_persistenceManager.exists(oldPid));
		Assert.assertTrue(_persistenceManager.exists(newPid));

		_persistenceManager.delete(newPid);

		ConfigurationTestUtil.deleteConfiguration(configuration);
	}

	@Test
	public void testCreateUpgradeStep() throws Exception {
		String oldPid = "test.old.pid";
		String newPid = "test.new.pid";

		Configuration configuration = _configurationAdmin.getConfiguration(
			oldPid);

		Assert.assertTrue(_persistenceManager.exists(oldPid));

		UpgradeStep upgradeStep =
			_configurationUpgradeStepFactory.createUpgradeStep(oldPid, newPid);

		upgradeStep.upgrade(
			new DBProcessContext() {

				@Override
				public DBContext getDBContext() {
					return new DBContext();
				}

				@Override
				public OutputStream getOutputStream() {
					return null;
				}

			});

		Assert.assertFalse(_persistenceManager.exists(oldPid));
		Assert.assertTrue(_persistenceManager.exists(newPid));

		_persistenceManager.delete(newPid);

		ConfigurationTestUtil.deleteConfiguration(configuration);
	}

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

	@Inject
	private PersistenceManager _persistenceManager;

}