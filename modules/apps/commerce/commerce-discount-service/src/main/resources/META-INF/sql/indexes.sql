create unique index IX_F11225F5 on CDiscountCAccountGroupRel (commerceDiscountId, commerceAccountGroupId);

create index IX_2118FA08 on CommerceDiscount (companyId, active_, couponCode[$COLUMN_LENGTH:75$]);
create index IX_E063D0AD on CommerceDiscount (companyId, couponCode[$COLUMN_LENGTH:75$]);
create unique index IX_D294CDB7 on CommerceDiscount (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F6864DB3 on CommerceDiscount (companyId, status, active_, levelType[$COLUMN_LENGTH:75$]);
create index IX_D0113E2E on CommerceDiscount (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_122C15C4 on CommerceDiscount (status, displayDate);
create index IX_2FBF0739 on CommerceDiscount (status, expirationDate);
create index IX_F1A4C552 on CommerceDiscount (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E082887A on CommerceDiscountAccountRel (commerceDiscountId, commerceAccountId);
create index IX_3AF7E47A on CommerceDiscountAccountRel (uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_614617A on CommerceDiscountOrderTypeRel (commerceOrderTypeId, commerceDiscountId);
create index IX_8125919F on CommerceDiscountOrderTypeRel (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_6B4EEC38 on CommerceDiscountRel (classNameId, classPK);
create index IX_585D82B6 on CommerceDiscountRel (classNameId, commerceDiscountId);
create index IX_A6E848CE on CommerceDiscountRel (commerceDiscountId);

create index IX_CB9E6769 on CommerceDiscountRule (commerceDiscountId);

create index IX_54F74209 on CommerceDiscountUsageEntry (commerceDiscountId, commerceOrderId, commerceAccountId);