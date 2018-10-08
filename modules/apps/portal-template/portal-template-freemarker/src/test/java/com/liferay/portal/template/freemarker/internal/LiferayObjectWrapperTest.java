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
	public void testRestrictions() throws Exception {

		// Test 1, if neither allowedClassNames nor restrictedClassNames is
		// provided, the object can be wrapped

		_testWrapLiferayObject(new LiferayObjectWrapper(null, null));

		// Test 2, if "*" exists, allow all classes and ignore restriction

		String[] allowedClassNames = {StringPool.STAR};
		String[] restrictedClassNames = {TestLiferayObject.class.getName()};

		_testWrapLiferayObject(
			new LiferayObjectWrapper(allowedClassNames, restrictedClassNames));

		// Test 3, allowed class names takes precedence over restricted class
		// names

		allowedClassNames[0] = TestLiferayObject.class.getName();

		_testWrapLiferayObject(
			new LiferayObjectWrapper(allowedClassNames, restrictedClassNames));

		// Test 4, TestLiferayObject is not restricted by class name

		restrictedClassNames[0] = "java.lang.String";

		_testWrapLiferayObject(
			new LiferayObjectWrapper(null, restrictedClassNames));

		// Test 5, TestLiferayObject is not restricted by package name

		restrictedClassNames[0] = "com.liferay.portal.cache";

		_testWrapLiferayObject(
			new LiferayObjectWrapper(null, restrictedClassNames));

		// Test 6, TestLiferayObject is restricted by class name

		restrictedClassNames[0] = TestLiferayObject.class.getName();

		try {
			_testWrapLiferayObject(
				new LiferayObjectWrapper(null, restrictedClassNames));

			Assert.fail("TemplateModelException was not thrown");
		}
		catch (TemplateModelException tme) {
			Assert.assertEquals(
				StringBundler.concat(
					"Denied resolving class ",
					TestLiferayObject.class.getName(), " by ",
					TestLiferayObject.class.getName()),
				tme.getMessage());
		}

		// Test 7, TestLiferayObject is restricted by package name

		restrictedClassNames[0] = "com.liferay.portal.template.freemarker";

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, restrictedClassNames);

		try {
			_testWrapLiferayObject(liferayObjectWrapper);

			Assert.fail("TemplateModelException was not thrown");
		}
		catch (TemplateModelException tme) {
			Assert.assertEquals(
				StringBundler.concat(
					"Denied resolving class ",
					TestLiferayObject.class.getName(), " by ",
					restrictedClassNames[0]),
				tme.getMessage());
		}

		// test 8, a class without package is considered allowed

		Method checkClassIsRestrictedMethod = ReflectionTestUtil.getMethod(
			LiferayObjectWrapper.class, "_checkClassIsRestricted", Class.class);

		checkClassIsRestrictedMethod.invoke(liferayObjectWrapper, byte.class);
	}

	@Test
	public void testHandleUnknownTypeEnumeration()throws Exception {
		List<String> list = new ArrayList<>();

		String testElement = "testElement";

		list.add(testElement);

		Enumeration<String> enumeration = Collections.enumeration(list);

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			enumeration);

		Assert.assertTrue(
			"Enumeration should be handled as EnumerationModel",
			templateModel instanceof EnumerationModel);

		_assertModelFactoryCache(
			"_ENUMERATION_MODEL_FACTORY", enumeration.getClass());

		EnumerationModel enumerationModel = (EnumerationModel)templateModel;

		TemplateModel nextTemplateModel = enumerationModel.next();

		Assert.assertEquals(testElement, nextTemplateModel.toString());
	}

	@Test
	public void testHandleUnknownTypeNode() throws Exception {
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

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			node);

		Assert.assertTrue(
			"org.w3c.dom.Node should be handled as NodeModel",
			templateModel instanceof NodeModel);

		_assertModelFactoryCache("_NODE_MODEL_FACTORY", node.getClass());

		NodeModel nodeModel = (NodeModel)templateModel;

		Assert.assertSame(node, nodeModel.getNode());
		Assert.assertEquals("element", nodeModel.getNodeType());
	}

	@Test
	public void testHandleUnknownTypeResourceBundle() {
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

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			resourceBundle);

		Assert.assertTrue(
			"ResourceBundle should be handled as ResourceBundleModel",
			templateModel instanceof ResourceBundleModel);

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
	public void testInitializationFailure() throws Exception {
		Exception exception = new Exception();

		ReflectionUtilAdvice.setDeclaredFieldThrowable(exception);

		try {
			Class.forName(
				"com.liferay.portal.template.freemarker.internal." +
					"LiferayObjectWrapper");

			Assert.fail("ExceptionInInitializerError was not thrown");
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertSame(exception, eiie.getCause());
		}
	}

	@Test
	public void testConstructor() {

		// Test 1, blank string is ignored

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			new String[] {StringPool.BLANK, TestLiferayObject.class.getName()},
			new String[] {StringPool.BLANK, TestLiferayMap.class.getName()});

		List<String> allowedClassNames = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_allowedClassNames");
		List<Class<?>> restrictedClasses = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_restrictedClasses");

		Assert.assertEquals(
			allowedClassNames.toString(), 1, allowedClassNames.size());
		Assert.assertEquals(
			restrictedClasses.toString(), 1, restrictedClasses.size());
		Assert.assertTrue(
			"_allowedClassNames should contain ".concat(
				TestLiferayObject.class.getName()),
			allowedClassNames.contains(TestLiferayObject.class.getName()));
		Assert.assertTrue(
			"_restrictedClasses should contain ".concat(
				TestLiferayMap.class.getName()),
			restrictedClasses.contains(TestLiferayMap.class));

		// Test 2, a restricted class name unable to be loaded as class is
		// registered as restricted package name with log output

		String restrictedPackageName = "com.liferay.package.name";

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					LiferayObjectWrapper.class.getName(), Level.INFO)) {

			liferayObjectWrapper = new LiferayObjectWrapper(
				null, new String[] {restrictedPackageName});

			restrictedClasses = ReflectionTestUtil.getFieldValue(
				liferayObjectWrapper, "_restrictedClasses");

			Assert.assertTrue(
				restrictedClasses.toString(), restrictedClasses.isEmpty());

			List<String> restrictedPackageNames =
				ReflectionTestUtil.getFieldValue(
					liferayObjectWrapper, "_restrictedPackageNames");

			Assert.assertEquals(
				restrictedPackageNames.toString(), 1,
				restrictedPackageNames.size());
			Assert.assertTrue(
				"_restrictedPackageNames should contain ".concat(
					restrictedPackageName),
				restrictedPackageNames.contains(restrictedPackageName));

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to find restricted class ",
					restrictedPackageName, ". Registering as a package."),
				logRecord.getMessage());

			// Logging level OFF

			captureHandler.resetLogLevel(Level.OFF);

			new LiferayObjectWrapper(
				null, new String[] {restrictedPackageName});

			logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorWithException() {
		Field cacheClassNamesField = ReflectionTestUtil.getAndSetFieldValue(
			LiferayObjectWrapper.class, "_cacheClassNamesField", null);

		try {
			new LiferayObjectWrapper(null, null);
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

		Assert.assertTrue(
			"Liferay collection implementation should be wrapped as" +
				"SimpleSequence",
			templateModel instanceof SimpleSequence);

		SimpleSequence simpleSequence = (SimpleSequence)templateModel;

		TemplateModel elementTemplateModel = simpleSequence.get(0);

		Assert.assertEquals(testElement, elementTemplateModel.toString());
	}

	@Test
	public void testWrapLiferayMap() throws Exception {
		TestLiferayMap testLiferayMap = new TestLiferayMap();

		String testKey = "testKey";
		String testValue = "testValue";

		testLiferayMap.put(testKey, testValue);

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel templateModel = liferayObjectWrapper.wrap(testLiferayMap);

		Assert.assertTrue(
			"Liferay map implementation should be wrapped as MapModel",
			templateModel instanceof MapModel);

		MapModel mapModel = (MapModel)templateModel;

		TemplateModel testValueModel = mapModel.get(testKey);

		Assert.assertEquals(testValue, testValueModel.toString());
	}

	@Test
	public void testWrapLiferayTemplateNode() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel templateModel = liferayObjectWrapper.wrap(
			new TemplateNode(null, "testName", "", "", null));

		Assert.assertTrue(
			"TemplateNode should be wrapped as LiferayTemplateModel",
			templateModel instanceof LiferayTemplateModel);

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

		Assert.assertTrue(
			"Unknown type (java.lang.Thread) should be wrapped as StringModel",
			templateModel instanceof StringModel);

		_assertModelFactoryCache("_STRING_MODEL_FACTORY", thread.getClass());

		StringModel stringModel = (StringModel)templateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());

		// Test 2, wrap the same type again

		thread = new Thread("testThread2");

		templateModel = liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknownTypeCount.get());

		Assert.assertTrue(
			"Unknown type (java.lang.Thread) should be wrapped as StringModel",
			templateModel instanceof StringModel);

		stringModel = (StringModel)templateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
	}

	private void _assertModelFactoryCache(
		String modelFactoryFieldName, Class<?> clazz) {

		Map<Class<?>, ModelFactory> modelFactories =
			ReflectionTestUtil.getFieldValue(
				LiferayObjectWrapper.class, "_modelFactories");

		Assert.assertSame(
			ReflectionTestUtil.getFieldValue(
				LiferayObjectWrapper.class, modelFactoryFieldName),
			modelFactories.get(clazz));
	}

	private void _testWrapLiferayObject(
			LiferayObjectWrapper liferayObjectWrapper)
		throws Exception {

		TestLiferayObject testLiferayObject = new TestLiferayObject();

		TemplateModel templateModel = liferayObjectWrapper.wrap(
			testLiferayObject);

		Assert.assertTrue(
			"Liferay classes should be wrapped as StringModel",
			templateModel instanceof StringModel);

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