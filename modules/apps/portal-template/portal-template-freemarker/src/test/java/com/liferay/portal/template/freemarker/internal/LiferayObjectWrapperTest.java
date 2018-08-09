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

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
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

		_assertLiferayObjectWrapperAction(
			node, NodeModel.class, liferayObjectWrapper::handleUnknownType,
			templateModel -> {
				Assert.assertSame(node, templateModel.getNode());
				Assert.assertEquals("element", templateModel.getNodeType());
			});

		// TemplateNode object

		_assertLiferayObjectWrapperAction(
			new TemplateNode(null, "testName", "", "", null),
			LiferayTemplateModel.class, liferayObjectWrapper::handleUnknownType,
			templateModel -> {
				TemplateModel nameTemplateModel = templateModel.get("name");

				Assert.assertEquals("testName", nameTemplateModel.toString());
			});

		// ResourceBundle object

		_assertLiferayObjectWrapperAction(
			new ResourceBundle() {

				@Override
				public Enumeration<String> getKeys() {
					return null;
				}

				@Override
				protected Object handleGetObject(String key) {
					return null;
				}

			},
			ResourceBundleModel.class, liferayObjectWrapper::handleUnknownType,
			templateModel -> {
				ResourceBundle handledResourceBundle =
					templateModel.getBundle();

				Assert.assertNull(handledResourceBundle.getKeys());
			});

		// Enumeration object

		List<String> list = new ArrayList<>();

		String testElement = "testElement";

		list.add(testElement);

		Enumeration<String> enumeration = Collections.enumeration(list);

		_assertLiferayObjectWrapperAction(
			enumeration, EnumerationModel.class,
			liferayObjectWrapper::handleUnknownType,
			templateModel -> {
				TemplateModel nextTemplateModel = templateModel.next();

				Assert.assertEquals(testElement, nextTemplateModel.toString());
			});

		// Collection object

		_assertLiferayObjectWrapperAction(
			list, SimpleSequence.class, liferayObjectWrapper::handleUnknownType,
			templateModel -> {
				TemplateModel elementTemplateModel = templateModel.get(0);

				Assert.assertEquals(
					testElement, elementTemplateModel.toString());
			});

		// Map object

		Map<String, String> map = new HashMap<>();

		String testKey = "testKey";
		String testValue = "testValue";

		map.put(testKey, testValue);

		_assertLiferayObjectWrapperAction(
			map, MapModel.class, liferayObjectWrapper::handleUnknownType,
			templateModel -> {
				TemplateModel testValueTemplateModel = templateModel.get(
					testKey);

				Assert.assertEquals(
					testValue, testValueTemplateModel.toString());
			});

		// Unknown type object

		Thread thread = new Thread("testThread");

		_assertLiferayObjectWrapperAction(
			thread, StringModel.class, liferayObjectWrapper::handleUnknownType,
			templateModel -> Assert.assertEquals(
				thread.toString(), templateModel.getAsString()));
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

		_assertLiferayObjectWrapperAction(
			new TemplateNode(null, "testName", "", "", null),
			LiferayTemplateModel.class, liferayObjectWrapper::wrap,
			templateModel -> {
				TemplateModel nameTemplateModel = templateModel.get("name");

				Assert.assertEquals("testName", nameTemplateModel.toString());
			});

		// Instance of Collection class starting with com.liferay.

		TestLiferayCollection testLiferayCollection =
			new TestLiferayCollection();

		String testElement = "testElement";

		testLiferayCollection.add(testElement);

		_assertLiferayObjectWrapperAction(
			testLiferayCollection, SimpleSequence.class,
			liferayObjectWrapper::wrap,
			templateModel -> {
				TemplateModel elementTemplateModel = templateModel.get(0);

				Assert.assertEquals(
					testElement, elementTemplateModel.toString());
			});

		// Instance of Map class starting with com.liferay.

		TestLiferayMap testLiferayMap = new TestLiferayMap();

		String testKey = "testKey";
		String testValue = "testValue";

		testLiferayMap.put(testKey, testValue);

		_assertLiferayObjectWrapperAction(
			testLiferayMap, MapModel.class, liferayObjectWrapper::wrap,
			templateModel -> {
				TemplateModel testValueModel = templateModel.get(testKey);

				Assert.assertEquals(testValue, testValueModel.toString());
			});

		// Instance of Unknown class starting with com.liferay.

		TestLiferayObject testLiferayObject = new TestLiferayObject();

		_assertLiferayObjectWrapperAction(
			testLiferayObject, StringModel.class, liferayObjectWrapper::wrap,
			templateModel -> Assert.assertEquals(
				testLiferayObject.toString(), templateModel.getAsString()));

		// null

		TemplateModel templateModel = liferayObjectWrapper.wrap(null);

		Assert.assertNull(templateModel);

		// Instance of TemplateModel

		TemplateModel oldTemplateModel =
			(TemplateModel)ProxyUtil.newProxyInstance(
				LiferayObjectWrapper.class.getClassLoader(),
				new Class<?>[] {TemplateModel.class},
				(proxy, method, args) -> null);

		TemplateModel newTemplateModel = liferayObjectWrapper.wrap(
			oldTemplateModel);

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

		templateModel = liferayObjectWrapper.wrap(thread);

		Assert.assertEquals(1, handleUnknowTypeCount.get());

		StringModel stringModel = (StringModel)templateModel;

		Assert.assertEquals(thread.toString(), stringModel.getAsString());
	}

	private <R> void _assertLiferayObjectWrapperAction(
			Object object, Class<R> expectedClass,
			UnsafeFunction<Object, TemplateModel, Exception> function,
			UnsafeConsumer<R, Exception> consumer)
		throws Exception {

		TemplateModel templateModel = function.apply(object);

		Class<?> templateModelClass = templateModel.getClass();

		Assert.assertTrue(expectedClass.isAssignableFrom(templateModelClass));

		R result = (R)templateModel;

		consumer.accept(result);
	}

	private class TestLiferayCollection extends ArrayList<Object> {
	}

	private class TestLiferayMap extends HashMap<String, Object> {
	}

	private class TestLiferayObject {
	}

}