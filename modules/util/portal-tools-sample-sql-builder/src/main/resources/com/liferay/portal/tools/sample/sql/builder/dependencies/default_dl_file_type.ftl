<#assign dlFileEntryTypeModel = dLDataFactory.defaultDLFileEntryTypeModel />

insert into DLFileEntryType values ('${dlFileEntryTypeModel.uuid}', ${dlFileEntryTypeModel.fileEntryTypeId}, ${dlFileEntryTypeModel.groupId}, ${dlFileEntryTypeModel.companyId}, ${dlFileEntryTypeModel.userId}, '${dlFileEntryTypeModel.userName}', '${initContext.getDateString(dlFileEntryTypeModel.createDate)}', '${initContext.getDateString(dlFileEntryTypeModel.modifiedDate)}', '${dlFileEntryTypeModel.fileEntryTypeKey}', '${dlFileEntryTypeModel.name}', '${dlFileEntryTypeModel.description}', '${initContext.getDateString(dlFileEntryTypeModel.lastPublishDate)}');

<@insertDDMStructure
	_ddmStructureModel = dLDataFactory.defaultDLDDMStructureModel
	_ddmStructureLayoutModel = dLDataFactory.defaultDLDDMStructureLayoutModel
	_ddmStructureVersionModel = dLDataFactory.defaultDLDDMStructureVersionModel
/>