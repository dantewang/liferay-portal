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

import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.commerce.currency.model.CommerceCurrencyModel;
import com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl;
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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class CommerceDataFactory extends BaseDataFactory {

	public CommerceDataFactory(
			AssetDataFactory assetDataFactory, UserDataFactory userDataFactory)
		throws Exception {

		_assetDataFactory = assetDataFactory;
		_userDataFactory = userDataFactory;

		_commerceCatalogGroupId = counter.get();
		_commerceChannelGroupId = counter.get();

		_initCommerceCurrencyModel();
		_initCommerceCatalogModel();
		_initCommerceChannelModel();
		_initCommerceProductModels();
		_initCommerceGroupModle();
	}

	public List<AssetEntryModel> getAssetEntryModels() {
		return new ArrayList<>(_assetEntryModels);
	}

	public GroupModel getCommerceCatalogGroupModel() {
		return _commerceCatalogGroupModel;
	}

	public CommerceCatalogModel getCommerceCatalogModel() {
		return _commerceCatalogModel;
	}

	public GroupModel getCommerceChannelGroupModel() {
		return _commerceChannelGroupModel;
	}

	public CommerceChannelModel getCommerceChannelModel() {
		return _commerceChannelModel;
	}

	public CommerceCurrencyModel getCommerceCurrencyModel() {
		return _commerceCurrencyModel;
	}

	public List<CPDefinitionLocalizationModel>
		getCPDefinitionLocalizationModels() {

		return new ArrayList<>(_cpDefinitionLocalizationModels);
	}

	public List<CPDefinitionModel> getCPDefinitionModels() {
		return new ArrayList<>(_cpDefinitionModels);
	}

	public List<CPFriendlyURLEntryModel> getCPFriendlyURLEntryModels() {
		return new ArrayList<>(_cpFriendlyURLEntryModels);
	}

	public List<CPInstanceModel> getCPInstanceModels() {
		return new ArrayList<>(_cpInstanceModels);
	}

	public List<CProductModel> getCProductModels() {
		return new ArrayList<>(_cProductModels);
	}

	public List<CPTaxCategoryModel> getCPTaxCategoryModels() {
		return new ArrayList<>(_cpTaxCategoryModels);
	}

	protected CPFriendlyURLEntryModel newCPFriendlyURLEntryModel(
		CProductModel cProductModel) {

		return _newCPFriendlyURLEntryModel(
			0, getClassNameId(CProduct.class), cProductModel.getCProductId(),
			FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
				"Definition " + cProductModel.getPublishedCPDefinitionId()));
	}

	private void _initCommerceCatalogModel() {
		_commerceCatalogModel = new CommerceCatalogModelImpl();

		_commerceCatalogModel.setCommerceCatalogId(counter.get());
		_commerceCatalogModel.setCompanyId(COMPANY_ID);
		_commerceCatalogModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		_commerceCatalogModel.setCreateDate(new Date());
		_commerceCatalogModel.setModifiedDate(new Date());
		_commerceCatalogModel.setName("Master");
		_commerceCatalogModel.setCommerceCurrencyCode(
			_commerceCurrencyModel.getCode());
		_commerceCatalogModel.setCatalogDefaultLanguageId("en_US");
		_commerceCatalogModel.setSystem(true);
	}

	private void _initCommerceChannelModel() {
		_commerceChannelModel = new CommerceChannelModelImpl();

		_commerceChannelModel.setCommerceChannelId(counter.get());
		_commerceChannelModel.setCompanyId(COMPANY_ID);
		_commerceChannelModel.setUserId(SAMPLE_USER_ID);
		_commerceChannelModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		_commerceChannelModel.setCreateDate(new Date());
		_commerceChannelModel.setModifiedDate(new Date());
		_commerceChannelModel.setSiteGroupId(1);
		_commerceChannelModel.setName(SAMPLE_USER_ID + " Channel");
		_commerceChannelModel.setType("site");
		_commerceChannelModel.setTypeSettings(
			String.valueOf(_userDataFactory.getGuestGroupId()));
		_commerceChannelModel.setCommerceCurrencyCode(
			_commerceCurrencyModel.getCode());
	}

	private void _initCommerceCurrencyModel() {
		_commerceCurrencyModel = new CommerceCurrencyModelImpl();

		_commerceCurrencyModel.setUuid(SequentialUUID.generate());
		_commerceCurrencyModel.setCommerceCurrencyId(counter.get());
		_commerceCurrencyModel.setCompanyId(COMPANY_ID);
		_commerceCurrencyModel.setUserId(SAMPLE_USER_ID);
		_commerceCurrencyModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		_commerceCurrencyModel.setCreateDate(new Date());
		_commerceCurrencyModel.setModifiedDate(new Date());
		_commerceCurrencyModel.setCode("USD");

		String name = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-locales",
			"=\"en_US\" default-locale=\"en_US\"><Name language-id=\"en_US\">",
			"US Dollar</Name></root>");

		_commerceCurrencyModel.setName(name);

		_commerceCurrencyModel.setRate(BigDecimal.valueOf(1));

		String formatPattern = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-locales",
			"=\"en_US\" default-locale=\"en_US\"><FormatPattern language-id",
			"=\"en_US\">$###,##0.00</FormatPattern></root>");

		_commerceCurrencyModel.setFormatPattern(formatPattern);

		_commerceCurrencyModel.setMaxFractionDigits(2);
		_commerceCurrencyModel.setMinFractionDigits(2);
		_commerceCurrencyModel.setRoundingMode("HALF_EVEN");
		_commerceCurrencyModel.setPrimary(true);
		_commerceCurrencyModel.setPriority(1);
		_commerceCurrencyModel.setActive(true);
		_commerceCurrencyModel.setLastPublishDate(new Date());
	}

	private void _initCommerceGroupModle() throws Exception {
		_commerceChannelGroupModel = _userDataFactory.newGroupModel(
			_commerceChannelGroupId, getClassNameId(CommerceChannel.class),
			_commerceChannelModel.getCommerceChannelId(),
			_commerceChannelModel.getName(), false);

		_commerceCatalogGroupModel = _userDataFactory.newGroupModel(
			_commerceCatalogGroupId, getClassNameId(CommerceCatalog.class),
			_commerceCatalogModel.getCommerceCatalogId(),
			_commerceCatalogModel.getName(), false);
	}

	private void _initCommerceProductModels() {
		CPTaxCategoryModel cpTaxCategoryModel = _newCPTaxCategoryModel(
			"Normal Product");

		int maxCProductCount = PropsValues.MAX_CPRODUCT_COUNT;
		int maxCPDefinitionCount = PropsValues.MAX_CP_DEFINITION_COUNT;
		int maxCPInstanceCount = PropsValues.MAX_CPINSTANCE_COUNT;

		_cpTaxCategoryModels = Collections.singletonList(cpTaxCategoryModel);

		_cProductModels = new ArrayList<>(maxCProductCount);

		int cpDefinitionCount = maxCProductCount * maxCPDefinitionCount;

		_assetEntryModels = new ArrayList<>(cpDefinitionCount);
		_cpDefinitionLocalizationModels = new ArrayList<>(cpDefinitionCount);
		_cpDefinitionModels = new ArrayList<>(cpDefinitionCount);
		_cpFriendlyURLEntryModels = new ArrayList<>(cpDefinitionCount);
		_cpInstanceModels = new ArrayList<>(
			cpDefinitionCount * maxCPDefinitionCount);

		for (int productIndex = 0; productIndex < maxCProductCount;
			 productIndex++) {

			long[] cpDefinitionIds = new long[maxCPDefinitionCount];

			for (int i = 0; i < maxCPDefinitionCount; i++) {
				cpDefinitionIds[i] = counter.get();
			}

			long cProductId = counter.get();

			CProductModel cProductModel = _newCProductModel(
				_commerceCatalogGroupId, cProductId,
				cpDefinitionIds[maxCPDefinitionCount - 1]);

			_cProductModels.add(cProductModel);

			for (int definitionIndex = 0;
				 definitionIndex < maxCPDefinitionCount; definitionIndex++) {

				long cpDefinitionId = cpDefinitionIds[definitionIndex];

				CPDefinitionLocalizationModel cpDefinitionLocalizationModel =
					_newCPDefinitionLocalizationModel(cpDefinitionId);

				_cpDefinitionLocalizationModels.add(
					cpDefinitionLocalizationModel);

				CPDefinitionModel cpDefinitionModel = _newCPDefinitionModel(
					_commerceCatalogGroupId, cpDefinitionId, cProductId,
					cpTaxCategoryModel.getCPTaxCategoryId(),
					definitionIndex + 1);

				_cpDefinitionModels.add(cpDefinitionModel);

				_assetEntryModels.add(
					_assetDataFactory.newAssetEntryModel(
						_commerceCatalogGroupId, new Date(), new Date(),
						getClassNameId(CPDefinition.class), cpDefinitionId,
						SequentialUUID.generate(), 0, true, true, "text/plain",
						cpDefinitionLocalizationModel.getName()));

				_cpFriendlyURLEntryModels.add(
					newCPFriendlyURLEntryModel(cProductModel));

				for (int instanceIndex = 0; instanceIndex < maxCPInstanceCount;
					 instanceIndex++) {

					_cpInstanceModels.add(
						_newCPInstanceModel(
							_commerceCatalogGroupId, cpDefinitionId,
							instanceIndex));
				}
			}
		}
	}

	private CPDefinitionLocalizationModel _newCPDefinitionLocalizationModel(
		long cpDefinitionId) {

		CPDefinitionLocalizationModel cpDefinitionLocalizationModel =
			new CPDefinitionLocalizationModelImpl();

		cpDefinitionLocalizationModel.setCpDefinitionLocalizationId(
			counter.get());

		cpDefinitionLocalizationModel.setCompanyId(COMPANY_ID);
		cpDefinitionLocalizationModel.setCPDefinitionId(cpDefinitionId);
		cpDefinitionLocalizationModel.setLanguageId("en_US");
		cpDefinitionLocalizationModel.setName("Definition " + cpDefinitionId);
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

	private CPDefinitionModel _newCPDefinitionModel(
		long groupId, long cpDefinitionId, long cProductId,
		long cpTaxCategoryId, int version) {

		CPDefinitionModel cpDefinitionModel = new CPDefinitionModelImpl();

		cpDefinitionModel.setUuid(SequentialUUID.generate());
		cpDefinitionModel.setCPDefinitionId(cpDefinitionId);
		cpDefinitionModel.setGroupId(groupId);
		cpDefinitionModel.setCompanyId(COMPANY_ID);
		cpDefinitionModel.setUserId(SAMPLE_USER_ID);
		cpDefinitionModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
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
		cpDefinitionModel.setStatusByUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		cpDefinitionModel.setStatusDate(new Date());

		return cpDefinitionModel;
	}

	private CPFriendlyURLEntryModel _newCPFriendlyURLEntryModel(
		long groupId, long classNameId, long classPK, String urlTitle) {

		CPFriendlyURLEntryModel cpFriendlyURLEntryModel =
			new CPFriendlyURLEntryModelImpl();

		cpFriendlyURLEntryModel.setUuid(SequentialUUID.generate());
		cpFriendlyURLEntryModel.setCPFriendlyURLEntryId(counter.get());
		cpFriendlyURLEntryModel.setGroupId(groupId);
		cpFriendlyURLEntryModel.setCompanyId(COMPANY_ID);
		cpFriendlyURLEntryModel.setUserId(SAMPLE_USER_ID);
		cpFriendlyURLEntryModel.setUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		cpFriendlyURLEntryModel.setCreateDate(new Date());
		cpFriendlyURLEntryModel.setModifiedDate(new Date());
		cpFriendlyURLEntryModel.setClassNameId(classNameId);
		cpFriendlyURLEntryModel.setClassPK(classPK);
		cpFriendlyURLEntryModel.setLanguageId(DataFactoryConstants.LANGUAGE_ID);
		cpFriendlyURLEntryModel.setUrlTitle(urlTitle);
		cpFriendlyURLEntryModel.setMain(true);

		return cpFriendlyURLEntryModel;
	}

	private CPInstanceModel _newCPInstanceModel(
		long groupId, long cpDefinitionId, int index) {

		CPInstanceModel cpInstanceModel = new CPInstanceModelImpl();

		cpInstanceModel.setUuid(SequentialUUID.generate());
		cpInstanceModel.setCPInstanceId(counter.get());
		cpInstanceModel.setGroupId(groupId);
		cpInstanceModel.setCompanyId(COMPANY_ID);
		cpInstanceModel.setUserId(SAMPLE_USER_ID);
		cpInstanceModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
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
		cpInstanceModel.setWidth(index * 2 + 1);
		cpInstanceModel.setHeight(index + 5);
		cpInstanceModel.setDepth(index);
		cpInstanceModel.setWeight(index * 3 + 1);
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
		cpInstanceModel.setStatusByUserName(
			DataFactoryConstants.SAMPLE_USER_NAME);
		cpInstanceModel.setStatusDate(new Date());

		return cpInstanceModel;
	}

	private CProductModel _newCProductModel(
		long groupId, long cProductId, long publishedCPDefinitionId) {

		CProductModel cProductModel = new CProductModelImpl();

		cProductModel.setUuid(SequentialUUID.generate());
		cProductModel.setCProductId(cProductId);
		cProductModel.setGroupId(groupId);
		cProductModel.setCompanyId(COMPANY_ID);
		cProductModel.setUserId(SAMPLE_USER_ID);
		cProductModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		cProductModel.setCreateDate(new Date());
		cProductModel.setModifiedDate(new Date());
		cProductModel.setPublishedCPDefinitionId(publishedCPDefinitionId);
		cProductModel.setLatestVersion(PropsValues.MAX_CP_DEFINITION_COUNT);

		return cProductModel;
	}

	private CPTaxCategoryModel _newCPTaxCategoryModel(String name) {
		CPTaxCategoryModel cpTaxCategoryModel = new CPTaxCategoryModelImpl();

		cpTaxCategoryModel.setCPTaxCategoryId(counter.get());

		cpTaxCategoryModel.setCompanyId(COMPANY_ID);
		cpTaxCategoryModel.setUserId(SAMPLE_USER_ID);
		cpTaxCategoryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		cpTaxCategoryModel.setCreateDate(new Date());
		cpTaxCategoryModel.setModifiedDate(new Date());

		name = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root available-locales",
			"=\"en_US\" default-locale=\"en_US\"><Name language-id=\"en_US\">",
			name, "</Name></root>");

		cpTaxCategoryModel.setName(name);

		cpTaxCategoryModel.setDescription(null);

		return cpTaxCategoryModel;
	}

	private final AssetDataFactory _assetDataFactory;
	private List<AssetEntryModel> _assetEntryModels;
	private final long _commerceCatalogGroupId;
	private GroupModel _commerceCatalogGroupModel;
	private CommerceCatalogModel _commerceCatalogModel;
	private final long _commerceChannelGroupId;
	private GroupModel _commerceChannelGroupModel;
	private CommerceChannelModel _commerceChannelModel;
	private CommerceCurrencyModel _commerceCurrencyModel;
	private List<CPDefinitionLocalizationModel> _cpDefinitionLocalizationModels;
	private List<CPDefinitionModel> _cpDefinitionModels;
	private List<CPFriendlyURLEntryModel> _cpFriendlyURLEntryModels;
	private List<CPInstanceModel> _cpInstanceModels;
	private List<CProductModel> _cProductModels;
	private List<CPTaxCategoryModel> _cpTaxCategoryModels;
	private final UserDataFactory _userDataFactory;

}