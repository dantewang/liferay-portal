<#assign fragmentCollectionModel = fragmentDataFactory.newFragmentCollectionModel(groupId) />

${insertSQLBuilder.toInsertSQL(fragmentCollectionModel)}

<#assign fragmentEntryModel = fragmentDataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

${insertSQLBuilder.toInsertSQL(fragmentEntryModel)}

<#assign contentLayoutModels = layoutDataFactory.newContentLayoutModels(groupId) />

<#list contentLayoutModels as contentLayoutModel>
	<@insertContentLayout
		_fragmentEntryModel=fragmentEntryModel
		_layoutModel=contentLayoutModel
		_portletPreferencesFactory=portletPreferenceDataFactory.portletPreferencesFactory
	/>

	${csvFileWriter.write("fragment", contentLayoutModel.friendlyURL + "\n")}
</#list>