<#list dataFactory.newAssetVocabularyModels() as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list dataFactory.newAssetCategoryModels() as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list dataFactory.newAssetTagModels() as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>