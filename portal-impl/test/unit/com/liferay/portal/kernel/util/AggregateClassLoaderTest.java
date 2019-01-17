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

package com.liferay.portal.kernel.util;

import com.liferay.petra.memory.EqualityWeakReference;
import com.liferay.portal.kernel.exception.LoggedExceptionInInitializerError;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.GCUtil;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.test.aspects.ReflectionUtilAdvice;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.AspectJNewEnvTestRule;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Xiangyue Cai
 */
public class AggregateClassLoaderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			AspectJNewEnvTestRule.INSTANCE, CodeCoverageAssertor.INSTANCE);

	@Test
	public void testAddClassLoader() {

		// parent class loader is null

		_testAddClassLoader(
			new ClassLoader[] {_testClassLoader1, _testClassLoader2},
			new ClassLoader[] {_testClassLoader1, _testClassLoader2}, null);

		_testAddClassLoader(
			new ClassLoader[] {_testClassLoader2},
			new ClassLoader[] {_testClassLoader2, _testClassLoader2}, null);

		_testAddClassLoader(
			new ClassLoader[] {_testClassLoader1},
			new ClassLoader[] {new AggregateClassLoader(_testClassLoader1)},
			null);

		_testAddClassLoader(
			new ClassLoader[] {_testClassLoader1, _testClassLoader2},
			new ClassLoader[] {
				AggregateClassLoader.getAggregateClassLoader(
					_testClassLoader1, _testClassLoader2)
			},
			null);

		// parent class loader is not null

		_testAddClassLoader(
			new ClassLoader[] {_testClassLoader2},
			new ClassLoader[] {_testClassLoader1, _testClassLoader2},
			_testClassLoader1);

		_testAddClassLoader(
			new ClassLoader[] {_testClassLoader2},
			new ClassLoader[] {_testClassLoader2, _testClassLoader2},
			_testClassLoader1);

		_testAddClassLoader(
			new ClassLoader[0],
			new ClassLoader[] {new AggregateClassLoader(_testClassLoader1)},
			_testClassLoader1);

		_testAddClassLoader(
			new ClassLoader[] {_testClassLoader2},
			new ClassLoader[] {
				AggregateClassLoader.getAggregateClassLoader(
					_testClassLoader1, _testClassLoader2)
			},
			_testClassLoader1);
	}

	@Test
	public void testConstructor() {
		AggregateClassLoader aggregateClassLoader = new AggregateClassLoader(
			_testClassLoader1);

		Assert.assertSame(_testClassLoader1, aggregateClassLoader.getParent());
	}

	@Test
	public void testEquals() {
		AggregateClassLoader aggregateClassLoader =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testClassLoader1);

		Assert.assertTrue(
			"equals() should return true for the same instance",
			aggregateClassLoader.equals(aggregateClassLoader));

		Assert.assertFalse(
			"equals() should return false for object which is not instance " +
				"of AggregateClassLoader",
			aggregateClassLoader.equals(_testClassLoader1));

		Assert.assertFalse(
			"equals() should return false for AggregateClassLoaders with " +
				"different aggregated class loaders",
			aggregateClassLoader.equals(
				AggregateClassLoader.getAggregateClassLoader(
					null, _testClassLoader2)));

		Assert.assertFalse(
			"equals() should return false for AggregateClassLoaders with " +
				"different parent class loaders",
			aggregateClassLoader.equals(
				AggregateClassLoader.getAggregateClassLoader(
					_testClassLoader2, _testClassLoader1)));

		Assert.assertTrue(
			"equals() should return true for AggregateClassLoaders with " +
				"_classLoaderReferences and parent classloader",
			aggregateClassLoader.equals(
				AggregateClassLoader.getAggregateClassLoader(
					null, _testClassLoader1)));
	}

	@Test
	public void testFindClass() throws Exception {
		AggregateClassLoader aggregateClassLoader1 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testClassLoader1);

		Assert.assertSame(
			TestClassLoader.class,
			aggregateClassLoader1.findClass(TestClassLoader.class.getName()));

		// trigger NullPointerException

		Object findClassMethod = ReflectionTestUtil.getAndSetFieldValue(
			AggregateClassLoader.class, "_FIND_CLASS_METHOD", null);

		try {
			aggregateClassLoader1.findClass(TestClassLoader.class.getName());

			Assert.fail("ClassNotFoundException was not thrown");
		}
		catch (ClassNotFoundException cnfe) {
			Assert.assertEquals(
				"Unable to find class ".concat(TestClassLoader.class.getName()),
				cnfe.getMessage());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				AggregateClassLoader.class, "_FIND_CLASS_METHOD",
				findClassMethod);
		}

		// trigger InvocationTargetException

		AggregateClassLoader aggregateClassLoader2 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testExceptionClassLoader);

		try {
			aggregateClassLoader2.findClass(TestClassLoader.class.getName());

			Assert.fail("ClassNotFoundException was not thrown");
		}
		catch (ClassNotFoundException cnfe) {
			Assert.assertEquals(
				"Unable to find class ".concat(TestClassLoader.class.getName()),
				cnfe.getMessage());
		}
	}

	@Test
	public void testGetAggregateClassLoader() {

		// getAggregateClassLoader(ClassLoader[])

		Assert.assertNull(
			AggregateClassLoader.getAggregateClassLoader(new ClassLoader[0]));

		_assertAggregateClassLoader(
			_testClassLoader1, new ClassLoader[0],
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				new ClassLoader[] {_testClassLoader1}));

		_assertAggregateClassLoader(
			_testClassLoader1, new ClassLoader[] {_testClassLoader2},
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				new ClassLoader[] {_testClassLoader1, _testClassLoader2}));

		// getAggregateClassLoader(ClassLoader, ClassLoader...)

		Assert.assertSame(
			_testClassLoader1,
			AggregateClassLoader.getAggregateClassLoader(_testClassLoader1));

		_assertAggregateClassLoader(
			_testClassLoader1, new ClassLoader[] {_testClassLoader2},
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				_testClassLoader1, _testClassLoader2));

		_assertAggregateClassLoader(
			_testClassLoader1, new ClassLoader[0],
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				new AggregateClassLoader(_testClassLoader1),
				_testClassLoader1));

		_assertAggregateClassLoader(
			_testClassLoader1, new ClassLoader[] {_testClassLoader2},
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				AggregateClassLoader.getAggregateClassLoader(
					_testClassLoader1, _testClassLoader2),
				_testClassLoader2));

		_assertAggregateClassLoader(
			_testClassLoader1, new ClassLoader[0],
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				new AggregateClassLoader(_testClassLoader1),
				new AggregateClassLoader(_testClassLoader1)));

		_assertAggregateClassLoader(
			_testClassLoader1, new ClassLoader[] {_testClassLoader2},
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				new AggregateClassLoader(_testClassLoader1),
				new AggregateClassLoader(_testClassLoader2)));
	}

	@Test
	public void testGetClassLoaders() throws Exception {
		AggregateClassLoader aggregateClassLoader = new AggregateClassLoader(
			null);

		TestClassLoader testClassLoader = new TestClassLoader();

		aggregateClassLoader.addClassLoader(_testClassLoader1, testClassLoader);

		Assert.assertEquals(
			Arrays.asList(_testClassLoader1, testClassLoader),
			aggregateClassLoader.getClassLoaders());

		testClassLoader = null;

		GCUtil.gc(true);

		Assert.assertEquals(
			Arrays.asList(_testClassLoader1),
			aggregateClassLoader.getClassLoaders());
	}

	@Test
	public void testGetResource() throws Exception {
		AggregateClassLoader aggregateClassLoader1 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testClassLoader1);

		Assert.assertEquals(
			new URL("file:testGetResource"),
			aggregateClassLoader1.getResource(""));

		// trigger NullPointerException

		Object getResourceMethod = ReflectionTestUtil.getAndSetFieldValue(
			AggregateClassLoader.class, "_GET_RESOURCE_METHOD", null);

		try {
			Assert.assertNull(aggregateClassLoader1.getResource(""));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				AggregateClassLoader.class, "_GET_RESOURCE_METHOD",
				getResourceMethod);
		}

		// trigger InvocationTargetException

		AggregateClassLoader aggregateClassLoader2 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testExceptionClassLoader);

		Assert.assertNull(aggregateClassLoader2.getResource(""));
	}

	@Test
	public void testGetResources() throws Exception {
		AggregateClassLoader aggregateClassLoader1 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				_testClassLoader1, _testClassLoader2);

		Assert.assertEquals(
			Collections.emptyList(),
			Collections.list(aggregateClassLoader1.getResources("")));

		// trigger NullPointerException

		AggregateClassLoader aggregateClassLoader2 = new AggregateClassLoader(
			null);

		try {
			aggregateClassLoader2.getResources("");

			Assert.fail("IOException was not thrown");
		}
		catch (Exception e) {
			Assert.assertTrue(
				"getResources() throws NullPointerException if parent class " +
					"loader was null",
				e.getCause() instanceof NullPointerException);
		}

		// trigger InvocationTargetException

		AggregateClassLoader aggregateClassLoader3 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testExceptionClassLoader);

		try {
			aggregateClassLoader3.getResources("");

			Assert.fail("IOException was not thrown");
		}
		catch (Exception e) {
			Assert.assertTrue(
				"getResources() throws IOException if classloader's " +
					"getResources() throws IOException",
				e.getCause() instanceof IOException);
		}
	}

	@Test
	public void testHashCode() {
		AggregateClassLoader aggregateClassLoader1 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				_testClassLoader1, _testClassLoader2);
		AggregateClassLoader aggregateClassLoader2 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				_testClassLoader1, _testClassLoader2);

		Assert.assertEquals(
			"AggregateClassLoader.hashCode() should be equals with same " +
				"parent and aggregated class loaders",
			aggregateClassLoader1.hashCode(), aggregateClassLoader2.hashCode());

		AggregateClassLoader aggregateClassLoader3 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				_testClassLoader1, _testClassLoader1);

		Assert.assertNotEquals(
			"AggregateClassLoader.hashCode() should be different with " +
				"different aggregated class loaders",
			aggregateClassLoader1.hashCode(), aggregateClassLoader3.hashCode());

		AggregateClassLoader aggregateClassLoader4 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				_testClassLoader2, _testClassLoader2);

		Assert.assertNotEquals(
			"AggregateClassLoader.hashCode() should be different with " +
				"different parent class loaders",
			aggregateClassLoader1.hashCode(), aggregateClassLoader4.hashCode());

		AggregateClassLoader aggregateClassLoader5 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				_testClassLoader2, _testClassLoader1);

		Assert.assertNotEquals(
			"AggregateClassLoader.hashCode() should be different with " +
				"different parent and aggregated class loaders",
			aggregateClassLoader1.hashCode(), aggregateClassLoader5.hashCode());
	}

	@AdviseWith(adviceClasses = ReflectionUtilAdvice.class)
	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testInitializationFailure() throws Exception {
		Exception exception = new Exception();

		ReflectionUtilAdvice.setDeclaredMethodThrowable(exception);

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					LoggedExceptionInInitializerError.class.getName(),
					Level.SEVERE)) {

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());

			try {
				Class.forName(AggregateClassLoader.class.getName());

				Assert.fail("LoggedExceptionInInitializerError was not thrown");
			}
			catch (LoggedExceptionInInitializerError leiie) {
				Assert.assertSame(exception, leiie.getCause());

				Assert.assertEquals(
					logRecords.toString(), 1, logRecords.size());

				LogRecord logRecord = logRecords.get(0);

				Assert.assertSame(
					Exception.class.getName(), logRecord.getMessage());
			}
		}
	}

	@Test
	public void testLoadClass() throws Exception {
		AggregateClassLoader aggregateClassLoader1 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testClassLoader1);

		Assert.assertSame(
			TestClassLoader.class,
			aggregateClassLoader1.loadClass(
				TestClassLoader.class.getName(), true));
		Assert.assertSame(
			TestClassLoader.class,
			aggregateClassLoader1.loadClass(
				TestClassLoader.class.getName(), false));

		// trigger NullPointerException

		AggregateClassLoader aggregateClassLoader2 = new AggregateClassLoader(
			null);

		try {
			aggregateClassLoader2.loadClass(
				TestClassLoader.class.getName(), false);

			Assert.fail("ClassNotFoundException was not thrown");
		}
		catch (ClassNotFoundException cnfe) {
			Assert.assertEquals(
				"Unable to load class ".concat(TestClassLoader.class.getName()),
				cnfe.getMessage());
		}

		// trigger InvocationTargetException

		AggregateClassLoader aggregateClassLoader3 =
			(AggregateClassLoader)AggregateClassLoader.getAggregateClassLoader(
				null, _testExceptionClassLoader);

		try {
			aggregateClassLoader3.loadClass(
				TestClassLoader.class.getName(), false);

			Assert.fail("ClassNotFoundException was not thrown");
		}
		catch (ClassNotFoundException cnfe) {
			Assert.assertEquals(
				"Unable to load class ".concat(TestClassLoader.class.getName()),
				cnfe.getMessage());
		}
	}

	private void _assertAggregateClassLoader(
		ClassLoader expectedParentClassLoader,
		ClassLoader[] expectedClassLoaders,
		AggregateClassLoader aggregateClassLoader) {

		Assert.assertSame(
			expectedParentClassLoader, aggregateClassLoader.getParent());

		_assertAggregatedClassLoaders(
			expectedClassLoaders, aggregateClassLoader);
	}

	private void _assertAggregatedClassLoaders(
		ClassLoader[] expectedClassLoaders,
		AggregateClassLoader aggregateClassLoader) {

		List<EqualityWeakReference<ClassLoader>> classLoaderReferences =
			ReflectionTestUtil.getFieldValue(
				aggregateClassLoader, "_classLoaderReferences");

		List<ClassLoader> classLoaders = new ArrayList<>(
			classLoaderReferences.size());

		for (EqualityWeakReference<ClassLoader> classLoaderReference :
				classLoaderReferences) {

			classLoaders.add(classLoaderReference.get());
		}

		Assert.assertArrayEquals(expectedClassLoaders, classLoaders.toArray());
	}

	private void _testAddClassLoader(
		ClassLoader[] expectedClassLoaders, ClassLoader[] classLoaders,
		ClassLoader parentClassLoader) {

		AggregateClassLoader aggregateClassLoader1 = new AggregateClassLoader(
			parentClassLoader);

		aggregateClassLoader1.addClassLoader(classLoaders);

		_assertAggregatedClassLoaders(
			expectedClassLoaders, aggregateClassLoader1);

		AggregateClassLoader aggregateClassLoader2 = new AggregateClassLoader(
			parentClassLoader);

		aggregateClassLoader2.addClassLoader(Arrays.asList(classLoaders));

		_assertAggregatedClassLoaders(
			expectedClassLoaders, aggregateClassLoader2);
	}

	private final TestClassLoader _testClassLoader1 = new TestClassLoader();
	private final TestClassLoader _testClassLoader2 = new TestClassLoader();
	private final TestExceptionClassLoader _testExceptionClassLoader =
		new TestExceptionClassLoader();

	private class TestClassLoader extends ClassLoader {

		@Override
		public URL getResource(String name) {
			try {
				return new URL("file:testGetResource");
			}
			catch (MalformedURLException murle) {
				throw new RuntimeException(murle);
			}
		}

		@Override
		public Enumeration<URL> getResources(String name) {
			return Collections.emptyEnumeration();
		}

		@Override
		protected Class<?> findClass(String name) {
			if (name.equals(TestClassLoader.class.getName())) {
				return TestClassLoader.class;
			}

			return null;
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) {
			return TestClassLoader.class;
		}

	}

	private class TestExceptionClassLoader extends ClassLoader {

		@Override
		public URL getResource(String name) {
			throw new RuntimeException();
		}

		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			throw new IOException();
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {

			throw new ClassNotFoundException();
		}

	}

}