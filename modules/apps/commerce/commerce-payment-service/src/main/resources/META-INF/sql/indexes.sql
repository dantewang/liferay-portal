create unique index IX_D9799B2A on CPMethodGroupRelQualifier (CPaymentMethodGroupRelId, classNameId, classPK);

create index IX_184451A6 on CommercePaymentEntry (companyId, classNameId, classPK);

create index IX_8BE29B30 on CommercePaymentEntryAudit (commercePaymentEntryId);

create index IX_4B95C00D on CommercePaymentMethodGroupRel (groupId, active_);
create unique index IX_FFF17D63 on CommercePaymentMethodGroupRel (groupId, paymentIntegrationKey[$COLUMN_LENGTH:75$]);