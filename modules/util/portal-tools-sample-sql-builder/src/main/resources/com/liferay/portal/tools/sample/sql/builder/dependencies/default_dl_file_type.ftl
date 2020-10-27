${dataFactory.toInsertSQL(dataFactory.newDLFileEntryTypeModel())}

<#assign defaultDLDDMStructureModel = dataFactory.newDefaultDLDDMStructureModel(dataFactory.getClassNameId("DLFileEntry")) />

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.newDefaultDLDDMStructureLayoutModel()
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>