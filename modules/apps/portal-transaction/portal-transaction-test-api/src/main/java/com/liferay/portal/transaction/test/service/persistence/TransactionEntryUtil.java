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

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.transaction.test.model.TransactionEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the transaction entry service. This utility wraps <code>com.liferay.portal.transaction.test.service.persistence.impl.TransactionEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TransactionEntryPersistence
 * @generated
 */
@ProviderType
public class TransactionEntryUtil {

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
	public static void clearCache(TransactionEntry transactionEntry) {
		getPersistence().clearCache(transactionEntry);
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
	public static Map<Serializable, TransactionEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<TransactionEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<TransactionEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<TransactionEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<TransactionEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static TransactionEntry update(TransactionEntry transactionEntry) {
		return getPersistence().update(transactionEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static TransactionEntry update(
		TransactionEntry transactionEntry, ServiceContext serviceContext) {

		return getPersistence().update(transactionEntry, serviceContext);
	}

	/**
	 * Caches the transaction entry in the entity cache if it is enabled.
	 *
	 * @param transactionEntry the transaction entry
	 */
	public static void cacheResult(TransactionEntry transactionEntry) {
		getPersistence().cacheResult(transactionEntry);
	}

	/**
	 * Caches the transaction entries in the entity cache if it is enabled.
	 *
	 * @param transactionEntries the transaction entries
	 */
	public static void cacheResult(List<TransactionEntry> transactionEntries) {
		getPersistence().cacheResult(transactionEntries);
	}

	/**
	 * Creates a new transaction entry with the primary key. Does not add the transaction entry to the database.
	 *
	 * @param transactionEntryId the primary key for the new transaction entry
	 * @return the new transaction entry
	 */
	public static TransactionEntry create(long transactionEntryId) {
		return getPersistence().create(transactionEntryId);
	}

	/**
	 * Removes the transaction entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry that was removed
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	public static TransactionEntry remove(long transactionEntryId)
		throws com.liferay.portal.transaction.test.exception.
			NoSuchTransactionEntryException {

		return getPersistence().remove(transactionEntryId);
	}

	public static TransactionEntry updateImpl(
		TransactionEntry transactionEntry) {

		return getPersistence().updateImpl(transactionEntry);
	}

	/**
	 * Returns the transaction entry with the primary key or throws a <code>NoSuchTransactionEntryException</code> if it could not be found.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry
	 * @throws NoSuchTransactionEntryException if a transaction entry with the primary key could not be found
	 */
	public static TransactionEntry findByPrimaryKey(long transactionEntryId)
		throws com.liferay.portal.transaction.test.exception.
			NoSuchTransactionEntryException {

		return getPersistence().findByPrimaryKey(transactionEntryId);
	}

	/**
	 * Returns the transaction entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry, or <code>null</code> if a transaction entry with the primary key could not be found
	 */
	public static TransactionEntry fetchByPrimaryKey(long transactionEntryId) {
		return getPersistence().fetchByPrimaryKey(transactionEntryId);
	}

	/**
	 * Returns all the transaction entries.
	 *
	 * @return the transaction entries
	 */
	public static List<TransactionEntry> findAll() {
		return getPersistence().findAll();
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
	public static List<TransactionEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
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
	public static List<TransactionEntry> findAll(
		int start, int end,
		OrderByComparator<TransactionEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
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
	public static List<TransactionEntry> findAll(
		int start, int end,
		OrderByComparator<TransactionEntry> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Removes all the transaction entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of transaction entries.
	 *
	 * @return the number of transaction entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static TransactionEntryPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<TransactionEntryPersistence, TransactionEntryPersistence>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			TransactionEntryPersistence.class);

		ServiceTracker<TransactionEntryPersistence, TransactionEntryPersistence>
			serviceTracker =
				new ServiceTracker
					<TransactionEntryPersistence, TransactionEntryPersistence>(
						bundle.getBundleContext(),
						TransactionEntryPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}