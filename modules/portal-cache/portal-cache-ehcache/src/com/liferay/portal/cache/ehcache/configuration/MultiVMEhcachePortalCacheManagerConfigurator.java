package com.liferay.portal.cache.ehcache.configuration;

import java.net.URL;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.cache.ehcache.internal.EhcacheConfigurationHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;

import net.sf.ehcache.config.Configuration;

@Component(
	immediate = true,
	property = {
		PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM
	},
	service = PortalCacheManagerConfigurator.class
)
public class MultiVMEhcachePortalCacheManagerConfigurator
	implements PortalCacheManagerConfigurator<CacheManagerConfigurator<Configuration>> {

	@Activate
	protected void activate() {
		String configFile = props.get(PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION);

		if (Validator.isNull(configFile)) {
			configFile = _DEFAULT_CONFIG_FILE_NAME;
			_usingDefault = true;
		}

		URL configFileURL = EhcacheConfigurationHelperUtil.class.getResource(
			configFile);

		if (configFileURL == null) {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			configFileURL = classLoader.getResource(configFile);
		}

		ObjectValuePair<Configuration, PortalCacheManagerConfiguration>
			configurationObjectValuePair =
				EhcacheConfigurationHelperUtil.getConfigurationObjectValuePair(
					PortalCacheManagerNames.MULTI_VM, configFileURL,
					true, _usingDefault, props);
	}

	@Reference
	public void setCacheManagerConfigurator(CacheManagerConfigurator<Configuration> c) {
		_cacheManagerConfigurator = c;
	}

	@Override
	public CacheManagerConfigurator<Configuration> getCacheManagerConfigurator() {
		return _cacheManagerConfigurator;
	}

	@Override
	public PortalCacheManagerConfiguration getPortalCacheManagerConfiguration() {
		return _pcmc;
	}

	@Override
	public boolean usingDefault() {
		return _usingDefault;
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

	private PortalCacheManagerConfiguration _pcmc;
	private CacheManagerConfigurator<Configuration> _cacheManagerConfigurator;
	private boolean _usingDefault = false;
	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-multi-vm-clustered.xml";

	protected Props props;

}