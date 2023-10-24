create index IX_281A3D07 on MBBan (ctCollectionId, banUserId);
create unique index IX_BBB34999 on MBBan (ctCollectionId, groupId, banUserId);
create index IX_267FE418 on MBBan (ctCollectionId, userId);
create index IX_BE1E5052 on MBBan (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_3D42C354 on MBBan (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_8A6ECF43 on MBCategory (ctCollectionId, categoryId, groupId, parentCategoryId, status);
create index IX_B75C5097 on MBCategory (ctCollectionId, companyId, status);
create unique index IX_BAE05D61 on MBCategory (ctCollectionId, groupId, friendlyURL[$COLUMN_LENGTH:255$]);
create index IX_4069CAD0 on MBCategory (ctCollectionId, groupId, parentCategoryId, status);
create index IX_2CC9ED59 on MBCategory (ctCollectionId, groupId, status);
create index IX_A42774B on MBCategory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_2C06548D on MBCategory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_4B5416 on MBDiscussion (ctCollectionId, classNameId, classPK);
create unique index IX_C88E75BA on MBDiscussion (ctCollectionId, threadId);
create index IX_312E6C35 on MBDiscussion (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_85E7BF7 on MBDiscussion (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_E449BFB1 on MBMailingList (ctCollectionId, active_);
create unique index IX_4252C13F on MBMailingList (ctCollectionId, groupId, categoryId);
create index IX_961C6E4C on MBMailingList (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_D04417CE on MBMailingList (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_86CF6411 on MBMessage (ctCollectionId, classNameId, classPK, status);
create index IX_13BC4CF8 on MBMessage (ctCollectionId, companyId, status);
create unique index IX_204F5663 on MBMessage (ctCollectionId, externalReferenceCode[$COLUMN_LENGTH:75$], groupId);
create index IX_788CB9E7 on MBMessage (ctCollectionId, groupId, categoryId, status);
create index IX_23C18AEC on MBMessage (ctCollectionId, groupId, categoryId, threadId, answer);
create index IX_9021DE20 on MBMessage (ctCollectionId, groupId, categoryId, threadId, status);
create index IX_440F8DFA on MBMessage (ctCollectionId, groupId, status);
create unique index IX_AB531905 on MBMessage (ctCollectionId, groupId, urlSubject[$COLUMN_LENGTH:255$]);
create index IX_D10FB834 on MBMessage (ctCollectionId, groupId, userId, status);
create index IX_3A0BE78 on MBMessage (ctCollectionId, parentMessageId, status);
create index IX_21628601 on MBMessage (ctCollectionId, threadId, answer);
create index IX_38409BB9 on MBMessage (ctCollectionId, threadId, parentMessageId);
create index IX_8DC2D935 on MBMessage (ctCollectionId, threadId, status);
create index IX_2CDE4CB on MBMessage (ctCollectionId, userId, classNameId, classPK, status);
create index IX_6956CFA4 on MBMessage (ctCollectionId, userId, classNameId, status);
create index IX_DBB0EACA on MBMessage (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_B9299BCC on MBMessage (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_22A681AD on MBSuspiciousActivity (ctCollectionId, messageId);
create index IX_50CAE01E on MBSuspiciousActivity (ctCollectionId, threadId);
create index IX_9B8C7633 on MBSuspiciousActivity (ctCollectionId, userId, messageId);
create index IX_54B142D8 on MBSuspiciousActivity (ctCollectionId, userId, threadId);
create index IX_FE1C3299 on MBSuspiciousActivity (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_72128B5B on MBSuspiciousActivity (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_8AC04E68 on MBThread (ctCollectionId, categoryId, priority);
create index IX_1B1C972C on MBThread (ctCollectionId, groupId, categoryId, lastPostDate);
create index IX_B589D6FA on MBThread (ctCollectionId, groupId, categoryId, status);
create index IX_E8996F8D on MBThread (ctCollectionId, groupId, status);
create index IX_EB120F13 on MBThread (ctCollectionId, lastPostDate, priority);
create index IX_398F0AAD on MBThread (ctCollectionId, rootMessageId);
create index IX_C8633B97 on MBThread (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_6BE3BBD9 on MBThread (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_2101D928 on MBThreadFlag (ctCollectionId, threadId);
create unique index IX_E98B91E2 on MBThreadFlag (ctCollectionId, userId, threadId);
create index IX_92F681A3 on MBThreadFlag (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_C26914E5 on MBThreadFlag (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);