<#assign
	wikiNodeModels = wikiDataFactory.newWikiNodeModels(groupId)
	wikiPageClassNameId = classNameDataFactory.getClassNameId("WikiPage")
/>

<#list wikiNodeModels as wikiNodeModel>
	${resourcePermissionDataFactory.toInsertSQL(wikiNodeModel)}

	<#assign wikiPageModels = wikiDataFactory.newWikiPageModels(wikiNodeModel) />

	<#list wikiPageModels as wikiPageModel>
		${resourcePermissionDataFactory.toInsertSQL(wikiPageModel)}

		${resourcePermissionDataFactory.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(wikiPageModel, classNameDataFactory.getClassNameId("MBDiscussion_WikiPage")))}

		${resourcePermissionDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(wikiPageModel, wikiPageClassNameId))}

		${resourcePermissionDataFactory.toInsertSQL(wikiDataFactory.newWikiPageResourceModel(wikiPageModel))}

		<@insertAssetEntry
			_categoryAndTag=true
			_classNameIds=[wikiPageClassNameId]
			_entry=wikiPageModel
		/>

		<#assign mbRootMessageId = counterDataFactory.getCounterNext() />

		<@insertMBDiscussion
			_classNameId=wikiPageClassNameId
			_classPK=wikiPageModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=wikiDataFactory.maxWikiPageCommentCount
			_mbRootMessageId=mbRootMessageId
			_mbThreadId=counterDataFactory.getCounterNext()
		/>

		${csvFileWriter.write("wiki", wikiNodeModel.nodeId + "," + wikiNodeModel.name + "," + wikiPageModel.resourcePrimKey + "," + wikiPageModel.title + "," + mbRootMessageId + "\n")}
	</#list>
</#list>