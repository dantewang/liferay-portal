${resourcePermissionDataFactory.toInsertSQL(dLDataFactory.newDLFileEntryTypeModel())}

<#assign defaultDLDDMStructureModel = dLDataFactory.newDefaultDLDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureLayoutModel=dLDataFactory.newDefaultDLDDMStructureLayoutModel()
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dLDataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>