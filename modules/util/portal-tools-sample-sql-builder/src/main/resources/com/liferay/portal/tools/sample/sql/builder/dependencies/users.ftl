<#assign
	groupIds = userDataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [userDataFactory.administratorRoleId, userDataFactory.powerUserRoleId, userDataFactory.userRoleId]

	userModels = userDataFactory.newUserModels()
/>

<#if cTDataFactory.maxCTCount gt 0>
	${resourcePermissionDataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel())}
</#if>

<#list userModels as userModel>
	<#assign userGroupModel = userDataFactory.newGroupModel(userModel) />

	<#if cTDataFactory.maxCTCount gt 0>
		<#include "changelist_journal_article.ftl">
	</#if>

	<@insertLayout _layoutModel=layoutDataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "") />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>