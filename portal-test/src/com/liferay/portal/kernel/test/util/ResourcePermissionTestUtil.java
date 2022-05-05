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

package com.liferay.portal.kernel.test.util;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionTable;
import com.liferay.portal.kernel.model.ResourcedModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.List;

/**
 * @author Alberto Chaparro
 */
public class ResourcePermissionTestUtil {

	public static ResourcePermission addResourcePermission(
			long actionIds, String name, long roleId)
		throws Exception {

		return addResourcePermission(
			actionIds, name, RandomTestUtil.randomString(), roleId,
			RandomTestUtil.nextInt());
	}

	public static ResourcePermission addResourcePermission(
			long actionIds, String name, String primKey, int scope)
		throws Exception {

		return addResourcePermission(
			actionIds, name, primKey, RandomTestUtil.nextInt(), scope);
	}

	public static ResourcePermission addResourcePermission(
			long actionIds, String name, String primKey, long roleId, int scope)
		throws Exception {

		long resourcePermissionId = CounterLocalServiceUtil.increment(
			ResourcePermission.class.getName());

		ResourcePermission resourcePermission =
			ResourcePermissionLocalServiceUtil.createResourcePermission(
				resourcePermissionId);

		resourcePermission.setCompanyId(TestPropsValues.getCompanyId());
		resourcePermission.setName(name);
		resourcePermission.setScope(scope);
		resourcePermission.setPrimKey(primKey);
		resourcePermission.setPrimKeyId(GetterUtil.getLong(primKey));
		resourcePermission.setRoleId(roleId);
		resourcePermission.setActionIds(actionIds);
		resourcePermission.setViewActionId((actionIds % 2) == 1);

		return ResourcePermissionLocalServiceUtil.addResourcePermission(
			resourcePermission);
	}

	public static void deleteResourcePermissions(PersistedModel persistedModel)
		throws Exception {

		if (persistedModel instanceof Company) {
			Company company = (Company)persistedModel;

			_deleteResourcePermissions(
				company.getCompanyId(), ResourceConstants.SCOPE_COMPANY,
				String.valueOf(company.getCompanyId()));

			_deleteResourcePermissions(
				company.getCompanyId(), ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(company.getCompanyId()));

			return;
		}

		if (persistedModel instanceof Group) {
			Group group = (Group)persistedModel;

			_deleteResourcePermissions(
				group.getCompanyId(), ResourceConstants.SCOPE_GROUP,
				String.valueOf(group.getGroupId()));

			_deleteResourcePermissions(
				group.getCompanyId(), ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(group.getGroupId()));

			return;
		}

		if (!(persistedModel instanceof BaseModel) ||
			!(persistedModel instanceof ShardedModel)) {

			return;
		}

		BaseModel<?> baseModel = (BaseModel)persistedModel;

		ShardedModel shardedModel = (ShardedModel)baseModel;

		ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
			shardedModel.getCompanyId(), baseModel.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(baseModel.getPrimaryKeyObj()));

		if (!(persistedModel instanceof ResourcedModel)) {
			return;
		}

		ResourcedModel resourcedModel = (ResourcedModel)baseModel;

		ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
			shardedModel.getCompanyId(), baseModel.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(resourcedModel.getResourcePrimKey()));
	}

	private static void _deleteResourcePermissions(
			long companyId, int scope, String primKey)
		throws Exception {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			ResourcePermissionTable.INSTANCE.resourcePermissionId
		).from(
			ResourcePermissionTable.INSTANCE
		).where(
			ResourcePermissionTable.INSTANCE.companyId.eq(
				companyId
			).and(
				ResourcePermissionTable.INSTANCE.scope.eq(scope)
			).and(
				ResourcePermissionTable.INSTANCE.primKey.eq(primKey)
			)
		);

		for (long resourcePermissionId :
				ResourcePermissionLocalServiceUtil.<List<Long>>dslQuery(
					dslQuery)) {

			ResourcePermissionLocalServiceUtil.deleteResourcePermission(
				resourcePermissionId);
		}
	}

}