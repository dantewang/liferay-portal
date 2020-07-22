<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

${resourcePermissionDataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${resourcePermissionDataFactory.toInsertSQL(cTCollectionModel)}

	<#assign
			journalFolderModels = journalDataFactory.newJournalFolderModels(cTCollectionModel, groupId)
			folderAsstEntryModels = assetDataFactory.newAssetEntryModels(journalFolderModels)
			journalArticleResourceModels = journalDataFactory.newJournalArticleResourceModels(groupId, journalFolderModels)
			journalArticleModels = journalDataFactory.newJournalArticleModels(journalArticleResourceModels, journalFolderModels)
			journalArticleLocalizationModels = journalDataFactory.newJournalArticleLocalizationModels(journalArticleModels, journalFolderModels)
			dDMTemplateLinkModels = journalDataFactory.newDDMTemplateLinkModels(journalArticleModels, ddmTemplateModel.templateId)
			dDMStorageLinkModels = journalDataFactory.newDDMStorageLinkModels(journalArticleModels, ddmStructureModel.structureId)
			socialActivityModels = socialActivityDataFactory.newSocialActivityModels(journalArticleModels)
			articleAssetEntryModels = assetDataFactory.newAssetEntryModels(journalArticleModels, journalArticleLocalizationModels)
			layoutModels = layoutDataFactory.newLayoutModels(groupId, cTCollectionModel)
			layoutFriendlyURLModels = layoutDataFactory.newLayoutFriendlyURLModels(layoutModels)
			layoutAssetEntryModels = assetDataFactory.newLayoutAssetEntryModels(layoutModels)
			journalPagePortletPreferencesModels = portletPreferenceDataFactory.newJournalPortletPreferencesModels(layoutModels)
			journalArticlePortletPreferencesModels = portletPreferenceDataFactory.newJournalArticlePortletPreferencesModels(layoutModels, journalArticleResourceModels)
			journalContentSearchModels = journalDataFactory.newJournalContentSearchModels(journalArticleModels, layoutModels)
			cTEntryModels = cTDataFactory.newCTEntryModels(cTCollectionModel)
		/>

		<#list journalFolderModels as journalFolderModel>
			${resourcePermissionDataFactory.toInsertSQL(journalFolderModel)}
		</#list>

		<#list folderAsstEntryModels as folderAsstEntryModel>
			${resourcePermissionDataFactory.toInsertSQL(folderAsstEntryModel)}
		</#list>

		<#list journalArticleResourceModels as journalArticleResourceModel>
			${resourcePermissionDataFactory.toInsertSQL(journalArticleResourceModel)}
		</#list>

		<#list journalArticleModels as journalArticleModel>
			${resourcePermissionDataFactory.toInsertSQL(journalArticleModel)}
		</#list>

		<#list journalArticleLocalizationModels as journalArticleLocalizationModel>
			${resourcePermissionDataFactory.toInsertSQL(journalArticleLocalizationModel)}
		</#list>

		<#list dDMTemplateLinkModels as dDMTemplateLinkModel>
			${resourcePermissionDataFactory.toInsertSQL(dDMTemplateLinkModel)}
		</#list>

		<#list dDMStorageLinkModels as dDMStorageLinkModel>
			${resourcePermissionDataFactory.toInsertSQL(dDMStorageLinkModel)}
		</#list>

		<#list socialActivityModels as socialActivityModel>
			${resourcePermissionDataFactory.toInsertSQL(socialActivityModel)}
		</#list>

		<#list articleAssetEntryModels as articleAssetEntryModel>
			${resourcePermissionDataFactory.toInsertSQL(articleAssetEntryModel)}
		</#list>

		<#list layoutModels as layoutModel>
			${resourcePermissionDataFactory.toInsertSQL(layoutModel)}
			${csvFileWriter.write("cTLayout", userModel.screenName + "," + layoutModel.friendlyURL + "," + layoutModel.ctCollectionId + "\n")}
		</#list>

		<#list layoutFriendlyURLModels as layoutFriendlyURLModel>
			${resourcePermissionDataFactory.toInsertSQL(layoutFriendlyURLModel)}
		</#list>

		<#list layoutAssetEntryModels as layoutAssetEntryModel>
			${resourcePermissionDataFactory.toInsertSQL(layoutAssetEntryModel)}
		</#list>

		<#list journalPagePortletPreferencesModels as journalPagePortletPreferencesModel>
			${resourcePermissionDataFactory.toInsertSQL(journalPagePortletPreferencesModel)}
		</#list>

		<#list journalArticlePortletPreferencesModels as journalArticlePortletPreferencesModel>
			${resourcePermissionDataFactory.toInsertSQL(journalArticlePortletPreferencesModel)}
		</#list>

		<#list journalContentSearchModels as journalContentSearchModel>
			${resourcePermissionDataFactory.toInsertSQL(journalContentSearchModel)}
		</#list>

		<#list cTEntryModels as cTEntryModel>
			${resourcePermissionDataFactory.toInsertSQL(cTEntryModel)}
		</#list>
</#list>