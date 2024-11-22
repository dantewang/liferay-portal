/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache3.internal.management;

import org.ehcache.core.statistics.CacheStatistics;

/**
 * @author Dante Wang
 */
public class CacheStatisticsMBean {

	public CacheStatisticsMBean(
		String cacheName, CacheStatistics cacheStatistics) {

		_cacheName = cacheName;
		_cacheStatistics = cacheStatistics;
	}

	public void clear() {
		_cacheStatistics.clear();
	}

	public long getCacheEvictions() {
		return _cacheStatistics.getCacheEvictions();
	}

	public long getCacheExpirations() {
		return _cacheStatistics.getCacheExpirations();
	}

	public long getCacheGets() {
		return _cacheStatistics.getCacheGets();
	}

	public float getCacheHitPercentage() {
		return _cacheStatistics.getCacheHitPercentage();
	}

	public long getCacheHits() {
		return _cacheStatistics.getCacheHits();
	}

	public long getCacheMisses() {
		return _cacheStatistics.getCacheMisses();
	}

	public float getCacheMissPercentage() {
		return _cacheStatistics.getCacheMissPercentage();
	}

	public long getCachePuts() {
		return _cacheStatistics.getCachePuts();
	}

	public long getCacheRemovals() {
		return _cacheStatistics.getCacheRemovals();
	}

	public String getName() {
		return _cacheName;
	}

	private final String _cacheName;
	private final CacheStatistics _cacheStatistics;

}