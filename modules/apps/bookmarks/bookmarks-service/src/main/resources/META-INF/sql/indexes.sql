create index IX_5BF17774 on BookmarksEntry (ctCollectionId, groupId, status, userId, folderId);
create unique index IX_6A97FB43 on BookmarksEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_82642A71 on BookmarksEntry (ctCollectionId, status, companyId);
create index IX_67DD20C9 on BookmarksEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_D5FFA81B on BookmarksFolder (ctCollectionId, companyId, status);
create index IX_FBB14808 on BookmarksFolder (ctCollectionId, groupId, status, parentFolderId);
create index IX_C56645DF on BookmarksFolder (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_1336DD9 on BookmarksFolder (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);