<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

${dataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${dataFactory.toInsertSQL(cTCollectionModel)}
</#list>