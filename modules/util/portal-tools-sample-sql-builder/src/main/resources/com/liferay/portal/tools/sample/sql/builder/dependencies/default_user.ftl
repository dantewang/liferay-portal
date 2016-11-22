<#-- Default user -->

<@insertUser
	_userModel = userDataFactory.defaultUserModel
/>

<#-- Guest user -->

<#assign userModel = userDataFactory.guestUserModel />

<@insertGroup
	_groupModel = userDataFactory.newGroupModel(userModel)
	_publicPageCount = 0
/>

<#assign
	groupIds = [userDataFactory.guestGroupModel.groupId]
	roleIds = [userDataFactory.administratorRoleModel.roleId]
/>

<@insertUser
	_groupIds = groupIds
	_roleIds = roleIds
	_userModel = userModel
/>

<#-- Sample user -->

<#assign
	userModel = userDataFactory.sampleUserModel

	sampleUserId = userModel.userId

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

<#assign
	groupIds = counterDataFactory.getSequence(initContext.maxGroupsCount)
	roleIds = [userDataFactory.administratorRoleModel.roleId, userDataFactory.powerUserRoleModel.roleId, userDataFactory.userRoleModel.roleId]
/>

<@insertUser
	_groupIds = groupIds
	_roleIds = roleIds
	_userModel = userModel
/>

<#list groupIds as groupId>
	<#assign blogsStatsUserModel = blogDataFactory.newBlogsStatsUserModel(groupId) />

	insert into BlogsStatsUser values (${blogsStatsUserModel.statsUserId}, ${blogsStatsUserModel.groupId}, ${blogsStatsUserModel.companyId}, ${blogsStatsUserModel.userId}, ${blogsStatsUserModel.entryCount}, '${dataFactory.getDateString(blogsStatsUserModel.lastPostDate)}', ${blogsStatsUserModel.ratingsTotalEntries}, ${blogsStatsUserModel.ratingsTotalScore}, ${blogsStatsUserModel.ratingsAverageScore});

	<#assign mbStatsUserModel = messageBoardDataFactory.newMBStatsUserModel(groupId) />

	insert into MBStatsUser values (${mbStatsUserModel.statsUserId}, ${mbStatsUserModel.groupId}, ${mbStatsUserModel.companyId}, ${mbStatsUserModel.userId}, ${mbStatsUserModel.messageCount}, '${dataFactory.getDateString(mbStatsUserModel.lastPostDate)}');
</#list>