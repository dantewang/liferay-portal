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

package com.liferay.coverage;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Xiangyue Cai
 */
public class ConstantClassTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstantVariableClass() {
		ConstantClass constantClass = new ConstantClass();

		constantClass.toString(_TEST_VARIABLE);
	}

	private static final String _TEST_VARIABLE =
		ReflectionTestUtil.getFieldValue(ConstantClass.class, "_TEST_VARIABLE");

}