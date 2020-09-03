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

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchNotCachedEntryException;
import com.liferay.portal.tools.service.builder.test.model.NotCachedEntry;
import com.liferay.portal.tools.service.builder.test.model.NotCachedEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.NotCachedEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.NotCachedEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.NotCachedEntryPersistence;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * The persistence implementation for the not cached entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class NotCachedEntryPersistenceImpl
	extends BasePersistenceImpl<NotCachedEntry>
	implements NotCachedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NotCachedEntryUtil</code> to access the not cached entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NotCachedEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @return the matching not cached entries
	 */
	@Override
	public List<NotCachedEntry> findByC_C(long column1, long column2) {
		return findByC_C(
			column1, column2, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotCachedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param start the lower bound of the range of not cached entries
	 * @param end the upper bound of the range of not cached entries (not inclusive)
	 * @return the range of matching not cached entries
	 */
	@Override
	public List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end) {

		return findByC_C(column1, column2, start, end, null);
	}

	/**
	 * Returns an ordered range of all the not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotCachedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param start the lower bound of the range of not cached entries
	 * @param end the upper bound of the range of not cached entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching not cached entries
	 */
	@Override
	public List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		return findByC_C(column1, column2, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotCachedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param start the lower bound of the range of not cached entries
	 * @param end the upper bound of the range of not cached entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching not cached entries
	 */
	@Override
	public List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end,
		OrderByComparator<NotCachedEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_C;
				finderArgs = new Object[] {column1, column2};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_C;
			finderArgs = new Object[] {
				column1, column2, start, end, orderByComparator
			};
		}

		List<NotCachedEntry> list = null;

		if (useFinderCache) {
			list = (List<NotCachedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (NotCachedEntry notCachedEntry : list) {
					if ((column1 != notCachedEntry.getColumn1()) ||
						(column2 != notCachedEntry.getColumn2())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_NOTCACHEDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_COLUMN1_2);

			sb.append(_FINDER_COLUMN_C_C_COLUMN2_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(NotCachedEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(column1);

				queryPos.add(column2);

				list = (List<NotCachedEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching not cached entry
	 * @throws NoSuchNotCachedEntryException if a matching not cached entry could not be found
	 */
	@Override
	public NotCachedEntry findByC_C_First(
			long column1, long column2,
			OrderByComparator<NotCachedEntry> orderByComparator)
		throws NoSuchNotCachedEntryException {

		NotCachedEntry notCachedEntry = fetchByC_C_First(
			column1, column2, orderByComparator);

		if (notCachedEntry != null) {
			return notCachedEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("column1=");
		sb.append(column1);

		sb.append(", column2=");
		sb.append(column2);

		sb.append("}");

		throw new NoSuchNotCachedEntryException(sb.toString());
	}

	/**
	 * Returns the first not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching not cached entry, or <code>null</code> if a matching not cached entry could not be found
	 */
	@Override
	public NotCachedEntry fetchByC_C_First(
		long column1, long column2,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		List<NotCachedEntry> list = findByC_C(
			column1, column2, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching not cached entry
	 * @throws NoSuchNotCachedEntryException if a matching not cached entry could not be found
	 */
	@Override
	public NotCachedEntry findByC_C_Last(
			long column1, long column2,
			OrderByComparator<NotCachedEntry> orderByComparator)
		throws NoSuchNotCachedEntryException {

		NotCachedEntry notCachedEntry = fetchByC_C_Last(
			column1, column2, orderByComparator);

		if (notCachedEntry != null) {
			return notCachedEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("column1=");
		sb.append(column1);

		sb.append(", column2=");
		sb.append(column2);

		sb.append("}");

		throw new NoSuchNotCachedEntryException(sb.toString());
	}

	/**
	 * Returns the last not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching not cached entry, or <code>null</code> if a matching not cached entry could not be found
	 */
	@Override
	public NotCachedEntry fetchByC_C_Last(
		long column1, long column2,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		int count = countByC_C(column1, column2);

		if (count == 0) {
			return null;
		}

		List<NotCachedEntry> list = findByC_C(
			column1, column2, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the not cached entries before and after the current not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the current not cached entry
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next not cached entry
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	@Override
	public NotCachedEntry[] findByC_C_PrevAndNext(
			long nestedSetsTreeEntryId, long column1, long column2,
			OrderByComparator<NotCachedEntry> orderByComparator)
		throws NoSuchNotCachedEntryException {

		NotCachedEntry notCachedEntry = findByPrimaryKey(nestedSetsTreeEntryId);

		Session session = null;

		try {
			session = openSession();

			NotCachedEntry[] array = new NotCachedEntryImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, notCachedEntry, column1, column2, orderByComparator,
				true);

			array[1] = notCachedEntry;

			array[2] = getByC_C_PrevAndNext(
				session, notCachedEntry, column1, column2, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected NotCachedEntry getByC_C_PrevAndNext(
		Session session, NotCachedEntry notCachedEntry, long column1,
		long column2, OrderByComparator<NotCachedEntry> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_NOTCACHEDENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COLUMN1_2);

		sb.append(_FINDER_COLUMN_C_C_COLUMN2_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(NotCachedEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(column1);

		queryPos.add(column2);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notCachedEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotCachedEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the not cached entries where column1 = &#63; and column2 = &#63; from the database.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 */
	@Override
	public void removeByC_C(long column1, long column2) {
		for (NotCachedEntry notCachedEntry :
				findByC_C(
					column1, column2, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(notCachedEntry);
		}
	}

	/**
	 * Returns the number of not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @return the number of matching not cached entries
	 */
	@Override
	public int countByC_C(long column1, long column2) {
		FinderPath finderPath = _finderPathCountByC_C;

		Object[] finderArgs = new Object[] {column1, column2};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_NOTCACHEDENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_C_COLUMN1_2);

			sb.append(_FINDER_COLUMN_C_C_COLUMN2_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(column1);

				queryPos.add(column2);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_C_C_COLUMN1_2 =
		"notCachedEntry.column1 = ? AND ";

	private static final String _FINDER_COLUMN_C_C_COLUMN2_2 =
		"notCachedEntry.column2 = ?";

	public NotCachedEntryPersistenceImpl() {
		setModelClass(NotCachedEntry.class);

		setModelImplClass(NotCachedEntryImpl.class);
		setModelPKClass(long.class);

		setTable(NotCachedEntryTable.INSTANCE);
	}

	/**
	 * Caches the not cached entry in the entity cache if it is enabled.
	 *
	 * @param notCachedEntry the not cached entry
	 */
	@Override
	public void cacheResult(NotCachedEntry notCachedEntry) {
		entityCache.putResult(
			NotCachedEntryImpl.class, notCachedEntry.getPrimaryKey(),
			notCachedEntry);
	}

	/**
	 * Caches the not cached entries in the entity cache if it is enabled.
	 *
	 * @param notCachedEntries the not cached entries
	 */
	@Override
	public void cacheResult(List<NotCachedEntry> notCachedEntries) {
		for (NotCachedEntry notCachedEntry : notCachedEntries) {
			if (entityCache.getResult(
					NotCachedEntryImpl.class, notCachedEntry.getPrimaryKey()) ==
						null) {

				cacheResult(notCachedEntry);
			}
		}
	}

	/**
	 * Clears the cache for all not cached entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(NotCachedEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the not cached entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(NotCachedEntry notCachedEntry) {
		entityCache.removeResult(NotCachedEntryImpl.class, notCachedEntry);
	}

	@Override
	public void clearCache(List<NotCachedEntry> notCachedEntries) {
		for (NotCachedEntry notCachedEntry : notCachedEntries) {
			entityCache.removeResult(NotCachedEntryImpl.class, notCachedEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(NotCachedEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new not cached entry with the primary key. Does not add the not cached entry to the database.
	 *
	 * @param nestedSetsTreeEntryId the primary key for the new not cached entry
	 * @return the new not cached entry
	 */
	@Override
	public NotCachedEntry create(long nestedSetsTreeEntryId) {
		NotCachedEntry notCachedEntry = new NotCachedEntryImpl();

		notCachedEntry.setNew(true);
		notCachedEntry.setPrimaryKey(nestedSetsTreeEntryId);

		return notCachedEntry;
	}

	/**
	 * Removes the not cached entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry that was removed
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	@Override
	public NotCachedEntry remove(long nestedSetsTreeEntryId)
		throws NoSuchNotCachedEntryException {

		return remove((Serializable)nestedSetsTreeEntryId);
	}

	/**
	 * Removes the not cached entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the not cached entry
	 * @return the not cached entry that was removed
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	@Override
	public NotCachedEntry remove(Serializable primaryKey)
		throws NoSuchNotCachedEntryException {

		Session session = null;

		try {
			session = openSession();

			NotCachedEntry notCachedEntry = (NotCachedEntry)session.get(
				NotCachedEntryImpl.class, primaryKey);

			if (notCachedEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchNotCachedEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(notCachedEntry);
		}
		catch (NoSuchNotCachedEntryException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected NotCachedEntry removeImpl(NotCachedEntry notCachedEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(notCachedEntry)) {
				notCachedEntry = (NotCachedEntry)session.get(
					NotCachedEntryImpl.class,
					notCachedEntry.getPrimaryKeyObj());
			}

			if (notCachedEntry != null) {
				session.delete(notCachedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (notCachedEntry != null) {
			clearCache(notCachedEntry);
		}

		return notCachedEntry;
	}

	@Override
	public NotCachedEntry updateImpl(NotCachedEntry notCachedEntry) {
		boolean isNew = notCachedEntry.isNew();

		if (!(notCachedEntry instanceof NotCachedEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(notCachedEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					notCachedEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in notCachedEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NotCachedEntry implementation " +
					notCachedEntry.getClass());
		}

		NotCachedEntryModelImpl notCachedEntryModelImpl =
			(NotCachedEntryModelImpl)notCachedEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(notCachedEntry);
			}
			else {
				notCachedEntry = (NotCachedEntry)session.merge(notCachedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			NotCachedEntryImpl.class, notCachedEntryModelImpl, false, true);

		if (isNew) {
			notCachedEntry.setNew(false);
		}

		notCachedEntry.resetOriginalValues();

		return notCachedEntry;
	}

	/**
	 * Returns the not cached entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the not cached entry
	 * @return the not cached entry
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	@Override
	public NotCachedEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchNotCachedEntryException {

		NotCachedEntry notCachedEntry = fetchByPrimaryKey(primaryKey);

		if (notCachedEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchNotCachedEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return notCachedEntry;
	}

	/**
	 * Returns the not cached entry with the primary key or throws a <code>NoSuchNotCachedEntryException</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	@Override
	public NotCachedEntry findByPrimaryKey(long nestedSetsTreeEntryId)
		throws NoSuchNotCachedEntryException {

		return findByPrimaryKey((Serializable)nestedSetsTreeEntryId);
	}

	/**
	 * Returns the not cached entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry, or <code>null</code> if a not cached entry with the primary key could not be found
	 */
	@Override
	public NotCachedEntry fetchByPrimaryKey(long nestedSetsTreeEntryId) {
		return fetchByPrimaryKey((Serializable)nestedSetsTreeEntryId);
	}

	/**
	 * Returns all the not cached entries.
	 *
	 * @return the not cached entries
	 */
	@Override
	public List<NotCachedEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the not cached entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotCachedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of not cached entries
	 * @param end the upper bound of the range of not cached entries (not inclusive)
	 * @return the range of not cached entries
	 */
	@Override
	public List<NotCachedEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the not cached entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotCachedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of not cached entries
	 * @param end the upper bound of the range of not cached entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of not cached entries
	 */
	@Override
	public List<NotCachedEntry> findAll(
		int start, int end,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the not cached entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotCachedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of not cached entries
	 * @param end the upper bound of the range of not cached entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of not cached entries
	 */
	@Override
	public List<NotCachedEntry> findAll(
		int start, int end, OrderByComparator<NotCachedEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<NotCachedEntry> list = null;

		if (useFinderCache) {
			list = (List<NotCachedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_NOTCACHEDENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_NOTCACHEDENTRY;

				sql = sql.concat(NotCachedEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<NotCachedEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the not cached entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (NotCachedEntry notCachedEntry : findAll()) {
			remove(notCachedEntry);
		}
	}

	/**
	 * Returns the number of not cached entries.
	 *
	 * @return the number of not cached entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_NOTCACHEDENTRY);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
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
		return "nestedSetsTreeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NOTCACHEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NotCachedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the not cached entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			NotCachedEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class, new NotCachedEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", NotCachedEntry.class.getName()));

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByC_C = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"column1", "column2"}, true);

		_finderPathWithoutPaginationFindByC_C = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"column1", "column2"}, true);

		_finderPathCountByC_C = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"column1", "column2"}, false);
	}

	public void destroy() {
		entityCache.removeCache(NotCachedEntryImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	protected EntityCache entityCache = ProxyFactory.newDummyInstance(
		EntityCache.class);
	protected FinderCache finderCache = ProxyFactory.newDummyInstance(
		FinderCache.class);

	private static final String _SQL_SELECT_NOTCACHEDENTRY =
		"SELECT notCachedEntry FROM NotCachedEntry notCachedEntry";

	private static final String _SQL_SELECT_NOTCACHEDENTRY_WHERE =
		"SELECT notCachedEntry FROM NotCachedEntry notCachedEntry WHERE ";

	private static final String _SQL_COUNT_NOTCACHEDENTRY =
		"SELECT COUNT(notCachedEntry) FROM NotCachedEntry notCachedEntry";

	private static final String _SQL_COUNT_NOTCACHEDENTRY_WHERE =
		"SELECT COUNT(notCachedEntry) FROM NotCachedEntry notCachedEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "notCachedEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No NotCachedEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No NotCachedEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		NotCachedEntryPersistenceImpl.class);

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					MapUtil.singletonDictionary("cache.name", cacheName)));
		}

		return finderPath;
	}

	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;
	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();

	private static class NotCachedEntryModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return FINDER_ARGS_EMPTY;
				}

				return null;
			}

			NotCachedEntryModelImpl notCachedEntryModelImpl =
				(NotCachedEntryModelImpl)baseModel;

			long columnBitmask = notCachedEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(
					notCachedEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						notCachedEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(
					notCachedEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private Object[] _getValue(
			NotCachedEntryModelImpl notCachedEntryModelImpl,
			String[] columnNames, boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						notCachedEntryModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = notCachedEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static Map<FinderPath, Long> _finderPathColumnBitmasksCache =
			new ConcurrentHashMap<>();

	}

}