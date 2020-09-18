<#assign
	assetVocabularyModelsArray = dataFactory.newAssetVocabularyModelsArray()
	assetCategoryModelsMaps = dataFactory.newAssetCategoryModelsMaps(assetVocabularyModelsArray)
	assetTagModelsMaps = dataFactory.newAssetTagModelsMaps()
/>

<#list dataFactory.newAssetVocabularyModels(dataFactory.newDefaultAssetVocabularyModel(), assetVocabularyModelsArray) as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list dataFactory.newAssetCategoryModels(assetCategoryModelsMaps) as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list dataFactory.newAssetTagModels(assetTagModelsMaps) as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>