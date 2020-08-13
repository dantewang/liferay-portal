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
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.util.Date;

/**
 * @author Lily Chi
 */
public class CommerceDataFactory extends BaseDataFactory {

	public int getMaxCommerceProductCount() {
		return BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_COUNT;
	}

	public int getMaxCommerceProductDefinitionCount() {
		return BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT;
	}

	public int getMaxCommerceProductInstanceCount() {
		return BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_INSTANCE_COUNT;
	}

	public CommerceCatalogModel newCommerceCatalogModel(
		CommerceCurrencyModel commerceCurrencyModel) {

		CommerceCatalogModel commerceCatalogModel =
			new CommerceCatalogModelImpl();

		// PK fields

		commerceCatalogModel.setCommerceCatalogId(counter.get());

		// Audit fields

		commerceCatalogModel.setCompanyId(COMPANY_ID);
		commerceCatalogModel.setUserName(SAMPLE_USER_NAME);
		commerceCatalogModel.setCreateDate(new Date());
		commerceCatalogModel.setModifiedDate(new Date());

		// Other fields

		commerceCatalogModel.setName("Master");
		commerceCatalogModel.setCommerceCurrencyCode(
			commerceCurrencyModel.getCode());
		commerceCatalogModel.setCatalogDefaultLanguageId("en_US");
		commerceCatalogModel.setSystem(true);

		return commerceCatalogModel;
	}

	public CommerceChannelModel newCommerceChannelModel(
		CommerceCurrencyModel commerceCurrencyModel) {

		CommerceChannelModel commerceChannelModel =
			new CommerceChannelModelImpl();

		// PK fields

		commerceChannelModel.setCommerceChannelId(counter.get());

		// Audit fields

		commerceChannelModel.setCompanyId(COMPANY_ID);
		commerceChannelModel.setUserId(SAMPLE_USER_ID);
		commerceChannelModel.setUserName(SAMPLE_USER_NAME);
		commerceChannelModel.setCreateDate(new Date());
		commerceChannelModel.setModifiedDate(new Date());

		// Other fields

		commerceChannelModel.setSiteGroupId(1);
		commerceChannelModel.setName(SAMPLE_USER_ID + " Channel");
		commerceChannelModel.setType("site");
		commerceChannelModel.setTypeSettings(String.valueOf(GUEST_GROUP_ID));
		commerceChannelModel.setCommerceCurrencyCode(
			commerceCurrencyModel.getCode());

		return commerceChannelModel;
	}

	public CommerceCurrencyModel newCommerceCurrencyModel() {
		CommerceCurrencyModel commerceCurrencyModel =
			new CommerceCurrencyModelImpl();

		commerceCurrencyModel.setUuid(SequentialUUID.generate());

		// PK fields

		commerceCurrencyModel.setCommerceCurrencyId(counter.get());

		// Audit fields

		commerceCurrencyModel.setCompanyId(COMPANY_ID);
		commerceCurrencyModel.setUserId(SAMPLE_USER_ID);
		commerceCurrencyModel.setUserName(SAMPLE_USER_NAME);
		commerceCurrencyModel.setCreateDate(new Date());
		commerceCurrencyModel.setModifiedDate(new Date());

		// Other fields

		commerceCurrencyModel.setCode("USD");

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

	public CPDefinitionLocalizationModel newCPDefinitionLocalizationModel(
		CPDefinitionModel cpDefinitionModel) {

		CPDefinitionLocalizationModel cpDefinitionLocalizationModel =
			new CPDefinitionLocalizationModelImpl();

		// Localized entity

		long cpDefinitionId = cpDefinitionModel.getCPDefinitionId();

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

		// Autogenerated fields

		cpDefinitionLocalizationModel.setCompanyId(COMPANY_ID);
		cpDefinitionLocalizationModel.setCPDefinitionId(cpDefinitionId);
		cpDefinitionLocalizationModel.setCpDefinitionLocalizationId(
			counter.get());
		cpDefinitionLocalizationModel.setLanguageId("en_US");

		return cpDefinitionLocalizationModel;
	}

	public CPDefinitionModel newCPDefinitionModel(
		CPTaxCategoryModel cpTaxCategoryModel, CProductModel cProductModel,
		GroupModel commerceCatalogGroupModel, int version) {

		CPDefinitionModel cpDefinitionModel = new CPDefinitionModelImpl();

		// UUID

		cpDefinitionModel.setUuid(SequentialUUID.generate());

		// PK fields

		long cpDefinitionId = counter.get();

		cpDefinitionModel.setCPDefinitionId(cpDefinitionId);

		// Group instance

		cpDefinitionModel.setGroupId(commerceCatalogGroupModel.getGroupId());

		// Audit fields

		cpDefinitionModel.setCompanyId(COMPANY_ID);
		cpDefinitionModel.setUserId(SAMPLE_USER_ID);
		cpDefinitionModel.setUserName(SAMPLE_USER_NAME);
		cpDefinitionModel.setCreateDate(new Date());
		cpDefinitionModel.setModifiedDate(new Date());

		// Other fields

		cpDefinitionModel.setCProductId(cProductModel.getCProductId());
		cpDefinitionModel.setCPTaxCategoryId(
			cpTaxCategoryModel.getCPTaxCategoryId());
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

		if (version ==
				(BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT -
					1)) {

			cProductModel.setPublishedCPDefinitionId(cpDefinitionId);
		}

		return cpDefinitionModel;
	}

	public CPFriendlyURLEntryModel newCPFriendlyURLEntryModel(
		CProductModel cProductModel) {

		return newCPFriendlyURLEntryModel(
			0, getClassNameId(CProduct.class), cProductModel.getCProductId(),
			FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
				"Definition " + cProductModel.getPublishedCPDefinitionId()));
	}

