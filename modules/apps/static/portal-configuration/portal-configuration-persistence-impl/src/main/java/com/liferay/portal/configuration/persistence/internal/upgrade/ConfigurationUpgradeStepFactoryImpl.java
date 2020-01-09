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

package com.liferay.portal.configuration.persistence.internal.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.Dictionary;

import org.apache.felix.cm.PersistenceManager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Preston Crary
 */
public class ConfigurationUpgradeStepFactoryImpl
	implements ConfigurationUpgradeStepFactory {

	public ConfigurationUpgradeStepFactoryImpl(
		BundleContext bundleContext, PersistenceManager persistenceManager) {

		_bundleContext = bundleContext;
		_persistenceManager = persistenceManager;
	}

	@Override
	public UpgradeStep createFactoryUpgradeStep(
		String oldFactoryPid, String newFactoryPid) {

		return dbProcessContext -> {
			ServiceReference<ConfigurationAdmin> serviceReference =
				_bundleContext.getServiceReference(ConfigurationAdmin.class);

			ConfigurationAdmin configurationAdmin = _bundleContext.getService(
				serviceReference);

			String filter = StringBundler.concat(
				StringPool.OPEN_PARENTHESIS,
				ConfigurationAdmin.SERVICE_FACTORYPID, StringPool.EQUAL,
				oldFactoryPid, StringPool.CLOSE_PARENTHESIS);

			try {
				Configuration[] configurations =
					configurationAdmin.listConfigurations(filter);

				if ((configurations == null) || (configurations.length == 0)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to upgrade ", oldFactoryPid,
								" because no associated configuration exists"));
					}

					return;
				}

				for (Configuration configuration : configurations) {
					Dictionary<String, String> dictionary =
						_persistenceManager.load(configuration.getPid());

					dictionary.put("service.factoryPid", newFactoryPid);

					String oldPid = dictionary.get("service.pid");

					String newPid = StringUtil.replace(
						oldPid, oldFactoryPid, newFactoryPid);

					dictionary.put("service.pid", newPid);

					dictionary.put(
						"felix.fileinstall.filename",
						StringUtil.replace(
							dictionary.get("felix.fileinstall.filename"),
							oldFactoryPid, newFactoryPid));

					_persistenceManager.store(newPid, dictionary);

					_persistenceManager.delete(oldPid);

					_renameConfigurationFile(oldPid, newPid, "cfg");
					_renameConfigurationFile(oldPid, newPid, "config");
				}
			}
			catch (Exception e) {
				throw new UpgradeException(e);
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		};
	}

	@Override
	public UpgradeStep createUpgradeStep(String oldPid, String newPid) {
		return dbProcessContext -> {
			try {
				if (_persistenceManager.exists(oldPid)) {
					Dictionary<String, String> dictionary =
						_persistenceManager.load(oldPid);

					dictionary.put("service.pid", newPid);

					_persistenceManager.store(newPid, dictionary);

					_persistenceManager.delete(oldPid);
				}

				_renameConfigurationFile(oldPid, newPid, "cfg");
				_renameConfigurationFile(oldPid, newPid, "config");
			}
			catch (IOException ioe) {
				throw new UpgradeException(ioe);
			}
		};
	}

	private void _renameConfigurationFile(
			String oldPid, String newPid, String extension)
		throws IOException {

		File oldConfigFile = new File(
			StringBundler.concat(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, "/", oldPid, ".",
				extension));

		if (!oldConfigFile.exists()) {
			return;
		}

		File newConfigFile = new File(
			StringBundler.concat(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, "/", newPid, ".",
				extension));

		if (newConfigFile.exists()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to rename ", oldConfigFile.getAbsolutePath(),
						" to ", newConfigFile.getAbsolutePath(),
						" because the file already exists"));
			}

			return;
		}

		Files.move(oldConfigFile.toPath(), newConfigFile.toPath());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationUpgradeStepFactoryImpl.class);

	private final BundleContext _bundleContext;
	private final PersistenceManager _persistenceManager;

}