<#assign blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId) />

${resourcePermissionDataFactory.toInsertSQL(blogDataFactory.newUserNotificationDeliveryModel("com_liferay_comment_web_portlet_CommentPortlet"))}

<#list blogsEntryModels as blogsEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(blogsEntryModel)}

	<#assign friendlyURLEntryModel = blogDataFactory.newFriendlyURLEntryModel(blogsEntryModel) />

	${resourcePermissionDataFactory.toInsertSQL(friendlyURLEntryModel)}

	${resourcePermissionDataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryLocalizationModel(friendlyURLEntryModel, blogsEntryModel))}

	${resourcePermissionDataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryMapping(friendlyURLEntryModel))}

	${resourcePermissionDataFactory.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(blogsEntryModel))}

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

	${resourcePermissionDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(blogsEntryModel))}

	${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(blogsEntryModel))}

	${csvFileWriter.write("blog", blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbRootMessageId + "\n")}
</#list>