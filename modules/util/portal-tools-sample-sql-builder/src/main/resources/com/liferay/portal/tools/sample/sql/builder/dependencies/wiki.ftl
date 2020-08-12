<#assign wikiNodeModels = wikiDataFactory.newWikiNodeModels(groupId) />

<#list wikiNodeModels as wikiNodeModel>
	${insertSQLBuilder.toInsertSQL(wikiNodeModel)}

	<#assign wikiPageModels = wikiDataFactory.newWikiPageModels(wikiNodeModel) />

	<#list wikiPageModels as wikiPageModel>
		${insertSQLBuilder.toInsertSQL(wikiPageModel)}

		${insertSQLBuilder.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(wikiPageModel))}

		${insertSQLBuilder.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(wikiPageModel))}

		${insertSQLBuilder.toInsertSQL(wikiDataFactory.newWikiPageResourceModel(wikiPageModel))}

		<@insertAssetEntry
			_categoryAndTag=true
			_entry=wikiPageModel
		/>

		<#assign mbRootMessageId = counterDataFactory.getCounterNext() />

		<@insertMBDiscussion
			_classNameId=wikiDataFactory.wikiPageClassNameId
			_classPK=wikiPageModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=wikiDataFactory.maxWikiPageCommentCount
			_mbRootMessageId=mbRootMessageId
			_mbThreadId=counterDataFactory.getCounterNext()
		/>

		${csvFileWriter.write("wiki", wikiNodeModel.nodeId + "," + wikiNodeModel.name + "," + wikiPageModel.resourcePrimKey + "," + wikiPageModel.title + "," + mbRootMessageId + "\n")}
	</#list>
</#list>