<#assign maxContentPageCount = dataFactory.maxContentPageCount />
<#if maxContentPageCount gt 0>
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

	<#assign contentPageCounts = dataFactory.getSequence(dataFactory.maxContentPageCount) />

	<#list contentPageCounts as contentPageCount>
		<#assign contentLayoutModel = dataFactory.newContentLayoutModel(groupId, groupId + "_content_page_" + contentPageCount, "web_content") />

		${dataFactory.getCSVWriter("layout").write(contentLayoutModel.friendlyURL + "\n")}

		<#assign contentPageJournalArticleCounts = dataFactory.getSequence(dataFactory.maxContentPageJournalArticleCount) />

		<#list contentPageJournalArticleCounts as contentPagejournalArticleCount>
			<#assign journalArticleResourceModel = dataFactory.newJournalArticleResourceModel(groupId) />

			${dataFactory.toInsertSQL(journalArticleResourceModel)}

			<#assign versionCounts = dataFactory.getSequence(dataFactory.maxJournalArticleVersionCount) />

			<#list versionCounts as versionCount>
				<#assign journalArticleModel = dataFactory.newJournalArticleModel(journalArticleResourceModel, contentPagejournalArticleCount, versionCount) />

				${dataFactory.toInsertSQL(journalArticleModel)}

				<#assign journalArticleLocalizationModel = dataFactory.newJournalArticleLocalizationModel(journalArticleModel, contentPagejournalArticleCount, versionCount) />

				${dataFactory.toInsertSQL(journalArticleLocalizationModel)}

				<#assign ddmTemplateModel = dataFactory.defaultJournalDDMTemplateModel />

				${dataFactory.toInsertSQL(dataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

				<#assign ddmStructureModel = dataFactory.defaultJournalDDMStructureModel />

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