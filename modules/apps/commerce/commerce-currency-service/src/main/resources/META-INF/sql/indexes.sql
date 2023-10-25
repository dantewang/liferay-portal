create unique index IX_B80E0518 on CommerceCurrency (companyId, code_[$COLUMN_LENGTH:75$]);
create index IX_6BF47A82 on CommerceCurrency (companyId, primary_, active_);
create index IX_E3DB30FE on CommerceCurrency (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_EE967482 on CommerceCurrency (uuid_[$COLUMN_LENGTH:75$]);