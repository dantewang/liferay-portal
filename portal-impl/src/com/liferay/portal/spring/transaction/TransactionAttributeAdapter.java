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

package com.liferay.portal.spring.transaction;

import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;

import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * @author Shuyang Zhou
 */
public class TransactionAttributeAdapter
	implements com.liferay.portal.kernel.transaction.TransactionAttribute,
			   TransactionAttribute {

	public TransactionAttributeAdapter(
		TransactionAttribute transactionAttribute) {

		this(transactionAttribute, false);
	}

	public TransactionAttributeAdapter(
		TransactionAttribute transactionAttribute, boolean strictReadOnly) {

		this(transactionAttribute, strictReadOnly, false);
	}

	public TransactionAttributeAdapter(
		TransactionAttribute transactionAttribute, boolean strictReadOnly,
		boolean masterDataSource) {

		_transactionAttribute = transactionAttribute;
		_strictReadOnly = strictReadOnly;
		_masterDataSource = masterDataSource;
	}

	@Override
	public Isolation getIsolation() {
		return Isolation.getIsolation(
			_transactionAttribute.getIsolationLevel());
	}

	@Override
	public int getIsolationLevel() {
		return _transactionAttribute.getIsolationLevel();
	}

	@Override
	public String getName() {
		return _transactionAttribute.getName();
	}

	@Override
	public Propagation getPropagation() {
		return Propagation.getPropagation(
			_transactionAttribute.getPropagationBehavior());
	}

	@Override
	public int getPropagationBehavior() {
		return _transactionAttribute.getPropagationBehavior();
	}

	@Override
	public String getQualifier() {
		return _transactionAttribute.getQualifier();
	}

	@Override
	public int getTimeout() {
		return _transactionAttribute.getTimeout();
	}

	@Override
	public boolean isMasterDataSource() {
		return _masterDataSource;
	}

	@Override
	public boolean isReadOnly() {
		return _transactionAttribute.isReadOnly();
	}

	@Override
	public boolean isStrictReadOnly() {
		return _strictReadOnly;
	}

	@Override
	public boolean rollbackOn(Throwable throwable) {
		return _transactionAttribute.rollbackOn(throwable);
	}

	private final boolean _masterDataSource;
	private final boolean _strictReadOnly;
	private final TransactionAttribute _transactionAttribute;

}