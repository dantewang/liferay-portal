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
			new PropsInvocationHandler() {

				@Override
				public Object invoke(
					Object proxy, Method method, Object[] args) {

					String methodName = method.getName();

					if (methodName.equals("get")) {
						String key = (String)args[0];

						if (PropsKeys.
								VALUE_OBJECT_ENTITY_THREAD_LOCAL_CACHE_MAX_SIZE.
									equals(key)) {

							return "2";
						}
					}

					return super.invoke(proxy, method, args);
				}

			});

		_props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler());

		_serializedMultiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, true));
		_notSerializedMultiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, false));
	}

	@Test
	public void testActivate() {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		// Local cache is not enabled

		Assert.assertSame(
			_serializedMultiVMPool,
			ReflectionTestUtil.getFieldValue(entityCacheImpl, "_multiVMPool"));
		Assert.assertSame(
			_props,
			ReflectionTestUtil.getFieldValue(entityCacheImpl, "_props"));

		Assert.assertEquals(
			Boolean.valueOf(
				_props.get(PropsKeys.VALUE_OBJECT_ENTITY_BLOCKING_CACHE)),
			ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_valueObjectEntityBlockingCacheEnabled"));
		Assert.assertEquals(
			Boolean.valueOf(
				_props.get(PropsKeys.VALUE_OBJECT_ENTITY_CACHE_ENABLED)),
			ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_valueObjectEntityCacheEnabled"));
		Assert.assertEquals(
			Boolean.valueOf(
				_props.get(PropsKeys.VALUE_OBJECT_MVCC_ENTITY_CACHE_ENABLED)),
			ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_valueObjectMVCCEntityCacheEnabled"));
		Assert.assertFalse(
			ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_localCacheAvailable"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(entityCacheImpl, "_localCache"));

		// Local cache is enabled

		entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _propsWithLocalCache);

		Assert.assertTrue(
			ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_localCacheAvailable"));

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			entityCacheImpl, "_localCache");

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
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		// Test clearCache() when there's no cache

		entityCacheImpl.clearCache();

		// Test clearCache(Class<?>) when there's no cache for the class

		entityCacheImpl.clearCache(EntityCacheImpl.class);

		ConcurrentMap<String, PortalCache<Serializable, Serializable>>
			portalCaches = ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_portalCaches");

		Assert.assertNotNull(portalCaches.get(EntityCacheImpl.class.getName()));

		// Test clearCache()

		PortalCache<Serializable, Serializable> entityCacheImplCache =
			entityCacheImpl.getPortalCache(EntityCacheImpl.class);
		PortalCache<Serializable, Serializable> entityCacheImplTestCache =
			entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		entityCacheImplCache.put(_PRIMARY_KEY_1, _nullModel);
		entityCacheImplTestCache.put(_PRIMARY_KEY_2, _nullModel);

		entityCacheImpl.clearCache();

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

		entityCacheImpl.clearCache(EntityCacheImpl.class);

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
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _propsWithLocalCache);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		entityCacheImpl.clearLocalCache();

		_assertLocalCacheSize(entityCacheImpl, 0);
	}

	@Test
	public void testDispose() {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		entityCacheImpl.dispose();

		ConcurrentMap<String, PortalCache<Serializable, Serializable>>
			portalCaches = ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_portalCaches");

		Assert.assertTrue(portalCaches.isEmpty());
	}

	@Test
	public void testGetPortalCache() {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		PortalCache<Serializable, Serializable> portalCache =
			entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		String prefix = ReflectionTestUtil.getFieldValue(
			entityCacheImpl, "_GROUP_KEY_PREFIX");

		String portalCacheName = portalCache.getPortalCacheName();

		Assert.assertNotNull(portalCacheName);
		Assert.assertEquals(
			prefix.concat(EntityCacheImplTest.class.getName()),
			portalCacheName);

		// Test Internal Map

		PortalCache<Serializable, Serializable> anotherPortalCache =
			entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		Assert.assertSame(portalCache, anotherPortalCache);

		// Test MVCC

		portalCache = entityCacheImpl.getPortalCache(MVCCModel.class);

		Assert.assertTrue(portalCache instanceof MVCCPortalCache);

		portalCache = entityCacheImpl.getPortalCache(EntityCacheImpl.class);

		Assert.assertFalse(portalCache instanceof MVCCPortalCache);

		entityCacheImpl.removeCache(MVCCModel.class.getName());

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_valueObjectMVCCEntityCacheEnabled", false);

		portalCache = entityCacheImpl.getPortalCache(MVCCModel.class);

		Assert.assertFalse(portalCache instanceof MVCCPortalCache);
	}

	@Test
	public void testGetPortalCacheConcurrent() throws InterruptedException {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		ConcurrentMap<String, PortalCache<Serializable, Serializable>>
			portalCaches = ReflectionTestUtil.getFieldValue(
				entityCacheImpl, "_portalCaches");

		Assert.assertTrue(portalCaches.isEmpty());

		ConcurrentMapInvocationHandler concurrentMapInvocationHandler =
			new ConcurrentMapInvocationHandler(portalCaches);

		ConcurrentMap<String, PortalCache<Serializable, Serializable>>
			proxyPortalCaches = (ConcurrentMap)ProxyUtil.newProxyInstance(
				_classLoader, new Class<?>[] {ConcurrentMap.class},
				concurrentMapInvocationHandler);

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_portalCaches", proxyPortalCaches);

		concurrentMapInvocationHandler.block();

		Thread thread1 = new Thread(
			() -> entityCacheImpl.getPortalCache(EntityCacheImplTest.class));

		thread1.start();

		concurrentMapInvocationHandler.waitUntilBlock(1);

		Thread thread2 = new Thread(
			() -> entityCacheImpl.getPortalCache(EntityCacheImplTest.class));

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
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		Assert.assertEquals(
			EntityCache.class.getName(), entityCacheImpl.getRegistryName());
	}

	@Test
	public void testInit() {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		entityCacheImpl.init();
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
		_testLoadResult(_notSerializedMultiVMPool);
		_testLoadResult(_serializedMultiVMPool);
	}

	@Test
	public void testLoadResultWithLocalCache() {
		_testLoadResultWithLocalCache(_notSerializedMultiVMPool);
		_testLoadResultWithLocalCache(_serializedMultiVMPool);
	}

	@Test
	public void testLocalCacheKey() {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _propsWithLocalCache);

		List<Object> localCacheKeys = new ArrayList<>(2);

		LRUMap testLRUMap = new LRUMap() {

			@Override
			public Object put(Object key, Object value) {
				localCacheKeys.add(key);

				return super.put(key, value);
			}

		};

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			entityCacheImpl, "_localCache");

		localCache.set(testLRUMap);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		Object key1 = localCacheKeys.get(0);
		Object key2 = localCacheKeys.get(1);

		Assert.assertNotSame(key1, key2);
		Assert.assertEquals(key1, key2);
		Assert.assertEquals(key1.hashCode(), key2.hashCode());

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_2, _nullModel);

		Object key3 = localCacheKeys.get(2);

		Assert.assertNotEquals(key1, key3);
		Assert.assertNotEquals(key1.hashCode(), key3.hashCode());

		entityCacheImpl.putResult(
			true, EntityCacheImpl.class, _PRIMARY_KEY_2, _nullModel);

		Object key4 = localCacheKeys.get(3);

		Assert.assertNotEquals(key1, key4);
		Assert.assertNotEquals(key1.hashCode(), key4.hashCode());
	}

	@Test
	public void testNotifyPortalCacheAdded() {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		entityCacheImpl.notifyPortalCacheAdded(null);
	}

	@Test
	public void testNotifyPortalCacheRemovedPortalCacheName() {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			_serializedMultiVMPool, _props);

		PortalCache<?, ?> portalCache = entityCacheImpl.getPortalCache(
			EntityCacheImplTest.class);

		Map<String, PortalCache<Serializable, Serializable>> portalCaches =
			ReflectionTestUtil.getFieldValue(entityCacheImpl, "_portalCaches");

		Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());
		Assert.assertSame(
			portalCache, portalCaches.get(EntityCacheImplTest.class.getName()));

		entityCacheImpl.notifyPortalCacheRemoved(
			EntityCacheImplTest.class.getName());

		Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());
		Assert.assertSame(
			portalCache, portalCaches.get(EntityCacheImplTest.class.getName()));

		entityCacheImpl.notifyPortalCacheRemoved(
			portalCache.getPortalCacheName());

		Assert.assertTrue(portalCaches.toString(), portalCaches.isEmpty());
	}

	@Test
	public void testPutAndGetNullModel() throws Exception {
		_testPutAndGetNullModel(_notSerializedMultiVMPool);
		_testPutAndGetNullModel(_serializedMultiVMPool);
	}

	@Test
	public void testPutAndGetWithLocalCache() {
		_testPutAndGetWithLocalCache(_notSerializedMultiVMPool);
		_testPutAndGetWithLocalCache(_serializedMultiVMPool);
	}

	@Test
	public void testPutAndRemoveNullModel() {
		_testPutAndRemoveNullModel(_notSerializedMultiVMPool);
		_testPutAndRemoveNullModel(_serializedMultiVMPool);
	}

	@Test
	public void testPutAndRemoveWithLocalCache() {
		_testPutAndRemoveWithLocalCache(_notSerializedMultiVMPool);
		_testPutAndRemoveWithLocalCache(_serializedMultiVMPool);
	}

	@Test
	public void testPutResultWithReplicator() {

		// Test replicator

		InvocationHandler invocationHandler = new MultiVMPoolInvocationHandler(
			_classLoader, true) {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

				String methodName = method.getName();

				if (methodName.equals("getPortalCache") && (args != null) &&
					(args.length > 0) && (args[0] instanceof String)) {

					return new TestPortalCache<>((String)args[0]);
				}

				return super.invoke(proxy, method, args);
			}

		};

		MultiVMPool multiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			invocationHandler);

		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _props);

		PortalCache<Serializable, Serializable> portalCache =
			entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		// Test put without replicator

		TestPortalCacheReplicator<Serializable, Serializable>
			testPortalCacheReplicator = new TestPortalCacheReplicator();

		portalCache.registerPortalCacheListener(testPortalCacheReplicator);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		testPortalCacheReplicator.assertActionsCount(0);

		// Test put with replicator

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_2, _nullModel, false);

		testPortalCacheReplicator.assertPut(_PRIMARY_KEY_2, _nullModel);
	}

	@Test
	public void testRemoveCache() {
		AtomicBoolean calledRemovePortalCache = new AtomicBoolean(false);

		InvocationHandler invocationHandler =
			new MultiVMPoolInvocationHandler(_classLoader, true) {

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

		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _props);

		entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		entityCacheImpl.removeCache(EntityCacheImplTest.class.getName());

		ConcurrentMap<String, PortalCache> portalCaches =
			ReflectionTestUtil.getFieldValue(entityCacheImpl, "_portalCaches");

		Assert.assertNull(
			portalCaches.get(EntityCacheImplTest.class.getName()));

		Assert.assertTrue(calledRemovePortalCache.get());
	}

	private EntityCacheImpl _activateEntityCacheImpl(
		MultiVMPool multiVMPool, Props props) {

		EntityCacheImpl entityCacheImpl = new EntityCacheImpl();

		entityCacheImpl.setMultiVMPool(multiVMPool);

		entityCacheImpl.setProps(props);

		entityCacheImpl.activate();

		return entityCacheImpl;
	}

	private void _assertGetResultShortcut(
		EntityCacheImpl entityCacheImpl, Class<?> clazz,
		Serializable primaryKey) {

		// Test shortcut when method parameter entityCacheEnabled is false

		Assert.assertNull(entityCacheImpl.getResult(false, clazz, primaryKey));

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			Assert.assertNull(
				entityCacheImpl.getResult(true, clazz, primaryKey));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			Assert.assertNull(
				entityCacheImpl.getResult(true, clazz, primaryKey));
		}
		finally {
			CacheRegistryUtil.setActive(true);
		}
	}

	private void _assertLocalCacheSize(
		EntityCacheImpl entityCacheImpl, int size) {

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			entityCacheImpl, "_localCache");

		LRUMap localCacheMap = localCache.get();

		Assert.assertEquals(size, localCacheMap.size());
	}

	private void _assertPutResultShortcut(
		EntityCacheImpl entityCacheImpl, Class<?> clazz,
		Serializable primaryKey) {

		PortalCache<Serializable, Serializable> portalCache =
			entityCacheImpl.getPortalCache(clazz);

		// Test shortcut when method parameter entityCacheEnabled is false

		entityCacheImpl.putResult(false, clazz, primaryKey, _nullModel);

		Assert.assertNull(portalCache.get(primaryKey));

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			entityCacheImpl.putResult(true, clazz, primaryKey, _nullModel);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		Assert.assertNull(portalCache.get(primaryKey));

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			entityCacheImpl.putResult(true, clazz, primaryKey, _nullModel);
		}
		finally {
			CacheRegistryUtil.setActive(true);
		}

		Assert.assertNull(portalCache.get(primaryKey));

		// Test shortcut when result is null

		entityCacheImpl.putResult(true, clazz, primaryKey, null);

		Assert.assertNull(portalCache.get(primaryKey));
	}

	private void _assertRemoveResultShortcut(
		EntityCacheImpl entityCacheImpl, Class<?> clazz) {

		PortalCache<Serializable, Serializable> portalCache =
			entityCacheImpl.getPortalCache(clazz);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		// Test shortcut when method parameter entityCacheEnabled is false

		entityCacheImpl.removeResult(
			false, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Assert.assertNotNull(portalCache.get(_PRIMARY_KEY_1));

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			entityCacheImpl.removeResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		Assert.assertNotNull(portalCache.get(_PRIMARY_KEY_1));

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			entityCacheImpl.removeResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1);
		}
		finally {
			CacheRegistryUtil.setActive(true);
		}

		Assert.assertNotNull(portalCache.get(_PRIMARY_KEY_1));
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

	private void _testLoadResult(MultiVMPool multiVMPool) {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _props);

		AtomicInteger sessionLoadCount = new AtomicInteger(0);

		SessionFactory sessionFactory = _getSessionFactory(sessionLoadCount);

		PortalCache<Serializable, Serializable> portalCache =
			entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		// Test shortcut when method parameter entityCacheEnabled is false

		Serializable result = entityCacheImpl.loadResult(
			false, EntityCacheImplTest.class, _PRIMARY_KEY_1, sessionFactory);

		Assert.assertNotNull(result);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));
		Assert.assertEquals(1, sessionLoadCount.get());

		// Test shortcut when _valueObjectEntityCacheEnabled is false

		ReflectionTestUtil.setFieldValue(
			entityCacheImpl, "_valueObjectEntityCacheEnabled", false);

		try {
			result = entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				entityCacheImpl, "_valueObjectEntityCacheEnabled", true);
		}

		Assert.assertNotNull(result);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));
		Assert.assertEquals(2, sessionLoadCount.get());

		// Test shortcut when CacheRegistryUtil.isActive() is false

		CacheRegistryUtil.setActive(false);

		try {
			result = entityCacheImpl.loadResult(
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

			result = entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);

			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());
			Assert.assertNotNull(result);
			Assert.assertSame(result, _nullModel);
			Assert.assertEquals(4, sessionLoadCount.get());

			captureHandler.resetLogLevel(Level.ALL);

			portalCache.removeAll();

			// Assert logger output

			entityCacheImpl.loadResult(
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

			entityCacheImpl.loadResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1,
				sessionFactory);

			Assert.assertEquals(5, sessionLoadCount.get());
		}

		result = entityCacheImpl.loadResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_2, sessionFactory);

		Assert.assertNull(result);
	}

	private void _testLoadResultWithLocalCache(MultiVMPool multiVMPool) {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _propsWithLocalCache);

		AtomicInteger sessionLoadCount = new AtomicInteger(0);

		SessionFactory sessionFactory = _getSessionFactory(sessionLoadCount);

		entityCacheImpl.loadResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, sessionFactory);

		Assert.assertEquals(1, sessionLoadCount.get());

		_assertLocalCacheSize(entityCacheImpl, 1);

		entityCacheImpl.loadResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, sessionFactory);

		Assert.assertEquals(1, sessionLoadCount.get());
	}

	private void _testPutAndGetNullModel(MultiVMPool multiVMPool) {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _props);

		_assertPutResultShortcut(
			entityCacheImpl, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		_assertGetResultShortcut(
			entityCacheImpl, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Serializable result = entityCacheImpl.getResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Assert.assertSame(_nullModel, result);
	}

	private void _testPutAndGetWithLocalCache(MultiVMPool multiVMPool) {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _propsWithLocalCache);

		_assertPutResultShortcut(
			entityCacheImpl, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		_assertLocalCacheSize(entityCacheImpl, 0);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			entityCacheImpl, "_localCache");

		LRUMap localCacheMap = localCache.get();

		Assert.assertEquals(localCacheMap.toString(), 1, localCacheMap.size());

		Set<Map.Entry> entries = localCacheMap.entrySet();

		Iterator<Map.Entry> itr = entries.iterator();

		Map.Entry entry = itr.next();

		Assert.assertSame(_nullModel, entry.getValue());

		_assertGetResultShortcut(
			entityCacheImpl, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Serializable result = entityCacheImpl.getResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Assert.assertSame(_nullModel, result);

		entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _propsWithLocalCache);

		PortalCache<Serializable, Serializable> portalCache =
			entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		portalCache.put(_PRIMARY_KEY_1, _nullModel);

		_assertLocalCacheSize(entityCacheImpl, 0);

		result = entityCacheImpl.getResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		_assertLocalCacheSize(entityCacheImpl, 1);

		Assert.assertSame(_nullModel, result);
	}

	private void _testPutAndRemoveNullModel(MultiVMPool multiVMPool) {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _props);

		PortalCache<Serializable, Serializable> portalCache =
			entityCacheImpl.getPortalCache(EntityCacheImplTest.class);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		_assertRemoveResultShortcut(entityCacheImpl, EntityCacheImplTest.class);

		entityCacheImpl.removeResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		Assert.assertNull(portalCache.get(_PRIMARY_KEY_1));
		Assert.assertNull(
			entityCacheImpl.getResult(
				true, EntityCacheImplTest.class, _PRIMARY_KEY_1));
	}

	private void _testPutAndRemoveWithLocalCache(MultiVMPool multiVMPool) {
		EntityCacheImpl entityCacheImpl = _activateEntityCacheImpl(
			multiVMPool, _propsWithLocalCache);

		entityCacheImpl.putResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1, _nullModel);

		_assertLocalCacheSize(entityCacheImpl, 1);

		_assertRemoveResultShortcut(entityCacheImpl, EntityCacheImplTest.class);

		_assertLocalCacheSize(entityCacheImpl, 1);

		entityCacheImpl.removeResult(
			true, EntityCacheImplTest.class, _PRIMARY_KEY_1);

		_assertLocalCacheSize(entityCacheImpl, 0);
	}

	private static final int _PRIMARY_KEY_1 = 12345;

	private static final int _PRIMARY_KEY_2 = 67890;

	private static ClassLoader _classLoader;
	private static MultiVMPool _notSerializedMultiVMPool;
	private static Serializable _nullModel;
	private static Props _props;
	private static Props _propsWithLocalCache;
	private static MultiVMPool _serializedMultiVMPool;

}