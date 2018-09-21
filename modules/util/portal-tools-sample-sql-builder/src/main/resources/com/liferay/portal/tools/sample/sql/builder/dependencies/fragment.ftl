<#assign contentPageEnable = dataFactory.contentPageEnable />
<#if contentPageEnable== "true">

	<#assign controlPanelLayoutModel = dataFactory.newControlPanelLayoutModel() />

	${dataFactory.toInsertSQL(controlPanelLayoutModel)}

	<@insertGroup
		_groupModel=dataFactory.controlPanelGroupModel
		_privatePageCount=1
		_publicPageCount=0
	/>

	${dataFactory.toInsertSQL(dataFactory.newLayoutFriendlyURLModel(controlPanelLayoutModel))}

	<#assign fragmentCollectionModel = dataFactory.newFragmentCollectionModel(groupId) />

	${dataFactory.toInsertSQL(fragmentCollectionModel)}

	<#assign fragmentEntryModels = dataFactory.newFragmentEntryModels(groupId, fragmentCollectionModel) />

	<#list fragmentEntryModels?keys as fragmentEntryModelName>
		${dataFactory.toInsertSQL(fragmentEntryModels["${fragmentEntryModelName}"])}
	</#list>

	<#assign portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_test" />

	${dataFactory.toInsertSQL(dataFactory.newJournalContentPortletPreferencesModel(controlPanelLayoutModel, portletIdPrefix))}

	<#assign ddmStructureModel = dataFactory.defaultJournalDDMStructureModel />

	<@insertDDMStructure
		_ddmStructureLayoutModel=dataFactory.defaultJournalDDMStructureLayoutModel
		_ddmStructureModel=ddmStructureModel
		_ddmStructureVersionModel=dataFactory.defaultJournalDDMStructureVersionModel
	/>

	<#assign ddmTemplateModel = dataFactory.defaultJournalDDMTemplateModel />

	${dataFactory.toInsertSQL(ddmTemplateModel)}

	<#assign ddmTemplateVersionModel = dataFactory.defaultJournalDDMTemplateVersionModel />

	${dataFactory.toInsertSQL(ddmTemplateVersionModel)}

	<#assign
		journalArticlePageCounts = dataFactory.getSequence(dataFactory.maxJournalArticlePageCount)

		resourcePermissionModels = dataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
	/>

	<#list resourcePermissionModels as resourcePermissionModel>
		${dataFactory.toInsertSQL(resourcePermissionModel)}
	</#list>

	<#list journalArticlePageCounts as journalArticlePageCount>
		<#assign contentLayoutModel = dataFactory.newContentLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "web_content") />

		${dataFactory.getCSVWriter("layout").write(contentLayoutModel.friendlyURL + "\n")}

		<#assign journalArticleCounts = dataFactory.getSequence(dataFactory.maxJournalArticleCount) />

		<#list journalArticleCounts as journalArticleCount>
			<#assign journalArticleResourceModel = dataFactory.newJournalArticleResourceModel(groupId) />

			${dataFactory.toInsertSQL(journalArticleResourceModel)}

			<#assign versionCounts = dataFactory.getSequence(dataFactory.maxJournalArticleVersionCount) />

			<#list versionCounts as versionCount>
				<#assign journalArticleModel = dataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

				${dataFactory.toInsertSQL(journalArticleModel)}

				<#assign journalArticleLocalizationModel = dataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

				${dataFactory.toInsertSQL(journalArticleLocalizationModel)}

				${dataFactory.toInsertSQL(dataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

				${dataFactory.toInsertSQL(dataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

				${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(journalArticleModel))}

				<#if versionCount = dataFactory.maxJournalArticleVersionCount>
					<@insertAssetEntry
						_categoryAndTag=true
						_entry=dataFactory.newObjectValuePair(journalArticleModel, journalArticleLocalizationModel)
					/>
				</#if>
			</#list>

			<@insertMBDiscussion
				_classNameId=dataFactory.journalArticleClassNameId
				_classPK=journalArticleResourceModel.resourcePrimKey
				_groupId=groupId
				_maxCommentCount=0
				_mbRootMessageId=dataFactory.getCounterNext()
				_mbThreadId=dataFactory.getCounterNext()
			/>

			${dataFactory.toInsertSQL(contentLayoutModel)}

			${dataFactory.toInsertSQL(dataFactory.newLayoutFriendlyURLModel(contentLayoutModel))}

			<#assign fragmentEntryLinkModels = dataFactory.newFragmentEntryLinkModels(contentLayoutModel, fragmentEntryModels) />

			<#list fragmentEntryLinkModels as fragmentEntryLinkModel>
				${dataFactory.toInsertSQL(fragmentEntryLinkModel)}

				<#assign layoutPageTemplateStructureModel = dataFactory.newLayoutPageTemplateStructureModel(contentLayoutModel, fragmentEntryLinkModel) />

				${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

				<#if fragmentEntryLinkModel.getHtml()?contains("lfr-widget-web-content")>
					${dataFactory.toInsertSQL(dataFactory.newJournalContentPortletPreferencesModel(contentLayoutModel, fragmentEntryLinkModel, journalArticleResourceModel))}
					${dataFactory.toInsertSQL(dataFactory.newJournalContentPortletPreferencesModel(controlPanelLayoutModel, fragmentEntryLinkModel, journalArticleResourceModel))}
					${dataFactory.toInsertSQL(dataFactory.newJournalContentSearchModel(controlPanelLayoutModel, journalArticleModel, fragmentEntryLinkModel))}
				</#if>
			</#list>
		</#list>
	</#list>
</#if>