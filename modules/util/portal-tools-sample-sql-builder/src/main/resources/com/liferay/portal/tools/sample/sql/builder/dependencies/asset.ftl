<#list assetDataFactory.assetVocabularyModels as assetVocabularyModel>
	${resourcePermissionDataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.assetCategoryModels as assetCategoryModel>
	${resourcePermissionDataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.assetTagModels as assetTagModel>
	${resourcePermissionDataFactory.toInsertSQL(assetTagModel)}
</#list>