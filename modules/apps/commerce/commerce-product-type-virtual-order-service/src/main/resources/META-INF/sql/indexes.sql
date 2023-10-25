create unique index IX_44EADF9A on CommerceVirtualOrderItem (commerceOrderItemId);
create index IX_828E23 on CommerceVirtualOrderItem (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_4FAC6121 on CommerceVirtualOrderItem (uuid_[$COLUMN_LENGTH:75$], groupId);