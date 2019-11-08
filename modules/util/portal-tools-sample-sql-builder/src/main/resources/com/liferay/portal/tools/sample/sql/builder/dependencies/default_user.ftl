<#-- Default user -->

<@insertUser _userModel=userDataFactory.defaultUserModel />

<#-- Guest user -->

<#assign userModel = userDataFactory.guestUserModel />

<@insertGroup _groupModel=userDataFactory.newGroupModel(userModel) />

<#assign
	groupIds = [userDataFactory.guestGroupModel.groupId]
	roleIds = [userDataFactory.administratorRoleModel.roleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=userModel
/>

<#-- Sample user -->

<#assign
	userModel = userDataFactory.sampleUserModel

	sampleUserId = userModel.userId

	userGroupModel = userDataFactory.newGroupModel(userModel)

	layoutModel = layoutDataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "")
/>

<@insertLayout _layoutModel=layoutModel />

<@insertGroup _groupModel=userGroupModel />

<#assign
	groupIds = counterDataFactory.getSequence(userDataFactory.maxGroupCount)
	roleIds = [userDataFactory.administratorRoleModel.roleId, userDataFactory.powerUserRoleModel.roleId, userDataFactory.userRoleModel.roleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=userModel
/>

<#list groupIds as groupId>
	${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newBlogsStatsUserModel(groupId))}

	${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newMBStatsUserModel(groupId))}
</#list>