/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.benchmark.test.util;

import com.liferay.petra.string.StringBundler;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
				String.format("%.2f", (double)totalTime / _runCount), " ms."));

		for (String testName : _testNames) {
			AtomicLong durationSum = _sumsMap.get(testName);

			double average = (double)durationSum.get() / _runCount;

			Queue<Long> durations = _durationsMap.get(testName);

			Assert.assertEquals(
				durations.toString(), _runCount, durations.size());

			double variance = 0;

			for (Long duration : durations) {
				variance += (duration - average) * (duration - average);
			}

			variance = variance / _runCount;

			System.out.println(
				StringBundler.concat(
					"\nTest step ", testName, "\n\tAverage time: ",
					String.format("%.2f", average),
					" ms\n\tStandard deviation: ",
					String.format("%.2f", Math.sqrt(variance)), 2,
					" ms\n\tTPS: ",
					String.format(
						"%.2f", _runCount * 1000 / (double)durationSum.get())));
		}
	}

	public void record(String testName, long duration) {
		AtomicLong sum = _getOrCreate(testName, _sumsMap, AtomicLong::new);

		sum.addAndGet(duration);

		Queue<Long> durations = _getOrCreate(
			testName, _durationsMap, ConcurrentLinkedQueue::new);

		durations.offer(duration);
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
			else {
				if (!_testNames.contains(key)) {
					_testNames.offer(key);
				}
			}
		}

		return t;
	}

	private final Map<String, Queue<Long>> _durationsMap =
		new ConcurrentHashMap<>();
	private final int _runCount;
	private long _startTime;
	private final Map<String, AtomicLong> _sumsMap = new ConcurrentHashMap<>();
	private final Queue<String> _testNames = new ConcurrentLinkedQueue<>();

}