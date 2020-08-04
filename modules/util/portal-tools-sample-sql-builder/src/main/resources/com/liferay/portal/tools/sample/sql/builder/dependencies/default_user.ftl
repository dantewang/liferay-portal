<#-- Default user -->

<@insertUser _userModel=userDataFactory.newDefaultUserModel() />

<#-- Guest user -->

<#assign guestUserModel = userDataFactory.newGuestUserModel() />

<@insertGroup _groupModel=userDataFactory.newGroupModel(guestUserModel) />

<#assign
	groupIds = [guestGroupModel.groupId]
	roleIds = [userDataFactory.administratorRoleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=guestUserModel
/>

<#-- Sample user -->

<#assign
	sampleUserModel = userDataFactory.newSampleUserModel()

	userGroupModel = userDataFactory.newGroupModel(sampleUserModel)

	layoutModel = dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "")
/>

<@insertLayout _layoutModel=layoutModel />

<@insertGroup _groupModel=userGroupModel />

<#assign
	groupIds = dataFactory.getSequence(userDataFactory.maxGroupCount)
	roleIds = [userDataFactory.administratorRoleId, userDataFactory.powerUserRoleId, userDataFactory.userRoleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=sampleUserModel
/>

<#list groupIds as groupId>
	${dataFactory.toInsertSQL(blogDataFactory.newBlogsStatsUserModel(groupId))}

	${dataFactory.toInsertSQL(messageBoardDataFactory.newMBStatsUserModel(groupId))}
</#list>