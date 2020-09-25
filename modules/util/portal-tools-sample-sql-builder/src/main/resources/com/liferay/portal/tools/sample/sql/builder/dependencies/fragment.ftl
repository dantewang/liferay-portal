<#assign fragmentCollectionModel = fragmentDataFactory.newFragmentCollectionModel(groupId) />

${resourcePermissionDataFactory.toInsertSQL(fragmentCollectionModel)}

<#assign fragmentEntryModel = fragmentDataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

${resourcePermissionDataFactory.toInsertSQL(fragmentEntryModel)}

<#assign contentLayoutModels = layoutDataFactory.newContentLayoutModels(groupId) />

<#list contentLayoutModels as contentLayoutModel>
	<@insertContentLayout
		_fragmentEntryModel=fragmentEntryModel
		_layoutModel=contentLayoutModel
		_portletPreferencesFactory=portletPreferenceDataFactory.portletPreferencesFactory
	/>

	${csvFileWriter.write("fragment", contentLayoutModel.friendlyURL + "\n")}
</#list>