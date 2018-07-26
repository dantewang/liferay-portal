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
import com.liferay.portal.test.aspects.ReflectionUtilAdvice;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.AspectJNewEnvTestRule;

import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultNode;

import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.ext.beans.StringModel;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;

import java.lang.reflect.Field;

import java.util.ArrayList;
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

import org.powermock.api.mockito.PowerMockito;

/**
 * @author Xiangyue Cai
 */
public class LiferayObjectWrapperTest extends PowerMockito {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			AspectJNewEnvTestRule.INSTANCE, CodeCoverageAssertor.INSTANCE);

	@Test
	public void testHandleUnknownType() {
		_liferayObjectWrapper = new LiferayObjectWrapper();

		// Node object

		DefaultNode defaultNode = new DefaultNode();

		TemplateModel result = _liferayObjectWrapper.handleUnknownType(
			defaultNode);

		Assert.assertNull(result);

		// TemplateNode object

		TemplateNode templateNode = new TemplateNode(null, "", "", "", null);

		result = _liferayObjectWrapper.handleUnknownType(templateNode);

		Assert.assertTrue(result instanceof LiferayTemplateModel);

		//ResourceBundle object
		ResourceBundle resourceBundle = mock(ResourceBundle.class);

		result = _liferayObjectWrapper.handleUnknownType(resourceBundle);

		Assert.assertTrue(result instanceof ResourceBundleModel);

		// Enumeration object

		Enumeration enumeration = mock(Enumeration.class);

		result = _liferayObjectWrapper.handleUnknownType(enumeration);

		Assert.assertTrue(result instanceof EnumerationModel);

		// Collection object

		List list = new ArrayList();

		result = _liferayObjectWrapper.handleUnknownType(list);

		Assert.assertTrue(result instanceof SimpleSequence);

		// Map object

		Map map = new HashMap();

		result = _liferayObjectWrapper.handleUnknownType(map);

		Assert.assertTrue(result instanceof MapModel);

		// unknown type object

		Date date = new Date();

		result = _liferayObjectWrapper.handleUnknownType(date);

		Assert.assertTrue(result instanceof StringModel);
	}

	@AdviseWith(adviceClasses = ReflectionUtilAdvice.class)
	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testInitializationFailure() {
		Throwable throwable = new Throwable();

		ReflectionUtilAdvice.setDeclaredFieldThrowable(throwable);

		try {
			new LiferayObjectWrapper();

			Assert.fail();
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertSame(throwable, eiie.getCause());
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
		_liferayObjectWrapper = new LiferayObjectWrapper();

		// TemplateNode class object start with com.liferay.

		TemplateNode templateNode = new TemplateNode(null,"","","",null);

		TemplateModel result = _liferayObjectWrapper.wrap(templateNode);

		Assert.assertTrue(result instanceof LiferayTemplateModel);

		// Collection class object start with com.liferay.

		ObjectCollection objectCollection = new ObjectCollection();

		result = _liferayObjectWrapper.wrap(objectCollection);

		Assert.assertTrue(result instanceof SimpleSequence);

		// Map class object start with com.liferay.

		ObjectMap objectMap = new ObjectMap();

		result = _liferayObjectWrapper.wrap(objectMap);

		Assert.assertTrue(result instanceof MapModel);

		// an unknown class object start with com.liferay.

		ObjectEmpty objectEmpty = new ObjectEmpty();

		result = _liferayObjectWrapper.wrap(objectEmpty);

		Assert.assertTrue(result instanceof StringModel);

		// object is null

		result = _liferayObjectWrapper.wrap(null);

		Assert.assertNull(result);

		// object is TemplateModel

		TemplateModel templateModel = mock(TemplateModel.class);

		result = _liferayObjectWrapper.wrap(templateModel);

		Assert.assertSame(templateModel, result);

		// Test wrap unknown type after handleUnknownType

		Date date = new Date();

		result = _liferayObjectWrapper.wrap(date);

		Assert.assertTrue(result instanceof SimpleDate);

		_liferayObjectWrapper.handleUnknownType(date);

		result = _liferayObjectWrapper.wrap(date);

		Assert.assertTrue(result instanceof StringModel);
	}

	private LiferayObjectWrapper _liferayObjectWrapper;

	private class ObjectCollection extends ArrayList {
	}

	private class ObjectEmpty {
	}

	private class ObjectMap extends HashMap {
	}

}