create index IX_42E60133 on NQueueEntryAttachment (notificationQueueEntryId);

create unique index IX_8F1205E1 on NTemplateAttachment (notificationTemplateId, objectFieldId);

create index IX_83DBCE06 on NotificationQueueEntry (notificationTemplateId);
create index IX_3B9F9C6C on NotificationQueueEntry (sentDate);
create index IX_24FE0BDD on NotificationQueueEntry (status, type_[$COLUMN_LENGTH:75$]);

create index IX_470340CF on NotificationRecipient (classPK);
create index IX_EA100520 on NotificationRecipient (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_68EF57D2 on NotificationRecipientSetting (notificationRecipientId, name[$COLUMN_LENGTH:75$]);
create index IX_F1CBA2F4 on NotificationRecipientSetting (uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_7E887280 on NotificationTemplate (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_161194F7 on NotificationTemplate (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_7256D229 on NotificationTemplate (uuid_[$COLUMN_LENGTH:75$]);