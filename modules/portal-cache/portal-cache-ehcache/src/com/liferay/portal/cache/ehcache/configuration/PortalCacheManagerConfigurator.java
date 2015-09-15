package com.liferay.portal.cache.ehcache.configuration;

import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;

public interface PortalCacheManagerConfigurator<T> {

	public T getCacheManagerConfiguration();

	public PortalCacheManagerConfiguration getPortalCacheManagerConfiguration();

	public boolean usingDefault();

}