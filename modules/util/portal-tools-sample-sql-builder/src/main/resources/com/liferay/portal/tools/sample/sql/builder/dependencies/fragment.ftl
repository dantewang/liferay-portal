<#if (dataFactory.maxContentLayoutCount > 0)>
	<@insertJournalArticle
		_journalArticleIndex=1
		_journalDDMStructureModel=defaultJournalDDMStructureModel
		_journalDDMTemplateModel=defaultJournalDDMTemplateModel
		_maxJournalArticleVersionCount=1
	/>

	<#assign fragmentCollectionModel = dataFactory.newFragmentCollectionModel(groupId) />

	${dataFactory.toInsertSQL(fragmentCollectionModel)}

	<#assign fragmentEntryModel = dataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

	${dataFactory.toInsertSQL(fragmentEntryModel)}

	<#list dataFactory.newContentLayoutModels(groupId) as contentLayoutModel>
		<@insertContentLayout
			_fragmentEntryModel=fragmentEntryModel
			_journalArticleModel=journalArticleModel
			_layoutModel=contentLayoutModel
		/>

		${csvFileWriter.write("fragment", contentLayoutModel.friendlyURL + "\n")}
	</#list>
</#if>