<#list userDataFactory.newRoleModels(classNameDataFactory.getClassNameId("Role")) as roleModel>
	${insertSQLBuilder.toInsertSQL(roleModel)}
</#list>