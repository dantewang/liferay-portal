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

import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.model.impl.ClassNameModelImpl;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiPage;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public static long getClassNameId(Class<?> clazz) {
		ClassNameModel classNameModel = _classNameModels.get(clazz.getName());

		return classNameModel.getClassNameId();
	}

	public long getClassNameId(String className) {
		ClassNameModel classNameModel = _classNameModels.get(className);

		return classNameModel.getClassNameId();
	}

	public Collection<ClassNameModel> getClassNameModels() {
		return _classNameModels.values();
	}

	public Date nextFutureDate() {
		return new Date(_FUTURE_TIME + (_FUTURE_COUNTER.get() * Time.SECOND));
	}

	protected static String getMBDiscussionCombinedClassName(Class<?> clazz) {
		return StringBundler.concat(
			MBDiscussion.class.getName(), StringPool.UNDERLINE,
			clazz.getName());
	}

	protected String getClassName(long classNameId) {
		for (ClassNameModel classNameModel : _classNameModels.values()) {
			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	protected InputStream getResourceInputStream(String resourceName) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	protected String readFile(String resourceName) throws IOException {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(getResourceInputStream(resourceName), lines);

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	protected static final long ADMINISTRATOR_ROLE_ID;

	protected static final long COMMERCE_CATALOG_GROUP_ID;

	protected static final long COMMERCE_CATALOG_ID;

	protected static final String COMMERCE_CATALOG_NAME = "Master";

	protected static final long COMMERCE_CHANNEL_GROUP_ID;

	protected static final long COMMERCE_CHANNEL_ID;

	protected static final String COMMERCE_CHANNEL_NAME =
		BaseDataFactory.SAMPLE_USER_NAME + " Channel";

	protected static final long COMPANY_ID;

	protected static final long DEFAULT_JOURNAL_DDM_STRUCTURE_ID;

	protected static final long DEFAULT_USER_ID;

	protected static final long GLOBAL_GROUP_ID;

	protected static final long GUEST_GROUP_ID;

	protected static final long GUEST_ROLE_ID;

	protected static final long OWNER_ROLE_ID;

	protected static final long POWER_USER_ROLE_ID;

	protected static final long SAMPLE_USER_ID;

	protected static final String SAMPLE_USER_NAME = "Sample";

	protected static final long SITE_MEMBER_ROLE_ID;

	protected static final long USER_ROLE_ID;

	protected static Map<Long, List<AssetCategoryModel>>[]
		assetCategoryModelsMaps;
	protected static long[] assetClassNameIds = new long[3];
	protected static final Map<Long, Integer> assetClassNameIdsIndexes =
		new HashMap<>();
	protected static Map<Long, List<AssetTagModel>>[] assetTagModelsMaps;
	protected static final SimpleCounter counter;
	protected static final List<long[]> cpDefinitionIdList = new ArrayList<>(
		BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT);
	protected static final Map<Long, String> cpDefinitionLocalizationNames =
		new HashMap<>();
	protected static final List<Long> cProductIds = new ArrayList<>();
	protected static final Map<Long, String> journalArticleResourceUUIDs =
		new HashMap<>();
	protected static final PortletPreferencesFactory portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();
	protected static final List<Long> publishedCPDefinitionIds =
		new ArrayList<>();
	protected static final SimpleCounter resourcePermissionCounter =
		new SimpleCounter();
	protected static final SimpleCounter socialActivityCounter =
		new SimpleCounter();

	private static void _initClassNameModels() {
		List<String> models = ModelHintsUtil.getModels();

		models.add(Layout.class.getName());
		models.add(UserPersonalSite.class.getName());

		models.add(getMBDiscussionCombinedClassName(BlogsEntry.class));
		models.add(getMBDiscussionCombinedClassName(WikiPage.class));

		for (String model : models) {
			ClassNameModel classNameModel = new ClassNameModelImpl();

			classNameModel.setClassNameId(counter.get());
			classNameModel.setValue(model);

			_classNameModels.put(model, classNameModel);
		}
	}

	private static void _initCommerceIds() {
		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			long[] cpDefinitionIds = new long
				[BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT];

			cProductIds.add(counter.get());

			for (int i = 0;
				 i <
					 BenchmarksPropsValues.
						 MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
				 i++) {

				cpDefinitionIds[i] = counter.get();
				cpDefinitionLocalizationNames.put(
					cpDefinitionIds[i], "Definition " + cpDefinitionIds[i]);
			}

			publishedCPDefinitionIds.add(
				cpDefinitionIds
					[BenchmarksPropsValues.
						MAX_COMMERCE_PRODUCT_DEFINITION_COUNT - 1]);

			cpDefinitionIdList.add(cpDefinitionIds);
		}
	}

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/data/";

	private static final SimpleCounter _FUTURE_COUNTER = new SimpleCounter();

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

	private static final Map<String, ClassNameModel> _classNameModels =
		new HashMap<>();

	static {
		counter = new SimpleCounter(BenchmarksPropsValues.MAX_GROUP_COUNT + 1);

		_initClassNameModels();
		_initCommerceIds();

		assetClassNameIds[0] = getClassNameId(BlogsEntry.class);
		assetClassNameIds[1] = getClassNameId(JournalArticle.class);
		assetClassNameIds[2] = getClassNameId(WikiPage.class);

		COMPANY_ID = counter.get();
		DEFAULT_USER_ID = counter.get();
		GLOBAL_GROUP_ID = counter.get();
		GUEST_GROUP_ID = counter.get();
		SAMPLE_USER_ID = counter.get();
		DEFAULT_JOURNAL_DDM_STRUCTURE_ID = counter.get();
		COMMERCE_CATALOG_GROUP_ID = counter.get();
		COMMERCE_CHANNEL_GROUP_ID = counter.get();
		COMMERCE_CHANNEL_ID = counter.get();
		COMMERCE_CATALOG_ID = counter.get();
		ADMINISTRATOR_ROLE_ID = counter.get();
		GUEST_ROLE_ID = counter.get();
		OWNER_ROLE_ID = counter.get();
		POWER_USER_ROLE_ID = counter.get();
		SITE_MEMBER_ROLE_ID = counter.get();
		USER_ROLE_ID = counter.get();
	}

}