create index IX_B48C8A11 on StyleBookEntry (ctCollectionId, groupId, head, defaultStyleBookEntry);
create index IX_684F7521 on StyleBookEntry (ctCollectionId, groupId, head, name[$COLUMN_LENGTH:75$]);
create unique index IX_12605CB5 on StyleBookEntry (ctCollectionId, groupId, head, styleBookEntryKey[$COLUMN_LENGTH:75$]);
create unique index IX_35D3B092 on StyleBookEntry (ctCollectionId, groupId, head, uuid_[$COLUMN_LENGTH:75$]);
create index IX_B05784CD on StyleBookEntry (ctCollectionId, groupId, name[$COLUMN_LENGTH:75$]);
create index IX_D72C2489 on StyleBookEntry (ctCollectionId, groupId, styleBookEntryKey[$COLUMN_LENGTH:75$]);
create index IX_EECD9666 on StyleBookEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_2C626CD8 on StyleBookEntry (ctCollectionId, head, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_EC8C186B on StyleBookEntry (ctCollectionId, headId);
create index IX_B5187F2C on StyleBookEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_282FC6E3 on StyleBookEntryVersion (ctCollectionId, version, groupId, defaultStyleBookEntry);
create index IX_612DF2A3 on StyleBookEntryVersion (ctCollectionId, version, groupId, name[$COLUMN_LENGTH:75$]);
create unique index IX_FABB35D7 on StyleBookEntryVersion (ctCollectionId, version, groupId, styleBookEntryKey[$COLUMN_LENGTH:75$]);
create unique index IX_5F1B209A on StyleBookEntryVersion (ctCollectionId, version, groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_C2AB243D on StyleBookEntryVersion (ctCollectionId, version, styleBookEntryId);
create index IX_92000894 on StyleBookEntryVersion (ctCollectionId, version, uuid_[$COLUMN_LENGTH:75$], companyId);