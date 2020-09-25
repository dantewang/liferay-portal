${dataFactory.toInsertSQL(dlDataFactory.newDLFileEntryTypeModel())}

<#assign defaultDLDDMStructureModel = dlDataFactory.newDefaultDLDDMStructureModel(classNameDataFactory.getClassNameId("DLFileEntry")) />

<@insertDDMStructure
	_ddmStructureLayoutModel=dlDataFactory.newDefaultDLDDMStructureLayoutModel()
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dlDataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>