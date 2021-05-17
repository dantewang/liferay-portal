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

package com.liferay.portal.dao.orm.hibernate.jmx;

import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.util.PropsValues;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.NaturalIdCacheStatistics;
import org.hibernate.stat.NaturalIdStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

/**
 * @author Shuyang Zhou
 */
@OSGiBeanProperties(property = "jmx.objectname=Hibernate:name=statistics")
public class HibernateStatisticsService implements Statistics {

	@Override
	public void clear() {
		_statistics.clear();
	}

	@Override
	public CacheRegionStatistics getCacheRegionStatistics(String regionName) {
		return _statistics.getCacheRegionStatistics(regionName);
	}

	@Override
	public long getCloseStatementCount() {
		return _statistics.getCloseStatementCount();
	}

	@Override
	public long getCollectionFetchCount() {
		return _statistics.getCollectionFetchCount();
	}

	@Override
	public long getCollectionLoadCount() {
		return _statistics.getCollectionLoadCount();
	}

	@Override
	public long getCollectionRecreateCount() {
		return _statistics.getCollectionRecreateCount();
	}

	@Override
	public long getCollectionRemoveCount() {
		return _statistics.getCollectionRemoveCount();
	}

	@Override
	public String[] getCollectionRoleNames() {
		return _statistics.getCollectionRoleNames();
	}

	@Override
	public CollectionStatistics getCollectionStatistics(String role) {
		return _statistics.getCollectionStatistics(role);
	}

	@Override
	public long getCollectionUpdateCount() {
		return _statistics.getCollectionUpdateCount();
	}

	@Override
	public long getConnectCount() {
		return _statistics.getConnectCount();
	}

	@Override
	public CacheRegionStatistics getDomainDataRegionStatistics(
		String regionName) {

		return _statistics.getDomainDataRegionStatistics(regionName);
	}

	@Override
	public long getEntityDeleteCount() {
		return _statistics.getEntityDeleteCount();
	}

	@Override
	public long getEntityFetchCount() {
		return _statistics.getEntityFetchCount();
	}

	@Override
	public long getEntityInsertCount() {
		return _statistics.getEntityInsertCount();
	}

	@Override
	public long getEntityLoadCount() {
		return _statistics.getEntityLoadCount();
	}

	@Override
	public String[] getEntityNames() {
		return _statistics.getEntityNames();
	}

	@Override
	public EntityStatistics getEntityStatistics(String entityName) {
		return _statistics.getEntityStatistics(entityName);
	}

	@Override
	public long getEntityUpdateCount() {
		return _statistics.getEntityUpdateCount();
	}

	@Override
	public long getFlushCount() {
		return _statistics.getFlushCount();
	}

	@Override
	public long getNaturalIdCacheHitCount() {
		return _statistics.getNaturalIdCacheHitCount();
	}

	@Override
	public long getNaturalIdCacheMissCount() {
		return _statistics.getNaturalIdCacheMissCount();
	}

	@Override
	public long getNaturalIdCachePutCount() {
		return _statistics.getNaturalIdCachePutCount();
	}

	@Override
	public NaturalIdCacheStatistics getNaturalIdCacheStatistics(
		String regionName) {

		return _statistics.getNaturalIdCacheStatistics(regionName);
	}

	@Override
	public long getNaturalIdQueryExecutionCount() {
		return _statistics.getNaturalIdQueryExecutionCount();
	}

	@Override
	public long getNaturalIdQueryExecutionMaxTime() {
		return _statistics.getNaturalIdQueryExecutionMaxTime();
	}

	@Override
	public String getNaturalIdQueryExecutionMaxTimeEntity() {
		return _statistics.getNaturalIdQueryExecutionMaxTimeEntity();
	}

	@Override
	public String getNaturalIdQueryExecutionMaxTimeRegion() {
		return _statistics.getNaturalIdQueryExecutionMaxTimeRegion();
	}

	@Override
	public NaturalIdStatistics getNaturalIdStatistics(String entityName) {
		return _statistics.getNaturalIdStatistics(entityName);
	}

	@Override
	public long getOptimisticFailureCount() {
		return _statistics.getOptimisticFailureCount();
	}

	@Override
	public long getPrepareStatementCount() {
		return _statistics.getPrepareStatementCount();
	}

	@Override
	public String[] getQueries() {
		return _statistics.getQueries();
	}

	@Override
	public long getQueryCacheHitCount() {
		return _statistics.getQueryCacheHitCount();
	}

	@Override
	public long getQueryCacheMissCount() {
		return _statistics.getQueryCacheMissCount();
	}

	@Override
	public long getQueryCachePutCount() {
		return _statistics.getQueryCachePutCount();
	}

	@Override
	public long getQueryExecutionCount() {
		return _statistics.getQueryExecutionCount();
	}

	@Override
	public long getQueryExecutionMaxTime() {
		return _statistics.getQueryExecutionMaxTime();
	}

	@Override
	public String getQueryExecutionMaxTimeQueryString() {
		return _statistics.getQueryExecutionMaxTimeQueryString();
	}

	@Override
	public CacheRegionStatistics getQueryRegionStatistics(String regionName) {
		return _statistics.getQueryRegionStatistics(regionName);
	}

	@Override
	public QueryStatistics getQueryStatistics(String queryString) {
		return _statistics.getQueryStatistics(queryString);
	}

	@Override
	public long getSecondLevelCacheHitCount() {
		return _statistics.getSecondLevelCacheHitCount();
	}

	@Override
	public long getSecondLevelCacheMissCount() {
		return _statistics.getSecondLevelCacheMissCount();
	}

	@Override
	public long getSecondLevelCachePutCount() {
		return _statistics.getSecondLevelCachePutCount();
	}

	@Override
	public String[] getSecondLevelCacheRegionNames() {
		return _statistics.getSecondLevelCacheRegionNames();
	}

	@Override
	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(
		String regionName) {

		return _statistics.getSecondLevelCacheStatistics(regionName);
	}

	@Override
	public long getSessionCloseCount() {
		return _statistics.getSessionCloseCount();
	}

	@Override
	public long getSessionOpenCount() {
		return _statistics.getSessionOpenCount();
	}

	@Override
	public long getStartTime() {
		return _statistics.getStartTime();
	}

	@Override
	public long getSuccessfulTransactionCount() {
		return _statistics.getSuccessfulTransactionCount();
	}

	@Override
	public long getTransactionCount() {
		return _statistics.getTransactionCount();
	}

	@Override
	public long getUpdateTimestampsCacheHitCount() {
		return _statistics.getUpdateTimestampsCacheHitCount();
	}

	@Override
	public long getUpdateTimestampsCacheMissCount() {
		return _statistics.getUpdateTimestampsCacheMissCount();
	}

	@Override
	public long getUpdateTimestampsCachePutCount() {
		return _statistics.getUpdateTimestampsCachePutCount();
	}

	@Override
	public boolean isStatisticsEnabled() {
		return _statistics.isStatisticsEnabled();
	}

	@Override
	public void logSummary() {
		_statistics.logSummary();
	}

	public void setSessionFactoryImplementor(
		SessionFactoryImplementor sessionFactoryImplementor) {

		_statistics = sessionFactoryImplementor.getStatistics();

		_statistics.setStatisticsEnabled(
			PropsValues.HIBERNATE_GENERATE_STATISTICS);
	}

	@Override
	public void setStatisticsEnabled(boolean statisticsEnabled) {
		_statistics.setStatisticsEnabled(statisticsEnabled);
	}

	private Statistics _statistics;

}