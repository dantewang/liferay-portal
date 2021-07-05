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

package com.liferay.portal.internal.change.tracking.hibernate;

import com.liferay.expando.kernel.model.adapter.StagedExpandoColumn;
import com.liferay.expando.kernel.model.adapter.StagedExpandoTable;
import com.liferay.portal.change.tracking.sql.CTSQLTransformer;
import com.liferay.portal.kernel.util.ServiceProxyFactory;
import com.liferay.portlet.expando.model.impl.ExpandoColumnImpl;
import com.liferay.portlet.expando.model.impl.ExpandoTableImpl;

import org.hibernate.EmptyInterceptor;

/**
 * @author Preston Crary
 */
public class CTSQLInterceptor extends EmptyInterceptor {

	@Override
	public String getEntityName(Object object) {
		if (object instanceof StagedExpandoTable) {
			return ExpandoTableImpl.class.getName();
		}
		else if (object instanceof StagedExpandoColumn) {
			return ExpandoColumnImpl.class.getName();
		}

		return super.getEntityName(object);
	}

	@Override
	public String onPrepareStatement(String sql) {
		if (_enabled) {
			return _ctSQLTransformer.transform(sql);
		}

		return sql;
	}

	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	private static volatile CTSQLTransformer _ctSQLTransformer =
		ServiceProxyFactory.newServiceTrackedInstance(
			CTSQLTransformer.class, CTSQLInterceptor.class, "_ctSQLTransformer",
			true);

	private boolean _enabled;

}