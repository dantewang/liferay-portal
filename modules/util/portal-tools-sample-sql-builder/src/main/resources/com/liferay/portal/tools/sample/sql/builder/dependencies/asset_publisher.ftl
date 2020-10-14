<#assign
	assetVocabularyModels = dataFactory.newAssetVocabularyModels(groupId)
	assetCategoryModelsMap = dataFactory.newAssetCategoryModelsMap(groupId, assetVocabularyModels)
	assetTagModelsMap = dataFactory.newAssetTagModelsMap(groupId)
	pageCounts = dataFactory.getSequence(dataFactory.maxAssetPublisherPageCount)
/>

<#list assetVocabularyModels as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetCategoryModelsMap?values as assetCategoryModels>
	<#list assetCategoryModels as assetCategoryModel>
		${dataFactory.toInsertSQL(assetCategoryModel)}
	</#list>
</#list>

<#list assetTagModelsMap?values as assetTagModels>
	<#list assetTagModels as assetTagModel>
		${dataFactory.toInsertSQL(assetTagModel)}
	</#list>
</#list>

<#list pageCounts as pageCount>
	<#assign
		portletId = dataFactory.getPortletId("com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_")

		layoutModel = dataFactory.newLayoutModel(groupId, groupId + "_asset_publisher_" + pageCount, "", portletId)
	/>

	${csvFileWriter.write("assetPublisher", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = dataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	${dataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, assetCategoryModelsMap, assetTagModelsMap))}
</#list>