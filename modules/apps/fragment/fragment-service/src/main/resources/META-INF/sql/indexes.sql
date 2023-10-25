create unique index IX_D2DCE0BB on FragmentCollection (ctCollectionId, groupId, fragmentCollectionKey[$COLUMN_LENGTH:75$]);
create index IX_DF515AD7 on FragmentCollection (ctCollectionId, groupId, name[$COLUMN_LENGTH:75$]);
create unique index IX_9F0E819C on FragmentCollection (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_58CB74E2 on FragmentCollection (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_B48741B4 on FragmentComposition (ctCollectionId, groupId, fragmentCollectionId, status, name[$COLUMN_LENGTH:75$]);
create unique index IX_55862D3 on FragmentComposition (ctCollectionId, groupId, fragmentCompositionKey[$COLUMN_LENGTH:75$]);
create unique index IX_4F880688 on FragmentComposition (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_D0F76ECE on FragmentComposition (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_E52EB5EC on FragmentEntry (ctCollectionId, groupId, fragmentCollectionId, status, name[$COLUMN_LENGTH:75$]);
create index IX_F4272EC8 on FragmentEntry (ctCollectionId, groupId, fragmentCollectionId, type_, status);
create index IX_DD670380 on FragmentEntry (ctCollectionId, groupId, head, fragmentCollectionId, status, name[$COLUMN_LENGTH:75$]);
create index IX_2F893B4 on FragmentEntry (ctCollectionId, groupId, head, fragmentCollectionId, type_, status);
create unique index IX_4DC3D967 on FragmentEntry (ctCollectionId, groupId, head, fragmentEntryKey[$COLUMN_LENGTH:75$]);
create unique index IX_D3B6E9FC on FragmentEntry (ctCollectionId, groupId, head, uuid_[$COLUMN_LENGTH:75$]);
create index IX_A04AA750 on FragmentEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_78F45D17 on FragmentEntry (ctCollectionId, head, fragmentCollectionId);
create index IX_B3F709BB on FragmentEntry (ctCollectionId, head, type_);
create index IX_DE5CF3C2 on FragmentEntry (ctCollectionId, head, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_25B5B355 on FragmentEntry (ctCollectionId, headId);
create index IX_2B5A558F on FragmentEntry (ctCollectionId, type_);
create index IX_FB94FD96 on FragmentEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_C40BEE4 on FragmentEntryLink (ctCollectionId, fragmentEntryId, deleted);
create index IX_BB896D52 on FragmentEntryLink (ctCollectionId, groupId, fragmentEntryId, classNameId, classPK);
create index IX_6AE95F48 on FragmentEntryLink (ctCollectionId, groupId, plid, fragmentEntryId);
create index IX_981D0577 on FragmentEntryLink (ctCollectionId, groupId, plid, originalFragmentEntryLinkId);
create index IX_70FF305 on FragmentEntryLink (ctCollectionId, groupId, plid, segmentsExperienceId, deleted);
create index IX_507E53F0 on FragmentEntryLink (ctCollectionId, groupId, plid, segmentsExperienceId, rendererKey[$COLUMN_LENGTH:200$]);
create index IX_1123CC2E on FragmentEntryLink (ctCollectionId, groupId, segmentsExperienceId, classNameId, classPK);
create unique index IX_B046C3EA on FragmentEntryLink (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_B49818C8 on FragmentEntryLink (ctCollectionId, rendererKey[$COLUMN_LENGTH:200$], companyId);
create index IX_FCFC5BB0 on FragmentEntryLink (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_F92D1BB1 on FragmentEntryVersion (ctCollectionId, version, fragmentCollectionId);
create unique index IX_8B08D4B5 on FragmentEntryVersion (ctCollectionId, version, fragmentEntryId);
create index IX_A25C0276 on FragmentEntryVersion (ctCollectionId, version, groupId, fragmentCollectionId, name[$COLUMN_LENGTH:75$]);
create index IX_6A5AD850 on FragmentEntryVersion (ctCollectionId, version, groupId, fragmentCollectionId, status, name[$COLUMN_LENGTH:75$]);
create index IX_679A100C on FragmentEntryVersion (ctCollectionId, version, groupId, fragmentCollectionId, status, type_);
create index IX_E51E6026 on FragmentEntryVersion (ctCollectionId, version, groupId, fragmentCollectionId, type_);
create unique index IX_97F36549 on FragmentEntryVersion (ctCollectionId, version, groupId, fragmentEntryKey[$COLUMN_LENGTH:75$]);
create unique index IX_CFBD1D84 on FragmentEntryVersion (ctCollectionId, version, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_39DF070D on FragmentEntryVersion (ctCollectionId, version, type_);
create index IX_621672FE on FragmentEntryVersion (ctCollectionId, version, uuid_[$COLUMN_LENGTH:75$], companyId);