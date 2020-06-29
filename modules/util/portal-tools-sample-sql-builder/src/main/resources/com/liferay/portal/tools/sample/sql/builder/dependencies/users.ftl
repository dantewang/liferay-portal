<#assign
	groupIds = dataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.powerUserRoleModel.roleId, dataFactory.userRoleModel.roleId]

	userModels = dataFactory.newUserModels()
/>

<#if dataFactory.maxCTCount gt 0>
	${dataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel())}
</#if>

<#list userModels as userModel>
	<#assign
		userGroupModel = dataFactory.newGroupModel(userModel)

		layoutModel = dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "")
	/>

	<#if dataFactory.maxCTCount gt 0>
		<#include "changelist_journal_article.ftl">
	</#if>

	<@insertLayout _layoutModel=layoutModel />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>