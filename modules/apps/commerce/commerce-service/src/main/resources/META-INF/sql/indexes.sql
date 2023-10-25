create unique index IX_17D56F1B on CPDAvailabilityEstimate (CProductId);
create index IX_E560850D on CPDAvailabilityEstimate (commerceAvailabilityEstimateId);
create index IX_C82D518C on CPDAvailabilityEstimate (uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_2A31024F on CPDefinitionInventory (ctCollectionId, CPDefinitionId);
create index IX_8CD72850 on CPDefinitionInventory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_83AC388A on CPDefinitionInventory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_4F4C712A on CSOptionAccountEntryRel (commerceChannelId, accountEntryId);
create index IX_64B9CFFC on CSOptionAccountEntryRel (commerceShippingOptionKey[$COLUMN_LENGTH:75$]);

create unique index IX_9DD3ABD3 on CommerceAddressRestriction (countryId, classNameId, classPK);

create index IX_51F7C710 on CommerceAvailabilityEstimate (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_12131FC1 on CommerceOrder (billingAddressId);
create unique index IX_48EEEDEE on CommerceOrder (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_87E6EC4A on CommerceOrder (groupId, commercePaymentMethodKey[$COLUMN_LENGTH:75$]);
create index IX_7DD246EA on CommerceOrder (groupId, orderStatus, commerceAccountId);
create index IX_2F8AA139 on CommerceOrder (groupId, orderStatus, userId);
create unique index IX_25C927E3 on CommerceOrder (groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_254B9A4E on CommerceOrder (orderStatus, commerceAccountId, createDate);
create index IX_B1634453 on CommerceOrder (orderStatus, userId, createDate);
create index IX_4B11FAD8 on CommerceOrder (shippingAddressId);
create index IX_EFAA753 on CommerceOrder (userId);
create index IX_C288AC65 on CommerceOrder (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_654BB574 on CommerceOrderItem (CIBookedQuantityId);
create index IX_F9E8D927 on CommerceOrderItem (CProductId);
create index IX_F18DBD61 on CommerceOrderItem (commerceOrderId, CPInstanceId);
create index IX_15B37023 on CommerceOrderItem (commerceOrderId, subscription);
create unique index IX_12257E21 on CommerceOrderItem (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F0E98FC7 on CommerceOrderItem (customerCommerceOrderItemId);
create index IX_8E1472FB on CommerceOrderItem (parentCommerceOrderItemId);
create index IX_5F540298 on CommerceOrderItem (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_BDCA6ED6 on CommerceOrderItem (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_CEB86C22 on CommerceOrderNote (commerceOrderId, restricted);
create unique index IX_EF4EEF80 on CommerceOrderNote (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_557211F7 on CommerceOrderNote (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_4492C9F5 on CommerceOrderNote (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_CF274005 on CommerceOrderPayment (commerceOrderId);

create index IX_37937972 on CommerceOrderType (companyId, active_);
create unique index IX_4EC1CAC8 on CommerceOrderType (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_9547D3F on CommerceOrderType (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_CAB43113 on CommerceOrderType (status, displayDate);
create index IX_56A0F58A on CommerceOrderType (status, expirationDate);
create index IX_FAD246E1 on CommerceOrderType (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_CBAD3B91 on CommerceOrderTypeRel (commerceOrderTypeId, classNameId, classPK);
create unique index IX_22C116C7 on CommerceOrderTypeRel (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_6991A73E on CommerceOrderTypeRel (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_D24B6642 on CommerceOrderTypeRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_F5105190 on CommerceShipment (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_221126A1 on CommerceShipment (groupId, commerceAddressId);
create index IX_68FBA2B5 on CommerceShipment (groupId, status);
create unique index IX_88139005 on CommerceShipment (groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_BBD99407 on CommerceShipment (uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_BF6D80D6 on CommerceShipmentItem (commerceShipmentId, commerceOrderItemId, commerceInventoryWarehouseId);
create unique index IX_41C840C3 on CommerceShipmentItem (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F0C4493A on CommerceShipmentItem (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_A67FF5F8 on CommerceShipmentItem (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_F58C3D11 on CommerceShippingMethod (groupId, active_);
create unique index IX_FFD3676D on CommerceShippingMethod (groupId, engineKey[$COLUMN_LENGTH:75$]);

create unique index IX_D7D137B1 on CommerceSubscriptionEntry (commerceOrderItemId);
create index IX_5F1D189C on CommerceSubscriptionEntry (companyId, groupId, userId);
create index IX_43E6F382 on CommerceSubscriptionEntry (companyId, userId);
create index IX_AAF6056C on CommerceSubscriptionEntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_B496E103 on CommerceSubscriptionEntry (subscriptionStatus);
create unique index IX_61F716AA on CommerceSubscriptionEntry (uuid_[$COLUMN_LENGTH:75$], groupId);