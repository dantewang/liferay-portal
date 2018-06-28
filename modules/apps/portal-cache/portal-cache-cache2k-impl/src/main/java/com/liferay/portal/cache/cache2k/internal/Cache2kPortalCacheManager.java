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

package com.liferay.portal.cache.cache2k.internal;

import com.liferay.portal.cache.BasePortalCacheManager;
import com.liferay.portal.cache.configuration.PortalCacheConfiguration;
import com.liferay.portal.cache.configuration.PortalCacheManagerConfiguration;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.util.Props;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheManager;
import org.cache2k.spi.Cache2kCoreProvider;
import org.cache2k.spi.SingleProviderResolver;

import javax.management.MBeanServer;

import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;

/**
 * @author Xiangyue Cai
 */
public class Cache2kPortalCacheManager<K extends Serializable, V>
        extends BasePortalCacheManager<K, V> {

    @Override
    public void reconfigurePortalCaches(URL configurationURL) {

    }

    @Override
    protected PortalCache<K, V> createPortalCache(
            PortalCacheConfiguration portalCacheConfiguration) {
        String portalCacheName = portalCacheConfiguration.getPortalCacheName();

        Cache2kBuilder<String, String> b = null;

        synchronized (_cacheManager) {
            b = new Cache2kBuilder<String, String>(){}
            .manager(CacheManager.getInstance(_cacheManager.getName()))
                    .name(portalCacheName);
        }

        Cache<String, String> c = b.build();

        return new Cache2kPortalCache<>(this, c);
    }

    @Override
    protected void doClearAll() {
        if(_cacheManager != null){
            _cacheManager.clear();
        }
    }

    @Override
    protected void doDestroy() {
        _provider.close();
    }

    @Override
    protected void doRemovePortalCache(String portalCacheName) {
        _cacheManager.getCache(portalCacheName).clear();

    }

    @Override
    protected PortalCacheManagerConfiguration
    getPortalCacheManagerConfiguration() {
        return new PortalCacheManagerConfiguration(
                null,new PortalCacheConfiguration(
                        "test",Collections.<Properties>emptySet(),
                new Properties()),null);
    }

    @Override
    protected void initPortalCacheManager() {
        _provider = SingleProviderResolver.resolveMandatory(
                Cache2kCoreProvider.class);
        _cacheManager = _provider.getManager(
                _provider.getDefaultClassLoader(),getPortalCacheManagerName());
    }

    @Override
    protected void removeConfigurableEhcachePortalCacheListeners(
            PortalCache<K, V> portalCache) {

    }

    protected MBeanServer mBeanServer;

    protected volatile Props props;

    private CacheManager _cacheManager;
    private Cache2kCoreProvider _provider;
    private PortalCacheManagerConfiguration _portalCacheManagerConfiguration;
}
