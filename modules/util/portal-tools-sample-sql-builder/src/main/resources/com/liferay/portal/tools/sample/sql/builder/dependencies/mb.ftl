<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${dataFactory.toInsertSQL(mbCategoryModel)}
	${dataFactory.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel, sampleUserModel))}

	${csvFileWriter.write("mbCategory", mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${dataFactory.toInsertSQL(mbThreadModel)}

		${dataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel))}

		<@insertAssetEntry _entry=mbThreadModel />

		${dataFactory.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${dataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}
		</#list>

		${csvFileWriter.write("mbThread", mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>