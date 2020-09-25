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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.util.SimpleCounter;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public abstract class BaseDataFactory {

	public Date nextFutureDate() {
		return new Date(_FUTURE_TIME + (_FUTURE_COUNTER.get() * Time.SECOND));
	}

	protected InputStream getResourceInputStream(String resourceName) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			_DEPENDENCIES_DIR + resourceName);
	}

	protected ResourcePermissionModel newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		// PK fields

		resourcePermissionModel.setResourcePermissionId(
			resourcePermissionCounter.get());

		// Audit fields

		resourcePermissionModel.setCompanyId(COMPANY_ID);

		// Other fields

		resourcePermissionModel.setName(name);
		resourcePermissionModel.setScope(ResourceConstants.SCOPE_INDIVIDUAL);
		resourcePermissionModel.setPrimKey(primKey);
		resourcePermissionModel.setPrimKeyId(GetterUtil.getLong(primKey));
		resourcePermissionModel.setRoleId(roleId);
		resourcePermissionModel.setOwnerId(ownerId);
		resourcePermissionModel.setActionIds(1);
		resourcePermissionModel.setViewActionId(true);

		return resourcePermissionModel;
	}

	protected List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, OWNER_ROLE_ID, ownerId));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, SITE_MEMBER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	protected String readFile(String resourceName) {
		List<String> lines = new ArrayList<>();

		try {
			StringUtil.readLines(getResourceInputStream(resourceName), lines);
		}
		catch (IOException ioException) {
			ioException.getStackTrace();
		}

		return StringUtil.merge(lines, StringPool.SPACE);
	}

	protected static final long ADMINISTRATOR_ROLE_ID =
		BaseDataFactory.counter.get();

	protected static final long COMPANY_ID = BaseDataFactory.counter.get();

	protected static final long DEFAULT_JOURNAL_DDM_STRUCTURE_ID =
		BaseDataFactory.counter.get();

	protected static final long DEFAULT_USER_ID = BaseDataFactory.counter.get();

	protected static final long GLOBAL_GROUP_ID = BaseDataFactory.counter.get();

	protected static final long GUEST_GROUP_ID = BaseDataFactory.counter.get();

	protected static final long GUEST_ROLE_ID = BaseDataFactory.counter.get();

	protected static final long OWNER_ROLE_ID = BaseDataFactory.counter.get();

	protected static final long POWER_USER_ROLE_ID =
		BaseDataFactory.counter.get();

	protected static final long SAMPLE_USER_ID = BaseDataFactory.counter.get();

	protected static final String SAMPLE_USER_NAME = "Sample";

	protected static final long SITE_MEMBER_ROLE_ID =
		BaseDataFactory.counter.get();

	protected static final long USER_ROLE_ID = BaseDataFactory.counter.get();

	protected static final Map<Long, List<AssetCategoryModel>>[]
		assetCategoryModelsMaps =
			(Map<Long, List<AssetCategoryModel>>[])
				new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
	protected static final Map<Long, List<AssetTagModel>>[] assetTagModelsMaps =
		(Map<Long, List<AssetTagModel>>[])
			new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
	protected static final SimpleCounter counter = new SimpleCounter(
		BenchmarksPropsValues.MAX_GROUP_COUNT + 1);
	protected static final Map<Long, String> journalArticleResourceUUIDs =
		new HashMap<>();
	protected static final SimpleCounter resourcePermissionCounter =
		new SimpleCounter();
	protected static final SimpleCounter socialActivityCounter =
		new SimpleCounter();

	private static final String _DEPENDENCIES_DIR =
		"com/liferay/portal/tools/sample/sql/builder/dependencies/data/";

	private static final SimpleCounter _FUTURE_COUNTER = new SimpleCounter();

	private static final long _FUTURE_TIME =
		System.currentTimeMillis() + Time.YEAR;

}