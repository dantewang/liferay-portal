package com.liferay.portal.cache.ehcache.configuration;

public interface CacheManagerConfigurator<T> {

	public boolean usingDefault();

	public T getCacheManagerConfiguration();

}