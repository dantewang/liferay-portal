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

/**
 * @author Dante Wang
 */
public interface DataFactoryConstants {

	public static final String ACCOUNT_LEGAL_NAME = "Liferay, Inc.";

	public static final String ACCOUNT_NAME = "Liferay";

	public static final String ASSET_CATEGORY_NAME_PREFIX = "TestCategory_";

	public static final String ASSET_TAG_NAME_PREFIX = "TestTag_";

	public static final String ASSET_VOCABULARY_NAME_PREFIX = "TestVocabulary_";

	public static final String BLOG_CONTENT_PREFIX = "This is test blog ";

	public static final String BLOG_ENTRY_SUBTITLE_PREFIX =
		"Subtitle of Test Blog ";

	public static final String BLOG_ENTRY_TITLE_PREFIX = "Test Blog ";

	public static final String BLOG_LAYOUT_NAME = "blogs";

	public static final String BLOG_URL_TITLE_PREFIX = "testblog";

	public static final String COMMERCE_CATALOG_NAME = "Master";

	public static final String COMMERCE_CHANNEL_NAME =
		DataFactoryConstants.SAMPLE_USER_NAME + " Channel";

	public static final String COMMERCE_CURRENCY_CODE = "USD";

	public static final String COMMERCE_LAYOUT_NAME = "commerce_product";

	public static final String COMPANY_WEBID = "liferay.com";

	public static final String CONTENT_LAYOUT_NAME_SUFFIX =
		"_" + DataFactoryConstants.FRAGMENT_ENTRY_KEY;

	public static final String CPDEFINITION_DESCRIPTION_PREFIX =
		"A longer and more verbose description for definition with ID ";

	public static final String CPDEFINITION_META_DESCRIPTION_PREFIX =
		"A meta-description for definition ";

	public static final String CPDEFINITION_META_KEYWORDS_PREFIX =
		"Meta-keywords for definition ";

	public static final String CPDEFINITION_META_TITLE_PREFIX =
		"A meta-title for definition ";

	public static final String CPDEFINITION_PRODUCT_TYPE_NAME = "simple";

	public static final String CPDEFINITION_SHORT_DESCRIPTION_PREFIX =
		"Short description for definition ";

	public static final String CPFRIENDLYURL_TITLE_PREFIX = "Definition ";

	public static final String CPINSTANCE_GTIN_PREFIX = "GTIN";

	public static final String CPINSTANCE_MPN_PREFIX = "MPN";

	public static final String CPINSTANCE_SKU_PREFIX = "SKU";

	public static final String DDL_DDM_STRUCTURE_KEY = "Test DDM Structure";

	public static final String DEFAULT_ASSET_PUBLISHER_PREFERENCE =
		"default_asset_publisher_preference.xml";

	public static final String DL_DDM_STRUCTURE_CONTENT =
		"ddm_structure_basic_document.json";

	public static final String DL_DDM_STRUCTURE_LAYOUT_CONTENT =
		"ddm_structure_layout_basic_document.json";

	public static final String DL_ENTRY_NAME_PREFIX = "TestFile";

	public static final String DL_EXTENSION = "txt";

	public static final String DL_FOLDER_NAME_PREFIX = "Test Folder ";

	public static final String DL_LAYOUT_NAME = "document_library";

	public static final String EMAIL_POSTFIX = "@liferay.com";

	public static final String FIRST_NAME_LIST = "first_names.txt";

	public static final String FORUMS_LAYOUT_NAME = "forums";

	public static final String FRAGMENT_COLLECTION_KEY = "fragmentcollection";

	public static final String FRAGMENT_ENTRY_KEY = "web_content";

	public static final String FRAGMENT_HTML_FILE_NAME =
		FRAGMENT_ENTRY_KEY + ".html";

	public static final String GREETING_PREFIX = "Welcome ";

	public static final String GROUP_NAME_PREFIX = "Site ";

	public static final String GUEST_USER_NAME = "Test";

	public static final String HELLO_WORLD_LAYOUT_NAME = "welcome";

	public static final String JOURNAL_ARTICLE_TITLE_PREFIX =
		"TestJournalArticle_";

	public static final String JOURNAL_DDM_STRUCTURE_CONTENT =
		"ddm_structure_basic_web_content.json";

	public static final String JOURNAL_DDM_STRUCTURE_LAYOUT_CONTENT =
		"ddm_structure_layout_basic_web_content.json";

	public static final String JOURNAL_STRUCTURE_KEY = "BASIC-WEB-CONTENT";

	public static final String LANGUAGE_ID = "en_US";

	public static final String LAST_NAME_LIST = "last_names.txt";

	public static final String LAYOUT_COLOR_THEME_ID = "01";

	public static final String LAYOUT_THEME_ID = "classic_WAR_classictheme";

	public static final String MB_BODY_PREFIX = "This is test message ";

	public static final String MB_CATEGORY_NAME_PREFIX = "Test Category ";

	public static final String MB_COMMENT_PREFIX = "This is test comment ";

	public static final String MB_MESSAGE_BODY = "This is test message 1.";

	public static final String MB_ROOT_SUBJECT =
		DataFactoryConstants.MB_SUBJECT_PREFIX + "1";

	public static final String MB_SUBJECT_PREFIX = "Test Message ";

	public static final String MB_URL_SUBJECT =
		DataFactoryConstants.MB_URL_SUBJECT_PREFIX + "-1";

	public static final String MB_URL_SUBJECT_PREFIX = "test-message";

	public static final String RELEASE_RESOURCE_FILE_NAME =
		"dependencies/releases.txt";

	public static final String REMINDER_QUERY_QUESTION =
		"What is your screen name?";

	public static final String SAMPLE_USER_NAME = "Sample";

	public static final String SCREEN_NAME_PREFIX = "test";

	public static final String USER_PASSWORD = "test";

	public static final String WIKI_LAYOUT_NAME = "wiki";

	public static final String WIKI_NODE_NAME_PREFIX = "Test Node ";

	public static final String WIKI_PAGE_CONTENT_PREFIX = "This is test page ";

	public static final String WIKI_PAGE_FORMAT = "creole";

	public static final String WIKI_PAGE_TITLE_PREFIX = "Test Page ";

}