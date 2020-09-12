<#list userDataFactory.newRoleModels(classNameDataFactory.getClassNameId("Role")) as roleModel>
	${dataFactory.toInsertSQL(roleModel)}
</#list>