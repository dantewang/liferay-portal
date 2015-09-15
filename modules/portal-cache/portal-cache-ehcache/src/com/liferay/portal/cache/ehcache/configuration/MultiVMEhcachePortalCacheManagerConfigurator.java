package com.liferay.portal.cache.ehcache.configuration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.util.Props;

import net.sf.ehcache.config.Configuration;

@Component(
	immediate = true,
	property = {
		PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM
	},
	service = PortalCacheManagerConfigurator.class
)
public class MultiVMEhcachePortalCacheManagerConfigurator
	extends AbstractEhcachePortalCacheManagerConfigurator {

	@Activate
	protected void activate() {
		Configuration configuration =
			_cacheManagerConfigurator.getCacheManagerConfiguration();

		
	}

	@Reference
	public void setCacheManagerConfigurator(
		CacheManagerConfigurator<Configuration> cacheManagerConfigurator) {

		_cacheManagerConfigurator = cacheManagerConfigurator;
	}

	@Override
	public CacheManagerConfigurator<Configuration>
		getCacheManagerConfigurator() {

		return _cacheManagerConfigurator;
	}

	@Override
	public PortalCacheManagerConfiguration
		getPortalCacheManagerConfiguration() {

		return _portalCacheManagerConfiguration;
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

	protected Props props;

	private PortalCacheManagerConfiguration _portalCacheManagerConfiguration;
	private CacheManagerConfigurator<Configuration> _cacheManagerConfigurator;

}