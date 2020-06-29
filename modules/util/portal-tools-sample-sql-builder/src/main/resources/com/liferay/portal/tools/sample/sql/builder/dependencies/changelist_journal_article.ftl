<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

${dataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${dataFactory.toInsertSQL(cTCollectionModel)}

	<#assign
		journalFolderModels = cTDataFactory.newJournalFolderModels(cTCollectionModel, groupId)
		folderAsstEntryModels = cTDataFactory.newAssetEntryModels(journalFolderModels)
		journalArticleResourceModels = cTDataFactory.newJournalArticleResourceModels(groupId, journalFolderModels)
		journalArticleModels = cTDataFactory.newJournalArticleModels(journalArticleResourceModels, journalFolderModels)
		journalArticleLocalizationModels = cTDataFactory.newJournalArticleLocalizationModels(journalArticleModels, journalFolderModels)
		dDMTemplateLinkModels = cTDataFactory.newDDMTemplateLinkModels(journalArticleModels, ddmTemplateModel.templateId)
		dDMStorageLinkModels = cTDataFactory.newDDMStorageLinkModels(journalArticleModels, ddmStructureModel.structureId)
		socialActivityModels = cTDataFactory.newSocialActivityModels(journalArticleModels)
		articleAssetEntryModels = cTDataFactory.newAssetEntryModels(journalArticleModels, journalArticleLocalizationModels)
		layoutModels = cTDataFactory.newLayoutModels(groupId, cTCollectionModel)
		layoutFriendlyURLModels = cTDataFactory.newLayoutFriendlyURLModels(layoutModels)
		layoutAssetEntryModels = cTDataFactory.newLayoutAssetEntryModels(layoutModels)
		journalPagePortletPreferencesModels = cTDataFactory.newJournalPortletPreferencesModels(layoutModels)
		journalArticlePortletPreferencesModels = cTDataFactory.newJournalArticlePortletPreferencesModels(layoutModels, journalArticleResourceModels)
		journalContentSearchModels = cTDataFactory.newJournalContentSearchModels(journalArticleModels, layoutModels)
		cTEntryModels = cTDataFactory.newCTEntryModels(cTCollectionModel)
	/>

	<#list journalFolderModels as journalFolderModel>
		${dataFactory.toInsertSQL(journalFolderModel)}
	</#list>

	<#list folderAsstEntryModels as folderAsstEntryModel>
		${dataFactory.toInsertSQL(folderAsstEntryModel)}
	</#list>

	<#list journalArticleResourceModels as journalArticleResourceModel>
		${dataFactory.toInsertSQL(journalArticleResourceModel)}
	</#list>

	<#list journalArticleModels as journalArticleModel>
		${dataFactory.toInsertSQL(journalArticleModel)}
	</#list>

	<#list journalArticleLocalizationModels as journalArticleLocalizationModel>
		${dataFactory.toInsertSQL(journalArticleLocalizationModel)}
	</#list>

	<#list dDMTemplateLinkModels as dDMTemplateLinkModel>
		${dataFactory.toInsertSQL(dDMTemplateLinkModel)}
	</#list>

	<#list dDMStorageLinkModels as dDMStorageLinkModel>
		${dataFactory.toInsertSQL(dDMStorageLinkModel)}
	</#list>

	<#list socialActivityModels as socialActivityModel>
		${dataFactory.toInsertSQL(socialActivityModel)}
	</#list>

	<#list articleAssetEntryModels as articleAssetEntryModel>
		${dataFactory.toInsertSQL(articleAssetEntryModel)}
	</#list>

	<#list layoutModels as layoutModel>
		${dataFactory.toInsertSQL(layoutModel)}
	</#list>

	<#list layoutFriendlyURLModels as layoutFriendlyURLModel>
		${dataFactory.toInsertSQL(layoutFriendlyURLModel)}
	</#list>

	<#list layoutAssetEntryModels as layoutAssetEntryModel>
		${dataFactory.toInsertSQL(layoutAssetEntryModel)}
	</#list>

	<#list journalPagePortletPreferencesModels as journalPagePortletPreferencesModel>
		${dataFactory.toInsertSQL(journalPagePortletPreferencesModel)}
	</#list>

	<#list journalArticlePortletPreferencesModels as journalArticlePortletPreferencesModel>
		${dataFactory.toInsertSQL(journalArticlePortletPreferencesModel)}
	</#list>

	<#list journalContentSearchModels as journalContentSearchModel>
		${dataFactory.toInsertSQL(journalContentSearchModel)}
	</#list>

	<#list cTEntryModels as cTEntryModel>
		${dataFactory.toInsertSQL(cTEntryModel)}
	</#list>
</#list>