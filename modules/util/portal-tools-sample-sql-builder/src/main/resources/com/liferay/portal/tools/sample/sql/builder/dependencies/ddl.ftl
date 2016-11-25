<#assign ddlRecordSetCounts = counterDataFactory.getSequence(initContext.maxDDLRecordSetCount) />

<#list ddlRecordSetCounts as ddlRecordSetCount>
	<#if ddlRecordSetCount = 1>
		<#assign
			ddmStructureModel = dDLDDMDataFactory.newDDLDDMStructureModel(groupId)
			ddmStructureVersionModel = dDLDDMDataFactory.newDDMStructureVersionModel(ddmStructureModel)
		/>

		<@insertDDMStructure
			_ddmStructureModel = ddmStructureModel
			_ddmStructureLayoutModel = dDLDDMDataFactory.newDDLDDMStructureLayoutModel(groupId, ddmStructureVersionModel)
			_ddmStructureVersionModel = ddmStructureVersionModel
		/>
	</#if>

	<#assign
		layoutName = "dynamic_data_list_display_" + ddlRecordSetCount
		portletId = "com_liferay_dynamic_data_lists_web_portlet_DDLDisplayPortlet_INSTANCE_TEST" + ddlRecordSetCount

		layoutModel = layoutDataFactory.newLayoutModel(groupId, layoutName, "", portletId)
	/>

	<@insertLayout
		_layoutModel = layoutModel
	/>

	<#assign ddlRecordSetModel = dDLDDMDataFactory.newDDLRecordSetModel(ddmStructureModel, ddlRecordSetCount) />

	insert into DDLRecordSet values ('${ddlRecordSetModel.uuid}', ${ddlRecordSetModel.recordSetId}, ${ddlRecordSetModel.groupId}, ${ddlRecordSetModel.companyId}, ${ddlRecordSetModel.userId}, '${ddlRecordSetModel.userName}', '${initContext.getDateString(ddlRecordSetModel.createDate)}', '${initContext.getDateString(ddlRecordSetModel.modifiedDate)}', ${ddlRecordSetModel.DDMStructureId}, '${ddlRecordSetModel.recordSetKey}', '${ddlRecordSetModel.name}', '${ddlRecordSetModel.description}', ${ddlRecordSetModel.minDisplayRows}, ${ddlRecordSetModel.scope}, '${ddlRecordSetModel.settings}', '${initContext.getDateString(ddlRecordSetModel.lastPublishDate)}');

	<@insertDDMStructureLink
		_entry = ddlRecordSetModel
	/>

	<@insertResourcePermissions
		_entry = ddlRecordSetModel
	/>

	<#assign ddlRecordCounts = counterDataFactory.getSequence(initContext.maxDDLRecordCount) />

	<#list ddlRecordCounts as ddlRecordCount>
		<#assign ddlRecordModel = dDLDDMDataFactory.newDDLRecordModel(ddlRecordSetModel) />

		insert into DDLRecord values ('${ddlRecordModel.uuid}', ${ddlRecordModel.recordId}, ${ddlRecordModel.groupId}, ${ddlRecordModel.companyId}, ${ddlRecordModel.userId}, '${ddlRecordModel.userName}', ${ddlRecordModel.versionUserId}, '${ddlRecordModel.versionUserName}', '${initContext.getDateString(ddlRecordModel.createDate)}', '${initContext.getDateString(ddlRecordModel.modifiedDate)}', ${ddlRecordModel.DDMStorageId}, ${ddlRecordModel.recordSetId}, '${ddlRecordModel.version}', ${ddlRecordModel.displayIndex}, '${initContext.getDateString(ddlRecordModel.lastPublishDate)}');

		<#assign ddlRecordVersionModel = dDLDDMDataFactory.newDDLRecordVersionModel(ddlRecordModel) />

		insert into DDLRecordVersion values (${ddlRecordVersionModel.recordVersionId}, ${ddlRecordVersionModel.groupId}, ${ddlRecordVersionModel.companyId}, ${ddlRecordVersionModel.userId}, '${ddlRecordVersionModel.userName}', '${initContext.getDateString(ddlRecordVersionModel.createDate)}', ${ddlRecordVersionModel.DDMStorageId}, ${ddlRecordVersionModel.recordSetId}, ${ddlRecordVersionModel.recordId}, '${ddlRecordVersionModel.version}', ${ddlRecordVersionModel.displayIndex}, ${ddlRecordVersionModel.status}, ${ddlRecordVersionModel.statusByUserId}, '${ddlRecordVersionModel.statusByUserName}', '${initContext.getDateString(ddlRecordVersionModel.statusDate)}');

		<@insertDDMContent
			_currentIndex = ddlRecordCount
			_ddmStorageLinkId = counterDataFactory.getCounterNext()
			_ddmStructureId = ddmStructureModel.structureId
			_entry = ddlRecordModel
		/>

		${dynamicDataListCSVWriter.write(ddlRecordModel.groupId + "," + layoutName + "," + portletId + "," + ddlRecordSetModel.recordSetId + "," + ddlRecordModel.recordId + "\n")}
	</#list>

	<#assign portletPreferencesModel = portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletId, ddlRecordSetModel) />

	<@insertPortletPreferences
		_portletPreferencesModel = portletPreferencesModel
	/>

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newDDLPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		<@insertPortletPreferences
			_portletPreferencesModel = portletPreferencesModel
		/>
	</#list>
</#list>