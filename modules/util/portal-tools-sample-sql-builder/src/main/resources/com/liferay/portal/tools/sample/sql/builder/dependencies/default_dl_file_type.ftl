<#assign dLFileEntryTypeModel = dataFactory.newDLFileEntryTypeModel() />

${dataFactory.toInsertSQL(dLFileEntryTypeModel)}

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.defaultDLDDMStructureLayoutModel
	_ddmStructureModel=dataFactory.defaultDLDDMStructureModel
	_ddmStructureVersionModel=dataFactory.defaultDLDDMStructureVersionModel
/>