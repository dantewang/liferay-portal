<#assign
	assetClassNameIds = classNameDataFactory.assetClassNameIds
	assetVocabularyModels = dataFactory.newAssetVocabularyModels(groupId)
	pageCounts = dataFactory.getSequence(dataFactory.maxAssetPublisherPageCount)
/>

<#list assetVocabularyModels as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list dataFactory.newAssetCategoryModels(groupId, assetVocabularyModels, assetClassNameIds) as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list dataFactory.newAssetTagModels(groupId, assetClassNameIds) as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>

<#list pageCounts as pageCount>
	<#assign
		portletId = portletPreferenceDataFactory.getPortletId("com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_")

		layoutModel = dataFactory.newLayoutModel(groupId, groupId + "_asset_publisher_" + pageCount, "", portletId)
	/>

	${csvFileWriter.write("assetPublisher", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	${dataFactory.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, assetClassNameIds))}
</#list>