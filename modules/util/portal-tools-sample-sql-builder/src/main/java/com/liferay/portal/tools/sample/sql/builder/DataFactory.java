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
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.social.BlogsActivityKeys;
import com.liferay.commerce.currency.model.CommerceCurrencyModel;
import com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLocalizationModel;
import com.liferay.commerce.product.model.CPDefinitionModel;
import com.liferay.commerce.product.model.CPFriendlyURLEntryModel;
import com.liferay.commerce.product.model.CPInstanceModel;
import com.liferay.commerce.product.model.CPTaxCategoryModel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CProductModel;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceCatalogModel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelModel;
import com.liferay.commerce.product.model.impl.CPDefinitionLocalizationModelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionModelImpl;
import com.liferay.commerce.product.model.impl.CPFriendlyURLEntryModelImpl;
import com.liferay.commerce.product.model.impl.CPInstanceModelImpl;
import com.liferay.commerce.product.model.impl.CPTaxCategoryModelImpl;
import com.liferay.commerce.product.model.impl.CProductModelImpl;
import com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl;
import com.liferay.commerce.product.model.impl.CommerceChannelModelImpl;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollectionModel;
import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.fragment.model.FragmentEntryModel;
import com.liferay.fragment.model.impl.FragmentCollectionModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryModelImpl;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.hello.world.web.internal.constants.HelloWorldPortletKeys;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureModel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelModel;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl;
import com.liferay.layout.util.template.LayoutData;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBCategoryModel;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageModel;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBThreadModel;
import com.liferay.message.boards.social.MBActivityKeys;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.AccountModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyModel;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.ContactModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutSetModel;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.model.ReleaseModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.model.UserPersonalSite;
import com.liferay.portal.kernel.model.VirtualHostModel;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.AccountModelImpl;
import com.liferay.portal.model.impl.CompanyModelImpl;
import com.liferay.portal.model.impl.ContactModelImpl;
import com.liferay.portal.model.impl.GroupModelImpl;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;
import com.liferay.portal.model.impl.LayoutSetModelImpl;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portal.model.impl.ReleaseModelImpl;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.portal.model.impl.RoleModelImpl;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.portal.model.impl.VirtualHostModelImpl;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityModel;
import com.liferay.subscription.constants.SubscriptionConstants;
import com.liferay.subscription.model.SubscriptionModel;
import com.liferay.subscription.model.impl.SubscriptionModelImpl;
import com.liferay.util.SimpleCounter;
import com.liferay.view.count.model.ViewCountEntryModel;
import com.liferay.view.count.model.impl.ViewCountEntryModelImpl;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;
import com.liferay.wiki.model.WikiPageResourceModel;
import com.liferay.wiki.model.impl.WikiNodeModelImpl;
import com.liferay.wiki.model.impl.WikiPageModelImpl;
import com.liferay.wiki.model.impl.WikiPageResourceModelImpl;
import com.liferay.wiki.social.WikiActivityKeys;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.math.BigDecimal;

