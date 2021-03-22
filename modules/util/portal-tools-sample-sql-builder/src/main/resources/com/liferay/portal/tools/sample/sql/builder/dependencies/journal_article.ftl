<#list dataFactory.newResourcePermissionModels("com.liferay.journal", groupId) as resourcePermissionModel>
	${dataFactory.toInsertSQL(resourcePermissionModel)}
</#list>

<#list dataFactory.getSequence(dataFactory.maxJournalArticlePageCount) as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = dataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", dataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${csvFileWriter.write("layout", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = dataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	<#list dataFactory.getSequence(dataFactory.maxJournalArticleCount) as journalArticleCount>
		<@insertJournalArticle
			_journalArticleIndex=journalArticleCount
			_journalDDMStructureModel=defaultJournalDDMStructureModel
			_journalDDMTemplateModel=defaultJournalDDMTemplateModel
			_maxJournalArticleVersionCount=dataFactory.maxJournalArticleVersionCount
		/>

		<@insertMBDiscussion
			_classNameId=dataFactory.journalArticleClassNameId
			_classPK=journalArticleResourceModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=0
			_mbRootMessageId=dataFactory.getCounterNext()
			_mbThreadId=dataFactory.getCounterNext()
		/>

		<#assign journalArticleResourcePortletPreferencesModel = dataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount) />

		${dataFactory.toInsertSQL(journalArticleResourcePortletPreferencesModel)}

		<#assign journalArticleResourcePortletPreferenceValueModels = dataFactory.newJournalArticleResourcePortletPreferenceValueModels(journalArticleResourcePortletPreferencesModel, journalArticleResourceModel) />

		<#list journalArticleResourcePortletPreferenceValueModels as journalArticleResourcePortletPreferenceValueModel>
			${dataFactory.toInsertSQL(journalArticleResourcePortletPreferenceValueModel)}
		</#list>

		${dataFactory.toInsertSQL(dataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.layoutId))}
	</#list>
</#list>