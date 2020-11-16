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

package com.liferay.portal.kernel.test.rule;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import org.junit.runner.Description;

/**
 * @author Leon Chi
 */
public class CompanyProviderMethodTestRule extends MethodTestRule<Long> {

	public static final CompanyProviderMethodTestRule INSTANCE =
		new CompanyProviderMethodTestRule();

	@Override
	protected void afterMethod(
		Description description, Long companyId, Object target) {

		CompanyThreadLocal.setCompanyId(companyId);
	}

	@Override
	protected Long beforeMethod(Description description, Object target)
		throws PortalException {

		Long companyId = CompanyThreadLocal.getCompanyId();

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());

		return companyId;
	}

	private CompanyProviderMethodTestRule() {
	}

}