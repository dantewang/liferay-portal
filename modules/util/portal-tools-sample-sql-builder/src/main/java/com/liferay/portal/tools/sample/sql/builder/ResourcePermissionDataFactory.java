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
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceCatalogModel;
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
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBCategoryModel;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassNameModel;
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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class ResourcePermissionDataFactory extends BaseDataFactory {

	public ResourcePermissionDataFactory(
		DataFactoryContext dataFactoryContext,
		CommerceDataFactory commerceDataFactory,
		UserDataFactory userDataFactory) {

		super(dataFactoryContext);

		_commerceDataFactory = commerceDataFactory;
		_userDataFactory = userDataFactory;
	}

	public ResourcePermissionModel commerceCatalogResourcePermission() {
		CommerceCatalogModel commerceCatalogModel =
			_commerceDataFactory.getCommerceCatalogModel();

		RoleModel guestRoleModel = _userDataFactory.getGuestRoleModel();

		return _newResourcePermissionModel(
			CommerceCatalog.class.getName(),
			String.valueOf(commerceCatalogModel.getCommerceCatalogId()),
			guestRoleModel.getRoleId(), dataFactoryContext.getSampleUserId());
	}

	public SimpleCounter getResourcePermissionCounter() {
		return _resourcePermissionCounter;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		return newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		long defaultUserId = dataFactoryContext.getDefaultUserId();
		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		if (assetVocabularyModel.getUserId() == defaultUserId) {
			return Collections.singletonList(
				_newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					ownerRoleModel.getRoleId(), defaultUserId));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			_newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				ownerRoleModel.getRoleId(),
				dataFactoryContext.getDefaultUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();
		RoleModel guestRoleModel = _userDataFactory.getGuestRoleModel();
		RoleModel userRoleModel = _userDataFactory.getUserRoleModel();

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMStructure.class.getName(),
			getClassName(ddmStructureModel.getClassNameId()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, guestRoleModel.getRoleId(), 0));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, ownerRoleModel.getRoleId(),
				ddmStructureModel.getUserId()));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, userRoleModel.getRoleId(), 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();
		RoleModel guestRoleModel = _userDataFactory.getGuestRoleModel();
		RoleModel userRoleModel = _userDataFactory.getUserRoleModel();

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMTemplate.class.getName(),
			getClassName(ddmTemplateModel.getResourceClassNameId()));
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, guestRoleModel.getRoleId(), 0));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, ownerRoleModel.getRoleId(),
				ddmTemplateModel.getUserId()));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, userRoleModel.getRoleId(), 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			_newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				ownerRoleModel.getRoleId(),
				dataFactoryContext.getSampleUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		return newResourcePermissionModels(
			Layout.class.getName(), String.valueOf(layoutModel.getPlid()), 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		return newResourcePermissionModels(
			MBCategory.class.getName(),
			String.valueOf(mbCategoryModel.getCategoryId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return newResourcePermissionModels(
			MBMessage.class.getName(),
			String.valueOf(mbMessageModel.getMessageId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
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

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			_newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				ownerRoleModel.getRoleId(),
				dataFactoryContext.getSampleUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			_newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				ownerRoleModel.getRoleId(), userModel.getUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		return newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			dataFactoryContext.getSampleUserId());
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		return newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()),
			dataFactoryContext.getSampleUserId());
	}

	public String toInsertSQL(BaseModel<?> baseModel) {
		try {
			StringBundler sb = new StringBundler();

			_toInsertSQL(sb, baseModel);

			Class<?> clazz = baseModel.getClass();

			for (Class<?> modelClass : clazz.getInterfaces()) {
				try {
					Method method =
						ResourcePermissionDataFactory.class.getMethod(
							"newResourcePermissionModels", modelClass);

					for (ResourcePermissionModel resourcePermissionModel :
							(List<ResourcePermissionModel>)method.invoke(
								this, baseModel)) {

						sb.append("\n");

						_toInsertSQL(sb, resourcePermissionModel);
					}
				}
				catch (NoSuchMethodException nsme) {
				}
			}

			return sb.toString();
		}
		catch (ReflectiveOperationException roe) {
			return ReflectionUtil.throwException(roe);
		}
	}

	protected String getClassName(long classNameId) {
		for (ClassNameModel classNameModel :
				dataFactoryContext.getClassNameModelValues()) {

			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	protected List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();
		RoleModel guestRoleModel = _userDataFactory.getGuestRoleModel();
		RoleModel siteMemberRoleModel =
			_userDataFactory.getSiteMemberRoleModel();

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, guestRoleModel.getRoleId(), 0));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, ownerRoleModel.getRoleId(), ownerId));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, siteMemberRoleModel.getRoleId(), 0));

		return resourcePermissionModels;
	}

	private String _getResourcePermissionModelName(String... classNames) {
		if (ArrayUtil.isEmpty(classNames)) {
			return StringPool.BLANK;
		}

		Arrays.sort(classNames);

		StringBundler sb = new StringBundler(classNames.length * 2);

		for (String className : classNames) {
			sb.append(className);
			sb.append(StringPool.DASH);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private ResourcePermissionModel _newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		resourcePermissionModel.setResourcePermissionId(
			_resourcePermissionCounter.get());
		resourcePermissionModel.setCompanyId(dataFactoryContext.getCompanyId());
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

	private void _toInsertSQL(StringBundler sb, BaseModel<?> baseModel) {
		try {
			sb.append("insert into ");

			Class<?> clazz = baseModel.getClass();

			Field tableNameField = clazz.getField("TABLE_NAME");

			sb.append(tableNameField.get(null));

			sb.append(" values (");

			Field tableColumnsField = clazz.getField("TABLE_COLUMNS");

			for (Object[] tableColumn :
					(Object[][])tableColumnsField.get(null)) {

				String name = TextFormatter.format(
					(String)tableColumn[0], TextFormatter.G);

				if (name.endsWith(StringPool.UNDERLINE)) {
					name = name.substring(0, name.length() - 1);
				}

				int type = (int)tableColumn[1];

				if (type == Types.TIMESTAMP) {
					Method method = clazz.getMethod("get".concat(name));

					Date date = (Date)method.invoke(baseModel);

					if (date == null) {
						sb.append("null");
					}
					else {
						sb.append("'");
						sb.append(dataFactoryContext.getDateString(date));
						sb.append("'");
					}
				}
				else if ((type == Types.VARCHAR) || (type == Types.CLOB)) {
					Method method = clazz.getMethod("get".concat(name));

					sb.append("'");
					sb.append(method.invoke(baseModel));
					sb.append("'");
				}
				else if (type == Types.BOOLEAN) {
					Method method = clazz.getMethod("is".concat(name));

					sb.append(method.invoke(baseModel));
				}
				else {
					Method method = clazz.getMethod("get".concat(name));

					sb.append(method.invoke(baseModel));
				}

				sb.append(", ");
			}

			sb.setIndex(sb.index() - 1);

			sb.append(");");
		}
		catch (ReflectiveOperationException roe) {
			ReflectionUtil.throwException(roe);
		}
	}

	private final CommerceDataFactory _commerceDataFactory;
	private final SimpleCounter _resourcePermissionCounter =
		new SimpleCounter();
	private final UserDataFactory _userDataFactory;

}