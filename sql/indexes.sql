create index IX_E29C5F1E on Address (ctCollectionId, companyId, classNameId, classPK, listTypeId);
create index IX_B9A42164 on Address (ctCollectionId, companyId, classNameId, classPK, mailing);
create index IX_57C88948 on Address (ctCollectionId, companyId, classNameId, classPK, primary_);
create unique index IX_FE3E411 on Address (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_44FC4888 on Address (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_34CBC45 on Address (ctCollectionId, countryId);
create index IX_4E016D5B on Address (ctCollectionId, regionId);
create index IX_19DD532 on Address (ctCollectionId, userId);
create index IX_6F042DBC on Address (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_37B0A8A2 on AnnouncementsDelivery (companyId);
create unique index IX_1664F489 on AnnouncementsDelivery (userId, type_[$COLUMN_LENGTH:75$]);

create index IX_AAE413 on AnnouncementsEntry (ctCollectionId, classNameId, classPK, alert);
create index IX_3A749B1 on AnnouncementsEntry (ctCollectionId, classNameId, classPK, companyId, alert);
create index IX_D179139A on AnnouncementsEntry (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_66B7EEC4 on AnnouncementsEntry (ctCollectionId, userId);
create index IX_B45799EA on AnnouncementsEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_F6244488 on AnnouncementsFlag (ctCollectionId, companyId);
create index IX_98EBC546 on AnnouncementsFlag (ctCollectionId, entryId, userId, value);

create unique index IX_48FD4E9F on AssetCategory (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_A1A0985C on AssetCategory (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_8D75762C on AssetCategory (ctCollectionId, groupId, vocabularyId, name[$COLUMN_LENGTH:255$]);
create index IX_E8F98BA4 on AssetCategory (ctCollectionId, groupId, vocabularyId, parentCategoryId);
create index IX_FF32DBA2 on AssetCategory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_C98CBE1D on AssetCategory (ctCollectionId, vocabularyId, parentCategoryId, name[$COLUMN_LENGTH:255$]);

create index IX_112337B8 on AssetEntries_AssetTags (companyId);
create index IX_B2A61B55 on AssetEntries_AssetTags (tagId);

create unique index IX_7BF8337B on AssetEntry (ctCollectionId, classNameId, classPK);
create index IX_25F682BE on AssetEntry (ctCollectionId, companyId);
create index IX_2AB3FA57 on AssetEntry (ctCollectionId, expirationDate);
create index IX_57CE6B38 on AssetEntry (ctCollectionId, groupId, classNameId, expirationDate, publishDate);
create index IX_F4F8029C on AssetEntry (ctCollectionId, groupId, classNameId, visible);
create index IX_82B5BBF1 on AssetEntry (ctCollectionId, groupId, classUuid[$COLUMN_LENGTH:75$]);
create index IX_D0744B5F on AssetEntry (ctCollectionId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_9293BAE7 on AssetEntry (ctCollectionId, publishDate);
create index IX_21C3E4BC on AssetEntry (ctCollectionId, visible);

create unique index IX_3A62E4DB on AssetTag (ctCollectionId, groupId, name[$COLUMN_LENGTH:75$]);
create index IX_FEBF6F8D on AssetTag (ctCollectionId, name[$COLUMN_LENGTH:75$]);
create index IX_16D77C5E on AssetTag (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_A62E3818 on AssetTag (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_35E71C87 on AssetVocabulary (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_3EAF712F on AssetVocabulary (ctCollectionId, groupId, name[$COLUMN_LENGTH:75$]);
create unique index IX_2B733644 on AssetVocabulary (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_193E3950 on AssetVocabulary (ctCollectionId, groupId, visibilityType);
create index IX_5ED59F8A on AssetVocabulary (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_E7B95510 on BrowserTracker (userId);

create unique index IX_B27A301F on ClassName_ (value[$COLUMN_LENGTH:200$]);

create index IX_38EFE3FD on Company (logoId);
create index IX_12566EC2 on Company (mx[$COLUMN_LENGTH:200$]);
create unique index IX_EC00543C on Company (webId[$COLUMN_LENGTH:75$]);

create unique index IX_85C63FD7 on CompanyInfo (companyId);

create index IX_791914FA on Contact_ (classNameId, classPK);
create index IX_FD2E9BDD on Contact_ (userId, companyId);

create index IX_4660D12B on Country (ctCollectionId, active_);
create unique index IX_ED9D0E05 on Country (ctCollectionId, companyId, a2[$COLUMN_LENGTH:75$]);
create unique index IX_EE49B9A4 on Country (ctCollectionId, companyId, a3[$COLUMN_LENGTH:75$]);
create index IX_54A8A218 on Country (ctCollectionId, companyId, active_, billingAllowed);
create index IX_17AA5651 on Country (ctCollectionId, companyId, active_, shippingAllowed);
create unique index IX_B2A91789 on Country (ctCollectionId, companyId, name[$COLUMN_LENGTH:75$]);
create unique index IX_74AB3DC on Country (ctCollectionId, companyId, number_[$COLUMN_LENGTH:75$]);
create index IX_36AE5B2A on Country (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_B9A36C5A on Country (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_66992A11 on CountryLocalization (countryId, ctCollectionId, languageId[$COLUMN_LENGTH:75$]);

create index IX_D8A2682E on DLFileEntry (ctCollectionId, folderId, name[$COLUMN_LENGTH:255$]);
create index IX_BE447028 on DLFileEntry (ctCollectionId, folderId, repositoryId);
create unique index IX_CB98B35F on DLFileEntry (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_FD628084 on DLFileEntry (ctCollectionId, groupId, folderId, fileEntryTypeId);
create unique index IX_A7B54120 on DLFileEntry (ctCollectionId, groupId, folderId, fileName[$COLUMN_LENGTH:255$]);
create unique index IX_2687115A on DLFileEntry (ctCollectionId, groupId, folderId, name[$COLUMN_LENGTH:255$]);
create unique index IX_F5CDF4CD on DLFileEntry (ctCollectionId, groupId, folderId, title[$COLUMN_LENGTH:255$]);
create index IX_23801F15 on DLFileEntry (ctCollectionId, groupId, folderId, userId);
create index IX_20AE71D2 on DLFileEntry (ctCollectionId, groupId, userId);
create unique index IX_D31D7D1C on DLFileEntry (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_F503BDD4 on DLFileEntry (ctCollectionId, mimeType[$COLUMN_LENGTH:75$]);
create index IX_47145E8B on DLFileEntry (ctCollectionId, repositoryId);
create index IX_FA614097 on DLFileEntry (ctCollectionId, smallImageId, largeImageId, custom2ImageId, custom1ImageId);
create index IX_C5099062 on DLFileEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_210396C0 on DLFileEntryMetadata (ctCollectionId, fileEntryId);
create unique index IX_8544C631 on DLFileEntryMetadata (ctCollectionId, fileVersionId, DDMStructureId);
create index IX_A66AA631 on DLFileEntryMetadata (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_223F9FF2 on DLFileEntryType (ctCollectionId, groupId, dataDefinitionId);
create unique index IX_14CEEA9B on DLFileEntryType (ctCollectionId, groupId, fileEntryTypeKey[$COLUMN_LENGTH:75$]);
create unique index IX_B35DE2F6 on DLFileEntryType (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_96C7E7BC on DLFileEntryType (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_2E64D9F9 on DLFileEntryTypes_DLFolders (companyId);
create index IX_6E00A2EC on DLFileEntryTypes_DLFolders (folderId);

create index IX_BBBF7D9C on DLFileShortcut (ctCollectionId, companyId, status);
create index IX_8617785C on DLFileShortcut (ctCollectionId, groupId, status, folderId, active_);
create unique index IX_790A6FF8 on DLFileShortcut (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_3845C9D8 on DLFileShortcut (ctCollectionId, toFileEntryId);
create index IX_A3853C3E on DLFileShortcut (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_BC184052 on DLFileVersion (ctCollectionId, companyId, status);
create index IX_96FF13E0 on DLFileVersion (ctCollectionId, companyId, storeUUID[$COLUMN_LENGTH:255$]);
create index IX_BC4E4AC8 on DLFileVersion (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_B48BF85F on DLFileVersion (ctCollectionId, fileEntryId, version[$COLUMN_LENGTH:75$]);
create index IX_D02B3635 on DLFileVersion (ctCollectionId, groupId, folderId, version[$COLUMN_LENGTH:75$], title[$COLUMN_LENGTH:255$]);
create index IX_28B09E1B on DLFileVersion (ctCollectionId, groupId, status, folderId);
create unique index IX_D0F99902 on DLFileVersion (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_21843E3A on DLFileVersion (ctCollectionId, mimeType[$COLUMN_LENGTH:75$]);
create index IX_88AD95AF on DLFileVersion (ctCollectionId, status, fileEntryId);
create index IX_CEDD137C on DLFileVersion (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_465073C7 on DLFolder (ctCollectionId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_A9E8A996 on DLFolder (ctCollectionId, groupId, parentFolderId, name[$COLUMN_LENGTH:255$]);
create index IX_A49D1C42 on DLFolder (ctCollectionId, groupId, parentFolderId, status, hidden_);
create index IX_FE31456D on DLFolder (ctCollectionId, groupId, parentFolderId, status, mountPoint, hidden_);
create unique index IX_45F30D84 on DLFolder (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_945F7FA0 on DLFolder (ctCollectionId, parentFolderId, name[$COLUMN_LENGTH:255$]);
create index IX_43197A5A on DLFolder (ctCollectionId, parentFolderId, repositoryId);
create index IX_2806BC5E on DLFolder (ctCollectionId, repositoryId, mountPoint);
create index IX_2D756490 on DLFolder (ctCollectionId, status, companyId);
create index IX_D8BCA6CA on DLFolder (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_ADCBE6C4 on EmailAddress (ctCollectionId, companyId, classNameId, classPK, primary_);
create index IX_CB95178C on EmailAddress (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_9F98D936 on EmailAddress (ctCollectionId, userId);
create index IX_5B569938 on EmailAddress (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_B3B757AB on ExpandoColumn (ctCollectionId, tableId, name[$COLUMN_LENGTH:75$]);

create unique index IX_677EA7C3 on ExpandoRow (ctCollectionId, tableId, classPK);

create unique index IX_F10FB956 on ExpandoTable (classNameId, companyId, ctCollectionId, name[$COLUMN_LENGTH:75$]);

create index IX_FF8FB775 on ExpandoValue (ctCollectionId, classPK, classNameId);
create unique index IX_4562CC43 on ExpandoValue (ctCollectionId, columnId, rowId_);
create unique index IX_68DBF435 on ExpandoValue (ctCollectionId, tableId, columnId, classPK);
create index IX_86F89C3B on ExpandoValue (ctCollectionId, tableId, rowId_);

create index IX_1827A2E5 on ExportImportConfiguration (companyId);
create index IX_F8451AA8 on ExportImportConfiguration (groupId, type_, status);

create index IX_25BE34 on Group_ (ctCollectionId, active_, type_);
create index IX_EB3A63D9 on Group_ (ctCollectionId, classNameId, classPK);
create unique index IX_4E28522B on Group_ (ctCollectionId, companyId, classNameId, classPK);
create index IX_5A0F50C2 on Group_ (ctCollectionId, companyId, classNameId, parentGroupId);
create index IX_BDE14B19 on Group_ (ctCollectionId, companyId, classNameId, site);
create unique index IX_23B1C81D on Group_ (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_3551EED4 on Group_ (ctCollectionId, companyId, friendlyURL[$COLUMN_LENGTH:255$]);
create unique index IX_42E6E774 on Group_ (ctCollectionId, companyId, groupKey[$COLUMN_LENGTH:150$]);
create index IX_E27B3BBE on Group_ (ctCollectionId, companyId, site, active_);
create index IX_CF2ABB49 on Group_ (ctCollectionId, companyId, site, parentGroupId, inheritContent);
create index IX_C74BC494 on Group_ (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_639B089A on Group_ (ctCollectionId, liveGroupId);
create index IX_4609C130 on Group_ (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_8BFD4548 on Groups_Orgs (companyId);
create index IX_6BBB7682 on Groups_Orgs (organizationId);

create index IX_557D8550 on Groups_Roles (companyId);
create index IX_3103EF3D on Groups_Roles (roleId);

create index IX_676FC818 on Groups_UserGroups (companyId);
create index IX_3B69160F on Groups_UserGroups (userGroupId);

create index IX_C4C6202F on Image (ctCollectionId, size_);

create index IX_31B45343 on Layout (ctCollectionId, classNameId, classPK);
create index IX_E5284C2D on Layout (ctCollectionId, companyId, layoutPrototypeUuid[$COLUMN_LENGTH:75$]);
create index IX_863F913 on Layout (ctCollectionId, groupId, masterLayoutPlid);
create unique index IX_1BDF8FD9 on Layout (ctCollectionId, groupId, privateLayout, friendlyURL[$COLUMN_LENGTH:255$]);
create unique index IX_79940548 on Layout (ctCollectionId, groupId, privateLayout, layoutId);
create index IX_9B24231 on Layout (ctCollectionId, groupId, privateLayout, parentLayoutId, hidden_);
create index IX_52538A66 on Layout (ctCollectionId, groupId, privateLayout, parentLayoutId, priority);
create index IX_87645EC2 on Layout (ctCollectionId, groupId, privateLayout, parentLayoutId, system_);
create index IX_1C59F9BB on Layout (ctCollectionId, groupId, privateLayout, sourcePrototypeLayoutUuid[$COLUMN_LENGTH:75$]);
create index IX_DF8D04E7 on Layout (ctCollectionId, groupId, privateLayout, status);
create index IX_13E45FB4 on Layout (ctCollectionId, groupId, privateLayout, type_[$COLUMN_LENGTH:75$]);
create unique index IX_41941F93 on Layout (ctCollectionId, groupId, privateLayout, uuid_[$COLUMN_LENGTH:75$]);
create index IX_49879CD9 on Layout (ctCollectionId, groupId, type_[$COLUMN_LENGTH:75$]);
create index IX_66DA5035 on Layout (ctCollectionId, layoutPrototypeUuid[$COLUMN_LENGTH:75$]);
create index IX_2400E403 on Layout (ctCollectionId, parentPlid);
create index IX_25B34C3E on Layout (ctCollectionId, privateLayout, iconImageId);
create index IX_A8C8CDAE on Layout (ctCollectionId, sourcePrototypeLayoutUuid[$COLUMN_LENGTH:75$]);
create index IX_CA29F8FE on Layout (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_6B1DA214 on LayoutBranch (plid, layoutSetBranchId, master);
create unique index IX_F18D227D on LayoutBranch (plid, layoutSetBranchId, name[$COLUMN_LENGTH:75$]);

create unique index IX_4CA878E2 on LayoutFriendlyURL (ctCollectionId, friendlyURL[$COLUMN_LENGTH:255$], groupId, languageId[$COLUMN_LENGTH:75$], privateLayout);
create index IX_D5DA2209 on LayoutFriendlyURL (ctCollectionId, friendlyURL[$COLUMN_LENGTH:255$], groupId, privateLayout);
create index IX_1B8DB7C1 on LayoutFriendlyURL (ctCollectionId, friendlyURL[$COLUMN_LENGTH:255$], plid);
create unique index IX_F4CFCC88 on LayoutFriendlyURL (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_5B573090 on LayoutFriendlyURL (ctCollectionId, plid, languageId[$COLUMN_LENGTH:75$]);
create index IX_4365B4CE on LayoutFriendlyURL (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_8E678D9B on LayoutPrototype (ctCollectionId, companyId, active_);
create index IX_731B43AC on LayoutPrototype (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_5F2C118 on LayoutPrototype (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_31D32CEC on LayoutRevision (layoutSetBranchId, status, head);
create index IX_43E8286A on LayoutRevision (plid, head);
create index IX_601358D6 on LayoutRevision (plid, layoutSetBranchId, head, layoutBranchId);
create index IX_CE51DFEA on LayoutRevision (plid, layoutSetBranchId, layoutBranchId);
create index IX_4A84AF43 on LayoutRevision (plid, layoutSetBranchId, parentLayoutRevisionId);
create index IX_70DA9ECB on LayoutRevision (plid, layoutSetBranchId, status);
create index IX_8EC3D2BC on LayoutRevision (plid, status);
create index IX_421223B1 on LayoutRevision (status);

create index IX_1B012981 on LayoutSet (ctCollectionId, layoutSetPrototypeUuid[$COLUMN_LENGTH:75$], companyId);
create unique index IX_257D66EF on LayoutSet (ctCollectionId, privateLayout, groupId);
create index IX_5898C647 on LayoutSet (ctCollectionId, privateLayout, logoId);

create index IX_6C5395BD on LayoutSetBranch (groupId, privateLayout, master);
create unique index IX_CCAA41B4 on LayoutSetBranch (groupId, privateLayout, name[$COLUMN_LENGTH:75$]);

create index IX_56436A0F on LayoutSetPrototype (companyId, active_);
create index IX_4191F11C on LayoutSetPrototype (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_C5D69B24 on LayoutSetPrototype (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_B7F62EEC on ListType (companyId, type_[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$]);

create index IX_C8CE1F26 on MembershipRequest (userId, groupId, statusId);

create index IX_6AF0D434 on OrgLabor (organizationId);

create unique index IX_87E47DA9 on Organization_ (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_7636A9D3 on Organization_ (ctCollectionId, companyId, name[$COLUMN_LENGTH:100$]);
create index IX_CEDBB500 on Organization_ (ctCollectionId, companyId, parentOrganizationId);
create index IX_E4D31220 on Organization_ (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_A68B7324 on Organization_ (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_2C1142E on PasswordPolicy (companyId, defaultPolicy);
create unique index IX_3FBFA9F4 on PasswordPolicy (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_4C6A161F on PasswordPolicy (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_51437A01 on PasswordPolicy (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_C3A17327 on PasswordPolicyRel (classNameId, classPK);
create index IX_CD25266E on PasswordPolicyRel (passwordPolicyId);

create index IX_326F75BD on PasswordTracker (userId);

create index IX_85A8420E on Phone (ctCollectionId, companyId, classNameId, classPK, primary_);
create index IX_A866AD02 on Phone (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_F8AAAA2C on Phone (ctCollectionId, userId);
create index IX_34EBD182 on Phone (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_7171B2E8 on PluginSetting (companyId, pluginId[$COLUMN_LENGTH:75$], pluginType[$COLUMN_LENGTH:75$]);

create unique index IX_E8977BB1 on PortalPreferenceValue (portalPreferencesId, namespace[$COLUMN_LENGTH:255$], key_[$COLUMN_LENGTH:255$], index_);
create index IX_4CDC3F0E on PortalPreferenceValue (portalPreferencesId, namespace[$COLUMN_LENGTH:255$], key_[$COLUMN_LENGTH:255$], smallValue[$COLUMN_LENGTH:255$]);

create index IX_D1F795F1 on PortalPreferences (ownerId, ownerType);

create unique index IX_12B5E51D on Portlet (companyId, portletId[$COLUMN_LENGTH:200$]);

create index IX_57B10E77 on PortletItem (classNameId, groupId, portletId[$COLUMN_LENGTH:200$], name[$COLUMN_LENGTH:75$]);

create unique index IX_D5CCA04D on PortletPreferenceValue (ctCollectionId, name[$COLUMN_LENGTH:255$], portletPreferencesId, index_);
create index IX_26D29C6A on PortletPreferenceValue (ctCollectionId, name[$COLUMN_LENGTH:255$], portletPreferencesId, smallValue[$COLUMN_LENGTH:255$]);
create index IX_3C8C941 on PortletPreferenceValue (ctCollectionId, name[$COLUMN_LENGTH:255$], smallValue[$COLUMN_LENGTH:255$], companyId);
create index IX_8947AF4D on PortletPreferenceValue (ctCollectionId, portletPreferencesId);

create index IX_78D3724 on PortletPreferences (ctCollectionId, portletId[$COLUMN_LENGTH:200$], ownerType, ownerId, companyId);
create unique index IX_A37459 on PortletPreferences (ctCollectionId, portletId[$COLUMN_LENGTH:200$], ownerType, plid, ownerId);
create index IX_2D4754D8 on PortletPreferences (ctCollectionId, portletId[$COLUMN_LENGTH:200$], plid);

create index IX_A5AAFD6D on RatingsEntry (ctCollectionId, classNameId, classPK, score);
create unique index IX_1CBE68EF on RatingsEntry (ctCollectionId, classNameId, classPK, userId);
create index IX_71504470 on RatingsEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_C286E0E2 on RatingsStats (classNameId, classPK, ctCollectionId);

create index IX_B91F79BD on RecentLayoutBranch (groupId);
create index IX_351E86E8 on RecentLayoutBranch (layoutBranchId);
create unique index IX_4D20EE9D on RecentLayoutBranch (userId, layoutSetBranchId, plid);

create index IX_8D8A2724 on RecentLayoutRevision (groupId);
create index IX_DA0788DA on RecentLayoutRevision (layoutRevisionId);
create unique index IX_D7039704 on RecentLayoutRevision (userId, layoutSetBranchId, plid);

create index IX_711995A5 on RecentLayoutSetBranch (groupId);
create index IX_23FF0700 on RecentLayoutSetBranch (layoutSetBranchId);
create unique index IX_35FAA138 on RecentLayoutSetBranch (userId, layoutSetId);

create index IX_B6DACF50 on Region (ctCollectionId, countryId, active_);
create unique index IX_6B866ABA on Region (ctCollectionId, countryId, regionCode[$COLUMN_LENGTH:75$]);
create index IX_950A87C8 on Region (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_2BC09BDB on RegionLocalization (ctCollectionId, regionId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_8BD6BCA7 on Release_ (servletContextName[$COLUMN_LENGTH:75$]);

create unique index IX_CCA8802A on Repository (ctCollectionId, groupId, name[$COLUMN_LENGTH:200$], portletId[$COLUMN_LENGTH:200$]);
create index IX_F2D5391E on Repository (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_DBF424D8 on Repository (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_5D8C64A7 on RepositoryEntry (ctCollectionId, repositoryId, mappedId[$COLUMN_LENGTH:255$]);
create index IX_7C6D6DDC on RepositoryEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_3AEE7116 on RepositoryEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_D35C2B6E on ResourceAction (name[$COLUMN_LENGTH:255$], actionId[$COLUMN_LENGTH:75$]);

create index IX_EF29D480 on ResourcePermission (ctCollectionId, name[$COLUMN_LENGTH:255$]);
create index IX_4E7CF7E6 on ResourcePermission (ctCollectionId, roleId);
create index IX_62BB061D on ResourcePermission (ctCollectionId, scope, companyId, name[$COLUMN_LENGTH:255$], primKey[$COLUMN_LENGTH:255$]);
create unique index IX_BD2E1218 on ResourcePermission (ctCollectionId, scope, companyId, name[$COLUMN_LENGTH:255$], roleId, primKey[$COLUMN_LENGTH:255$]);
create index IX_4A5540E7 on ResourcePermission (ctCollectionId, scope, companyId, name[$COLUMN_LENGTH:255$], roleId, primKeyId, viewActionId);
create index IX_81BF829E on ResourcePermission (ctCollectionId, scope, companyId, primKey[$COLUMN_LENGTH:255$]);

create unique index IX_BED262E2 on Role_ (ctCollectionId, companyId, classNameId, classPK);
create unique index IX_D11C3796 on Role_ (ctCollectionId, companyId, name[$COLUMN_LENGTH:75$]);
create index IX_B8EF7CDE on Role_ (ctCollectionId, companyId, type_);
create index IX_E69F3CBD on Role_ (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_38D5866C on Role_ (ctCollectionId, name[$COLUMN_LENGTH:75$]);
create index IX_BAAA2D8 on Role_ (ctCollectionId, type_, subtype[$COLUMN_LENGTH:75$]);
create index IX_760FC8A7 on Role_ (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4F0315B8 on ServiceComponent (buildNamespace[$COLUMN_LENGTH:75$], buildNumber);

create index IX_9E7AC81A on SocialActivity (ctCollectionId, activitySetId);
create unique index IX_9EEBFBAF on SocialActivity (ctCollectionId, classNameId, classPK, groupId, receiverUserId, type_, userId, createDate);
create index IX_DA3C38D2 on SocialActivity (ctCollectionId, classNameId, classPK, mirrorActivityId);
create index IX_D99F3480 on SocialActivity (ctCollectionId, classNameId, classPK, type_);
create index IX_5AD306C4 on SocialActivity (ctCollectionId, companyId);
create index IX_2E5C5BCA on SocialActivity (ctCollectionId, groupId);
create index IX_7EAE93D on SocialActivity (ctCollectionId, mirrorActivityId);
create index IX_C81BA29 on SocialActivity (ctCollectionId, receiverUserId);
create index IX_A1DD0D1A on SocialActivity (ctCollectionId, userId);

create index IX_F702158F on SocialActivityAchievement (ctCollectionId, groupId, userId, firstInGroup);
create unique index IX_9433630C on SocialActivityAchievement (ctCollectionId, groupId, userId, name[$COLUMN_LENGTH:75$]);

create unique index IX_EF48F1A7 on SocialActivityCounter (ctCollectionId, groupId, classNameId, classPK, ownerType, name[$COLUMN_LENGTH:75$], endPeriod);
create unique index IX_F8F39B86 on SocialActivityCounter (ctCollectionId, groupId, classNameId, classPK, ownerType, name[$COLUMN_LENGTH:75$], startPeriod);

create unique index IX_DE4FFD31 on SocialActivityLimit (ctCollectionId, classNameId, classPK, groupId, userId, activityCounterName[$COLUMN_LENGTH:75$], activityType);
create index IX_9E0C5847 on SocialActivityLimit (ctCollectionId, groupId);
create index IX_7C2D0CFD on SocialActivityLimit (ctCollectionId, userId);

create index IX_C4268230 on SocialActivitySet (ctCollectionId, userId, type_, classNameId, classPK);
create index IX_BD79AF05 on SocialActivitySet (ctCollectionId, userId, type_, classNameId, groupId);
create index IX_DD1E56CF on SocialActivitySet (ctCollectionId, userId, type_, groupId);

create index IX_A4ACE614 on SocialActivitySetting (ctCollectionId, groupId, activityType, classNameId, name[$COLUMN_LENGTH:75$]);
create index IX_5F09DAB8 on SocialActivitySetting (ctCollectionId, groupId, classNameId);

create index IX_A32092DD on SocialRelation (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_7570D2FE on SocialRelation (ctCollectionId, type_, companyId);
create unique index IX_4D353EC9 on SocialRelation (ctCollectionId, userId2, type_, userId1);
create index IX_3D3A436E on SocialRelation (ctCollectionId, userId2, userId1);
create index IX_19070687 on SocialRelation (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_9FC9022B on SocialRequest (ctCollectionId, status, classNameId, classPK, receiverUserId, type_);
create index IX_A591915 on SocialRequest (ctCollectionId, status, receiverUserId);
create index IX_CB6930CE on SocialRequest (ctCollectionId, status, userId, classNameId, classPK, type_);
create unique index IX_90CF630B on SocialRequest (ctCollectionId, userId, classNameId, classPK, receiverUserId, type_);
create index IX_FE42D3F6 on SocialRequest (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_9F3A95B0 on SocialRequest (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_5C67CE6F on SystemEvent (ctCollectionId, groupId, classNameId, classPK, type_);
create index IX_CADA055D on SystemEvent (ctCollectionId, groupId, systemEventSetKey);

create unique index IX_6434CF68 on Team (ctCollectionId, groupId, name[$COLUMN_LENGTH:75$]);
create index IX_BA056AB1 on Team (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_B6999F2B on Team (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_DAD135B4 on Ticket (classNameId, classPK, type_, companyId);
create index IX_B2468446 on Ticket (key_[$COLUMN_LENGTH:75$]);

create unique index IX_A33BD191 on UserGroup (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_C3A268EB on UserGroup (ctCollectionId, companyId, name[$COLUMN_LENGTH:255$]);
create index IX_21901169 on UserGroup (ctCollectionId, companyId, parentUserGroupId);
create index IX_44DF3608 on UserGroup (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_4FAEB03C on UserGroup (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_A723F635 on UserGroupGroupRole (ctCollectionId, roleId, userGroupId, groupId);

create unique index IX_9F6EEB7B on UserGroupRole (ctCollectionId, roleId, userId, groupId);

create index IX_2AC5356C on UserGroups_Teams (companyId);
create index IX_7F187E63 on UserGroups_Teams (userGroupId);

create unique index IX_1D280E23 on UserIdMapper (type_[$COLUMN_LENGTH:75$], externalUserId[$COLUMN_LENGTH:75$]);
create unique index IX_2DE52B22 on UserIdMapper (type_[$COLUMN_LENGTH:75$], userId);
create index IX_E60EA987 on UserIdMapper (userId);

create unique index IX_DB0E4086 on UserNotificationDelivery (userId, classNameId, deliveryType, notificationType, portletId[$COLUMN_LENGTH:200$]);

create index IX_BF29100B on UserNotificationEvent (type_[$COLUMN_LENGTH:200$]);
create index IX_851DE483 on UserNotificationEvent (userId, archived, actionRequired);
create index IX_AEB9F785 on UserNotificationEvent (userId, archived, delivered, actionRequired);
create index IX_32A44A07 on UserNotificationEvent (userId, archived, delivered, deliveryType, actionRequired);
create index IX_C1D9FAB9 on UserNotificationEvent (userId, archived, delivered, deliveryType, type_[$COLUMN_LENGTH:200$]);
create index IX_D6EA285 on UserNotificationEvent (userId, archived, deliveryType, actionRequired);
create index IX_D6084AE3 on UserNotificationEvent (userId, delivered, deliveryType, type_[$COLUMN_LENGTH:200$]);
create index IX_E4D2496 on UserNotificationEvent (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_29BA1CF5 on UserTracker (companyId);
create index IX_46B0AE8E on UserTracker (sessionId[$COLUMN_LENGTH:200$]);
create index IX_E4EFBA8D on UserTracker (userId);

create index IX_14D8BCC0 on UserTrackerPath (userTrackerId);

create unique index IX_77D89D58 on User_ (ctCollectionId, companyId, emailAddress[$COLUMN_LENGTH:254$]);
create unique index IX_6FF64E11 on User_ (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_C36EFC61 on User_ (ctCollectionId, companyId, facebookId);
create index IX_21E838BF on User_ (ctCollectionId, companyId, googleUserId[$COLUMN_LENGTH:75$]);
create index IX_2C316435 on User_ (ctCollectionId, companyId, modifiedDate, createDate);
create index IX_8B1DCCE5 on User_ (ctCollectionId, companyId, openId[$COLUMN_LENGTH:1024$]);
create unique index IX_6B7C3D77 on User_ (ctCollectionId, companyId, screenName[$COLUMN_LENGTH:75$]);
create index IX_66453103 on User_ (ctCollectionId, companyId, type_, status);
create index IX_5B22B288 on User_ (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_C15FB5CF on User_ (ctCollectionId, contactId);
create index IX_ED0F49A4 on User_ (ctCollectionId, emailAddress[$COLUMN_LENGTH:254$]);
create index IX_D83BFA02 on User_ (ctCollectionId, portraitId);
create index IX_70BC03BC on User_ (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_3499B657 on Users_Groups (companyId);
create index IX_F10B6C6B on Users_Groups (userId);

create index IX_5FBB883C on Users_Orgs (companyId);
create index IX_FB646CA6 on Users_Orgs (userId);

create index IX_F987A0DC on Users_Roles (companyId);
create index IX_C1A01806 on Users_Roles (userId);

create index IX_799F8283 on Users_Teams (companyId);
create index IX_A098EFBF on Users_Teams (userId);

create index IX_BB65040C on Users_UserGroups (companyId);
create index IX_66FF2503 on Users_UserGroups (userGroupId);

create index IX_74548FF0 on VirtualHost (ctCollectionId, companyId, layoutSetId, defaultVirtualHost);
create unique index IX_4C60203E on VirtualHost (ctCollectionId, hostname[$COLUMN_LENGTH:200$]);

create unique index IX_97DFA146 on WebDAVProps (classNameId, classPK);

create index IX_F6EA8123 on Website (companyId, classNameId, classPK, primary_);
create index IX_D8BDF3CD on Website (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_F75690BB on Website (userId);
create index IX_76F15D13 on Website (uuid_[$COLUMN_LENGTH:75$]);

create index IX_CC1C7294 on WorkflowDefinitionLink (companyId, ctCollectionId, groupId, classPK, classNameId, typePK);
create index IX_A50463F1 on WorkflowDefinitionLink (companyId, ctCollectionId, workflowDefinitionName[$COLUMN_LENGTH:75$], workflowDefinitionVersion);

create index IX_5BA863AD on WorkflowInstanceLink (classNameId, companyId, ctCollectionId, groupId, classPK);