create unique index IX_3E1C50FD on LayoutSEOEntry (ctCollectionId, groupId, layoutId, privateLayout);
create index IX_8FE24EB3 on LayoutSEOEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_FF039BAD on LayoutSEOEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_696CE02C on LayoutSEOSite (ctCollectionId, groupId);
create index IX_8D86084E on LayoutSEOSite (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);