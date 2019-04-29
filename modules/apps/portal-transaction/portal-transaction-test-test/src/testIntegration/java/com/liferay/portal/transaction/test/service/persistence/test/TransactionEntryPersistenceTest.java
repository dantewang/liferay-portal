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

package com.liferay.portal.transaction.test.service.persistence.test;

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
import com.liferay.portal.transaction.test.exception.NoSuchTransactionEntryException;
import com.liferay.portal.transaction.test.model.TransactionEntry;
import com.liferay.portal.transaction.test.service.TransactionEntryLocalServiceUtil;
import com.liferay.portal.transaction.test.service.persistence.TransactionEntryPersistence;
import com.liferay.portal.transaction.test.service.persistence.TransactionEntryUtil;

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
public class TransactionEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.transaction.test.service"));

	@Before
	public void setUp() {
		_persistence = TransactionEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<TransactionEntry> iterator = _transactionEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TransactionEntry transactionEntry = _persistence.create(pk);

		Assert.assertNotNull(transactionEntry);

		Assert.assertEquals(transactionEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		TransactionEntry newTransactionEntry = addTransactionEntry();

		_persistence.remove(newTransactionEntry);

		TransactionEntry existingTransactionEntry =
			_persistence.fetchByPrimaryKey(newTransactionEntry.getPrimaryKey());

		Assert.assertNull(existingTransactionEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addTransactionEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TransactionEntry newTransactionEntry = _persistence.create(pk);

		newTransactionEntry.setValue(RandomTestUtil.randomString());

		_transactionEntries.add(_persistence.update(newTransactionEntry));

		TransactionEntry existingTransactionEntry =
			_persistence.findByPrimaryKey(newTransactionEntry.getPrimaryKey());

		Assert.assertEquals(
			existingTransactionEntry.getTransactionEntryId(),
			newTransactionEntry.getTransactionEntryId());
		Assert.assertEquals(
			existingTransactionEntry.getValue(),
			newTransactionEntry.getValue());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		TransactionEntry newTransactionEntry = addTransactionEntry();

		TransactionEntry existingTransactionEntry =
			_persistence.findByPrimaryKey(newTransactionEntry.getPrimaryKey());

		Assert.assertEquals(existingTransactionEntry, newTransactionEntry);
	}

	@Test(expected = NoSuchTransactionEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<TransactionEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"TransactionEntry", "transactionEntryId", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		TransactionEntry newTransactionEntry = addTransactionEntry();

		TransactionEntry existingTransactionEntry =
			_persistence.fetchByPrimaryKey(newTransactionEntry.getPrimaryKey());

		Assert.assertEquals(existingTransactionEntry, newTransactionEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TransactionEntry missingTransactionEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingTransactionEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		TransactionEntry newTransactionEntry1 = addTransactionEntry();
		TransactionEntry newTransactionEntry2 = addTransactionEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTransactionEntry1.getPrimaryKey());
		primaryKeys.add(newTransactionEntry2.getPrimaryKey());

		Map<Serializable, TransactionEntry> transactionEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, transactionEntries.size());
		Assert.assertEquals(
			newTransactionEntry1,
			transactionEntries.get(newTransactionEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newTransactionEntry2,
			transactionEntries.get(newTransactionEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, TransactionEntry> transactionEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(transactionEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		TransactionEntry newTransactionEntry = addTransactionEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTransactionEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, TransactionEntry> transactionEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, transactionEntries.size());
		Assert.assertEquals(
			newTransactionEntry,
			transactionEntries.get(newTransactionEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, TransactionEntry> transactionEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(transactionEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		TransactionEntry newTransactionEntry = addTransactionEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTransactionEntry.getPrimaryKey());

		Map<Serializable, TransactionEntry> transactionEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, transactionEntries.size());
		Assert.assertEquals(
			newTransactionEntry,
			transactionEntries.get(newTransactionEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			TransactionEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<TransactionEntry>() {

				@Override
				public void performAction(TransactionEntry transactionEntry) {
					Assert.assertNotNull(transactionEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		TransactionEntry newTransactionEntry = addTransactionEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TransactionEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"transactionEntryId",
				newTransactionEntry.getTransactionEntryId()));

		List<TransactionEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		TransactionEntry existingTransactionEntry = result.get(0);

		Assert.assertEquals(existingTransactionEntry, newTransactionEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TransactionEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"transactionEntryId", RandomTestUtil.nextLong()));

		List<TransactionEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		TransactionEntry newTransactionEntry = addTransactionEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TransactionEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("transactionEntryId"));

		Object newTransactionEntryId =
			newTransactionEntry.getTransactionEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"transactionEntryId", new Object[] {newTransactionEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingTransactionEntryId = result.get(0);

		Assert.assertEquals(existingTransactionEntryId, newTransactionEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TransactionEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("transactionEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"transactionEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected TransactionEntry addTransactionEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TransactionEntry transactionEntry = _persistence.create(pk);

		transactionEntry.setValue(RandomTestUtil.randomString());

		_transactionEntries.add(_persistence.update(transactionEntry));

		return transactionEntry;
	}

	private List<TransactionEntry> _transactionEntries =
		new ArrayList<TransactionEntry>();
	private TransactionEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}