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

		TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(
			node);

		Assert.assertTrue(templateModel instanceof NodeModel);

		NodeModel nodeModel = (NodeModel)templateModel;

		Assert.assertSame(node, nodeModel.getNode());
		Assert.assertEquals("element", nodeModel.getNodeType());

		// TemplateNode object

		templateModel = liferayObjectWrapper.handleUnknownType(
			new TemplateNode(null, "testName", "", "", null));

		Assert.assertTrue(templateModel instanceof LiferayTemplateModel);

		LiferayTemplateModel liferayTemplateModel =
			(LiferayTemplateModel)templateModel;

		TemplateModel nameTemplateModel = liferayTemplateModel.get("name");

		Assert.assertEquals("testName", nameTemplateModel.toString());

		// ResourceBundle object

		templateModel = liferayObjectWrapper.handleUnknownType(
			new ResourceBundle() {

				@Override
				public Enumeration<String> getKeys() {
					return null;
				}

				@Override
				protected Object handleGetObject(String key) {
					return null;
				}

			});

		Assert.assertTrue(templateModel instanceof ResourceBundleModel);

		ResourceBundleModel resourceBundleModel =
			(ResourceBundleModel)templateModel;

		ResourceBundle handledResourceBundle = resourceBundleModel.getBundle();

		Assert.assertNull(handledResourceBundle.getKeys());

		// Enumeration object

		List<String> list = new ArrayList<>();

		String testElement = "testElement";

		list.add(testElement);

		Enumeration<String> enumeration = Collections.enumeration(list);

		templateModel = liferayObjectWrapper.handleUnknownType(enumeration);

		Assert.assertTrue(templateModel instanceof EnumerationModel);

		EnumerationModel enumerationModel = (EnumerationModel)templateModel;

		TemplateModel nextTemplateModel = enumerationModel.next();

		Assert.assertEquals(testElement, nextTemplateModel.toString());

		// Collection object

		templateModel = liferayObjectWrapper.handleUnknownType(list);

		Assert.assertTrue(templateModel instanceof SimpleSequence);

		SimpleSequence simpleSequence = (SimpleSequence)templateModel;

		TemplateModel elementTemplateModel = simpleSequence.get(0);

		Assert.assertEquals(testElement, elementTemplateModel.toString());

		// Map object

		Map<String, String> map = new HashMap<>();

		String testKey = "testKey";
		String testValue = "testValue";

		map.put(testKey, testValue);

		templateModel = liferayObjectWrapper.handleUnknownType(map);

		Assert.assertTrue(templateModel instanceof MapModel);

		MapModel mapModel = (MapModel)templateModel;

		TemplateModel testValueTemplateModel = mapModel.get(testKey);

		Assert.assertEquals(testValue, testValueTemplateModel.toString());

		// Unknown type object

		Thread thread = new Thread("testThread");

		templateModel = liferayObjectWrapper.handleUnknownType(thread);

		Assert.assertTrue(templateModel instanceof StringModel);

		StringModel stringModel = (StringModel)templateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
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

		TemplateModel newTemplateModel = liferayObjectWrapper.wrap(
			new TemplateNode(null, "testName", "", "", null));

		Assert.assertTrue(newTemplateModel instanceof LiferayTemplateModel);

		LiferayTemplateModel liferayTemplateModel =
			(LiferayTemplateModel)newTemplateModel;

		TemplateModel nameTemplateModel = liferayTemplateModel.get("name");

		Assert.assertEquals("testName", nameTemplateModel.toString());

		// Instance of Collection class starting with com.liferay.

		TestLiferayCollection testLiferayCollection =
			new TestLiferayCollection();

		String testElement = "testElement";

		testLiferayCollection.add(testElement);

		newTemplateModel = liferayObjectWrapper.wrap(testLiferayCollection);

		Assert.assertTrue(newTemplateModel instanceof SimpleSequence);

		SimpleSequence simpleSequence = (SimpleSequence)newTemplateModel;

		TemplateModel elementTemplateModel = simpleSequence.get(0);

		Assert.assertEquals(testElement, elementTemplateModel.toString());

		// Instance of Map class starting with com.liferay.

		TestLiferayMap testLiferayMap = new TestLiferayMap();

		String testKey = "testKey";
		String testValue = "testValue";

		testLiferayMap.put(testKey, testValue);

		newTemplateModel = liferayObjectWrapper.wrap(testLiferayMap);

		Assert.assertTrue(newTemplateModel instanceof MapModel);

		MapModel mapModel = (MapModel)newTemplateModel;

		TemplateModel testValueModel = mapModel.get(testKey);

		Assert.assertEquals(testValue, testValueModel.toString());

		// Instance of Unknown class starting with com.liferay.

		TestLiferayObject testLiferayObject = new TestLiferayObject();

		newTemplateModel = liferayObjectWrapper.wrap(testLiferayObject);

		Assert.assertTrue(newTemplateModel instanceof StringModel);

		StringModel stringModel = (StringModel)newTemplateModel;

		Assert.assertEquals(
			testLiferayObject.toString(), stringModel.getAsString());

		// null

		newTemplateModel = liferayObjectWrapper.wrap(null);

		Assert.assertNull(newTemplateModel);

		// Instance of TemplateModel

		TemplateModel oldTemplateModel =
			(TemplateModel)ProxyUtil.newProxyInstance(
				LiferayObjectWrapper.class.getClassLoader(),
				new Class<?>[] {TemplateModel.class},
				(proxy, method, args) -> null);

		newTemplateModel = liferayObjectWrapper.wrap(oldTemplateModel);

		Assert.assertSame(oldTemplateModel, newTemplateModel);

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

		newTemplateModel = liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknowTypeCount.get());

		stringModel = (StringModel)newTemplateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
	}

	private class TestLiferayCollection extends ArrayList<Object> {
	}

	private class TestLiferayMap extends HashMap<String, Object> {
	}

	private class TestLiferayObject {
	}

}