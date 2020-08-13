${insertSQLBuilder.toInsertSQL(dlDataFactory.newDLFileEntryTypeModel())}

<#assign defaultDLDDMStructureModel = dlDataFactory.newDefaultDLDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureLayoutModel=dlDataFactory.newDefaultDLDDMStructureLayoutModel()
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dlDataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>