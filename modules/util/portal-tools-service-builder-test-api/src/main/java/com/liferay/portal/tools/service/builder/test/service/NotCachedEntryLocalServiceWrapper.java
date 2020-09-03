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

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link NotCachedEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see NotCachedEntryLocalService
 * @generated
 */
public class NotCachedEntryLocalServiceWrapper
	implements NotCachedEntryLocalService,
			   ServiceWrapper<NotCachedEntryLocalService> {

	public NotCachedEntryLocalServiceWrapper(
		NotCachedEntryLocalService notCachedEntryLocalService) {

		_notCachedEntryLocalService = notCachedEntryLocalService;
	}

	/**
	 * Adds the not cached entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotCachedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notCachedEntry the not cached entry
	 * @return the not cached entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
		addNotCachedEntry(
			com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
				notCachedEntry) {

		return _notCachedEntryLocalService.addNotCachedEntry(notCachedEntry);
	}

	/**
	 * Creates a new not cached entry with the primary key. Does not add the not cached entry to the database.
	 *
	 * @param nestedSetsTreeEntryId the primary key for the new not cached entry
	 * @return the new not cached entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
		createNotCachedEntry(long nestedSetsTreeEntryId) {

		return _notCachedEntryLocalService.createNotCachedEntry(
			nestedSetsTreeEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notCachedEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the not cached entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotCachedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry that was removed
	 * @throws PortalException if a not cached entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
			deleteNotCachedEntry(long nestedSetsTreeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notCachedEntryLocalService.deleteNotCachedEntry(
			nestedSetsTreeEntryId);
	}

	/**
	 * Deletes the not cached entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotCachedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notCachedEntry the not cached entry
	 * @return the not cached entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
		deleteNotCachedEntry(
			com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
				notCachedEntry) {

		return _notCachedEntryLocalService.deleteNotCachedEntry(notCachedEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notCachedEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _notCachedEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _notCachedEntryLocalService.dynamicQuery();
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

		return _notCachedEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.NotCachedEntryModelImpl</code>.
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

		return _notCachedEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.NotCachedEntryModelImpl</code>.
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

		return _notCachedEntryLocalService.dynamicQuery(
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

		return _notCachedEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _notCachedEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
		fetchNotCachedEntry(long nestedSetsTreeEntryId) {

		return _notCachedEntryLocalService.fetchNotCachedEntry(
			nestedSetsTreeEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _notCachedEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _notCachedEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the not cached entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.NotCachedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of not cached entries
	 * @param end the upper bound of the range of not cached entries (not inclusive)
	 * @return the range of not cached entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.NotCachedEntry>
			getNotCachedEntries(int start, int end) {

		return _notCachedEntryLocalService.getNotCachedEntries(start, end);
	}

	/**
	 * Returns the number of not cached entries.
	 *
	 * @return the number of not cached entries
	 */
	@Override
	public int getNotCachedEntriesCount() {
		return _notCachedEntryLocalService.getNotCachedEntriesCount();
	}

	/**
	 * Returns the not cached entry with the primary key.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the not cached entry
	 * @return the not cached entry
	 * @throws PortalException if a not cached entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
			getNotCachedEntry(long nestedSetsTreeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notCachedEntryLocalService.getNotCachedEntry(
			nestedSetsTreeEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _notCachedEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _notCachedEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the not cached entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotCachedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notCachedEntry the not cached entry
	 * @return the not cached entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
		updateNotCachedEntry(
			com.liferay.portal.tools.service.builder.test.model.NotCachedEntry
				notCachedEntry) {

		return _notCachedEntryLocalService.updateNotCachedEntry(notCachedEntry);
	}

	@Override
	public NotCachedEntryLocalService getWrappedService() {
		return _notCachedEntryLocalService;
	}

	@Override
	public void setWrappedService(
		NotCachedEntryLocalService notCachedEntryLocalService) {

		_notCachedEntryLocalService = notCachedEntryLocalService;
	}

	private NotCachedEntryLocalService _notCachedEntryLocalService;

}