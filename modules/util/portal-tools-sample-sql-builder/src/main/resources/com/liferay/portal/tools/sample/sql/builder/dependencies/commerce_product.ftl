<#list dataFactory.newCPDefinitionAssetEntryModels() as assetEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(assetEntryModel)}
</#list>

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCommerceCatalogModel())}

${resourcePermissionDataFactory.toInsertSQL(resourcePermissionDataFactory.newCommerceCatalogResourcePermission())}

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCommerceChannelModel())}

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCommerceCurrencyModel())}

<#list commerceDataFactory.newCPDefinitionLocalizationModels() as cpDefinitionLocalizationModel>
	${resourcePermissionDataFactory.toInsertSQL(cpDefinitionLocalizationModel)}
</#list>

<#list commerceDataFactory.newCPDefinitionModels() as cpDefinitionModel>
	${resourcePermissionDataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list commerceDataFactory.newCPFriendlyURLEntryModels() as cpFriendlyURLEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${csvFileWriter.write("cpFriendlyURLEntry", cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list commerceDataFactory.newCPInstanceModels() as cpInstanceModel>
	${resourcePermissionDataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list commerceDataFactory.newCProductModels() as cProductModel>
	${resourcePermissionDataFactory.toInsertSQL(cProductModel)}
</#list>

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.newCPTaxCategoryModel())}

<@insertGroup _groupModel=userDataFactory.newCommerceCatalogGroupModel() />

<@insertGroup _groupModel=userDataFactory.newCommerceChannelGroupModel() />