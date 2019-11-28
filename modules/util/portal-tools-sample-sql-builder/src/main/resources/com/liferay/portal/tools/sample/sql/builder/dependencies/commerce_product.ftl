<#assign
	commerceCurrencyModel = dataFactory.newCommerceCurrencyModel()

	commerceCatalogModel = dataFactory.newCommerceCatalogModel(commerceCurrencyModel)

	commerceChannelModel = dataFactory.newCommerceChannelModel(commerceCurrencyModel)

	commerceCatalogGroupModel = dataFactory.newCommerceCatalogGroupModel(commerceCatalogModel)

	commerceChannelGroupModel = dataFactory.newCommerceChannelGroupModel(commerceChannelModel)

	cPDefinitionLocalizationModels = dataFactory.newCPDefinitionLocalizationModels()

	assetEntryModels = dataFactory.newAssetEntryModels(cPDefinitionLocalizationModels)
/>

<#list assetEntryModels as assetEntryModel>
	${dataFactory.toInsertSQL(assetEntryModel)}
</#list>

${dataFactory.toInsertSQL(commerceCatalogModel)}

${dataFactory.toInsertSQL(dataFactory.newResourcePermissionModel(commerceCatalogModel))}

${dataFactory.toInsertSQL(commerceChannelModel)}

${dataFactory.toInsertSQL(commerceCurrencyModel)}

<#list cPDefinitionLocalizationModels as cpDefinitionLocalizationModel>
	${dataFactory.toInsertSQL(cpDefinitionLocalizationModel)}
</#list>

<#list dataFactory.CPDefinitionModels as cpDefinitionModel>
	${dataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list dataFactory.CPFriendlyURLEntryModels as cpFriendlyURLEntryModel>
	${dataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${dataFactory.getCSVWriter("cpFriendlyURLEntry").write(cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list dataFactory.CPInstanceModels as cpInstanceModel>
	${dataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list dataFactory.CProductModels as cProductModel>
	${dataFactory.toInsertSQL(cProductModel)}
</#list>

<#list dataFactory.CPTaxCategoryModels as cpTaxCategoryModel>
	${dataFactory.toInsertSQL(cpTaxCategoryModel)}
</#list>

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=commerceChannelGroupModel />