create unique index IX_4BD0EB07 on COREntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_11FE4C4 on COREntry (companyId, type_[$COLUMN_LENGTH:75$], active_);
create index IX_2B51FB7E on COREntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_44FA9674 on COREntry (status, displayDate);
create index IX_9CB08889 on COREntry (status, expirationDate);
create index IX_DD753A02 on COREntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_EA6EFFC3 on COREntryRel (COREntryId, classNameId, classPK);