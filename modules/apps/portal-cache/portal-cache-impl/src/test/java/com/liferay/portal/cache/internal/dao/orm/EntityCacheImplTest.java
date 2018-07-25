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

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.portal.cache.MVCCPortalCache;
import com.liferay.portal.cache.test.util.TestPortalCache;
import com.liferay.portal.cache.test.util.TestPortalCacheReplicator;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.commons.collections.map.LRUMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Tina Tian
 * @author Dante Wang
 */
public class EntityCacheImplTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		_classLoader = EntityCacheImplTest.class.getClassLoader();
		_nullModel = ReflectionTestUtil.getFieldValue(
			BasePersistenceImpl.class, "nullModel");

		_propsWithLocalCache = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(2));
		_propsWithoutLocalCache = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler());

		_testPortalCacheMultiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new TestPortalCacheMultiVMPoolInvocationHandler(_classLoader));
	}

	@Before
	public void setUp() {
		_entityCacheImpl = _getEntityCacheImpl(
			_testPortalCacheMultiVMPool, _propsWithoutLocalCache);
		_entityCacheImplWithLocalCache = _getEntityCacheImpl(
			_testPortalCacheMultiVMPool, _propsWithLocalCache);
	}

	@Test
	public void testActivate() {

		// Local cache is not enabled

		Assert.assertSame(
			_testPortalCacheMultiVMPool,
			ReflectionTestUtil.getFieldValue(_entityCacheImpl, "_multiVMPool"));
		Assert.assertSame(
			_propsWithoutLocalCache,
			ReflectionTestUtil.getFieldValue(_entityCacheImpl, "_props"));

		Assert.assertEquals(
			Boolean.valueOf(
				_propsWithoutLocalCache.get(
					PropsKeys.VALUE_OBJECT_ENTITY_BLOCKING_CACHE)),
			ReflectionTestUtil.getFieldValue(
				_entityCacheImpl, "_valueObjectEntityBlockingCacheEnabled"));
		Assert.assertEquals(
			Boolean.valueOf(
				_propsWithoutLocalCache.get(
					PropsKeys.VALUE_OBJECT_ENTITY_CACHE_ENABLED)),
			ReflectionTestUtil.getFieldValue(
				_entityCacheImpl, "_valueObjectEntityCacheEnabled"));
		Assert.assertEquals(
			Boolean.valueOf(
				_propsWithoutLocalCache.get(
					PropsKeys.VALUE_OBJECT_MVCC_ENTITY_CACHE_ENABLED)),
			ReflectionTestUtil.getFieldValue(
				_entityCacheImpl, "_valueObjectMVCCEntityCacheEnabled"));
		Assert.assertFalse(
			ReflectionTestUtil.getFieldValue(
				_entityCacheImpl, "_localCacheAvailable"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(_entityCacheImpl, "_localCache"));

		// Local cache is enabled

		Assert.assertTrue(
			ReflectionTestUtil.getFieldValue(
				_entityCacheImplWithLocalCache, "_localCacheAvailable"));

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			_entityCacheImplWithLocalCache, "_localCache");

		Assert.assertNotNull(localCache);

		LRUMap localCacheMap = localCache.get();

		Assert.assertNotNull(localCacheMap);
		Assert.assertEquals(
			(int)Integer.valueOf(
				_propsWithLocalCache.get(
					PropsKeys.VALUE_OBJECT_ENTITY_THREAD_LOCAL_CACHE_MAX_SIZE)),
			localCacheMap.maxSize());
	}

	@Test
	public void testClearCache() {
		try {
			_entityCacheImpl.clearCache();
			_entityCacheImpl.clearCache(FinderCacheImplTest.class);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		PortalCache entityCacheImplCache = _entityCacheImpl.getPortalCache(
			EntityCacheImpl.class);
		PortalCache entityCacheImplTestCache = _entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		entityCacheImplCache.put(_PRIMARY_KEY_1, _nullModel);
		entityCacheImplTestCache.put(_PRIMARY_KEY_2, _nullModel);

		// Test clearCache()

		_entityCacheImpl.clearCache();

		List<Serializable> entityCacheImplCacheKeys =
			entityCacheImplCache.getKeys();
		List<Serializable> entityCacheImplTestCacheKeys =
			entityCacheImplTestCache.getKeys();

		Assert.assertTrue(
			entityCacheImplCacheKeys.toString(),
			entityCacheImplCacheKeys.isEmpty());
		Assert.assertTrue(
			entityCacheImplTestCacheKeys.toString(),
			entityCacheImplTestCacheKeys.isEmpty());

		// Test clearCache(Class<?>)

		entityCacheImplCache.put(_PRIMARY_KEY_1, _nullModel);
		entityCacheImplTestCache.put(_PRIMARY_KEY_2, _nullModel);

		_entityCacheImpl.clearCache(EntityCacheImpl.class);

		entityCacheImplCacheKeys = entityCacheImplCache.getKeys();
		entityCacheImplTestCacheKeys = entityCacheImplTestCache.getKeys();

		Assert.assertTrue(
			entityCacheImplCacheKeys.toString(),
			entityCacheImplCacheKeys.isEmpty());
		Assert.assertFalse(
			entityCacheImplTestCacheKeys.toString(),
			entityCacheImplTestCacheKeys.isEmpty());
	}

	@Test
	public void testClearLocalCache() {
		_entityCacheImplWithLocalCache.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		_entityCacheImplWithLocalCache.clearLocalCache();

		_assertLocalCacheSize(_entityCacheImplWithLocalCache, 0);
	}

	@Test
	public void testDispose() {
		_entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		_entityCacheImpl.dispose();

		ConcurrentMap<String, PortalCache> portalCaches =
			ReflectionTestUtil.getFieldValue(_entityCacheImpl, "_portalCaches");

		Assert.assertTrue(portalCaches.isEmpty());
	}

	@Test
	public void testGetPortalCache() {
		PortalCache portalCache = _entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		String portalCacheName = portalCache.getPortalCacheName();

		Assert.assertTrue(
			portalCacheName,
			portalCacheName.endsWith(EntityCacheImplTest.class.getName()));

		// Test Internal Map

		PortalCache portalCache1 = _entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		Assert.assertSame(portalCache, portalCache1);

		// Test MVCC

		portalCache = _entityCacheImpl.getPortalCache(MVCCModel.class);

		Assert.assertTrue(portalCache instanceof MVCCPortalCache);

		portalCache = _entityCacheImpl.getPortalCache(EntityCacheImpl.class);

		Assert.assertFalse(portalCache instanceof MVCCPortalCache);

		_entityCacheImpl.removeCache(MVCCModel.class.getName());

		ReflectionTestUtil.setFieldValue(
			_entityCacheImpl, "_valueObjectMVCCEntityCacheEnabled", false);

		portalCache = _entityCacheImpl.getPortalCache(MVCCModel.class);

		Assert.assertFalse(portalCache instanceof MVCCPortalCache);
	}

	@Test
	public void testGetPortalCacheConcurrent() throws InterruptedException {
		ConcurrentMap<String, PortalCache> portalCaches =
			ReflectionTestUtil.getFieldValue(_entityCacheImpl, "_portalCaches");

		Assert.assertTrue(portalCaches.isEmpty());

		ConcurrentMapInvocationHandler concurrentMapInvocationHandler =
			new ConcurrentMapInvocationHandler(portalCaches);

		ConcurrentMap<String, PortalCache> proxyPortalCaches =
			(ConcurrentMap)ProxyUtil.newProxyInstance(
				_classLoader, new Class<?>[] {ConcurrentMap.class},
				concurrentMapInvocationHandler);

		ReflectionTestUtil.setFieldValue(
			_entityCacheImpl, "_portalCaches", proxyPortalCaches);

		concurrentMapInvocationHandler.block();

		Thread thread1 = new Thread(
			() -> _entityCacheImpl.getPortalCache(EntityCacheImplTest.class));

		thread1.start();

		concurrentMapInvocationHandler.waitUntilBlock(1);

		Thread thread2 = new Thread(
			() -> _entityCacheImpl.getPortalCache(EntityCacheImplTest.class));

		thread2.start();

		concurrentMapInvocationHandler.waitUntilBlock(2);

		concurrentMapInvocationHandler.unblock(2);

		thread1.join();
		thread2.join();

		Assert.assertEquals(
			"ConcurrentMap.putIfAbsent should be executed 2 times.", 2,
			concurrentMapInvocationHandler.getPutIfAbsentExecutionCount());
		Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());
	}

	@Test
	public void testGetRegistryName() {
		Assert.assertEquals(
			EntityCache.class.getName(), _entityCacheImpl.getRegistryName());
	}

	@Test
	public void testGetResult() {
		PortalCache portalCache = _entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		portalCache.put(_PRIMARY_KEY_1, _nullModel);

		Assert.assertSame(
			_nullModel,
			_entityCacheImpl.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1));
		Assert.assertNull(
			_entityCacheImpl.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_2));

		// Test shortcut when method parameter entityCacheEnabled is false

		Assert.assertNull(
			_entityCacheImpl.getResult(
				false, EntityCacheImplTest.class, _PRIMARY_KEY_1));

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			_entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			Assert.assertNull(
				_entityCacheImpl.getResult(
					true, EntityCacheImplTest.class, _PRIMARY_KEY_1));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			Assert.assertNull(
				_entityCacheImpl.getResult(
					true, EntityCacheImplTest.class, _PRIMARY_KEY_1));
		}
		finally {
			CacheRegistryUtil.setActive(true);
		}
	}

	@Test
	public void testGetResultWithLocalCache() {
		PortalCache portalCache = _entityCacheImplWithLocalCache.getPortalCache(
			EntityCacheImplTest.class);

		portalCache.put(_PRIMARY_KEY_1, _nullModel);

		_assertLocalCacheSize(_entityCacheImplWithLocalCache, 0);

		_entityCacheImplWithLocalCache.getResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		_assertLocalCacheSize(_entityCacheImplWithLocalCache, 1);

		Assert.assertSame(
			_nullModel,
			_entityCacheImplWithLocalCache.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1));
	}

	@Test
	public void testInit() {
		_entityCacheImpl.init();
	}

	@Test
	public void testInvalidate() {
		AtomicBoolean calledClearCache = new AtomicBoolean(false);

		EntityCacheImpl entityCacheImpl = new EntityCacheImpl() {

			@Override
			public void clearCache() {
				calledClearCache.set(true);
			}

		};

		entityCacheImpl.invalidate();

		Assert.assertTrue(calledClearCache.get());
	}

	@Test
	public void testLoadResult() {
		AtomicInteger sessionLoadCount = new AtomicInteger(0);

		SessionFactory sessionFactory = _getSessionFactory(sessionLoadCount);

		PortalCache portalCache = _entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		// Test shortcut when method parameter entityCacheEnabled is false

		Serializable result = _entityCacheImpl.loadResult(
			false, EntityCacheImplTest.class, _PRIMARY_KEY_1, sessionFactory);

		Assert.assertNotNull(result);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));
		Assert.assertEquals(1, sessionLoadCount.get());

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			_entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			result = _entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		Assert.assertNotNull(result);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));
		Assert.assertEquals(2, sessionLoadCount.get());

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			result = _entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);
		}
		finally {
			CacheRegistryUtil.setActive(true);
		}

		Assert.assertNotNull(result);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));
		Assert.assertEquals(3, sessionLoadCount.get());

		// Test normal load

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					EntityCacheImpl.class.getName(), Level.OFF)) {

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			result = _entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);

			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());
			Assert.assertNotNull(result);
			Assert.assertSame(result, portalCache.get(_PRIMARY_KEY_1));
			Assert.assertEquals(4, sessionLoadCount.get());

			captureHandler.resetLogLevel(Level.ALL);

			portalCache.removeAll();

			// Assert logger output

			_entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
			Assert.assertEquals(5, sessionLoadCount.get());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				"Load class com.liferay.portal.cache.internal.dao.orm." +
					"EntityCacheImplTest " + _PRIMARY_KEY_1 + " from session",
				logRecord.getMessage());

			// Test load the same model again

			_entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);

			Assert.assertEquals(5, sessionLoadCount.get());
		}

		result = _entityCacheImpl.loadResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_2, sessionFactory);

		Assert.assertNull(result);
	}

	@Test
	public void testLoadResultWithLocalCache() {
		AtomicInteger sessionLoadCount = new AtomicInteger(0);

		SessionFactory sessionFactory = _getSessionFactory(sessionLoadCount);

		_entityCacheImplWithLocalCache.loadResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, sessionFactory);

		Assert.assertEquals(1, sessionLoadCount.get());

		_entityCacheImplWithLocalCache.loadResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, sessionFactory);

		Assert.assertEquals(1, sessionLoadCount.get());
	}

	@Test
	public void testLocalCacheKey() {
		List<Object> localCacheKeys = new ArrayList<>(2);

		LRUMap testLRUMap = new LRUMap() {

			@Override
			public Object put(Object key, Object value) {
				localCacheKeys.add(key);

				return super.put(key, value);
			}

		};

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			_entityCacheImplWithLocalCache, "_localCache");

		localCache.set(testLRUMap);

		_entityCacheImplWithLocalCache.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		_entityCacheImplWithLocalCache.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		Object key1 = localCacheKeys.get(0);
		Object key2 = localCacheKeys.get(1);

		Assert.assertNotSame(key1, key2);
		Assert.assertEquals(key1, key2);
		Assert.assertEquals(key1.hashCode(), key2.hashCode());

		_entityCacheImplWithLocalCache.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_2, _nullModel);

		Object key3 = localCacheKeys.get(2);

		Assert.assertNotEquals(key1, key3);
		Assert.assertNotEquals(key1.hashCode(), key3.hashCode());

		_entityCacheImplWithLocalCache.putResult(
			true, EntityCacheImpl.class, _PRIMARY_KEY_2, _nullModel);

		Object key4 = localCacheKeys.get(3);

		Assert.assertNotEquals(key1, key4);
		Assert.assertNotEquals(key1.hashCode(), key4.hashCode());
	}

	@Test
	public void testNotifyPortalCacheAdded() {
		_entityCacheImpl.notifyPortalCacheAdded(null);
	}

	@Test
	public void testPutAndGetNullModel() throws Exception {
		_testPutAndGetNullModel(false);
		_testPutAndGetNullModel(true);
	}

	@Test
	public void testPutResult() {
		PortalCache portalCache = _entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		// Test shortcut when method parameter entityCacheEnabled is false

		_entityCacheImpl.putResult(
			false, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			_entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			_entityCacheImpl.putResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			_entityCacheImpl.putResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);
		}
		finally {
			CacheRegistryUtil.setActive(true);
		}

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));

		// Test shortcut when result is null

		_entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, null);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));

		// Test put without replicator

		TestPortalCacheReplicator testPortalCacheReplicator =
			new TestPortalCacheReplicator();

		portalCache.registerPortalCacheListener(testPortalCacheReplicator);

		_entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		testPortalCacheReplicator.assertActionsCount(0);

		// Test put with replicator

		_entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_2, _nullModel, false);

		testPortalCacheReplicator.assertPut(_PRIMARY_KEY_2, _nullModel);
	}

	@Test
	public void testPutResultWithLocalCache() {
		_entityCacheImplWithLocalCache.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			_entityCacheImplWithLocalCache, "_localCache");

		LRUMap localCacheMap = localCache.get();

		Assert.assertEquals(localCacheMap.toString(), 1, localCacheMap.size());

		Set<Map.Entry> entries = localCacheMap.entrySet();

		Iterator<Map.Entry> itr = entries.iterator();

		Map.Entry entry = itr.next();

		Assert.assertSame(_nullModel, entry.getValue());
	}

	@Test
	public void testRemoveCache() {
		AtomicBoolean calledRemovePortalCache = new AtomicBoolean(false);

		InvocationHandler invocationHandler =
			new TestPortalCacheMultiVMPoolInvocationHandler(_classLoader) {

				@Override
				public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

					String methodName = method.getName();

					if (methodName.equals("removePortalCache")) {
						calledRemovePortalCache.set(true);
					}

					return super.invoke(proxy, method, args);
				}

			};

		MultiVMPool multiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			invocationHandler);

		EntityCacheImpl entityCacheImpl = _getEntityCacheImpl(
			multiVMPool, _propsWithoutLocalCache);

		entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		entityCacheImpl.removeCache(EntityCacheImplTest.class.getName());

		ConcurrentMap<String, PortalCache> portalCaches =
			ReflectionTestUtil.getFieldValue(entityCacheImpl, "_portalCaches");

		Assert.assertNull(
			portalCaches.get(EntityCacheImplTest.class.getName()));

		Assert.assertTrue(calledRemovePortalCache.get());
	}

	@Test
	public void testRemoveResult() {
		_entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		// Test shortcut when method parameter entityCacheEnabled is false

		_entityCacheImpl.removeResult(
			false, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Assert.assertNotNull(
			_entityCacheImpl.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1));

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			_entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			_entityCacheImpl.removeResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		Assert.assertNotNull(
			_entityCacheImpl.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1));

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			_entityCacheImpl.removeResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1);
		}
		finally {
			CacheRegistryUtil.setActive(true);
		}

		Assert.assertNotNull(
			_entityCacheImpl.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1));

		// Test remove

		_entityCacheImpl.removeResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Assert.assertNull(
			_entityCacheImpl.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1));
	}

	@Test
	public void testRemoveResultWithLocalCache() {
		_entityCacheImplWithLocalCache = _getEntityCacheImpl(
			_testPortalCacheMultiVMPool, _propsWithLocalCache);

		_entityCacheImplWithLocalCache.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		_assertLocalCacheSize(_entityCacheImplWithLocalCache, 1);

		_entityCacheImplWithLocalCache.removeResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		_assertLocalCacheSize(_entityCacheImplWithLocalCache, 0);
	}

	private void _assertLocalCacheSize(
		EntityCacheImpl entityCacheImpl, int size) {

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			entityCacheImpl, "_localCache");

		LRUMap localCacheMap = localCache.get();

		Assert.assertEquals(size, localCacheMap.size());
	}

	private EntityCacheImpl _getEntityCacheImpl(
		MultiVMPool multiVMPool, Props props) {

		EntityCacheImpl entityCacheImpl = new EntityCacheImpl();

		entityCacheImpl.setMultiVMPool(multiVMPool);

		entityCacheImpl.setProps(props);

		entityCacheImpl.activate();

		return entityCacheImpl;
	}

	private SessionFactory _getSessionFactory(AtomicInteger count) {
		InvocationHandler sessionInvocationHandler = (proxy, method, args) -> {
			String methodName = method.getName();

			if (methodName.equals("load")) {
				count.incrementAndGet();

				if (args[1].equals(_PRIMARY_KEY_2)) {
					return null;
				}

				return _nullModel;
			}

			return null;
		};

		InvocationHandler sessionFactoryInvocationHandler =
			(proxy, method, args) -> {
				String methodName = method.getName();

				if (methodName.equals("openSession")) {
					return ProxyUtil.newProxyInstance(
						_classLoader, new Class<?>[] {Session.class},
						sessionInvocationHandler);
				}

				return null;
			};

		return (SessionFactory)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {SessionFactory.class},
			sessionFactoryInvocationHandler);
	}

	private void _testPutAndGetNullModel(boolean serialized) {
		MultiVMPool multiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, serialized));

		EntityCacheImpl entityCacheImpl = _getEntityCacheImpl(
			multiVMPool, _propsWithoutLocalCache);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		Serializable result = entityCacheImpl.getResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Assert.assertSame(_nullModel, result);
	}

	private static final int _PRIMARY_KEY_1 = 12345;

	private static final int _PRIMARY_KEY_2 = 67890;

	private static ClassLoader _classLoader;
	private static Serializable _nullModel;
	private static Props _propsWithLocalCache;
	private static Props _propsWithoutLocalCache;
	private static MultiVMPool _testPortalCacheMultiVMPool;

	private EntityCacheImpl _entityCacheImpl;
	private EntityCacheImpl _entityCacheImplWithLocalCache;

	private static class TestPortalCacheMultiVMPoolInvocationHandler
		extends MultiVMPoolInvocationHandler {

		public TestPortalCacheMultiVMPoolInvocationHandler(
			ClassLoader classLoader) {

			super(classLoader, false);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("getPortalCache")) {
				String portalCacheName = (String)args[0];

				if (args.length == 3) {
					boolean mvcc = (boolean)args[2];

					if (mvcc) {
						return new MVCCPortalCache(
							new TestPortalCache(portalCacheName));
					}
				}

				return new TestPortalCache(portalCacheName);
			}

			return super.invoke(proxy, method, args);
		}

	}

}