import java.sql.Types;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.portlet.PortletPreferences;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory extends BaseDDMDataFactory {

	public DataFactory() throws Exception {
		_simpleDateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", TimeZone.getDefault());

		_timeCounter = new SimpleCounter();
		_userScreenNameCounter = new SimpleCounter();

		_accountId = counter.get();
		_cpTaxCategoryId = counter.get();
		_userPersonalSiteGroupId = counter.get();

		_defaultAssetPublisherPortletPreferencesImpl =
			(PortletPreferencesImpl)portletPreferencesFactory.fromDefaultXML(
				readFile("default_asset_publisher_preference.xml"));

		initAssetCategoryModels();
		initAssetTagModels();

		initRoleModels();
		initUserNames();
	}

	public long getAdministratorRoleId() {
		return ADMINISTRATOR_ROLE_ID;
	}

	public List<Long> getAssetCategoryIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
			assetCategoryModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		if ((assetCategoryModelsMap == null) ||
			assetCategoryModelsMap.isEmpty()) {

			return Collections.emptyList();
		}

		List<AssetCategoryModel> assetCategoryModels =
			assetCategoryModelsMap.get(assetEntryModel.getClassNameId());

		if ((assetCategoryModels == null) || assetCategoryModels.isEmpty()) {
			return Collections.emptyList();
		}

		if (_assetCategoryCounters == null) {
			_assetCategoryCounters =
				(Map<Long, SimpleCounter>[])
					new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		}

		SimpleCounter counter = getSimpleCounter(
			_assetCategoryCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetCategoryIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT);

		for (int i = 0;
			 i < BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT;
			 i++) {

			int index = (int)counter.get() % assetCategoryModels.size();

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index);

			assetCategoryIds.add(assetCategoryModel.getCategoryId());
		}

		return assetCategoryIds;
	}

	public List<AssetCategoryModel> getAssetCategoryModels() {
		List<AssetCategoryModel> allAssetCategoryModels = new ArrayList<>();

		for (Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap :
				assetCategoryModelsMaps) {

			for (List<AssetCategoryModel> assetCategoryModels :
					assetCategoryModelsMap.values()) {

				allAssetCategoryModels.addAll(assetCategoryModels);
			}
		}

		return allAssetCategoryModels;
	}

	public List<Long> getAssetTagIds(AssetEntryModel assetEntryModel) {
		Map<Long, List<AssetTagModel>> assetTagModelsMap =
			assetTagModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		if ((assetTagModelsMap == null) || assetTagModelsMap.isEmpty()) {
			return Collections.emptyList();
		}

		List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
			assetEntryModel.getClassNameId());

		if ((assetTagModels == null) || assetTagModels.isEmpty()) {
			return Collections.emptyList();
		}

		if (_assetTagCounters == null) {
			_assetTagCounters =
				(Map<Long, SimpleCounter>[])
					new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		}

		SimpleCounter counter = getSimpleCounter(
			_assetTagCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetTagIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT);

		for (int i = 0;
			 i < BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT;
			 i++) {

			int index = (int)counter.get() % assetTagModels.size();

			AssetTagModel assetTagModel = assetTagModels.get(index);

			assetTagIds.add(assetTagModel.getTagId());
		}

		return assetTagIds;
	}

	public List<AssetTagModel> getAssetTagModels() {
		List<AssetTagModel> allAssetTagModels = new ArrayList<>();

		for (Map<Long, List<AssetTagModel>> assetTagModelsMap :
				assetTagModelsMaps) {

			for (List<AssetTagModel> assetTagModels :
					assetTagModelsMap.values()) {

				allAssetTagModels.addAll(assetTagModels);
			}
		}

		return allAssetTagModels;
	}

	public List<AssetVocabularyModel> getAssetVocabularyModels() {
		List<AssetVocabularyModel> allAssetVocabularyModels = new ArrayList<>();

		allAssetVocabularyModels.add(_defaultAssetVocabularyModel);

		for (List<AssetVocabularyModel> assetVocabularyModels :
				_assetVocabularyModelsArray) {

			allAssetVocabularyModels.addAll(assetVocabularyModels);
		}

		return allAssetVocabularyModels;
	}

	public long getCounterNext() {
		return counter.get();
	}

	public int getMaxAssetPublisherPageCount() {
		return BenchmarksPropsValues.MAX_ASSETPUBLISHER_PAGE_COUNT;
	}

	public int getMaxGroupCount() {
		return BenchmarksPropsValues.MAX_GROUP_COUNT;
	}

	public int getMaxWikiPageCommentCount() {
		return BenchmarksPropsValues.MAX_WIKI_PAGE_COMMENT_COUNT;
	}

	public List<Long> getNewUserGroupIds(
		long groupId, GroupModel guestGroupModel) {

		List<Long> groupIds = new ArrayList<>(
			BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT + 1);

		groupIds.add(guestGroupModel.getGroupId());

		if ((groupId + BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT) >
				BenchmarksPropsValues.MAX_GROUP_COUNT) {

			groupId =
				groupId - BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT + 1;
		}

		for (int i = 0; i < BenchmarksPropsValues.MAX_USER_TO_GROUP_COUNT;
			 i++) {

			groupIds.add(groupId + i);
		}

		return groupIds;
	}

	public long getNextAssetClassNameId(long groupId) {
		Integer index = assetClassNameIdsIndexes.get(groupId);

		if (index == null) {
			index = 0;
		}

		long classNameId = assetClassNameIds[index % assetClassNameIds.length];

		assetClassNameIdsIndexes.put(groupId, ++index);

		return classNameId;
	}

	public String getPortletId(String portletPrefix) {
		return portletPrefix.concat(PortletIdCodec.generateInstanceId());
	}

	public long getPowerUserRoleId() {
		return POWER_USER_ROLE_ID;
	}

	public List<RoleModel> getRoleModels() {
		return _roleModels;
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public long getUserRoleId() {
		return USER_ROLE_ID;
	}

	public long getWikiPageClassNameId() {
		return getClassNameId(WikiPage.class);
	}

	public void initAssetCategoryModels() {
		_assetCategoryModelsArray =
			(List<AssetCategoryModel>[])
				new List<?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		assetCategoryModelsMaps =
			(Map<Long, List<AssetCategoryModel>>[])
				new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		_assetVocabularyModelsArray =
			(List<AssetVocabularyModel>[])
				new List<?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		_defaultAssetVocabularyModel = newAssetVocabularyModel(
			GLOBAL_GROUP_ID, DEFAULT_USER_ID, null,
			PropsValues.ASSET_VOCABULARY_DEFAULT);

		StringBundler sb = new StringBundler(4);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>(
				BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT);
			List<AssetCategoryModel> assetCategoryModels = new ArrayList<>(
				BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT *
					BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT);

			for (int j = 0;
				 j < BenchmarksPropsValues.MAX_ASSET_VUCABULARY_COUNT; j++) {

				sb.setIndex(0);

				sb.append("TestVocabulary_");
				sb.append(i);
				sb.append(StringPool.UNDERLINE);
				sb.append(j);

				AssetVocabularyModel assetVocabularyModel =
					newAssetVocabularyModel(
						i, SAMPLE_USER_ID, SAMPLE_USER_NAME, sb.toString());

				assetVocabularyModels.add(assetVocabularyModel);

				for (int k = 0;
					 k < BenchmarksPropsValues.MAX_ASSET_CATEGORY_COUNT; k++) {

					sb.setIndex(0);

					sb.append("TestCategory_");
					sb.append(assetVocabularyModel.getVocabularyId());
					sb.append(StringPool.UNDERLINE);
					sb.append(k);

					assetCategoryModels.add(
						newAssetCategoryModel(
							i, sb.toString(),
							assetVocabularyModel.getVocabularyId()));
				}
			}

			_assetCategoryModelsArray[i - 1] = assetCategoryModels;
			_assetVocabularyModelsArray[i - 1] = assetVocabularyModels;

			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				new HashMap<>();

			int pageSize =
				assetCategoryModels.size() / assetClassNameIds.length;

			for (int j = 0; j < assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;

				int toIndex = (j + 1) * pageSize;

				if (j == (assetClassNameIds.length - 1)) {
					toIndex = assetCategoryModels.size();
				}

				assetCategoryModelsMap.put(
					assetClassNameIds[j],
					assetCategoryModels.subList(fromIndex, toIndex));
			}

			assetCategoryModelsMaps[i - 1] = assetCategoryModelsMap;
		}
	}

	public void initAssetTagModels() {
		_assetTagModelsArray =
			(List<AssetTagModel>[])
				new List<?>[BenchmarksPropsValues.MAX_GROUP_COUNT];
		assetTagModelsMaps =
			(Map<Long, List<AssetTagModel>>[])
				new HashMap<?, ?>[BenchmarksPropsValues.MAX_GROUP_COUNT];

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			List<AssetTagModel> assetTagModels = new ArrayList<>(
				BenchmarksPropsValues.MAX_ASSET_TAG_COUNT);

			for (int j = 0; j < BenchmarksPropsValues.MAX_ASSET_TAG_COUNT;
				 j++) {

				AssetTagModel assetTagModel = new AssetTagModelImpl();

				assetTagModel.setUuid(SequentialUUID.generate());
				assetTagModel.setTagId(counter.get());
				assetTagModel.setGroupId(i);
				assetTagModel.setCompanyId(COMPANY_ID);
				assetTagModel.setUserId(SAMPLE_USER_ID);
				assetTagModel.setUserName(SAMPLE_USER_NAME);
				assetTagModel.setCreateDate(new Date());
				assetTagModel.setModifiedDate(new Date());
				assetTagModel.setName(
					StringBundler.concat("TestTag_", i, "_", j));
				assetTagModel.setLastPublishDate(new Date());

				assetTagModels.add(assetTagModel);
			}

			_assetTagModelsArray[i - 1] = assetTagModels;

			Map<Long, List<AssetTagModel>> assetTagModelsMap = new HashMap<>();

			int pageSize = assetTagModels.size() / assetClassNameIds.length;

			for (int j = 0; j < assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;

				int toIndex = (j + 1) * pageSize;

				if (j == (assetClassNameIds.length - 1)) {
					toIndex = assetTagModels.size();
				}

				assetTagModelsMap.put(
					assetClassNameIds[j],
					assetTagModels.subList(fromIndex, toIndex));
			}

			assetTagModelsMaps[i - 1] = assetTagModelsMap;
		}
	}

	public void initRoleModels() {
		_roleModels = new ArrayList<>();

		// Administrator

		_administratorRoleModel = newRoleModel(
			RoleConstants.ADMINISTRATOR, RoleConstants.TYPE_REGULAR,
			ADMINISTRATOR_ROLE_ID);

		_roleModels.add(_administratorRoleModel);

		// Guest

		_guestRoleModel = newRoleModel(
			RoleConstants.GUEST, RoleConstants.TYPE_REGULAR, GUEST_ROLE_ID);

		_roleModels.add(_guestRoleModel);

		// Organization Administrator

		_roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_ADMINISTRATOR,
				RoleConstants.TYPE_ORGANIZATION, counter.get()));

		// Organization Owner

		_roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_OWNER,
				RoleConstants.TYPE_ORGANIZATION, counter.get()));

		// Organization User

		_roleModels.add(
			newRoleModel(
				RoleConstants.ORGANIZATION_USER,
				RoleConstants.TYPE_ORGANIZATION, counter.get()));

		// Owner

		_ownerRoleModel = newRoleModel(
			RoleConstants.OWNER, RoleConstants.TYPE_REGULAR, OWNER_ROLE_ID);

		_roleModels.add(_ownerRoleModel);

		// Power User

		_powerUserRoleModel = newRoleModel(
			RoleConstants.POWER_USER, RoleConstants.TYPE_REGULAR,
			POWER_USER_ROLE_ID);

		_roleModels.add(_powerUserRoleModel);

		// Site Administrator

		_roleModels.add(
			newRoleModel(
				RoleConstants.SITE_ADMINISTRATOR, RoleConstants.TYPE_SITE,
				counter.get()));

		// Site Member

		_siteMemberRoleModel = newRoleModel(
			RoleConstants.SITE_MEMBER, RoleConstants.TYPE_SITE,
			SITE_MEMBER_ROLE_ID);

		_roleModels.add(_siteMemberRoleModel);

		// Site Owner

		_roleModels.add(
			newRoleModel(
				RoleConstants.SITE_OWNER, RoleConstants.TYPE_SITE,
				counter.get()));

		// User

		_userRoleModel = newRoleModel(
			RoleConstants.USER, RoleConstants.TYPE_REGULAR, USER_ROLE_ID);

		_roleModels.add(_userRoleModel);
	}

	public void initUserNames() throws IOException {
		_firstNames = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream("first_names.txt")));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			_firstNames.add(line);
		}

		unsyncBufferedReader.close();

		_lastNames = new ArrayList<>();

		unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(getResourceInputStream("last_names.txt")));

		while ((line = unsyncBufferedReader.readLine()) != null) {
			_lastNames.add(line);
		}

		unsyncBufferedReader.close();
	}

	public AccountModel newAccountModel() {
		AccountModel accountModel = new AccountModelImpl();

		accountModel.setAccountId(_accountId);
		accountModel.setCompanyId(COMPANY_ID);
		accountModel.setCreateDate(new Date());
		accountModel.setModifiedDate(new Date());
		accountModel.setName("Liferay");
		accountModel.setLegalName("Liferay, Inc.");

		return accountModel;
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), getClassNameId(BlogsEntry.class),
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(),
			getClassNameId(DLFileEntry.class),
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), getClassNameId(DLFolder.class),
			dLFolderModel.getFolderId(), dLFolderModel.getUuid(), 0, true, true,
			null, dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = getClassNameId(MBDiscussion.class);
		}
		else {
			classNameId = getClassNameId(MBMessage.class);
			visible = true;
		}

		return newAssetEntryModel(
			mbMessageModel.getGroupId(), mbMessageModel.getCreateDate(),
			mbMessageModel.getModifiedDate(), classNameId,
			mbMessageModel.getMessageId(), mbMessageModel.getUuid(), 0, true,
			visible, ContentTypes.TEXT_HTML, mbMessageModel.getSubject());
	}

	public AssetEntryModel newAssetEntryModel(MBThreadModel mbThreadModel) {
		return newAssetEntryModel(
			mbThreadModel.getGroupId(), mbThreadModel.getCreateDate(),
			mbThreadModel.getModifiedDate(), getClassNameId(MBThread.class),
			mbThreadModel.getThreadId(), mbThreadModel.getUuid(), 0, true,
			false, StringPool.BLANK,
			String.valueOf(mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		String resourceUUID = journalArticleResourceUUIDs.get(resourcePrimKey);

		return newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			getClassNameId(JournalArticle.class), resourcePrimKey, resourceUUID,
			DEFAULT_JOURNAL_DDM_STRUCTURE_ID, journalArticleModel.isIndexable(),
			true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), getClassNameId(WikiPage.class),
			wikiPageModel.getResourcePrimKey(), wikiPageModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_HTML, wikiPageModel.getTitle());
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, BlogsPortletKeys.BLOGS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, WikiPortletKeys.WIKI,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public GroupModel newCommerceCatalogGroupModel() {
		return newGroupModel(
			COMMERCE_CATALOG_GROUP_ID, getClassNameId(CommerceCatalog.class),
			COMMERCE_CATALOG_ID, _COMMERCE_CATALOG_NAME, false);
	}

	public CommerceCatalogModel newCommerceCatalogModel() {
		CommerceCatalogModel commerceCatalogModel =
			new CommerceCatalogModelImpl();

		commerceCatalogModel.setCommerceCatalogId(COMMERCE_CATALOG_ID);
		commerceCatalogModel.setCompanyId(COMPANY_ID);
		commerceCatalogModel.setUserName(SAMPLE_USER_NAME);
		commerceCatalogModel.setCreateDate(new Date());
		commerceCatalogModel.setModifiedDate(new Date());
		commerceCatalogModel.setName(_COMMERCE_CATALOG_NAME);
		commerceCatalogModel.setCommerceCurrencyCode(_COMMERCE_CURRENCY_CODE);
		commerceCatalogModel.setCatalogDefaultLanguageId("en_US");
		commerceCatalogModel.setSystem(true);

		return commerceCatalogModel;
	}

	public ResourcePermissionModel newCommerceCatalogResourcePermission() {
		return newResourcePermissionModel(
			CommerceCatalog.class.getName(),
			String.valueOf(COMMERCE_CATALOG_ID), GUEST_ROLE_ID, SAMPLE_USER_ID);
	}

	public GroupModel newCommerceChannelGroupModel() {
		return newGroupModel(
			COMMERCE_CHANNEL_GROUP_ID, getClassNameId(CommerceChannel.class),
			COMMERCE_CHANNEL_ID, _COMMERCE_CHANNEL_NAME, false);
	}

	public CommerceChannelModel newCommerceChannelModel() {
		CommerceChannelModel commerceChannelModel =
			new CommerceChannelModelImpl();

		commerceChannelModel.setCommerceChannelId(COMMERCE_CHANNEL_ID);
		commerceChannelModel.setCompanyId(COMPANY_ID);
		commerceChannelModel.setUserId(SAMPLE_USER_ID);
		commerceChannelModel.setUserName(SAMPLE_USER_NAME);
		commerceChannelModel.setCreateDate(new Date());
		commerceChannelModel.setModifiedDate(new Date());
		commerceChannelModel.setSiteGroupId(1);
		commerceChannelModel.setName(_COMMERCE_CHANNEL_NAME);
		commerceChannelModel.setType("site");
		commerceChannelModel.setTypeSettings(String.valueOf(GUEST_GROUP_ID));
		commerceChannelModel.setCommerceCurrencyCode(_COMMERCE_CURRENCY_CODE);

		return commerceChannelModel;
	}

	public CommerceCurrencyModel newCommerceCurrencyModel() {
		CommerceCurrencyModel commerceCurrencyModel =
			new CommerceCurrencyModelImpl();

		commerceCurrencyModel.setUuid(SequentialUUID.generate());
		commerceCurrencyModel.setCommerceCurrencyId(counter.get());
		commerceCurrencyModel.setCompanyId(COMPANY_ID);
		commerceCurrencyModel.setUserId(SAMPLE_USER_ID);
		commerceCurrencyModel.setUserName(SAMPLE_USER_NAME);
		commerceCurrencyModel.setCreateDate(new Date());
		commerceCurrencyModel.setModifiedDate(new Date());
		commerceCurrencyModel.setCode(_COMMERCE_CURRENCY_CODE);

		String name = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-locales",
			"=\"en_US\" default-locale=\"en_US\"><Name language-id=\"en_US\">",
			"US Dollar</Name></root>");

		commerceCurrencyModel.setName(name);

		commerceCurrencyModel.setRate(BigDecimal.valueOf(1));

		String formatPattern = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-locales",
			"=\"en_US\" default-locale=\"en_US\"><FormatPattern language-id",
			"=\"en_US\">$###,##0.00</FormatPattern></root>");

		commerceCurrencyModel.setFormatPattern(formatPattern);

		commerceCurrencyModel.setMaxFractionDigits(2);
		commerceCurrencyModel.setMinFractionDigits(2);
		commerceCurrencyModel.setRoundingMode("HALF_EVEN");
		commerceCurrencyModel.setPrimary(true);
		commerceCurrencyModel.setPriority(1);
		commerceCurrencyModel.setActive(true);
		commerceCurrencyModel.setLastPublishDate(new Date());

		return commerceCurrencyModel;
	}

	public CompanyModel newCompanyModel() {
		CompanyModel companyModel = new CompanyModelImpl();

		companyModel.setCompanyId(COMPANY_ID);
		companyModel.setAccountId(_accountId);
		companyModel.setWebId("liferay.com");
		companyModel.setMx("liferay.com");
		companyModel.setActive(true);

		return companyModel;
	}

	public ContactModel newContactModel(UserModel userModel) {
		ContactModel contactModel = new ContactModelImpl();

		contactModel.setContactId(userModel.getContactId());
		contactModel.setCompanyId(userModel.getCompanyId());
		contactModel.setUserId(userModel.getUserId());

		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		contactModel.setUserName(
			fullNameGenerator.getFullName(
				userModel.getFirstName(), userModel.getMiddleName(),
				userModel.getLastName()));

		contactModel.setCreateDate(new Date());
		contactModel.setModifiedDate(new Date());
		contactModel.setClassNameId(getClassNameId(User.class));
		contactModel.setClassPK(userModel.getUserId());
		contactModel.setAccountId(_accountId);
		contactModel.setParentContactId(
			ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contactModel.setEmailAddress(userModel.getEmailAddress());
		contactModel.setFirstName(userModel.getFirstName());
		contactModel.setLastName(userModel.getLastName());
		contactModel.setMale(true);
		contactModel.setBirthday(new Date());

		return contactModel;
	}

	public LayoutModel newContentLayoutModel(
		long groupId, String name, String fragmentEntries) {

		SimpleCounter simpleCounter = _layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			_layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());
		layoutModel.setPlid(counter.get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(COMPANY_ID);
		layoutModel.setUserId(SAMPLE_USER_ID);
		layoutModel.setUserName(SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());
		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_CONTENT);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsUnicodeProperties = new UnicodeProperties(
			true);

		typeSettingsUnicodeProperties.setProperty(
			"fragmentEntries", fragmentEntries);

		layoutModel.setTypeSettings(
			StringUtil.replace(
				typeSettingsUnicodeProperties.toString(), '\n', "\\n"));

		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	public List<LayoutModel> newContentLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		for (int i = 0; i < BenchmarksPropsValues.MAX_CONTENT_LAYOUT_COUNT;
			 i++) {

			layoutModels.add(
				newContentLayoutModel(
					groupId, i + "_web_content", "web_content"));
		}

		return layoutModels;
	}

	public List<CounterModel> newCounterModels() {
		List<CounterModel> counterModels = new ArrayList<>();

		// Counter

		CounterModel counterModel = new CounterModelImpl();

		counterModel.setName(Counter.class.getName());
		counterModel.setCurrentId(counter.get());

		counterModels.add(counterModel);

		// FriendlyURLEntryLocalization

		counterModel = new CounterModelImpl();

		counterModel.setName(FriendlyURLEntryLocalization.class.getName());
		counterModel.setCurrentId(counter.get());

		counterModels.add(counterModel);

		// ResourcePermission

		counterModel = new CounterModelImpl();

		counterModel.setName(ResourcePermission.class.getName());
		counterModel.setCurrentId(resourcePermissionCounter.get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(socialActivityCounter.get());

		counterModels.add(counterModel);

		return counterModels;
	}

	public List<AssetEntryModel> newCPDefinitionAssetEntryModels() {
		List<AssetEntryModel> assetEntryModels = new ArrayList<>();

		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			long[] cpDefinitionIds = cpDefinitionIdList.get(productIndex);

			for (int definitionIndex = 0;
				 definitionIndex <
					 BenchmarksPropsValues.
						 MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
				 definitionIndex++) {

				assetEntryModels.add(
					newAssetEntryModel(
						COMMERCE_CATALOG_GROUP_ID, new Date(), new Date(),
						getClassNameId(CPDefinition.class),
						cpDefinitionIds[definitionIndex],
						SequentialUUID.generate(), 0, true, true, "text/plain",
						cpDefinitionLocalizationNames.get(
							cpDefinitionIds[definitionIndex])));
			}
		}

		return assetEntryModels;
	}

	public List<CPDefinitionLocalizationModel>
		newCPDefinitionLocalizationModels() {

		List<CPDefinitionLocalizationModel> cpDefinitionLocalizationModels =
			new ArrayList<>();

		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			long[] cpDefinitionIds = cpDefinitionIdList.get(productIndex);

			for (int definitionIndex = 0;
				 definitionIndex <
					 BenchmarksPropsValues.
						 MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
				 definitionIndex++) {

				long cpDefinitionId = cpDefinitionIds[definitionIndex];

				cpDefinitionLocalizationModels.add(
					newCPDefinitionLocalizationModel(cpDefinitionId));
			}
		}

		return cpDefinitionLocalizationModels;
	}

	public List<CPDefinitionModel> newCPDefinitionModels() {
		List<CPDefinitionModel> cpDefinitionModels = new ArrayList<>();

		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			long[] cpDefinitionIds = cpDefinitionIdList.get(productIndex);

			for (int definitionIndex = 0;
				 definitionIndex <
					 BenchmarksPropsValues.
						 MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
				 definitionIndex++) {

				long cpDefinitionId = cpDefinitionIds[definitionIndex];

				cpDefinitionModels.add(
					newCPDefinitionModel(
						cpDefinitionId, cProductIds.get(productIndex),
						_cpTaxCategoryId, definitionIndex + 1));
			}
		}

		return cpDefinitionModels;
	}

	public List<CPFriendlyURLEntryModel> newCPFriendlyURLEntryModels() {
		List<CPFriendlyURLEntryModel> cpFriendlyURLEntryModels =
			new ArrayList<>();

		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			for (int definitionIndex = 0;
				 definitionIndex <
					 BenchmarksPropsValues.
						 MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
				 definitionIndex++) {

				cpFriendlyURLEntryModels.add(
					newCPFriendlyURLEntryModel(
						cProductIds.get(productIndex),
						publishedCPDefinitionIds.get(productIndex)));
			}
		}

		return cpFriendlyURLEntryModels;
	}

	public List<CPInstanceModel> newCPInstanceModels() {
		List<CPInstanceModel> cpInstanceModels = new ArrayList<>();

		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			long[] cpDefinitionIds = cpDefinitionIdList.get(productIndex);

			for (int definitionIndex = 0;
				 definitionIndex <
					 BenchmarksPropsValues.
						 MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
				 definitionIndex++) {

				long cpDefinitionId = cpDefinitionIds[definitionIndex];

				for (int instanceIndex = 0;
					 instanceIndex <
						 BenchmarksPropsValues.
							 MAX_COMMERCE_PRODUCT_INSTANCE_COUNT;
					 instanceIndex++) {

					cpInstanceModels.add(
						newCPInstanceModel(cpDefinitionId, instanceIndex));
				}
			}
		}

		return cpInstanceModels;
	}

	public List<CProductModel> newCProductModels() {
		List<CProductModel> cProductModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT);

		for (int productIndex = 0;
			 productIndex < BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
			 productIndex++) {

			cProductModels.add(
				newCProductModel(
					cProductIds.get(productIndex),
					publishedCPDefinitionIds.get(productIndex)));
		}

		return cProductModels;
	}

	public CPTaxCategoryModel newCPTaxCategoryModel() {
		CPTaxCategoryModel cpTaxCategoryModel = new CPTaxCategoryModelImpl();

		cpTaxCategoryModel.setCPTaxCategoryId(_cpTaxCategoryId);
		cpTaxCategoryModel.setCompanyId(COMPANY_ID);
		cpTaxCategoryModel.setUserId(SAMPLE_USER_ID);
		cpTaxCategoryModel.setUserName(SAMPLE_USER_NAME);
		cpTaxCategoryModel.setCreateDate(new Date());
		cpTaxCategoryModel.setModifiedDate(new Date());
		cpTaxCategoryModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\"><Name ",
				"language-id=\"en_US\">Normal Product</Name></root>"));

		cpTaxCategoryModel.setDescription(null);

		return cpTaxCategoryModel;
	}

	public List<PortletPreferencesModel> newDDLPortletPreferencesModels(
		long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public UserModel newDefaultUserModel() {
		return newUserModel(
			DEFAULT_USER_ID, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, true);
	}

	public FragmentCollectionModel newFragmentCollectionModel(long groupId) {
		FragmentCollectionModel fragmentCollectionModel =
			new FragmentCollectionModelImpl();

		fragmentCollectionModel.setUuid(SequentialUUID.generate());
		fragmentCollectionModel.setFragmentCollectionId(counter.get());
		fragmentCollectionModel.setGroupId(groupId);
		fragmentCollectionModel.setCompanyId(COMPANY_ID);
		fragmentCollectionModel.setUserId(SAMPLE_USER_ID);
		fragmentCollectionModel.setCreateDate(new Date());
		fragmentCollectionModel.setModifiedDate(new Date());
		fragmentCollectionModel.setFragmentCollectionKey("fragmentcollection");
		fragmentCollectionModel.setName("fragmentcollection");

		return fragmentCollectionModel;
	}

	public FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, FragmentEntryModel fragmentEntryModel) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		fragmentEntryLinkModel.setUuid(SequentialUUID.generate());
		fragmentEntryLinkModel.setFragmentEntryLinkId(counter.get());
		fragmentEntryLinkModel.setGroupId(fragmentEntryModel.getGroupId());
		fragmentEntryLinkModel.setCompanyId(COMPANY_ID);
		fragmentEntryLinkModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryLinkModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());
		fragmentEntryLinkModel.setFragmentEntryId(
			fragmentEntryModel.getFragmentEntryId());
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setCss(fragmentEntryModel.getCss());
		fragmentEntryLinkModel.setJs(fragmentEntryModel.getJs());
		fragmentEntryLinkModel.setHtml(fragmentEntryModel.getHtml());
		fragmentEntryLinkModel.setEditableValues(StringPool.BLANK);
		fragmentEntryLinkModel.setNamespace(StringUtil.randomId());
		fragmentEntryLinkModel.setPosition(0);

		return fragmentEntryLinkModel;
	}

	public FragmentEntryModel newFragmentEntryModel(
			long groupId, FragmentCollectionModel fragmentCollectionModel)
		throws Exception {

		FragmentEntryModel fragmentEntryModel = new FragmentEntryModelImpl();

		fragmentEntryModel.setUuid(SequentialUUID.generate());
		fragmentEntryModel.setFragmentEntryId(counter.get());
		fragmentEntryModel.setGroupId(groupId);
		fragmentEntryModel.setCompanyId(COMPANY_ID);
		fragmentEntryModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryModel.setCreateDate(new Date());
		fragmentEntryModel.setModifiedDate(new Date());
		fragmentEntryModel.setFragmentCollectionId(
			fragmentCollectionModel.getFragmentCollectionId());
		fragmentEntryModel.setFragmentEntryKey("web_content");
		fragmentEntryModel.setName("web_content");
		fragmentEntryModel.setCss(StringPool.BLANK);
		fragmentEntryModel.setHtml(readFile("web_content.html"));
		fragmentEntryModel.setJs(StringPool.BLANK);
		fragmentEntryModel.setType(FragmentConstants.TYPE_COMPONENT);
		fragmentEntryModel.setStatus(WorkflowConstants.STATUS_APPROVED);

		return fragmentEntryModel;
	}

	public GroupModel newGlobalGroupModel() {
		return newGroupModel(
			GLOBAL_GROUP_ID, getClassNameId(Company.class), COMPANY_ID,
			GroupConstants.GLOBAL, false);
	}

	public GroupModel newGroupModel(UserModel userModel) {
		return newGroupModel(
			counter.get(), getClassNameId(User.class), userModel.getUserId(),
			userModel.getScreenName(), false);
	}

	public List<GroupModel> newGroupModels() {
		List<GroupModel> groupModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_GROUP_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_GROUP_COUNT; i++) {
			groupModels.add(
				newGroupModel(
					i, getClassNameId(Group.class), i, "Site " + i, true));
		}

		return groupModels;
	}

	public GroupModel newGuestGroupModel() {
		return newGroupModel(
			GUEST_GROUP_ID, getClassNameId(Group.class), GUEST_GROUP_ID,
			GroupConstants.GUEST, true);
	}

	public UserModel newGuestUserModel() {
		return newUserModel(counter.get(), "Test", "Test", "Test", false);
	}

	public List<PortletPreferencesModel> newJournalPortletPreferencesModels(
		long plid) {

		return Collections.singletonList(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public LayoutFriendlyURLModel newLayoutFriendlyURLModel(
		LayoutModel layoutModel) {

		LayoutFriendlyURLModel layoutFriendlyURLEntryModel =
			new LayoutFriendlyURLModelImpl();

		layoutFriendlyURLEntryModel.setUuid(SequentialUUID.generate());
		layoutFriendlyURLEntryModel.setLayoutFriendlyURLId(counter.get());
		layoutFriendlyURLEntryModel.setGroupId(layoutModel.getGroupId());
		layoutFriendlyURLEntryModel.setCompanyId(COMPANY_ID);
		layoutFriendlyURLEntryModel.setUserId(SAMPLE_USER_ID);
		layoutFriendlyURLEntryModel.setUserName(SAMPLE_USER_NAME);
		layoutFriendlyURLEntryModel.setCreateDate(new Date());
		layoutFriendlyURLEntryModel.setModifiedDate(new Date());
		layoutFriendlyURLEntryModel.setPlid(layoutModel.getPlid());
		layoutFriendlyURLEntryModel.setFriendlyURL(
			layoutModel.getFriendlyURL());
		layoutFriendlyURLEntryModel.setLanguageId("en_US");
		layoutFriendlyURLEntryModel.setLastPublishDate(new Date());

		return layoutFriendlyURLEntryModel;
	}

	public LayoutModel newLayoutModel(
		long groupId, String name, String column1, String column2) {

		SimpleCounter simpleCounter = _layoutCounters.get(groupId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter();

			_layoutCounters.put(groupId, simpleCounter);
		}

		LayoutModel layoutModel = new LayoutModelImpl();

		layoutModel.setUuid(SequentialUUID.generate());
		layoutModel.setPlid(counter.get());
		layoutModel.setGroupId(groupId);
		layoutModel.setCompanyId(COMPANY_ID);
		layoutModel.setUserId(SAMPLE_USER_ID);
		layoutModel.setUserName(SAMPLE_USER_NAME);
		layoutModel.setCreateDate(new Date());
		layoutModel.setModifiedDate(new Date());
		layoutModel.setLayoutId(simpleCounter.get());
		layoutModel.setName(
			"<?xml version=\"1.0\"?><root><name>" + name + "</name></root>");
		layoutModel.setType(LayoutConstants.TYPE_PORTLET);
		layoutModel.setFriendlyURL(StringPool.FORWARD_SLASH + name);

		UnicodeProperties typeSettingsUnicodeProperties = new UnicodeProperties(
			true);

		typeSettingsUnicodeProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "2_columns_ii");
		typeSettingsUnicodeProperties.setProperty("column-1", column1);
		typeSettingsUnicodeProperties.setProperty("column-2", column2);

		layoutModel.setTypeSettings(
			StringUtil.replace(
				typeSettingsUnicodeProperties.toString(), '\n', "\\n"));

		layoutModel.setLastPublishDate(new Date());

		return layoutModel;
	}

	public LayoutPageTemplateStructureModel newLayoutPageTemplateStructureModel(
		LayoutModel layoutModel) {

		LayoutPageTemplateStructureModel layoutPageTemplateStructureModel =
			new LayoutPageTemplateStructureModelImpl();

		layoutPageTemplateStructureModel.setUuid(SequentialUUID.generate());

		layoutPageTemplateStructureModel.setLayoutPageTemplateStructureId(
			counter.get());

		layoutPageTemplateStructureModel.setGroupId(layoutModel.getGroupId());
		layoutPageTemplateStructureModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureModel.setCreateDate(new Date());
		layoutPageTemplateStructureModel.setModifiedDate(new Date());
		layoutPageTemplateStructureModel.setClassNameId(
			getClassNameId(Layout.class));
		layoutPageTemplateStructureModel.setClassPK(layoutModel.getPlid());

		return layoutPageTemplateStructureModel;
	}

	public LayoutPageTemplateStructureRelModel
		newLayoutPageTemplateStructureRelModel(
			LayoutModel layoutModel,
			LayoutPageTemplateStructureModel layoutPageTemplateStructureModel,
			FragmentEntryLinkModel fragmentEntryLinkModel) {

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			counter.get());
		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());
		layoutPageTemplateStructureRelModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureRelModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureRelModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());
		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(0L);

		LayoutData layoutData = LayoutData.of(
			layoutModel.toEscapedModel(),
			layoutRow -> layoutRow.addLayoutColumns(
				layoutColumn -> {
					List<Long> fragmentEntryLinkIds =
						layoutColumn.getFragmentEntryLinkIds();

					fragmentEntryLinkIds.add(
						fragmentEntryLinkModel.getFragmentEntryLinkId());
				}));

		JSONObject jsonObject = layoutData.getLayoutDataJSONObject();

		layoutPageTemplateStructureRelModel.setData(jsonObject.toString());

		return layoutPageTemplateStructureRelModel;
	}

	public List<LayoutSetModel> newLayoutSetModels(long groupId) {
		List<LayoutSetModel> layoutSetModels = new ArrayList<>(2);

		layoutSetModels.add(newLayoutSetModel(groupId, true));
		layoutSetModels.add(newLayoutSetModel(groupId, false));

		return layoutSetModels;
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		BlogsEntryModel blogsEntryModel) {

		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(),
			getClassNameId(getMBDiscussionCombinedClassName(BlogsEntry.class)),
			blogsEntryModel.getEntryId(), "", 0, true, false, "",
			String.valueOf(blogsEntryModel.getGroupId()));
	}

	public AssetEntryModel newMBDiscussionAssetEntryModel(
		WikiPageModel wikiPageModel) {

		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(),
			getClassNameId(getMBDiscussionCombinedClassName(WikiPage.class)),
			wikiPageModel.getResourcePrimKey(), "", 0, true, false, "",
			String.valueOf(wikiPageModel.getGroupId()));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		if (currentIndex == 1) {
			return newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		String assetPublisherQueryName = "assetCategories";

		if ((currentIndex % 2) == 0) {
			assetPublisherQueryName = "assetTags";
		}

		ObjectValuePair<String[], Integer> objectValuePair = null;

		Integer startIndex = _assetPublisherQueryStartIndexes.get(groupId);

		if (startIndex == null) {
			startIndex = 0;
		}

		if (assetPublisherQueryName.equals("assetCategories")) {
			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				assetCategoryModelsMaps[(int)groupId - 1];

			List<AssetCategoryModel> assetCategoryModels =
				assetCategoryModelsMap.get(getNextAssetClassNameId(groupId));

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetCategoriesQueryValues(
				assetCategoryModels, startIndex);
		}
		else {
			Map<Long, List<AssetTagModel>> assetTagModelsMap =
				assetTagModelsMaps[(int)groupId - 1];

			List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
				getNextAssetClassNameId(groupId));

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetTagsQueryValues(
				assetTagModels, startIndex);
		}

		String[] assetPublisherQueryValues = objectValuePair.getKey();

		_assetPublisherQueryStartIndexes.put(
			groupId, objectValuePair.getValue());

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)
				_defaultAssetPublisherPortletPreferencesImpl.clone();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue("queryName0", assetPublisherQueryName);
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue("queryName1", assetPublisherQueryName);
		jxPortletPreferences.setValue(
			"queryValues1", assetPublisherQueryValues[3]);

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue("editable", "true");
		jxPortletPreferences.setValue(
			"recordSetId", String.valueOf(ddlRecordSetModel.getRecordSetId()));
		jxPortletPreferences.setValue("spreadsheet", "false");

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return newPortletPreferencesModel(
			plid, portletId,
			portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public List<LayoutModel> newPublicLayoutModels(long groupId) {
		List<LayoutModel> layoutModels = new ArrayList<>();

		layoutModels.add(
			newLayoutModel(
				groupId, "welcome", LoginPortletKeys.LOGIN + ",",
				HelloWorldPortletKeys.HELLO_WORLD + ","));
		layoutModels.add(
			newLayoutModel(groupId, "blogs", "", BlogsPortletKeys.BLOGS + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "commerce_product", "",
				CPPortletKeys.CP_CONTENT_WEB + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "document_library", "",
				DLPortletKeys.DOCUMENT_LIBRARY + ","));
		layoutModels.add(
			newLayoutModel(
				groupId, "forums", "", MBPortletKeys.MESSAGE_BOARDS + ","));
		layoutModels.add(
			newLayoutModel(groupId, "wiki", "", WikiPortletKeys.WIKI + ","));

		return layoutModels;
	}

	public List<ReleaseModel> newReleaseModels() throws IOException {
		List<ReleaseModel> releases = new ArrayList<>();

		Version latestSchemaVersion =
			PortalUpgradeProcess.getLatestSchemaVersion();

		releases.add(
			newReleaseModel(
				ReleaseConstants.DEFAULT_ID,
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
				latestSchemaVersion.toString(), ReleaseInfo.getBuildNumber(),
				false, ReleaseConstants.TEST_STRING));

		try (InputStream is = DataFactory.class.getResourceAsStream(
				"dependencies/releases.txt");
			Reader reader = new InputStreamReader(is);
			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String[] parts = StringUtil.split(line, CharPool.COLON);

				if (parts.length > 0) {
					String servletContextName = parts[0];
					String schemaVersion = parts[1];

					releases.add(
						newReleaseModel(
							counter.get(), servletContextName, schemaVersion, 0,
							true, null));
				}
			}
		}

		return releases;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		return newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		if (assetVocabularyModel.getUserId() == DEFAULT_USER_ID) {
			return Collections.singletonList(
				newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					OWNER_ROLE_ID, DEFAULT_USER_ID));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				OWNER_ROLE_ID, DEFAULT_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMStructure.class.getName(),
			getClassName(ddmStructureModel.getClassNameId()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, OWNER_ROLE_ID, ddmStructureModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, USER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMTemplate.class.getName(),
			getClassName(ddmTemplateModel.getResourceClassNameId()));
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, GUEST_ROLE_ID, 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, OWNER_ROLE_ID, ddmTemplateModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(name, primKey, USER_ROLE_ID, 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				OWNER_ROLE_ID, SAMPLE_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			SAMPLE_USER_ID);
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
			String.valueOf(mbCategoryModel.getCategoryId()), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		return newResourcePermissionModels(
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

		return newResourcePermissionModels(name, primKey, 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				OWNER_ROLE_ID, SAMPLE_USER_ID));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		return newResourcePermissionModels(
			name, String.valueOf(primKey), SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				OWNER_ROLE_ID, userModel.getUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		return newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			SAMPLE_USER_ID);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		return newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()), SAMPLE_USER_ID);
	}

	public UserModel newSampleUserModel() {
		return newUserModel(
			SAMPLE_USER_ID, SAMPLE_USER_NAME, SAMPLE_USER_NAME,
			SAMPLE_USER_NAME, false);
	}

	public SocialActivityModel newSocialActivityModel(
		BlogsEntryModel blogsEntryModel) {

		return newSocialActivityModel(
			blogsEntryModel.getGroupId(), getClassNameId(BlogsEntry.class),
			blogsEntryModel.getEntryId(), BlogsActivityKeys.ADD_ENTRY,
			"{\"title\":\"" + blogsEntryModel.getTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		DLFileEntryModel dlFileEntryModel) {

		return newSocialActivityModel(
			dlFileEntryModel.getGroupId(), getClassNameId(DLFileEntry.class),
			dlFileEntryModel.getFileEntryId(), DLActivityKeys.ADD_FILE_ENTRY,
			StringPool.BLANK);
	}

	public SocialActivityModel newSocialActivityModel(
		JournalArticleModel journalArticleModel) {

		int type = JournalActivityKeys.UPDATE_ARTICLE;

		if (journalArticleModel.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT) {

			type = JournalActivityKeys.ADD_ARTICLE;
		}

		return newSocialActivityModel(
			journalArticleModel.getGroupId(),
			getClassNameId(JournalArticle.class),
			journalArticleModel.getResourcePrimKey(), type,
			"{\"title\":\"" + journalArticleModel.getUrlTitle() + "\"}");
	}

	public SocialActivityModel newSocialActivityModel(
		MBMessageModel mbMessageModel) {

		long classNameId = mbMessageModel.getClassNameId();
		long classPK = mbMessageModel.getClassPK();

		int type = 0;
		String extraData = null;

		if (classNameId == getClassNameId(WikiPage.class)) {
			extraData = "{\"version\":1}";

			type = WikiActivityKeys.ADD_PAGE;
		}
		else if (classNameId == 0) {
			extraData = "{\"title\":\"" + mbMessageModel.getSubject() + "\"}";

			type = MBActivityKeys.ADD_MESSAGE;

			classNameId = getClassNameId(MBMessage.class);
			classPK = mbMessageModel.getMessageId();
		}
		else {
			StringBundler sb = new StringBundler(5);

			sb.append("{\"messageId\": \"");
			sb.append(mbMessageModel.getMessageId());
			sb.append("\", \"title\": ");
			sb.append(mbMessageModel.getSubject());
			sb.append("}");

			extraData = sb.toString();

			type = SocialActivityConstants.TYPE_ADD_COMMENT;
		}

		return newSocialActivityModel(
			mbMessageModel.getGroupId(), classNameId, classPK, type, extraData);
	}

	public SubscriptionModel newSubscriptionModel(
		BlogsEntryModel blogsEntryModel) {

		return newSubscriptionModel(
			getClassNameId(BlogsEntry.class), blogsEntryModel.getEntryId());
	}

	public SubscriptionModel newSubscriptionModel(MBThreadModel mBThreadModel) {
		return newSubscriptionModel(
			getClassNameId(MBThread.class), mBThreadModel.getThreadId());
	}

	public SubscriptionModel newSubscriptionModel(WikiPageModel wikiPageModel) {
		return newSubscriptionModel(
			getClassNameId(WikiPage.class), wikiPageModel.getResourcePrimKey());
	}

	public List<UserModel> newUserModels() {
		List<UserModel> userModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_USER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_USER_COUNT; i++) {
			String[] userName = nextUserName(i);

			userModels.add(
				newUserModel(
					counter.get(), userName[0], userName[1],
					"test" + _userScreenNameCounter.get(), false));
		}

		return userModels;
	}

	public GroupModel newUserPersonalSiteGroupModel() {
		return newGroupModel(
			_userPersonalSiteGroupId, getClassNameId(UserPersonalSite.class),
			DEFAULT_USER_ID, GroupConstants.USER_PERSONAL_SITE, false);
	}

	public ViewCountEntryModel newViewCountEntryModel(
		AssetEntryModel assetEntryModel) {

		return newViewCountEntryModel(
			assetEntryModel.getCompanyId(), getClassNameId(AssetEntry.class),
			assetEntryModel.getPrimaryKey(), 0);
	}

	public VirtualHostModel newVirtualHostModel() {
		VirtualHostModel virtualHostModel = new VirtualHostModelImpl();

		virtualHostModel.setVirtualHostId(counter.get());
		virtualHostModel.setCompanyId(COMPANY_ID);
		virtualHostModel.setHostname(BenchmarksPropsValues.VIRTUAL_HOST_NAME);

		return virtualHostModel;
	}

	public List<WikiNodeModel> newWikiNodeModels(long groupId) {
		List<WikiNodeModel> wikiNodeModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_WIKI_NODE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_WIKI_NODE_COUNT; i++) {
			wikiNodeModels.add(newWikiNodeModel(groupId, i));
		}

		return wikiNodeModels;
	}

	public List<WikiPageModel> newWikiPageModels(WikiNodeModel wikiNodeModel) {
		List<WikiPageModel> wikiPageModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_WIKI_PAGE_COUNT);

		for (int i = 1; i <= BenchmarksPropsValues.MAX_WIKI_PAGE_COUNT; i++) {
			wikiPageModels.add(newWikiPageModel(wikiNodeModel, i));
		}

		return wikiPageModels;
	}

	public WikiPageResourceModel newWikiPageResourceModel(
		WikiPageModel wikiPageModel) {

		WikiPageResourceModel wikiPageResourceModel =
			new WikiPageResourceModelImpl();

		wikiPageResourceModel.setUuid(SequentialUUID.generate());
		wikiPageResourceModel.setResourcePrimKey(
			wikiPageModel.getResourcePrimKey());
		wikiPageResourceModel.setNodeId(wikiPageModel.getNodeId());
		wikiPageResourceModel.setTitle(wikiPageModel.getTitle());

		return wikiPageResourceModel;
	}

	public String[] nextUserName(long index) {
		String[] userName = new String[2];

		userName[0] = _firstNames.get(
			(int)(index / _lastNames.size()) % _firstNames.size());
		userName[1] = _lastNames.get((int)(index % _lastNames.size()));

		return userName;
	}

	public String toInsertSQL(BaseModel<?> baseModel) {
		try {
			StringBundler sb = new StringBundler();

			toInsertSQL(sb, baseModel);

			Class<?> clazz = baseModel.getClass();

			for (Class<?> modelClass : clazz.getInterfaces()) {
				try {
					Method method = DataFactory.class.getMethod(
						"newResourcePermissionModels", modelClass);

					for (ResourcePermissionModel resourcePermissionModel :
							(List<ResourcePermissionModel>)method.invoke(
								this, baseModel)) {

						sb.append("\n");

						toInsertSQL(sb, resourcePermissionModel);
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

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetCategoriesQueryValues(
			List<AssetCategoryModel> assetCategoryModels, int index) {

		String[] categoryIds = new String[4];

		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				index +=
					BenchmarksPropsValues.
						MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT;
			}

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index % assetCategoryModels.size());

			categoryIds[i] = String.valueOf(assetCategoryModel.getCategoryId());
		}

		return new ObjectValuePair<>(
			categoryIds,
			index +
				BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_CATEGORY_COUNT);
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetTagsQueryValues(
			List<AssetTagModel> assetTagModels, int index) {

		String[] assetTagNames = new String[4];

		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				index +=
					BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT;
			}

			AssetTagModel assetTagModel = assetTagModels.get(
				index % assetTagModels.size());

			assetTagNames[i] = String.valueOf(assetTagModel.getName());
		}

		return new ObjectValuePair<>(
			assetTagNames,
			index + BenchmarksPropsValues.MAX_ASSET_ENTRY_TO_ASSET_TAG_COUNT);
	}

	protected SimpleCounter getSimpleCounter(
		Map<Long, SimpleCounter>[] simpleCountersArray, long groupId,
		long classNameId) {

		Map<Long, SimpleCounter> simpleCounters =
			simpleCountersArray[(int)groupId - 1];

		if (simpleCounters == null) {
			simpleCounters = new HashMap<>();

			simpleCountersArray[(int)groupId - 1] = simpleCounters;
		}

		SimpleCounter simpleCounter = simpleCounters.get(classNameId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter(0);

			simpleCounters.put(classNameId, simpleCounter);
		}

		return simpleCounter;
	}

	protected AssetCategoryModel newAssetCategoryModel(
		long groupId, String name, long vocabularyId) {

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		assetCategoryModel.setUuid(SequentialUUID.generate());
		assetCategoryModel.setCategoryId(counter.get());
		assetCategoryModel.setGroupId(groupId);
		assetCategoryModel.setCompanyId(COMPANY_ID);
		assetCategoryModel.setUserId(SAMPLE_USER_ID);
		assetCategoryModel.setUserName(SAMPLE_USER_NAME);
		assetCategoryModel.setCreateDate(new Date());
		assetCategoryModel.setModifiedDate(new Date());
		assetCategoryModel.setParentCategoryId(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		assetCategoryModel.setTreePath(
			"/" + assetCategoryModel.getCategoryId() + "/");
		assetCategoryModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetCategoryModel.setTitle(sb.toString());

		assetCategoryModel.setVocabularyId(vocabularyId);
		assetCategoryModel.setLastPublishDate(new Date());

		return assetCategoryModel;
	}

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		assetEntryModel.setEntryId(counter.get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(COMPANY_ID);
		assetEntryModel.setUserId(SAMPLE_USER_ID);
		assetEntryModel.setUserName(SAMPLE_USER_NAME);
		assetEntryModel.setCreateDate(createDate);
		assetEntryModel.setModifiedDate(modifiedDate);
		assetEntryModel.setClassNameId(classNameId);
		assetEntryModel.setClassPK(classPK);
		assetEntryModel.setClassUuid(uuid);
		assetEntryModel.setClassTypeId(classTypeId);
		assetEntryModel.setListable(listable);
		assetEntryModel.setVisible(visible);
		assetEntryModel.setStartDate(createDate);
		assetEntryModel.setEndDate(nextFutureDate());
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(nextFutureDate());
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
	}

	protected AssetVocabularyModel newAssetVocabularyModel(
		long grouId, long userId, String userName, String name) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		assetVocabularyModel.setUuid(SequentialUUID.generate());
		assetVocabularyModel.setVocabularyId(counter.get());
		assetVocabularyModel.setGroupId(grouId);
		assetVocabularyModel.setCompanyId(COMPANY_ID);
		assetVocabularyModel.setUserId(userId);
		assetVocabularyModel.setUserName(userName);
		assetVocabularyModel.setCreateDate(new Date());
		assetVocabularyModel.setModifiedDate(new Date());
		assetVocabularyModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetVocabularyModel.setTitle(sb.toString());

		assetVocabularyModel.setSettings(
			"multiValued=true\\nselectedClassNameIds=0");
		assetVocabularyModel.setLastPublishDate(new Date());

		return assetVocabularyModel;
	}

	protected CPDefinitionLocalizationModel newCPDefinitionLocalizationModel(
		long cpDefinitionId) {

		CPDefinitionLocalizationModel cpDefinitionLocalizationModel =
			new CPDefinitionLocalizationModelImpl();

		cpDefinitionLocalizationModel.setCpDefinitionLocalizationId(
			counter.get());
		cpDefinitionLocalizationModel.setCompanyId(COMPANY_ID);
		cpDefinitionLocalizationModel.setCPDefinitionId(cpDefinitionId);
		cpDefinitionLocalizationModel.setLanguageId("en_US");
		cpDefinitionLocalizationModel.setName(
			cpDefinitionLocalizationNames.get(cpDefinitionId));
		cpDefinitionLocalizationModel.setShortDescription(
			"Short description for definition " + cpDefinitionId);
		cpDefinitionLocalizationModel.setDescription(
			"A longer and more verbose description for definition with ID " +
				cpDefinitionId);
		cpDefinitionLocalizationModel.setMetaTitle(
			"A meta-title for definition " + cpDefinitionId);
		cpDefinitionLocalizationModel.setMetaDescription(
			"A meta-description for definition " + cpDefinitionId);
		cpDefinitionLocalizationModel.setMetaKeywords(
			"Meta-keywords for definition " + cpDefinitionId);

		return cpDefinitionLocalizationModel;
	}

	protected CPDefinitionModel newCPDefinitionModel(
		long cpDefinitionId, long cProductId, long cpTaxCategoryId,
		int version) {

		CPDefinitionModel cpDefinitionModel = new CPDefinitionModelImpl();

		cpDefinitionModel.setUuid(SequentialUUID.generate());
		cpDefinitionModel.setCPDefinitionId(cpDefinitionId);
		cpDefinitionModel.setGroupId(COMMERCE_CATALOG_GROUP_ID);
		cpDefinitionModel.setCompanyId(COMPANY_ID);
		cpDefinitionModel.setUserId(SAMPLE_USER_ID);
		cpDefinitionModel.setUserName(SAMPLE_USER_NAME);
		cpDefinitionModel.setCreateDate(new Date());
		cpDefinitionModel.setModifiedDate(new Date());
		cpDefinitionModel.setCProductId(cProductId);
		cpDefinitionModel.setCPTaxCategoryId(cpTaxCategoryId);
		cpDefinitionModel.setProductTypeName("simple");
		cpDefinitionModel.setAvailableIndividually(true);
		cpDefinitionModel.setIgnoreSKUCombinations(true);
		cpDefinitionModel.setShippable(true);
		cpDefinitionModel.setFreeShipping(false);
		cpDefinitionModel.setShipSeparately(true);
		cpDefinitionModel.setShippingExtraPrice(3.0);
		cpDefinitionModel.setWidth(0);
		cpDefinitionModel.setHeight(0);
		cpDefinitionModel.setDepth(0);
		cpDefinitionModel.setWeight(0);
		cpDefinitionModel.setTaxExempt(false);
		cpDefinitionModel.setTelcoOrElectronics(false);
		cpDefinitionModel.setDDMStructureKey(null);
		cpDefinitionModel.setPublished(true);
		cpDefinitionModel.setDisplayDate(new Date());
		cpDefinitionModel.setExpirationDate(null);
		cpDefinitionModel.setLastPublishDate(null);
		cpDefinitionModel.setSubscriptionEnabled(false);
		cpDefinitionModel.setSubscriptionLength(0);
		cpDefinitionModel.setSubscriptionType(null);
		cpDefinitionModel.setSubscriptionTypeSettings(null);
		cpDefinitionModel.setMaxSubscriptionCycles(0);
		cpDefinitionModel.setVersion(version);
		cpDefinitionModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		cpDefinitionModel.setStatusByUserId(SAMPLE_USER_ID);
		cpDefinitionModel.setStatusByUserName(SAMPLE_USER_NAME);
		cpDefinitionModel.setStatusDate(new Date());

		return cpDefinitionModel;
	}

	protected CPFriendlyURLEntryModel newCPFriendlyURLEntryModel(
		long cProductId, long publishedCPDefinitionId) {

		return newCPFriendlyURLEntryModel(
			0, getClassNameId(CProduct.class), cProductId,
			FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
				"Definition " + publishedCPDefinitionId));
	}

	protected CPFriendlyURLEntryModel newCPFriendlyURLEntryModel(
		long groupId, long classNameId, long classPK, String urlTitle) {

		CPFriendlyURLEntryModel cpFriendlyURLEntryModel =
			new CPFriendlyURLEntryModelImpl();

		cpFriendlyURLEntryModel.setUuid(SequentialUUID.generate());
		cpFriendlyURLEntryModel.setCPFriendlyURLEntryId(counter.get());
		cpFriendlyURLEntryModel.setGroupId(groupId);
		cpFriendlyURLEntryModel.setCompanyId(COMPANY_ID);
		cpFriendlyURLEntryModel.setUserId(SAMPLE_USER_ID);
		cpFriendlyURLEntryModel.setUserName(SAMPLE_USER_NAME);
		cpFriendlyURLEntryModel.setCreateDate(new Date());
		cpFriendlyURLEntryModel.setModifiedDate(new Date());
		cpFriendlyURLEntryModel.setClassNameId(classNameId);
		cpFriendlyURLEntryModel.setClassPK(classPK);
		cpFriendlyURLEntryModel.setLanguageId("en_US");
		cpFriendlyURLEntryModel.setUrlTitle(urlTitle);
		cpFriendlyURLEntryModel.setMain(true);

		return cpFriendlyURLEntryModel;
	}

	protected CPInstanceModel newCPInstanceModel(
		long cpDefinitionId, int index) {

		CPInstanceModel cpInstanceModel = new CPInstanceModelImpl();

		cpInstanceModel.setUuid(SequentialUUID.generate());
		cpInstanceModel.setCPInstanceId(counter.get());
		cpInstanceModel.setGroupId(COMMERCE_CATALOG_GROUP_ID);
		cpInstanceModel.setCompanyId(COMPANY_ID);
		cpInstanceModel.setUserId(SAMPLE_USER_ID);
		cpInstanceModel.setUserName(SAMPLE_USER_NAME);
		cpInstanceModel.setCreateDate(new Date());
		cpInstanceModel.setModifiedDate(new Date());
		cpInstanceModel.setCPDefinitionId(cpDefinitionId);
		cpInstanceModel.setCPInstanceUuid(SequentialUUID.generate());

		String instanceKey = cpDefinitionId + StringPool.POUND + index;

		cpInstanceModel.setSku("SKU" + instanceKey);
		cpInstanceModel.setGtin("GTIN" + instanceKey);
		cpInstanceModel.setManufacturerPartNumber("MPN" + instanceKey);

		cpInstanceModel.setPurchasable(true);
		cpInstanceModel.setJson("[]");
		cpInstanceModel.setWidth((index * 2) + 1);
		cpInstanceModel.setHeight(index + 5);
		cpInstanceModel.setDepth(index);
		cpInstanceModel.setWeight((index * 3) + 1);
		cpInstanceModel.setPrice(BigDecimal.valueOf(index + 10.1));
		cpInstanceModel.setPromoPrice(BigDecimal.valueOf(index + 9.2));
		cpInstanceModel.setCost(BigDecimal.valueOf(index + 6.4));
		cpInstanceModel.setPublished(true);
		cpInstanceModel.setDisplayDate(new Date());
		cpInstanceModel.setExpirationDate(null);
		cpInstanceModel.setLastPublishDate(null);
		cpInstanceModel.setOverrideSubscriptionInfo(false);
		cpInstanceModel.setSubscriptionEnabled(false);
		cpInstanceModel.setSubscriptionLength(0);
		cpInstanceModel.setSubscriptionType(null);
		cpInstanceModel.setSubscriptionTypeSettings(null);
		cpInstanceModel.setMaxSubscriptionCycles(0);
		cpInstanceModel.setStatus(WorkflowConstants.STATUS_APPROVED);
		cpInstanceModel.setStatusByUserId(SAMPLE_USER_ID);
		cpInstanceModel.setStatusByUserName(SAMPLE_USER_NAME);
		cpInstanceModel.setStatusDate(new Date());

		return cpInstanceModel;
	}

	protected CProductModel newCProductModel(
		long cProductId, long publishedCPDefinitionId) {

		CProductModel cProductModel = new CProductModelImpl();

		cProductModel.setUuid(SequentialUUID.generate());
		cProductModel.setCProductId(cProductId);
		cProductModel.setGroupId(COMMERCE_CATALOG_GROUP_ID);
		cProductModel.setCompanyId(COMPANY_ID);
		cProductModel.setUserId(SAMPLE_USER_ID);
		cProductModel.setUserName(SAMPLE_USER_NAME);
		cProductModel.setCreateDate(new Date());
		cProductModel.setModifiedDate(new Date());
		cProductModel.setPublishedCPDefinitionId(publishedCPDefinitionId);
		cProductModel.setLatestVersion(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT);

		return cProductModel;
	}

	protected GroupModel newGroupModel(
		long groupId, long classNameId, long classPK, String name,
		boolean site) {

		GroupModel groupModel = new GroupModelImpl();

		groupModel.setUuid(SequentialUUID.generate());
		groupModel.setGroupId(groupId);
		groupModel.setCompanyId(COMPANY_ID);
		groupModel.setCreatorUserId(SAMPLE_USER_ID);
		groupModel.setClassNameId(classNameId);
		groupModel.setClassPK(classPK);
		groupModel.setTreePath(
			StringPool.SLASH + groupModel.getGroupId() + StringPool.SLASH);
		groupModel.setGroupKey(name);
		groupModel.setName(name);
		groupModel.setManualMembership(true);
		groupModel.setMembershipRestriction(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION);
		groupModel.setFriendlyURL(
			StringPool.FORWARD_SLASH +
				FriendlyURLNormalizerUtil.normalize(name));
		groupModel.setSite(site);
		groupModel.setActive(true);

		return groupModel;
	}

	protected LayoutSetModel newLayoutSetModel(
		long groupId, boolean privateLayout) {

		LayoutSetModel layoutSetModel = new LayoutSetModelImpl();

		long layoutSetId = counter.get();

		layoutSetModel.setLayoutSetId(layoutSetId);

		layoutSetModel.setGroupId(groupId);
		layoutSetModel.setCompanyId(COMPANY_ID);
		layoutSetModel.setCreateDate(new Date());
		layoutSetModel.setModifiedDate(new Date());
		layoutSetModel.setPrivateLayout(privateLayout);
		layoutSetModel.setThemeId("classic_WAR_classictheme");
		layoutSetModel.setColorSchemeId("01");

		return layoutSetModel;
	}

	protected PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId, String preferences) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		portletPreferencesModel.setCompanyId(COMPANY_ID);
		portletPreferencesModel.setPortletPreferencesId(counter.get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	protected ReleaseModelImpl newReleaseModel(
			long releaseId, String servletContextName, String schemaVersion,
			int buildNumber, boolean verified, String testString)
		throws IOException {

		ReleaseModelImpl releaseModelImpl = new ReleaseModelImpl();

		releaseModelImpl.setReleaseId(releaseId);
		releaseModelImpl.setCreateDate(new Date());
		releaseModelImpl.setModifiedDate(new Date());
		releaseModelImpl.setServletContextName(servletContextName);
		releaseModelImpl.setSchemaVersion(schemaVersion);
		releaseModelImpl.setBuildNumber(buildNumber);
		releaseModelImpl.setBuildDate(new Date());
		releaseModelImpl.setVerified(verified);
		releaseModelImpl.setTestString(testString);

		return releaseModelImpl;
	}

	protected ResourcePermissionModel newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		resourcePermissionModel.setResourcePermissionId(
			resourcePermissionCounter.get());
		resourcePermissionModel.setCompanyId(COMPANY_ID);
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

	protected RoleModel newRoleModel(String name, int type, long roleId) {
		RoleModel roleModel = new RoleModelImpl();

		roleModel.setUuid(SequentialUUID.generate());
		roleModel.setRoleId(roleId);
		roleModel.setCompanyId(COMPANY_ID);
		roleModel.setUserId(SAMPLE_USER_ID);
		roleModel.setUserName(SAMPLE_USER_NAME);
		roleModel.setCreateDate(new Date());
		roleModel.setModifiedDate(new Date());
		roleModel.setClassNameId(getClassNameId(Role.class));
		roleModel.setClassPK(roleModel.getRoleId());
		roleModel.setName(name);
		roleModel.setType(type);

		return roleModel;
	}

	protected SocialActivityModel newSocialActivityModel(
		long groupId, long classNameId, long classPK, int type,
		String extraData) {

		SocialActivityModel socialActivityModel = new SocialActivityModelImpl();

		socialActivityModel.setActivityId(socialActivityCounter.get());
		socialActivityModel.setGroupId(groupId);
		socialActivityModel.setCompanyId(COMPANY_ID);
		socialActivityModel.setUserId(SAMPLE_USER_ID);
		socialActivityModel.setCreateDate(_CURRENT_TIME + _timeCounter.get());
		socialActivityModel.setClassNameId(classNameId);
		socialActivityModel.setClassPK(classPK);
		socialActivityModel.setType(type);
		socialActivityModel.setExtraData(extraData);

		return socialActivityModel;
	}

	protected SubscriptionModel newSubscriptionModel(
		long classNameId, long classPK) {

		SubscriptionModel subscriptionModel = new SubscriptionModelImpl();

		subscriptionModel.setSubscriptionId(counter.get());
		subscriptionModel.setCompanyId(COMPANY_ID);
		subscriptionModel.setUserId(SAMPLE_USER_ID);
		subscriptionModel.setUserName(SAMPLE_USER_NAME);
		subscriptionModel.setCreateDate(new Date());
		subscriptionModel.setModifiedDate(new Date());
		subscriptionModel.setClassNameId(classNameId);
		subscriptionModel.setClassPK(classPK);
		subscriptionModel.setFrequency(SubscriptionConstants.FREQUENCY_INSTANT);

		return subscriptionModel;
	}

	protected UserModel newUserModel(
		long userId, String firstName, String lastName, String screenName,
		boolean defaultUser) {

		if (Validator.isNull(screenName)) {
			screenName = String.valueOf(userId);
		}

		UserModel userModel = new UserModelImpl();

		userModel.setUuid(SequentialUUID.generate());
		userModel.setUserId(userId);
		userModel.setCompanyId(COMPANY_ID);
		userModel.setCreateDate(new Date());
		userModel.setModifiedDate(new Date());
		userModel.setDefaultUser(defaultUser);
		userModel.setContactId(counter.get());
		userModel.setPassword("test");
		userModel.setPasswordModifiedDate(new Date());
		userModel.setReminderQueryQuestion("What is your screen name?");
		userModel.setReminderQueryAnswer(screenName);
		userModel.setEmailAddress(screenName + "@liferay.com");
		userModel.setScreenName(screenName);
		userModel.setLanguageId("en_US");
		userModel.setGreeting("Welcome " + screenName + StringPool.EXCLAMATION);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		userModel.setLoginDate(new Date());
		userModel.setLastLoginDate(new Date());
		userModel.setLastFailedLoginDate(new Date());
		userModel.setLockoutDate(new Date());
		userModel.setAgreedToTermsOfUse(true);
		userModel.setEmailAddressVerified(true);

		return userModel;
	}

	protected ViewCountEntryModel newViewCountEntryModel(
		long companyId, long classNameId, long classPK, long viewCount) {

		ViewCountEntryModel viewCountEntryModel = new ViewCountEntryModelImpl();

		viewCountEntryModel.setPrimaryKey(new ViewCountEntryPK());
		viewCountEntryModel.setCompanyId(companyId);
		viewCountEntryModel.setClassNameId(classNameId);
		viewCountEntryModel.setClassPK(classPK);
		viewCountEntryModel.setViewCount(viewCount);

		return viewCountEntryModel;
	}

	protected WikiNodeModel newWikiNodeModel(long groupId, int index) {
		WikiNodeModel wikiNodeModel = new WikiNodeModelImpl();

		wikiNodeModel.setUuid(SequentialUUID.generate());
		wikiNodeModel.setNodeId(counter.get());
		wikiNodeModel.setGroupId(groupId);
		wikiNodeModel.setCompanyId(COMPANY_ID);
		wikiNodeModel.setUserId(SAMPLE_USER_ID);
		wikiNodeModel.setUserName(SAMPLE_USER_NAME);
		wikiNodeModel.setCreateDate(new Date());
		wikiNodeModel.setModifiedDate(new Date());
		wikiNodeModel.setName("Test Node " + index);
		wikiNodeModel.setLastPostDate(new Date());
		wikiNodeModel.setLastPublishDate(new Date());
		wikiNodeModel.setStatusDate(new Date());

		return wikiNodeModel;
	}

	protected WikiPageModel newWikiPageModel(
		WikiNodeModel wikiNodeModel, int index) {

		WikiPageModel wikiPageModel = new WikiPageModelImpl();

		wikiPageModel.setUuid(SequentialUUID.generate());
		wikiPageModel.setPageId(counter.get());
		wikiPageModel.setResourcePrimKey(counter.get());
		wikiPageModel.setGroupId(wikiNodeModel.getGroupId());
		wikiPageModel.setCompanyId(COMPANY_ID);
		wikiPageModel.setUserId(SAMPLE_USER_ID);
		wikiPageModel.setUserName(SAMPLE_USER_NAME);
		wikiPageModel.setCreateDate(new Date());
		wikiPageModel.setModifiedDate(new Date());
		wikiPageModel.setNodeId(wikiNodeModel.getNodeId());
		wikiPageModel.setTitle("Test Page " + index);
		wikiPageModel.setVersion(WikiPageConstants.VERSION_DEFAULT);
		wikiPageModel.setContent(
			StringBundler.concat(
				"This is Test Page ", index, " of ", wikiNodeModel.getName(),
				"."));
		wikiPageModel.setFormat("creole");
		wikiPageModel.setHead(true);
		wikiPageModel.setLastPublishDate(new Date());

		return wikiPageModel;
	}

	protected void toInsertSQL(StringBundler sb, BaseModel<?> baseModel) {
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

	private static final String _COMMERCE_CATALOG_NAME = "Master";

	private static final String _COMMERCE_CHANNEL_NAME =
		SAMPLE_USER_NAME + " Channel";

	private static final String _COMMERCE_CURRENCY_CODE = "USD";

	private static final long _CURRENT_TIME = System.currentTimeMillis();

	private final long _accountId;
	private RoleModel _administratorRoleModel;
	private Map<Long, SimpleCounter>[] _assetCategoryCounters;
	private List<AssetCategoryModel>[] _assetCategoryModelsArray;
	private final Map<Long, Integer> _assetPublisherQueryStartIndexes =
		new HashMap<>();
	private Map<Long, SimpleCounter>[] _assetTagCounters;
	private List<AssetTagModel>[] _assetTagModelsArray;
	private List<AssetVocabularyModel>[] _assetVocabularyModelsArray;
	private final long _cpTaxCategoryId;
	private final PortletPreferencesImpl
		_defaultAssetPublisherPortletPreferencesImpl;
	private AssetVocabularyModel _defaultAssetVocabularyModel;
	private List<String> _firstNames;
	private RoleModel _guestRoleModel;
	private List<String> _lastNames;
	private final Map<Long, SimpleCounter> _layoutCounters = new HashMap<>();
	private RoleModel _ownerRoleModel;
	private RoleModel _powerUserRoleModel;
	private List<RoleModel> _roleModels;
	private final Format _simpleDateFormat;
	private RoleModel _siteMemberRoleModel;
	private final SimpleCounter _timeCounter;
	private final long _userPersonalSiteGroupId;
	private RoleModel _userRoleModel;
	private final SimpleCounter _userScreenNameCounter;

}