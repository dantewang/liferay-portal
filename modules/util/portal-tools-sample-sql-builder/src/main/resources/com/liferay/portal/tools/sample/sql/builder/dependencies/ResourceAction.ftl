<#list dataFactory.newAnnouncementsEntryResourceActionModels() as announcementsEntryResourceActionModel>
	${dataFactory.toInsertSQL(announcementsEntryResourceActionModel)}
</#list>

<#list dataFactory.newAssetCategoryResourceActionModels() as assetCategoryResourceActionModel>
	${dataFactory.toInsertSQL(assetCategoryResourceActionModel)}
</#list>

<#list dataFactory.newAssetResourceActionModels() as assetResourceActionModel>
	${dataFactory.toInsertSQL(assetResourceActionModel)}
</#list>

<#list dataFactory.newAssetVocabularyResourceActionModels() as assetVocabularyResourceActionModel>
	${dataFactory.toInsertSQL(assetVocabularyResourceActionModel)}
</#list>

<#list dataFactory.newDLFileEntryMetadataCombineDDMStructureResourceActionModels() as dlFileEntryMetadataCombineDDMStructureResourceActionModel>
	${dataFactory.toInsertSQL(dlFileEntryMetadataCombineDDMStructureResourceActionModel)}
</#list>

<#list dataFactory.newDLFileEntryResourceActionModels() as dlFileEntryResourceActionModel>
	${dataFactory.toInsertSQL(dlFileEntryResourceActionModel)}
</#list>

<#list dataFactory.newDLFileEntryTypeResourceActionModels() as dlFileEntryTypeResourceActionModel>
	${dataFactory.toInsertSQL(dlFileEntryTypeResourceActionModel)}
</#list>

<#list dataFactory.newDLFileShortcutResourceActionModels() as dlFileShortcutResourceActionModel>
	${dataFactory.toInsertSQL(dlFileShortcutResourceActionModel)}
</#list>

<#list dataFactory.newDLFolderResourceActionModels() as dlFolderResourceActionModel>
	${dataFactory.toInsertSQL(dlFolderResourceActionModel)}
</#list>

<#list dataFactory.newExpandoColumnResourceActionModels() as expandoColumnResourceActionModel>
	${dataFactory.toInsertSQL(expandoColumnResourceActionModel)}
</#list>

<#list dataFactory.newGroupResourceActionModels() as groupResourceActionModel>
	${dataFactory.toInsertSQL(groupResourceActionModel)}
</#list>

<#list dataFactory.newLayoutBranchResourceActionModels() as layoutBranchResourceActionModel>
	${dataFactory.toInsertSQL(layoutBranchResourceActionModel)}
</#list>

<#list dataFactory.newLayoutPrototypeResourceActionModels() as layoutPrototypeResourceActionModel>
	${dataFactory.toInsertSQL(layoutPrototypeResourceActionModel)}
</#list>

<#list dataFactory.newLayoutResourceActionModels() as layoutResourceActionModel>
	${dataFactory.toInsertSQL(layoutResourceActionModel)}
</#list>

<#list dataFactory.newLayoutSetBranchResourceActionModels() as layoutSetBranchResourceActionModel>
	${dataFactory.toInsertSQL(layoutSetBranchResourceActionModel)}
</#list>

<#list dataFactory.newLayoutSetPrototypeResourceActionModels() as layoutSetPrototypeResourceActionModel>
	${dataFactory.toInsertSQL(layoutSetPrototypeResourceActionModel)}
</#list>

<#list dataFactory.newOrganizationResourceActionModels() as organizationResourceActionModel>
	${dataFactory.toInsertSQL(organizationResourceActionModel)}
</#list>

<#list dataFactory.newPasswordPolicyResourceActionModels() as passwordPolicyResourceActionModel>
	${dataFactory.toInsertSQL(passwordPolicyResourceActionModel)}
</#list>

<#list dataFactory.newRoleResourceActionModels() as roleResourceActionModel>
	${dataFactory.toInsertSQL(roleResourceActionModel)}
</#list>

<#list dataFactory.newTeamResourceActionModels() as teamResourceActionModel>
	${dataFactory.toInsertSQL(teamResourceActionModel)}
</#list>

<#list dataFactory.newUserGroupResourceActionModels() as userGroupResourceActionModel>
	${dataFactory.toInsertSQL(userGroupResourceActionModel)}
</#list>

<#list dataFactory.newUserResourceActionModels() as userResourceActionModel>
	${dataFactory.toInsertSQL(userResourceActionModel)}
</#list>