	public CPInstanceModel newCPInstanceModel(
		CPDefinitionModel cpDefinitionModel,
		GroupModel commerceCatalogGroupModel, int index) {

		CPInstanceModel cpInstanceModel = new CPInstanceModelImpl();

		// UUID

		cpInstanceModel.setUuid(SequentialUUID.generate());

		// PK fields

		cpInstanceModel.setCPInstanceId(counter.get());

		// Group instance

		cpInstanceModel.setGroupId(commerceCatalogGroupModel.getGroupId());

		// Audit fields

		cpInstanceModel.setCompanyId(COMPANY_ID);
		cpInstanceModel.setUserId(SAMPLE_USER_ID);
		cpInstanceModel.setUserName(SAMPLE_USER_NAME);
		cpInstanceModel.setCreateDate(new Date());
		cpInstanceModel.setModifiedDate(new Date());

		// Other fields

		long cpDefinitionId = cpDefinitionModel.getCPDefinitionId();

		cpInstanceModel.setCPDefinitionId(cpDefinitionId);

		cpInstanceModel.setCPInstanceUuid(SequentialUUID.generate());

		String instanceKey = cpDefinitionId + StringPool.POUND + index;

		cpInstanceModel.setSku("SKU" + instanceKey);
		cpInstanceModel.setGtin("GTIN" + instanceKey);
		cpInstanceModel.setManufacturerPartNumber("MPN" + instanceKey);

		cpInstanceModel.setPurchasable(true);
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

	public CProductModel newCProductModel(
		GroupModel commerceCatalogGroupModel) {

		CProductModel cProductModel = new CProductModelImpl();

		// UUID

		cProductModel.setUuid(SequentialUUID.generate());

		// PK fields

		cProductModel.setCProductId(counter.get());

		// Group instance

		cProductModel.setGroupId(commerceCatalogGroupModel.getGroupId());

		// Audit fields

		cProductModel.setCompanyId(COMPANY_ID);
		cProductModel.setUserId(SAMPLE_USER_ID);
		cProductModel.setUserName(SAMPLE_USER_NAME);
		cProductModel.setCreateDate(new Date());
		cProductModel.setModifiedDate(new Date());

		// Other fields

		cProductModel.setLatestVersion(
			BenchmarksPropsValues.MAX_COMMERCE_PRODUCT_DEFINITION_COUNT);

		return cProductModel;
	}

	public CPTaxCategoryModel newCPTaxCategoryModel() {
		CPTaxCategoryModel cpTaxCategoryModel = new CPTaxCategoryModelImpl();

		// PK fields

		cpTaxCategoryModel.setCPTaxCategoryId(counter.get());

		// Audit fields

		cpTaxCategoryModel.setCompanyId(COMPANY_ID);
		cpTaxCategoryModel.setUserId(SAMPLE_USER_ID);
		cpTaxCategoryModel.setUserName(SAMPLE_USER_NAME);
		cpTaxCategoryModel.setCreateDate(new Date());
		cpTaxCategoryModel.setModifiedDate(new Date());

		// Other fields

		cpTaxCategoryModel.setName(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root ",
				"available-locales=\"en_US\" default-locale=\"en_US\"><Name ",
				"language-id=\"en_US\">Normal Product</Name></root>"));

		cpTaxCategoryModel.setDescription(null);

		return cpTaxCategoryModel;
	}

	protected CPFriendlyURLEntryModel newCPFriendlyURLEntryModel(
		long groupId, long classNameId, long classPK, String urlTitle) {

		CPFriendlyURLEntryModel cpFriendlyURLEntryModel =
			new CPFriendlyURLEntryModelImpl();

		// UUID

		cpFriendlyURLEntryModel.setUuid(SequentialUUID.generate());

		// PK fields

		cpFriendlyURLEntryModel.setCPFriendlyURLEntryId(counter.get());

		// Group instance

		cpFriendlyURLEntryModel.setGroupId(groupId);

		// Audit fields

		cpFriendlyURLEntryModel.setCompanyId(COMPANY_ID);
		cpFriendlyURLEntryModel.setUserId(SAMPLE_USER_ID);
		cpFriendlyURLEntryModel.setUserName(SAMPLE_USER_NAME);
		cpFriendlyURLEntryModel.setCreateDate(new Date());
		cpFriendlyURLEntryModel.setModifiedDate(new Date());

		// Other fields

		cpFriendlyURLEntryModel.setClassNameId(classNameId);
		cpFriendlyURLEntryModel.setClassPK(classPK);
		cpFriendlyURLEntryModel.setLanguageId("en_US");
		cpFriendlyURLEntryModel.setUrlTitle(urlTitle);
		cpFriendlyURLEntryModel.setMain(true);

		return cpFriendlyURLEntryModel;
	}

}