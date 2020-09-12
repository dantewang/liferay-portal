<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${insertSQLBuilder.toInsertSQL(mbCategoryModel)}
	${insertSQLBuilder.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel, sampleUserModel))}

	${csvFileWriter.write("mbCategory", mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${insertSQLBuilder.toInsertSQL(mbThreadModel)}

		${insertSQLBuilder.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel, classNameDataFactory.getClassNameId("MBThread")))}

		<@insertAssetEntry
			_classNameIds=[classNameDataFactory.getClassNameId("MBThread")]
			_entry=mbThreadModel
		/>

		${insertSQLBuilder.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${insertSQLBuilder.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel, classNameDataFactory.getClassNameId("WikiPage"), classNameDataFactory.getClassNameId("MBMessage")))}
		</#list>

		${csvFileWriter.write("mbThread", mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>