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

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.NotCachedEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the not cached entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.NotCachedEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see NotCachedEntryPersistence
 * @generated
 */
public class NotCachedEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(NotCachedEntry notCachedEntry) {
		getPersistence().clearCache(notCachedEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, NotCachedEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<NotCachedEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<NotCachedEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<NotCachedEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static NotCachedEntry update(NotCachedEntry notCachedEntry) {
		return getPersistence().update(notCachedEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static NotCachedEntry update(
		NotCachedEntry notCachedEntry, ServiceContext serviceContext) {

		return getPersistence().update(notCachedEntry, serviceContext);
	}

	/**
	 * Returns all the not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @return the matching not cached entries
	 */
	public static List<NotCachedEntry> findByC_C(long column1, long column2) {
		return getPersistence().findByC_C(column1, column2);
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
	public static List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end) {

		return getPersistence().findByC_C(column1, column2, start, end);
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
	public static List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		return getPersistence().findByC_C(
			column1, column2, start, end, orderByComparator);
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
	public static List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end,
		OrderByComparator<NotCachedEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			column1, column2, start, end, orderByComparator, useFinderCache);
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
	public static NotCachedEntry findByC_C_First(
			long column1, long column2,
			OrderByComparator<NotCachedEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchNotCachedEntryException {

		return getPersistence().findByC_C_First(
			column1, column2, orderByComparator);
	}

	/**
	 * Returns the first not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching not cached entry, or <code>null</code> if a matching not cached entry could not be found
	 */
	public static NotCachedEntry fetchByC_C_First(
		long column1, long column2,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			column1, column2, orderByComparator);
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
	public static NotCachedEntry findByC_C_Last(
			long column1, long column2,
			OrderByComparator<NotCachedEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchNotCachedEntryException {

		return getPersistence().findByC_C_Last(
			column1, column2, orderByComparator);
	}

	/**
	 * Returns the last not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching not cached entry, or <code>null</code> if a matching not cached entry could not be found
	 */
	public static NotCachedEntry fetchByC_C_Last(
		long column1, long column2,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			column1, column2, orderByComparator);
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
	public static NotCachedEntry[] findByC_C_PrevAndNext(
			long nestedSetsTreeEntryId, long column1, long column2,
			OrderByComparator<NotCachedEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchNotCachedEntryException {

		return getPersistence().findByC_C_PrevAndNext(
			nestedSetsTreeEntryId, column1, column2, orderByComparator);
	}

	/**
	 * Removes all the not cached entries where column1 = &#63; and column2 = &#63; from the database.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 */
	public static void removeByC_C(long column1, long column2) {
		getPersistence().removeByC_C(column1, column2);
	}

	/**
	 * Returns the number of not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @return the number of matching not cached entries
	 */
	public static int countByC_C(long column1, long column2) {
		return getPersistence().countByC_C(column1, column2);
	}

	/**
	 * Caches the not cached entry in the entity cache if it is enabled.
	 *
	 * @param notCachedEntry the not cached entry
	 */
	public static void cacheResult(NotCachedEntry notCachedEntry) {
		getPersistence().cacheResult(notCachedEntry);
	}

	/**
	 * Caches the not cached entries in the entity cache if it is enabled.
	 *
	 * @param notCachedEntries the not cached entries
	 */
	public static void cacheResult(List<NotCachedEntry> notCachedEntries) {
		getPersistence().cacheResult(notCachedEntries);
	}

	/**
	 * Creates a new not cached entry with the primary key. Does not add the not cached entry to the database.
	 *
	 * @param nestedSetsTreeEntryId the primary key for the new not cached entry
	 * @return the new not cached entry
	 */
	public static NotCachedEntry create(long nestedSetsTreeEntryId) {
		return getPersistence().create(nestedSetsTreeEntryId);
	}

	/**
	 * Removes the not cached entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry that was removed
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	public static NotCachedEntry remove(long nestedSetsTreeEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchNotCachedEntryException {

		return getPersistence().remove(nestedSetsTreeEntryId);
	}

	public static NotCachedEntry updateImpl(NotCachedEntry notCachedEntry) {
		return getPersistence().updateImpl(notCachedEntry);
	}

	/**
	 * Returns the not cached entry with the primary key or throws a <code>NoSuchNotCachedEntryException</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	public static NotCachedEntry findByPrimaryKey(long nestedSetsTreeEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchNotCachedEntryException {

		return getPersistence().findByPrimaryKey(nestedSetsTreeEntryId);
	}

	/**
	 * Returns the not cached entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry, or <code>null</code> if a not cached entry with the primary key could not be found
	 */
	public static NotCachedEntry fetchByPrimaryKey(long nestedSetsTreeEntryId) {
		return getPersistence().fetchByPrimaryKey(nestedSetsTreeEntryId);
	}

	/**
	 * Returns all the not cached entries.
	 *
	 * @return the not cached entries
	 */
	public static List<NotCachedEntry> findAll() {
		return getPersistence().findAll();
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
	public static List<NotCachedEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<NotCachedEntry> findAll(
		int start, int end,
		OrderByComparator<NotCachedEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<NotCachedEntry> findAll(
		int start, int end, OrderByComparator<NotCachedEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the not cached entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of not cached entries.
	 *
	 * @return the number of not cached entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static NotCachedEntryPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<NotCachedEntryPersistence, NotCachedEntryPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			NotCachedEntryPersistence.class);

		ServiceTracker<NotCachedEntryPersistence, NotCachedEntryPersistence>
			serviceTracker =
				new ServiceTracker
					<NotCachedEntryPersistence, NotCachedEntryPersistence>(
						bundle.getBundleContext(),
						NotCachedEntryPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}