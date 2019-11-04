<#assign
	blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId)

	userNotificationDeliveryModel = blogDataFactory.newUserNotificationDeliveryModel("com_liferay_comment_web_portlet_CommentPortlet")
/>

${resourcePermissionDataFactory.toInsertSQL(userNotificationDeliveryModel)}

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

	<#assign
		mbThreadId = counterDataFactory.getCounterNext()
		mbRootMessageId = counterDataFactory.getCounterNext()
	/>

	<@insertMBDiscussion
		_classNameId=blogDataFactory.blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=dataFactoryContext.maxBlogsEntryCommentCount
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=mbThreadId
	/>

	${resourcePermissionDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(blogsEntryModel))}

	${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(blogsEntryModel))}

	${dataFactoryContext.getCSVWriter("blog").write(blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbThreadId + "," + mbRootMessageId + "\n")}
</#list>