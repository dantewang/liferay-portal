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

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link NotCachedEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see NotCachedEntry
 * @generated
 */
public class NotCachedEntryWrapper
	extends BaseModelWrapper<NotCachedEntry>
	implements ModelWrapper<NotCachedEntry>, NotCachedEntry {

	public NotCachedEntryWrapper(NotCachedEntry notCachedEntry) {
		super(notCachedEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("nestedSetsTreeEntryId", getNestedSetsTreeEntryId());
		attributes.put("column1", getColumn1());
		attributes.put("column2", getColumn2());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long nestedSetsTreeEntryId = (Long)attributes.get(
			"nestedSetsTreeEntryId");

		if (nestedSetsTreeEntryId != null) {
			setNestedSetsTreeEntryId(nestedSetsTreeEntryId);
		}

		Long column1 = (Long)attributes.get("column1");

		if (column1 != null) {
			setColumn1(column1);
		}

		Long column2 = (Long)attributes.get("column2");

		if (column2 != null) {
			setColumn2(column2);
		}
	}

	/**
	 * Returns the column1 of this not cached entry.
	 *
	 * @return the column1 of this not cached entry
	 */
	@Override
	public long getColumn1() {
		return model.getColumn1();
	}

	/**
	 * Returns the column2 of this not cached entry.
	 *
	 * @return the column2 of this not cached entry
	 */
	@Override
	public long getColumn2() {
		return model.getColumn2();
	}

	/**
	 * Returns the nested sets tree entry ID of this not cached entry.
	 *
	 * @return the nested sets tree entry ID of this not cached entry
	 */
	@Override
	public long getNestedSetsTreeEntryId() {
		return model.getNestedSetsTreeEntryId();
	}

	/**
	 * Returns the primary key of this not cached entry.
	 *
	 * @return the primary key of this not cached entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Sets the column1 of this not cached entry.
	 *
	 * @param column1 the column1 of this not cached entry
	 */
	@Override
	public void setColumn1(long column1) {
		model.setColumn1(column1);
	}

	/**
	 * Sets the column2 of this not cached entry.
	 *
	 * @param column2 the column2 of this not cached entry
	 */
	@Override
	public void setColumn2(long column2) {
		model.setColumn2(column2);
	}

	/**
	 * Sets the nested sets tree entry ID of this not cached entry.
	 *
	 * @param nestedSetsTreeEntryId the nested sets tree entry ID of this not cached entry
	 */
	@Override
	public void setNestedSetsTreeEntryId(long nestedSetsTreeEntryId) {
		model.setNestedSetsTreeEntryId(nestedSetsTreeEntryId);
	}

	/**
	 * Sets the primary key of this not cached entry.
	 *
	 * @param primaryKey the primary key of this not cached entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	protected NotCachedEntryWrapper wrap(NotCachedEntry notCachedEntry) {
		return new NotCachedEntryWrapper(notCachedEntry);
	}

}