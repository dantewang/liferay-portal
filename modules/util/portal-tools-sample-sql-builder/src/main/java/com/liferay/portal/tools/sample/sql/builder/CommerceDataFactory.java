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

import com.liferay.commerce.currency.model.CommerceCurrencyModel;
import com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl;
import com.liferay.commerce.product.model.CPDefinitionLocalizationModel;
import com.liferay.commerce.product.model.CPDefinitionModel;
import com.liferay.commerce.product.model.CPFriendlyURLEntryModel;
import com.liferay.commerce.product.model.CPInstanceModel;
import com.liferay.commerce.product.model.CPTaxCategoryModel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CProductModel;
import com.liferay.commerce.product.model.CommerceCatalogModel;
import com.liferay.commerce.product.model.CommerceChannelModel;
import com.liferay.commerce.product.model.impl.CPDefinitionLocalizationModelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionModelImpl;
import com.liferay.commerce.product.model.impl.CPFriendlyURLEntryModelImpl;
import com.liferay.commerce.product.model.impl.CPInstanceModelImpl;
import com.liferay.commerce.product.model.impl.CPTaxCategoryModelImpl;
import com.liferay.commerce.product.model.impl.CProductModelImpl;
import com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl;
import com.liferay.commerce.product.model.impl.CommerceChannelModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class CommerceDataFactory extends BaseDataFactory {

	public CommerceDataFactory() {
		_cpTaxCategoryId = counter.get();
	}

	public CommerceCatalogModel newCommerceCatalogModel() {
		CommerceCatalogModel commerceCatalogModel =
			new CommerceCatalogModelImpl();

		commerceCatalogModel.setCommerceCatalogId(COMMERCE_CATALOG_ID);
		commerceCatalogModel.setCompanyId(COMPANY_ID);
		commerceCatalogModel.setUserName(SAMPLE_USER_NAME);
		commerceCatalogModel.setCreateDate(new Date());
		commerceCatalogModel.setModifiedDate(new Date());
		commerceCatalogModel.setName(COMMERCE_CATALOG_NAME);
		commerceCatalogModel.setCommerceCurrencyCode(_COMMERCE_CURRENCY_CODE);
		commerceCatalogModel.setCatalogDefaultLanguageId("en_US");
		commerceCatalogModel.setSystem(true);

		return commerceCatalogModel;
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
		commerceChannelModel.setName(COMMERCE_CHANNEL_NAME);
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

	private static final String _COMMERCE_CURRENCY_CODE = "USD";

	private final long _cpTaxCategoryId;

}