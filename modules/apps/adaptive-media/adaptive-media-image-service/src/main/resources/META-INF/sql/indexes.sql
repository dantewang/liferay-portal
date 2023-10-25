create index IX_1BA41EE0 on AMImageEntry (ctCollectionId, companyId, configurationUuid[$COLUMN_LENGTH:75$]);
create unique index IX_B7E26351 on AMImageEntry (ctCollectionId, configurationUuid[$COLUMN_LENGTH:75$], fileVersionId);
create index IX_379D2580 on AMImageEntry (ctCollectionId, fileVersionId);
create index IX_63348657 on AMImageEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_4076C51 on AMImageEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);