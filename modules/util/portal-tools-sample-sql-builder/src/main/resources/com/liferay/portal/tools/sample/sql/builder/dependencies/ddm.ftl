<#assign
	journalArticleClassNameId = dataFactory.getClassNameId("com.liferay.journal.model.JournalArticle")
	ddmStructureClassNameId = dataFactory.getClassNameId("com.liferay.dynamic.data.mapping.model.DDMStructure")
	defaultJournalDDMStructureModel = dataFactory.newDefaultJournalDDMStructureModel(journalArticleClassNameId)
/>

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.newDefaultJournalDDMStructureLayoutModel()
	_ddmStructureModel=defaultJournalDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultJournalDDMStructureVersionModel(defaultJournalDDMStructureModel)
/>

<#assign defaultJournalDDMTemplateModel = dataFactory.newDefaultJournalDDMTemplateModel(journalArticleClassNameId, ddmStructureClassNameId) />

${dataFactory.toInsertSQL(defaultJournalDDMTemplateModel)}

${dataFactory.toInsertSQL(dataFactory.newDefaultJournalDDMTemplateVersionModel(ddmStructureClassNameId))}