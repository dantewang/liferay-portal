create index IX_F5701FF9 on RedirectEntry (groupId, destinationURL[$COLUMN_LENGTH:4000$]);
create unique index IX_5040C136 on RedirectEntry (groupId, sourceURL[$COLUMN_LENGTH:4000$]);
create unique index IX_E33009E6 on RedirectEntry (groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_C1BF19A8 on RedirectEntry (uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_84671762 on RedirectNotFoundEntry (groupId, url[$COLUMN_LENGTH:4000$]);