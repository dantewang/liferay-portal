create unique index IX_370F2EC6 on SharingEntry (classNameId, toUserId, classPK);
create index IX_8E0359AC on SharingEntry (classNameId, userId);
create index IX_1E35B88D on SharingEntry (expirationDate);
create index IX_C024CFB1 on SharingEntry (toUserId);
create index IX_EA2FF796 on SharingEntry (userId);
create index IX_4F34C4E8 on SharingEntry (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_2C978526 on SharingEntry (uuid_[$COLUMN_LENGTH:75$], groupId);