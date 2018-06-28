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

import com.liferay.portal.cache.BasePortalCache;
import com.liferay.portal.kernel.cache.PortalCacheManager;

import org.cache2k.Cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Xiangyue Cai
 */
public class Cache2kPortalCache<K extends Serializable, V>
        extends BasePortalCache<K, V>{

    public Cache2kPortalCache(
            PortalCacheManager<K, V> portalCacheManager,Cache cache) {
        super(portalCacheManager);

        this.cache = cache;
    }

    public Cache getCache2k() {
        return cache;
    }

    @Override
    public List<K> getKeys() {
        List<K> keys = new ArrayList<>();

        Iterator i = cache.keys().iterator();
        while(i.hasNext()){
            keys.add((K) i.next());
        }

        return keys;
    }

    @Override
    public String getPortalCacheName() {
        return cache.getName();
    }

    @Override
    public void removeAll() {
        cache.clear();

    }

    @Override
    protected V doGet(K key) {
        return (V)cache.get(key);
    }

    @Override
    protected void doPut(K key, V value, int timeToLive) {
        cache.put(key,value);
    }

    @Override
    protected V doPutIfAbsent(K key, V value, int timeToLive) {
        cache.put(key,value);

        return (V)cache.get(key);
    }

    @Override
    protected void doRemove(K key) {
        cache.remove(key);
    }

    @Override
    protected boolean doRemove(K key, V value) {
        cache.remove(key);

        return true;
    }

    @Override
    protected V doReplace(K key, V value, int timeToLive) {
        V res = (V)cache.get(key);
        cache.replace(key,value);

        return res;
    }

    @Override
    protected boolean doReplace(K key, V oldValue, V newValue, int timeToLive) {
        cache.replace(key,newValue);

        return true;
    }

    protected volatile Cache cache;
}
