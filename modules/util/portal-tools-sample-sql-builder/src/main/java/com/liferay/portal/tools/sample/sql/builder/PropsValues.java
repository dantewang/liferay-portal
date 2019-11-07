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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SortedProperties;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.time.ZoneId;

import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class PropsValues {

	public static final String[] CSV_NAMES = StringUtil.split(
		PropertiesLoader.get(PropsKeys.CSV_NAMES));

	public static final DBType DB_TYPE = DBType.valueOf(
		StringUtil.toUpperCase(PropertiesLoader.get(PropsKeys.DB_TYPE)));

	public static final int MAX_ASSET_CATEGORY_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_ASSET_CATEGORY_COUNT));

	public static final int MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT =
		GetterUtil.getInteger(
			PropertiesLoader.get(
				PropsKeys.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT));

	public static final int MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT =
		GetterUtil.getInteger(
			PropertiesLoader.get(PropsKeys.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT));

	public static final int MAX_ASSET_TAG_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_ASSET_TAG_COUNT));

	public static final int MAX_ASSET_VUCABULARY_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_ASSET_VUCABULARY_COUNT));

	public static final int MAX_ASSETPUBLISHER_PAGE_COUNT =
		GetterUtil.getInteger(
			PropertiesLoader.get(PropsKeys.MAX_ASSETPUBLISHER_PAGE_COUNT));

	public static final int MAX_BLOGS_ENTRY_COMMENT_COUNT =
		GetterUtil.getInteger(
			PropertiesLoader.get(PropsKeys.MAX_BLOGS_ENTRY_COMMENT_COUNT));

	public static final int MAX_BLOGS_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_BLOGS_ENTRY_COUNT));

	public static final int MAX_CP_DEFINITION_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_CP_DEFINITION_COUNT));

	public static final int MAX_CPINSTANCE_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_CPINSTANCE_COUNT));

	public static final int MAX_CPRODUCT_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_CPRODUCT_COUNT));

	public static final int MAX_DDL_CUSTOM_FIELD_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_DDL_CUSTOM_FIELD_COUNT));

	public static final int MAX_DDL_RECORDER_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_DDL_RECORDER_COUNT));

	public static final int MAX_DDL_RECORDER_SET_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_DDL_RECORDER_SET_COUNT));

	public static final int MAX_DL_FILE_ENTRY_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_DL_FILE_ENTRY_COUNT));

	public static final int MAX_DL_FILE_ENTRY_SIZE = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_DL_FILE_ENTRY_SIZE));

	public static final int MAX_DL_FOLDER_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_DL_FOLDER_COUNT));

	public static final int MAX_DL_FOLDER_DEPTH = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_DL_FOLDER_DEPTH));

	public static final int MAX_GROUP_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_GROUP_COUNT));

	public static final int MAX_JOURNAL_ARTICLE_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_JOURNAL_ARTICLE_COUNT));

	public static final int MAX_JOURNAL_ARTICLE_PAGE_COUNT =
		GetterUtil.getInteger(
			PropertiesLoader.get(PropsKeys.MAX_JOURNAL_ARTICLE_PAGE_COUNT));

	public static final int MAX_JOURNAL_ARTICLE_SIZE = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_JOURNAL_ARTICLE_SIZE));

	public static final int MAX_JOURNAL_ARTICLE_VERSION_COUNT =
		GetterUtil.getInteger(
			PropertiesLoader.get(PropsKeys.MAX_JOURNAL_ARTICLE_VERSION_COUNT));

	public static final int MAX_MB_CATEGORY_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_MB_CATEGORY_COUNT));

	public static final int MAX_MB_MESSAGE_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_MB_MESSAGE_COUNT));

	public static final int MAX_MB_THREAD_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_MB_THREAD_COUNT));

	public static final int MAX_USER_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_USER_COUNT));

	public static final int MAX_USER_TO_GROUP_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_USER_TO_GROUP_COUNT));

	public static final int MAX_WIKI_NODE_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_WIKI_NODE_COUNT));

	public static final int MAX_WIKI_PAGE_COMMENT_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_WIKI_PAGE_COMMENT_COUNT));

	public static final int MAX_WIKI_PAGE_COUNT = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.MAX_WIKI_PAGE_COUNT));

	public static final int OPTIMIZE_BUFFER_SIZE = GetterUtil.getInteger(
		PropertiesLoader.get(PropsKeys.OPTIMIZE_BUFFER_SIZE));

	public static final String OUTPUT_DIR = PropertiesLoader.get(
		PropsKeys.OUTPUT_DIR);

	public static final boolean OUTPUT_MERGE = GetterUtil.getBoolean(
		PropertiesLoader.get(PropsKeys.OUTPUT_MERGE));

	public static final String SCRIPT = PropertiesLoader.get(PropsKeys.SCRIPT);

	public static final String VIRTUAL_HOST_NAME = PropertiesLoader.get(
		PropsKeys.VIRTUAL_HOST_NAME);

	public static String getActualProperties() {
		StringBundler sb = new StringBundler();

		for (String key : PropertiesLoader._properties.stringPropertyNames()) {
			if (!key.startsWith("sample.sql")) {
				continue;
			}

			String value = PropertiesLoader._properties.getProperty(key);

			sb.append(key);
			sb.append(StringPool.EQUAL);
			sb.append(value);
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static class PropertiesLoader {

		public static String get(String key) {
			return _properties.getProperty(key);
		}

		private static final Properties _properties = new SortedProperties() {
			{
				Reader reader = null;

				try {
					reader = new FileReader(
						System.getProperty("properties.file.path"));

					load(reader);

					TimeZone timeZone = TimeZone.getDefault();

					String timeZoneId = getProperty("sample.sql.db.time.zone");

					if (Validator.isNotNull(timeZoneId)) {
						timeZone = TimeZone.getTimeZone(ZoneId.of(timeZoneId));

						TimeZone.setDefault(timeZone);
					}
					else {
						setProperty(
							"sample.sql.db.time.zone", timeZone.getID());
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (reader != null) {
						try {
							reader.close();
						}
						catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}
			}
		};

	}

}