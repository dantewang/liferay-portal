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

package com.liferay.portal.model;

import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ModelListenerRegistrationUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class ModelListenerRegistrationUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestModelListener testModelListener = new TestModelListener();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			ModelListener.class, testModelListener, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetModelListeners() {
		List<ModelListener<Contact>> modelListeners = new ArrayList<>(
			Arrays.asList(
				ModelListenerRegistrationUtil.getModelListeners(
					Contact.class)));

		String testClassName = TestModelListener.class.getName();

		Assert.assertTrue(
			testClassName + " not found in " + modelListeners,
			modelListeners.removeIf(
				modelListener -> {
					Class<?> clazz = modelListener.getClass();

					return testClassName.equals(clazz.getName());
				}));
	}

	private static ServiceRegistration<ModelListener> _serviceRegistration;

	private static class TestModelListener extends BaseModelListener<Contact> {
	}

}