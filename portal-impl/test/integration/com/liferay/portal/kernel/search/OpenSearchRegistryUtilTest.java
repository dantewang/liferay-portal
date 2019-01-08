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

package com.liferay.portal.kernel.search;

import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Peter Fellwock
 */
public class OpenSearchRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		TestOpenSearch testOpenSearch = new TestOpenSearch();

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<>();

		properties.put("service.ranking", Integer.MAX_VALUE);

		_serviceRegistration = registry.registerService(
			OpenSearch.class, testOpenSearch, properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetOpenSearch() {
		OpenSearch openSearch = OpenSearchRegistryUtil.getOpenSearch(
			TestOpenSearch.class);

		Assert.assertEquals(
			TestOpenSearch.class.getName(), openSearch.getClassName());
	}

	@Test
	public void testGetOpenSearchInstances() {
		boolean exists = false;

		List<OpenSearch> openSearches =
			OpenSearchRegistryUtil.getOpenSearchInstances();

		for (OpenSearch openSearch : openSearches) {
			String openSearchClassName = openSearch.getClassName();

			if (openSearchClassName.equals(TestOpenSearch.class.getName())) {
				exists = true;

				break;
			}
		}

		Assert.assertTrue(exists);
	}

	private static ServiceRegistration<OpenSearch> _serviceRegistration;

	private static class TestOpenSearch implements OpenSearch {

		@Override
		public String getClassName() {
			return TestOpenSearch.class.getName();
		}

		@Override
		public boolean isEnabled() {
			return false;
		}

		@Override
		public String search(
			HttpServletRequest request, long groupId, long userId,
			String keywords, int startPage, int itemsPerPage, String format) {

			return groupId + ":" + userId;
		}

		@Override
		public String search(
			HttpServletRequest request, long userId, String keywords,
			int startPage, int itemsPerPage, String format) {

			return userId + ":" + startPage;
		}

		@Override
		public String search(HttpServletRequest request, String url) {
			return url;
		}

	}

}