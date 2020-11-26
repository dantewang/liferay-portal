<#assign journalArticleContentPageCounts = dataFactory.getSequence(dataFactory.maxContentLayoutCount) />

<#list journalArticleContentPageCounts as journalArticleContentPageCount>
	<#assign
		journalArticleContentLayoutModels = dataFactory.newContentPageLayoutModels(groupId, journalArticleContentPageCount + "_web_content")
		journalArticleFragmentEntryLinkModels = dataFactory.newFragmentEntryLinkModels(journalArticleContentLayoutModels, "", "", "", "", "journal_editValue.json")
	/>

	<@insertContentPageLayout
		_fragmentEntryLinkModels=journalArticleFragmentEntryLinkModels
		_layoutModels=journalArticleContentLayoutModels
		_templateFileName="journal_contentpage_layout_definition.json"
	/>

	<#list dataFactory.newJournalContentPortletPreferencesModels(journalArticleFragmentEntryLinkModels) as journalContentPortletPreferencesModel>
		${dataFactory.toInsertSQL(journalContentPortletPreferencesModel)}
	</#list>

	<#list journalArticleContentLayoutModels as journalArticleContentLayoutModel>
		<#if journalArticleContentLayoutModel.classNameId = 0>
			${csvFileWriter.write("fragment", journalArticleContentLayoutModel.friendlyURL + "\n")}
		</#if>
	</#list>
</#list>