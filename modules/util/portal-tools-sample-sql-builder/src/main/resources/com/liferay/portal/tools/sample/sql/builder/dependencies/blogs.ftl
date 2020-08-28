<#assign
	blogsEntryClassNameId = dataFactory.getClassNameId("com.liferay.blogs.model.BlogsEntry")
	blogsEntryModels = dataFactory.newBlogsEntryModels(groupId)
/>

<#list blogsEntryModels as blogsEntryModel>
	${dataFactory.toInsertSQL(blogsEntryModel)}

	<#assign friendlyURLEntryModel = dataFactory.newFriendlyURLEntryModel(blogsEntryModel, blogsEntryClassNameId) />

	${dataFactory.toInsertSQL(friendlyURLEntryModel)}

	${dataFactory.toInsertSQL(dataFactory.newFriendlyURLEntryLocalizationModel(friendlyURLEntryModel, blogsEntryModel))}

	${dataFactory.toInsertSQL(dataFactory.newFriendlyURLEntryMapping(friendlyURLEntryModel))}

	${dataFactory.toInsertSQL(dataFactory.newMBDiscussionAssetEntryModel(blogsEntryModel, dataFactory.getClassNameId("com.liferay.message.boards.model.MBDiscussion_com.liferay.blogs.model.BlogsEntry")))}

	<@insertAssetEntry
		_categoryAndTag=true
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

	${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(blogsEntryModel))}

	${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(blogsEntryModel, blogsEntryClassNameId))}

	${csvFileWriter.write("blog", blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbRootMessageId + "\n")}
</#list>