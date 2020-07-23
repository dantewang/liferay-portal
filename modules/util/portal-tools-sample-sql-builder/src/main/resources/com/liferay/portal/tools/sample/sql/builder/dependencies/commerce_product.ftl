<#list dataFactory.newCPDefinitionAssetEntryModels() as assetEntryModel>
	${dataFactory.toInsertSQL(assetEntryModel)}
</#list>

${dataFactory.toInsertSQL(commerceDataFactory.newCommerceCatalogModel())}

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogResourcePermission())}

${dataFactory.toInsertSQL(commerceDataFactory.newCommerceChannelModel())}

${dataFactory.toInsertSQL(commerceDataFactory.newCommerceCurrencyModel())}

<#list commerceDataFactory.newCPDefinitionLocalizationModels() as cpDefinitionLocalizationModel>
	${dataFactory.toInsertSQL(cpDefinitionLocalizationModel)}
</#list>

<#list commerceDataFactory.newCPDefinitionModels() as cpDefinitionModel>
	${dataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list commerceDataFactory.newCPFriendlyURLEntryModels() as cpFriendlyURLEntryModel>
	${dataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list commerceDataFactory.newCPInstanceModels() as cpInstanceModel>
	${dataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list commerceDataFactory.newCProductModels() as cProductModel>
	${dataFactory.toInsertSQL(cProductModel)}
</#list>

${dataFactory.toInsertSQL(commerceDataFactory.newCPTaxCategoryModel())}

<@insertGroup _groupModel=userDataFactory.newCommerceCatalogGroupModel() />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel() />