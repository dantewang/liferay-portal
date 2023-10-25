create index IX_2C6347C6 on JSONStorageEntry (classNameId, ctCollectionId, companyId, index_, type_, valueLong);
create index IX_5D7A6019 on JSONStorageEntry (classNameId, ctCollectionId, companyId, key_[$COLUMN_LENGTH:255$], type_, valueLong);
create unique index IX_E86BD2E5 on JSONStorageEntry (classNameId, ctCollectionId, index_, key_[$COLUMN_LENGTH:255$], classPK, parentJSONStorageEntryId);