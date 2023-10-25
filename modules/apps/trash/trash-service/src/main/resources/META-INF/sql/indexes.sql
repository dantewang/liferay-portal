create unique index IX_16DA0033 on TrashEntry (ctCollectionId, classNameId, classPK);
create index IX_D7357906 on TrashEntry (ctCollectionId, companyId);
create index IX_4A5F63FE on TrashEntry (ctCollectionId, groupId, classNameId);
create index IX_5E29D98E on TrashEntry (ctCollectionId, groupId, createDate);

create unique index IX_96536499 on TrashVersion (ctCollectionId, classNameId, classPK);
create index IX_5AAA8D7 on TrashVersion (ctCollectionId, classNameId, entryId);
create index IX_7309C4D9 on TrashVersion (ctCollectionId, entryId);