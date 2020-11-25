<#assign
	blogAssetCategoryModels = dataFactory.pickAssetCategoryModels(assetCategoryModels, 2)

	blogAssetTagModels = dataFactory.pickAssetTagModels(assetTagModels, 2)

	blogsEntryModels = dataFactory.newBlogsEntryModels(groupId)

	blogsEntryClassNameId = dataFactory.getClassNameId("BlogsEntry")
/>

<#list blogsEntryModels as blogsEntryModel>
	${dataFactory.toInsertSQL(blogsEntryModel)}

	<#assign friendlyURLEntryModel = dataFactory.newFriendlyURLEntryModel(blogsEntryModel, blogsEntryClassNameId) />

	${dataFactory.toInsertSQL(friendlyURLEntryModel)}

	${dataFactory.toInsertSQL(dataFactory.newFriendlyURLEntryLocalizationModel(friendlyURLEntryModel, blogsEntryModel))}

	${dataFactory.toInsertSQL(dataFactory.newFriendlyURLEntryMapping(friendlyURLEntryModel))}

	${dataFactory.toInsertSQL(dataFactory.newMBDiscussionAssetEntryModel(blogsEntryModel, dataFactory.getClassNameId("MBDiscussion_BlogsEntry")))}

	<@insertAssetEntry
		_assetCategoryModels=blogAssetCategoryModels
		_assetTagModels=blogAssetTagModels
		_classNameIds=[blogsEntryClassNameId]
		_entry=blogsEntryModel
	/>

	<#assign mbRootMessageId = dataFactory.getCounterNext() />

	<@insertMBDiscussion
		_classNameId=blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=dataFactory.maxBlogsEntryCommentCount
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=dataFactory.getCounterNext()
	/>

	${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(blogsEntryModel, blogsEntryClassNameId))}

	${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(blogsEntryModel, blogsEntryClassNameId))}

	${csvFileWriter.write("blog", blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbRootMessageId + "\n")}
</#list>