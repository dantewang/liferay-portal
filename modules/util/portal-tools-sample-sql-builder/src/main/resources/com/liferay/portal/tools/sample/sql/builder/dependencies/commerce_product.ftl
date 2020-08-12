<#assign
	commerceCurrencyModel = commerceDataFactory.newCommerceCurrencyModel()

	commerceCatalogModel = commerceDataFactory.newCommerceCatalogModel(commerceCurrencyModel)

	commerceCatalogGroupModel = userDataFactory.newCommerceCatalogGroupModel(commerceCatalogModel)
	commerceChannelModel = commerceDataFactory.newCommerceChannelModel(commerceCurrencyModel)
	cpTaxCategoryModel = commerceDataFactory.newCPTaxCategoryModel()
/>

${insertSQLBuilder.toInsertSQL(commerceCatalogModel)}

${insertSQLBuilder.toInsertSQL(commerceDataFactory.newCommerceCatalogResourcePermissionModel(commerceCatalogModel))}

${insertSQLBuilder.toInsertSQL(commerceChannelModel)}

${insertSQLBuilder.toInsertSQL(commerceCurrencyModel)}

<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductCount) as commerceProductCount>
	<#assign cProductModel = commerceDataFactory.newCProductModel(commerceCatalogGroupModel) />

	${insertSQLBuilder.toInsertSQL(cProductModel)}

	<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductDefinitionCount) as commerceProductDefinitionCount>
		<#assign
			cpDefinitionModel = commerceDataFactory.newCPDefinitionModel(cpTaxCategoryModel, cProductModel, commerceCatalogGroupModel, commerceProductDefinitionCount)
			cpFriendlyURLEntryModel = commerceDataFactory.newCPFriendlyURLEntryModel(cProductModel)
		/>

		${insertSQLBuilder.toInsertSQL(cpDefinitionModel)}

		${insertSQLBuilder.toInsertSQL(cpFriendlyURLEntryModel)}

		${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}

		${insertSQLBuilder.toInsertSQL(assetDataFactory.newCPDefinitionModelAssetEntryModel(cpDefinitionModel, commerceCatalogGroupModel))}

		${insertSQLBuilder.toInsertSQL(commerceDataFactory.newCPDefinitionLocalizationModel(cpDefinitionModel))}

		<#list counterDataFactory.getSequence(commerceDataFactory.maxCommerceProductInstanceCount) as commerceProductInstanceCount>
			${insertSQLBuilder.toInsertSQL(commerceDataFactory.newCPInstanceModel(cpDefinitionModel, commerceCatalogGroupModel, commerceProductInstanceCount))}
		</#list>
	</#list>
</#list>

${insertSQLBuilder.toInsertSQL(cpTaxCategoryModel)}

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel(commerceChannelModel) />