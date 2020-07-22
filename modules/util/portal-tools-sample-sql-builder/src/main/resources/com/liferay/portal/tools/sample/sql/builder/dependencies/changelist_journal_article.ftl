<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

<#list cTCollectionModels as cTCollectionModel>
	${resourcePermissionDataFactory.toInsertSQL(cTCollectionModel)}
</#list>