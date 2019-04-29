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

package com.liferay.portal.transaction.test.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link TransactionEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see TransactionEntryLocalService
 * @generated
 */
@ProviderType
public class TransactionEntryLocalServiceWrapper
	implements TransactionEntryLocalService,
			   ServiceWrapper<TransactionEntryLocalService> {

	public TransactionEntryLocalServiceWrapper(
		TransactionEntryLocalService transactionEntryLocalService) {

		_transactionEntryLocalService = transactionEntryLocalService;
	}

	/**
	 * Adds the transaction entry to the database. Also notifies the appropriate model listeners.
	 *
	 * @param transactionEntry the transaction entry
	 * @return the transaction entry that was added
	 */
	@Override
	public com.liferay.portal.transaction.test.model.TransactionEntry
		addTransactionEntry(
			com.liferay.portal.transaction.test.model.TransactionEntry
				transactionEntry) {

		return _transactionEntryLocalService.addTransactionEntry(
			transactionEntry);
	}

	/**
	 * Creates a new transaction entry with the primary key. Does not add the transaction entry to the database.
	 *
	 * @param transactionEntryId the primary key for the new transaction entry
	 * @return the new transaction entry
	 */
	@Override
	public com.liferay.portal.transaction.test.model.TransactionEntry
		createTransactionEntry(long transactionEntryId) {

		return _transactionEntryLocalService.createTransactionEntry(
			transactionEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _transactionEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the transaction entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry that was removed
	 * @throws PortalException if a transaction entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.transaction.test.model.TransactionEntry
			deleteTransactionEntry(long transactionEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _transactionEntryLocalService.deleteTransactionEntry(
			transactionEntryId);
	}

	/**
	 * Deletes the transaction entry from the database. Also notifies the appropriate model listeners.
	 *
	 * @param transactionEntry the transaction entry
	 * @return the transaction entry that was removed
	 */
	@Override
	public com.liferay.portal.transaction.test.model.TransactionEntry
		deleteTransactionEntry(
			com.liferay.portal.transaction.test.model.TransactionEntry
				transactionEntry) {

		return _transactionEntryLocalService.deleteTransactionEntry(
			transactionEntry);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _transactionEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _transactionEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.portal.transaction.test.model.impl.TransactionEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _transactionEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.portal.transaction.test.model.impl.TransactionEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _transactionEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _transactionEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _transactionEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.transaction.test.model.TransactionEntry
		fetchTransactionEntry(long transactionEntryId) {

		return _transactionEntryLocalService.fetchTransactionEntry(
			transactionEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _transactionEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _transactionEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _transactionEntryLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _transactionEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a range of all the transaction entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.portal.transaction.test.model.impl.TransactionEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of transaction entries
	 * @param end the upper bound of the range of transaction entries (not inclusive)
	 * @return the range of transaction entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.transaction.test.model.TransactionEntry>
			getTransactionEntries(int start, int end) {

		return _transactionEntryLocalService.getTransactionEntries(start, end);
	}

	/**
	 * Returns the number of transaction entries.
	 *
	 * @return the number of transaction entries
	 */
	@Override
	public int getTransactionEntriesCount() {
		return _transactionEntryLocalService.getTransactionEntriesCount();
	}

	/**
	 * Returns the transaction entry with the primary key.
	 *
	 * @param transactionEntryId the primary key of the transaction entry
	 * @return the transaction entry
	 * @throws PortalException if a transaction entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.transaction.test.model.TransactionEntry
			getTransactionEntry(long transactionEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _transactionEntryLocalService.getTransactionEntry(
			transactionEntryId);
	}

	/**
	 * Updates the transaction entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param transactionEntry the transaction entry
	 * @return the transaction entry that was updated
	 */
	@Override
	public com.liferay.portal.transaction.test.model.TransactionEntry
		updateTransactionEntry(
			com.liferay.portal.transaction.test.model.TransactionEntry
				transactionEntry) {

		return _transactionEntryLocalService.updateTransactionEntry(
			transactionEntry);
	}

	@Override
	public TransactionEntryLocalService getWrappedService() {
		return _transactionEntryLocalService;
	}

	@Override
	public void setWrappedService(
		TransactionEntryLocalService transactionEntryLocalService) {

		_transactionEntryLocalService = transactionEntryLocalService;
	}

	private TransactionEntryLocalService _transactionEntryLocalService;

}