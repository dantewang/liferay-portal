package com.liferay.portal.cache.ehcache.internal;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;

import net.sf.ehcache.config.Configuration;

@Component(
	immediate = true,
	property = {
		PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM
	},
	service = PortalCacheManagerConfigurator.class
)
public class MultiVMEhcachePortalCacheManagerConfigurator
	implements PortalCacheManagerConfigurator<Configuration> {

	@Override
	public Configuration getEhcacheManagerConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PortalCacheManagerConfiguration getPortalCacheManagerConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}