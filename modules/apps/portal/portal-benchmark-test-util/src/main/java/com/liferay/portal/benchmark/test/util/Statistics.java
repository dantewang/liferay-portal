/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.petra.string.StringBundler;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.junit.Assert;

/**
 * @author Dante Wang
 */
public class Statistics {

	public Statistics(int runCount) {
		_runCount = runCount;
	}

	public void finish() {
		long totalTime = System.currentTimeMillis() - _startTime;

		System.out.println(
			StringBundler.concat(
				"\nTests took ", totalTime, " ms. Each Login session took ",
				(double)totalTime / _runCount, " ms."));

		for (Map.Entry<String, AtomicLong> entry : _sumsMap.entrySet()) {
			AtomicLong durationSum = entry.getValue();

			double average = (double)durationSum.get() / _runCount;

			BlockingQueue<Long> durations = _durationsMap.get(entry.getKey());

			Assert.assertEquals(
				durations.toString(), _runCount, durations.size());

			double variance = 0;

			for (Long duration : durations) {
				variance += (duration - average) * (duration - average);
			}

			variance = variance / _runCount;

			System.out.println(
				StringBundler.concat(
					"\nTest step ", entry.getKey(), "\n\tAverage time: ",
					average, " ms\n\tStandard deviation: ", Math.sqrt(variance),
					" ms\n\tTPS: ",
					_runCount * 1000 / (double)durationSum.get()));
		}
	}

	public <T> T record(String testName, Callable<T> callable)
		throws Exception {

		long startTime = System.currentTimeMillis();

		T result = callable.call();

		long duration = System.currentTimeMillis() - startTime;

		AtomicLong sum = _getOrCreate(testName, _sumsMap, AtomicLong::new);

		sum.addAndGet(duration);

		BlockingQueue<Long> durations = _getOrCreate(
			testName, _durationsMap, LinkedBlockingQueue::new);

		durations.offer(duration);

		return result;
	}

	public void start() {
		_startTime = System.currentTimeMillis();
	}

	private <T> T _getOrCreate(
		String key, Map<String, T> map, Supplier<T> supplier) {

		T t = map.get(key);

		if (t == null) {
			t = supplier.get();

			T prevousT = map.putIfAbsent(key, t);

			if (prevousT != null) {
				t = prevousT;
			}
		}

		return t;
	}

	private final Map<String, BlockingQueue<Long>> _durationsMap =
		new ConcurrentHashMap<>();
	private final int _runCount;
	private long _startTime;
	private final Map<String, AtomicLong> _sumsMap = new ConcurrentHashMap<>();

}