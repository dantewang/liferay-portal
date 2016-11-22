<#assign dlFileEntryTypeModel = dLDataFactory.defaultDLFileEntryTypeModel />

insert into DLFileEntryType values ('${dlFileEntryTypeModel.uuid}', ${dlFileEntryTypeModel.fileEntryTypeId}, ${dlFileEntryTypeModel.groupId}, ${dlFileEntryTypeModel.companyId}, ${dlFileEntryTypeModel.userId}, '${dlFileEntryTypeModel.userName}', '${dataFactory.getDateString(dlFileEntryTypeModel.createDate)}', '${dataFactory.getDateString(dlFileEntryTypeModel.modifiedDate)}', '${dlFileEntryTypeModel.fileEntryTypeKey}', '${dlFileEntryTypeModel.name}', '${dlFileEntryTypeModel.description}', '${dataFactory.getDateString(dlFileEntryTypeModel.lastPublishDate)}');

<@insertDDMStructure
	_ddmStructureModel = dLDataFactory.defaultDLDDMStructureModel
	_ddmStructureLayoutModel = dLDataFactory.defaultDLDDMStructureLayoutModel
	_ddmStructureVersionModel = dLDataFactory.defaultDLDDMStructureVersionModel
/>