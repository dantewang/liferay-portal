<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

${resourcePermissionDataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${resourcePermissionDataFactory.toInsertSQL(cTCollectionModel)}
</#list>