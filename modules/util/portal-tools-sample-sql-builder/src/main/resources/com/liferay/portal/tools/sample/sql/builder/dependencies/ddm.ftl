<#assign
	journalArticleClassNameId = classNameDataFactory.getClassNameId("JournalArticle")
	ddmStructureClassNameId = classNameDataFactory.getClassNameId("DDMStructure")
	defaultJournalDDMStructureModel = dataFactory.newDefaultJournalDDMStructureModel(journalArticleClassNameId)
/>

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.newDefaultJournalDDMStructureLayoutModel()
	_ddmStructureModel=defaultJournalDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultJournalDDMStructureVersionModel(defaultJournalDDMStructureModel)
/>

<#assign defaultJournalDDMTemplateModel = dataFactory.newDefaultJournalDDMTemplateModel(journalArticleClassNameId, ddmStructureClassNameId) />

${dataFactory.toInsertSQL(defaultJournalDDMTemplateModel, classNameDataFactory.getClassName(defaultJournalDDMTemplateModel))}

${dataFactory.toInsertSQL(dataFactory.newDefaultJournalDDMTemplateVersionModel(ddmStructureClassNameId))}