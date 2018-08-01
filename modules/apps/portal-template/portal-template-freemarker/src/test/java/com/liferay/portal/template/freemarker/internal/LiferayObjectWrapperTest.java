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

import com.liferay.portal.kernel.templateparser.TemplateNode;
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

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

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
	public void testHandleUnknownType() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper();

		// Node object

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

		TemplateModel result = liferayObjectWrapper.handleUnknownType(node);

		Assert.assertTrue(result instanceof NodeModel);

		NodeModel nodeModel = (NodeModel)result;

		Assert.assertSame(node, nodeModel.getNode());
		Assert.assertEquals("element", nodeModel.getNodeType());

		// TemplateNode object

		TemplateNode templateNode = new TemplateNode(
			null, "testName", "", "", null);

		result = liferayObjectWrapper.handleUnknownType(templateNode);

		Assert.assertTrue(result instanceof LiferayTemplateModel);

		LiferayTemplateModel liferayTemplateModel =
			(LiferayTemplateModel)result;

		Assert.assertEquals(
			"testName", liferayTemplateModel.get("name").toString());

		// ResourceBundle object

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

		result = liferayObjectWrapper.handleUnknownType(resourceBundle);

		Assert.assertTrue(result instanceof ResourceBundleModel);

		ResourceBundleModel resourceBundleModel = (ResourceBundleModel)result;

		ResourceBundle handledResourceBundle = resourceBundleModel.getBundle();

		Assert.assertNull(handledResourceBundle.getKeys());

		// Enumeration object

		List<String> list = new ArrayList<>();

		String testElement = "testElement";

		list.add(testElement);

		Enumeration<String> enumeration = Collections.enumeration(list);

		result = liferayObjectWrapper.handleUnknownType(enumeration);

		Assert.assertTrue(result instanceof EnumerationModel);

		EnumerationModel enumerationModel = (EnumerationModel)result;

		Assert.assertEquals(testElement, enumerationModel.next().toString());

		// Collection object

		result = liferayObjectWrapper.handleUnknownType(list);

		Assert.assertTrue(result instanceof SimpleSequence);

		SimpleSequence simpleSequence = (SimpleSequence)result;

		Assert.assertEquals(testElement, simpleSequence.get(0).toString());

		// Map object

		Map<String, String> map = new HashMap<>();
		String testKey = "testKey";
		String testValue = "testValue";

		map.put(testKey, testValue);

		result = liferayObjectWrapper.handleUnknownType(map);

		Assert.assertTrue(result instanceof MapModel);

		MapModel mapModel = (MapModel)result;

		Assert.assertEquals(testValue, mapModel.get(testKey).toString());

		// Unknown type object

		Thread thread = new Thread("testThread");

		result = liferayObjectWrapper.handleUnknownType(thread);

		Assert.assertTrue(result instanceof StringModel);

		StringModel stringModel = (StringModel)result;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
	}

	@AdviseWith(adviceClasses = ReflectionUtilAdvice.class)
	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testInitializationFailure() {
		Exception exception = new Exception();

		ReflectionUtilAdvice.setDeclaredFieldThrowable(exception);

		try {
			new LiferayObjectWrapper();

			Assert.fail("No exception thrown!");
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertSame(exception, eiie.getCause());
		}
	}

	@Test
	public void testLiferayObjectWrapperConstructor() {
		Field cacheClassNamesField = ReflectionTestUtil.getAndSetFieldValue(
			LiferayObjectWrapper.class, "_cacheClassNamesField", null);

		try {
			new LiferayObjectWrapper();
			Assert.fail("no exception thrown!");
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
	public void testWrap() throws Exception {
		LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper();

		// Instance of TemplateNode class starting with com.liferay.

		TemplateNode templateNode = new TemplateNode(
			null, "testName", "", "", null);

		TemplateModel result = liferayObjectWrapper.wrap(templateNode);

		Assert.assertTrue(result instanceof LiferayTemplateModel);

		LiferayTemplateModel liferayTemplateModel =
			(LiferayTemplateModel)result;

		Assert.assertEquals(
			"testName", liferayTemplateModel.get("name").toString());

		// Instance of Collection class starting with com.liferay.

		TestLiferayCollection testLiferayCollection =
			new TestLiferayCollection();

		String testElement = "testElement";

		testLiferayCollection.add(testElement);

		result = liferayObjectWrapper.wrap(testLiferayCollection);

		Assert.assertTrue(result instanceof SimpleSequence);

		SimpleSequence simpleSequence = (SimpleSequence)result;

		Assert.assertEquals(testElement, simpleSequence.get(0).toString());

		// Instance of Map class starting with com.liferay.

		TestLiferayMap testLiferayMap = new TestLiferayMap();

		String testKey = "testKey";
		String testValue = "testValue";

		testLiferayMap.put(testKey, testValue);

		result = liferayObjectWrapper.wrap(testLiferayMap);

		Assert.assertTrue(result instanceof MapModel);

		MapModel mapModel = (MapModel)result;

		Assert.assertEquals(testValue, mapModel.get(testKey).toString());

		// Instance of Unknown class starting with com.liferay.

		TestLiferayObject testLiferayObject = new TestLiferayObject();

		result = liferayObjectWrapper.wrap(testLiferayObject);

		Assert.assertTrue(result instanceof StringModel);

		StringModel stringModel = (StringModel)result;

		Assert.assertEquals(
			testLiferayObject.toString(), stringModel.toString());

		// null

		result = liferayObjectWrapper.wrap(null);

		Assert.assertNull(result);

		// Instance of TemplateModel

		TemplateModel templateModel = (TemplateModel)ProxyUtil.newProxyInstance(
			LiferayObjectWrapper.class.getClassLoader(),
			new Class<?>[] {TemplateModel.class},
			(proxy, method, args) -> null);

		result = liferayObjectWrapper.wrap(templateModel);

		Assert.assertSame(templateModel, result);

		// Test that handleUnknownType() cache is used by wrap()

		AtomicInteger handleUnknowTypeCount = new AtomicInteger(0);

		liferayObjectWrapper = new LiferayObjectWrapper() {

			@Override
			protected TemplateModel handleUnknownType(Object object) {
				handleUnknowTypeCount.incrementAndGet();

				return super.handleUnknownType(object);
			}

		};

		Thread thread = new Thread("testThread");

		liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknowTypeCount.get());

		result = liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknowTypeCount.get());

		stringModel = (StringModel)result;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
	}

	private class TestLiferayCollection extends ArrayList<Object> {
	}

	private class TestLiferayMap extends HashMap<String, Object> {
	}

	private class TestLiferayObject {
	}

}