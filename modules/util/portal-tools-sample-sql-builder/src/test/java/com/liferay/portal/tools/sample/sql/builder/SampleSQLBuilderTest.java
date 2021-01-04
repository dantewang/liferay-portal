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

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.LogAssertionTestRule;
import com.liferay.portal.tools.HypersonicLoader;
import com.liferay.portal.tools.ToolDependencies;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.util.Properties;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class SampleSQLBuilderTest {

	@ClassRule
	public static final LogAssertionTestRule logAssertionTestRule =
		LogAssertionTestRule.INSTANCE;

	@Test
	public void testFreemarkerTemplateContent() throws Exception {
		Class<?> clazz = getClass();

		URL url = clazz.getResource(
			"/com/liferay/portal/tools/sample/sql/builder/dependencies" +
				"/sample.ftl");

		String fileContent = new String(
			Files.readAllBytes(Paths.get(url.toURI())), StringPool.UTF8);

		Assert.assertTrue(
			"sample.ftl must end with " + _SAMPLE_FTL_END,
			fileContent.endsWith(_SAMPLE_FTL_END));
	}

	@Test
	public void testGenerateAndInsertSampleSQL() throws Exception {
		ToolDependencies.wireBasic();

		DBManagerUtil.setDB(DBType.HYPERSONIC, null);

		Properties properties = new Properties();

		File tempDir = new File(
			SystemProperties.get(SystemProperties.TMP_DIR),
			String.valueOf(System.currentTimeMillis()));

		tempDir.mkdir();

		File createDir = new File(tempDir, "create");

		createDir.mkdir();

		File outputFile = new File(createDir, "create.sql");

		_initProperties(
			properties, tempDir.getAbsolutePath(),
			outputFile.getAbsolutePath());

		File tempPropertiesFile = File.createTempFile("test", ".properties");

		try (Writer writer = new FileWriter(tempPropertiesFile)) {
			properties.store(writer, null);

			System.setProperty(
				"sample-sql-properties", tempPropertiesFile.getAbsolutePath());

			new SampleSQLBuilder();

			_loadHypersonic(
				tempDir.getAbsolutePath(), outputFile.getAbsolutePath());
		}
		finally {
			FileUtil.deltree(tempDir);
		}
	}

	private void _initProperties(
		Properties properties, String outputDir, String outputFile) {

		String currentWorkingDir = System.getProperty("user.dir");

		String rootDir = currentWorkingDir.substring(
			0, currentWorkingDir.lastIndexOf("modules"));

		properties.put(BenchmarksPropsKeys.DB_TYPE, "hypersonic");
		properties.put(BenchmarksPropsKeys.MAX_ASSET_CATEGORY_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_ASSETPUBLISHER_PAGE_COUNT, "2");
		properties.put(BenchmarksPropsKeys.MAX_ASSET_TAG_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_ASSET_VUCABULARY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_BLOGS_ENTRY_COMMENT_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_BLOGS_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_COMMERCE_PRODUCT_INSTANCE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_CONTENT_LAYOUT_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DDL_CUSTOM_FIELD_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DDL_RECORD_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DDL_RECORD_SET_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FILE_ENTRY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FILE_ENTRY_SIZE, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FOLDER_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_DL_FOLDER_DEPTH, "1");
		properties.put(BenchmarksPropsKeys.MAX_GROUP_COUNT, "2");
		properties.put(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_PAGE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_SIZE, "1");
		properties.put(
			BenchmarksPropsKeys.MAX_JOURNAL_ARTICLE_VERSION_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_MB_CATEGORY_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_MB_MESSAGE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_MB_THREAD_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_USER_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_USER_TO_GROUP_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_WIKI_NODE_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_WIKI_PAGE_COMMENT_COUNT, "1");
		properties.put(BenchmarksPropsKeys.MAX_WIKI_PAGE_COUNT, "1");

		properties.put(
			BenchmarksPropsKeys.MODULE_BND_FILES,
			StringBundler.concat(
				rootDir, "modules/apps/asset/asset-entry-rel-service/bnd.bnd,",
				rootDir, "modules/apps/blogs/blogs-service/bnd.bnd,", rootDir,
				"modules/apps/commerce/commerce-currency-service/bnd.bnd,",
				rootDir, "modules/apps/commerce/commerce-product-service",
				"/bnd.bnd,", rootDir, "modules/apps/dynamic-data-lists",
				"/dynamic-data-lists-service/bnd.bnd,", rootDir,
				"modules/apps/dynamic-data-mapping",
				"/dynamic-data-mapping-service/bnd.bnd,", rootDir,
				"modules/apps/fragment/fragment-service/bnd.bnd,", rootDir,
				"modules/apps/friendly-url/friendly-url-service/bnd.bnd,",
				rootDir, "modules/apps/journal/journal-service/bnd.bnd,",
				rootDir, "modules/apps/layout/layout-page-template-service",
				"/bnd.bnd,", rootDir, "modules/apps/message-boards",
				"/message-boards-service/bnd.bnd,", rootDir, "modules/apps",
				"/subscription/subscription-service/bnd.bnd,", rootDir,
				"modules/apps/view-count/view-count-service/bnd.bnd,", rootDir,
				"modules/apps/wiki/wiki-service/bnd.bnd"));
		properties.put(BenchmarksPropsKeys.OPTIMIZE_BUFFER_SIZE, "8192");
		properties.put(
			BenchmarksPropsKeys.OUTPUT_CSV_FILE_NAMES,
			"assetPublisher,blog,company,documentLibrary,dynamicDataList," +
				"fragment,layout,mbCategory,mbThread,repository,wiki");
		properties.put(BenchmarksPropsKeys.OUTPUT_DIR, outputDir);
		properties.put(BenchmarksPropsKeys.OUTPUT_FILE, outputFile);
		properties.put(
			BenchmarksPropsKeys.SCRIPT,
			"com/liferay/portal/tools/sample/sql/builder/dependencies" +
				"/sample.ftl");
		properties.put(BenchmarksPropsKeys.SEARCH_BAR_ENABLED, "true");
		properties.put(
			BenchmarksPropsKeys.SERVICE_JARS_DIR, rootDir + "tools/sdk/dist/");
		properties.put(BenchmarksPropsKeys.VIRTUAL_HOST_NAME, "localhost");
	}

	private void _loadHypersonic(String outputDir, String outputFile)
		throws Exception {

		try (Connection connection = DriverManager.getConnection(
				"jdbc:hsqldb:mem:testSampleSQLBuilderDB;shutdown=true", "sa",
				"")) {

			HypersonicLoader.loadHypersonic(connection, outputFile);

			File dir = new File(outputDir);

			boolean miscSQLFile = false;

			for (File file : dir.listFiles()) {
				String fileName = file.getName();

				if (fileName.equals("misc.sql")) {
					miscSQLFile = true;
				}
				else if (fileName.endsWith(".sql")) {
					HypersonicLoader.loadHypersonic(
						connection, outputDir + "/" + fileName);
				}
			}

			if (miscSQLFile) {
				HypersonicLoader.loadHypersonic(
					connection, outputDir + "/misc.sql");
			}

			try (Statement statement = connection.createStatement()) {
				statement.execute("SHUTDOWN COMPACT");
			}
		}
	}

	private static final String _SAMPLE_FTL_END =
		"<#include \"counters.ftl\">\n\nCOMMIT_TRANSACTION";

}