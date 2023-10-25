create index IX_65CA6CC9 on KaleoProcess (DDLRecordSetId);
create index IX_295256C1 on KaleoProcess (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_9471B93F on KaleoProcess (uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_10E0E9D0 on KaleoProcessLink (kaleoProcessId, workflowTaskName[$COLUMN_LENGTH:75$]);