create index IX_45C5C370 on CIAudit (companyId, sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_E7D143D9 on CIAudit (createDate);

create index IX_33BF9CB0 on CIBookedQuantity (expirationDate);
create index IX_625D8A54 on CIBookedQuantity (sku[$COLUMN_LENGTH:75$], companyId, unitOfMeasureKey[$COLUMN_LENGTH:75$]);

create index IX_967CACA8 on CIReplenishmentItem (commerceInventoryWarehouseId);
create unique index IX_3462AACC on CIReplenishmentItem (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_62C4534C on CIReplenishmentItem (companyId, sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_5C1DE543 on CIReplenishmentItem (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_E160DC29 on CIReplenishmentItem (sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$], availabilityDate);
create index IX_B359B95D on CIReplenishmentItem (uuid_[$COLUMN_LENGTH:75$]);

create index IX_138EC3F1 on CIWarehouse (companyId, countryTwoLettersISOCode[$COLUMN_LENGTH:75$], active_);
create unique index IX_68E6B8D8 on CIWarehouse (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F4B78B4F on CIWarehouse (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_3CCB62D1 on CIWarehouse (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_8A09C40B on CIWarehouseItem (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_B86B6C8B on CIWarehouseItem (companyId, sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_FC55C82 on CIWarehouseItem (companyId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_B4413476 on CIWarehouseItem (sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$], commerceInventoryWarehouseId);
create index IX_4AD4537E on CIWarehouseItem (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_A743341B on CIWarehouseRel (CIWarehouseId, classNameId, classPK);