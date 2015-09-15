package com.liferay.portal.cache.ehcache.configuration;

import java.net.URL;

import com.liferay.portal.cache.ehcache.internal.EhcacheConfigurationHelperUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Validator;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

public abstract class AbstractEhcacheManagerConfigurator
	implements CacheManagerConfigurator<Configuration> {

	@Override
	public Configuration getCacheManagerConfiguration() {
		return configuration;
	}

	protected void processConfigFile(
		String configFile, String defaultConfigFile) {

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
	}

	@Override
	public boolean usingDefault() {
		return usingDefault;
	}

	protected boolean usingDefault = false;
	protected Configuration configuration;

}