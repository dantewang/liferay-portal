create index IX_5B76D798 on DepotAppCustomization (depotEntryId, enabled);
create unique index IX_DA8D9ACC on DepotAppCustomization (depotEntryId, portletId[$COLUMN_LENGTH:75$]);

create unique index IX_884D6226 on DepotEntry (groupId);
create index IX_63722690 on DepotEntry (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_F329B161 on DepotEntryGroupRel (toGroupId, ddmStructuresAvailable);
create unique index IX_65D34444 on DepotEntryGroupRel (toGroupId, depotEntryId);
create index IX_C61C803B on DepotEntryGroupRel (toGroupId, searchable);
create index IX_E66A114A on DepotEntryGroupRel (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_A0188208 on DepotEntryGroupRel (uuid_[$COLUMN_LENGTH:75$], groupId);