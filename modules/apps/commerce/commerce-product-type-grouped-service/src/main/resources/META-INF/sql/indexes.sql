create unique index IX_64F7EFA0 on CPDefinitionGroupedEntry (entryCProductId, CPDefinitionId);
create index IX_61CF0246 on CPDefinitionGroupedEntry (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_B0BD8204 on CPDefinitionGroupedEntry (uuid_[$COLUMN_LENGTH:75$], groupId);