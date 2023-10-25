create unique index IX_17295166 on ListTypeDefinition (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_EE28FFDD on ListTypeDefinition (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_C3F53B03 on ListTypeDefinition (uuid_[$COLUMN_LENGTH:75$]);

create index IX_F6DA940C on ListTypeEntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_AEFE0B2F on ListTypeEntry (listTypeDefinitionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_7C87A7AE on ListTypeEntry (listTypeDefinitionId, key_[$COLUMN_LENGTH:75$]);
create index IX_79966E34 on ListTypeEntry (uuid_[$COLUMN_LENGTH:75$]);