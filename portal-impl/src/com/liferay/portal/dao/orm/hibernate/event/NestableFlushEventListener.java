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

package com.liferay.portal.dao.orm.hibernate.event;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.internal.DefaultFlushEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.spi.StatisticsImplementor;

/**
 * @author Matthew Tambara
 */
public class NestableFlushEventListener extends DefaultFlushEventListener {

	public static final NestableFlushEventListener INSTANCE =
		new NestableFlushEventListener();

	@Override
	public void onFlush(FlushEvent event) throws HibernateException {
		EventSource eventSource = event.getSession();

		PersistenceContext persistenceContext =
			eventSource.getPersistenceContext();

		Map.Entry<Object, EntityEntry>[] entityEntries =
			persistenceContext.reentrantSafeEntityEntries();

		if (entityEntries.length == 0) {
			int collectionEntriesSize =
				persistenceContext.getCollectionEntriesSize();

			if (collectionEntriesSize == 0) {
				return;
			}
		}

		boolean flushing = persistenceContext.isFlushing();

		try {
			flushEverythingToExecutions(event);

			persistenceContext.setFlushing(true);

			performExecutions(eventSource);

			postFlush(eventSource);
		}
		finally {
			persistenceContext.setFlushing(flushing);
		}

		SessionFactoryImplementor sessionFactoryImplementor =
			eventSource.getFactory();

		Statistics statistics = sessionFactoryImplementor.getStatistics();

		if (statistics.isStatisticsEnabled()) {
			StatisticsImplementor statisticsImplementor =
				sessionFactoryImplementor.getStatistics();

			statisticsImplementor.flush();
		}
	}

}