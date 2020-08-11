<#assign
	commerceCurrencyModel = commerceDataFactory.newCommerceCurrencyModel()

	commerceCatalogModel = commerceDataFactory.newCommerceCatalogModel(commerceCurrencyModel)

	commerceCatalogGroupModel = userDataFactory.newCommerceCatalogGroupModel(commerceCatalogModel)
	commerceChannelModel = commerceDataFactory.newCommerceChannelModel(commerceCurrencyModel)
	cpTaxCategoryModel = commerceDataFactory.newCPTaxCategoryModel()
/>

${dataFactory.toInsertSQL(commerceCatalogModel)}

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogResourcePermissionModel(commerceCatalogModel))}

${dataFactory.toInsertSQL(commerceChannelModel)}

${dataFactory.toInsertSQL(commerceCurrencyModel)}

<#list dataFactory.getSequence(commerceDataFactory.maxCommerceProductCount) as commerceProductCount>
	<#assign cProductModel = commerceDataFactory.newCProductModel(commerceCatalogGroupModel) />

	${dataFactory.toInsertSQL(cProductModel)}

	<#list dataFactory.getSequence(commerceDataFactory.maxCommerceProductDefinitionCount) as commerceProductDefinitionCount>
		<#assign
			cpDefinitionModel = commerceDataFactory.newCPDefinitionModel(cpTaxCategoryModel, cProductModel, commerceCatalogGroupModel, commerceProductDefinitionCount)
			cpFriendlyURLEntryModel = commerceDataFactory.newCPFriendlyURLEntryModel(cProductModel)
		/>

		${dataFactory.toInsertSQL(cpDefinitionModel)}

		${dataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

		${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}

		${dataFactory.toInsertSQL(dataFactory.newCPDefinitionModelAssetEntryModel(cpDefinitionModel, commerceCatalogGroupModel))}

		${dataFactory.toInsertSQL(commerceDataFactory.newCPDefinitionLocalizationModel(cpDefinitionModel))}

		<#list dataFactory.getSequence(commerceDataFactory.maxCommerceProductInstanceCount) as commerceProductInstanceCount>
			${dataFactory.toInsertSQL(commerceDataFactory.newCPInstanceModel(cpDefinitionModel, commerceCatalogGroupModel, commerceProductInstanceCount))}
		</#list>
	</#list>
</#list>

${dataFactory.toInsertSQL(cpTaxCategoryModel)}

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel(commerceChannelModel) />