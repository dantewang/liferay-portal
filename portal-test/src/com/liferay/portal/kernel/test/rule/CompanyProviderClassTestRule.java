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
import org.junit.runners.model.Statement;

/**
 * @author Cristina González
 */
public class CompanyProviderClassTestRule extends ClassTestRule<Long> {

	public static final CompanyProviderClassTestRule INSTANCE =
		new CompanyProviderClassTestRule();

	@Override
	protected void afterClass(Description description, Long previousCompanyId) {
		CompanyThreadLocal.setCompanyId(previousCompanyId);
	}

	@Override
	protected void afterMethod(Description description, Void v, Object target) {
		CompanyThreadLocal.setCompanyId(_companyId);
	}

	@Override
	protected Long beforeClass(Description description) throws PortalException {
		Long companyId = CompanyThreadLocal.getCompanyId();

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());

		return companyId;
	}

	@Override
	protected Void beforeMethod(Description description, Object target) {
		_companyId = CompanyThreadLocal.getCompanyId();

		return null;
	}

	@Override
	protected Statement createMethodStatement(
		Statement statement, Description description) {

		return new StatementWrapper(statement) {

			@Override
			public void evaluate() throws Throwable {
				Object target = inspectTarget(statement);

				Void m = beforeMethod(description, target);

				Throwable throwable1 = null;

				try {
					statement.evaluate();
				}
				catch (Throwable throwable2) {
					throwable1 = throwable2;
				}
				finally {
					try {
						afterMethod(description, m, target);
					}
					catch (Throwable throwable2) {
						if (throwable1 != null) {
							throwable2.addSuppressed(throwable1);
						}

						throwable1 = throwable2;
					}
				}

				if (throwable1 != null) {
					throw throwable1;
				}
			}

		};
	}

	private CompanyProviderClassTestRule() {
	}

	private Long _companyId;

}