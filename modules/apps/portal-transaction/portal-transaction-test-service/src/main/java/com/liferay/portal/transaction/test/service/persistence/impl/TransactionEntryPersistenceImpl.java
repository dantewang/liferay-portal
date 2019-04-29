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

package com.liferay.portal.transaction.test.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.transaction.test.exception.NoSuchTransactionEntryException;
import com.liferay.portal.transaction.test.model.TransactionEntry;
import com.liferay.portal.transaction.test.model.impl.TransactionEntryImpl;
import com.liferay.portal.transaction.test.model.impl.TransactionEntryModelImpl;
import com.liferay.portal.transaction.test.service.persistence.TransactionEntryPersistence;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the transaction entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class TransactionEntryPersistenceImpl
	extends BasePersistenceImpl<TransactionEntry>
	implements TransactionEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TransactionEntryUtil</code> to access the transaction entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TransactionEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public TransactionEntryPersistenceImpl() {
		setModelClass(TransactionEntry.class);

		setModelImplClass(TransactionEntryImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(TransactionEntryModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the transaction entry in the entity cache if it is enabled.
	 *
	 * @param transactionEntry the transaction entry
	 */
	@Override
	public void cacheResult(TransactionEntry transactionEntry) {
		entityCache.putResult(
			TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
			TransactionEntryImpl.class, transactionEntry.getPrimaryKey(),
			transactionEntry);

		transactionEntry.resetOriginalValues();
	}

	/**
	 * Caches the transaction entries in the entity cache if it is enabled.
	 *
	 * @param transactionEntries the transaction entries
	 */
	@Override
	public void cacheResult(List<TransactionEntry> transactionEntries) {
		for (TransactionEntry transactionEntry : transactionEntries) {
			if (entityCache.getResult(
					TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
					TransactionEntryImpl.class,
					transactionEntry.getPrimaryKey()) == null) {

				cacheResult(transactionEntry);
			}
			else {
				transactionEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all transaction entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(TransactionEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the transaction entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(TransactionEntry transactionEntry) {
		entityCache.removeResult(
			TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
			TransactionEntryImpl.class, transactionEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<TransactionEntry> transactionEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (TransactionEntry transactionEntry : transactionEntries) {
			entityCache.removeResult(
				TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
				TransactionEntryImpl.class, transactionEntry.getPrimaryKey());
		}
	}

	/**
	 * Creates a new transaction entry with the primary key. Does not add the transaction entry to the database.
	 *
	 * @param transactionEntryId the primary key for the new transaction entry
	 * @return the new transaction entry
	 */
	@Override
	public TransactionEntry create(long transactionEntryId) {
		TransactionEntry transactionEntry = new TransactionEntryImpl();

		transactionEntry.setNew(true);
		transactionEntry.setPrimaryKey(transactionEntryId);

		return transactionEntry;
	}

	/**
	 * Removes the transaction entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry that was removed
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	@Override
	public TransactionEntry remove(long transactionEntryId)
		throws NoSuchTransactionEntryException {

		return remove((Serializable)transactionEntryId);
	}

	/**
	 * Removes the transaction entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the transaction entry
	 * @return the transaction entry that was removed
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	@Override
	public TransactionEntry remove(Serializable primaryKey)
		throws NoSuchTransactionEntryException {

		Session session = null;

		try {
			session = openSession();

			TransactionEntry transactionEntry = (TransactionEntry)session.get(
				TransactionEntryImpl.class, primaryKey);

			if (transactionEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchTransactionEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(transactionEntry);
		}
		catch (NoSuchTransactionEntryException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected TransactionEntry removeImpl(TransactionEntry transactionEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(transactionEntry)) {
				transactionEntry = (TransactionEntry)session.get(
					TransactionEntryImpl.class,
					transactionEntry.getPrimaryKeyObj());
			}

			if (transactionEntry != null) {
				session.delete(transactionEntry);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (transactionEntry != null) {
			clearCache(transactionEntry);
		}

		return transactionEntry;
	}

	@Override
	public TransactionEntry updateImpl(TransactionEntry transactionEntry) {
		boolean isNew = transactionEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (transactionEntry.isNew()) {
				session.save(transactionEntry);

				transactionEntry.setNew(false);
			}
			else {
				transactionEntry = (TransactionEntry)session.merge(
					transactionEntry);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(
			TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
			TransactionEntryImpl.class, transactionEntry.getPrimaryKey(),
			transactionEntry, false);

		transactionEntry.resetOriginalValues();

		return transactionEntry;
	}

	/**
	 * Returns the transaction entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the transaction entry
	 * @return the transaction entry
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	@Override
	public TransactionEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchTransactionEntryException {

		TransactionEntry transactionEntry = fetchByPrimaryKey(primaryKey);

		if (transactionEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchTransactionEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return transactionEntry;
	}

	/**
	 * Returns the transaction entry with the primary key or throws a <code>NoSuchTransactionEntryException</code> if it could not be found.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	@Override
	public TransactionEntry findByPrimaryKey(long transactionEntryId)
		throws NoSuchTransactionEntryException {

		return findByPrimaryKey((Serializable)transactionEntryId);
	}

	/**
	 * Returns the transaction entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry, or <code>null</code> if a transaction entry with the primary key could not be found
	 */
	@Override
	public TransactionEntry fetchByPrimaryKey(long transactionEntryId) {
		return fetchByPrimaryKey((Serializable)transactionEntryId);
	}

	/**
	 * Returns all the transaction entries.
	 *
	 * @return the transaction entries
	 */
	@Override
	public List<TransactionEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the transaction entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TransactionEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of transaction entries
	 * @param end the upper bound of the range of transaction entries (not inclusive)
	 * @return the range of transaction entries
	 */
	@Override
	public List<TransactionEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the transaction entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TransactionEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of transaction entries
	 * @param end the upper bound of the range of transaction entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of transaction entries
	 */
	@Override
	public List<TransactionEntry> findAll(
		int start, int end,
		OrderByComparator<TransactionEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the transaction entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TransactionEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of transaction entries
	 * @param end the upper bound of the range of transaction entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of transaction entries
	 */
	@Override
	public List<TransactionEntry> findAll(
		int start, int end,
		OrderByComparator<TransactionEntry> orderByComparator,
		boolean retrieveFromCache) {

		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			pagination = false;
			finderPath = _finderPathWithoutPaginationFindAll;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<TransactionEntry> list = null;

		if (retrieveFromCache) {
			list = (List<TransactionEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_TRANSACTIONENTRY);

				appendOrderByComparator(
					query, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_TRANSACTIONENTRY;

				if (pagination) {
					sql = sql.concat(TransactionEntryModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<TransactionEntry>)QueryUtil.list(
						q, getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<TransactionEntry>)QueryUtil.list(
						q, getDialect(), start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the transaction entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (TransactionEntry transactionEntry : findAll()) {
			remove(transactionEntry);
		}
	}

	/**
	 * Returns the number of transaction entries.
	 *
	 * @return the number of transaction entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_TRANSACTIONENTRY);

				count = (Long)q.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception e) {
				finderCache.removeResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "transactionEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TRANSACTIONENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TransactionEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the transaction entry persistence.
	 */
	public void afterPropertiesSet() {
		_finderPathWithPaginationFindAll = new FinderPath(
			TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
			TransactionEntryModelImpl.FINDER_CACHE_ENABLED,
			TransactionEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
			TransactionEntryModelImpl.FINDER_CACHE_ENABLED,
			TransactionEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			TransactionEntryModelImpl.ENTITY_CACHE_ENABLED,
			TransactionEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);
	}

	public void destroy() {
		entityCache.removeCache(TransactionEntryImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_TRANSACTIONENTRY =
		"SELECT transactionEntry FROM TransactionEntry transactionEntry";

	private static final String _SQL_COUNT_TRANSACTIONENTRY =
		"SELECT COUNT(transactionEntry) FROM TransactionEntry transactionEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS = "transactionEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No TransactionEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		TransactionEntryPersistenceImpl.class);

}