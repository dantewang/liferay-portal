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

package com.liferay.portal.kernel.atom;

import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Peter Fellwock
 */
public class AtomCollectionAdapterRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			AtomCollectionAdapter.class, new TestAtomCollectionAdapter(),
			new HashMap<String, Object>() {
				{
					put("service.ranking", Integer.MAX_VALUE);
				}
			});
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetAtomCollectionAdapter() {
		AtomCollectionAdapter<?> atomCollectionAdapter =
			AtomCollectionAdapterRegistryUtil.getAtomCollectionAdapter(
				TestAtomCollectionAdapter.COLLECTION_NAME);

		Class<?> clazz = atomCollectionAdapter.getClass();

		Assert.assertEquals(
			TestAtomCollectionAdapter.class.getName(), clazz.getName());
	}

	@Test
	public void testGetAtomCollectionAdapters() {
		List<AtomCollectionAdapter<?>> atomCollectionAdapters =
			AtomCollectionAdapterRegistryUtil.getAtomCollectionAdapters();

		Assert.assertTrue(
			TestAtomCollectionAdapter.COLLECTION_NAME + " not found in " +
				atomCollectionAdapters,
			atomCollectionAdapters.removeIf(
				atomCollectionAdapter ->
					TestAtomCollectionAdapter.COLLECTION_NAME.equals(
						atomCollectionAdapter.getCollectionName())));
	}

	private static ServiceRegistration<AtomCollectionAdapter>
		_serviceRegistration;

	private static class TestAtomCollectionAdapter
		implements AtomCollectionAdapter<Object> {

		public static final String COLLECTION_NAME =
			"TestAtomCollectionAdapter";

		@Override
		public void deleteEntry(
			String resourceName, AtomRequestContext atomRequestContext) {
		}

		@Override
		public String getCollectionName() {
			return COLLECTION_NAME;
		}

		@Override
		public Object getEntry(
			String resourceName, AtomRequestContext atomRequestContext) {

			return null;
		}

		@Override
		public List<String> getEntryAuthors(Object entry) {
			return null;
		}

		@Override
		public AtomEntryContent getEntryContent(
			Object entry, AtomRequestContext atomRequestContext) {

			return null;
		}

		@Override
		public String getEntryId(Object entry) {
			return null;
		}

		@Override
		public String getEntrySummary(Object entry) {
			return null;
		}

		@Override
		public String getEntryTitle(Object entry) {
			return null;
		}

		@Override
		public Date getEntryUpdated(Object entry) {
			return null;
		}

		@Override
		public Iterable<Object> getFeedEntries(
			AtomRequestContext atomRequestContext) {

			return null;
		}

		@Override
		public String getFeedTitle(AtomRequestContext atomRequestContext) {
			return null;
		}

		@Override
		public String getMediaContentType(Object entry) {
			return null;
		}

		@Override
		public String getMediaName(Object entry) {
			return null;
		}

		@Override
		public InputStream getMediaStream(Object entry) {
			return null;
		}

		@Override
		public Object postEntry(
			String title, String summary, String content, Date date,
			AtomRequestContext atomRequestContext) {

			return null;
		}

		@Override
		public Object postMedia(
			String mimeType, String slug, InputStream inputStream,
			AtomRequestContext atomRequestContext) {

			return null;
		}

		@Override
		public void putEntry(
			Object entry, String title, String summary, String content,
			Date date, AtomRequestContext atomRequestContext) {
		}

		@Override
		public void putMedia(
			Object entry, String mimeType, String slug, InputStream inputStream,
			AtomRequestContext atomRequestContext) {
		}

	}

}