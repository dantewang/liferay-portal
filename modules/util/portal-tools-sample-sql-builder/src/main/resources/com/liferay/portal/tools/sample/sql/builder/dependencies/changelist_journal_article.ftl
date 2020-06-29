<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

${dataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${dataFactory.toInsertSQL(cTCollectionModel)}

	<#assign
		journalFolderModels = cTDataFactory.newJournalFolderModels(cTCollectionModel, groupId)
		folderAsstEntryModels = cTDataFactory.newAssetEntryModels(journalFolderModels)
	/>

	<#list journalFolderModels as journalFolderModel>
		${dataFactory.toInsertSQL(journalFolderModel)}
	</#list>

	<#list folderAsstEntryModels as folderAsstEntryModel>
		${dataFactory.toInsertSQL(folderAsstEntryModel)}
	</#list>
</#list>