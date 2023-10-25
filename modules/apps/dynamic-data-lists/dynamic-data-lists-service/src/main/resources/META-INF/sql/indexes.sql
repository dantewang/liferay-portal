create index IX_181962D1 on DDLRecord (ctCollectionId, className[$COLUMN_LENGTH:300$], classPK);
create index IX_56D4E5B6 on DDLRecord (ctCollectionId, recordSetId, recordSetVersion[$COLUMN_LENGTH:75$]);
create index IX_6AE4FB35 on DDLRecord (ctCollectionId, recordSetId, userId);
create index IX_36EEAB71 on DDLRecord (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_1A5C0FEB on DDLRecord (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_559DA7DE on DDLRecordSet (ctCollectionId, DDMStructureId);
create unique index IX_9D1A8703 on DDLRecordSet (ctCollectionId, groupId, recordSetKey[$COLUMN_LENGTH:75$]);
create index IX_A1BE6819 on DDLRecordSet (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_43C2D693 on DDLRecordSet (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_EDC5DF2B on DDLRecordSetVersion (ctCollectionId, recordSetId, status);
create unique index IX_F27CDE63 on DDLRecordSetVersion (ctCollectionId, recordSetId, version[$COLUMN_LENGTH:75$]);

create index IX_7D2686A5 on DDLRecordVersion (ctCollectionId, recordId, status);
create unique index IX_4F312629 on DDLRecordVersion (ctCollectionId, recordId, version[$COLUMN_LENGTH:75$]);
create index IX_4D6016F8 on DDLRecordVersion (ctCollectionId, status, recordSetId, recordSetVersion[$COLUMN_LENGTH:75$], userId);