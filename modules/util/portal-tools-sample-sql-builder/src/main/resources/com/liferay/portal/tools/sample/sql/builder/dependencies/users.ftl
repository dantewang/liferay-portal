<#assign
	groupIds = dataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [dataFactory.administratorRoleId, dataFactory.powerUserRoleId, dataFactory.userRoleId]

	userModels = dataFactory.newUserModels()
/>

<#list userModels as userModel>
	<#assign userGroupModel = dataFactory.newGroupModel(userModel) />

	<@insertLayout _layoutModel=dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "") />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>