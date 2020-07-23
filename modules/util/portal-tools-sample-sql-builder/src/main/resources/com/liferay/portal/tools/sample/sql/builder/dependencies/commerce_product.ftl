<#list dataFactory.newCPDefinitionAssetEntryModels() as assetEntryModel>
	${dataFactory.toInsertSQL(assetEntryModel)}
</#list>

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogModel())}

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogResourcePermission())}

${dataFactory.toInsertSQL(dataFactory.newCommerceChannelModel())}

${dataFactory.toInsertSQL(dataFactory.newCommerceCurrencyModel())}

<#list dataFactory.newCPDefinitionLocalizationModels() as cpDefinitionLocalizationModel>
	${dataFactory.toInsertSQL(cpDefinitionLocalizationModel)}
</#list>

<#list dataFactory.newCPDefinitionModels() as cpDefinitionModel>
	${dataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list dataFactory.newCPFriendlyURLEntryModels() as cpFriendlyURLEntryModel>
	${dataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list dataFactory.newCPInstanceModels() as cpInstanceModel>
	${dataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list dataFactory.newCProductModels() as cProductModel>
	${dataFactory.toInsertSQL(cProductModel)}
</#list>

${dataFactory.toInsertSQL(dataFactory.newCPTaxCategoryModel())}

<@insertGroup _groupModel=userDataFactory.newCommerceCatalogGroupModel() />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel() />