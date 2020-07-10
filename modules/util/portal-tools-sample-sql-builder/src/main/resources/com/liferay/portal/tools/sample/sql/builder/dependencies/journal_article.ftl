<#assign ddmStructureModel = journalDataFactory.newDefaultJournalDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureLayoutModel=journalDataFactory.newDefaultJournalDDMStructureLayoutModel()
	_ddmStructureModel=ddmStructureModel
	_ddmStructureVersionModel=journalDataFactory.newDefaultJournalDDMStructureVersionModel(ddmStructureModel)
/>

<#assign ddmTemplateModel = journalDataFactory.newDefaultJournalDDMTemplateModel() />

${resourcePermissionDataFactory.toInsertSQL(ddmTemplateModel)}

${resourcePermissionDataFactory.toInsertSQL(journalDataFactory.newDefaultJournalDDMTemplateVersionModel())}

<#assign
	journalArticlePageCounts = counterDataFactory.getSequence(journalDataFactory.maxJournalArticlePageCount)

	resourcePermissionModels = resourcePermissionDataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
/>

<#list resourcePermissionModels as resourcePermissionModel>
	${resourcePermissionDataFactory.toInsertSQL(resourcePermissionModel)}
</#list>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", journalDataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${csvFileWriter.write("layout", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = dataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${resourcePermissionDataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	<#assign journalArticleCounts = counterDataFactory.getSequence(journalDataFactory.maxJournalArticleCount) />

	<#list journalArticleCounts as journalArticleCount>
		<#assign journalArticleResourceModel = journalDataFactory.newJournalArticleResourceModel(groupId) />

		${resourcePermissionDataFactory.toInsertSQL(journalArticleResourceModel)}

		<#assign versionCounts = counterDataFactory.getSequence(journalDataFactory.maxJournalArticleVersionCount) />

		<#list versionCounts as versionCount>
			<#assign journalArticleModel = journalDataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

			${resourcePermissionDataFactory.toInsertSQL(journalArticleModel)}

			<#assign journalArticleLocalizationModel = journalDataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

			${resourcePermissionDataFactory.toInsertSQL(journalArticleLocalizationModel)}

			${resourcePermissionDataFactory.toInsertSQL(journalDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${resourcePermissionDataFactory.toInsertSQL(journalDataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

			${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(journalArticleModel))}

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
			_mbRootMessageId=counterDataFactory.getCounterNext()
			_mbThreadId=counterDataFactory.getCounterNext()
		/>

		${resourcePermissionDataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount, journalArticleResourceModel))}

		${resourcePermissionDataFactory.toInsertSQL(journalDataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.layoutId))}
	</#list>
</#list>