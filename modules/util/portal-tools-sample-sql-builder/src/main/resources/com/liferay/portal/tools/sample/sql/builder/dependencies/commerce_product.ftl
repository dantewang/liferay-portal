<#list commerceDataFactory.assetEntryModels as assetEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(assetEntryModel)}
</#list>

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.commerceCatalogModel)}

${resourcePermissionDataFactory.toInsertSQL(resourcePermissionDataFactory.commerceCatalogResourcePermission())}

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.commerceChannelModel)}

${resourcePermissionDataFactory.toInsertSQL(commerceDataFactory.commerceCurrencyModel)}

<#list commerceDataFactory.CPDefinitionLocalizationModels as cpDefinitionLocalizationModel>
	${resourcePermissionDataFactory.toInsertSQL(cpDefinitionLocalizationModel)}
</#list>

<#list commerceDataFactory.CPDefinitionModels as cpDefinitionModel>
	${resourcePermissionDataFactory.toInsertSQL(cpDefinitionModel)}
</#list>

<#list commerceDataFactory.CPFriendlyURLEntryModels as cpFriendlyURLEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(cpFriendlyURLEntryModel)}

	${dataFactoryContext.getCSVWriter("cpFriendlyURLEntry").write(cpFriendlyURLEntryModel.urlTitle + "\n")}
</#list>

<#list commerceDataFactory.CPInstanceModels as cpInstanceModel>
	${resourcePermissionDataFactory.toInsertSQL(cpInstanceModel)}
</#list>

<#list commerceDataFactory.CProductModels as cProductModel>
	${resourcePermissionDataFactory.toInsertSQL(cProductModel)}
</#list>

<#list commerceDataFactory.CPTaxCategoryModels as cpTaxCategoryModel>
	${resourcePermissionDataFactory.toInsertSQL(cpTaxCategoryModel)}
</#list>