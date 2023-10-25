create index IX_8F495D7 on LayoutClassedModelUsage (ctCollectionId, classNameId, classPK, type_);
create index IX_90BD087F on LayoutClassedModelUsage (ctCollectionId, classNameId, companyId, cmExternalReferenceCode[$COLUMN_LENGTH:75$], type_);
create index IX_ADFD7824 on LayoutClassedModelUsage (ctCollectionId, classNameId, companyId, containerType);
create unique index IX_BD2ED6EF on LayoutClassedModelUsage (ctCollectionId, classNameId, containerType, plid, classPK, cmExternalReferenceCode[$COLUMN_LENGTH:75$], containerKey[$COLUMN_LENGTH:200$]);
create index IX_567ECD79 on LayoutClassedModelUsage (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_4E0B4237 on LayoutClassedModelUsage (ctCollectionId, containerType, plid, containerKey[$COLUMN_LENGTH:200$]);
create index IX_DE2FB4 on LayoutClassedModelUsage (ctCollectionId, plid);
create unique index IX_261D13F3 on LayoutClassedModelUsage (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_2FA447F9 on LayoutLocalization (ctCollectionId, plid, languageId[$COLUMN_LENGTH:75$]);
create index IX_17B2CC37 on LayoutLocalization (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_A51DAA31 on LayoutLocalization (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);