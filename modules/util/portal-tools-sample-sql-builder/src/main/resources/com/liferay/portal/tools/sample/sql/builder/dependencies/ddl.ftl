<#assign
	ddlRecordSetClassNameId = classNameDataFactory.getClassNameId("DDLRecordSet")
	ddlRecordSetCounts = dataFactory.getSequence(ddlDDMDataFactory.maxDDLRecordSetCount)
/>

<#list ddlRecordSetCounts as ddlRecordSetCount>
	<#if ddlRecordSetCount = 1>
		<#assign
			ddmStructureModel = ddlDDMDataFactory.newDDLDDMStructureModel(groupId, ddlRecordSetClassNameId)
			ddmStructureVersionModel = ddlDDMDataFactory.newDDMStructureVersionModel(ddmStructureModel)
		/>

		<@insertDDMStructure
			_ddmStructureLayoutModel=ddlDDMDataFactory.newDDLDDMStructureLayoutModel(groupId, ddmStructureVersionModel)
			_ddmStructureModel=ddmStructureModel
			_ddmStructureVersionModel=ddmStructureVersionModel
		/>
	</#if>

	<#assign
		layoutName = "dynamic_data_list_display_" + ddlRecordSetCount
		portletId = "com_liferay_dynamic_data_lists_web_portlet_DDLDisplayPortlet_INSTANCE_TEST" + ddlRecordSetCount

		layoutModel = dataFactory.newLayoutModel(groupId, layoutName, "", portletId)
	/>

	<@insertLayout _layoutModel=layoutModel />

	<#assign ddlRecordSetModel = ddlDDMDataFactory.newDDLRecordSetModel(ddmStructureModel, ddlRecordSetCount) />

	${dataFactory.toInsertSQL(ddlRecordSetModel)}

	${dataFactory.toInsertSQL(ddlDDMDataFactory.newDDMStructureLinkModel(ddlRecordSetModel, ddlRecordSetClassNameId))}

	<#assign ddlRecordCounts = dataFactory.getSequence(ddlDDMDataFactory.maxDDLRecordCount) />

	<#list ddlRecordCounts as ddlRecordCount>
		<#assign ddlRecordModel = ddlDDMDataFactory.newDDLRecordModel(ddlRecordSetModel) />

		${dataFactory.toInsertSQL(ddlRecordModel)}

		${dataFactory.toInsertSQL(ddlDDMDataFactory.newDDLRecordVersionModel(ddlRecordModel))}

		<@insertDDMContent
			_currentIndex=ddlRecordCount
			_ddmStorageLinkId=dataFactory.getCounterNext()
			_ddmStructureId=ddmStructureModel.structureId
			_entry=ddlRecordModel
		/>

		${csvFileWriter.write("dynamicDataList", ddlRecordModel.groupId + "," + layoutName + "," + portletId + "," + ddlRecordSetModel.recordSetId + "," + ddlRecordModel.recordId + "\n")}
	</#list>

	${dataFactory.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletId, ddlRecordSetModel))}

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newDDLPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>
</#list>