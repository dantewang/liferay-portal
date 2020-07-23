<#assign wikiNodeModels = wikiDataFactory.newWikiNodeModels(groupId) />

<#list wikiNodeModels as wikiNodeModel>
	${dataFactory.toInsertSQL(wikiNodeModel)}

	<#assign wikiPageModels = wikiDataFactory.newWikiPageModels(wikiNodeModel) />

	<#list wikiPageModels as wikiPageModel>
		${dataFactory.toInsertSQL(wikiPageModel)}

		${dataFactory.toInsertSQL(dataFactory.newMBDiscussionAssetEntryModel(wikiPageModel))}

		${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(wikiPageModel))}

		${dataFactory.toInsertSQL(wikiDataFactory.newWikiPageResourceModel(wikiPageModel))}

		<@insertAssetEntry
			_categoryAndTag=true
			_entry=wikiPageModel
		/>

		<#assign mbRootMessageId = dataFactory.getCounterNext() />

		<@insertMBDiscussion
			_classNameId=wikiDataFactory.wikiPageClassNameId
			_classPK=wikiPageModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=wikiDataFactory.maxWikiPageCommentCount
			_mbRootMessageId=mbRootMessageId
			_mbThreadId=dataFactory.getCounterNext()
		/>

		${csvFileWriter.write("wiki", wikiNodeModel.nodeId + "," + wikiNodeModel.name + "," + wikiPageModel.resourcePrimKey + "," + wikiPageModel.title + "," + mbRootMessageId + "\n")}
	</#list>
</#list>