/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.management;

import org.ehcache.Cache;
import org.ehcache.config.CacheRuntimeConfiguration;

/**
 * @author Dante Wang
 */
public class CacheMBean {

	public CacheMBean(String cacheName, Cache<?, ?> cache) {
		_cacheName = cacheName;
		_cache = cache;

		_cacheRuntimeConfiguration = cache.getRuntimeConfiguration();
	}

	public void clear() {
		_cache.clear();
	}

	public String getKeyType() {
		return _cacheRuntimeConfiguration.getKeyType(
		).getName();
	}

	public String getName() {
		return _cacheName;
	}

	public String getValueType() {
		return _cacheRuntimeConfiguration.getValueType(
		).getName();
	}

	private final Cache<?, ?> _cache;
	private final String _cacheName;
	private final CacheRuntimeConfiguration<?, ?> _cacheRuntimeConfiguration;

}