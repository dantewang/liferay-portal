<#list userDataFactory.roleModels as roleModel>
	${resourcePermissionDataFactory.toInsertSQL(roleModel)}
</#list>