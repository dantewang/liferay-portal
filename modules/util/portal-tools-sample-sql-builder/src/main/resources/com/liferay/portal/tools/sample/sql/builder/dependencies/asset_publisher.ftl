<#assign
	blogsEntryClassNameId = dataFactory.getClassNameId("com.liferay.blogs.model.BlogsEntry")
	journalArticleClassNameId = dataFactory.getClassNameId("com.liferay.journal.model.JournalArticle")
	wikiPageClassNameId = dataFactory.getClassNameId("com.liferay.wiki.model.WikiPage")
	pageCounts = dataFactory.getSequence(dataFactory.maxAssetPublisherPageCount)
/>
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

	${dataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, assetClassNameIds, assetCategoryModelsMaps, assetTagModelsMaps))}
</#list>