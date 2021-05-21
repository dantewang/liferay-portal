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

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.internal.DefaultAutoFlushEventListener;
import org.hibernate.event.spi.AutoFlushEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.spi.StatisticsImplementor;

/**
 * @author Shuyang Zhou
 */
public class NestableAutoFlushEventListener
	extends DefaultAutoFlushEventListener {

	public static final NestableAutoFlushEventListener INSTANCE =
		new NestableAutoFlushEventListener();

	@Override
	public void onAutoFlush(AutoFlushEvent autoFlushEvent)
		throws HibernateException {

		EventSource eventSource = autoFlushEvent.getSession();

		if (!_isFlushable(eventSource)) {
			return;
		}

		ActionQueue actionQueue = eventSource.getActionQueue();

		int oldSize = actionQueue.numberOfCollectionRemovals();

		PersistenceContext persistenceContext =
			eventSource.getPersistenceContext();

		boolean flushing = persistenceContext.isFlushing();

		try {
			flushEverythingToExecutions(autoFlushEvent);
		}
		finally {
			persistenceContext.setFlushing(flushing);
		}

		if (_isFlushReallyNeeded(autoFlushEvent, eventSource)) {
			persistenceContext.setFlushing(true);

			try {
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
		else if (!persistenceContext.isFlushing()) {
			actionQueue.clearFromFlushNeededCheck(oldSize);
		}

		autoFlushEvent.setFlushRequired(
			_isFlushReallyNeeded(autoFlushEvent, eventSource));
	}

	private boolean _isFlushable(EventSource eventSource) {
		FlushMode flushMode = eventSource.getHibernateFlushMode();

		if (flushMode.lessThan(FlushMode.AUTO)) {
			return false;
		}

		if (eventSource.getDontFlushFromFind() != 0) {
			return false;
		}

		PersistenceContext persistenceContext =
			eventSource.getPersistenceContext();

		Map.Entry<Object, EntityEntry>[] entityEntries =
			persistenceContext.reentrantSafeEntityEntries();

		if (entityEntries.length > 0) {
			return true;
		}

		int collectionEntriesSize =
			persistenceContext.getCollectionEntriesSize();

		if (collectionEntriesSize > 0) {
			return true;
		}

		return false;
	}

	private boolean _isFlushReallyNeeded(
		AutoFlushEvent autoFlushEvent, EventSource eventSource) {

		if (eventSource.getHibernateFlushMode() == FlushMode.ALWAYS) {
			return true;
		}

		ActionQueue actionQueue = eventSource.getActionQueue();

		return actionQueue.areTablesToBeUpdated(
			autoFlushEvent.getQuerySpaces());
	}

}