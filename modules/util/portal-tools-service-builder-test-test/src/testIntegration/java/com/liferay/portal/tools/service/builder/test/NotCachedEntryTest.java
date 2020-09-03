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

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.NotCachedEntry;
import com.liferay.portal.tools.service.builder.test.service.NotCachedEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.NotCachedEntryPersistence;

import java.lang.reflect.InvocationHandler;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class NotCachedEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddEntry() {
		NotCachedEntry notCachedEntry =
			_notCachedEntryLocalService.addNotCachedEntry(1L, 2L);

		Assert.assertNull(
			"EntityCache should not have NotCachedEntry cached",
			_entityCache.getResult(
				_notCachedEntryLocalService.getEntityCacheClass(),
				notCachedEntry.getPrimaryKey()));

		List<NotCachedEntry> notCachedEntries =
			_notCachedEntryLocalService.getNotCachedEntriesByColumns(1L, 2L);

		Assert.assertEquals(
			notCachedEntries.toString(), 1, notCachedEntries.size());

		Assert.assertNull(
			"FinderCache should not have NotCachedEntry cached",
			_finderCache.getResult(
				ReflectionTestUtil.getFieldValue(
					_notCachedEntryPersistence, "_finderPathCountByC_C"),
				new Object[] {1L, 2L},
				(BasePersistenceImpl<NotCachedEntry>)
					_notCachedEntryPersistence));
	}

	@Test
	public void testDummyProxyInstance() {
		_testDummyProxyInstance("entityCache");
		_testDummyProxyInstance("finderCache");
	}

	private void _testDummyProxyInstance(String field) {
		Object instance = ReflectionTestUtil.getFieldValue(
			_notCachedEntryPersistence, field);

		Assert.assertTrue(
			"The " + field + "instance should be a proxy instance",
			ProxyUtil.isProxyClass(instance.getClass()));

		InvocationHandler invocationHandler = ProxyUtil.getInvocationHandler(
			instance);

		Class<?> clazz = invocationHandler.getClass();

		Assert.assertEquals(
			"The " + field + "instance should have a dummy InvocationHandler",
			"com.liferay.portal.kernel.util.ProxyFactory$" +
				"DummyInvocationHandler",
			clazz.getName());
	}

	@Inject
	private static NotCachedEntryPersistence _notCachedEntryPersistence;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private NotCachedEntryLocalService _notCachedEntryLocalService;

}