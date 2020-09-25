<#assign
	assetClassNameIds = classNameDataFactory.assetClassNameIds
	assetVocabularyModels = assetDataFactory.newAssetVocabularyModels(groupId)
	pageCounts = counterDataFactory.getSequence(assetDataFactory.maxAssetPublisherPageCount)
/>

<#list assetVocabularyModels as assetVocabularyModel>
	${resourcePermissionDataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.newAssetCategoryModels(groupId, assetVocabularyModels, assetClassNameIds) as assetCategoryModel>
	${resourcePermissionDataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.newAssetTagModels(groupId, assetClassNameIds) as assetTagModel>
	${resourcePermissionDataFactory.toInsertSQL(assetTagModel)}
</#list>

<#list pageCounts as pageCount>
	<#assign
		portletId = portletPreferenceDataFactory.getPortletId("com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_")

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_asset_publisher_" + pageCount, "", portletId)
	/>

	${csvFileWriter.write("assetPublisher", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${resourcePermissionDataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	${resourcePermissionDataFactory.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, assetClassNameIds))}
</#list>