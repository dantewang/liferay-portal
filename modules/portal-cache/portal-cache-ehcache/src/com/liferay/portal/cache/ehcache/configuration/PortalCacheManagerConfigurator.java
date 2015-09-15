package com.liferay.portal.cache.ehcache.configuration;

import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;

public interface PortalCacheManagerConfigurator<T extends CacheManagerConfigurator<?>> {

	public T getCacheManagerConfigurator();

	public PortalCacheManagerConfiguration getPortalCacheManagerConfiguration();

}