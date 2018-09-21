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

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.aspects.ReflectionUtilAdvice;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.AspectJNewEnvTestRule;

import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.dom.NodeModel;
import freemarker.ext.util.ModelFactory;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Xiangyue Cai
 */
public class LiferayObjectWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			AspectJNewEnvTestRule.INSTANCE, CodeCoverageAssertor.INSTANCE);

	@Test
	public void testCheckClassIsRestricted() throws Exception {

		// Test 1, if "*" exists, allow all classes and ignore restriction

		String[] allowedClassNames = {StringPool.STAR};
		String[] restrictedClassNames = {TestLiferayObject.class.getName()};

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			allowedClassNames, restrictedClassNames);

		_testLiferayObject(liferayObjectWrapper);

		// Test 2, allowed class names takes precedence over restricted class
		// names

		allowedClassNames[0] = TestLiferayObject.class.getName();

		liferayObjectWrapper = new LiferayObjectWrapper(
			allowedClassNames, restrictedClassNames);

		_testLiferayObject(liferayObjectWrapper);

		// Test 3, TestLiferayObject is restricted by class name

		restrictedClassNames[0] = "java.lang.String";

		liferayObjectWrapper = new LiferayObjectWrapper(
			null, restrictedClassNames);

		_testLiferayObject(liferayObjectWrapper);

		// Test 4, TestLiferayObject is not restricted by package name

		restrictedClassNames[0] = "com.liferay.portal.cache";

		liferayObjectWrapper = new LiferayObjectWrapper(
			null, restrictedClassNames);

		_testLiferayObject(liferayObjectWrapper);

		// Test 5, TestLiferayObject is restricted by class name

		restrictedClassNames[0] = TestLiferayObject.class.getName();

		liferayObjectWrapper = new LiferayObjectWrapper(
			null, restrictedClassNames);

		try {
			_testLiferayObject(liferayObjectWrapper);

			Assert.fail("No exception thrown!");
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof TemplateModelException);

			Assert.assertEquals(
				StringBundler.concat(
					"Denied resolving class ",
					TestLiferayObject.class.getName(), " by ",
					TestLiferayObject.class.getName()),
				e.getMessage());
		}

		// Test 6, TestLiferayObject is restricted by package name

		restrictedClassNames[0] = "com.liferay.portal.template.freemarker";

		liferayObjectWrapper = new LiferayObjectWrapper(
			null, restrictedClassNames);

		try {
			_testLiferayObject(liferayObjectWrapper);

			Assert.fail("No exception thrown!");
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof TemplateModelException);

			Assert.assertEquals(
				StringBundler.concat(
					"Denied resolving class ",
					TestLiferayObject.class.getName(), " by ",
					restrictedClassNames[0]),
				e.getMessage());
		}

		// test 7, byte.class.getName() return "byte" does not contain period

		Method checkClassIsRestricted = ReflectionTestUtil.getMethod(
			LiferayObjectWrapper.class, "_checkClassIsRestricted", Class.class);

		checkClassIsRestricted.invoke(liferayObjectWrapper, byte.class);
	}

	@Test
	public void testHandleUnknownTypeEnumeration()throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		List<String> list = new ArrayList<>();

		String testElement = "testElement";

		list.add(testElement);

		Enumeration<String> enumeration = Collections.enumeration(list);

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			enumeration);

		Assert.assertTrue(templateModel instanceof EnumerationModel);

		_assertModelFactoryCache(
			"_ENUMERATION_MODEL_FACTORY", enumeration.getClass());

		EnumerationModel enumerationModel = (EnumerationModel)templateModel;

		TemplateModel nextTemplateModel = enumerationModel.next();

		Assert.assertEquals(testElement, nextTemplateModel.toString());
	}

	@Test
	public void testHandleUnknownTypeNode() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		Node node = (Node)ProxyUtil.newProxyInstance(
			LiferayObjectWrapper.class.getClassLoader(),
			new Class<?>[] {Node.class, Element.class},
			(proxy, method, args) -> {
				String methodName = method.getName();

				if (methodName.equals("getNodeType")) {
					return Node.ELEMENT_NODE;
				}

				return null;
			});

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			node);

		Assert.assertTrue(templateModel instanceof NodeModel);

		_assertModelFactoryCache("_NODE_MODEL_FACTORY", node.getClass());

		NodeModel nodeModel = (NodeModel)templateModel;

		Assert.assertSame(node, nodeModel.getNode());
		Assert.assertEquals("element", nodeModel.getNodeType());
	}

	@Test
	public void testHandleUnknownTypeResourceBundle() {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		ResourceBundle resourceBundle = new ResourceBundle() {

			@Override
			public Enumeration<String> getKeys() {
				return null;
			}

			@Override
			protected Object handleGetObject(String key) {
				return null;
			}

		};

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			resourceBundle);

		Assert.assertTrue(templateModel instanceof ResourceBundleModel);

		_assertModelFactoryCache(
			"_RESOURCE_BUNDLE_MODEL_FACTORY", resourceBundle.getClass());

		ResourceBundleModel resourceBundleModel =
			(ResourceBundleModel)templateModel;

		ResourceBundle handledResourceBundle = resourceBundleModel.getBundle();

		Assert.assertNull(handledResourceBundle.getKeys());
	}

	@AdviseWith(adviceClasses = ReflectionUtilAdvice.class)
	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testInitializationFailure() {
		Exception exception = new Exception();

		ReflectionUtilAdvice.setDeclaredFieldThrowable(exception);

		try {
			Class.forName(
				"com.liferay.portal.template.freemarker.internal." +
					"LiferayObjectWrapper");

			Assert.fail("No exception thrown!");
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertSame(exception, eiie.getCause());
		}
		catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Test
	public void testLiferayObjectWrapperConstructor()
		throws ClassNotFoundException {

		// Test 1, allowedClassNames and restrictedClassNames are not provided

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		List<String> allowedClassNames = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_allowedClassNames");
		List<Class<?>> restrictedClasses = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_restrictedClasses");
		List<String> restrictedPackageNames = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_restrictedPackageNames");

		Assert.assertTrue(allowedClassNames.isEmpty());
		Assert.assertTrue(restrictedClasses.isEmpty());
		Assert.assertTrue(restrictedPackageNames.isEmpty());

		// Test 2, allowedClassNames has "*"

		String[] testAllowedClassNames =
			{StringPool.STAR, StringPool.BLANK, "test.allow.class"};

		liferayObjectWrapper = new LiferayObjectWrapper(
			testAllowedClassNames, null);

		allowedClassNames = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_allowedClassNames");

		Assert.assertTrue(allowedClassNames.contains(StringPool.STAR));
		Assert.assertTrue(allowedClassNames.contains("test.allow.class"));
		Assert.assertFalse(allowedClassNames.contains(StringPool.BLANK));

		Assert.assertTrue(
			ReflectionTestUtil.getFieldValue(
				liferayObjectWrapper, "_allowAllClasses"));

		// Test 3, allowedClassNames doesn't have "*"

		testAllowedClassNames[0] = "test.not.contain.star";

		liferayObjectWrapper = new LiferayObjectWrapper(
			testAllowedClassNames, null);

		Assert.assertFalse(
			ReflectionTestUtil.getFieldValue(
				liferayObjectWrapper, "_allowAllClasses"));

		// Test 4, restrictedClassNames

		String[] testRestrictedClassNames = {
			"com.liferay.portal.template.ServiceLocator", StringPool.BLANK,
			"test.not.found.class.exception"
		};

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					LiferayObjectWrapper.class.getName(), Level.OFF)) {

			liferayObjectWrapper = new LiferayObjectWrapper(
				null, testRestrictedClassNames);

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());

			restrictedClasses = ReflectionTestUtil.getFieldValue(
				liferayObjectWrapper, "_restrictedClasses");

			Assert.assertEquals(
				restrictedClasses.toString(), 1, restrictedClasses.size());
			Assert.assertTrue(
				restrictedClasses.contains(
					Class.forName(testRestrictedClassNames[0])));

			restrictedPackageNames = ReflectionTestUtil.getFieldValue(
				liferayObjectWrapper, "_restrictedPackageNames");

			Assert.assertEquals(
				restrictedPackageNames.toString(), 1,
				restrictedPackageNames.size());
			Assert.assertTrue(
				restrictedPackageNames.contains(testRestrictedClassNames[2]));

			captureHandler.resetLogLevel(Level.INFO);

			new LiferayObjectWrapper(null, testRestrictedClassNames);

			logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to find restricted class ",
					testRestrictedClassNames[2], ". Registering as a package."),
				logRecord.getMessage());
		}
	}

	@Test
	public void testLiferayObjectWrapperConstructorWithException() {
		Field cacheClassNamesField = ReflectionTestUtil.getAndSetFieldValue(
			LiferayObjectWrapper.class, "_cacheClassNamesField", null);

		try {
			new LiferayObjectWrapper(null, null);

			Assert.fail("No exception thrown!");
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof NullPointerException);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				LiferayObjectWrapper.class, "_cacheClassNamesField",
				cacheClassNamesField);
		}
	}

	@Test
	public void testWrapLiferayCollection() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TestLiferayCollection testLiferayCollection =
			new TestLiferayCollection();

		String testElement = "testElement";

		testLiferayCollection.add(testElement);

		TemplateModel templateModel = liferayObjectWrapper.wrap(
			testLiferayCollection);

		Assert.assertTrue(templateModel instanceof SimpleSequence);

		SimpleSequence simpleSequence = (SimpleSequence)templateModel;

		TemplateModel elementTemplateModel = simpleSequence.get(0);

		Assert.assertEquals(testElement, elementTemplateModel.toString());
	}

	@Test
	public void testWrapLiferayMap() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TestLiferayMap testLiferayMap = new TestLiferayMap();

		String testKey = "testKey";
		String testValue = "testValue";

		testLiferayMap.put(testKey, testValue);

		TemplateModel templateModel = liferayObjectWrapper.wrap(testLiferayMap);

		Assert.assertTrue(templateModel instanceof MapModel);

		MapModel mapModel = (MapModel)templateModel;

		TemplateModel testValueModel = mapModel.get(testKey);

		Assert.assertEquals(testValue, testValueModel.toString());
	}

	@Test
	public void testWrapLiferayObject() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		_testLiferayObject(liferayObjectWrapper);
	}

	@Test
	public void testWrapLiferayTemplateNode() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel templateModel = liferayObjectWrapper.wrap(
			new TemplateNode(null, "testName", "", "", null));

		Assert.assertTrue(templateModel instanceof LiferayTemplateModel);

		LiferayTemplateModel liferayTemplateModel =
			(LiferayTemplateModel)templateModel;

		TemplateModel nameTemplateModel = liferayTemplateModel.get("name");

		Assert.assertEquals("testName", nameTemplateModel.toString());
	}

	@Test
	public void testWrapNull() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		Assert.assertNull(liferayObjectWrapper.wrap(null));
	}

	@Test
	public void testWrapTemplateModel() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel originalTemplateModel =
			(TemplateModel)ProxyUtil.newProxyInstance(
				LiferayObjectWrapper.class.getClassLoader(),
				new Class<?>[] {TemplateModel.class},
				(proxy, method, args) -> null);

		Assert.assertSame(
			originalTemplateModel,
			liferayObjectWrapper.wrap(originalTemplateModel));
	}

	@Test
	public void testWrapUnknownType() throws Exception {
		AtomicInteger handleUnknownTypeCount = new AtomicInteger(0);

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null) {

			@Override
			protected TemplateModel handleUnknownType(Object object) {
				handleUnknownTypeCount.incrementAndGet();

				return super.handleUnknownType(object);
			}

		};

		Assert.assertEquals(0, handleUnknownTypeCount.get());

		// Test 1, wrap unkown type for the first time

		Thread thread = new Thread("testThread1");

		TemplateModel templateModel = liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknownTypeCount.get());

		Assert.assertTrue(templateModel instanceof StringModel);

		_assertModelFactoryCache("_STRING_MODEL_FACTORY", thread.getClass());

		StringModel stringModel = (StringModel)templateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());

		// Test 2, wrap the same type again

		thread = new Thread("testThread2");

		templateModel = liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknownTypeCount.get());

		Assert.assertTrue(templateModel instanceof StringModel);

		stringModel = (StringModel)templateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
	}

	private void _assertModelFactoryCache(
		String modelFactoryFieldName, Class<?> clazz) {

		ModelFactory modelFactory = ReflectionTestUtil.getFieldValue(
			LiferayObjectWrapper.class, modelFactoryFieldName);

		Map<Class<?>, ModelFactory> modelFactories =
			ReflectionTestUtil.getFieldValue(
				LiferayObjectWrapper.class, "_modelFactories");

		Assert.assertSame(modelFactory, modelFactories.get(clazz));
	}

	private void _testLiferayObject(LiferayObjectWrapper liferayObjectWrapper)
		throws Exception {

		TestLiferayObject testLiferayObject = new TestLiferayObject();

		TemplateModel templateModel = liferayObjectWrapper.wrap(
			testLiferayObject);

		Assert.assertTrue(templateModel instanceof StringModel);

		StringModel stringModel = (StringModel)templateModel;

		Assert.assertEquals(
			testLiferayObject.toString(), stringModel.getAsString());
	}

	private class TestLiferayCollection extends ArrayList<Object> {
	}

	private class TestLiferayMap extends HashMap<String, Object> {
	}

	private class TestLiferayObject {
	}

}