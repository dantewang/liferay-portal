create index IX_E558B2AD on CChannelAccountEntryRel (ctCollectionId, classNameId, classPK);
create index IX_C8FE8EC9 on CChannelAccountEntryRel (ctCollectionId, type_, accountEntryId);
create unique index IX_34B219C7 on CChannelAccountEntryRel (ctCollectionId, type_, commerceChannelId, accountEntryId, classNameId, classPK);
create index IX_41FDCE13 on CChannelAccountEntryRel (ctCollectionId, type_, commerceChannelId, classNameId, classPK);

create index IX_F80B0935 on CPAttachmentFileEntry (ctCollectionId, classNameId, classPK, cdnURL[$COLUMN_LENGTH:4000$]);
create index IX_1005D5A2 on CPAttachmentFileEntry (ctCollectionId, classNameId, classPK, fileEntryId);
create index IX_4030F47 on CPAttachmentFileEntry (ctCollectionId, classNameId, classPK, status, displayDate);
create index IX_9F79F190 on CPAttachmentFileEntry (ctCollectionId, classNameId, classPK, status, type_);
create index IX_B8BE31E9 on CPAttachmentFileEntry (ctCollectionId, classNameId, fileEntryId, groupId);
create unique index IX_BA421003 on CPAttachmentFileEntry (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_6BCB9AD7 on CPAttachmentFileEntry (ctCollectionId, fileEntryId);
create index IX_265ECF7C on CPAttachmentFileEntry (ctCollectionId, status, displayDate);
create index IX_D344987A on CPAttachmentFileEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_EC2BAB34 on CPAttachmentFileEntry (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_DDD44E12 on CPDSpecificationOptionValue (ctCollectionId, CPDefinitionId, CPOptionCategoryId);
create index IX_5A0422EF on CPDSpecificationOptionValue (ctCollectionId, CPDefinitionId, CPSpecificationOptionId);
create index IX_D71FC803 on CPDSpecificationOptionValue (ctCollectionId, CPOptionCategoryId);
create index IX_8D3C379E on CPDSpecificationOptionValue (ctCollectionId, CPSpecificationOptionId);
create index IX_E067AC44 on CPDSpecificationOptionValue (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_35B5297E on CPDSpecificationOptionValue (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_44AA747F on CPDefinition (ctCollectionId, CPTaxCategoryId);
create index IX_71A91E09 on CPDefinition (ctCollectionId, CProductId, version);
create index IX_6F0A4E4C on CPDefinition (ctCollectionId, groupId, status);
create index IX_23383EEE on CPDefinition (ctCollectionId, groupId, subscriptionEnabled);
create unique index IX_8845958E on CPDefinition (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_F1E012C5 on CPDefinition (ctCollectionId, status, CProductId);
create index IX_197B4D62 on CPDefinition (ctCollectionId, status, displayDate);
create index IX_D08D5454 on CPDefinition (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_C83EF008 on CPDefinitionLink (ctCollectionId, status, displayDate);
create index IX_4BA4D675 on CPDefinitionLink (ctCollectionId, status, expirationDate);
create unique index IX_8349424D on CPDefinitionLink (ctCollectionId, type_[$COLUMN_LENGTH:75$], CPDefinitionId, CProductId);
create index IX_B6A1323C on CPDefinitionLink (ctCollectionId, type_[$COLUMN_LENGTH:75$], CProductId);
create index IX_CC6677A2 on CPDefinitionLink (ctCollectionId, type_[$COLUMN_LENGTH:75$], status, CPDefinitionId);
create index IX_DC102D96 on CPDefinitionLink (ctCollectionId, type_[$COLUMN_LENGTH:75$], status, CProductId);
create index IX_5C4F236E on CPDefinitionLink (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_28846328 on CPDefinitionLink (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_4FD04A13 on CPDefinitionLocalization (CPDefinitionId, ctCollectionId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_170B5E88 on CPDefinitionOptionRel (ctCollectionId, CPDefinitionId, CPOptionId);
create unique index IX_78CCF36B on CPDefinitionOptionRel (ctCollectionId, CPDefinitionId, key_[$COLUMN_LENGTH:75$]);
create index IX_42DA8EA on CPDefinitionOptionRel (ctCollectionId, CPDefinitionId, required);
create index IX_EA96E249 on CPDefinitionOptionRel (ctCollectionId, CPDefinitionId, skuContributor);
create index IX_D19F52D8 on CPDefinitionOptionRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_5C0D6512 on CPDefinitionOptionRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_2CA7C89E on CPDefinitionOptionValueRel (ctCollectionId, CPDefinitionOptionRelId, key_[$COLUMN_LENGTH:75$]);
create index IX_526F1CE4 on CPDefinitionOptionValueRel (ctCollectionId, CPDefinitionOptionRelId, preselected);
create index IX_F817D0D2 on CPDefinitionOptionValueRel (ctCollectionId, CPInstanceUuid[$COLUMN_LENGTH:75$]);
create index IX_707C7CB5 on CPDefinitionOptionValueRel (ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_543E52F1 on CPDefinitionOptionValueRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_EE6F976B on CPDefinitionOptionValueRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_2C513592 on CPDisplayLayout (ctCollectionId, groupId, classNameId, classPK);
create index IX_AAC05EA3 on CPDisplayLayout (ctCollectionId, groupId, layoutPageTemplateEntryUuid[$COLUMN_LENGTH:75$]);
create index IX_CA079D40 on CPDisplayLayout (ctCollectionId, groupId, layoutUuid[$COLUMN_LENGTH:75$]);
create unique index IX_6856274D on CPDisplayLayout (ctCollectionId, groupId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_EEC07253 on CPDisplayLayout (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_31443E86 on CPInstance (ctCollectionId, CPDefinitionId, CPInstanceUuid[$COLUMN_LENGTH:75$]);
create unique index IX_E06787D8 on CPInstance (ctCollectionId, CPDefinitionId, sku[$COLUMN_LENGTH:75$]);
create index IX_140F071 on CPInstance (ctCollectionId, CPDefinitionId, status, displayDate);
create index IX_7BF6BCF7 on CPInstance (ctCollectionId, CPInstanceUuid[$COLUMN_LENGTH:75$]);
create unique index IX_679A095F on CPInstance (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_EE6308AF on CPInstance (ctCollectionId, companyId, sku[$COLUMN_LENGTH:75$]);
create index IX_5C12C9D6 on CPInstance (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_5940D0A0 on CPInstance (ctCollectionId, status, displayDate);
create index IX_C61F208A on CPInstance (ctCollectionId, status, groupId);
create unique index IX_724E8390 on CPInstance (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_4B841608 on CPInstanceOptionValueRel (ctCollectionId, CPDefinitionOptionRelId);
create unique index IX_E45EE045 on CPInstanceOptionValueRel (ctCollectionId, CPInstanceId, CPDefinitionOptionRelId, CPDefinitionOptionValueRelId);
create index IX_B330CBE0 on CPInstanceOptionValueRel (ctCollectionId, CPInstanceId, CPDefinitionOptionValueRelId);
create index IX_BBFEFCF3 on CPInstanceOptionValueRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_5AF6D9ED on CPInstanceOptionValueRel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_DD233BFF on CPInstanceUOM (ctCollectionId, CPInstanceId, active_);
create unique index IX_9DC1F81A on CPInstanceUOM (ctCollectionId, CPInstanceId, key_[$COLUMN_LENGTH:75$]);
create index IX_93B07D97 on CPInstanceUOM (ctCollectionId, CPInstanceId, primary_);
create index IX_83F2644A on CPInstanceUOM (ctCollectionId, companyId, key_[$COLUMN_LENGTH:75$], sku[$COLUMN_LENGTH:75$]);
create index IX_BE12A25E on CPInstanceUOM (ctCollectionId, companyId, sku[$COLUMN_LENGTH:75$]);
create index IX_FE42B3C5 on CPInstanceUOM (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_38F0669F on CPInstanceUOM (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_D52621F0 on CPMeasurementUnit (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_51CDE4C1 on CPMeasurementUnit (ctCollectionId, companyId, key_[$COLUMN_LENGTH:75$]);
create index IX_64AA3957 on CPMeasurementUnit (ctCollectionId, companyId, type_, primary_);
create index IX_135C2467 on CPMeasurementUnit (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_FA9F4E61 on CPMeasurementUnit (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create unique index IX_4E312C7F on CPOption (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_143B0E52 on CPOption (ctCollectionId, companyId, key_[$COLUMN_LENGTH:75$]);
create index IX_9E942CF6 on CPOption (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_D1AEC20E on CPOption (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E4988A74 on CPOptionCategory (ctCollectionId, companyId, key_[$COLUMN_LENGTH:75$]);
create index IX_D9E63514 on CPOptionCategory (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_3B90A0B0 on CPOptionCategory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_DA77C838 on CPOptionValue (ctCollectionId, CPOptionId, key_[$COLUMN_LENGTH:75$]);
create unique index IX_DC509C0C on CPOptionValue (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_CE965683 on CPOptionValue (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_AAC70EA1 on CPOptionValue (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_CF01C3DE on CPSpecificationOption (ctCollectionId, CPOptionCategoryId);
create unique index IX_669F7749 on CPSpecificationOption (ctCollectionId, companyId, key_[$COLUMN_LENGTH:75$]);
create index IX_98BCE2DF on CPSpecificationOption (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_1FBCA3C5 on CPSpecificationOption (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_C88C2B59 on CPTaxCategory (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_626C1FD0 on CPTaxCategory (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_88121374 on CPTaxCategory (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_5FAC5769 on CProduct (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F5066BE0 on CProduct (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_92F7201A on CProduct (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_4807BAF6 on CommerceCatalog (ctCollectionId, accountEntryId);
create unique index IX_F7CAA7DB on CommerceCatalog (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_142CC65E on CommerceCatalog (ctCollectionId, companyId, system_);
create index IX_8AE7E052 on CommerceCatalog (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_DD299032 on CommerceCatalog (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create index IX_4F0C1D60 on CommerceChannel (ctCollectionId, accountEntryId);
create unique index IX_27C703C5 on CommerceChannel (ctCollectionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E25D503C on CommerceChannel (ctCollectionId, companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_31A69BF7 on CommerceChannel (ctCollectionId, siteGroupId);
create index IX_297AEE88 on CommerceChannel (ctCollectionId, uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4469A625 on CommerceChannelRel (ctCollectionId, classNameId, classPK, commerceChannelId);
create index IX_90EE555A on CommerceChannelRel (ctCollectionId, commerceChannelId);