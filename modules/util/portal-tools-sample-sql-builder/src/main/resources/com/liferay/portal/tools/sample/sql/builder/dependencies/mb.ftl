<#assign
	mbCategoryModels = dataFactory.newMBCategoryModels(groupId)
	mbThreadClassNameId = dataFactory.getClassNameId("com.liferay.message.boards.model.MBThread")
/>

<#list mbCategoryModels as mbCategoryModel>
	${dataFactory.toInsertSQL(mbCategoryModel)}
	${dataFactory.toInsertSQL(dataFactory.newMBMailingListModel(mbCategoryModel, sampleUserModel))}

	${csvFileWriter.write("mbCategory", mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#assign mbThreadModels = dataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${dataFactory.toInsertSQL(mbThreadModel)}

		${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(mbThreadModel, mbThreadClassNameId))}

		<@insertAssetEntry
			_assetCategoryModelsMaps=assetCategoryModelsMaps
			_assetTagModelsMaps=assetTagModelsMaps
			_classNameIds=[mbThreadClassNameId]
			_entry=mbThreadModel
		/>

		${dataFactory.toInsertSQL(dataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = dataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage
				_mbMessageAssetCategoryModelsMaps=assetCategoryModelsMaps
				_mbMessageAssetTagModelsMaps=assetTagModelsMaps
				_mbMessageModel=mbMessageModel
			/>

			${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(mbMessageModel, wikiPageClassNameId, dataFactory.getClassNameId("com.liferay.message.boards.model.MBMessage")))}
		</#list>

		${csvFileWriter.write("mbThread", mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>