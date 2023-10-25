create unique index IX_B46C54E2 on CPLCommerceGroupAccountRel (ctCollectionId, commercePriceListId, commerceAccountGroupId);
create index IX_F014465 on CPLCommerceGroupAccountRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_8036F8C8 on CommercePriceEntry (ctCollectionId, CPInstanceUuid[$COLUMN_LENGTH:75$], quantity, unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_208B496B on CommercePriceEntry (ctCollectionId, commercePriceListId);
create unique index IX_E44CB241 on CommercePriceEntry (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_29A776B8 on CommercePriceEntry (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_3252BBC0 on CommercePriceEntry (ctCollectionId, status, CPInstanceUuid[$COLUMN_LENGTH:75$], commercePriceListId);
create index IX_1C3607E on CommercePriceEntry (ctCollectionId, status, displayDate);
create index IX_A6C00A3F on CommercePriceEntry (ctCollectionId, status, expirationDate);
create index IX_F87FD8C on CommercePriceEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_8BFB69EB on CommercePriceList (ctCollectionId, commerceCurrencyId);
create unique index IX_A0692909 on CommercePriceList (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_60FE5BF9 on CommercePriceList (ctCollectionId, companyId, groupId, status, type_[$COLUMN_LENGTH:75$]);
create index IX_ABDA7D80 on CommercePriceList (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_7418AF2C on CommercePriceList (ctCollectionId, groupId, type_[$COLUMN_LENGTH:75$], catalogBasePriceList);
create unique index IX_14E319BA on CommercePriceList (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_9AF8489D on CommercePriceList (ctCollectionId, parentCommercePriceListId);
create index IX_71C584B6 on CommercePriceList (ctCollectionId, status, displayDate);
create index IX_ADF563C4 on CommercePriceList (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_D44100F6 on CommercePriceListAccountRel (ctCollectionId, commercePriceListId, commerceAccountId);
create index IX_8B8A7FCC on CommercePriceListAccountRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_A19A53CA on CommercePriceListChannelRel (ctCollectionId, commercePriceListId, commerceChannelId);
create index IX_BA2DA836 on CommercePriceListChannelRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_EB1C0CF8 on CommercePriceListDiscountRel (ctCollectionId, commercePriceListId, commerceDiscountId);
create index IX_2534EBE0 on CommercePriceListDiscountRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_D2482B40 on CommercePriceListOrderTypeRel (ctCollectionId, commercePriceListId, commerceOrderTypeId);
create index IX_ABD826F1 on CommercePriceListOrderTypeRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_181910 on CommerceTierPriceEntry (ctCollectionId, commercePriceEntryId, minQuantity);
create unique index IX_4B03AB43 on CommerceTierPriceEntry (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F24B3BA on CommerceTierPriceEntry (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_EAE1EC3C on CommerceTierPriceEntry (ctCollectionId, status, displayDate);
create index IX_67FDC1 on CommerceTierPriceEntry (ctCollectionId, status, expirationDate);
create index IX_A9AECDCA on CommerceTierPriceEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);