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

import freemarker.template.SimpleDate;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
	public void testHandleUnknownType() {
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

		// TemplateNode object

		TemplateNode templateNode = new TemplateNode(null, "", "", "", null);

		result = liferayObjectWrapper.handleUnknownType(templateNode);

		Assert.assertTrue(result instanceof LiferayTemplateModel);

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

		// Enumeration object

		Enumeration enumeration = Collections.enumeration(
			Collections.emptyList());

		result = liferayObjectWrapper.handleUnknownType(enumeration);

		Assert.assertTrue(result instanceof EnumerationModel);

		// Collection object

		List<?> list = new ArrayList<>();

		result = liferayObjectWrapper.handleUnknownType(list);

		Assert.assertTrue(result instanceof SimpleSequence);

		// Map object

		Map<?, ?> map = new HashMap<>();

		result = liferayObjectWrapper.handleUnknownType(map);

		Assert.assertTrue(result instanceof MapModel);

		// Unknown type object

		Date date = new Date();

		result = liferayObjectWrapper.handleUnknownType(date);

		Assert.assertTrue(result instanceof StringModel);
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

		TemplateNode templateNode = new TemplateNode(null, "", "", "", null);

		TemplateModel result = liferayObjectWrapper.wrap(templateNode);

		Assert.assertTrue(result instanceof LiferayTemplateModel);

		// Instance of Collection class starting with com.liferay.

		TestLiferayCollection liferayCollection = new TestLiferayCollection();

		result = liferayObjectWrapper.wrap(liferayCollection);

		Assert.assertTrue(result instanceof SimpleSequence);

		// Instance of Map class starting with com.liferay.

		TestLiferayMap liferayMap = new TestLiferayMap();

		result = liferayObjectWrapper.wrap(liferayMap);

		Assert.assertTrue(result instanceof MapModel);

		// Instance of Unknown class starting with com.liferay.

		TestLiferayObject liferayObject = new TestLiferayObject();

		result = liferayObjectWrapper.wrap(liferayObject);

		Assert.assertTrue(result instanceof StringModel);

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

		// Wrap unknown type after handleUnknownType

		Date date = new Date();

		result = liferayObjectWrapper.wrap(date);

		Assert.assertTrue(result instanceof SimpleDate);

		liferayObjectWrapper.handleUnknownType(date);

		result = liferayObjectWrapper.wrap(date);

		Assert.assertTrue(result instanceof StringModel);
	}

	private class TestLiferayCollection extends ArrayList {
	}

	private class TestLiferayMap extends HashMap {
	}

	private class TestLiferayObject {
	}

}