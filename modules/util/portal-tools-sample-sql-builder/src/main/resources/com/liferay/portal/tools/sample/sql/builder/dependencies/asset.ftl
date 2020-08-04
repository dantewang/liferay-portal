<#list dataFactory.assetVocabularyModels as assetVocabularyModel>
	${resourcePermissionDataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list dataFactory.assetCategoryModels as assetCategoryModel>
	${resourcePermissionDataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list dataFactory.assetTagModels as assetTagModel>
	${resourcePermissionDataFactory.toInsertSQL(assetTagModel)}
</#list>