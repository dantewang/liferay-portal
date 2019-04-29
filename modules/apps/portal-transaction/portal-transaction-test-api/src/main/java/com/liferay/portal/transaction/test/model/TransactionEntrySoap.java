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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class TransactionEntrySoap implements Serializable {

	public static TransactionEntrySoap toSoapModel(TransactionEntry model) {
		TransactionEntrySoap soapModel = new TransactionEntrySoap();

		soapModel.setTransactionEntryId(model.getTransactionEntryId());
		soapModel.setValue(model.getValue());

		return soapModel;
	}

	public static TransactionEntrySoap[] toSoapModels(
		TransactionEntry[] models) {

		TransactionEntrySoap[] soapModels =
			new TransactionEntrySoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static TransactionEntrySoap[][] toSoapModels(
		TransactionEntry[][] models) {

		TransactionEntrySoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new TransactionEntrySoap[models.length][models[0].length];
		}
		else {
			soapModels = new TransactionEntrySoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static TransactionEntrySoap[] toSoapModels(
		List<TransactionEntry> models) {

		List<TransactionEntrySoap> soapModels =
			new ArrayList<TransactionEntrySoap>(models.size());

		for (TransactionEntry model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new TransactionEntrySoap[soapModels.size()]);
	}

	public TransactionEntrySoap() {
	}

	public long getPrimaryKey() {
		return _transactionEntryId;
	}

	public void setPrimaryKey(long pk) {
		setTransactionEntryId(pk);
	}

	public long getTransactionEntryId() {
		return _transactionEntryId;
	}

	public void setTransactionEntryId(long transactionEntryId) {
		_transactionEntryId = transactionEntryId;
	}

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		_value = value;
	}

	private long _transactionEntryId;
	private String _value;

}