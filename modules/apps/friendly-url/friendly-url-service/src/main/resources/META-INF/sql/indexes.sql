create index IX_AAFD9E63 on FriendlyURLEntry (ctCollectionId, groupId, classNameId, classPK);
create index IX_976509E2 on FriendlyURLEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_7109569C on FriendlyURLEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_44BB2711 on FriendlyURLEntryLocalization (ctCollectionId, classNameId, groupId, languageId[$COLUMN_LENGTH:75$], classPK);
create unique index IX_8DD9AECF on FriendlyURLEntryLocalization (ctCollectionId, classNameId, groupId, languageId[$COLUMN_LENGTH:75$], urlTitle[$COLUMN_LENGTH:255$]);
create index IX_EA3FA3C8 on FriendlyURLEntryLocalization (ctCollectionId, classNameId, groupId, urlTitle[$COLUMN_LENGTH:255$]);
create unique index IX_3488F90F on FriendlyURLEntryLocalization (ctCollectionId, languageId[$COLUMN_LENGTH:75$], friendlyURLEntryId);

create unique index IX_5BE324B9 on FriendlyURLEntryMapping (classNameId, classPK, ctCollectionId);