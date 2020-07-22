<#assign pageCounts = counterDataFactory.getSequence(assetDataFactory.maxAssetPublisherPageCount) />

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

	${resourcePermissionDataFactory.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount))}
</#list>