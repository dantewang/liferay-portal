create unique index IX_A35C3A6B on AssetListEntry (ctCollectionId, groupId, assetListEntryKey[$COLUMN_LENGTH:75$]);
create unique index IX_3B729C6 on AssetListEntry (ctCollectionId, groupId, title[$COLUMN_LENGTH:75$]);
create index IX_1EC91093 on AssetListEntry (ctCollectionId, groupId, type_);
create unique index IX_4C78D072 on AssetListEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_54DD6638 on AssetListEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_92499A36 on AssetListEntryAssetEntryRel (ctCollectionId, assetListEntryId, segmentsEntryId, position);
create index IX_2D9E17E7 on AssetListEntryAssetEntryRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_BBC821E1 on AssetListEntryAssetEntryRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_99A29B7B on AssetListEntrySegmentsEntryRel (ctCollectionId, segmentsEntryId, assetListEntryId);
create index IX_E4D315F on AssetListEntrySegmentsEntryRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_C5DF3959 on AssetListEntrySegmentsEntryRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_A318ABE0 on AssetListEntryUsage (ctCollectionId, classNameId, groupId, key_[$COLUMN_LENGTH:255$], type_);
create index IX_36DB7A0F on AssetListEntryUsage (ctCollectionId, classNameId, key_[$COLUMN_LENGTH:255$], companyId);
create unique index IX_49F85F2B on AssetListEntryUsage (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_D5D3C8BD on AssetListEntryUsage (ctCollectionId, plid, classNameId, containerType, groupId, key_[$COLUMN_LENGTH:255$], containerKey[$COLUMN_LENGTH:255$]);
create index IX_C384FBFF on AssetListEntryUsage (ctCollectionId, plid, containerType, containerKey[$COLUMN_LENGTH:255$]);
create index IX_F0B42AB1 on AssetListEntryUsage (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);