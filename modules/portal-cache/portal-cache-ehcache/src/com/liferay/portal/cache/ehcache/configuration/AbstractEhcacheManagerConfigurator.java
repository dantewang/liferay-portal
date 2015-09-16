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

package com.liferay.portal.cache.ehcache.configuration;

import com.liferay.portal.cache.ehcache.internal.EhcacheConfigurationHelperUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

/**
 * @author Dante Wang
 */
public abstract class AbstractEhcacheManagerConfigurator
	implements CacheManagerConfigurator<Configuration> {

	@Override
	public Configuration getCacheManagerConfiguration() {
		return configuration;
	}

	@Override
	public boolean usingDefault() {
		return usingDefault;
	}

	protected void processConfigFile(
		String configFile, String defaultConfigFile, String cacheManagerName) {

		if (Validator.isNull(configFile)) {
			configFile = defaultConfigFile;

			usingDefault = true;
		}

		URL configFileURL = EhcacheConfigurationHelperUtil.class.getResource(
			configFile);

		if (configFileURL == null) {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			configFileURL = classLoader.getResource(configFile);
		}

		configuration = ConfigurationFactory.parseConfiguration(configFileURL);

		configuration.setName(cacheManagerName);
	}

	protected Configuration configuration;
	protected boolean usingDefault = false;

}