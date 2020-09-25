<#list userDataFactory.newRoleModels(classNameDataFactory.getClassNameId("Role")) as roleModel>
	${resourcePermissionDataFactory.toInsertSQL(roleModel)}
</#list>