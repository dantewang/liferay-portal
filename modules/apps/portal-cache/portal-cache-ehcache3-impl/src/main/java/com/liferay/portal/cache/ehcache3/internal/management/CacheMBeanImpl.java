/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.management;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.ehcache.Cache;
import org.ehcache.config.CacheRuntimeConfiguration;

/**
 * @author Dante Wang
 */
public class CacheMBeanImpl extends StandardMBean implements CacheMBean {

	public CacheMBeanImpl(String cacheName, Cache<?, ?> cache)
		throws NotCompliantMBeanException {

		super(CacheMBean.class);

		_cacheName = cacheName;
		_cache = cache;

		_cacheRuntimeConfiguration = cache.getRuntimeConfiguration();
	}

	@Override
	public void clear() {
		_cache.clear();
	}

	@Override
	public String getKeyType() {
		return _cacheRuntimeConfiguration.getKeyType(
		).getName();
	}

	@Override
	public String getName() {
		return _cacheName;
	}

	@Override
	public String getValueType() {
		return _cacheRuntimeConfiguration.getValueType(
		).getName();
	}

	private final Cache<?, ?> _cache;
	private final String _cacheName;
	private final CacheRuntimeConfiguration<?, ?> _cacheRuntimeConfiguration;

}