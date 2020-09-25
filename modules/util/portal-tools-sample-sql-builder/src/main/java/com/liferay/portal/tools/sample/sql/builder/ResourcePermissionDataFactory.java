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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.sql.Types;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Lily Chi
 */
public class ResourcePermissionDataFactory extends BaseDataFactory {

	public static ResourcePermissionDataFactory getInstance() {
		return _resourcePermissionDataFactory;
	}

	public ResourcePermissionModel newCommerceCatalogResourcePermissionModel(
		CommerceCatalogModel commerceCatalogModel) {

		return _newResourcePermissionModel(
			CommerceCatalog.class.getName(),
			String.valueOf(commerceCatalogModel.getCommerceCatalogId()),
			GUEST_ROLE_ID, SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		return _newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() == DEFAULT_USER_ID) {
			return Collections.singletonList(
				_newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					OWNER_ROLE_ID, DEFAULT_USER_ID));
		}

		return _newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return _newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return Collections.singletonList(
			_newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				OWNER_ROLE_ID, DEFAULT_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel, String className) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMStructure.class.getName(), className);
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			_newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, OWNER_ROLE_ID, ddmStructureModel.getUserId()));
		resourcePermissionModels.add(
			_newResourcePermissionModel(name, primKey, USER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel, String className) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMTemplate.class.getName(), className);
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			_newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			_newResourcePermissionModel(
				name, primKey, OWNER_ROLE_ID, ddmTemplateModel.getUserId()));
		resourcePermissionModels.add(
			_newResourcePermissionModel(name, primKey, USER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return _newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return _newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			_newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				OWNER_ROLE_ID, SAMPLE_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return _newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		return _newResourcePermissionModels(
			Layout.class.getName(), String.valueOf(layoutModel.getPlid()), 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		return _newResourcePermissionModels(
			MBCategory.class.getName(),
			String.valueOf(mbCategoryModel.getCategoryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return _newResourcePermissionModels(
			MBMessage.class.getName(),
			String.valueOf(mbMessageModel.getMessageId()), SAMPLE_USER_ID);
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

		return _newResourcePermissionModels(name, primKey, 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		return Collections.singletonList(
			_newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				OWNER_ROLE_ID, SAMPLE_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return _newResourcePermissionModels(
			name, String.valueOf(primKey), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			_newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				OWNER_ROLE_ID, userModel.getUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		return _newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		return _newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()), SAMPLE_USER_ID);
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
				catch (NoSuchMethodException noSuchMethodException) {
				}
			}

			return sb.toString();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			return ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	public String toInsertSQL(BaseModel<?> baseModel, String className) {
		try {
			StringBundler sb = new StringBundler();

			_toInsertSQL(sb, baseModel);

			Class<?> clazz = baseModel.getClass();

			for (Class<?> modelClass : clazz.getInterfaces()) {
				try {
					Class<?>[] classArg = new Class<?>[2];

					classArg[0] = modelClass;
					classArg[1] = String.class;

					Method method =
						ResourcePermissionDataFactory.class.getMethod(
							"newResourcePermissionModels", classArg);

					for (ResourcePermissionModel resourcePermissionModel :
							(List<ResourcePermissionModel>)method.invoke(
								this, baseModel, className)) {

						sb.append("\n");

						_toInsertSQL(sb, resourcePermissionModel);
					}
				}
				catch (NoSuchMethodException noSuchMethodException) {
				}
			}

			return sb.toString();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			return ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	public String toInsertSQL(
		String mappingTableName, long companyId, long leftPrimaryKey,
		long rightPrimaryKey) {

		StringBundler sb = new StringBundler(9);

		sb.append("insert into ");
		sb.append(mappingTableName);
		sb.append(" values (");
		sb.append(companyId);
		sb.append(", ");
		sb.append(leftPrimaryKey);
		sb.append(", ");
		sb.append(rightPrimaryKey);
		sb.append(", 0, null);");

		return sb.toString();
	}

	private ResourcePermissionDataFactory() {
		_simpleDateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", TimeZone.getDefault());
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

	private List<ResourcePermissionModel> _newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			_newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			_newResourcePermissionModel(name, primKey, OWNER_ROLE_ID, ownerId));
		resourcePermissionModels.add(
			_newResourcePermissionModel(name, primKey, SITE_MEMBER_ROLE_ID, 0));

		return resourcePermissionModels;
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
				else if (name.equals("DeliverySubTypeSettings")) {
					name = "DeliverySubscriptionTypeSettings";
				}
				else if (name.equals("LPageTemplateStructureRelId")) {
					name = "LayoutPageTemplateStructureRelId";
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
						sb.append(_simpleDateFormat.format(date));
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
		catch (ReflectiveOperationException reflectiveOperationException) {
			ReflectionUtil.throwException(reflectiveOperationException);
		}
	}

	private static ResourcePermissionDataFactory
		_resourcePermissionDataFactory = new ResourcePermissionDataFactory();

	private final Format _simpleDateFormat;

}