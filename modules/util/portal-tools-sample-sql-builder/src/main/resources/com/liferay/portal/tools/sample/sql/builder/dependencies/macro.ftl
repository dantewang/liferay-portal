<#setting number_format = "computer">

<#macro insertAssetEntry
	_entry
	_categoryAndTag = false
>
	<#local assetEntryModel = dataFactory.newAssetEntryModel(_entry)>

	${dataFactory.toInsertSQL(assetEntryModel)}

	<#if _categoryAndTag>
		<#local assetCategoryIds = dataFactory.getAssetCategoryIds(assetEntryModel)>

		<#list assetCategoryIds as assetCategoryId>
			<#local assetEntryAssetCategoryRelId = counterDataFactory.getCounterNext()>

			insert into AssetEntryAssetCategoryRel values (0, 0, ${assetEntryAssetCategoryRelId}, ${assetEntryModel.companyId}, ${assetEntryModel.entryId}, ${assetCategoryId}, 0);
		</#list>

		<#local assetTagIds = dataFactory.getAssetTagIds(assetEntryModel)>

		<#list assetTagIds as assetTagId>
			${dataFactory.toInsertSQL("AssetEntries_AssetTags", assetEntryModel.companyId, assetEntryModel.entryId, assetTagId)}
		</#list>
	</#if>
</#macro>

<#macro insertContentLayout
	_layoutModel
	_fragmentEntryModel
>
	${dataFactory.toInsertSQL(_layoutModel)}

	${dataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}

	<#local fragmentEntryLinkModel = fragmentDataFactory.newFragmentEntryLinkModel(_layoutModel, _fragmentEntryModel)>

	${dataFactory.toInsertSQL(fragmentEntryLinkModel)}

	${dataFactory.toInsertSQL(journalDataFactory.newJournalContentPortletPreferencesModel(fragmentEntryLinkModel))}

	<#local layoutPageTemplateStructureModel = fragmentDataFactory.newLayoutPageTemplateStructureModel(_layoutModel)>

	${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

	<#local layoutPageTemplateStructureRelModel = fragmentDataFactory.newLayoutPageTemplateStructureRelModel(_layoutModel, layoutPageTemplateStructureModel, fragmentEntryLinkModel)>

	${dataFactory.toInsertSQL(layoutPageTemplateStructureRelModel)}
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

	${dataFactory.toInsertSQL(ddmContentModel)}

	${dataFactory.toInsertSQL(dLDataFactory.newDDMStorageLinkModel(_ddmStorageLinkId, ddmContentModel, _ddmStructureId))}
</#macro>

<#macro insertDDMStructure
	_ddmStructureModel
	_ddmStructureLayoutModel
	_ddmStructureVersionModel
>
	${dataFactory.toInsertSQL(_ddmStructureModel)}

	${dataFactory.toInsertSQL(_ddmStructureLayoutModel)}

	${dataFactory.toInsertSQL(_ddmStructureVersionModel)}
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
			${dataFactory.toInsertSQL(dlFolderModel)}

			<@insertAssetEntry _entry=dlFolderModel />

			<#local dlFileEntryModels = dLDataFactory.newDlFileEntryModels(dlFolderModel)>

			<#list dlFileEntryModels as dlFileEntryModel>
				${dataFactory.toInsertSQL(dlFileEntryModel)}

				<#local dlFileVersionModel = dLDataFactory.newDLFileVersionModel(dlFileEntryModel)>

				${dataFactory.toInsertSQL(dlFileVersionModel)}

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

				${dataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(dlFileEntryModel))}

				<#local dlFileEntryMetadataModel = dLDataFactory.newDLFileEntryMetadataModel(ddmStorageLinkId, _ddmStructureId, dlFileVersionModel)>

				${dataFactory.toInsertSQL(dlFileEntryMetadataModel)}

				${dataFactory.toInsertSQL(dDLDDMDataFactory.newDDMStructureLinkModel(dlFileEntryMetadataModel))}

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
	${dataFactory.toInsertSQL(_groupModel)}

	<#local layoutSetModels = layoutDataFactory.newLayoutSetModels(_groupModel.groupId)>

	<#list layoutSetModels as layoutSetModel>
		${dataFactory.toInsertSQL(layoutSetModel)}
	</#list>
</#macro>

<#macro insertLayout
	_layoutModel
>
	${dataFactory.toInsertSQL(_layoutModel)}

	${dataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}
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

	${dataFactory.toInsertSQL(mbThreadModel)}

	<#local mbRootMessageModel = messageBoardDataFactory.newMBMessageModel(mbThreadModel, _classNameId, _classPK, 0)>

	<@insertMBMessage _mbMessageModel=mbRootMessageModel />

	<#local mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel, _classNameId, _classPK, _maxCommentCount)>

	<#list mbMessageModels as mbMessageModel>
		<@insertMBMessage _mbMessageModel=mbMessageModel />

		${dataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}
	</#list>

	${dataFactory.toInsertSQL(messageBoardDataFactory.newMBDiscussionModel(_groupId, _classNameId, _classPK, _mbThreadId))}
</#macro>

<#macro insertMBMessage
	_mbMessageModel
>
	${dataFactory.toInsertSQL(_mbMessageModel)}

	<@insertAssetEntry _entry=_mbMessageModel />
</#macro>

<#macro insertUser
	_userModel
	_groupIds = []
	_roleIds = []
>
	${dataFactory.toInsertSQL(_userModel)}

	${dataFactory.toInsertSQL(userDataFactory.newContactModel(_userModel))}

	<#list _roleIds as roleId>
		${dataFactory.toInsertSQL("Users_Roles", 0, roleId, _userModel.userId)}
	</#list>

	<#list _groupIds as groupId>
		${dataFactory.toInsertSQL("Users_Groups", 0, groupId, _userModel.userId)}
	</#list>
</#macro>