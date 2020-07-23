<#assign
	cpInstanceModels = dataFactory.newCPInstanceModels()
	cpDefinitionIdList = dataFactory.getCPDefinitionIdList(cpInstanceModels)
	publishedCPDefinitionIds = dataFactory.getPublishedCPDefinitionIds(cpDefinitionIdList)
	cProductModels = dataFactory.newCProductModels(publishedCPDefinitionIds)
	cpDefinitionModels = dataFactory.newCPDefinitionModels(cpDefinitionIdList, cProductModels)
	cpFriendlyURLEntryModels = dataFactory.newCPFriendlyURLEntryModels(cProductModels)
	assetEntryModels = dataFactory.newCPDefinitionAssetEntryModels(cpDefinitionIdList)
	cpDefinitionLocalizationModels = dataFactory.newCPDefinitionLocalizationModels(cpDefinitionIdList)
/>

<#list assetEntryModels as assetEntryModel>
	${dataFactory.toInsertSQL(assetEntryModel)}
</#list>

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogModel())}

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogResourcePermission())}

${dataFactory.toInsertSQL(dataFactory.newCommerceChannelModel())}

${dataFactory.toInsertSQL(dataFactory.newCommerceCurrencyModel())}

<#list cpDefinitionLocalizationModels as cpDefinitionLocalizationModel>
	${dataFactory.toInsertSQL(cpDefinitionLocalizationModel)}
</#list>

<#list cpDefinitionModels as cpDefinitionModel>
	${dataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list cpFriendlyURLEntryModels as cpFriendlyURLEntryModel>
	${dataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list cpInstanceModels as cpInstanceModel>
	${dataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list cProductModels as cProductModel>
	${dataFactory.toInsertSQL(cProductModel)}
</#list>

${dataFactory.toInsertSQL(dataFactory.newCPTaxCategoryModel())}

<@insertGroup _groupModel=userDataFactory.newCommerceCatalogGroupModel() />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel() />