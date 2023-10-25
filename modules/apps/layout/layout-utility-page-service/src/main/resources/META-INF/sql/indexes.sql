create unique index IX_8CB34E72 on LayoutUtilityPageEntry (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_AA74AF18 on LayoutUtilityPageEntry (ctCollectionId, groupId, type_[$COLUMN_LENGTH:75$], defaultLayoutUtilityPageEntry);
create unique index IX_8045A171 on LayoutUtilityPageEntry (ctCollectionId, groupId, type_[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$]);
create unique index IX_93FE9E2F on LayoutUtilityPageEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_CA8014F0 on LayoutUtilityPageEntry (ctCollectionId, plid);
create index IX_D226B8B5 on LayoutUtilityPageEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);