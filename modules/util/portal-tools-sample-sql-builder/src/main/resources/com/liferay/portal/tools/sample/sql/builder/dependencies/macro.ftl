<#setting number_format = "computer">

<#macro insertAssetEntry
	_entry
	_categoryAndTag = false
>
	<#local assetEntryModel = assetDataFactory.newAssetEntryModel(_entry)>

	${resourcePermissionDataFactory.toInsertSQL(assetEntryModel)}

	<#if _categoryAndTag>
		<#local assetCategoryIds = assetDataFactory.getAssetCategoryIds(assetEntryModel)>

		<#list assetCategoryIds as assetCategoryId>
			<#local assetEntryAssetCategoryRelId = counterDataFactory.getCounterNext()>

			insert into AssetEntryAssetCategoryRel values (0, 0, ${assetEntryAssetCategoryRelId}, ${assetEntryModel.companyId}, ${assetEntryModel.entryId}, ${assetCategoryId}, 0);
		</#list>

		<#local assetTagIds = assetDataFactory.getAssetTagIds(assetEntryModel)>

		<#list assetTagIds as assetTagId>
			${resourcePermissionDataFactory.toInsertSQL("AssetEntries_AssetTags", assetEntryModel.companyId, assetEntryModel.entryId, assetTagId)}
		</#list>
	</#if>
</#macro>

<#macro insertContentLayout
	_layoutModel
	_fragmentEntryModel
>
	${resourcePermissionDataFactory.toInsertSQL(_layoutModel)}

	${resourcePermissionDataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}

	<#local fragmentEntryLinkModel = fragmentDataFactory.newFragmentEntryLinkModel(_layoutModel, _fragmentEntryModel)>

	${resourcePermissionDataFactory.toInsertSQL(fragmentEntryLinkModel)}

	${resourcePermissionDataFactory.toInsertSQL(journalDataFactory.newJournalContentPortletPreferencesModel(fragmentEntryLinkModel))}

	<#local layoutPageTemplateStructureModel = fragmentDataFactory.newLayoutPageTemplateStructureModel(_layoutModel)>

	${resourcePermissionDataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

	<#local layoutPageTemplateStructureRelModel = fragmentDataFactory.newLayoutPageTemplateStructureRelModel(_layoutModel, layoutPageTemplateStructureModel, fragmentEntryLinkModel)>

	${resourcePermissionDataFactory.toInsertSQL(layoutPageTemplateStructureRelModel)}
</#macro>

<#macro insertDDMContent
	_ddmStorageLinkId
	_ddmStructureId
	_entry
	_currentIndex = -1
>
	<#if _currentIndex = -1>
		<#local ddmContentModel = dLDataFactory.newDDMContentModel(_entry)>
	<#else>
		<#local ddmContentModel = dDLDDMDataFactory.newDDMContentModel(_entry, _currentIndex)>
	</#if>

	${resourcePermissionDataFactory.toInsertSQL(ddmContentModel)}

	${resourcePermissionDataFactory.toInsertSQL(dLDataFactory.newDDMStorageLinkModel(_ddmStorageLinkId, ddmContentModel, _ddmStructureId))}
</#macro>

<#macro insertDDMStructure
	_ddmStructureModel
	_ddmStructureLayoutModel
	_ddmStructureVersionModel
>
	${resourcePermissionDataFactory.toInsertSQL(_ddmStructureModel)}

	${resourcePermissionDataFactory.toInsertSQL(_ddmStructureLayoutModel)}

	${resourcePermissionDataFactory.toInsertSQL(_ddmStructureVersionModel)}
</#macro>

<#macro insertDLFolder
	_ddmStructureId
	_dlFolderDepth
	_groupId
	_parentDLFolderId
>
	<#if _dlFolderDepth <= dLDataFactory.maxDLFolderDepth>
		<#local dlFolderModels = dLDataFactory.newDLFolderModels(_groupId, _parentDLFolderId)>

		<#list dlFolderModels as dlFolderModel>
			${resourcePermissionDataFactory.toInsertSQL(dlFolderModel)}

			<@insertAssetEntry _entry=dlFolderModel />

			<#local dlFileEntryModels = dLDataFactory.newDlFileEntryModels(dlFolderModel)>

			<#list dlFileEntryModels as dlFileEntryModel>
				${resourcePermissionDataFactory.toInsertSQL(dlFileEntryModel)}

				<#local dlFileVersionModel = dLDataFactory.newDLFileVersionModel(dlFileEntryModel)>

				${resourcePermissionDataFactory.toInsertSQL(dlFileVersionModel)}

				<@insertAssetEntry _entry=dlFileEntryModel />

				<#local ddmStorageLinkId = counterDataFactory.getCounterNext()>

				<@insertDDMContent
					_ddmStorageLinkId=ddmStorageLinkId
					_ddmStructureId=_ddmStructureId
					_entry=dlFileEntryModel
				/>

				<@insertMBDiscussion
					_classNameId=dLDataFactory.DLFileEntryClassNameId
					_classPK=dlFileEntryModel.fileEntryId
					_groupId=dlFileEntryModel.groupId
					_maxCommentCount=0
					_mbRootMessageId=counterDataFactory.getCounterNext()
					_mbThreadId=counterDataFactory.getCounterNext()
				/>

				${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(dlFileEntryModel))}

				<#local dlFileEntryMetadataModel = dLDataFactory.newDLFileEntryMetadataModel(ddmStorageLinkId, _ddmStructureId, dlFileVersionModel)>

				${resourcePermissionDataFactory.toInsertSQL(dlFileEntryMetadataModel)}

				${resourcePermissionDataFactory.toInsertSQL(dDLDDMDataFactory.newDDMStructureLinkModel(dlFileEntryMetadataModel))}

				${csvFileWriter.write("documentLibrary", dlFileEntryModel.uuid + "," + dlFolderModel.folderId + "," + dlFileEntryModel.name + "," + dlFileEntryModel.fileEntryId + "\n")}
			</#list>

			<@insertDLFolder
				_ddmStructureId=_ddmStructureId
				_dlFolderDepth=_dlFolderDepth + 1
				_groupId=groupId
				_parentDLFolderId=dlFolderModel.folderId
			/>
		</#list>
	</#if>
