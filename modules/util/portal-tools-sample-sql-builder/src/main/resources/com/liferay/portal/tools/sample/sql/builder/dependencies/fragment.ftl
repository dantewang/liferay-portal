<#assign fragmentCollectionModel = fragmentDataFactory.newFragmentCollectionModel(groupId) />

${dataFactory.toInsertSQL(fragmentCollectionModel)}

<#assign fragmentEntryModel = fragmentDataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

${dataFactory.toInsertSQL(fragmentEntryModel)}

<#assign contentLayoutModels = dataFactory.newContentLayoutModels(groupId) />

<#list contentLayoutModels as contentLayoutModel>
	<@insertContentLayout
		_fragmentEntryModel=fragmentEntryModel
		_layoutModel=contentLayoutModel
		_portletPreferencesFactory=portletPreferenceDataFactory.portletPreferencesFactory
	/>

	${csvFileWriter.write("fragment", contentLayoutModel.friendlyURL + "\n")}
</#list>