<#assign pageCounts = counterDataFactory.getSequence(initContext.maxAssetPublisherPageCount) />

<#list pageCounts as pageCount>
	<#assign
		portletId = "com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_TEST_" + pageCount

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_asset_publisher_" + pageCount, "", portletId)
	/>

	${assetPublisherCSVWriter.write(layoutModel.friendlyURL + "\n")}

	<@insertLayout
		_layoutModel = layoutModel
	/>

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		<@insertPortletPreferences
			_portletPreferencesModel = portletPreferencesModel
		/>
	</#list>

	<#assign portletPreferencesModel = portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount) />

	<@insertPortletPreferences
		_portletPreferencesModel = portletPreferencesModel
	/>
</#list>