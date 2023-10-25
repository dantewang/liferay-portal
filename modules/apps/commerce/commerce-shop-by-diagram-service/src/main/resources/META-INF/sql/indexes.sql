create unique index IX_E9871D7 on CSDiagramEntry (ctCollectionId, CPDefinitionId, sequence[$COLUMN_LENGTH:75$]);
create index IX_9D8C9924 on CSDiagramEntry (ctCollectionId, CPInstanceId);
create index IX_6DCBA0EE on CSDiagramEntry (ctCollectionId, CProductId);

create index IX_1BC6C985 on CSDiagramPin (CPDefinitionId, ctCollectionId);

create unique index IX_4F753100 on CSDiagramSetting (ctCollectionId, CPDefinitionId);
create index IX_71CA6EC1 on CSDiagramSetting (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);