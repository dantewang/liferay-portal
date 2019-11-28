<#assign
	commerceCurrencyModel = dataFactory.newCommerceCurrencyModel()

	commerceCatalogModel = dataFactory.newCommerceCatalogModel(commerceCurrencyModel)

	commerceChannelModel = dataFactory.newCommerceChannelModel(commerceCurrencyModel)

	commerceCatalogGroupModel = dataFactory.newCommerceCatalogGroupModel(commerceCatalogModel)

	commerceChannelGroupModel = dataFactory.newCommerceChannelGroupModel(commerceChannelModel)

	cPDefinitionLocalizationModels = dataFactory.newCPDefinitionLocalizationModels()

	assetEntryModels = dataFactory.newAssetEntryModels(cPDefinitionLocalizationModels)

	cProductModels=dataFactory.newCProductModels()

	cpTaxCategoryModel=dataFactory.newCPTaxCategoryModel("Normal Product")

	cpDefinitionModels=dataFactory.newCPDefinitionModels(cpTaxCategoryModel, cProductModels)

	cpFriendlyURLEntryModels=dataFactory.newCPFriendlyURLEntryModels(cProductModels)

	cpInstanceModels=dataFactory.newCPInstanceModels()
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

<#list cpDefinitionModels as cpDefinitionModel>
	${dataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list cpFriendlyURLEntryModels as cpFriendlyURLEntryModel>
	${dataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${dataFactory.getCSVWriter("cpFriendlyURLEntry").write(cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list cpInstanceModels as cpInstanceModel>
	${dataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list cProductModels as cProductModel>
	${dataFactory.toInsertSQL(cProductModel)}
</#list>

${dataFactory.toInsertSQL(cpTaxCategoryModel)}

<@insertGroup _groupModel=commerceCatalogGroupModel />

<@insertGroup _groupModel=commerceChannelGroupModel />