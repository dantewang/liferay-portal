/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal.management;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.ehcache.core.statistics.CacheStatistics;

/**
 * @author Dante Wang
 */
public class CacheStatisticsMBeanImpl
	extends StandardMBean implements CacheStatisticsMBean {

	public CacheStatisticsMBeanImpl(
			String cacheName, CacheStatistics cacheStatistics)
		throws NotCompliantMBeanException {

		super(CacheStatisticsMBean.class);

		_cacheName = cacheName;
		_cacheStatistics = cacheStatistics;
	}

	@Override
	public void clear() {
		_cacheStatistics.clear();
	}

	@Override
	public long getCacheEvictions() {
		return _cacheStatistics.getCacheEvictions();
	}

	@Override
	public long getCacheExpirations() {
		return _cacheStatistics.getCacheExpirations();
	}

	@Override
	public long getCacheGets() {
		return _cacheStatistics.getCacheGets();
	}

	@Override
	public float getCacheHitPercentage() {
		return _cacheStatistics.getCacheHitPercentage();
	}

	@Override
	public long getCacheHits() {
		return _cacheStatistics.getCacheHits();
	}

	@Override
	public long getCacheMisses() {
		return _cacheStatistics.getCacheMisses();
	}

	@Override
	public float getCacheMissPercentage() {
		return _cacheStatistics.getCacheMissPercentage();
	}

	@Override
	public long getCachePuts() {
		return _cacheStatistics.getCachePuts();
	}

	@Override
	public long getCacheRemovals() {
		return _cacheStatistics.getCacheRemovals();
	}

	@Override
	public String getName() {
		return _cacheName;
	}

	private final String _cacheName;
	private final CacheStatistics _cacheStatistics;

}