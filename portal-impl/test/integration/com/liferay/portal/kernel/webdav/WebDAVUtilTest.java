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

package com.liferay.portal.kernel.webdav;

import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.webdav.methods.MethodFactory;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Collection;
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
 * @author Philip Jones
 */
public class WebDAVUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestWebDAVStorage testWebDAVStorage = new TestWebDAVStorage();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);
		properties.put("webdav.storage.token", TestWebDAVStorage.TOKEN);

		_serviceRegistration = registry.registerService(
			WebDAVStorage.class, testWebDAVStorage, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetStorage() {
		WebDAVStorage webDAVStorage = WebDAVUtil.getStorage(
			TestWebDAVStorage.TOKEN);

		Class<?> clazz = webDAVStorage.getClass();

		Assert.assertEquals(TestWebDAVStorage.class.getName(), clazz.getName());
	}

	@Test
	public void testGetStorageTokens() {
		Collection<String> storageTokens = WebDAVUtil.getStorageTokens();

		Assert.assertTrue(
			storageTokens.toString(),
			storageTokens.contains(TestWebDAVStorage.TOKEN));
	}

	private static ServiceRegistration<WebDAVStorage> _serviceRegistration;

	private static class TestWebDAVStorage implements WebDAVStorage {

		public static final String TOKEN = "TOKEN";

		@Override
		public int copyCollectionResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite, long depth) {

			return 0;
		}

		@Override
		public int copySimpleResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite) {

			return 0;
		}

		@Override
		public int deleteResource(WebDAVRequest webDAVRequest) {
			return 0;
		}

		@Override
		public MethodFactory getMethodFactory() {
			return null;
		}

		@Override
		public Resource getResource(WebDAVRequest webDAVRequest) {
			return null;
		}

		@Override
		public List<Resource> getResources(WebDAVRequest webDAVRequest) {
			return null;
		}

		@Override
		public String getRootPath() {
			return null;
		}

		@Override
		public String getToken() {
			return _token;
		}

		@Override
		public boolean isAvailable(WebDAVRequest webDAVRequest) {
			return false;
		}

		@Override
		public boolean isSupportsClassTwo() {
			return false;
		}

		@Override
		public Status lockResource(
			WebDAVRequest webDAVRequest, String owner, long timeout) {

			return null;
		}

		@Override
		public Status makeCollection(WebDAVRequest webDAVRequest) {
			return null;
		}

		@Override
		public int moveCollectionResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite) {

			return 0;
		}

		@Override
		public int moveSimpleResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite) {

			return 0;
		}

		@Override
		public int putResource(WebDAVRequest webDAVRequest) {
			return 0;
		}

		@Override
		public Lock refreshResourceLock(
			WebDAVRequest webDAVRequest, String uuid, long timeout) {

			return null;
		}

		@Override
		public void setRootPath(String rootPath) {
		}

		@Override
		public void setToken(String token) {
			_token = token;
		}

		@Override
		public boolean unlockResource(
			WebDAVRequest webDAVRequest, String token) {

			return false;
		}

		private String _token;

	}

}