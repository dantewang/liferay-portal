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

/**
 * @author Lily Chi
 */
public class SampleSQLBuilderCompanyModel {

	public SampleSQLBuilderCompanyModel(
		long accountId, long companyId, String webId) {

		_accountId = accountId;
		_companyId = companyId;
		_webId = webId;
	}

	public long getAccountId() {
		return _accountId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public String getWebId() {
		return _webId;
	}

	private final long _accountId;
	private final long _companyId;
	private final String _webId;

}