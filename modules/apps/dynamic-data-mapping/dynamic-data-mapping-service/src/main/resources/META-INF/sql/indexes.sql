create index IX_98A07511 on DDMDataProviderInstance (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_5333C18B on DDMDataProviderInstance (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_F4F649A4 on DDMDataProviderInstanceLink (ctCollectionId, structureId, dataProviderInstanceId);

create index IX_21E88F31 on DDMField (ctCollectionId, companyId, fieldType[$COLUMN_LENGTH:255$]);
create index IX_9EF6E242 on DDMField (ctCollectionId, storageId, fieldName[$COLUMN_LENGTH:255$]);
create unique index IX_CFC9B471 on DDMField (ctCollectionId, storageId, instanceId[$COLUMN_LENGTH:75$]);
create index IX_B349D7E5 on DDMField (ctCollectionId, structureVersionId);

create unique index IX_6821726C on DDMFieldAttribute (ctCollectionId, attributeName[$COLUMN_LENGTH:255$], languageId[$COLUMN_LENGTH:75$], fieldId);
create index IX_5C20E44C on DDMFieldAttribute (ctCollectionId, attributeName[$COLUMN_LENGTH:255$], smallAttributeValue[$COLUMN_LENGTH:255$]);
create index IX_E73EB326 on DDMFieldAttribute (ctCollectionId, attributeName[$COLUMN_LENGTH:255$], storageId);
create index IX_378AECC on DDMFieldAttribute (ctCollectionId, storageId, languageId[$COLUMN_LENGTH:75$]);

create index IX_A902059A on DDMFormInstance (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_86A1E054 on DDMFormInstance (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_8A64884C on DDMFormInstanceRecord (ctCollectionId, formInstanceId, formInstanceVersion[$COLUMN_LENGTH:75$]);
create index IX_D927805D on DDMFormInstanceRecord (ctCollectionId, formInstanceId, userId);
create index IX_A46750B on DDMFormInstanceRecord (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_2C6D7805 on DDMFormInstanceRecord (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_4DA45502 on DDMFormInstanceRecordVersion (ctCollectionId, formInstanceId, status, userId, formInstanceVersion[$COLUMN_LENGTH:75$]);
create index IX_28036507 on DDMFormInstanceRecordVersion (ctCollectionId, formInstanceId, userId);
create index IX_5D0A6AA4 on DDMFormInstanceRecordVersion (ctCollectionId, formInstanceRecordId, status);
create unique index IX_6BC9C20A on DDMFormInstanceRecordVersion (ctCollectionId, formInstanceRecordId, version[$COLUMN_LENGTH:75$]);

create index IX_F8F7EB46 on DDMFormInstanceReport (ctCollectionId, formInstanceId);

create index IX_DBF65B84 on DDMFormInstanceVersion (ctCollectionId, formInstanceId, status);
create unique index IX_CA5BED2A on DDMFormInstanceVersion (ctCollectionId, formInstanceId, version[$COLUMN_LENGTH:75$]);

create unique index IX_6979A733 on DDMStorageLink (ctCollectionId, classPK);
create index IX_8A422EF2 on DDMStorageLink (ctCollectionId, structureId);
create index IX_6B1F7980 on DDMStorageLink (ctCollectionId, structureVersionId);
create index IX_AF01E9BC on DDMStorageLink (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_344EA2E4 on DDMStructure (ctCollectionId, classNameId, companyId);
create unique index IX_734B9D4A on DDMStructure (ctCollectionId, groupId, classNameId, structureKey[$COLUMN_LENGTH:75$]);
create index IX_4DBEB874 on DDMStructure (ctCollectionId, groupId, parentStructureId);
create unique index IX_6DDCFA94 on DDMStructure (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_DEB0128A on DDMStructure (ctCollectionId, parentStructureId);
create index IX_CC92E22A on DDMStructure (ctCollectionId, structureKey[$COLUMN_LENGTH:75$]);
create index IX_ADDF8FDA on DDMStructure (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_B0C5B54A on DDMStructureLayout (ctCollectionId, groupId, classNameId, structureLayoutKey[$COLUMN_LENGTH:75$]);
create index IX_630D4B08 on DDMStructureLayout (ctCollectionId, groupId, classNameId, structureVersionId);
create unique index IX_FA77ADE on DDMStructureLayout (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_3381C5AA on DDMStructureLayout (ctCollectionId, structureLayoutKey[$COLUMN_LENGTH:75$]);
create index IX_E5C95B68 on DDMStructureLayout (ctCollectionId, structureVersionId);
create index IX_70B25A4 on DDMStructureLayout (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_F7718C05 on DDMStructureLink (ctCollectionId, structureId, classNameId, classPK);

create index IX_3FCD4FC2 on DDMStructureVersion (ctCollectionId, structureId, status);
create unique index IX_C67A820 on DDMStructureVersion (ctCollectionId, structureId, version[$COLUMN_LENGTH:75$]);

create index IX_83251775 on DDMTemplate (ctCollectionId, classNameId, classPK, type_[$COLUMN_LENGTH:75$]);
create index IX_1D802253 on DDMTemplate (ctCollectionId, groupId, classNameId, classPK, type_[$COLUMN_LENGTH:75$], mode_[$COLUMN_LENGTH:75$]);
create unique index IX_DBA4D62A on DDMTemplate (ctCollectionId, groupId, classNameId, templateKey[$COLUMN_LENGTH:75$]);
create index IX_21666FB6 on DDMTemplate (ctCollectionId, groupId, classPK);
create unique index IX_B0644995 on DDMTemplate (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_C2D1F457 on DDMTemplate (ctCollectionId, language[$COLUMN_LENGTH:75$]);
create index IX_D016958E on DDMTemplate (ctCollectionId, smallImageId);
create index IX_7E6D468A on DDMTemplate (ctCollectionId, templateKey[$COLUMN_LENGTH:75$]);
create index IX_706521AA on DDMTemplate (ctCollectionId, type_[$COLUMN_LENGTH:75$]);
create index IX_6BCF229B on DDMTemplate (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_79ED5CFA on DDMTemplateLink (ctCollectionId, classNameId, classPK);
create index IX_8E50CCCE on DDMTemplateLink (ctCollectionId, templateId);

create index IX_ACBAD24 on DDMTemplateVersion (ctCollectionId, templateId, status);
create unique index IX_AC181F8A on DDMTemplateVersion (ctCollectionId, templateId, version[$COLUMN_LENGTH:75$]);