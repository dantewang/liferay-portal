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