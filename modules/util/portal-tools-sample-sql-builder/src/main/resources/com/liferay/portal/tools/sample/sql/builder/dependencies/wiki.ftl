<#assign
	wikiNodeModels = dataFactory.newWikiNodeModels(groupId)
	wikiPageClassNameId = dataFactory.getClassNameId("com.liferay.wiki.model.WikiPage")
/>

<#list wikiNodeModels as wikiNodeModel>
	${dataFactory.toInsertSQL(wikiNodeModel)}

	<#assign wikiPageModels = dataFactory.newWikiPageModels(wikiNodeModel) />

	<#list wikiPageModels as wikiPageModel>
		${dataFactory.toInsertSQL(wikiPageModel)}

		${dataFactory.toInsertSQL(dataFactory.newMBDiscussionAssetEntryModel(wikiPageModel, dataFactory.getClassNameId("com.liferay.message.boards.model.MBDiscussion_com.liferay.wiki.model.WikiPage")))}

		${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(wikiPageModel, wikiPageClassNameId))}

		${dataFactory.toInsertSQL(dataFactory.newWikiPageResourceModel(wikiPageModel))}

		<@insertAssetEntry
			_categoryAndTag=true
			_classNameIds=[wikiPageClassNameId]
			_entry=wikiPageModel
		/>

		<#assign mbRootMessageId = dataFactory.getCounterNext() />

		<@insertMBDiscussion
			_classNameId=dataFactory.wikiPageClassNameId
			_classPK=wikiPageModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=dataFactory.maxWikiPageCommentCount
			_mbRootMessageId=mbRootMessageId
			_mbThreadId=dataFactory.getCounterNext()
		/>

		${csvFileWriter.write("wiki", wikiNodeModel.nodeId + "," + wikiNodeModel.name + "," + wikiPageModel.resourcePrimKey + "," + wikiPageModel.title + "," + mbRootMessageId + "\n")}
	</#list>
</#list>