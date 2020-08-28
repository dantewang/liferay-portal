<#assign
	assetClassNameIds = dataFactory.assetClassNameIds
	assetVocabularyModelsArray = dataFactory.newAssetVocabularyModelsArray()
	assetCategoryModelsMaps = dataFactory.newAssetCategoryModelsMaps(assetVocabularyModelsArray, assetClassNameIds)
/>

<#list dataFactory.newAssetVocabularyModels(dataFactory.newDefaultAssetVocabularyModel(), assetVocabularyModelsArray) as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list dataFactory.newAssetCategoryModels(assetCategoryModelsMaps) as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list dataFactory.assetTagModels as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>