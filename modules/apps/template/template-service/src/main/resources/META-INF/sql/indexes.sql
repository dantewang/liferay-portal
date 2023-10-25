create index IX_8C9BC796 on TemplateEntry (ctCollectionId, ddmTemplateId);
create index IX_C7B53589 on TemplateEntry (ctCollectionId, groupId, infoItemClassName[$COLUMN_LENGTH:75$], infoItemFormVariationKey[$COLUMN_LENGTH:75$]);
create index IX_57070AAC on TemplateEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_E9E901E6 on TemplateEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);