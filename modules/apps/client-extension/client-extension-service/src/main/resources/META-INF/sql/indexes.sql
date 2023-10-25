create unique index IX_3EBFEF7B on ClientExtensionEntry (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_15F0A813 on ClientExtensionEntry (ctCollectionId, companyId, type_[$COLUMN_LENGTH:75$]);
create index IX_43A067F2 on ClientExtensionEntry (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_4E798C92 on ClientExtensionEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_6962A8E1 on ClientExtensionEntryRel (ctCollectionId, classNameId, classPK, type_[$COLUMN_LENGTH:75$]);
create index IX_5CAB8A76 on ClientExtensionEntryRel (ctCollectionId, companyId, cetExternalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_30CF54EC on ClientExtensionEntryRel (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_7ADE3CAF on ClientExtensionEntryRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_82DAD8A9 on ClientExtensionEntryRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);