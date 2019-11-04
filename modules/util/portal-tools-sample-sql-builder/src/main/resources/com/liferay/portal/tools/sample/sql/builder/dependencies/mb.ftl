<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${resourcePermissionDataFactory.toInsertSQL(mbCategoryModel)}
	${resourcePermissionDataFactory.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel))}

	${dataFactoryContext.getCSVWriter("mbCategory").write(mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${resourcePermissionDataFactory.toInsertSQL(mbThreadModel)}

		${resourcePermissionDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel))}

		<@insertAssetEntry _entry=mbThreadModel />

		${resourcePermissionDataFactory.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}
		</#list>

		${dataFactoryContext.getCSVWriter("mbThread").write(mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>