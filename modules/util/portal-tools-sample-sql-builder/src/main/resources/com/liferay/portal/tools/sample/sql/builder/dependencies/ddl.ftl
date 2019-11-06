<#assign ddlRecordSetCounts = counterDataFactory.getSequence(dDLDDMDataFactory.maxDDLRecordSetCount) />

<#list ddlRecordSetCounts as ddlRecordSetCount>
	<#if ddlRecordSetCount = 1>
		<#assign
			ddmStructureModel = dDLDDMDataFactory.newDDLDDMStructureModel(groupId)
			ddmStructureVersionModel = dDLDDMDataFactory.newDDMStructureVersionModel(ddmStructureModel)
		/>

		<@insertDDMStructure
			_ddmStructureLayoutModel=dDLDDMDataFactory.newDDLDDMStructureLayoutModel(groupId, ddmStructureVersionModel)
			_ddmStructureModel=ddmStructureModel
			_ddmStructureVersionModel=ddmStructureVersionModel
		/>
	</#if>

	<#assign
		layoutName = "dynamic_data_list_display_" + ddlRecordSetCount
		portletId = "com_liferay_dynamic_data_lists_web_portlet_DDLDisplayPortlet_INSTANCE_TEST" + ddlRecordSetCount

		layoutModel = layoutDataFactory.newLayoutModel(groupId, layoutName, "", portletId)
	/>

	<@insertLayout _layoutModel=layoutModel />

	<#assign ddlRecordSetModel = dDLDDMDataFactory.newDDLRecordSetModel(ddmStructureModel, ddlRecordSetCount) />

	${resourcePermissionDataFactory.toInsertSQL(ddlRecordSetModel)}

	${resourcePermissionDataFactory.toInsertSQL(dDLDDMDataFactory.newDDMStructureLinkModel(ddlRecordSetModel))}

	<#assign ddlRecordCounts = counterDataFactory.getSequence(dDLDDMDataFactory.maxDDLRecordCount) />

	<#list ddlRecordCounts as ddlRecordCount>
		<#assign ddlRecordModel = dDLDDMDataFactory.newDDLRecordModel(ddlRecordSetModel) />

		${resourcePermissionDataFactory.toInsertSQL(ddlRecordModel)}

		${resourcePermissionDataFactory.toInsertSQL(dDLDDMDataFactory.newDDLRecordVersionModel(ddlRecordModel))}

		<@insertDDMContent
			_currentIndex=ddlRecordCount
			_ddmStorageLinkId=counterDataFactory.getCounterNext()
			_ddmStructureId=ddmStructureModel.structureId
			_entry=ddlRecordModel
		/>

		${dDLDDMDataFactory.getCSVWriter("dynamicDataList").write(ddlRecordModel.groupId + "," + layoutName + "," + portletId + "," + ddlRecordSetModel.recordSetId + "," + ddlRecordModel.recordId + "\n")}
	</#list>

	${resourcePermissionDataFactory.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletId, ddlRecordSetModel))}

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newDDLPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${resourcePermissionDataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>
</#list>