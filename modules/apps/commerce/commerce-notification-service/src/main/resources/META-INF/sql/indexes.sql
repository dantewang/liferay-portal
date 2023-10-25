create unique index IX_CB3B24DA on CNTemplateCAccountGroupRel (commerceNotificationTemplateId, commerceAccountGroupId);

create index IX_6E9D8183 on CNotificationAttachment (CNotificationQueueEntryId);
create index IX_F4A430E3 on CNotificationAttachment (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_157B3E1 on CNotificationAttachment (uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_F9149FC on CommerceNotificationQueueEntry (commerceNotificationTemplateId);
create index IX_DFF577D4 on CommerceNotificationQueueEntry (groupId, sent, classNameId, classPK);
create index IX_BEFF6FD9 on CommerceNotificationQueueEntry (sent);
create index IX_80026CA7 on CommerceNotificationQueueEntry (sentDate);

create index IX_9BE0AAF6 on CommerceNotificationTemplate (groupId, enabled, type_[$COLUMN_LENGTH:75$]);
create index IX_4A280CF2 on CommerceNotificationTemplate (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_56F147B0 on CommerceNotificationTemplate (uuid_[$COLUMN_LENGTH:75$], groupId);