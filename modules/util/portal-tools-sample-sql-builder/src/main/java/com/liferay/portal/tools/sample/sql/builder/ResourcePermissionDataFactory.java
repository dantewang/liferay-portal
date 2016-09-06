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

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.model.BlogsEntryModel;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.message.boards.kernel.model.MBCategory;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lily Chi
 */
public class ResourcePermissionDataFactory {

	public static ResourcePermissionModel newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId,
		long resourcePermissionId, long companyId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		resourcePermissionModel.setResourcePermissionId(resourcePermissionId);
		resourcePermissionModel.setCompanyId(companyId);
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

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		return newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		AssetTagModel assetTagModel) {

		return newResourcePermissionModels(
			AssetTag.class.getName(), String.valueOf(assetTagModel.getTagId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() ==
				InitContextUtil.getDefaultUserId()) {

			return Collections.singletonList(
				newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					InitContextUtil.getOwnerRoleModel().getRoleId(),
					InitContextUtil.getDefaultUserId(),
					InitContextUtil.getResourcePermissionCounter().get(),
					InitContextUtil.getCompanyId()));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getDefaultUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = InitDataFactoryUtil.getResourcePermissionModelName(
			DDMStructure.class.getName(),
			InitDataFactoryUtil.getClassName(
				ddmStructureModel.getClassNameId(),
				InitContextUtil.getClassNameModels()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getGuestRoleModel().getRoleId(),
				0, InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getOwnerRoleModel().getRoleId(),
				ddmStructureModel.getUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getUserRoleModel().getRoleId(),
				0, InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));

		return resourcePermissionModels;
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = InitDataFactoryUtil.getResourcePermissionModelName(
			DDMTemplate.class.getName(), InitDataFactoryUtil.getClassName(
				ddmTemplateModel.getResourceClassNameId(),
				InitContextUtil.getClassNameModels()));
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getGuestRoleModel().getRoleId(),
				0, InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getOwnerRoleModel().getRoleId(),
				ddmTemplateModel.getUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getUserRoleModel().getRoleId(),
				0, InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));

		return resourcePermissionModels;
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getSampleUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		return newResourcePermissionModels(
			Layout.class.getName(), String.valueOf(layoutModel.getPlid()), 0);
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		return newResourcePermissionModels(
			MBCategory.class.getName(),
			String.valueOf(mbCategoryModel.getCategoryId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				MBMessage.class.getName(),
				String.valueOf(mbMessageModel.getMessageId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getSampleUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		PortletPreferencesModel portletPreferencesModel) {

		String portletId = portletPreferencesModel.getPortletId();

		String name = portletId;

		int index = portletId.indexOf(StringPool.UNDERLINE);

		if (index > 0) {
			name = portletId.substring(0, index);
		}

		String primKey = PortletPermissionUtil.getPrimaryKey(
			portletPreferencesModel.getPlid(), portletId);

		return newResourcePermissionModels(name, primKey, 0);
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				InitContextUtil.getSampleUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey), InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getGuestRoleModel().getRoleId(),
				0, InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, InitContextUtil.getOwnerRoleModel().getRoleId(),
				ownerId, InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey,
				InitContextUtil.getSiteMemberRoleModel().getRoleId(), 0,
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));

		return resourcePermissionModels;
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				InitContextUtil.getOwnerRoleModel().getRoleId(),
				userModel.getUserId(),
				InitContextUtil.getResourcePermissionCounter().get(),
				InitContextUtil.getCompanyId()));
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		return newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			InitContextUtil.getSampleUserId());
	}

	public static List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		return newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()),
			InitContextUtil.getSampleUserId());
	}

}