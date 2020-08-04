<#assign
	commerceCurrencyModel = commerceDataFactory.newCommerceCurrencyModel()
	commerceCatalogModel = commerceDataFactory.newCommerceCatalogModel(commerceCurrencyModel)
	commerceChannelModel = commerceDataFactory.newCommerceChannelModel(commerceCurrencyModel)
	commerceCatalogGroupModel = userDataFactory.newCommerceCatalogGroupModel(commerceCatalogModel)
	cpTaxCategoryModel = commerceDataFactory.newCPTaxCategoryModel()
/>

${resourcePermissionDataFactory.toInsertSQL(commerceCatalogModel)}

${resourcePermissionDataFactory.toInsertSQL(resourcePermissionDataFactory.newCommerceCatalogResourcePermissionModel(commerceCatalogModel))}

${resourcePermissionDataFactory.toInsertSQL(commerceChannelModel)}

${resourcePermissionDataFactory.toInsertSQL(commerceCurrencyModel)}

<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductCount) as commerceProductCount>
	<#assign cProductModel = commerceDataFactory.newCProductModel(commerceCatalogGroupModel) />

	${resourcePermissionDataFactory.toInsertSQL(cProductModel)}

	<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductDefinitionCount) as commerceProductDefinitionCount>
		<#assign
			cpDefinitionModel = commerceDataFactory.newCPDefinitionModel(cpTaxCategoryModel, cProductModel, commerceCatalogGroupModel, commerceProductDefinitionCount)
			cpFriendlyURLEntryModel = commerceDataFactory.newCPFriendlyURLEntryModel(cProductModel)
			assetEntryModel = dataFactory.newCPDefinitionAssetEntryModel(cpDefinitionModel, commerceCatalogGroupModel)
			cpDefinitionLocalizationModel = commerceDataFactory.newCPDefinitionLocalizationModel(cpDefinitionModel)
		/>

		${resourcePermissionDataFactory.toInsertSQL(cpDefinitionModel)}

		${resourcePermissionDataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

		${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}

		${resourcePermissionDataFactory.toInsertSQL(assetEntryModel)}

		${resourcePermissionDataFactory.toInsertSQL(cpDefinitionLocalizationModel)}

		<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductInstanceCount) as commerceProductInstanceCount>
			${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCPInstanceModel(cpDefinitionModel, commerceCatalogGroupModel, commerceProductInstanceCount))}
		</#list>
	</#list>
</#list>

${resourcePermissionDataFactory.toInsertSQL(cpTaxCategoryModel)}

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel(commerceChannelModel) />