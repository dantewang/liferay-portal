<#assign
	groupIds = dataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.powerUserRoleModel.roleId, dataFactory.userRoleModel.roleId]

/>

<#if virtualInstanceCount?? && virtualInstanceCount != 0>
	<#assign userModels = dataFactory.newVirtualInstanceUserModels() />
<#else>
	<#assign userModels = dataFactory.newUserModels() />
</#if>

<#list userModels as userModel>
	<#assign userGroupModel = dataFactory.newGroupModel(userModel) />

	${csvFileWriter.write("user", companyModel.companyId + "," + virtualHostModel.hostname + "," + userModel.screenName + "\n")}

	<@insertLayout _layoutModel=dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "") />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>