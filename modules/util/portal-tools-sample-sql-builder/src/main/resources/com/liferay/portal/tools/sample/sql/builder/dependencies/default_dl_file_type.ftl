<#assign
	dLFileEntryTypeModel = dataFactory.newDLFileEntryTypeModel()

	defaultDLDDMStructureModel = dataFactory.newDefaultDLDDMStructureModel()

	defaultDLDDMStructureVersionModel = dataFactory.newDDMStructureVersionModel(defaultDLDDMStructureModel)
/>

${dataFactory.toInsertSQL(dLFileEntryTypeModel)}

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.newDefaultDLDDMStructureLayoutModel(defaultDLDDMStructureVersionModel)
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=defaultDLDDMStructureVersionModel
/>