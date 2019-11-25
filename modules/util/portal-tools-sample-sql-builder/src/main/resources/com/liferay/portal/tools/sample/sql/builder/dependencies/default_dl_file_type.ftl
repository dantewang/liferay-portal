<#assign
	dLFileEntryTypeModel = dataFactory.newDLFileEntryTypeModel()

	defaultDLDDMStructureModel = dataFactory.newDefaultDLDDMStructureModel()

	defaultDLDDMStructureVersionModel = dataFactory.newDDMStructureVersionModel(defaultDLDDMStructureModel)

	defaultDLDDMStructureLayoutModel = dataFactory.newDefaultDLDDMStructureLayoutModel(defaultDLDDMStructureVersionModel)
/>

${dataFactory.toInsertSQL(dLFileEntryTypeModel)}

<@insertDDMStructure
	_ddmStructureLayoutModel=defaultDLDDMStructureLayoutModel
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=defaultDLDDMStructureVersionModel
/>