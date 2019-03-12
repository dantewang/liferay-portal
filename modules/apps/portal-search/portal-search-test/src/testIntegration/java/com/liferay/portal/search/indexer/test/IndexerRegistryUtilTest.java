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

package com.liferay.portal.search.indexer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gregory Amerson
 */
@RunWith(Arquillian.class)
public class IndexerRegistryUtilTest {

	@Test
	public void testGetIndexerByIndexerClassName() throws Exception {
		Indexer<JournalArticle> journalArticleIndexer =
			IndexerRegistryUtil.getIndexer(
				"com.liferay.journal.internal.search.JournalArticleIndexer");

		Assert.assertNotNull(journalArticleIndexer);
	}

	@Test
	public void testGetIndexerByModelClassName() throws Exception {
		Indexer<User> userIndexer = IndexerRegistryUtil.getIndexer(
			User.class.getName());

		Assert.assertNotNull(userIndexer);

		Indexer<UserGroup> userGroupIndexer = IndexerRegistryUtil.getIndexer(
			UserGroup.class.getName());

		Assert.assertNotNull(userGroupIndexer);
	}

}