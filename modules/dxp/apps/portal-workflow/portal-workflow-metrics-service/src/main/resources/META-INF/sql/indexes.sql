create index IX_6C443ED2 on WMSLADefinition (active_, wmSLADefinitionId);
create index IX_20399751 on WMSLADefinition (companyId, active_, processId, name[$COLUMN_LENGTH:75$]);
create index IX_57467573 on WMSLADefinition (companyId, active_, processId, status, processVersion[$COLUMN_LENGTH:75$]);
create index IX_73175D43 on WMSLADefinition (companyId, status);
create index IX_A96F4BB7 on WMSLADefinition (companyId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_F61373B5 on WMSLADefinition (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_57E88F05 on WMSLADefinitionVersion (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_A9013283 on WMSLADefinitionVersion (uuid_[$COLUMN_LENGTH:75$], groupId);
create index IX_A59DFB41 on WMSLADefinitionVersion (wmSLADefinitionId, version[$COLUMN_LENGTH:75$]);