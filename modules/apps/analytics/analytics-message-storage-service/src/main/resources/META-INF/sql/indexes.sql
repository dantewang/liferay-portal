create index IX_7248F4A9 on AnalyticsAssociation (associationClassName[$COLUMN_LENGTH:75$], companyId, ctCollectionId, associationClassPK);
create index IX_B37A589A on AnalyticsAssociation (associationClassName[$COLUMN_LENGTH:75$], companyId, ctCollectionId, modifiedDate);

create index IX_116D6D75 on AnalyticsDeleteMessage (companyId, ctCollectionId, modifiedDate);

create index IX_E05580DF on AnalyticsMessage (companyId, ctCollectionId);