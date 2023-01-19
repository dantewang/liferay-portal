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

package com.liferay.portal.dao.jdbc.aop;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.dao.jdbc.aop.DynamicDataSourceTargetSource;
import com.liferay.portal.kernel.dao.jdbc.aop.Operation;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionAttribute;
import com.liferay.portal.kernel.transaction.TransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionLifecycleManager;
import com.liferay.portal.kernel.transaction.TransactionStatus;

import java.util.Deque;
import java.util.LinkedList;

import javax.sql.DataSource;

import org.springframework.aop.TargetSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Michael Young
 */
public class DefaultDynamicDataSourceTargetSource
	implements DynamicDataSourceTargetSource, TargetSource {

	public void afterPropertiesSet() {
		_defaultDynamicDataSourceTargetSource = this;

		TransactionLifecycleManager.register(_TRANSACTION_LIFECYCLE_LISTENER);
	}

	@Override
	public Operation getOperation() {
		Deque<Operation> operations = _operations.get();

		Operation operation = operations.peek();

		if (operation == null) {
			operation = Operation.WRITE;
		}

		return operation;
	}

	@Override
	public DataSource getReadDataSource() {
		return _readDataSource;
	}

	@Override
	public Object getTarget() {
		Operation operationType = getOperation();

		if (operationType == Operation.READ) {
			if (_log.isTraceEnabled()) {
				_log.trace("Returning read data source");
			}

			return _readDataSource;
		}

		if (_log.isTraceEnabled()) {
			_log.trace("Returning write data source");
		}

		return _writeDataSource;
	}

	@Override
	public Class<DataSource> getTargetClass() {
		return DataSource.class;
	}

	@Override
	public DataSource getWriteDataSource() {
		return _writeDataSource;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public Operation popOperation() {
		Deque<Operation> operations = _operations.get();

		return operations.pop();
	}

	@Override
	public void pushOperation(Operation operation) {
		Deque<Operation> operations = _operations.get();

		operations.push(operation);
	}

	@Override
	public void releaseTarget(Object target) throws Exception {
	}

	@Override
	public void setReadDataSource(DataSource readDataSource) {
		_readDataSource = readDataSource;
	}

	@Override
	public void setWriteDataSource(DataSource writeDataSource) {
		_writeDataSource = writeDataSource;
	}

	private static final TransactionLifecycleListener
		_TRANSACTION_LIFECYCLE_LISTENER = new TransactionLifecycleListener() {

			@Override
			public void committed(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				_defaultDynamicDataSourceTargetSource.popOperation();
			}

			@Override
			public void created(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				_defaultDynamicDataSourceTargetSource.pushOperation(
					_getOperation(transactionAttribute, transactionStatus));
			}

			@Override
			public void rollbacked(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus, Throwable throwable) {

				_defaultDynamicDataSourceTargetSource.popOperation();
			}

			private Operation _getOperation(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				if (transactionAttribute.isMasterDataSource() ||
					!transactionAttribute.isReadOnly()) {

					return Operation.WRITE;
				}

				if (transactionStatus.isNewTransaction()) {
					return Operation.READ;
				}

				Propagation propagation = transactionAttribute.getPropagation();

				if (((propagation == Propagation.SUPPORTS) &&
					 !TransactionSynchronizationManager.
						 isActualTransactionActive()) ||
					(propagation == Propagation.NEVER) ||
					(propagation == Propagation.NOT_SUPPORTED)) {

					return Operation.READ;
				}

				return _defaultDynamicDataSourceTargetSource.getOperation();
			}

		};

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultDynamicDataSourceTargetSource.class);

	private static DefaultDynamicDataSourceTargetSource
		_defaultDynamicDataSourceTargetSource;
	private static final ThreadLocal<Deque<Operation>> _operations =
		new CentralizedThreadLocal<>(
			DefaultDynamicDataSourceTargetSource.class + "._operations",
			LinkedList::new);

	private DataSource _readDataSource;
	private DataSource _writeDataSource;

}