<#assign
	assetVocabularyModels = dataFactory.newAssetVocabularyModels(groupId)
	assetCategoryModels = dataFactory.newAssetCategoryModels(groupId, assetVocabularyModels)
	assetTagModels = dataFactory.newAssetTagModels(groupId)
	pageCounts = dataFactory.getSequence(dataFactory.maxAssetPublisherPageCount)
/>

<#list assetVocabularyModels as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetCategoryModels as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetTagModels as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>

<#include "blogs.ftl">

<#include "journal_article.ftl">

<#include "wiki.ftl">

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

	<#if pageCount = 1>
		${dataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, portletId))}
	<#elseif pageCount % 2 = 0>
		<#assign nextAssetTagModels = dataFactory.getNextAssetTagModels([journalArticleAssetTagModels, blogAssetTagModels, wikiAssetTagModels]) />

		${dataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, nextAssetTagModels))}
	<#else>
		<#assign nextAssetCategoryModels = dataFactory.getNextAssetCategoryModels([journalArticleAssetCategoryModels, blogAssetCategoryModels, wikiAssetCategoryModels]) />

		${dataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, nextAssetCategoryModels))}
	</#if>
</#list>