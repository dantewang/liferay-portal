create unique index IX_CF837971 on DEDataDefinitionFieldLink (ctCollectionId, ddmStructureId, fieldName[$COLUMN_LENGTH:255$], classNameId, classPK);
create index IX_C1AB9DEA on DEDataDefinitionFieldLink (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_46754CA4 on DEDataDefinitionFieldLink (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_F2132B23 on DEDataListView (ctCollectionId, companyId, ddmStructureId, groupId);
create index IX_DEEBD8E7 on DEDataListView (ctCollectionId, ddmStructureId);
create index IX_5685ED02 on DEDataListView (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_C34881BC on DEDataListView (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);