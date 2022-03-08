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

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
			setSqlTypeDescriptor(_autoCommitAwareClobTypeDescriptor);
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

	private static final AutoCommitAwareClobTypeDescriptor
		_autoCommitAwareClobTypeDescriptor =
			new AutoCommitAwareClobTypeDescriptor();
	private static final Field _basicBinderSQLTypeDescriptorField;
	private static final DBType _dbType;
	private static final Method _doBindMethodWithIndex;
	private static final Method _doBindMethodWithName;

	static {
		DB db = DBManagerUtil.getDB();

		_dbType = db.getDBType();

		try {
			_basicBinderSQLTypeDescriptorField =
				ReflectionUtil.getDeclaredField(
					BasicBinder.class, "sqlDescriptor");

			_doBindMethodWithIndex = ReflectionUtil.getDeclaredMethod(
				BasicBinder.class, "doBind", PreparedStatement.class,
				Object.class, int.class, WrapperOptions.class);

			_doBindMethodWithName = ReflectionUtil.getDeclaredMethod(
				BasicBinder.class, "doBind", CallableStatement.class,
				Object.class, String.class, WrapperOptions.class);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private static class AutoCommitAwareClobTypeDescriptor
		extends ClobTypeDescriptor {

		@Override
		public boolean canBeRemapped() {

			// If this method returns true, this instance will be replaced with
			// the one provided by
			// PostgreSQL81Dialect#getSqlTypeDescriptorOverride

			return false;
		}

		@Override
		public <X> ValueExtractor<X> getExtractor(
			JavaTypeDescriptor<X> javaTypeDescriptor) {

			return new BasicExtractor<X>(javaTypeDescriptor, this) {

				@Override
				public X doExtract(
						CallableStatement callableStatement, int index,
						WrapperOptions wrapperOptions)
					throws SQLException {

					return _executeWithDisabledAutoCommit(
						callableStatement.getConnection(),
						() -> javaTypeDescriptor.wrap(
							callableStatement.getClob(index), wrapperOptions));
				}

				@Override
				public X doExtract(
						ResultSet resultSet, String name,
						WrapperOptions wrapperOptions)
					throws SQLException {

					Statement statement = resultSet.getStatement();

					return _executeWithDisabledAutoCommit(
						statement.getConnection(),
						() -> javaTypeDescriptor.wrap(
							resultSet.getClob(name), wrapperOptions));
				}

				@Override
				protected X doExtract(
						CallableStatement callableStatement, String name,
						WrapperOptions wrapperOptions)
					throws SQLException {

					return _executeWithDisabledAutoCommit(
						callableStatement.getConnection(),
						() -> javaTypeDescriptor.wrap(
							callableStatement.getClob(name), wrapperOptions));
				}

			};
		}

		@Override
		protected <X> BasicBinder<X> getClobBinder(
			JavaTypeDescriptor<X> javaTypeDescriptor) {

			BasicBinder<X> basicBinder = (BasicBinder<X>)DEFAULT.getBinder(
				javaTypeDescriptor);

			try {
				_basicBinderSQLTypeDescriptorField.set(basicBinder, this);
			}
			catch (IllegalAccessException illegalAccessException) {
				throw new RuntimeException(illegalAccessException);
			}

			return new BasicBinder<X>(javaTypeDescriptor, this) {

				@Override
				public void doBind(
						CallableStatement callableStatement, X value,
						String name, WrapperOptions wrapperOptions)
					throws SQLException {

					_executeWithDisabledAutoCommit(
						callableStatement.getConnection(),
						() -> {
							try {
								_doBindMethodWithName.invoke(
									basicBinder, callableStatement, value, name,
									wrapperOptions);
							}
							catch (ReflectiveOperationException
										reflectiveOperationException) {

								throw new SQLException(
									reflectiveOperationException);
							}

							return null;
						});
				}

				@Override
				public void doBind(
						PreparedStatement preparedStatement, X value, int index,
						WrapperOptions wrapperOptions)
					throws SQLException {

					_executeWithDisabledAutoCommit(
						preparedStatement.getConnection(),
						() -> {
							try {
								_doBindMethodWithIndex.invoke(
									basicBinder, preparedStatement, value,
									index, wrapperOptions);
							}
							catch (ReflectiveOperationException
										reflectiveOperationException) {

								throw new SQLException(
									reflectiveOperationException);
							}

							return null;
						});
				}

			};
		}

		private <X> X _executeWithDisabledAutoCommit(
				Connection connection,
				UnsafeSupplier<X, SQLException> unsafeSupplier)
			throws SQLException {

			boolean autoCommit = connection.getAutoCommit();

			try {
				if (autoCommit) {
					connection.setAutoCommit(false);
				}

				return unsafeSupplier.get();
			}
			finally {
				connection.setAutoCommit(autoCommit);
			}
		}

	}

}