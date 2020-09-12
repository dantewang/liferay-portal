<#assign
	blogsEntryClassNameId = classNameDataFactory.getClassNameId("BlogsEntry")
	blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId)
/>

<#list blogsEntryModels as blogsEntryModel>
	${insertSQLBuilder.toInsertSQL(blogsEntryModel)}

	<#assign friendlyURLEntryModel = blogDataFactory.newFriendlyURLEntryModel(blogsEntryModel, blogsEntryClassNameId) />

	${insertSQLBuilder.toInsertSQL(friendlyURLEntryModel)}

	${insertSQLBuilder.toInsertSQL(blogDataFactory.newFriendlyURLEntryLocalizationModel(friendlyURLEntryModel, blogsEntryModel))}

	${insertSQLBuilder.toInsertSQL(blogDataFactory.newFriendlyURLEntryMapping(friendlyURLEntryModel))}

	${insertSQLBuilder.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(blogsEntryModel, classNameDataFactory.getClassNameId("MBDiscussion_BlogsEntry")))}

	<@insertAssetEntry
		_categoryAndTag=true
		_classNameIds=[blogsEntryClassNameId]
		_entry=blogsEntryModel
	/>

	<#assign mbRootMessageId = counterDataFactory.getCounterNext() />

	<@insertMBDiscussion
		_classNameId=blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=blogDataFactory.maxBlogsEntryCommentCount
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=counterDataFactory.getCounterNext()
	/>

	${insertSQLBuilder.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(blogsEntryModel, blogsEntryClassNameId))}

	${insertSQLBuilder.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(blogsEntryModel, blogsEntryClassNameId))}

	${csvFileWriter.write("blog", blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbRootMessageId + "\n")}
</#list>