<#assign
	groupIds = userDataFactory.getNewUserGroupIds(groupModel.groupId)
	roleIds = [userDataFactory.administratorRoleModel.roleId, userDataFactory.powerUserRoleModel.roleId, userDataFactory.userRoleModel.roleId]

	userModels = userDataFactory.newUserModels()
/>

<#list userModels as userModel>
	<#assign
		userGroupModel = userDataFactory.newGroupModel(userModel)

		layoutModel = layoutDataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "")
	/>

	<@insertLayout
		_layoutModel = layoutModel
	/>

	<@insertGroup
		_groupModel = userGroupModel
		_publicPageCount = 1
	/>

	<@insertUser
		_groupIds = groupIds
		_roleIds = roleIds
		_userModel = userModel
	/>
</#list>