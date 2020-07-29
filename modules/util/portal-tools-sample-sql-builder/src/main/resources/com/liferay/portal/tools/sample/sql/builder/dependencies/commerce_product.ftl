<#assign
	cpInstanceModels = commerceDataFactory.newCPInstanceModels()
	cpDefinitionIdList = commerceDataFactory.getCPDefinitionIdList(cpInstanceModels)
	publishedCPDefinitionIds = commerceDataFactory.getPublishedCPDefinitionIds(cpDefinitionIdList)
	cProductModels = commerceDataFactory.newCProductModels(publishedCPDefinitionIds)
	cpDefinitionModels = commerceDataFactory.newCPDefinitionModels(cpDefinitionIdList, cProductModels)
	cpFriendlyURLEntryModels = commerceDataFactory.newCPFriendlyURLEntryModels(cProductModels)
	assetEntryModels = assetDataFactory.newCPDefinitionAssetEntryModels(cpDefinitionIdList)
	cpDefinitionLocalizationModels = commerceDataFactory.newCPDefinitionLocalizationModels(cpDefinitionIdList)
/>

<#list assetEntryModels as assetEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(assetEntryModel)}
</#list>

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCommerceCatalogModel())}

${resourcePermissionDataFactory.toInsertSQL(resourcePermissionDataFactory.newCommerceCatalogResourcePermission())}

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCommerceChannelModel())}

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCommerceCurrencyModel())}

<#list cpDefinitionLocalizationModels as cpDefinitionLocalizationModel>
	${resourcePermissionDataFactory.toInsertSQL(cpDefinitionLocalizationModel)}
</#list>

<#list cpDefinitionModels as cpDefinitionModel>
	${resourcePermissionDataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list cpFriendlyURLEntryModels as cpFriendlyURLEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list cpInstanceModels as cpInstanceModel>
	${resourcePermissionDataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list cProductModels as cProductModel>
	${resourcePermissionDataFactory.toInsertSQL(cProductModel)}
</#list>

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCPTaxCategoryModel())}

<@insertGroup _groupModel=userDataFactory.newCommerceCatalogGroupModel() />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel() />