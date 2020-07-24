<#assign
	groupIds = userDataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [userDataFactory.administratorRoleId, userDataFactory.powerUserRoleId, userDataFactory.userRoleId]

	userModels = userDataFactory.newUserModels()
/>

<#list userModels as userModel>
	<#assign userGroupModel = userDataFactory.newGroupModel(userModel) />

	<@insertLayout _layoutModel=layoutDataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "") />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>