create unique index IX_FBFAF640 on AccountEntry (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_48CB043 on AccountEntry (companyId, status);
create index IX_3A8398B7 on AccountEntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_ECAD14C4 on AccountEntry (type_[$COLUMN_LENGTH:75$], userId);
create index IX_6901A669 on AccountEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_EC6CC41D on AccountEntryOrganizationRel (organizationId, accountEntryId);

create index IX_ED720A80 on AccountEntryUserRel (accountUserId, accountEntryId);

create index IX_38BDB33 on AccountGroup (companyId, defaultAccountGroup);
create unique index IX_F7BFA1CD on AccountGroup (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8EE6A92F on AccountGroup (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_B4733E65 on AccountGroup (companyId, type_[$COLUMN_LENGTH:75$]);
create index IX_E222FE44 on AccountGroup (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_E86A36FC on AccountGroup (uuid_[$COLUMN_LENGTH:75$]);

create index IX_448835E3 on AccountGroupRel (classNameId, classPK, accountGroupId);

create index IX_9BCBCB2B on AccountRole (companyId, accountEntryId);
create index IX_714A358E on AccountRole (roleId);