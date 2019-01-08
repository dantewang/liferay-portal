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

package com.liferay.portal.kernel.lar.xstream;

import com.liferay.exportimport.kernel.xstream.XStreamConverter;
import com.liferay.exportimport.kernel.xstream.XStreamConverterRegistryUtil;
import com.liferay.exportimport.kernel.xstream.XStreamHierarchicalStreamReader;
import com.liferay.exportimport.kernel.xstream.XStreamHierarchicalStreamWriter;
import com.liferay.exportimport.kernel.xstream.XStreamMarshallingContext;
import com.liferay.exportimport.kernel.xstream.XStreamUnmarshallingContext;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class XStreamConverterRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestXStreamConverter testXStreamConverter = new TestXStreamConverter();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			XStreamConverter.class, testXStreamConverter, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetXStreamConverters() {
		Set<XStreamConverter> xStreamConverters =
			XStreamConverterRegistryUtil.getXStreamConverters();

		String testClassName = TestXStreamConverter.class.getName();

		Assert.assertTrue(
			testClassName + " not found in " + xStreamConverters,
			xStreamConverters.removeIf(
				xStreamConverter -> {
					Class<?> clazz = xStreamConverter.getClass();

					return testClassName.equals(clazz.getName());
				}));
	}

	private static ServiceRegistration<XStreamConverter> _serviceRegistration;

	private static class TestXStreamConverter implements XStreamConverter {

		@Override
		public boolean canConvert(Class<?> clazz) {
			return false;
		}

		@Override
		public void marshal(
			Object object,
			XStreamHierarchicalStreamWriter xStreamHierarchicalStreamWriter,
			XStreamMarshallingContext xStreamMarshallingContext) {
		}

		@Override
		public Object unmarshal(
			XStreamHierarchicalStreamReader xStreamHierarchicalStreamReader,
			XStreamUnmarshallingContext xStreamUnmarshallingContext) {

			return null;
		}

	}

}