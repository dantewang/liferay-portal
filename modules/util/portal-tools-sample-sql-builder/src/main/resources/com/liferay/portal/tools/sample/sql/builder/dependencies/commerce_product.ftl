<#assign
	commerceCurrencyModel = commerceDataFactory.newCommerceCurrencyModel()
	commerceCatalogModel = commerceDataFactory.newCommerceCatalogModel(commerceCurrencyModel)
	commerceChannelModel = commerceDataFactory.newCommerceChannelModel(commerceCurrencyModel)
	commerceCatalogGroupModel = userDataFactory.newCommerceCatalogGroupModel(commerceCatalogModel)
	cpTaxCategoryModel = commerceDataFactory.newCPTaxCategoryModel()
/>

${dataFactory.toInsertSQL(commerceCatalogModel)}

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogResourcePermissionModel(commerceCatalogModel))}

${dataFactory.toInsertSQL(commerceChannelModel)}

${dataFactory.toInsertSQL(commerceCurrencyModel)}

<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductCount) as commerceProductCount>
	<#assign cProductModel = commerceDataFactory.newCProductModel(commerceCatalogGroupModel) />

	${dataFactory.toInsertSQL(cProductModel)}

	<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductDefinitionCount) as commerceProductDefinitionCount>
		<#assign
			cpDefinitionModel = commerceDataFactory.newCPDefinitionModel(cpTaxCategoryModel, cProductModel, commerceCatalogGroupModel, commerceProductDefinitionCount)
			cpFriendlyURLEntryModel = commerceDataFactory.newCPFriendlyURLEntryModel(cProductModel)
			assetEntryModel = dataFactory.newCPDefinitionAssetEntryModel(cpDefinitionModel, commerceCatalogGroupModel)
			cpDefinitionLocalizationModel = commerceDataFactory.newCPDefinitionLocalizationModel(cpDefinitionModel)
		/>

		${dataFactory.toInsertSQL(cpDefinitionModel)}

		${dataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

		${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}

		${dataFactory.toInsertSQL(assetEntryModel)}

		${dataFactory.toInsertSQL(cpDefinitionLocalizationModel)}

		<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductInstanceCount) as commerceProductInstanceCount>
			${dataFactory.toInsertSQL(commerceDataFactory.newCPInstanceModel(cpDefinitionModel, commerceCatalogGroupModel, commerceProductInstanceCount))}
		</#list>
	</#list>
</#list>

${dataFactory.toInsertSQL(cpTaxCategoryModel)}

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel(commerceChannelModel) />