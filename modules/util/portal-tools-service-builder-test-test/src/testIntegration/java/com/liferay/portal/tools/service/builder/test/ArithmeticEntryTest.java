/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.ArithmeticEntry;
import com.liferay.portal.tools.service.builder.test.model.ArithmeticEntryTable;
import com.liferay.portal.tools.service.builder.test.service.persistence.ArithmeticEntryPersistence;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ArithmeticEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Test
	public void testDivideByDecimal() {
		_testDivide(
			arithmeticEntry -> {
				arithmeticEntry.setNumber1(3L);
				arithmeticEntry.setNumber2(2L);
			},
			Collections.singletonList(1.5));
	}

	@Test
	public void testDivideByNull() {
		_testDivide(
			arithmeticEntry -> arithmeticEntry.setNumber1(3L),
			Collections.singletonList(null));
	}

	@Test
	public void testDivideByZero() {
		_testDivide(
			arithmeticEntry -> {
				arithmeticEntry.setNumber1(3L);
				arithmeticEntry.setNumber2(0L);
			},
			Collections.singletonList(null));
	}

	private void _testDivide(
		Consumer<ArithmeticEntry> consumer, List<Double> expectResults) {

		_arithmeticEntry = _arithmeticEntryPersistence.create(
			RandomTestUtil.nextLong());

		consumer.accept(_arithmeticEntry);

		_arithmeticEntry = _arithmeticEntryPersistence.update(_arithmeticEntry);

		Assert.assertEquals(
			expectResults,
			_arithmeticEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.divide(
						ArithmeticEntryTable.INSTANCE.number1,
						ArithmeticEntryTable.INSTANCE.number2
					).as(
						"alias", Double.class
					)
				).from(
					ArithmeticEntryTable.INSTANCE
				)));
	}

	@DeleteAfterTestRun
	private ArithmeticEntry _arithmeticEntry;

	@Inject
	private ArithmeticEntryPersistence _arithmeticEntryPersistence;

}