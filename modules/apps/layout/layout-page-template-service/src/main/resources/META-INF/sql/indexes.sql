create index IX_9AFE1C5A on LayoutPageTemplateCollection (ctCollectionId, groupId, parentLPTCollectionId);
create unique index IX_857CE0FB on LayoutPageTemplateCollection (ctCollectionId, groupId, type_, lptCollectionKey[$COLUMN_LENGTH:75$]);
create unique index IX_46A9FA81 on LayoutPageTemplateCollection (ctCollectionId, groupId, type_, name[$COLUMN_LENGTH:75$]);
create unique index IX_AE9F033F on LayoutPageTemplateCollection (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_C64219C5 on LayoutPageTemplateCollection (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_7375C41B on LayoutPageTemplateEntry (ctCollectionId, groupId, layoutPageTemplateEntryKey[$COLUMN_LENGTH:75$]);
create index IX_37BC91D7 on LayoutPageTemplateEntry (ctCollectionId, groupId, status, classNameId, classTypeId, defaultTemplate);
create index IX_9922D2CD on LayoutPageTemplateEntry (ctCollectionId, groupId, status, layoutPageTemplateCollectionId);
create index IX_35D22A4C on LayoutPageTemplateEntry (ctCollectionId, groupId, status, name[$COLUMN_LENGTH:75$], layoutPageTemplateCollectionId);
create index IX_B09B44FE on LayoutPageTemplateEntry (ctCollectionId, groupId, type_, classNameId, classTypeId, name[$COLUMN_LENGTH:75$]);
create index IX_263D5AEF on LayoutPageTemplateEntry (ctCollectionId, groupId, type_, classNameId, defaultTemplate);
create index IX_BF0A3D0E on LayoutPageTemplateEntry (ctCollectionId, groupId, type_, layoutPageTemplateCollectionId);
create unique index IX_EFD4408F on LayoutPageTemplateEntry (ctCollectionId, groupId, type_, name[$COLUMN_LENGTH:75$]);
create index IX_67B45E58 on LayoutPageTemplateEntry (ctCollectionId, groupId, type_, status, classNameId, classTypeId, name[$COLUMN_LENGTH:75$]);
create index IX_6020F8ED on LayoutPageTemplateEntry (ctCollectionId, groupId, type_, status, defaultTemplate);
create index IX_28538E9 on LayoutPageTemplateEntry (ctCollectionId, groupId, type_, status, name[$COLUMN_LENGTH:75$]);
create unique index IX_FE426BCD on LayoutPageTemplateEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_E3D19060 on LayoutPageTemplateEntry (ctCollectionId, layoutPrototypeId);
create unique index IX_E4BCB00E on LayoutPageTemplateEntry (ctCollectionId, plid);
create index IX_BAAD96D3 on LayoutPageTemplateEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_40C992B1 on LayoutPageTemplateStructure (ctCollectionId, groupId, plid);
create index IX_33CEF0D4 on LayoutPageTemplateStructure (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_F044520E on LayoutPageTemplateStructure (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_8594A1A7 on LayoutPageTemplateStructureRel (ctCollectionId, segmentsExperienceId, layoutPageTemplateStructureId);
create index IX_B229708D on LayoutPageTemplateStructureRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_987D6C07 on LayoutPageTemplateStructureRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);