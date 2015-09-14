package com.liferay.portal.cache.ehcache.internal;

import com.liferay.portal.kernel.cache.configuration.PortalCacheManagerConfiguration;

public interface PortalCacheManagerConfigurator<T> {

	public T getEhcacheManagerConfiguration();

	public PortalCacheManagerConfiguration getPortalCacheManagerConfiguration();

}