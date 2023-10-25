create index IX_FDC27113 on JournalArticle (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_60CC9052 on JournalArticle (ctCollectionId, groupId, DDMStructureId);
create index IX_BF43A05B on JournalArticle (ctCollectionId, groupId, DDMTemplateKey[$COLUMN_LENGTH:75$]);
create index IX_2EE7DBF8 on JournalArticle (ctCollectionId, groupId, classNameId, DDMStructureId);
create index IX_1736E28F on JournalArticle (ctCollectionId, groupId, classNameId, DDMTemplateKey[$COLUMN_LENGTH:75$]);
create index IX_BB532ED2 on JournalArticle (ctCollectionId, groupId, classNameId, classPK);
create index IX_3FCF435C on JournalArticle (ctCollectionId, groupId, classNameId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_D87221DD on JournalArticle (ctCollectionId, groupId, classNameId, userId);
create index IX_43342680 on JournalArticle (ctCollectionId, groupId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_C065BD56 on JournalArticle (ctCollectionId, groupId, status, articleId[$COLUMN_LENGTH:75$]);
create index IX_42BC640C on JournalArticle (ctCollectionId, groupId, status, classNameId, folderId);
create index IX_C20B5C30 on JournalArticle (ctCollectionId, groupId, status, folderId);
create index IX_ADC8584A on JournalArticle (ctCollectionId, groupId, status, urlTitle[$COLUMN_LENGTH:255$]);
create index IX_D4C0F7E4 on JournalArticle (ctCollectionId, groupId, urlTitle[$COLUMN_LENGTH:255$]);
create index IX_CFEE3701 on JournalArticle (ctCollectionId, groupId, userId);
create unique index IX_E948D60D on JournalArticle (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_73D8C598 on JournalArticle (ctCollectionId, groupId, version, articleId[$COLUMN_LENGTH:75$]);
create unique index IX_7A3105B8 on JournalArticle (ctCollectionId, groupId, version, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_EB1D70FC on JournalArticle (ctCollectionId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_9723E006 on JournalArticle (ctCollectionId, smallImageId);
create index IX_F1378443 on JournalArticle (ctCollectionId, status, displayDate);
create index IX_5BAE4AA8 on JournalArticle (ctCollectionId, status, resourcePrimKey, indexable);
create index IX_33C37681 on JournalArticle (ctCollectionId, status, version, companyId);
create index IX_1C21EE11 on JournalArticle (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_A37A61A7 on JournalArticle (ctCollectionId, version, companyId);

create unique index IX_DA02A968 on JournalArticleLocalization (articlePK, ctCollectionId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_7514751E on JournalArticleResource (ctCollectionId, groupId, articleId[$COLUMN_LENGTH:75$]);
create index IX_7DBFCB41 on JournalArticleResource (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_D392E3BB on JournalArticleResource (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_856A8596 on JournalContentSearch (ctCollectionId, companyId);
create index IX_115EECA2 on JournalContentSearch (ctCollectionId, groupId, privateLayout, articleId[$COLUMN_LENGTH:75$]);
create unique index IX_5062C90C on JournalContentSearch (ctCollectionId, groupId, privateLayout, layoutId, portletId[$COLUMN_LENGTH:200$], articleId[$COLUMN_LENGTH:75$]);
create index IX_44858D17 on JournalContentSearch (ctCollectionId, portletId[$COLUMN_LENGTH:200$]);

create unique index IX_4F2FEBA4 on JournalFeed (ctCollectionId, groupId, feedId[$COLUMN_LENGTH:75$]);
create index IX_46B46589 on JournalFeed (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_1BF97003 on JournalFeed (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_224C42A1 on JournalFolder (ctCollectionId, companyId, status);
create unique index IX_B1CCB956 on JournalFolder (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_8415F627 on JournalFolder (ctCollectionId, groupId, parentFolderId, name[$COLUMN_LENGTH:100$]);
create index IX_D9C2000E on JournalFolder (ctCollectionId, groupId, parentFolderId, status);
create unique index IX_B725113 on JournalFolder (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_3B794299 on JournalFolder (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);