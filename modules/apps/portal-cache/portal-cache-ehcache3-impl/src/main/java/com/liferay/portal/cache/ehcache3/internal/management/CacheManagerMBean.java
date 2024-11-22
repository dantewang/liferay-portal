/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.management;

import org.ehcache.CacheManager;

/**
 * @author Dante Wang
 */
public class CacheManagerMBean {

	public CacheManagerMBean(
		String cacheManagerName, CacheManager cacheManager) {

		_cacheManagerName = cacheManagerName;
		_cacheManager = cacheManager;
	}

	public String getName() {
		return _cacheManagerName;
	}

	public String getStatus() {
		return String.valueOf(_cacheManager.getStatus());
	}

	private final CacheManager _cacheManager;
	private final String _cacheManagerName;

}