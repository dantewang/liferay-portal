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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.portal.dao.db.PostgreSQLDB;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

/**
 * @author Dante Wang
 */
public class SamplePostgreSQLDB extends PostgreSQLDB {

	public SamplePostgreSQLDB(int majorVersion, int minorVersion) {
		super(majorVersion, minorVersion);
	}

	@Override
	public String buildSQL(String template) throws IOException {
		return StringUtil.replace(super.buildSQL(template), "\\n", "\n");
	}

}
