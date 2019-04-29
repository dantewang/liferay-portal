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

package com.liferay.portal.transaction.test.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.transaction.test.model.TransactionEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing TransactionEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class TransactionEntryCacheModel
	implements CacheModel<TransactionEntry>, Externalizable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof TransactionEntryCacheModel)) {
			return false;
		}

		TransactionEntryCacheModel transactionEntryCacheModel =
			(TransactionEntryCacheModel)obj;

		if (transactionEntryId ==
				transactionEntryCacheModel.transactionEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, transactionEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{transactionEntryId=");
		sb.append(transactionEntryId);
		sb.append(", value=");
		sb.append(value);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public TransactionEntry toEntityModel() {
		TransactionEntryImpl transactionEntryImpl = new TransactionEntryImpl();

		transactionEntryImpl.setTransactionEntryId(transactionEntryId);

		if (value == null) {
			transactionEntryImpl.setValue("");
		}
		else {
			transactionEntryImpl.setValue(value);
		}

		transactionEntryImpl.resetOriginalValues();

		return transactionEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		transactionEntryId = objectInput.readLong();
		value = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(transactionEntryId);

		if (value == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(value);
		}
	}

	public long transactionEntryId;
	public String value;

}