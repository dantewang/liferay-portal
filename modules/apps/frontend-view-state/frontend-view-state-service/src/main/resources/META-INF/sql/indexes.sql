create unique index IX_BDF8CF8B on FVSActiveEntry (clayDataSetDisplayId[$COLUMN_LENGTH:75$], plid, portletId[$COLUMN_LENGTH:200$], userId);
create index IX_37B60BDB on FVSActiveEntry (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_1AEA3DD0 on FVSCustomEntry (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_90D5DA01 on FVSEntry (uuid_[$COLUMN_LENGTH:75$], companyId);