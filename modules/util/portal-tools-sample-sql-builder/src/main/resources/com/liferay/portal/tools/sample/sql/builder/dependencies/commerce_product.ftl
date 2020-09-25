<#assign
	commerceCurrencyModel = commerceDataFactory.newCommerceCurrencyModel()

	commerceCatalogModel = commerceDataFactory.newCommerceCatalogModel(commerceCurrencyModel)

	commerceCatalogGroupModel = userDataFactory.newCommerceCatalogGroupModel(commerceCatalogModel, classNameDataFactory.getClassNameId("CommerceCatalog"))
	commerceChannelModel = commerceDataFactory.newCommerceChannelModel(commerceCurrencyModel)
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
		/>

		${resourcePermissionDataFactory.toInsertSQL(cpDefinitionModel)}

		${resourcePermissionDataFactory.toInsertSQL(assetDataFactory.newCPDefinitionModelAssetEntryModel(cpDefinitionModel, commerceCatalogGroupModel, classNameDataFactory.getClassNameId("CPDefinition")))}

		${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCPDefinitionLocalizationModel(cpDefinitionModel))}

		<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductInstanceCount) as commerceProductInstanceCount>
			${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCPInstanceModel(cpDefinitionModel, commerceCatalogGroupModel, commerceProductInstanceCount))}
		</#list>
	</#list>
</#list>

${resourcePermissionDataFactory.toInsertSQL(cpTaxCategoryModel)}

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel(commerceChannelModel, classNameDataFactory.getClassNameId("CommerceChannel")) />