<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

${resourcePermissionDataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${resourcePermissionDataFactory.toInsertSQL(cTCollectionModel)}

	<#assign
			journalFolderModels = journalDataFactory.newJournalFolderModels(cTCollectionModel, groupId)
			folderAsstEntryModels = assetDataFactory.newAssetEntryModels(journalFolderModels)
		/>

		<#list journalFolderModels as journalFolderModel>
			${resourcePermissionDataFactory.toInsertSQL(journalFolderModel)}
		</#list>

		<#list folderAsstEntryModels as folderAsstEntryModel>
			${resourcePermissionDataFactory.toInsertSQL(folderAsstEntryModel)}
		</#list>
</#list>