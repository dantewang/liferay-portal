<#assign blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId) />

${insertSQLBuilder.toInsertSQL(blogDataFactory.newUserNotificationDeliveryModel("com_liferay_comment_web_portlet_CommentPortlet"))}

<#list blogsEntryModels as blogsEntryModel>
	${insertSQLBuilder.toInsertSQL(blogsEntryModel)}

	<#assign friendlyURLEntryModel = blogDataFactory.newFriendlyURLEntryModel(blogsEntryModel) />

	${insertSQLBuilder.toInsertSQL(friendlyURLEntryModel)}

	${insertSQLBuilder.toInsertSQL(blogDataFactory.newFriendlyURLEntryLocalizationModel(friendlyURLEntryModel, blogsEntryModel))}

	${insertSQLBuilder.toInsertSQL(blogDataFactory.newFriendlyURLEntryMapping(friendlyURLEntryModel))}

	${insertSQLBuilder.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(blogsEntryModel))}

	<@insertAssetEntry
		_categoryAndTag=true
		_entry=blogsEntryModel
	/>

	<#assign mbRootMessageId = counterDataFactory.getCounterNext() />

	<@insertMBDiscussion
		_classNameId=blogDataFactory.blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=blogDataFactory.maxBlogsEntryCommentCount
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=counterDataFactory.getCounterNext()
	/>

	${insertSQLBuilder.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(blogsEntryModel))}

	${insertSQLBuilder.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(blogsEntryModel))}

	${csvFileWriter.write("blog", blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbRootMessageId + "\n")}
</#list>