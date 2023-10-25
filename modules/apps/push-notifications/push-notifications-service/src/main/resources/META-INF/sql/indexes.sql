create index IX_A0EEFEB on PushNotificationsDevice (platform[$COLUMN_LENGTH:75$], userId);
create unique index IX_2F3EDC9F on PushNotificationsDevice (token[$COLUMN_LENGTH:4000$]);