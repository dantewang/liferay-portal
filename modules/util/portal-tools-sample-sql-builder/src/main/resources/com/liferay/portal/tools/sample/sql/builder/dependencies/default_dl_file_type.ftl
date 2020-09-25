${dataFactory.toInsertSQL(dataFactory.newDLFileEntryTypeModel())}

<#assign defaultDLDDMStructureModel = dataFactory.newDefaultDLDDMStructureModel(classNameDataFactory.getClassNameId("DLFileEntry")) />

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.newDefaultDLDDMStructureLayoutModel()
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>