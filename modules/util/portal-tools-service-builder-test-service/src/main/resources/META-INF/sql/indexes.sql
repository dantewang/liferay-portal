create index IX_838D8DFC on BigDecimalEntries_LVEntries (companyId);
create index IX_67100507 on BigDecimalEntries_LVEntries (lvEntryId);

create index IX_867C5A9 on BigDecimalEntry (bigDecimalValue);

create unique index IX_1CF99E19 on CacheDisabledEntry (name[$COLUMN_LENGTH:75$]);

create index IX_4F11FECA on CacheFieldEntry (groupId);

create unique index IX_32F1A726 on ERCCompanyEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_EAD4D59D on ERCCompanyEntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_84557D43 on ERCCompanyEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_46A93C9C on ERCGroupEntry (groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E1BD5F5B on ERCGroupEntry (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_E55A2059 on ERCGroupEntry (uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_3BBD2CED on EagerBlobEntry (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_420C1E47 on FinderWhereClauseEntry (name[$COLUMN_LENGTH:75$]);

create unique index IX_2FF02DF5 on LVEntry (groupId, head, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create index IX_8F9FD921 on LVEntry (groupId, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create unique index IX_50CAD09D on LVEntry (headId);
create index IX_800F27DA on LVEntry (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_96CB2944 on LVEntry (uuid_[$COLUMN_LENGTH:75$], groupId, head);
create index IX_72375B06 on LVEntry (uuid_[$COLUMN_LENGTH:75$], head, companyId);

create unique index IX_FC1C4C16 on LVEntryLocalization (headId);
create unique index IX_CE7BD65 on LVEntryLocalization (lvEntryId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_F28D96D9 on LVEntryLocalizationVersion (version, lvEntryId, languageId[$COLUMN_LENGTH:75$]);
create unique index IX_EAC6D2F9 on LVEntryLocalizationVersion (version, lvEntryLocalizationId);

create unique index IX_D4DF2FAF on LVEntryVersion (version, groupId, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create unique index IX_158AC9C8 on LVEntryVersion (version, groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_4D8E2BAB on LVEntryVersion (version, lvEntryId);
create index IX_3A6932C6 on LVEntryVersion (version, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_C4DC74F1 on LazyBlobEntry (uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_3BF59D83 on LocalizedEntryLocalization (localizedEntryId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_46C721B9 on NullConvertibleEntry (name[$COLUMN_LENGTH:75$]);

create unique index IX_32712A54 on RedundantIndexEntry (companyId, name[$COLUMN_LENGTH:75$]);

create index IX_DA817981 on RenameFinderColumnEntry (columnToRename[$COLUMN_LENGTH:75$]);

create index IX_6770C47D on VersionedEntry (groupId, head);
create unique index IX_AAA6F330 on VersionedEntry (headId);

create index IX_D2594361 on VersionedEntryVersion (version, groupId);
create unique index IX_3129EDCF on VersionedEntryVersion (version, versionedEntryId);
create index IX_6FBDC3ED on VersionedEntryVersion (versionedEntryId);