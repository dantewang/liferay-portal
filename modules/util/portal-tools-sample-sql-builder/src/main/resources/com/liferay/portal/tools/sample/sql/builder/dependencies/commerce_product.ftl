<#assign
	cpInstanceModels = commerceDataFactory.newCPInstanceModels()
	cpDefinitionIdList = commerceDataFactory.getCPDefinitionIdList(cpInstanceModels)
	publishedCPDefinitionIds = commerceDataFactory.getPublishedCPDefinitionIds(cpDefinitionIdList)
	cProductModels = commerceDataFactory.newCProductModels(publishedCPDefinitionIds)
	cpDefinitionModels = commerceDataFactory.newCPDefinitionModels(cpDefinitionIdList, cProductModels)
	cpFriendlyURLEntryModels = commerceDataFactory.newCPFriendlyURLEntryModels(cProductModels)
	assetEntryModels = dataFactory.newCPDefinitionAssetEntryModels(cpDefinitionIdList)
	cpDefinitionLocalizationModels = commerceDataFactory.newCPDefinitionLocalizationModels(cpDefinitionIdList)
/>

<#list assetEntryModels as assetEntryModel>
	${dataFactory.toInsertSQL(assetEntryModel)}
</#list>

${dataFactory.toInsertSQL(commerceDataFactory.newCommerceCatalogModel())}

${dataFactory.toInsertSQL(dataFactory.newCommerceCatalogResourcePermission())}

${dataFactory.toInsertSQL(commerceDataFactory.newCommerceChannelModel())}

${dataFactory.toInsertSQL(commerceDataFactory.newCommerceCurrencyModel())}

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

${dataFactory.toInsertSQL(commerceDataFactory.newCPTaxCategoryModel())}

<@insertGroup _groupModel=userDataFactory.newCommerceCatalogGroupModel() />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel() />