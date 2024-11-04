/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eric Yan
 */
public class IndexEntryTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		try (InputStream inputStream = IndexEntryTest.class.getResourceAsStream(
				"/META-INF/sql/indexes.sql")) {

			_indexSQLs = Collections.unmodifiableList(
				ListUtil.filter(
					ListUtil.fromString(StringUtil.read(inputStream)),
					sql -> sql.contains(" on IndexEntry ")));
		}
	}

	@Test
	public void testBTreeOptimization() {
		_assertIndexes(
			Arrays.asList(
				new ObjectValuePair<>(
					Arrays.asList(
						"companyId", "ctCollectionId", "externalReferenceCode"),
					true),
				new ObjectValuePair<>(Arrays.asList("ownerId"), false),
				new ObjectValuePair<>(
					Arrays.asList("ownerType", "ownerId", "plid"), false),
				new ObjectValuePair<>(Arrays.asList("plid"), false),
				new ObjectValuePair<>(
					Arrays.asList(
						"portletId", "ownerType", "ownerId", "companyId"),
					false),
				new ObjectValuePair<>(
					Arrays.asList(
						"portletId", "ownerType", "ownerId", "plid",
						"ctCollectionId"),
					true),
				new ObjectValuePair<>(
					Arrays.asList("portletId", "ownerType", "plid"), false),
				new ObjectValuePair<>(
					Arrays.asList("portletId", "plid"), false)),
			_indexSQLs);
	}

	@Test
	public void testCtCollectionId() {
		List<String> indexSQLsWithCtCollectionId = ListUtil.filter(
			_indexSQLs,
			sql -> ListUtil.exists(
				_getTrimmedIndexColumnNames(sql),
				columnName -> columnName.equals("ctCollectionId")));

		_assertIndexes(
			Arrays.asList(
				new ObjectValuePair<>(
					Arrays.asList(
						"companyId", "ctCollectionId", "externalReferenceCode"),
					true),
				new ObjectValuePair<>(
					Arrays.asList(
						"portletId", "ownerType", "ownerId", "plid",
						"ctCollectionId"),
					true)),
			indexSQLsWithCtCollectionId);
	}

	@Test
	public void testExternalReferenceCode() {
		List<String> indexSQLsWithExternalReferenceCode = ListUtil.filter(
			_indexSQLs,
			sql -> ListUtil.exists(
				_getTrimmedIndexColumnNames(sql),
				columnName -> columnName.equals("externalReferenceCode")));

		_assertIndexes(
			Arrays.asList(
				new ObjectValuePair<>(
					Arrays.asList(
						"companyId", "ctCollectionId", "externalReferenceCode"),
					true)),
			indexSQLsWithExternalReferenceCode);
	}

	private void _assertIndexes(
		List<ObjectValuePair<List<String>, Boolean>> expectedObjectValuePairs,
		List<String> sqls) {

		Assert.assertEquals(
			sqls.toString(), expectedObjectValuePairs.size(), sqls.size());

		for (int i = 0; i < expectedObjectValuePairs.size(); i++) {
			ObjectValuePair<List<String>, Boolean> expectedObjectValuePair =
				expectedObjectValuePairs.get(i);

			List<String> expectedColumnNames = expectedObjectValuePair.getKey();
			boolean unique = expectedObjectValuePair.getValue();

			String sql = sqls.get(i);

			Assert.assertEquals(
				expectedColumnNames, _getTrimmedIndexColumnNames(sql));

			if (unique) {
				Assert.assertTrue(sql.startsWith("create unique index"));
			}
			else {
				Assert.assertTrue(sql.startsWith("create index"));
			}
		}
	}

	private List<String> _getTrimmedIndexColumnNames(String sql) {
		return TransformUtil.transform(
			StringUtil.split(
				sql.substring(
					sql.indexOf(CharPool.OPEN_PARENTHESIS) + 1,
					sql.indexOf(CharPool.CLOSE_PARENTHESIS))),
			columnName -> {
				columnName = columnName.trim();

				int index = columnName.indexOf("[$COLUMN_LENGTH:");

				if (index > 0) {
					columnName = columnName.substring(0, index);
				}

				return columnName;
			});
	}

	private static List<String> _indexSQLs;

}