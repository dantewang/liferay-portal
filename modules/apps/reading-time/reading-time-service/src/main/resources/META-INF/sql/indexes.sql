create unique index IX_B3DFB758 on ReadingTimeEntry (ctCollectionId, groupId, classNameId, classPK);
create index IX_612486CD on ReadingTimeEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_EBC91247 on ReadingTimeEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);