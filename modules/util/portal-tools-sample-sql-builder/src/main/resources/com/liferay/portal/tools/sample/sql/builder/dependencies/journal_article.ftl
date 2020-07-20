<#assign ddmStructureModel = journalDataFactory.newDefaultJournalDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureLayoutModel=journalDataFactory.newDefaultJournalDDMStructureLayoutModel()
	_ddmStructureModel=ddmStructureModel
	_ddmStructureVersionModel=journalDataFactory.newDefaultJournalDDMStructureVersionModel(ddmStructureModel)
/>

<#assign ddmTemplateModel = journalDataFactory.newDefaultJournalDDMTemplateModel() />

${dataFactory.toInsertSQL(ddmTemplateModel)}

${dataFactory.toInsertSQL(journalDataFactory.newDefaultJournalDDMTemplateVersionModel())}

<#assign
	journalArticlePageCounts = dataFactory.getSequence(journalDataFactory.maxJournalArticlePageCount)

	resourcePermissionModels = dataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
/>

<#list resourcePermissionModels as resourcePermissionModel>
	${dataFactory.toInsertSQL(resourcePermissionModel)}
</#list>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = dataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", journalDataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${csvFileWriter.write("layout", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = dataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	<#assign journalArticleCounts = dataFactory.getSequence(journalDataFactory.maxJournalArticleCount) />

	<#list journalArticleCounts as journalArticleCount>
		<#assign journalArticleResourceModel = journalDataFactory.newJournalArticleResourceModel(groupId) />

		${dataFactory.toInsertSQL(journalArticleResourceModel)}

		<#assign versionCounts = dataFactory.getSequence(journalDataFactory.maxJournalArticleVersionCount) />

		<#list versionCounts as versionCount>
			<#assign journalArticleModel = journalDataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

			${dataFactory.toInsertSQL(journalArticleModel)}

			<#assign journalArticleLocalizationModel = journalDataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

			${dataFactory.toInsertSQL(journalArticleLocalizationModel)}

			${dataFactory.toInsertSQL(journalDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${dataFactory.toInsertSQL(journalDataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

			${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(journalArticleModel))}

			<#if versionCount = journalDataFactory.maxJournalArticleVersionCount>
				<@insertAssetEntry
					_categoryAndTag=true
					_entry=journalDataFactory.newObjectValuePair(journalArticleModel, journalArticleLocalizationModel)
				/>
			</#if>
		</#list>

		<@insertMBDiscussion
			_classNameId=journalDataFactory.journalArticleClassNameId
			_classPK=journalArticleResourceModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=0
			_mbRootMessageId=dataFactory.getCounterNext()
			_mbThreadId=dataFactory.getCounterNext()
		/>

		${dataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount, journalArticleResourceModel))}

		${dataFactory.toInsertSQL(journalDataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.layoutId))}
	</#list>
</#list>