create index IX_A06B42EC on SiteNavigationMenu (ctCollectionId, groupId, auto_);
create unique index IX_5AA0FCAB on SiteNavigationMenu (ctCollectionId, groupId, name[$COLUMN_LENGTH:75$]);
create index IX_60035A69 on SiteNavigationMenu (ctCollectionId, groupId, type_);
create unique index IX_8DB31A48 on SiteNavigationMenu (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_30AC928E on SiteNavigationMenu (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_BABBD7FE on SiteNavigationMenuItem (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_16B5119 on SiteNavigationMenuItem (ctCollectionId, siteNavigationMenuId, name[$COLUMN_LENGTH:255$]);
create index IX_4AB1A780 on SiteNavigationMenuItem (ctCollectionId, siteNavigationMenuId, parentSiteNavigationMenuItemId);
create index IX_5AE9A741 on SiteNavigationMenuItem (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_6983BFBB on SiteNavigationMenuItem (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);