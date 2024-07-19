/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.spring.hibernate.SpringHibernateThreadLocalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.LVEntry;
import com.liferay.portal.tools.service.builder.test.service.LVEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.LVEntryLocalServiceWrapper;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class IndexingTransactionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws PortalException {
		Bundle bundle = FrameworkUtil.getBundle(IndexingTransactionTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		AtomicInteger atomicInteger = new AtomicInteger(0);

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(
				ModelDocumentContributor.class,
				(ModelDocumentContributor<LVEntry>)(document, lvEntry) ->
					_lvEntryLocalService.getBigDecimalEntryPrimaryKeys(
						lvEntry.getLvEntryId()),
				HashMapDictionaryBuilder.put(
					"indexer.class.name",
					"com.liferay.portal.tools.service.builder.test.model." +
						"LVEntry"
				).build());

		ServiceRegistration<?> serviceRegistration2 =
			bundleContext.registerService(
				ServiceWrapper.class,
				new TestLVEntryLocalServiceWrapper(atomicInteger),
				new HashMapDictionary<>());

		try {
			_lvEntry = _lvEntryLocalService.create();

			_lvEntryLocalService.addLVEntryManuallyIndex(_lvEntry);

			_lvEntry.setGroupId(0);

			_lvEntryLocalService.updateLVEntry(_lvEntry);

			Assert.assertEquals(2, atomicInteger.get());
		}
		finally {
			serviceRegistration2.unregister();

			serviceRegistration1.unregister();
		}
	}

	@Inject
	private static LVEntryLocalService _lvEntryLocalService;

	@DeleteAfterTestRun
	private LVEntry _lvEntry;

	private static class TestLVEntryLocalServiceWrapper
		extends LVEntryLocalServiceWrapper {

		public TestLVEntryLocalServiceWrapper(AtomicInteger atomicInteger) {
			_atomicInteger = atomicInteger;
		}

		@Override
		public long[] getBigDecimalEntryPrimaryKeys(long lvEntryId) {
			_atomicInteger.incrementAndGet();

			Assert.assertFalse(
				SpringHibernateThreadLocalUtil.isCurrentTransactionReadOnly());

			return super.getBigDecimalEntryPrimaryKeys(lvEntryId);
		}

		private final AtomicInteger _atomicInteger;

	}

}