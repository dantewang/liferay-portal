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

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

/**
 * The extended model interface for the TransactionEntry service. Represents a row in the &quot;TransactionEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see TransactionEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.transaction.test.model.impl.TransactionEntryImpl"
)
@ProviderType
public interface TransactionEntry
	extends PersistedModel, TransactionEntryModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.transaction.test.model.impl.TransactionEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<TransactionEntry, Long>
		TRANSACTION_ENTRY_ID_ACCESSOR = new Accessor<TransactionEntry, Long>() {

			@Override
			public Long get(TransactionEntry transactionEntry) {
				return transactionEntry.getTransactionEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<TransactionEntry> getTypeClass() {
				return TransactionEntry.class;
			}

		};

}