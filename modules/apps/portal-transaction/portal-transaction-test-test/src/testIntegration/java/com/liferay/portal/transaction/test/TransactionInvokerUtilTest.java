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

package com.liferay.portal.transaction.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.transaction.test.model.TransactionEntry;
import com.liferay.portal.transaction.test.service.TransactionEntryLocalService;
import com.liferay.portal.transaction.test.service.persistence.TransactionEntryPersistence;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class TransactionInvokerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCommit() throws Throwable {
		final long transactionEntryId = CounterLocalServiceUtil.increment();
		final String transactionEntryValue = PwdGenerator.getPassword();

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						TransactionEntry transactionEntry =
							_transactionEntryPersistence.create(
								transactionEntryId);

						transactionEntry.setValue(transactionEntryValue);

						_transactionEntryPersistence.update(transactionEntry);

						return null;
					}

				});

			TransactionEntry transactionEntry =
				_transactionEntryLocalService.fetchTransactionEntry(
					transactionEntryId);

			Assert.assertNotNull(transactionEntry);
			Assert.assertEquals(
				transactionEntryValue, transactionEntry.getValue());
		}
		finally {
			_transactionEntryLocalService.deleteTransactionEntry(
				transactionEntryId);
		}
	}

	@Test
	public void testRollback() throws Throwable {
		final long transactionEntryId = CounterLocalServiceUtil.increment();
		final Exception exception = new Exception();

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						TransactionEntry transactionEntry =
							_transactionEntryPersistence.create(
								transactionEntryId);

						transactionEntry.setValue(PwdGenerator.getPassword());

						_transactionEntryPersistence.update(transactionEntry);

						throw exception;
					}

				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(exception, throwable);

			TransactionEntry transactionEntry =
				_transactionEntryLocalService.fetchTransactionEntry(
					transactionEntryId);

			Assert.assertNull(transactionEntry);
		}
		finally {
			try {
				_transactionEntryLocalService.deleteTransactionEntry(
					transactionEntryId);
			}
			catch (Exception e) {
			}
		}
	}

	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRED);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

	@Inject
	private TransactionEntryLocalService _transactionEntryLocalService;

	@Inject
	private TransactionEntryPersistence _transactionEntryPersistence;

}