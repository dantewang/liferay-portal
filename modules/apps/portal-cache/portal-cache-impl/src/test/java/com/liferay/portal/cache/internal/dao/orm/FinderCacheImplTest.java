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

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.cache.key.HashCodeHexStringCacheKeyGenerator;
import com.liferay.portal.cache.test.util.TestPortalCache;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.collections.map.LRUMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Preston Crary
 * @author Leon Chi
 */
public class FinderCacheImplTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler());

		_serializedMultiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, true));
		_notSerializedMultiVMPool = (MultiVMPool)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {MultiVMPool.class},
			new MultiVMPoolInvocationHandler(_classLoader, false));

		RegistryUtil.setRegistry(new BasicRegistryImpl());

		CacheKeyGeneratorUtil cacheKeyGeneratorUtil =
			new CacheKeyGeneratorUtil();

		cacheKeyGeneratorUtil.setDefaultCacheKeyGenerator(_cacheKeyGenerator);
	}

	@Before
	public void setUp() {
		_finderPath = new FinderPath(
			true, true, FinderCacheImplTest.class,
			FinderCacheImplTest.class.getName(), "test",
			new String[] {String.class.getName()});

		_nullModel = ReflectionTestUtil.getFieldValue(
			BasePersistenceImpl.class, "nullModel");
	}

	@Test
	public void testActivate() {
		_assertActivate(_notSerializedMultiVMPool);
		_assertActivate(_serializedMultiVMPool);
	}

	@Test
	public void testClearLocalCache() {
		_assertClearLocalCache(_notSerializedMultiVMPool);
		_assertClearLocalCache(_serializedMultiVMPool);
	}

	@Test
	public void testGetPortalCacheConcurrent() throws InterruptedException {
		_assertGetPortalCacheConcurrent(_notSerializedMultiVMPool);
		_assertGetPortalCacheConcurrent(_serializedMultiVMPool);
	}

	@Test
	public void testGetResultInvalid() {
		_assertGetResultInvalid(_notSerializedMultiVMPool);
		_assertGetResultInvalid(_serializedMultiVMPool);
	}

	@Test
	public void testInit() {
		FinderCacheImpl finderCacheImpl = new FinderCacheImpl();

		finderCacheImpl.init();
	}

	@Test
	public void testNotifyPortalCacheAdded() {
		FinderCacheImpl finderCacheImpl = new FinderCacheImpl();

		finderCacheImpl.notifyPortalCacheAdded(null);
	}

	@Test
	public void testPortalCaches() {
		_assertPortalCaches(_notSerializedMultiVMPool);
		_assertPortalCaches(_serializedMultiVMPool);
	}

	@Test
	public void testPutAndGetList() {
		_assertPutAndGetList(_notSerializedMultiVMPool);
		_assertPutAndGetList(_serializedMultiVMPool);
	}

	@Test
	public void testPutAndGetNullModel() {
		_assertPutAndGetNullModel(_notSerializedMultiVMPool);
		_assertPutAndGetNullModel(_serializedMultiVMPool);
	}

	@Test
	public void testPutEmptyListInvalid() {
		_assertPutEmptyListInvalid(_notSerializedMultiVMPool);
		_assertPutEmptyListInvalid(_serializedMultiVMPool);
	}

	@Test
	public void testPutEmptyListValid() {
		_assertPutEmptyListValid(_notSerializedMultiVMPool);
		_assertPutEmptyListValid(_serializedMultiVMPool);
	}

	@Test
	public void testPutResultInvalid() {
		_assertPutResultInvalid(_notSerializedMultiVMPool);
		_assertPutResultInvalid(_serializedMultiVMPool);
	}

	@Test
	public void testPutStringResult() {
		_assertPutStringResult(_serializedMultiVMPool);
	}

	@Test
	public void testRegistryName() {
		FinderCacheImpl finderCacheImpl = new FinderCacheImpl();

		Assert.assertEquals(
			FinderCache.class.getName(), finderCacheImpl.getRegistryName());
	}

	@Test
	public void testRemoveResult() {
		_assertRemoveResult(_notSerializedMultiVMPool);
		_assertRemoveResult(_serializedMultiVMPool);
	}

	@Test
	public void testTestKeysCollide() {
		Assert.assertEquals(
			_cacheKeyGenerator.getCacheKey(_KEY1),
			_cacheKeyGenerator.getCacheKey(_KEY2));
	}

	@Test
	public void testThreshold() {
		_assertThreshold(_notSerializedMultiVMPool);
		_assertThreshold(_serializedMultiVMPool);
	}

	private FinderCache _activateFinderCache(MultiVMPool multiVMPool) {
		FinderCacheImpl finderCacheImpl = new FinderCacheImpl();

		EntityCacheImpl entityCacheImpl = new EntityCacheImpl();

		entityCacheImpl.setMultiVMPool(multiVMPool);

		finderCacheImpl.setEntityCache(entityCacheImpl);

		finderCacheImpl.setMultiVMPool(multiVMPool);

		finderCacheImpl.setProps(_props);

		finderCacheImpl.activate();

		return finderCacheImpl;
	}

	private void _assertActivate(MultiVMPool multiVMPool) {
		FinderCacheImpl finderCacheImpl = new FinderCacheImpl();

		Props props = (Props)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {Props.class},
			new PropsInvocationHandler(false, false));

		finderCacheImpl.setMultiVMPool(multiVMPool);

		finderCacheImpl.setProps(props);

		finderCacheImpl.activate();

		boolean valueObjectFinderCacheEnabled =
			ReflectionTestUtil.getFieldValue(
				finderCacheImpl, "_valueObjectFinderCacheEnabled");

		Assert.assertFalse(valueObjectFinderCacheEnabled);

		boolean localCacheAvailable = ReflectionTestUtil.getFieldValue(
			finderCacheImpl, "_localCacheAvailable");

		Assert.assertFalse(localCacheAvailable);

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			finderCacheImpl, "_localCache");

		Assert.assertNull(localCache);
	}

	private void _assertClearLocalCache(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			finderCache, "_localCache");

		LRUMap map = localCache.get();

		Assert.assertEquals(0, map.size());

		map.put("key1", "value1");
		map.put("key2", "value2");

		map = localCache.get();

		Assert.assertEquals(2, map.size());

		finderCache.clearLocalCache();

		map = localCache.get();

		Assert.assertEquals(0, map.size());
	}

	private void _assertGetPortalCacheConcurrent(MultiVMPool multiVMPool)
		throws InterruptedException {

		FinderCache finderCache = _activateFinderCache(multiVMPool);

		ConcurrentMap<String, PortalCache> portalCaches =
			new ConcurrentHashMap<>();

		ConcurrentMapInvocationHandler concurrentMapInvocationHandler =
			new ConcurrentMapInvocationHandler(portalCaches);

		ConcurrentMap<String, PortalCache> proxyPortalCaches =
			(ConcurrentMap)ProxyUtil.newProxyInstance(
				_classLoader, new Class<?>[] {ConcurrentMap.class},
				concurrentMapInvocationHandler);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_portalCaches", proxyPortalCaches);

		Method method = ReflectionTestUtil.getMethod(
			FinderCacheImpl.class, "_getPortalCache", String.class);

		concurrentMapInvocationHandler.block();

		Thread thread1 = new Thread() {

			@Override
			public void run() {
				try {
					method.invoke(finderCache, "PortalCacheName");
				}
				catch (IllegalAccessException iae) {
					iae.printStackTrace();
				}
				catch (InvocationTargetException ite) {
					ite.printStackTrace();
				}
			}

		};

		thread1.start();

		concurrentMapInvocationHandler.waitUntilBlock(1);

		Thread thread2 = new Thread() {

			@Override
			public void run() {
				try {
					method.invoke(finderCache, "PortalCacheName");
				}
				catch (IllegalAccessException iae) {
					iae.printStackTrace();
				}
				catch (InvocationTargetException ite) {
					ite.printStackTrace();
				}
			}

		};

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

	private void _assertGetResultInvalid(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheEnabled", false);

		Object result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		Assert.assertNull(result);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheEnabled", true);
		ReflectionTestUtil.setFieldValue(
			_finderPath, "_finderCacheEnabled", false);

		Object result1 = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		Assert.assertNull(result1);

		ReflectionTestUtil.setFieldValue(
			_finderPath, "_finderCacheEnabled", true);

		CacheRegistryUtil.setActive(false);

		Object result2 = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		CacheRegistryUtil.setActive(true);
		Assert.assertNull(result2);
	}

	private void _assertPortalCaches(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		ConcurrentMap<String, PortalCache<Serializable, Serializable>>
			portalCaches = ReflectionTestUtil.getFieldValue(
				finderCache, "_portalCaches");

		PortalCache testPortalCache1 = new TestPortalCache("testPortalCache1");
		PortalCache testPortalCache2 = new TestPortalCache("testPortalCache2");

		testPortalCache1.put("key1", "value1");
		testPortalCache2.put("key1", "value1");

		portalCaches.put("testPortalCache1", testPortalCache1);
		portalCaches.put("testPortalCache2", testPortalCache2);

		finderCache.clearCache("testPortalCache1");

		Assert.assertNull(testPortalCache1.get("key1"));
		Assert.assertEquals("value1", testPortalCache2.get("key1"));
		Assert.assertEquals(portalCaches.toString(), 2, portalCaches.size());

		finderCache.removeCache("testPortalCache1");

		Assert.assertNull(portalCaches.get("testPortalCache1"));
		Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());

		finderCache.invalidate();

		Assert.assertNull(testPortalCache2.get("key1"));
		Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());

		FinderCacheImpl finderCacheImpl = (FinderCacheImpl)finderCache;

		finderCacheImpl.dispose();

		Assert.assertEquals(portalCaches.toString(), 0, portalCaches.size());
	}

	private void _assertPutAndGetList(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		List<Serializable> values = new ArrayList<>();

		values.add("a");
		values.add("b");

		finderCache.putResult(_finderPath, _KEY1, values, true);

		values.remove("b");

		Object result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(new HashSet<>(values)));

		Assert.assertNull(result);
	}

	private void _assertPutAndGetNullModel(MultiVMPool multiVMPool) {
		FinderPath finderPath = new FinderPath(
			true, true, BaseModel.class, FinderCacheImplTest.class.getName(),
			"test", new String[] {String.class.getName()});

		TestBaseModel testBaseModel = new TestBaseModel();

		TestBasePersistence testBasePersistence = new TestBasePersistence(null);

		EntityCacheImplInvocationHandler entityCacheImplInvocationHandler =
			new EntityCacheImplInvocationHandler(testBaseModel);

		EntityCache proxyEntityCache = (EntityCache)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {EntityCache.class},
			entityCacheImplInvocationHandler);

		FinderCacheImpl finderCacheImpl = new FinderCacheImpl();

		finderCacheImpl.setEntityCache(proxyEntityCache);

		finderCacheImpl.setMultiVMPool(multiVMPool);

		finderCacheImpl.setProps(_props);

		finderCacheImpl.activate();

		finderCacheImpl.putResult(finderPath, _KEY1, testBaseModel, true);

		Object result = finderCacheImpl.getResult(
			finderPath, _KEY1, testBasePersistence);

		Assert.assertSame(testBaseModel, result);

		EntityCacheImplInvocationHandler entityCacheImplInvocationHandler1 =
			new EntityCacheImplInvocationHandler(_nullModel);

		EntityCache proxyEntityCache1 = (EntityCache)ProxyUtil.newProxyInstance(
			_classLoader, new Class<?>[] {EntityCache.class},
			entityCacheImplInvocationHandler1);

		finderCacheImpl.setEntityCache(proxyEntityCache1);

		result = finderCacheImpl.getResult(
			finderPath, _KEY1, testBasePersistence);

		Assert.assertNull(result);
	}

	private void _assertPutEmptyListInvalid(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		finderCache.putResult(
			_finderPath, _KEY1, Collections.emptyList(), true);

		Object result = finderCache.getResult(_finderPath, _KEY2, null);

		Assert.assertNull(result);

		result = finderCache.getResult(_finderPath, _KEY3, null);

		Assert.assertNull(result);

		finderCache.putResult(
			_finderPath, _KEY3, Collections.emptyList(), true);

		result = finderCache.getResult(_finderPath, _KEY4, null);

		Assert.assertNull(result);
	}

	private void _assertPutEmptyListValid(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		finderCache.putResult(
			_finderPath, _KEY1, Collections.emptyList(), true);

		Object result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertSame(Collections.emptyList(), result);

		ThreadLocal<LRUMap> localCache = ReflectionTestUtil.getFieldValue(
			finderCache, "_localCache");

		LRUMap map = localCache.get();

		Assert.assertEquals(1, map.size());

		localCache.remove();

		map = localCache.get();

		Assert.assertEquals(0, map.size());

		finderCache.getResult(_finderPath, _KEY1, null);

		map = localCache.get();

		Assert.assertEquals(1, map.size());

		finderCache.putResult(
			_finderPath, _KEY1, Collections.emptyList(), false);

		result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertSame(Collections.emptyList(), result);
	}

	private void _assertPutResultInvalid(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		ConcurrentMap<String, PortalCache<Serializable, Serializable>>
			portalCaches = ReflectionTestUtil.getFieldValue(
				finderCache, "_portalCaches");

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheEnabled", false);

		finderCache.putResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		Assert.assertEquals(portalCaches.toString(), 0, portalCaches.size());

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheEnabled", true);
		ReflectionTestUtil.setFieldValue(
			_finderPath, "_finderCacheEnabled", false);

		finderCache.putResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		Assert.assertEquals(portalCaches.toString(), 0, portalCaches.size());

		ReflectionTestUtil.setFieldValue(
			_finderPath, "_finderCacheEnabled", true);

		CacheRegistryUtil.setActive(false);

		finderCache.putResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		CacheRegistryUtil.setActive(true);

		Assert.assertEquals(portalCaches.toString(), 0, portalCaches.size());

		finderCache.putResult(_finderPath, _KEY1, null);

		Assert.assertEquals(portalCaches.toString(), 0, portalCaches.size());
	}

	private void _assertPutStringResult(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		finderCache.putResult(_finderPath, _KEY1, "StringResult", true);

		String result = (String)finderCache.getResult(_finderPath, _KEY2, null);

		Assert.assertEquals("StringResult", result);
	}

	private void _assertRemoveResult(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		finderCache.putResult(
			_finderPath, _KEY1, Collections.emptyList(), true);

		Object result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertSame(Collections.emptyList(), result);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheEnabled", false);

		finderCache.removeResult(_finderPath, _KEY1);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheEnabled", true);

		result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertNotNull(result);

		ReflectionTestUtil.setFieldValue(
			_finderPath, "_finderCacheEnabled", false);

		finderCache.removeResult(_finderPath, _KEY1);

		ReflectionTestUtil.setFieldValue(
			_finderPath, "_finderCacheEnabled", true);

		result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		Assert.assertNotNull(result);

		CacheRegistryUtil.setActive(false);

		finderCache.removeResult(_finderPath, _KEY1);

		CacheRegistryUtil.setActive(true);

		result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(null));

		Assert.assertNotNull(result);

		finderCache.removeResult(_finderPath, _KEY1);

		result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertNull(result);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_localCacheAvailable", false);

		finderCache.putResult(
			_finderPath, _KEY1, Collections.emptyList(), true);

		result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertSame(Collections.emptyList(), result);

		finderCache.removeResult(_finderPath, _KEY1);

		result = finderCache.getResult(_finderPath, _KEY1, null);

		Assert.assertNull(result);
	}

	private void _assertThreshold(MultiVMPool multiVMPool) {
		FinderCache finderCache = _activateFinderCache(multiVMPool);

		List<Serializable> values = new ArrayList<>();

		values.add("a");
		values.add("b");

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheListThreshold", 0);

		finderCache.putResult(_finderPath, _KEY1, values, true);

		Object result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(new HashSet<>(values)));

		Assert.assertEquals(values, result);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_valueObjectFinderCacheListThreshold", 2);

		finderCache.putResult(_finderPath, _KEY1, values, true);

		result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(new HashSet<>(values)));

		Assert.assertEquals(values, result);

		values.add("c");

		finderCache.putResult(_finderPath, _KEY1, values, true);

		result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(new HashSet<>(values)));

		Assert.assertNull(result);

		ReflectionTestUtil.setFieldValue(
			finderCache, "_localCacheAvailable", false);

		finderCache.putResult(_finderPath, _KEY1, values, false);

		result = finderCache.getResult(
			_finderPath, _KEY1, new TestBasePersistence(new HashSet<>(values)));

		Assert.assertNull(result);
	}

	private static final String[] _KEY1 = {"home"};

	private static final String[] _KEY2 = {"j1me"};

	private static final String[] _KEY3 = {"homel"};

	private static final String[] _KEY4 = {"homg", ""};

	private static final CacheKeyGenerator _cacheKeyGenerator =
		new HashCodeHexStringCacheKeyGenerator();
	private static final ClassLoader _classLoader =
		FinderCacheImplTest.class.getClassLoader();
	private static MultiVMPool _notSerializedMultiVMPool;
	private static Props _props;
	private static MultiVMPool _serializedMultiVMPool;

	private FinderPath _finderPath;
	private Serializable _nullModel;

	private static class TestBaseModel implements BaseModel<TestBaseModel> {

		@Override
		public Object clone() {
			return null;
		}

		@Override
		public int compareTo(TestBaseModel o) {
			return 0;
		}

		@Override
		public ExpandoBridge getExpandoBridge() {
			return null;
		}

		@Override
		public Map<String, Object> getModelAttributes() {
			return null;
		}

		@Override
		public Class<?> getModelClass() {
			return null;
		}

		@Override
		public String getModelClassName() {
			return null;
		}

		@Override
		public Serializable getPrimaryKeyObj() {
			return _PRIMARY_KEY;
		}

		@Override
		public boolean isCachedModel() {
			return false;
		}

		@Override
		public boolean isEntityCacheEnabled() {
			return false;
		}

		@Override
		public boolean isEscapedModel() {
			return false;
		}

		@Override
		public boolean isFinderCacheEnabled() {
			return false;
		}

		@Override
		public boolean isNew() {
			return false;
		}

		@Override
		public void resetOriginalValues() {
		}

		@Override
		public void setCachedModel(boolean cachedModel) {
		}

		@Override
		public void setExpandoBridgeAttributes(BaseModel<?> baseModel) {
		}

		@Override
		public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		}

		@Override
		public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		}

		@Override
		public void setModelAttributes(Map<String, Object> attributes) {
		}

		@Override
		public void setNew(boolean n) {
		}

		@Override
		public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		}

		@Override
		public CacheModel<TestBaseModel> toCacheModel() {
			return null;
		}

		@Override
		public TestBaseModel toEscapedModel() {
			return null;
		}

		@Override
		public TestBaseModel toUnescapedModel() {
			return null;
		}

		@Override
		public String toXmlString() {
			return null;
		}

		private static final String _PRIMARY_KEY = "PrimaryKey";

	}

	private static class TestBasePersistence<T extends BaseModel<T>>
		extends BasePersistenceImpl<T> {

		@Override
		public Map<Serializable, T> fetchByPrimaryKeys(
			Set<Serializable> primaryKeys) {

			Assert.assertNotNull(_keys);

			Map map = new HashMap();

			for (Object key : _keys) {
				map.put(key, key);
			}

			return map;
		}

		private TestBasePersistence(Set<?> keys) {
			_keys = keys;
		}

		private final Set<?> _keys;

	}

}