<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${resourcePermissionDataFactory.toInsertSQL(mbCategoryModel)}
	${resourcePermissionDataFactory.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel, sampleUserModel))}

	${csvFileWriter.write("mbCategory", mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${resourcePermissionDataFactory.toInsertSQL(mbThreadModel)}

		${resourcePermissionDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel, classNameDataFactory.getClassNameId("MBThread")))}

		<@insertAssetEntry
			_classNameIds=[classNameDataFactory.getClassNameId("MBThread")]
			_entry=mbThreadModel
		/>

		${resourcePermissionDataFactory.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel, classNameDataFactory.getClassNameId("WikiPage"), classNameDataFactory.getClassNameId("MBMessage")))}
		</#list>

		${csvFileWriter.write("mbThread", mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>