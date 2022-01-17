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

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.lang.reflect.Method;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Objects;

import org.hibernate.type.MaterializedClobType;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;

/**
 * @author Shuyang Zhou
 */
@SuppressWarnings("deprecation")
public class StringClobType extends MaterializedClobType {

	public StringClobType() {
		if (_dbType.equals(DBType.POSTGRESQL)) {
			setSqlTypeDescriptor(new ClobTypeDescriptorImpl());
		}

		setJavaTypeDescriptor(
			new StringTypeDescriptor() {

				@Override
				public boolean areEqual(String x, String y) {
					if (Objects.equals(x, y)) {
						return true;
					}
					else if (((x == null) || x.equals(StringPool.BLANK)) &&
							 ((y == null) || y.equals(StringPool.BLANK))) {

						return true;
					}

					return false;
				}

			});
	}

	private static final Log _log = LogFactoryUtil.getLog(StringClobType.class);

	private static final DBType _dbType;

	static {
		DB db = DBManagerUtil.getDB();

		_dbType = db.getDBType();
	}

	private class ClobTypeDescriptorImpl extends ClobTypeDescriptor {

		@Override
		public boolean canBeRemapped() {
			return false;
		}

		@Override
		public <X> BasicBinder<X> getClobBinder(
			JavaTypeDescriptor<X> javaTypeDescriptor) {

			try {
				Method method = ReflectionUtil.getDeclaredMethod(
					ClobTypeDescriptor.class, "getClobBinder",
					JavaTypeDescriptor.class);

				return (BasicBinder<X>)method.invoke(
					DEFAULT, javaTypeDescriptor);
			}
			catch (Exception exception) {
				_log.error(exception, exception);

				return null;
			}
		}

		@Override
		public <X> ValueExtractor<X> getExtractor(
			final JavaTypeDescriptor<X> javaTypeDescriptor) {

			return new BasicExtractor<X>(javaTypeDescriptor, this) {

				@Override
				protected X doExtract(
						CallableStatement statement, int index,
						WrapperOptions options)
					throws SQLException {

					return javaTypeDescriptor.wrap(
						statement.getClob(index), options);
				}

				@Override
				protected X doExtract(
						CallableStatement statement, String name,
						WrapperOptions options)
					throws SQLException {

					return javaTypeDescriptor.wrap(
						statement.getClob(name), options);
				}

				@Override
				protected X doExtract(
						ResultSet resultSet, String name,
						WrapperOptions options)
					throws SQLException {

					return javaTypeDescriptor.wrap(
						resultSet.getClob(name), options);
				}

			};
		}

	}

}