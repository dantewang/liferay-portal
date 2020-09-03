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

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchNotCachedEntryException;
import com.liferay.portal.tools.service.builder.test.model.NotCachedEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the not cached entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see NotCachedEntryUtil
 * @generated
 */
@ProviderType
public interface NotCachedEntryPersistence
	extends BasePersistence<NotCachedEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link NotCachedEntryUtil} to access the not cached entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @return the matching not cached entries
	 */
	public java.util.List<NotCachedEntry> findByC_C(long column1, long column2);

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
	public java.util.List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end);

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
	public java.util.List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
			orderByComparator);

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
	public java.util.List<NotCachedEntry> findByC_C(
		long column1, long column2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching not cached entry
	 * @throws NoSuchNotCachedEntryException if a matching not cached entry could not be found
	 */
	public NotCachedEntry findByC_C_First(
			long column1, long column2,
			com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
				orderByComparator)
		throws NoSuchNotCachedEntryException;

	/**
	 * Returns the first not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching not cached entry, or <code>null</code> if a matching not cached entry could not be found
	 */
	public NotCachedEntry fetchByC_C_First(
		long column1, long column2,
		com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
			orderByComparator);

	/**
	 * Returns the last not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching not cached entry
	 * @throws NoSuchNotCachedEntryException if a matching not cached entry could not be found
	 */
	public NotCachedEntry findByC_C_Last(
			long column1, long column2,
			com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
				orderByComparator)
		throws NoSuchNotCachedEntryException;

	/**
	 * Returns the last not cached entry in the ordered set where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching not cached entry, or <code>null</code> if a matching not cached entry could not be found
	 */
	public NotCachedEntry fetchByC_C_Last(
		long column1, long column2,
		com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
			orderByComparator);

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
	public NotCachedEntry[] findByC_C_PrevAndNext(
			long nestedSetsTreeEntryId, long column1, long column2,
			com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
				orderByComparator)
		throws NoSuchNotCachedEntryException;

	/**
	 * Removes all the not cached entries where column1 = &#63; and column2 = &#63; from the database.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 */
	public void removeByC_C(long column1, long column2);

	/**
	 * Returns the number of not cached entries where column1 = &#63; and column2 = &#63;.
	 *
	 * @param column1 the column1
	 * @param column2 the column2
	 * @return the number of matching not cached entries
	 */
	public int countByC_C(long column1, long column2);

	/**
	 * Caches the not cached entry in the entity cache if it is enabled.
	 *
	 * @param notCachedEntry the not cached entry
	 */
	public void cacheResult(NotCachedEntry notCachedEntry);

	/**
	 * Caches the not cached entries in the entity cache if it is enabled.
	 *
	 * @param notCachedEntries the not cached entries
	 */
	public void cacheResult(java.util.List<NotCachedEntry> notCachedEntries);

	/**
	 * Creates a new not cached entry with the primary key. Does not add the not cached entry to the database.
	 *
	 * @param nestedSetsTreeEntryId the primary key for the new not cached entry
	 * @return the new not cached entry
	 */
	public NotCachedEntry create(long nestedSetsTreeEntryId);

	/**
	 * Removes the not cached entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry that was removed
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	public NotCachedEntry remove(long nestedSetsTreeEntryId)
		throws NoSuchNotCachedEntryException;

	public NotCachedEntry updateImpl(NotCachedEntry notCachedEntry);

	/**
	 * Returns the not cached entry with the primary key or throws a <code>NoSuchNotCachedEntryException</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry
	 * @throws NoSuchNotCachedEntryException if a not cached entry with the primary key could not be found
	 */
	public NotCachedEntry findByPrimaryKey(long nestedSetsTreeEntryId)
		throws NoSuchNotCachedEntryException;

	/**
	 * Returns the not cached entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry, or <code>null</code> if a not cached entry with the primary key could not be found
	 */
	public NotCachedEntry fetchByPrimaryKey(long nestedSetsTreeEntryId);

	/**
	 * Returns all the not cached entries.
	 *
	 * @return the not cached entries
	 */
	public java.util.List<NotCachedEntry> findAll();

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
	public java.util.List<NotCachedEntry> findAll(int start, int end);

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
	public java.util.List<NotCachedEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
			orderByComparator);

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
	public java.util.List<NotCachedEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<NotCachedEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the not cached entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of not cached entries.
	 *
	 * @return the number of not cached entries
	 */
	public int countAll();

}