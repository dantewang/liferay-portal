create unique index IX_7CBDB1F3 on CPricingClassCPDefinitionRel (ctCollectionId, CPDefinitionId, commercePricingClassId);
create index IX_CD543364 on CPricingClassCPDefinitionRel (ctCollectionId, commercePricingClassId);

create index IX_2D86244A on CommercePriceModifier (ctCollectionId, commercePriceListId);
create unique index IX_DB76B9C2 on CommercePriceModifier (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_FCFD3DD5 on CommercePriceModifier (ctCollectionId, companyId, status, groupId);
create index IX_6E554EE0 on CommercePriceModifier (ctCollectionId, companyId, target[$COLUMN_LENGTH:75$]);
create index IX_ACAAA039 on CommercePriceModifier (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_EBE3B5D on CommercePriceModifier (ctCollectionId, status, displayDate);
create index IX_23044F00 on CommercePriceModifier (ctCollectionId, status, expirationDate);
create unique index IX_969E96B3 on CommercePriceModifier (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_6B47904D on CommercePriceModifierRel (ctCollectionId, classNameId, classPK);
create unique index IX_65CB769F on CommercePriceModifierRel (ctCollectionId, classNameId, commercePriceModifierId, classPK);
create index IX_153045D4 on CommercePriceModifierRel (ctCollectionId, commercePriceModifierId);

create unique index IX_58120DAA on CommercePricingClass (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_75FAC421 on CommercePricingClass (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_4C1EF7C3 on CommercePricingClass (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);