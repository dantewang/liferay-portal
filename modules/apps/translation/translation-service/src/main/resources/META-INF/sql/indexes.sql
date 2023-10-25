create unique index IX_B69D02A1 on TranslationEntry (ctCollectionId, classNameId, classPK, languageId[$COLUMN_LENGTH:75$]);
create index IX_85990295 on TranslationEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_A939E00F on TranslationEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);