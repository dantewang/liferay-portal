/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.transaction.TransactionConfig;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Dante Wang
 */
public interface ActionableDSLQuery {

	public void buildDSL(Consumer<DSLBuilder> consumer);

	public PerformActionMethod<?> getPerformActionMethod();

	public PerformCountMethod getPerformCountMethod();

	public boolean isParallel();

	public void performActions() throws PortalException;

	public long performCount() throws PortalException;

	public void setBaseLocalService(BaseLocalService baseLocalService);

	public void setCompanyId(long companyId);

	public void setGroupId(long groupId);

	public void setGroupIdPropertyName(String groupIdPropertyName);

	public void setInterval(int interval);

	public void setParallel(boolean parallel);

	public void setPerformActionMethod(
		PerformActionMethod<?> performActionMethod);

	public void setPerformCountMethod(PerformCountMethod performCountMethod);

	public void setPrimaryKeyPropertyName(String primaryKeyPropertyName);

	public void setTable(Table<?> table);

	public void setTransactionConfig(TransactionConfig transactionConfig);

	public interface DSLBuilder {

		public DSLBuilder groupByFunction(
			Function<GroupByStep, OrderByStep> function);

		public DSLBuilder joinFunction(Function<JoinStep, JoinStep> function);

		public DSLBuilder orderByFunction(
			Function<OrderByStep, LimitStep> function);

		public DSLBuilder wherePredicate(Predicate predicate);

	}

	public interface PerformActionMethod<T> {

		public void performAction(T t) throws PortalException;

	}

	public interface PerformCountMethod {

		public long performCount() throws PortalException;

	}

}