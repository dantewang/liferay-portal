create unique index IX_AC81187 on MBBan (ctCollectionId, groupId, banUserId);
create index IX_267FE418 on MBBan (ctCollectionId, userId);
create index IX_7E9D6FEE on MBBan (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_AFBCFA8 on MBBan (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_DC0D1D0D on MBCategory (ctCollectionId, groupId, friendlyURL[$COLUMN_LENGTH:255$]);
create index IX_4C18CC43 on MBCategory (ctCollectionId, groupId, status, parentCategoryId, categoryId);
create unique index IX_F9BF60E1 on MBCategory (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_7C0E7813 on MBCategory (ctCollectionId, status, companyId);
create index IX_CAC196E7 on MBCategory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_F4BC4496 on MBDiscussion (ctCollectionId, classNameId, classPK);
create unique index IX_C88E75BA on MBDiscussion (ctCollectionId, threadId);
create index IX_F1AD8BD1 on MBDiscussion (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_D617884B on MBDiscussion (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_DF91F0AD on MBMailingList (ctCollectionId, active_);
create unique index IX_BB865A05 on MBMailingList (ctCollectionId, groupId, categoryId);
create index IX_569B8DE8 on MBMailingList (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_9DFD2422 on MBMailingList (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_204F5663 on MBMessage (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E3ABB2E6 on MBMessage (ctCollectionId, groupId, status, threadId, categoryId);
create index IX_6AC44C34 on MBMessage (ctCollectionId, groupId, status, userId);
create index IX_2C710C8E on MBMessage (ctCollectionId, groupId, threadId, categoryId, answer);
create unique index IX_AB531905 on MBMessage (ctCollectionId, groupId, urlSubject[$COLUMN_LENGTH:255$]);
create index IX_E58EA74E on MBMessage (ctCollectionId, groupId, userId);
create unique index IX_86E2A820 on MBMessage (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_D86E7474 on MBMessage (ctCollectionId, status, companyId);
create index IX_3A0BE78 on MBMessage (ctCollectionId, status, parentMessageId);
create index IX_F3077AB5 on MBMessage (ctCollectionId, status, threadId);
create index IX_AEF40BCB on MBMessage (ctCollectionId, status, userId, classNameId, classPK);
create index IX_7F01B281 on MBMessage (ctCollectionId, threadId, answer);
create index IX_49109D8B on MBMessage (ctCollectionId, threadId, parentMessageId);
create index IX_F706EDE5 on MBMessage (ctCollectionId, userId, classNameId, classPK);
create index IX_9C300A66 on MBMessage (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_F491DCE7 on MBSuspiciousActivity (ctCollectionId, userId, messageId);
create index IX_51AD3A58 on MBSuspiciousActivity (ctCollectionId, userId, threadId);
create index IX_BE9B5235 on MBSuspiciousActivity (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_3FCB97AF on MBSuspiciousActivity (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_909BB168 on MBThread (ctCollectionId, categoryId, priority);
create index IX_C5DA5D72 on MBThread (ctCollectionId, groupId, categoryId, lastPostDate);
create index IX_9A9349C0 on MBThread (ctCollectionId, groupId, categoryId, status);
create index IX_E8996F8D on MBThread (ctCollectionId, groupId, status);
create unique index IX_399CC82D on MBThread (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_EB120F13 on MBThread (ctCollectionId, lastPostDate, priority);
create index IX_398F0AAD on MBThread (ctCollectionId, rootMessageId);
create index IX_88E25B33 on MBThread (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_E6878962 on MBThreadFlag (ctCollectionId, userId, threadId);
create index IX_5375A13F on MBThreadFlag (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_90222139 on MBThreadFlag (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);