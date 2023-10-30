create index IX_D0050DF7 on SavedContentEntry (classNameId, companyId, classPK);
create index IX_8D476824 on SavedContentEntry (groupId);
create unique index IX_4715B10F on SavedContentEntry (userId, classNameId, companyId, classPK, ctCollectionId);
create index IX_26BC5C5E on SavedContentEntry (userId, groupId);