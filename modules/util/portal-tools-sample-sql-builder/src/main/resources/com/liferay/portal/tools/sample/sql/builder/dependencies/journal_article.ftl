<#assign ddmStructureModel = journalDataFactory.newDefaultJournalDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureLayoutModel=journalDataFactory.newDefaultJournalDDMStructureLayoutModel()
	_ddmStructureModel=ddmStructureModel
	_ddmStructureVersionModel=journalDataFactory.newDefaultJournalDDMStructureVersionModel(ddmStructureModel)
/>

<#assign ddmTemplateModel = journalDataFactory.newDefaultJournalDDMTemplateModel() />

${insertSQLBuilder.toInsertSQL(ddmTemplateModel)}

${insertSQLBuilder.toInsertSQL(journalDataFactory.newDefaultJournalDDMTemplateVersionModel())}

<#assign
	journalArticlePageCounts = counterDataFactory.getSequence(journalDataFactory.maxJournalArticlePageCount)

	resourcePermissionModels = journalDataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
/>

<#list resourcePermissionModels as resourcePermissionModel>
	${insertSQLBuilder.toInsertSQL(resourcePermissionModel)}
</#list>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", journalDataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${csvFileWriter.write("layout", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${insertSQLBuilder.toInsertSQL(portletPreferencesModel)}
	</#list>

	<#assign journalArticleCounts = counterDataFactory.getSequence(journalDataFactory.maxJournalArticleCount) />

	<#list journalArticleCounts as journalArticleCount>
		<#assign journalArticleResourceModel = journalDataFactory.newJournalArticleResourceModel(groupId) />

		${insertSQLBuilder.toInsertSQL(journalArticleResourceModel)}

		<#assign versionCounts = counterDataFactory.getSequence(journalDataFactory.maxJournalArticleVersionCount) />

		<#list versionCounts as versionCount>
			<#assign journalArticleModel = journalDataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

			${insertSQLBuilder.toInsertSQL(journalArticleModel)}

			<#assign journalArticleLocalizationModel = journalDataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

			${insertSQLBuilder.toInsertSQL(journalArticleLocalizationModel)}

			${insertSQLBuilder.toInsertSQL(journalDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${insertSQLBuilder.toInsertSQL(journalDataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

			${insertSQLBuilder.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(journalArticleModel))}

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

		${insertSQLBuilder.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount, journalArticleResourceModel))}

		${insertSQLBuilder.toInsertSQL(journalDataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.layoutId))}
	</#list>
</#list>