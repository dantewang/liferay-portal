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
import com.liferay.portal.kernel.log.Log;
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
		TestLiferayMap testLiferayMap = new TestLiferayMap();
		String[] testAllowedClassNames = new String[1];

		testAllowedClassNames[0] = StringPool.STAR;

		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			testAllowedClassNames, null);

		liferayObjectWrapper.wrap(testLiferayMap);

		Class<?> clazz = TestLiferayMap.class;

		String className = clazz.getName();

		testAllowedClassNames[0] = className;

		liferayObjectWrapper = new LiferayObjectWrapper(
			testAllowedClassNames, null);

		liferayObjectWrapper.wrap(testLiferayMap);

		String[] restrictedClassNames = new String[2];

		restrictedClassNames[0] = "java.lang.String";
		restrictedClassNames[1] = className;

		liferayObjectWrapper = new LiferayObjectWrapper(
			null, restrictedClassNames);

		try {
			liferayObjectWrapper.wrap(testLiferayMap);

			Assert.fail("No exception thrown!");
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof TemplateModelException);

			Assert.assertEquals(
				StringBundler.concat(
					"Denied resolving class ", className, " by ", className),
				e.getMessage());
		}

		String restrictedPackageName = "java.lang";
		String testRestrictedPackage = "testRestrictedPackage";

		try {
			restrictedClassNames[0] = "test.not.start.with";
			restrictedClassNames[1] = restrictedPackageName;

			liferayObjectWrapper = new LiferayObjectWrapper(
				null, restrictedClassNames);

			liferayObjectWrapper.wrap(testRestrictedPackage);

			Assert.fail("No exception thrown!");
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof TemplateModelException);

			Class<?> restrictedClass = testRestrictedPackage.getClass();

			Assert.assertEquals(
				StringBundler.concat(
					"Denied resolving class ", restrictedClass.getName(),
					" by ", restrictedPackageName),
				e.getMessage());
		}
	}

	@Test
	public void testHandleUnknownTypeByEnumeration() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		List<String> list = new ArrayList<>();

		String testElement = "testElement";

		list.add(testElement);

		Enumeration<String> enumeration = Collections.enumeration(list);

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			enumeration);

		Assert.assertTrue(templateModel instanceof EnumerationModel);

		EnumerationModel enumerationModel = (EnumerationModel)templateModel;

		TemplateModel nextTemplateModel = enumerationModel.next();

		Assert.assertEquals(testElement, nextTemplateModel.toString());

		Map<Class<?>, ModelFactory> modelFactories =
			ReflectionTestUtil.getFieldValue(
				LiferayObjectWrapper.class, "_modelFactories");

		Assert.assertNotNull(modelFactories.get(enumeration.getClass()));
	}

	@Test
	public void testHandleUnknownTypeByNode() throws Exception {
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

		NodeModel nodeModel = (NodeModel)templateModel;

		Assert.assertSame(node, nodeModel.getNode());

		Assert.assertEquals("element", nodeModel.getNodeType());

		Map<Class<?>, ModelFactory> modelFactories =
			ReflectionTestUtil.getFieldValue(
				LiferayObjectWrapper.class, "_modelFactories");

		Assert.assertNotNull(modelFactories.get(node.getClass()));
	}

	@Test
	public void testHandleUnknownTypeByResourceBundle() {
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

		ResourceBundleModel resourceBundleModel =
			(ResourceBundleModel)templateModel;

		ResourceBundle handledResourceBundle = resourceBundleModel.getBundle();

		Assert.assertNull(handledResourceBundle.getKeys());

		Map<Class<?>, ModelFactory> modelFactories =
			ReflectionTestUtil.getFieldValue(
				LiferayObjectWrapper.class, "_modelFactories");

		Assert.assertNotNull(modelFactories.get(resourceBundle.getClass()));
	}

	@AdviseWith(adviceClasses = ReflectionUtilAdvice.class)
	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testInitializationFailure() {
		Exception exception = new Exception();

		ReflectionUtilAdvice.setDeclaredFieldThrowable(exception);

		try {
			Class<?> clazz = Class.forName(
				"com.liferay.portal.template.freemarker.internal." +
					"LiferayObjectWrapper");

			clazz.newInstance();

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

		final int testSize = 3;

		String[] testAllowedClassNames = new String[testSize];
		String[] testRestrictedClassNames = new String[testSize];

		final int all = 0;
		final int blank = 1;
		final int allow = 2;
		testAllowedClassNames[all] = StringPool.STAR;
		testAllowedClassNames[blank] = StringPool.BLANK;
		testAllowedClassNames[allow] = "test.allow.class";

		liferayObjectWrapper = new LiferayObjectWrapper(
			testAllowedClassNames, null);

		allowedClassNames = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_allowedClassNames");

		Assert.assertTrue(
			allowedClassNames.contains(testAllowedClassNames[all]));
		Assert.assertFalse(
			allowedClassNames.contains(testAllowedClassNames[blank]));
		Assert.assertTrue(
			allowedClassNames.contains(testAllowedClassNames[allow]));

		Boolean allowAllClasses = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_allowAllClasses");

		Assert.assertTrue(allowAllClasses);

		testAllowedClassNames[all] = "test.not.contain.star";

		liferayObjectWrapper = new LiferayObjectWrapper(
			testAllowedClassNames, null);

		allowAllClasses = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_allowAllClasses");

		Assert.assertFalse(allowAllClasses);

		final int restrict = 0;
		final int exception = 2;
		testRestrictedClassNames[restrict] =
			"com.liferay.portal.template.ServiceLocator";
		testRestrictedClassNames[blank] = StringPool.BLANK;
		testRestrictedClassNames[exception] = "test.not.found.class.exception";

		try (CaptureHandler captureHandler =
				JDKLoggerTestUtil.configureJDKLogger(
					LiferayObjectWrapper.class.getName(), Level.ALL)) {

			Log log = (Log)ProxyUtil.newProxyInstance(
				Log.class.getClassLoader(), new Class<?>[] {Log.class},
				(proxy, method, args) -> false);

			log = ReflectionTestUtil.getAndSetFieldValue(
				LiferayObjectWrapper.class, "_log", log);

			new LiferayObjectWrapper(null, testRestrictedClassNames);

			List<LogRecord> logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 0, logRecords.size());

			ReflectionTestUtil.setFieldValue(
				LiferayObjectWrapper.class, "_log", log);

			liferayObjectWrapper = new LiferayObjectWrapper(
				null, testRestrictedClassNames);

			logRecords = captureHandler.getLogRecords();

			Assert.assertEquals(logRecords.toString(), 1, logRecords.size());

			LogRecord logRecord = logRecords.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to find restricted class ",
					testRestrictedClassNames[exception],
					". Registering as a package."),
				logRecord.getMessage());
		}

		restrictedClasses = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_restrictedClasses");

		Assert.assertTrue(
			restrictedClasses.contains(
				Class.forName(testRestrictedClassNames[restrict])));

		Assert.assertEquals(
			restrictedClasses.toString(), 1, restrictedClasses.size());

		restrictedPackageNames = ReflectionTestUtil.getFieldValue(
			liferayObjectWrapper, "_restrictedPackageNames");

		Assert.assertFalse(
			restrictedPackageNames.contains(
				testRestrictedClassNames[restrict]));

		Assert.assertTrue(
			restrictedPackageNames.contains(
				testRestrictedClassNames[exception]));

		Assert.assertEquals(
			restrictedPackageNames.toString(), 1,
			restrictedPackageNames.size());
	}

	@Test
	public void testLiferayObjectWrapperConstructorThrowException() {
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
	public void testWrapByCollectionStartWithLiferay() throws Exception {
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
	public void testWrapByHandleUnknownTypeCache() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper;

		AtomicInteger handleUnknownTypeCount = new AtomicInteger(0);

		liferayObjectWrapper = new LiferayObjectWrapper(null, null) {

			@Override
			protected TemplateModel handleUnknownType(Object object) {
				handleUnknownTypeCount.incrementAndGet();

				return super.handleUnknownType(object);
			}

		};

		Thread thread = new Thread("testThread");

		TemplateModel templateModel = liferayObjectWrapper.wrap(thread);

		Assert.assertTrue(templateModel instanceof StringModel);

		Assert.assertEquals(1, handleUnknownTypeCount.get());

		templateModel = liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknownTypeCount.get());

		StringModel stringModel = (StringModel)templateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
	}

	@Test
	public void testWrapByMapStartWithLiferay() throws Exception {
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
	public void testWrapByNull() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel templateModel = liferayObjectWrapper.wrap(null);

		Assert.assertNull(templateModel);
	}

	@Test
	public void testWrapByTemplateModel() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

		TemplateModel oldTemplateModel =
			(TemplateModel)ProxyUtil.newProxyInstance(
				LiferayObjectWrapper.class.getClassLoader(),
				new Class<?>[] {TemplateModel.class},
				(proxy, method, args) -> null);

		TemplateModel newTemplateModel = liferayObjectWrapper.wrap(
			oldTemplateModel);

		Assert.assertSame(oldTemplateModel, newTemplateModel);
	}

	@Test
	public void testWrapByTemplateNodeStartWithLiferay() throws Exception {
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
	public void testWrapByUnknownClassStartWithLiferay() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(
			null, null);

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