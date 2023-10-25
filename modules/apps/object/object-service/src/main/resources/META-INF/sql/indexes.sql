create index IX_222D70C7 on ObjectAction (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_199B943E on ObjectAction (objectDefinitionId, active_, name[$COLUMN_LENGTH:75$], objectActionTriggerKey[$COLUMN_LENGTH:75$]);
create index IX_B045717F on ObjectAction (objectDefinitionId, active_, objectActionTriggerKey[$COLUMN_LENGTH:75$]);
create unique index IX_58340631 on ObjectAction (objectDefinitionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_911F75B on ObjectAction (objectDefinitionId, name[$COLUMN_LENGTH:75$]);
create index IX_570E3859 on ObjectAction (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C5CB8E8D on ObjectDefinition (companyId, className[$COLUMN_LENGTH:255$]);
create unique index IX_F861636D on ObjectDefinition (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_3E56F38F on ObjectDefinition (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_E5612EB6 on ObjectDefinition (companyId, status);
create index IX_C66B9DF8 on ObjectDefinition (companyId, system_, modifiable);
create index IX_A0282523 on ObjectDefinition (companyId, system_, status, active_);
create index IX_20BBFFE4 on ObjectDefinition (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_8D232754 on ObjectDefinition (objectFolderId);
create index IX_1FBC0C2 on ObjectDefinition (system_, status);
create index IX_7B61F95C on ObjectDefinition (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_A2F3B3C on ObjectEntry (companyId, groupId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_B14B6BA5 on ObjectEntry (companyId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_28B2B723 on ObjectEntry (groupId, uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_4CE66A0F on ObjectEntry (objectDefinitionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_7403EBB8 on ObjectEntry (objectDefinitionId, groupId, status);
create index IX_A388E5A0 on ObjectEntry (objectDefinitionId, status);
create index IX_4507FEF4 on ObjectEntry (objectDefinitionId, userId);
create index IX_BD205C3B on ObjectEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C0DD702D on ObjectField (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_6DCE835D on ObjectField (listTypeDefinitionId, state_);
create unique index IX_8BEECA97 on ObjectField (objectDefinitionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_284528AB on ObjectField (objectDefinitionId, dbTableName[$COLUMN_LENGTH:75$]);
create index IX_2912D535 on ObjectField (objectDefinitionId, indexed, dbType[$COLUMN_LENGTH:75$]);
create index IX_48FE1ACB on ObjectField (objectDefinitionId, localized);
create index IX_C69730C1 on ObjectField (objectDefinitionId, name[$COLUMN_LENGTH:75$]);
create index IX_4A69C63E on ObjectField (objectDefinitionId, system_);
create index IX_FBA3DCB3 on ObjectField (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_94344C6C on ObjectFieldSetting (objectFieldId, name[$COLUMN_LENGTH:75$]);
create index IX_AE8F1F47 on ObjectFieldSetting (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_B3C95F49 on ObjectFilter (objectFieldId);
create index IX_85CD8909 on ObjectFilter (uuid_[$COLUMN_LENGTH:75$], companyId);

create unique index IX_677F9088 on ObjectFolder (companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8FBAE114 on ObjectFolder (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_FBD5C2FF on ObjectFolder (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_14631921 on ObjectFolder (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_61EBCE03 on ObjectFolderItem (objectFolderId, objectDefinitionId);
create index IX_68A03C32 on ObjectFolderItem (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_C2DD36AC on ObjectLayout (objectDefinitionId, defaultObjectLayout);
create index IX_4A0CEBBB on ObjectLayout (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_5F97F7CF on ObjectLayoutBox (objectLayoutTabId);
create index IX_355E0F74 on ObjectLayoutBox (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_E992BFE1 on ObjectLayoutColumn (objectFieldId);
create index IX_46CE5537 on ObjectLayoutColumn (objectLayoutRowId);
create index IX_BB4D4171 on ObjectLayoutColumn (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_FA14DE56 on ObjectLayoutRow (objectLayoutBoxId);
create index IX_CBDEAE03 on ObjectLayoutRow (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_F01F1EEA on ObjectLayoutTab (objectLayoutId);
create index IX_4CC508B8 on ObjectLayoutTab (objectRelationshipId);
create index IX_FC65883E on ObjectLayoutTab (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_D2BFA726 on ObjectRelationship (objectDefinitionId1, edge);
create index IX_5B865C97 on ObjectRelationship (objectDefinitionId1, reverse, deletionType[$COLUMN_LENGTH:75$]);
create index IX_BAD36E80 on ObjectRelationship (objectDefinitionId1, reverse, type_[$COLUMN_LENGTH:75$], objectDefinitionId2, name[$COLUMN_LENGTH:75$]);
create index IX_BC1CDAB8 on ObjectRelationship (objectDefinitionId1, type_[$COLUMN_LENGTH:75$], objectDefinitionId2, name[$COLUMN_LENGTH:75$]);
create index IX_F1DC092D on ObjectRelationship (objectFieldId2);
create index IX_820C98BE on ObjectRelationship (parameterObjectFieldId);
create index IX_22D86D64 on ObjectRelationship (reverse, dbTableName[$COLUMN_LENGTH:75$]);
create index IX_B7B05EFB on ObjectRelationship (reverse, type_[$COLUMN_LENGTH:75$], objectDefinitionId2);
create index IX_796D0889 on ObjectRelationship (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_C34F0F9E on ObjectState (objectStateFlowId, listTypeEntryId);
create index IX_6362E244 on ObjectState (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_AE828160 on ObjectStateFlow (objectFieldId);
create index IX_73F39B92 on ObjectStateFlow (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_DB56B27E on ObjectStateTransition (objectStateFlowId);
create index IX_9C3FAB55 on ObjectStateTransition (sourceObjectStateId);
create index IX_FB9AC71F on ObjectStateTransition (targetObjectStateId);
create index IX_B4FBB8B9 on ObjectStateTransition (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_A8840D26 on ObjectValidationRule (companyId, uuid_[$COLUMN_LENGTH:75$]);
create index IX_C909C790 on ObjectValidationRule (objectDefinitionId, active_);
create unique index IX_7BB41F10 on ObjectValidationRule (objectDefinitionId, companyId, externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_9F23EA31 on ObjectValidationRule (objectDefinitionId, engine[$COLUMN_LENGTH:255$]);
create index IX_465D010A on ObjectValidationRule (objectDefinitionId, outputType[$COLUMN_LENGTH:75$]);
create index IX_ADDDA15A on ObjectValidationRule (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_3AD663DD on ObjectValidationRuleSetting (objectValidationRuleId, name[$COLUMN_LENGTH:75$], value[$COLUMN_LENGTH:75$]);
create index IX_4185A82E on ObjectValidationRuleSetting (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_A7522CC on ObjectView (objectDefinitionId, defaultObjectView);
create index IX_74094376 on ObjectView (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_450013E9 on ObjectViewColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_5A99A0EC on ObjectViewColumn (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_F2526C51 on ObjectViewFilterColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_6B57BF84 on ObjectViewFilterColumn (uuid_[$COLUMN_LENGTH:75$], companyId);

create index IX_8F4D826B on ObjectViewSortColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_9150C72A on ObjectViewSortColumn (uuid_[$COLUMN_LENGTH:75$], companyId);