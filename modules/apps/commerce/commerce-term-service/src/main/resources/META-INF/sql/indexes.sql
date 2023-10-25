create unique index IX_B197F41B on CTermEntryLocalization (commerceTermEntryId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_B241C786 on CommerceTermEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_2AB59656 on CommerceTermEntry (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_3C08925 on CommerceTermEntry (companyId, type_[$COLUMN_LENGTH:75$], active_);
create unique index IX_CEAD7846 on CommerceTermEntry (companyId, type_[$COLUMN_LENGTH:75$], priority);
create index IX_C031B5FD on CommerceTermEntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_E4825795 on CommerceTermEntry (status, displayDate);
create index IX_539427C8 on CommerceTermEntry (status, expirationDate);
create index IX_7C4118E3 on CommerceTermEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_6C84800D on CommerceTermEntryRel (commerceTermEntryId, classNameId, classPK);