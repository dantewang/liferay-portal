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

package com.liferay.portal.transaction.test.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.transaction.test.exception.NoSuchTransactionEntryException;
import com.liferay.portal.transaction.test.model.TransactionEntry;

/**
 * The persistence interface for the transaction entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TransactionEntryUtil
 * @generated
 */
@ProviderType
public interface TransactionEntryPersistence
	extends BasePersistence<TransactionEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link TransactionEntryUtil} to access the transaction entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Caches the transaction entry in the entity cache if it is enabled.
	 *
	 * @param transactionEntry the transaction entry
	 */
	public void cacheResult(TransactionEntry transactionEntry);

	/**
	 * Caches the transaction entries in the entity cache if it is enabled.
	 *
	 * @param transactionEntries the transaction entries
	 */
	public void cacheResult(
		java.util.List<TransactionEntry> transactionEntries);

	/**
	 * Creates a new transaction entry with the primary key. Does not add the transaction entry to the database.
	 *
	 * @param transactionEntryId the primary key for the new transaction entry
	 * @return the new transaction entry
	 */
	public TransactionEntry create(long transactionEntryId);

	/**
	 * Removes the transaction entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry that was removed
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	public TransactionEntry remove(long transactionEntryId)
		throws NoSuchTransactionEntryException;

	public TransactionEntry updateImpl(TransactionEntry transactionEntry);

	/**
	 * Returns the transaction entry with the primary key or throws a <code>NoSuchTransactionEntryException</code> if it could not be found.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	public TransactionEntry findByPrimaryKey(long transactionEntryId)
		throws NoSuchTransactionEntryException;

	/**
	 * Returns the transaction entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry, or <code>null</code> if a transaction entry with the primary key could not be found
	 */
	public TransactionEntry fetchByPrimaryKey(long transactionEntryId);

	/**
	 * Returns all the transaction entries.
	 *
	 * @return the transaction entries
	 */
	public java.util.List<TransactionEntry> findAll();

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
	public java.util.List<TransactionEntry> findAll(int start, int end);

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
	public java.util.List<TransactionEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TransactionEntry>
			orderByComparator);

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
	public java.util.List<TransactionEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TransactionEntry>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Removes all the transaction entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of transaction entries.
	 *
	 * @return the number of transaction entries
	 */
	public int countAll();

}