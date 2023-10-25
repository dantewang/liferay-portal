create unique index IX_1530FB80 on AssetDisplayPageEntry (ctCollectionId, groupId, classNameId, classPK);
create index IX_B0A855F5 on AssetDisplayPageEntry (ctCollectionId, layoutPageTemplateEntryId);
create index IX_B74FA1A5 on AssetDisplayPageEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_8B2D031F on AssetDisplayPageEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);