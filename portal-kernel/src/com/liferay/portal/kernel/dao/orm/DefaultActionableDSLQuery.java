/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Dante Wang
 */
public class DefaultActionableDSLQuery implements ActionableDSLQuery {

	public static final TransactionConfig REQUIRES_NEW_TRANSACTION_CONFIG;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRES_NEW);
		builder.setRollbackForClasses(
			PortalException.class, SystemException.class);

		REQUIRES_NEW_TRANSACTION_CONFIG = builder.build();
	}

	@Override
	public void buildDSL(Consumer<DSLBuilder> consumer) {
		consumer.accept(new DSLBuilderImpl());
	}

	@Override
	public PerformActionMethod<?> getPerformActionMethod() {
		return _performActionMethod;
	}

	@Override
	public PerformCountMethod getPerformCountMethod() {
		return _performCountMethod;
	}

	@Override
	public boolean isParallel() {
		return _parallel;
	}

	@Override
	public void performActions() throws PortalException {
		try {
			long previousPrimaryKey = -1;

			while (true) {
				long lastPrimaryKey = doPerformActions(previousPrimaryKey);

				if (lastPrimaryKey < 0) {
					return;
				}

				intervalCompleted(previousPrimaryKey, lastPrimaryKey);

				previousPrimaryKey = lastPrimaryKey;
			}
		}
		finally {
			_offset = 0;

			actionsCompleted();
		}
	}

	@Override
	public long performCount() throws PortalException {
		if (_performCountMethod != null) {
			return _performCountMethod.performCount();
		}

		return (Long)executeDSLQuery(_dslQueryCountMethod, getDslQuery(null));
	}

	@Override
	public void setBaseLocalService(BaseLocalService baseLocalService) {
		_baseLocalService = baseLocalService;

		Class<?> clazz = _baseLocalService.getClass();

		try {
			_dslQueryMethod = clazz.getMethod("dslQuery", DSLQuery.class);
			_dslQueryCountMethod = clazz.getMethod(
				"dslQueryCount", DSLQuery.class);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new SystemException(noSuchMethodException);
		}
	}

	@Override
	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	@Override
	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setGroupIdPropertyName(String groupIdPropertyName) {
		_groupIdPropertyName = groupIdPropertyName;
	}

	@Override
	public void setInterval(int interval) {
		_interval = interval;
	}

	@Override
	public void setParallel(boolean parallel) {
		_parallel = parallel;
	}

	@Override
	public void setPerformActionMethod(
		PerformActionMethod<?> performActionMethod) {

		_performActionMethod = performActionMethod;
	}

	@Override
	public void setPerformCountMethod(PerformCountMethod performCountMethod) {
		_performCountMethod = performCountMethod;
	}

	@Override
	public void setPrimaryKeyPropertyName(String primaryKeyPropertyName) {
		_primaryKeyPropertyName = primaryKeyPropertyName;
	}

	@Override
	public void setTable(Table<?> table) {
		_table = table;
	}

	@Override
	public void setTransactionConfig(TransactionConfig transactionConfig) {
		_transactionConfig = transactionConfig;
	}

	protected void actionsCompleted() throws PortalException {
	}

	protected long doPerformActions(long previousPrimaryKey)
		throws PortalException {

		DSLQuery dslQuery = getDslQuery(
			predicate -> {
				if (_orderByFunction != null) {
					return predicate;
				}

				Predicate primaryKeyPredicate = _table.getColumn(
					_primaryKeyPropertyName, Long.class
				).gt(
					previousPrimaryKey
				);

				return primaryKeyPredicate.and(predicate);
			});

		Callable<Long> callable = () -> {
			List<Object> objects = (List<Object>)executeDSLQuery(
				_dslQueryMethod, dslQuery);

			_offset += objects.size();

			if (objects.isEmpty()) {
				return -1L;
			}

			PortalExecutorManager portalExecutorManager =
				_portalExecutorManagerSnapshot.get();

			ExecutorService executorService =
				portalExecutorManager.getPortalExecutor(
					DefaultActionableDSLQuery.class.getName());

			if (_parallel && (executorService != null)) {
				List<Future<Void>> futures = new ArrayList<>(objects.size());

				for (Object object : objects) {
					futures.add(
						executorService.submit(
							new CompanyInheritableThreadLocalCallable<>(
								() -> {
									performAction(object);

									return null;
								})));
				}

				for (Future<Void> future : futures) {
					future.get();
				}
			}
			else {
				for (Object object : objects) {
					performAction(object);
				}
			}

			if (objects.size() < _interval) {
				return -1L;
			}

			BaseModel<?> baseModel = (BaseModel<?>)objects.get(
				objects.size() - 1);

			return (Long)baseModel.getPrimaryKeyObj();
		};

		TransactionConfig transactionConfig = getTransactionConfig();

		try {
			if (transactionConfig == null) {
				return callable.call();
			}

			return TransactionInvokerUtil.invoke(transactionConfig, callable);
		}
		catch (Throwable throwable) {
			if (throwable instanceof PortalException) {
				throw (PortalException)throwable;
			}

			if (throwable instanceof SystemException) {
				throw (SystemException)throwable;
			}

			throw new SystemException(throwable);
		}
	}

	protected Object executeDSLQuery(
			Method dynamicQueryMethod, Object... arguments)
		throws PortalException {

		try {
			return dynamicQueryMethod.invoke(_baseLocalService, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			if (throwable instanceof PortalException) {
				throw (PortalException)throwable;
			}
			else if (throwable instanceof SystemException) {
				throw (SystemException)throwable;
			}

			throw new SystemException(invocationTargetException);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	protected long getCompanyId() {
		return _companyId;
	}

	protected DSLQuery getDslQuery(
		Function<Predicate, Predicate> customizePredicateFunction) {

		JoinStep joinStep = DSLQueryFactoryUtil.select(
		).from(
			_table
		);

		if (_joinFunction != null) {
			joinStep = _joinFunction.apply(joinStep);
		}

		Predicate predicate = getPredicate();

		if (customizePredicateFunction != null) {
			predicate = customizePredicateFunction.apply(predicate);
		}

		GroupByStep groupByStep = joinStep.where(predicate);

		OrderByStep orderByStep = groupByStep;

		if (_groupByFunction != null) {
			orderByStep = _groupByFunction.apply(groupByStep);
		}

		LimitStep limitStep = orderByStep.orderBy(getOrderByFunction());

		if (_orderByFunction == null) {
			return limitStep.limit(0, _interval);
		}

		return limitStep.limit(_offset, _interval + _offset);
	}

	protected int getInterval() {
		return _interval;
	}

	protected Function<OrderByStep, LimitStep> getOrderByFunction() {
		if (_orderByFunction != null) {
			return _orderByFunction;
		}

		return orderByStep -> orderByStep.orderBy(
			_table.getColumn(
				_primaryKeyPropertyName
			).ascending());
	}

	protected Predicate getPredicate() {
		Predicate defaultPredicate = null;

		if (_companyId > 0) {
			defaultPredicate = _table.getColumn(
				"companyId", Long.class
			).eq(
				_companyId
			);
		}

		if (_groupId > 0) {
			Predicate predicate = _table.getColumn(
				_groupIdPropertyName, Long.class
			).eq(
				_groupId
			);

			if (defaultPredicate != null) {
				defaultPredicate = defaultPredicate.and(predicate);
			}
			else {
				defaultPredicate = predicate;
			}
		}

		if (_predicate != null) {
			return _predicate.and(defaultPredicate);
		}

		return defaultPredicate;
	}

	protected TransactionConfig getTransactionConfig() {
		return _transactionConfig;
	}

	protected void intervalCompleted(long startPrimaryKey, long endPrimaryKey)
		throws PortalException {
	}

	protected void performAction(Object object) throws PortalException {
		if (_performActionMethod != null) {
			_performActionMethod.performAction(object);
		}
	}

	private static final Snapshot<PortalExecutorManager>
		_portalExecutorManagerSnapshot = new Snapshot<>(
			DefaultActionableDSLQuery.class, PortalExecutorManager.class);

	private BaseLocalService _baseLocalService;
	private long _companyId;
	private Method _dslQueryCountMethod;
	private Method _dslQueryMethod;
	private Function<GroupByStep, OrderByStep> _groupByFunction;
	private long _groupId;
	private String _groupIdPropertyName = "groupId";
	private int _interval = Indexer.DEFAULT_INTERVAL;
	private Function<JoinStep, JoinStep> _joinFunction;
	private int _offset;
	private Function<OrderByStep, LimitStep> _orderByFunction;
	private boolean _parallel;

	@SuppressWarnings("rawtypes")
	private PerformActionMethod _performActionMethod;

	private PerformCountMethod _performCountMethod;
	private Predicate _predicate;
	private String _primaryKeyPropertyName;
	private Table<?> _table;
	private TransactionConfig _transactionConfig;

	private class DSLBuilderImpl implements DSLBuilder {

		@Override
		public DSLBuilder groupByFunction(
			Function<GroupByStep, OrderByStep> function) {

			_groupByFunction = function;

			return this;
		}

		@Override
		public DSLBuilder joinFunction(Function<JoinStep, JoinStep> function) {
			_joinFunction = function;

			return this;
		}

		@Override
		public DSLBuilder orderByFunction(
			Function<OrderByStep, LimitStep> function) {

			_orderByFunction = function;

			return this;
		}

		@Override
		public DSLBuilder wherePredicate(Predicate predicate) {
			_predicate = predicate;

			return this;
		}

	}

}