</#macro>

<#macro insertGroup
	_groupModel
>
	${resourcePermissionDataFactory.toInsertSQL(_groupModel)}

	<#local layoutSetModels = layoutDataFactory.newLayoutSetModels(_groupModel.groupId)>

	<#list layoutSetModels as layoutSetModel>
		${resourcePermissionDataFactory.toInsertSQL(layoutSetModel)}
	</#list>
</#macro>

<#macro insertLayout
	_layoutModel
>
	${resourcePermissionDataFactory.toInsertSQL(_layoutModel)}

	${resourcePermissionDataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}
</#macro>

<#macro insertMBDiscussion
	_classNameId
	_classPK
	_groupId
	_maxCommentCount
	_mbRootMessageId
	_mbThreadId
>
	<#local mbThreadModel = messageBoardDataFactory.newMBThreadModel(_mbThreadId, _groupId, _mbRootMessageId)>

	${resourcePermissionDataFactory.toInsertSQL(mbThreadModel)}

	<#local mbRootMessageModel = messageBoardDataFactory.newMBMessageModel(mbThreadModel, _classNameId, _classPK, 0)>

	<@insertMBMessage _mbMessageModel=mbRootMessageModel />

	<#local mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel, _classNameId, _classPK, _maxCommentCount)>

	<#list mbMessageModels as mbMessageModel>
		<@insertMBMessage _mbMessageModel=mbMessageModel />

		${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}
	</#list>

	${resourcePermissionDataFactory.toInsertSQL(messageBoardDataFactory.newMBDiscussionModel(_groupId, _classNameId, _classPK, _mbThreadId))}
</#macro>

<#macro insertMBMessage
	_mbMessageModel
>
	${resourcePermissionDataFactory.toInsertSQL(_mbMessageModel)}

	<@insertAssetEntry _entry=_mbMessageModel />
</#macro>

<#macro insertUser
	_userModel
	_groupIds = []
	_roleIds = []
>
	${resourcePermissionDataFactory.toInsertSQL(_userModel)}

	${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newContactModel(_userModel))}

	<#list _roleIds as roleId>
		${resourcePermissionDataFactory.toInsertSQL("Users_Roles", 0, roleId, _userModel.userId)}
	</#list>

	<#list _groupIds as groupId>
		${resourcePermissionDataFactory.toInsertSQL("Users_Groups", 0, groupId, _userModel.userId)}
	</#list>
</#macro>