/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.ehcache.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cache.ehcache.internal.configuration.EhcachePortalCacheManagerConfiguration;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceType;
import org.ehcache.config.SizedResourcePool;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.internal.executor.OnDemandExecutionService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Tina Tian
 */
public class ShardedEhcachePortalCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() {
		ExecutorService executorService = ReflectionTestUtil.getFieldValue(
			BaseEhcachePortalCacheManager.class, "_executorService");

		CacheManagerBuilder<CacheManager> cacheManagerBuilder =
			CacheManagerBuilder.newCacheManagerBuilder();

		_cacheManager = cacheManagerBuilder.using(
			new OnDemandExecutionService() {

				@Override
				public ExecutorService getOrderedExecutor(
						String poolAlias, BlockingQueue<Runnable> queue)
					throws IllegalArgumentException {

					return executorService;
				}

			}
		).withCache(
			_TEST_CACHE_NAME,
			CacheConfigurationBuilder.newCacheConfigurationBuilder(
				Object.class, Object.class,
				ResourcePoolsBuilder.heap(_MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE))
		).withCache(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1),
			CacheConfigurationBuilder.newCacheConfigurationBuilder(
				Object.class, Object.class,
				ResourcePoolsBuilder.heap(
					_MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE_COMPANY_1))
		).build(
			true
		);

		_baseEhcachePortalCacheManager = new BaseEhcachePortalCacheManager() {
		};

		ReflectionTestUtil.setFieldValue(
			_baseEhcachePortalCacheManager, "_cacheManager", _cacheManager);

		EhcachePortalCacheManagerConfiguration
			ehcachePortalCacheManagerConfiguration =
				new EhcachePortalCacheManagerConfiguration(
					CacheConfigurationBuilder.newCacheConfigurationBuilder(
						Object.class, Object.class,
						ResourcePoolsBuilder.heap(
							_MAX_ENTRIES_LOCAL_HEAP_DEFAULT)
					).build(),
					null, Collections.emptySet());

		ReflectionTestUtil.setFieldValue(
			_baseEhcachePortalCacheManager,
			"_ehcachePortalCacheManagerConfiguration",
			ehcachePortalCacheManagerConfiguration);

		_companyThreadLocalMockedStatic.when(
			CompanyThreadLocal::getNonsystemCompanyId
		).thenAnswer(
			(Answer<Long>)invocationOnMock -> {
				long currentCompanyId = _companyIdThreadLocal.get();

				if (_companyIdThreadLocal.get() == CompanyConstants.SYSTEM) {
					currentCompanyId = _TEST_COMPANY_ID_1;
				}

				return currentCompanyId;
			}
		);

		_companyIdThreadLocal = ReflectionTestUtil.getFieldValue(
			CompanyThreadLocal.class, "_companyId");

		_shardedEhcachePortalCache = new ShardedEhcachePortalCache(
			_baseEhcachePortalCacheManager,
			new EhcachePortalCacheConfiguration(
				_TEST_CACHE_NAME, Collections.emptySet(), false));

		_companyIdThreadLocal.set(CompanyConstants.SYSTEM);

		_shardedEhcachePortalCache.put(_TEST_KEY_SYSTEM, _TEST_VALUE_SYSTEM);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		_shardedEhcachePortalCache.put(_TEST_KEY_1, _TEST_VALUE_1);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		_shardedEhcachePortalCache.put(_TEST_KEY_2, _TEST_VALUE_2);
	}

	@After
	public void tearDown() {
		_cacheManager.close();
		_companyThreadLocalMockedStatic.close();
	}

	@Test
	public void testCacheConfiguration() {
		_assertCacheConfiguration(
			_TEST_CACHE_NAME, _MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE);
		_assertCacheConfiguration(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1),
			_MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE_COMPANY_1);
		_assertCacheConfiguration(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2),
			_MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE);

		ShardedEhcachePortalCache testDefaultShardedEhcachePortalCache =
			new ShardedEhcachePortalCache(
				_baseEhcachePortalCacheManager,
				new EhcachePortalCacheConfiguration(
					"test.default.cache", Collections.emptySet(), false));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		testDefaultShardedEhcachePortalCache.put(_TEST_KEY_1, _TEST_VALUE_1);

		_assertCacheConfiguration(
			_getShardedCacheName("test.default.cache", _TEST_COMPANY_ID_1),
			_MAX_ENTRIES_LOCAL_HEAP_DEFAULT);
	}

	@Test
	public void testDispose() {
		List<String> cacheNames = _getCacheNames();

		Assert.assertTrue(
			cacheNames.toString(), cacheNames.contains(_TEST_CACHE_NAME));
		Assert.assertTrue(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1)));
		Assert.assertTrue(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2)));

		_shardedEhcachePortalCache.dispose();

		cacheNames = _getCacheNames();

		Assert.assertFalse(
			cacheNames.toString(), cacheNames.contains(_TEST_CACHE_NAME));
		Assert.assertFalse(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1)));
		Assert.assertFalse(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2)));
	}

	@Test
	public void testEhcacheName() {
		_assertEhcacheName(CompanyConstants.SYSTEM);
		_assertEhcacheName(_TEST_COMPANY_ID_1);
		_assertEhcacheName(_TEST_COMPANY_ID_2);
	}

	@Test
	public void testGet() {
		_companyIdThreadLocal.set(CompanyConstants.SYSTEM);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));
		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_2));
		Assert.assertSame(
			_TEST_VALUE_SYSTEM,
			_shardedEhcachePortalCache.get(_TEST_KEY_SYSTEM));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));
		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_1));
		Assert.assertSame(
			_TEST_VALUE_2, _shardedEhcachePortalCache.get(_TEST_KEY_2));
	}

	@Test
	public void testGetKeys() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertEquals(
			Arrays.asList(_TEST_KEY_1, _TEST_KEY_SYSTEM),
			_shardedEhcachePortalCache.getKeys());

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertEquals(
			Collections.singletonList(_TEST_KEY_2),
			_shardedEhcachePortalCache.getKeys());
	}

	@Test
	public void testMisc() {
		Assert.assertTrue(_shardedEhcachePortalCache.isSharded());

		Map<Long, Cache<Object, Object>> cachesMap =
			ReflectionTestUtil.getFieldValue(
				_shardedEhcachePortalCache, "_caches");

		Assert.assertFalse(cachesMap.toString(), cachesMap.isEmpty());

		_shardedEhcachePortalCache.resetEhcache();

		Assert.assertTrue(cachesMap.toString(), cachesMap.isEmpty());
	}

	@Test
	public void testPut() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_2));

		_shardedEhcachePortalCache.put(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertSame(
			_TEST_VALUE_2, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_1));

		_shardedEhcachePortalCache.put(_TEST_KEY_1, _TEST_VALUE_2, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_2, _TEST_KEY_1, _TEST_VALUE_2, 1000);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));
	}

	@Test
	public void testPutIfAbsent() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));

		_shardedEhcachePortalCache.putIfAbsent(_TEST_KEY_1, _TEST_VALUE_2);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_2));

		_shardedEhcachePortalCache.putIfAbsent(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_1));

		Assert.assertSame(
			_TEST_VALUE_2, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		_shardedEhcachePortalCache.putIfAbsent(
			_TEST_KEY_1, _TEST_VALUE_2, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_2, _TEST_KEY_1, _TEST_VALUE_2, 1000);

		_shardedEhcachePortalCache.putIfAbsent(
			_TEST_KEY_2, _TEST_VALUE_1, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_2, _TEST_KEY_2, _TEST_VALUE_2, 0);
	}

	@Test
	public void testRemove() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));

		_shardedEhcachePortalCache.remove(_TEST_KEY_1);

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_1));

		_shardedEhcachePortalCache.put(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertSame(
			_TEST_VALUE_2, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		_shardedEhcachePortalCache.remove(_TEST_KEY_2);

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_2));

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		_shardedEhcachePortalCache.remove(_TEST_KEY_2, _TEST_VALUE_2);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_2));

		_shardedEhcachePortalCache.remove(_TEST_KEY_2, _TEST_VALUE_1);

		Assert.assertNull(_shardedEhcachePortalCache.get(_TEST_KEY_2));
	}

	/*
	@Test
	public void testRegisterPortalCacheListener() {
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1), null);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2), null);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		PortalCacheListener<?, ?> portalCacheListener1 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_shardedEhcachePortalCache.registerPortalCacheListener(
			portalCacheListener1);

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1),
			portalCacheListener1);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2),
			portalCacheListener1);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		PortalCacheListener<?, ?> portalCacheListener2 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_shardedEhcachePortalCache.registerPortalCacheListener(
			portalCacheListener2, PortalCacheListenerScope.LOCAL);

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1),
			portalCacheListener1, portalCacheListener2);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2),
			portalCacheListener1, portalCacheListener2);

		_companyIdThreadLocal.set(3000L);

		_shardedEhcachePortalCache.put(_TEST_KEY_1, _TEST_VALUE_1);

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, 3000L), portalCacheListener1,
			portalCacheListener2);
	}
	*/

	@Test
	public void testRemoveAll() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		List<?> keys = _shardedEhcachePortalCache.getKeys();

		Assert.assertFalse(keys.isEmpty());

		_shardedEhcachePortalCache.removeAll();

		keys = _shardedEhcachePortalCache.getKeys();

		Assert.assertTrue(keys.isEmpty());

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		keys = _shardedEhcachePortalCache.getKeys();

		Assert.assertFalse(keys.isEmpty());
	}

	@Test
	public void testRemoveEhcache() {
		List<String> cacheNames = _getCacheNames();

		Assert.assertTrue(
			cacheNames.toString(), cacheNames.contains(_TEST_CACHE_NAME));
		Assert.assertTrue(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1)));
		Assert.assertTrue(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2)));

		_shardedEhcachePortalCache.removeEhcache(_TEST_COMPANY_ID_1);

		cacheNames = _getCacheNames();

		Assert.assertTrue(
			cacheNames.toString(), cacheNames.contains(_TEST_CACHE_NAME));
		Assert.assertFalse(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1)));
		Assert.assertTrue(
			cacheNames.toString(),
			cacheNames.contains(
				_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2)));

		_shardedEhcachePortalCache.removeEhcache(100L);

		Assert.assertEquals(cacheNames, _getCacheNames());
	}

	@Test
	public void testReplace() {
		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		_shardedEhcachePortalCache.put(_TEST_KEY_1, _TEST_VALUE_2);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));

		_shardedEhcachePortalCache.replace(_TEST_KEY_1, _TEST_VALUE_2);

		Assert.assertSame(
			_TEST_VALUE_2, _shardedEhcachePortalCache.get(_TEST_KEY_1));

		_shardedEhcachePortalCache.replace(
			_TEST_KEY_1, _TEST_VALUE_2, _TEST_VALUE_1);

		Assert.assertSame(
			_TEST_VALUE_1, _shardedEhcachePortalCache.get(_TEST_KEY_1));

		_shardedEhcachePortalCache.replace(_TEST_KEY_1, _TEST_VALUE_2, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_1, _TEST_KEY_1, _TEST_VALUE_2, 1000);

		_shardedEhcachePortalCache.replace(
			_TEST_KEY_1, _TEST_VALUE_2, _TEST_VALUE_1, 1000);

		_assertTimeToLive(_TEST_COMPANY_ID_1, _TEST_KEY_1, _TEST_VALUE_1, 1000);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		Assert.assertSame(
			_TEST_VALUE_2, _shardedEhcachePortalCache.get(_TEST_KEY_1));
	}

	private void _assertCacheConfiguration(
		String cacheName, int maxEntriesLocalHeap) {

		Cache<Object, Object> cache = _cacheManager.getCache(
			cacheName, Object.class, Object.class);

		CacheConfiguration<Object, Object> cacheConfiguration =
			cache.getRuntimeConfiguration();

		ResourcePools resourcePools = cacheConfiguration.getResourcePools();

		SizedResourcePool sizedResourcePool = resourcePools.getPoolForResource(
			ResourceType.Core.HEAP);

		Assert.assertEquals(EntryUnit.ENTRIES, sizedResourcePool.getUnit());
		Assert.assertEquals(maxEntriesLocalHeap, sizedResourcePool.getSize());
	}

	/*
	@Test
	public void testUnregisterPortalCacheListener() {
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1), null);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2), null);

		PortalCacheListener<?, ?> portalCacheListener1 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_shardedEhcachePortalCache.registerPortalCacheListener(
			portalCacheListener1);

		PortalCacheListener<?, ?> portalCacheListener2 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_shardedEhcachePortalCache.registerPortalCacheListener(
			portalCacheListener2);

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1),
			portalCacheListener1, portalCacheListener2);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2),
			portalCacheListener1, portalCacheListener2);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_1);

		_shardedEhcachePortalCache.unregisterPortalCacheListener(
			portalCacheListener1);

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1),
			portalCacheListener2);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2),
			portalCacheListener2);

		_companyIdThreadLocal.set(_TEST_COMPANY_ID_2);

		_shardedEhcachePortalCache.unregisterPortalCacheListener(
			portalCacheListener2);

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1), null);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2), null);
	}

	@Test
	public void testUnregisterPortalCacheListeners() {
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1), null);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2), null);

		PortalCacheListener<?, ?> portalCacheListener1 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_shardedEhcachePortalCache.registerPortalCacheListener(
			portalCacheListener1);

		PortalCacheListener<?, ?> portalCacheListener2 =
			ProxyFactory.newDummyInstance(PortalCacheListener.class);

		_shardedEhcachePortalCache.registerPortalCacheListener(
			portalCacheListener2);

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1),
			portalCacheListener1, portalCacheListener2);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2),
			portalCacheListener1, portalCacheListener2);

		_shardedEhcachePortalCache.unregisterPortalCacheListeners();

		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_1), null);
		_assertPortalCacheListener(
			_getShardedCacheName(_TEST_CACHE_NAME, _TEST_COMPANY_ID_2), null);
	}
	*/

	private void _assertEhcacheName(long companyId) {
		_companyIdThreadLocal.set(companyId);

		Assert.assertSame(
			_cacheManager.getCache(
				_getShardedCacheName(
					_TEST_CACHE_NAME,
					(companyId == CompanyConstants.SYSTEM) ?
						_TEST_COMPANY_ID_1 : companyId),
				Object.class, Object.class),
			_shardedEhcachePortalCache.getEhcache());
	}

	private void _assertTimeToLive(
		long companyId, String key, String value, int timeToLive) {

		Cache<Object, Object> cache = _cacheManager.getCache(
			_getShardedCacheName(_TEST_CACHE_NAME, companyId), Object.class,
			Object.class);

		EhcacheValue ehcacheValue = (EhcacheValue)cache.get(key);

		Assert.assertEquals(value, ehcacheValue.getValue());

		long actualTimeToLive = 0;

		Duration duration = ehcacheValue.getTimeToLive();

		if (!duration.equals(ExpiryPolicy.INFINITE)) {
			actualTimeToLive = duration.toSeconds();
		}

		Assert.assertEquals(timeToLive, actualTimeToLive);
	}

	/*
	private void _assertPortalCacheListener(
		String cacheName,
		PortalCacheListener<?, ?>... registeredPortalCacheListeners) {

		Ehcache<Object, Object> ehcache =
			(Ehcache<Object, Object>)_cacheManager.getCache(
				cacheName, Object.class, Object.class);

		RegisteredEventListeners registeredEventListeners =
			cache.getCacheEventNotificationService();

		Set<CacheEventListener> cacheEventListeners =
			registeredEventListeners.getCacheEventListeners();

		Assert.assertEquals(
			cacheEventListeners.toString(), 1, cacheEventListeners.size());

		Iterator<CacheEventListener> iterator = cacheEventListeners.iterator();

		AggregatedPortalCacheListener<?, ?> aggregatedPortalCacheListener =
			ReflectionTestUtil.getFieldValue(
				iterator.next(), "_aggregatedPortalCacheListener");

		Map<?, ?> portalCacheListeners =
			aggregatedPortalCacheListener.getPortalCacheListeners();

		if (registeredPortalCacheListeners == null) {
			Assert.assertTrue(
				portalCacheListeners.toString(),
				portalCacheListeners.isEmpty());
		}
		else {
			for (PortalCacheListener<?, ?> registeredPortalCacheListener :
					registeredPortalCacheListeners) {

				Assert.assertTrue(
					portalCacheListeners.toString(),
					portalCacheListeners.containsKey(
						registeredPortalCacheListener));
			}
		}
	}*/

	private List<String> _getCacheNames() {
		Configuration configuration = _cacheManager.getRuntimeConfiguration();

		Map<String, CacheConfiguration<?, ?>> cacheConfigurationsMap =
			configuration.getCacheConfigurations();

		return new ArrayList<>(cacheConfigurationsMap.keySet());
	}

	private String _getShardedCacheName(String cacheName, long companyId) {
		return StringBundler.concat(
			cacheName,
			ReflectionTestUtil.getFieldValue(
				ShardedEhcachePortalCache.class, "_SHARDED_SEPARATOR"),
			companyId);
	}

	private static final int _MAX_ENTRIES_LOCAL_HEAP_DEFAULT = 100;

	private static final int _MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE = 200;

	private static final int _MAX_ENTRIES_LOCAL_HEAP_TEST_CACHE_COMPANY_1 = 300;

	private static final String _TEST_CACHE_NAME = "TEST_CACHE_NAME";

	private static final long _TEST_COMPANY_ID_1 = 1000L;

	private static final long _TEST_COMPANY_ID_2 = 2000L;

	private static final String _TEST_KEY_1 = "TEST_KEY_1";

	private static final String _TEST_KEY_2 = "TEST_KEY_2";

	private static final String _TEST_KEY_SYSTEM = "TEST_KEY_SYSTEM";

	private static final String _TEST_VALUE_1 = "TEST_VALUE_1";

	private static final String _TEST_VALUE_2 = "TEST_VALUE_2";

	private static final String _TEST_VALUE_SYSTEM = "TEST_VALUE_SYSTEM";

	private static BaseEhcachePortalCacheManager _baseEhcachePortalCacheManager;
	private static CacheManager _cacheManager;
	private static ThreadLocal<Long> _companyIdThreadLocal;

	private final MockedStatic<CompanyThreadLocal>
		_companyThreadLocalMockedStatic = Mockito.mockStatic(
			CompanyThreadLocal.class);
	private ShardedEhcachePortalCache _shardedEhcachePortalCache;

}