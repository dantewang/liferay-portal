<#assign
	commerceCurrencyModel = commerceDataFactory.newCommerceCurrencyModel()

	commerceCatalogModel = commerceDataFactory.newCommerceCatalogModel(commerceCurrencyModel)

	commerceCatalogGroupModel = userDataFactory.newCommerceCatalogGroupModel(commerceCatalogModel)
	commerceChannelModel = commerceDataFactory.newCommerceChannelModel(commerceCurrencyModel)
	cpTaxCategoryModel = commerceDataFactory.newCPTaxCategoryModel()
/>

${resourcePermissionDataFactory.toInsertSQL(commerceCatalogModel)}

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCommerceCatalogResourcePermissionModel(commerceCatalogModel))}

${resourcePermissionDataFactory.toInsertSQL(commerceChannelModel)}

${resourcePermissionDataFactory.toInsertSQL(commerceCurrencyModel)}

<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductCount) as commerceProductCount>
	<#assign cProductModel = commerceDataFactory.newCProductModel(commerceCatalogGroupModel) />

	${resourcePermissionDataFactory.toInsertSQL(cProductModel)}

	<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductDefinitionCount) as commerceProductDefinitionCount>
		<#assign
			cpDefinitionModel = commerceDataFactory.newCPDefinitionModel(cpTaxCategoryModel, cProductModel, commerceCatalogGroupModel, commerceProductDefinitionCount)
			cpFriendlyURLEntryModel = commerceDataFactory.newCPFriendlyURLEntryModel(cProductModel)
		/>

		${resourcePermissionDataFactory.toInsertSQL(cpDefinitionModel)}

		${resourcePermissionDataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

		${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}

		${resourcePermissionDataFactory.toInsertSQL(assetDataFactory.newCPDefinitionModelAssetEntryModel(cpDefinitionModel, commerceCatalogGroupModel))}

		${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCPDefinitionLocalizationModel(cpDefinitionModel))}

		<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductInstanceCount) as commerceProductInstanceCount>
			${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCPInstanceModel(cpDefinitionModel, commerceCatalogGroupModel, commerceProductInstanceCount))}
		</#list>
	</#list>
</#list>

${resourcePermissionDataFactory.toInsertSQL(cpTaxCategoryModel)}

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel(commerceChannelModel) />