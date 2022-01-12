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

package com.liferay.portal.dao.db.test;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alberto Chaparro
 */
public abstract class BaseDBTestCase {

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		dbInspector = new DBInspector(_connection);

		_db = DBManagerUtil.getDB();

		_db.runSQL(
			StringBundler.concat(
				"create table ", TABLE_NAME, "(id LONG not null primary key, ",
				"notNilColumn VARCHAR(75) not null, nilColumn VARCHAR(75) ",
				"null)"));
	}

	@After
	public void tearDown() throws Exception {
		_db.runSQL("drop table " + TABLE_NAME);

		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testAlterColumnTypeAlterSize() throws Exception {
		_db.runSQL(_getAlterColumType("notNilColumn", "VARCHAR(200) not null"));

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME, "notNilColumn", "VARCHAR(200) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToNotNull() throws Exception {
		_db.runSQL(_getAlterColumType("nilColumn", "VARCHAR(75) not null"));

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME, "nilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToNull() throws Exception {
		_db.runSQL(_getAlterColumType("notNilColumn", "VARCHAR(75) null"));

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME, "notNilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testAlterColumnTypeNoChangesNotNull() throws Exception {
		_db.runSQL(_getAlterColumType("notNilColumn", "VARCHAR(75) not null"));

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME, "notNilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnTypeNoChangesNull() throws Exception {
		_db.runSQL(_getAlterColumType("nilColumn", "VARCHAR(75) null"));

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME, "nilColumn", "VARCHAR(75) null"));
	}

	protected static final String TABLE_NAME = "DBTest";

	protected DBInspector dbInspector;

	private String _getAlterColumType(String columnName, String newType) {
		return StringBundler.concat(
			"alter_column_type ", TABLE_NAME, StringPool.SPACE, columnName,
			StringPool.SPACE, newType);
	}

	private Connection _connection;
	private DB _db;

}