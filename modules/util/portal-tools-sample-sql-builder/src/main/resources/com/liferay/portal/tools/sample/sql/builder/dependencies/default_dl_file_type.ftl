${dataFactory.toInsertSQL(dataFactory.newDLFileEntryTypeModel())}

<#assign defaultDLDDMStructureModel = dataFactory.newDefaultDLDDMStructureModel(dataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntry")) />

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.newDefaultDLDDMStructureLayoutModel()
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>