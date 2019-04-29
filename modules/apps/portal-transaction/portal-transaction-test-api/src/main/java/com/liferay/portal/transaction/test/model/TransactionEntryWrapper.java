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

package com.liferay.portal.transaction.test.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link TransactionEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TransactionEntry
 * @generated
 */
@ProviderType
public class TransactionEntryWrapper
	extends BaseModelWrapper<TransactionEntry>
	implements TransactionEntry, ModelWrapper<TransactionEntry> {

	public TransactionEntryWrapper(TransactionEntry transactionEntry) {
		super(transactionEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("transactionEntryId", getTransactionEntryId());
		attributes.put("value", getValue());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long transactionEntryId = (Long)attributes.get("transactionEntryId");

		if (transactionEntryId != null) {
			setTransactionEntryId(transactionEntryId);
		}

		String value = (String)attributes.get("value");

		if (value != null) {
			setValue(value);
		}
	}

	/**
	 * Returns the primary key of this transaction entry.
	 *
	 * @return the primary key of this transaction entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the transaction entry ID of this transaction entry.
	 *
	 * @return the transaction entry ID of this transaction entry
	 */
	@Override
	public long getTransactionEntryId() {
		return model.getTransactionEntryId();
	}

	/**
	 * Returns the value of this transaction entry.
	 *
	 * @return the value of this transaction entry
	 */
	@Override
	public String getValue() {
		return model.getValue();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the primary key of this transaction entry.
	 *
	 * @param primaryKey the primary key of this transaction entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the transaction entry ID of this transaction entry.
	 *
	 * @param transactionEntryId the transaction entry ID of this transaction entry
	 */
	@Override
	public void setTransactionEntryId(long transactionEntryId) {
		model.setTransactionEntryId(transactionEntryId);
	}

	/**
	 * Sets the value of this transaction entry.
	 *
	 * @param value the value of this transaction entry
	 */
	@Override
	public void setValue(String value) {
		model.setValue(value);
	}

	@Override
	protected TransactionEntryWrapper wrap(TransactionEntry transactionEntry) {
		return new TransactionEntryWrapper(transactionEntry);
	}

}