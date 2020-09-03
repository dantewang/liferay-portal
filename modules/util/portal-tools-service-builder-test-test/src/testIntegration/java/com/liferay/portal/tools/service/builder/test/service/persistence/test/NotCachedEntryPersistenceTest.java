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

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchNotCachedEntryException;
import com.liferay.portal.tools.service.builder.test.model.NotCachedEntry;
import com.liferay.portal.tools.service.builder.test.service.NotCachedEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.NotCachedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.NotCachedEntryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class NotCachedEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = NotCachedEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NotCachedEntry> iterator = _notCachedEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotCachedEntry notCachedEntry = _persistence.create(pk);

		Assert.assertNotNull(notCachedEntry);

		Assert.assertEquals(notCachedEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NotCachedEntry newNotCachedEntry = addNotCachedEntry();

		_persistence.remove(newNotCachedEntry);

		NotCachedEntry existingNotCachedEntry = _persistence.fetchByPrimaryKey(
			newNotCachedEntry.getPrimaryKey());

		Assert.assertNull(existingNotCachedEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNotCachedEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotCachedEntry newNotCachedEntry = _persistence.create(pk);

		newNotCachedEntry.setColumn1(RandomTestUtil.nextLong());

		newNotCachedEntry.setColumn2(RandomTestUtil.nextLong());

		_notCachedEntries.add(_persistence.update(newNotCachedEntry));

		NotCachedEntry existingNotCachedEntry = _persistence.findByPrimaryKey(
			newNotCachedEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNotCachedEntry.getNestedSetsTreeEntryId(),
			newNotCachedEntry.getNestedSetsTreeEntryId());
		Assert.assertEquals(
			existingNotCachedEntry.getColumn1(),
			newNotCachedEntry.getColumn1());
		Assert.assertEquals(
			existingNotCachedEntry.getColumn2(),
			newNotCachedEntry.getColumn2());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NotCachedEntry newNotCachedEntry = addNotCachedEntry();

		NotCachedEntry existingNotCachedEntry = _persistence.findByPrimaryKey(
			newNotCachedEntry.getPrimaryKey());

		Assert.assertEquals(existingNotCachedEntry, newNotCachedEntry);
	}

	@Test(expected = NoSuchNotCachedEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NotCachedEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"NotCachedEntry", "nestedSetsTreeEntryId", true, "column1", true,
			"column2", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NotCachedEntry newNotCachedEntry = addNotCachedEntry();

		NotCachedEntry existingNotCachedEntry = _persistence.fetchByPrimaryKey(
			newNotCachedEntry.getPrimaryKey());

		Assert.assertEquals(existingNotCachedEntry, newNotCachedEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotCachedEntry missingNotCachedEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingNotCachedEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NotCachedEntry newNotCachedEntry1 = addNotCachedEntry();
		NotCachedEntry newNotCachedEntry2 = addNotCachedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotCachedEntry1.getPrimaryKey());
		primaryKeys.add(newNotCachedEntry2.getPrimaryKey());

		Map<Serializable, NotCachedEntry> notCachedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, notCachedEntries.size());
		Assert.assertEquals(
			newNotCachedEntry1,
			notCachedEntries.get(newNotCachedEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newNotCachedEntry2,
			notCachedEntries.get(newNotCachedEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NotCachedEntry> notCachedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notCachedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NotCachedEntry newNotCachedEntry = addNotCachedEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotCachedEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NotCachedEntry> notCachedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notCachedEntries.size());
		Assert.assertEquals(
			newNotCachedEntry,
			notCachedEntries.get(newNotCachedEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NotCachedEntry> notCachedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notCachedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NotCachedEntry newNotCachedEntry = addNotCachedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotCachedEntry.getPrimaryKey());

		Map<Serializable, NotCachedEntry> notCachedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notCachedEntries.size());
		Assert.assertEquals(
			newNotCachedEntry,
			notCachedEntries.get(newNotCachedEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			NotCachedEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<NotCachedEntry>() {

				@Override
				public void performAction(NotCachedEntry notCachedEntry) {
					Assert.assertNotNull(notCachedEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		NotCachedEntry newNotCachedEntry = addNotCachedEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			NotCachedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"nestedSetsTreeEntryId",
				newNotCachedEntry.getNestedSetsTreeEntryId()));

		List<NotCachedEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		NotCachedEntry existingNotCachedEntry = result.get(0);

		Assert.assertEquals(existingNotCachedEntry, newNotCachedEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			NotCachedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"nestedSetsTreeEntryId", RandomTestUtil.nextLong()));

		List<NotCachedEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		NotCachedEntry newNotCachedEntry = addNotCachedEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			NotCachedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("nestedSetsTreeEntryId"));

		Object newNestedSetsTreeEntryId =
			newNotCachedEntry.getNestedSetsTreeEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"nestedSetsTreeEntryId",
				new Object[] {newNestedSetsTreeEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingNestedSetsTreeEntryId = result.get(0);

		Assert.assertEquals(
			existingNestedSetsTreeEntryId, newNestedSetsTreeEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			NotCachedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("nestedSetsTreeEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"nestedSetsTreeEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected NotCachedEntry addNotCachedEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotCachedEntry notCachedEntry = _persistence.create(pk);

		notCachedEntry.setColumn1(RandomTestUtil.nextLong());

		notCachedEntry.setColumn2(RandomTestUtil.nextLong());

		_notCachedEntries.add(_persistence.update(notCachedEntry));

		return notCachedEntry;
	}

	private List<NotCachedEntry> _notCachedEntries =
		new ArrayList<NotCachedEntry>();
	private NotCachedEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}