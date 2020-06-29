<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

<#list cTCollectionModels as cTCollectionModel>
	${dataFactory.toInsertSQL(cTCollectionModel)}
</#list>